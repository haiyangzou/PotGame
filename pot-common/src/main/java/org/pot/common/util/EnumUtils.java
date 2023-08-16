package org.pot.common.util;

import org.pot.common.communication.server.ServerType;
import org.pot.common.enums.IntEnum;

import java.util.HashMap;
import java.util.Map;

public class EnumUtils {
    public static <E extends IntEnum> Map<Integer, E> toMap(E[] types) {
        Map<Integer, E> map = new HashMap<>();
        for (E e : types) {
            int id = e.getId();
            if (map.containsKey(id)) {
                throw new IllegalStateException("Enum has duplicate:Enum=" + e.getClass() + ",id=" + id);
            }
            map.put(id, e);
        }
        return map;
    }

    public static Map<Integer, ServerType> toMap(ServerType[] types) {
        Map<Integer, ServerType> map = new HashMap<>();
        for (ServerType e : types) {
            int id = e.getValue();

            map.put(id, e);
        }
        return map;
    }
}
