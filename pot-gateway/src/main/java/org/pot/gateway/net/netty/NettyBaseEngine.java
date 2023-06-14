package org.pot.gateway.net.netty;

import io.netty.channel.socket.SocketChannel;

import java.util.function.Function;

import org.pot.common.concurrent.executor.ScheduledExcutor;
import org.pot.core.config.NettyConfig;
import org.pot.gateway.net.connection.ConnectionManager;
import org.pot.gateway.net.connection.EvictConnectionTask;

import io.netty.channel.ChannelInitializer;

public abstract class NettyBaseEngine<M extends FrameMessage> extends ChannelInitializer<SocketChannel> {
    protected final ConnectionManager<M> connectionManager;
    protected final ScheduledExcutor excutor = ScheduledExcutor.newScheduledExecutor(1,
            EvictConnectionTask.class.getName());
    protected final Function<NettyBaseEngine<M>, FrameCodec<M>> codecFactory;

    public NettyBaseEngine(NettyConfig config, Function<NettyBaseEngine<M>, FrameCodec<M>> codeFactory) {
        this.connectionManager = new ConnectionManager<>(config.getConnectionIdleSeconds());
        this.codecFactory = codeFactory;
    }
}
