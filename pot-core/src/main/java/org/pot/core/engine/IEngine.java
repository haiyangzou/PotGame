package org.pot.core.engine;

import org.pot.common.concurrent.executor.AsyncExecutor;
import org.pot.core.net.netty.FrameMessage;
import org.pot.core.net.netty.NettyClientEngine;
import org.pot.core.net.netty.NettyServerEngine;

public interface IEngine<T extends EngineConfig> {
    T getConfig();

    AsyncExecutor getAsyncExecutor();

    IHttpServer getHttpServer();

    NettyClientEngine<? extends FrameMessage> getNettyClientEngine();

    NettyServerEngine<? extends FrameMessage> getNettyServerEngine();
}
