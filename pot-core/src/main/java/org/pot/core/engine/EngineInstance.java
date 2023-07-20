package org.pot.core.engine;

import org.pot.core.AppEngine;


public class EngineInstance {
    private static volatile AppEngine<? extends EngineConfig> instance;

    public static AppEngine<? extends EngineConfig> getInstance() {
        return instance;
    }

    public static void setInstance(AppEngine<? extends EngineConfig> engine) {
        synchronized (EngineInstance.class) {
            if (EngineInstance.instance == null || EngineInstance.instance == engine) {
                EngineInstance.instance = engine;
            } else {
                String errorInfo = String.format(
                        "engine instance hash ben initialized to %s, can not set engine instance to %s",
                        instance.getClass().getName(), engine.getClass().getName());
                throw new IllegalStateException(errorInfo);
            }
        }
    }
}
