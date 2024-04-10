package org.pot.chat.module;

import lombok.Getter;
import lombok.Setter;
import org.pot.cache.player.PlayerCaches;
import org.pot.cache.player.snapshot.PlayerSnapShot;
import org.pot.chat.util.ChatUtil;
import org.pot.message.protocol.chat.ChatPersonalInfo;
import org.pot.message.protocol.chat.ChatPersonalS2S;
import org.pot.message.protocol.chat.PushPersonalChatS2C;

@Setter
@Getter
public class ChatPersonal {
    private long id;

    private long playerId;

    private long receiverId;

    private long time;

    private String title;

    private String content;

    private boolean status;

    private long senderPlayerId;

    private long receiverPlayerId;


    public void toChatPersonal(ChatPersonalS2S chatPersonalS2S, long receiverPlayerId) {
        ChatPersonalInfo chatPersonalInfo = chatPersonalS2S.getPersonalInfo();
        this.id = chatPersonalInfo.getId();
        this.playerId = chatPersonalS2S.getPlayerId();
        this.receiverId = chatPersonalS2S.getReceiverId();
        this.time = chatPersonalInfo.getTime();
        this.title = chatPersonalInfo.getTitle();
        this.content = chatPersonalInfo.getContent();
        this.status = (chatPersonalInfo.getStatus() == 1);
        this.senderPlayerId = chatPersonalInfo.getSendInfo().getPlayerId();
        this.receiverPlayerId = receiverPlayerId;
    }

    public PushPersonalChatS2C toChatPersonalInfo() {
        PushPersonalChatS2C.Builder builder = PushPersonalChatS2C.newBuilder();
        builder.setPlayerId(this.playerId);
        builder.setReceiverId(this.receiverId);
        ChatPersonalInfo.Builder personalBuilder = ChatPersonalInfo.newBuilder();
        personalBuilder.setId(this.id);
        personalBuilder.setTime(this.time);
        personalBuilder.setTitle(this.title);
        personalBuilder.setContent(this.content);
        personalBuilder.setStatus(this.status ? 1 : 0);
        PlayerSnapShot playerSnapshot = PlayerCaches.snapShot().getSnapshot(this.senderPlayerId);
        if (playerSnapshot != null) {
            personalBuilder.setSendInfo(ChatUtil.toChatSendInfo(playerSnapshot));
        } else {
            return null;
        }
        builder.setChatPersonalInfo(personalBuilder);
        playerSnapshot = PlayerCaches.snapShot().getSnapshot(this.receiverPlayerId);
        if (playerSnapshot != null) {
            builder.setReceiverInfo(ChatUtil.toChatSendInfo(playerSnapshot));
        } else {
            return null;
        }
        return builder.build();
    }
}
