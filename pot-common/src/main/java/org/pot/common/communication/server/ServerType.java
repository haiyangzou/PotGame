package org.pot.common.communication.server;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import org.pot.common.util.EnumUtils;

import java.util.Map;

public enum ServerType {
    GAME_SERVER(0);
    @Getter
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
}
