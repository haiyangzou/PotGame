package org.pot.core.enums;


import java.util.Map;

import lombok.Getter;
import org.pot.common.enums.IntEnum;
import org.pot.common.util.EnumUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Getter
public enum ChatType implements IntEnum {
    NORMAL(1),
    POINT(2),
    NOTICE(3),
    TRUMPET(4),
    BATTLE(5);

    static {
        log = LoggerFactory.getLogger(ChatType.class);
        INDEXES = EnumUtils.toMap(values());
    }

    private static final Logger log;

    private final int type;

    private static final Map<Integer, ChatType> INDEXES;

    ChatType(int type) {
        this.type = type;
    }

    public int getId() {
        return this.type;
    }

    public static ChatType getChatType(int type) {
        ChatType chatType = INDEXES.get(type);
        if (chatType == null)
            log.error("buildingType findById is null, id:{}", type);
        return chatType;
    }
}
