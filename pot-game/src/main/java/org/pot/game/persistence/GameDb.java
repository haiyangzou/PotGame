package org.pot.game.persistence;

import org.pot.core.engine.EngineConfig;
import org.pot.dal.DbSupport;

import java.util.Map;

public class GameDb {
    private static DbSupport LOCAL;

    public static void init(EngineConfig engineConfig) {

    }

    public static DbSupport local() {
        return LOCAL;
    }

    public static void close() {

    }

    public static Map<String, Object> getStatus() {
        return null;
    }
}
