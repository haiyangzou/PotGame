package org.pot.game.engine.player.module.chat;

import org.pot.message.protocol.chat.*;
import com.google.protobuf.ByteString;
import com.google.protobuf.ProtocolStringList;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.pot.cache.player.PlayerCaches;
import org.pot.common.communication.server.ServerType;
import org.pot.common.concurrent.exception.IErrorCode;
import org.pot.common.enums.Language;
import org.pot.common.util.JsonUtil;
import org.pot.common.util.SensitiveWordUtil;
import org.pot.common.util.UnicodeUtil;
import org.pot.core.enums.ChatChannel;
import org.pot.core.enums.ChatType;
import org.pot.game.engine.GameEngine;
import org.pot.game.engine.error.GameErrorCode;
import org.pot.game.engine.player.Player;
import org.pot.game.engine.player.PlayerAgentAdapter;
import org.pot.game.engine.player.PlayerManager;
import org.pot.game.resource.parameter.GameParameters;
import org.pot.remote.thrift.client.manager.RpcClientManager;
import org.pot.remote.thrift.define.chat.ChatRemote;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
public class PlayerChatAgent extends PlayerAgentAdapter {
    private long lastSendTime;

    private String lastContext;

    public PlayerChatAgent(Player player) {
        super(player);
        this.lastSendTime = System.currentTimeMillis();
        this.lastContext = "";
    }

    protected void onLoginSuccess() {
        getChatCountryList();
        getChatUnionList();
        getChatPersonalList();
    }

    public IErrorCode sendChat(int channel, String content, String replyContent) {
        if (!UnicodeUtil.isValidStr(content)) {
            return GameErrorCode.ILLEGAL_CHAR;
        }
        if ((content.getBytes()).length > GameParameters.channel_max.getInt()) {
            return GameErrorCode.CHAT_CONTENT_LIMIT;
        }
        IErrorCode iErrorCode = checkChatTimeLimit(content);
        if (iErrorCode != null)
            return iErrorCode;
        content = SensitiveWordUtil.replaceSensitiveWord(content.toLowerCase(), '*');
        ChatS2S.Builder builder = ChatS2S.newBuilder();
        ChatChannel chatChannel = ChatChannel.getChatChannel(channel);
        if (chatChannel == ChatChannel.COUNTRY) {
            builder.setServerId(this.player.getProfile().getServerId());
            builder.setUniqueId(this.player.getProfile().getServerId());
        } else if (chatChannel == ChatChannel.UNION) {
            if (this.player.unionAgent.getUnionId() <= 0)
                return GameErrorCode.CHAT_UNION_NO_EXISTS;
            builder.setUniqueId(this.player.unionAgent.getUnionId());
        }
        builder.setChannel(channel);
        builder.setChatInfo(buildChatInfo(ChatType.NORMAL.getType(), channel, content, replyContent));
        try {
            ChatRemote handler = RpcClientManager.instance.randomService(ServerType.CHAT_SERVER, ChatRemote.class);
            PushChatS2C response = handler.sendChat(builder.build());
            if (channel == ChatChannel.COUNTRY.getChannel()) {
                PlayerManager.getInstance().broadcast(response);
            } else if (channel == ChatChannel.UNION.getChannel()) {
                int uniqueId = PlayerChatAgent.this.player.unionAgent.getUnionId();
//                UnionModule.getInstance().broadcast(uniqueId, response);
            }
        } catch (Exception exception) {
            log.error("Remote Rpc  sendChat failed:{}", exception.getMessage());
        }
        return null;
    }

    private IErrorCode checkChatTimeLimit(String content) {
        if (System.currentTimeMillis() - this.lastSendTime <= TimeUnit.SECONDS.toMillis(GameParameters.worldTalk_cd.getInt()))
            return GameErrorCode.CHAT_SEND_TIME_LIMIT;
        if (StringUtils.equals(this.lastContext, content) && System.currentTimeMillis() - this.lastSendTime <= TimeUnit.SECONDS
                .toMillis(GameParameters.worldTalk_same_cd.getInt()))
            return GameErrorCode.CHAT_SEND_SAME_CONTENT_TIME_LIMIT;
        this.lastSendTime = System.currentTimeMillis();
        this.lastContext = content;
        return null;
    }

    public IErrorCode sendChatBroadcast(String content) {
        content = SensitiveWordUtil.replaceSensitiveWord(content.toLowerCase(), '*');
        ChatS2S.Builder builder = ChatS2S.newBuilder();
        builder.setServerId(this.player.getProfile().getServerId());
        builder.setUniqueId(this.player.getProfile().getServerId());
        builder.setChannel(ChatChannel.COUNTRY.getChannel());
        builder.setChatInfo(buildChatInfo(ChatType.TRUMPET.getType(), ChatChannel.COUNTRY.getChannel(), content, ""));

        try {
            ChatRemote handler = RpcClientManager.instance.randomService(ServerType.CHAT_SERVER, ChatRemote.class);
            PushChatS2C response = handler.sendChat(builder.build());
            int channel = response.getChatInfo().getChannel();
            if (channel == ChatChannel.COUNTRY.getChannel()) {
                PlayerManager.getInstance().broadcast(response);
            } else if (channel == ChatChannel.UNION.getChannel()) {
                int uniqueId = PlayerChatAgent.this.player.unionAgent.getUnionId();
//                UnionModule.getInstance().broadcast(uniqueId, response);
            }
            if (response.getChatInfo().getType() == ChatType.TRUMPET.getType()) {
                SendChatBroadcastS2C.Builder broadBuilder = SendChatBroadcastS2C.newBuilder();
                broadBuilder.setContent(response.getChatInfo().getContent());
                broadBuilder.setSendInfo(response.getChatInfo().getSendInfo());
                PlayerManager.getInstance().broadcast(broadBuilder.build());
            }
        } catch (Exception exception) {
            log.error("Remote Rpc  sendChat failed:{}", exception.getMessage());
        }
        return null;
    }

    public IErrorCode chatPersonal(String playerName, String title, String content) {
        if ((content.getBytes()).length > GameParameters.privateChat_max.getInt())
            return GameErrorCode.CHAT_CONTENT_LIMIT;
        content = SensitiveWordUtil.replaceSensitiveWord(content.toLowerCase(), '*');
        ChatPersonalS2S.Builder builder = ChatPersonalS2S.newBuilder();
        ChatPersonalInfo.Builder personalBuilder = ChatPersonalInfo.newBuilder();
        personalBuilder.setId(GameEngine.getInstance().nextId());
        personalBuilder.setTime(System.currentTimeMillis());
        personalBuilder.setTitle(title);
        personalBuilder.setContent(content);
        personalBuilder.setSendInfo(buildChatSendInfo());
        builder.setPersonalInfo(personalBuilder.build());
        long receiverId = PlayerCaches.name().getUid(playerName);
        if (receiverId <= 0L)
            return GameErrorCode.PLAYER_NOT_FOUND;
        builder.setPlayerId(this.player.getUid());
        builder.setReceiverId(receiverId);
        try {
            ChatRemote handler = RpcClientManager.instance.randomService(ServerType.CHAT_SERVER, ChatRemote.class);
            PushPersonalChatS2C response = handler.sendPersonalChat(builder.build());
            if (!this.player.commonAgent.getPlayerCommonInfo().getForbiddenPlayerIds().contains(receiverId))
                PlayerChatAgent.this.player.sendMessage(response);
        } catch (Exception exception) {
            log.error("Remote Rpc  sendPersonalChat failed:{}", exception.getMessage());
        }
        return null;
    }

    public IErrorCode chatPersonalRead(long playerId) {
        ChatPersonalReadS2S.Builder builder = ChatPersonalReadS2S.newBuilder();
        builder.setPlayerId(this.player.getUid());
        builder.setOtherPlayerId(playerId);
        try {
            ChatRemote handler = RpcClientManager.instance.randomService(ServerType.CHAT_SERVER, ChatRemote.class);
            handler.chatPersonalRead(builder.build());
        } catch (Exception exception) {
            log.error("Remote Rpc  getChatBeforeList failed:{}", exception.getMessage());
        }
        return null;
    }

    public IErrorCode getChatBeforeList(int channel, long id) {
        ChatListS2S.Builder builder = ChatListS2S.newBuilder();
        builder.setChannel(channel);
        if (channel == ChatChannel.COUNTRY.getChannel()) {
            builder.setUniqueId(this.player.getProfile().getServerId());
        } else if (channel == ChatChannel.UNION.getChannel()) {
            builder.setUniqueId(this.player.unionAgent.getUnionId());
        }
        builder.setId(id);
        try {
            ChatRemote handler = RpcClientManager.instance.randomService(ServerType.CHAT_SERVER, ChatRemote.class);
            ChatListS2C response = handler.getChatList(builder.build());
            this.player.sendMessage(response);
        } catch (Exception exception) {
            log.error("Remote Rpc  getChatBeforeList failed:{}", exception.getMessage());
        }
        return null;
    }

    public void getChatCountryList() {
        ChatListS2S.Builder builder = ChatListS2S.newBuilder();
        builder.setChannel(ChatChannel.COUNTRY.getChannel());
        builder.setUniqueId(this.player.getProfile().getServerId());
        try {
            ChatRemote handler = RpcClientManager.instance.randomService(ServerType.CHAT_SERVER, ChatRemote.class);
            ChatListS2C response = handler.getChatList(builder.build());
            List<ChatInfo> oldChatInfosList = response.getChatInfosList();
            List<ChatInfo> newChatInfosList = new ArrayList<>(response.getChatInfosList().size());
            for (ChatInfo oldChatInfo : oldChatInfosList) {
                if (oldChatInfo.getChatNotice().getTemplateId() == 10) {
                    ChatInfo.Builder newChatInfo = oldChatInfo.toBuilder();
                    ChatNotice.Builder newChatNotice = oldChatInfo.getChatNotice().toBuilder();
                    ProtocolStringList paramsList = newChatNotice.getParamsList();
                    if (paramsList.size() == 1) {
                        HashMap<String, String> noticeContent = JsonUtil.parseMap(paramsList.get(0), new HashMap<>(), String.class, String.class);
                        if (noticeContent.containsKey(PlayerChatAgent.this.player.getProfile().getLanguage())) {
                            newChatInfo.setChatNotice(newChatNotice.setParams(0, noticeContent.get(PlayerChatAgent.this.player.getProfile().getLanguage())).build());
                            newChatInfosList.add(newChatInfo.build());
                            continue;
                        }
                        if (noticeContent.containsKey(Language.English.getConfigCode())) {
                            newChatInfo.setChatNotice(newChatNotice.setParams(0, noticeContent.get(Language.English.getConfigCode())).build());
                            newChatInfosList.add(newChatInfo.build());
                        }
                    }
                    continue;
                }
                newChatInfosList.add(oldChatInfo);
            }
            this.player.sendMessage(ChatListS2C.newBuilder().addAllChatInfos(newChatInfosList).build());
        } catch (Exception exception) {
            log.error("Remote Rpc  getChatList failed:{}", exception.getMessage());
        }
    }

    public void getChatUnionList() {
        int unionId = this.player.unionAgent.getUnionId();
        if (unionId <= 0)
            return;
        ChatListS2S.Builder builder = ChatListS2S.newBuilder();
        builder.setChannel(ChatChannel.UNION.getChannel());
        builder.setUniqueId(unionId);
        try {
            ChatRemote handler = RpcClientManager.instance.randomService(ServerType.CHAT_SERVER, ChatRemote.class);
            ChatListS2C response = handler.getChatList(builder.build());
            this.player.sendMessage(response);
        } catch (Exception exception) {
            log.error("Remote Rpc  getChatList failed:{}", exception.getMessage());
        }
    }

    private void getChatPersonalList() {
        ChatPersonalListS2S.Builder builder = ChatPersonalListS2S.newBuilder();
        builder.setPlayerId(this.player.getUid());
        try {
            ChatRemote handler = RpcClientManager.instance.randomService(ServerType.CHAT_SERVER, ChatRemote.class);
            ChatPersonalListS2C response = handler.getChatPersonalList(builder.build());
            this.player.sendMessage(response);
        } catch (Exception exception) {
            log.error("Remote Rpc  getChatList failed:{}", exception.getMessage());
        }
    }

    private ChatInfo buildChatInfo(int type, int channel, String content, String replyContent) {
        ChatInfo.Builder chatBuilder = ChatInfo.newBuilder();
        chatBuilder.setId(GameEngine.getInstance().nextId());
        chatBuilder.setTime(System.currentTimeMillis());
        chatBuilder.setType(type);
        chatBuilder.setChannel(channel);
        chatBuilder.setContent(content);
        chatBuilder.setReplyContent(replyContent);
        chatBuilder.setSendInfo(buildChatSendInfo());
        return chatBuilder.build();
    }

    public ChatSendInfo buildChatSendInfo() {
        ChatSendInfo.Builder chatSendInfoBuilder = ChatSendInfo.newBuilder();
        chatSendInfoBuilder.setPlayerId(this.player.getUid());
        chatSendInfoBuilder.setName(this.player.getName());
        chatSendInfoBuilder.setIcon(this.player.commonAgent.getPlayerCommonInfo().getIconId());
        chatSendInfoBuilder.setFrameId(this.player.commonAgent.getPlayerCommonInfo().getFrameId());
        chatSendInfoBuilder.setAvatar(ByteString.copyFrom(this.player.commonAgent.getPlayerCommonInfo().getPicture()));
//        Union union = UnionModule.getInstance().getUnion(Integer.valueOf(this.player.unionAgent.getUnionId()));
//        if (union != null) {
//            chatSendInfoBuilder.setUnionAlias(union.getAlias());
//            UnionMember member = union.getMember(Long.valueOf(this.player.getUid()));
//            if (member != null)
//                chatSendInfoBuilder.setUnionPosition(member.getPosition());
//        }
        chatSendInfoBuilder.setKingdomTitle(0);
        chatSendInfoBuilder.setArtifactId(0);
        chatSendInfoBuilder.setAbility(this.player.powerComponent.getPower());
        return chatSendInfoBuilder.build();
    }
}
