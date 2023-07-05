package org.pot.core.engine;

import org.pot.common.concurrent.executor.AsyncExecutor;

public interface IEngine<T extends EngineConfig> {
    T getConfig();

    AsyncExecutor getAsyncExecutor();
}
