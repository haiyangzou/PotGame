package org.pot.core;

import org.pot.common.concurrent.executor.AsyncExecutor;
import org.pot.core.engine.EngineConfig;
import org.pot.core.engine.IEngine;

import lombok.Getter;

public class AppEngine<T extends EngineConfig> extends Thread implements IEngine<T> {
    @Getter
    private final AsyncExecutor asyncExecutor;
    @Getter
    private final T config;

    protected AppEngine(Class<T> configClass) throws Exception {
        this.config = null;
        this.asyncExecutor = new AsyncExecutor(getConfig().getAsyExecutorConfig());
    }
}
