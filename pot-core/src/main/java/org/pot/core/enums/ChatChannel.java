package org.pot.core.enums;

import java.util.Map;

import lombok.Getter;
import org.pot.common.enums.IntEnum;
import org.pot.common.util.EnumUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Getter
public enum ChatChannel implements IntEnum {
    COUNTRY(1),
    UNION(2),
    SPECIAL(3),
    PERSONAL(4);

    static {
        log = LoggerFactory.getLogger(ChatChannel.class);
        INDEXES = EnumUtils.toMap(values());
    }

    private static final Logger log;

    private final int channel;

    private static final Map<Integer, ChatChannel> INDEXES;

    ChatChannel(int channel) {
        this.channel = channel;
    }

    public int getId() {
        return this.channel;
    }

    public static ChatChannel getChatChannel(int channel) {
        ChatChannel chatChannel = INDEXES.get(channel);
        if (chatChannel == null)
            log.error("buildingType findById is null, id:{}", channel);
        return chatChannel;
    }
}
