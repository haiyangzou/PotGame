package org.pot.chat.module;

import lombok.Getter;
import lombok.Setter;
import org.pot.chat.util.ChatUtil;
import org.pot.core.enums.ChatType;
import org.pot.cache.player.PlayerCaches;
import org.pot.cache.player.snapshot.PlayerSnapShot;
import org.pot.message.protocol.chat.ChatInfo;
import org.pot.message.protocol.chat.ChatS2S;

@Setter
@Getter
public class Chat {
    private long id;

    private long time;

    private int type;

    private int channel;

    private String content;

    private String replyContent;

    private ChatNoticeInfo chatNoticeInfo;

    private long senderPlayerId;


    protected boolean canEqual(Object other) {
        return other instanceof Chat;
    }


    public String toString() {
        return "Chat(id=" + getId() + ", time=" + getTime() + ", type=" + getType() + ", channel=" + getChannel() + ", content=" + getContent() + ", replyContent=" + getReplyContent() + ", chatNoticeInfo=" + getChatNoticeInfo() + ", senderPlayerId=" + getSenderPlayerId() + ")";
    }

    public Chat() {
    }

    public Chat(ChatS2S chatS2S) {
        ChatInfo chatInfo = chatS2S.getChatInfo();
        this.id = chatInfo.getId();
        this.time = chatInfo.getTime();
        this.type = chatInfo.getType();
        this.channel = chatInfo.getChannel();
        this.content = chatInfo.getContent();
        this.replyContent = chatInfo.getReplyContent();
        this.chatNoticeInfo = ChatNoticeInfo.toChatNoticeInfo(chatInfo.getChatNotice());
        this.senderPlayerId = chatInfo.getSendInfo().getPlayerId();
    }

    public ChatInfo toChatInfo() {
        ChatInfo.Builder chatInfoBuilder = ChatInfo.newBuilder();
        chatInfoBuilder.setId(this.id);
        chatInfoBuilder.setTime(this.time);
        chatInfoBuilder.setType(this.type);
        chatInfoBuilder.setChannel(this.channel);
        chatInfoBuilder.setContent(this.content);
        chatInfoBuilder.setReplyContent(this.replyContent);
        chatInfoBuilder.setChatNotice(ChatNoticeInfo.toChatNotice(this.chatNoticeInfo));
        PlayerSnapShot playerSnapshot = PlayerCaches.snapShot().getSnapshot(this.senderPlayerId);
        if (playerSnapshot != null) {
            chatInfoBuilder.setSendInfo(ChatUtil.toChatSendInfo(playerSnapshot));
        } else if (this.type == ChatType.NORMAL.getType()) {
            return null;
        }
        return chatInfoBuilder.build();
    }
}