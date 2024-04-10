package org.pot.chat.module;

import lombok.Getter;
import lombok.Setter;
import org.pot.message.protocol.chat.ChatNotice;

import java.util.List;

@Getter
@Setter
public class ChatNoticeInfo {
    private String key;

    private List<String> params;

    private int templateId;

    public static ChatNoticeInfo toChatNoticeInfo(ChatNotice chatNotice) {
        ChatNoticeInfo chatNoticeInfo = new ChatNoticeInfo();
        chatNoticeInfo.setKey(chatNotice.getKey());
        chatNoticeInfo.setParams(chatNotice.getParamsList());
        chatNoticeInfo.setTemplateId(chatNotice.getTemplateId());
        return chatNoticeInfo;
    }

    public static ChatNotice toChatNotice(ChatNoticeInfo chatNoticeInfo) {
        ChatNotice.Builder builder = ChatNotice.newBuilder();
        if (chatNoticeInfo.getKey() != null)
            builder.setKey(chatNoticeInfo.getKey());
        if (chatNoticeInfo.getParams() != null && !chatNoticeInfo.getParams().isEmpty())
            builder.addAllParams(chatNoticeInfo.getParams());
        builder.setTemplateId(chatNoticeInfo.templateId);
        return builder.build();
    }
}