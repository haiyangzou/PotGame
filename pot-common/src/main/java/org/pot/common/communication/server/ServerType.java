package org.pot.common.communication.server;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import org.pot.common.enums.IntEnum;
import org.pot.common.util.EnumUtils;

import java.util.Map;

@Getter
public enum ServerType implements IntEnum {
    GAME_SERVER(0),
    GATE_SERVER(1),
    GLOBAL_SERVER(2),
    SLAVE_SERVER(3),
    STRATEGY(14),
    MAIL_SERVER(15),
    CHAT_SERVER(16),
    ;
    private final int value;

    ServerType(int value) {
        this.value = value;
    }

    private static final Map<Integer, ServerType> idMap = ImmutableMap.copyOf(EnumUtils.toMap(values()));

    public static ServerType valueOf(int value) {
        ServerType type = idMap.get(value);
        if (type != null) return type;
        throw new IllegalArgumentException("Not Found");
    }

    @Override
    public int getId() {
        return value;
    }
}
