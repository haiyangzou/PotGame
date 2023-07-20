package org.pot.common.util;

import org.pot.common.communication.server.ServerType;

import java.util.HashMap;
import java.util.Map;

public class EnumUtils {
    public static Map<Integer, ServerType> toMap(ServerType[] types) {
        Map<Integer, ServerType> map = new HashMap<>();
        for (ServerType e : types) {
            int id = e.getValue();

            map.put(id, e);
        }
        return map;
    }
}
