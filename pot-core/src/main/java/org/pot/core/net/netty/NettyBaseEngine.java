package org.pot.core.net.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import lombok.Getter;
import org.pot.common.concurrent.executor.ScheduledExecutor;
import org.pot.core.config.NettyConfig;
import org.pot.core.net.connection.ConnectionManager;
import org.pot.core.net.connection.EvictConnectionTask;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public abstract class NettyBaseEngine<M extends FrameMessage> extends ChannelInitializer<SocketChannel> {
    @Getter
    protected final NettyConfig config;
    @Getter
    protected final ConnectionManager<M> connectionManager;
    @Getter
    protected final NetEngineStatus netEngineStatus = new NetEngineStatus();

    protected final ScheduledExecutor executor = ScheduledExecutor.newScheduledExecutor(1,
            EvictConnectionTask.class.getName());
    protected final Function<NettyBaseEngine<M>, FrameCodec<M>> codecFactory;

    public NettyBaseEngine(NettyConfig config, Function<NettyBaseEngine<M>, FrameCodec<M>> codeFactory) {
        this.connectionManager = new ConnectionManager<>(config.getConnectionIdleSeconds());
        this.codecFactory = codeFactory;
        this.config = config;
        this.executor.scheduleAtFixedRate(new EvictConnectionTask<>(connectionManager), config.getConnectionEvictSeconds(), config.getConnectionEvictSeconds(), TimeUnit.SECONDS);
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast(codecFactory.apply(this));
        pipeline.addLast(new NettyChannelHandler<>(this));
    }
}
