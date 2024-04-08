package org.pot.core.enums;

import java.util.Map;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.pot.common.enums.IntEnum;
import org.pot.common.util.EnumUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Slf4j
@Getter
public enum ChatChannel implements IntEnum {
    COUNTRY(1),
    UNION(2),
    SPECIAL(3),
    PERSONAL(4);

    static {
        INDEXES = EnumUtils.toMap(values());
    }
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
