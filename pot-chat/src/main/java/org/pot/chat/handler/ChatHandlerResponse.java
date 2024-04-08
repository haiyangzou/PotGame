package org.pot.chat.handler;

import com.eyu.kylin.magics.protocol.chat.*;
import org.pot.cache.player.PlayerCaches;
import org.pot.cache.player.snapshot.PlayerSnapShot;
import org.pot.chat.engine.ChatEngine;
import org.pot.chat.module.Chat;
import org.pot.chat.module.ChatPersonal;
import org.pot.chat.module.ChatServerManager;
import org.pot.chat.util.ChatUtil;
import org.pot.common.communication.server.ServerType;
import org.pot.remote.thrift.define.RemoteServerType;
import org.pot.remote.thrift.define.chat.ChatRemote;

import java.util.List;

@RemoteServerType(ServerType.CHAT_SERVER)
public class ChatHandlerResponse implements ChatRemote {
    @Override
    public ChatListS2C getChatList(ChatListS2S request) {
        ChatListS2C.Builder builder = ChatListS2C.newBuilder();
        if (request.getId() == 0L) {
            List<Chat> chatList = ChatEngine.getInstance().getChatServerManager().getChatList(request.getChannel(), request.getUniqueId());
            chatList.forEach(chat -> {
                ChatInfo chatInfo = chat.toChatInfo();
                if (chatInfo != null)
                    builder.addChatInfos(chatInfo);
            });
        } else {
            List<Chat> chatList = ChatEngine.getInstance().getChatServerManager().getChatBeforeList(request.getChannel(), request.getUniqueId(), request.getId());
            chatList.forEach(chat -> {
                ChatInfo chatInfo = chat.toChatInfo();
                if (chatInfo != null)
                    builder.addChatInfos(chatInfo);
            });
        }
        return builder.build();
    }

    @Override
    public PushChatS2C sendChat(ChatS2S request) {
        Chat chat = new Chat(request);
        ChatEngine.getInstance().getChatServerManager().addChat(chat, request.getUniqueId());
        PushChatS2C.Builder builder = PushChatS2C.newBuilder();
        builder.setChatInfo(request.getChatInfo().toBuilder());
        return builder.build();
    }

    @Override
    public ChatPersonalListS2C getChatPersonalList(ChatPersonalListS2S request) {
        List<ChatPersonal> chatPersonalList = ChatEngine.getInstance().getChatServerManager().getChatPersonalList(request.getPlayerId(), request.getShieldPlayerIdsList());
        ChatPersonalListS2C.Builder builder = ChatPersonalListS2C.newBuilder();
        chatPersonalList.forEach(chatPersonal -> {
            PushPersonalChatS2C personalInfo = chatPersonal.toChatPersonalInfo();
            if (personalInfo != null)
                builder.addChatPersonalInfos(personalInfo);
        });
        return builder.build();
    }

    @Override
    public void chatPersonalRead(ChatPersonalReadS2S request) {
        ChatEngine.getInstance().getChatServerManager().personalRead(request.getPlayerId(), request.getOtherPlayerId());
    }

    @Override
    public PushPersonalChatS2C sendPersonalChat(ChatPersonalS2S request) {
        ChatPersonalInfo personalInfo = request.getPersonalInfo();
        long receiverId = request.getReceiverId();
        ChatEngine.getInstance().getChatServerManager().addChatPersonal(request);
        PushPersonalChatS2C.Builder builder = PushPersonalChatS2C.newBuilder();
        builder.setPlayerId(request.getPlayerId());
        builder.setReceiverId(request.getReceiverId());
        builder.setChatPersonalInfo(personalInfo);
        PlayerSnapShot playerSnapshot = PlayerCaches.snapShot().getSnapshot(receiverId);
        if (playerSnapshot != null) {
            builder.setReceiverInfo(ChatUtil.toChatSendInfo(playerSnapshot));
            int serverId = playerSnapshot.getServerId();
            ChatServerManager.brodCastPersonalMessage(serverId, builder.build());
            return builder.build();
        }
        return null;
    }
}
