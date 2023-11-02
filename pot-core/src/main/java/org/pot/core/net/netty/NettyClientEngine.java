package org.pot.core.net.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SystemUtils;
import org.pot.common.concurrent.executor.ExecutorUtil;
import org.pot.common.exception.NetSuppressErrors;
import org.pot.core.config.NettyConfig;
import org.pot.core.net.connection.IConnection;

import java.util.function.Function;

@Slf4j
public class NettyClientEngine<M extends FrameMessage> extends NettyBaseEngine<M> {
    private Bootstrap clientBootstrap;
    private EventLoopGroup eventLoopGroup;

    public NettyClientEngine(NettyConfig config, Function<NettyBaseEngine<M>, FrameCodec<M>> codeFactory) {
        super(config, codeFactory);
    }

    public void stop() {
        if (eventLoopGroup != null) {
            eventLoopGroup.shutdownGracefully();
        }
        ExecutorUtil.shutdownExecutor(executor);
    }

    public void start() {
        configureWorkerAndChannel();
        clientBootstrap.handler(this);
        clientBootstrap.option(ChannelOption.SO_REUSEADDR, true);
    }

    private void configureWorkerAndChannel() {
        clientBootstrap = new Bootstrap();
        if (config.isEnableNative()) {
            if (SystemUtils.IS_OS_LINUX) {
                if (Epoll.isAvailable()) {
                    log.info("{} setup with epoll", getClass().getSimpleName());
                    DefaultThreadFactory threadFactory = new DefaultThreadFactory(getClass().getSimpleName() + "EpollWorker");
                    eventLoopGroup = new EpollEventLoopGroup(config.getWorkerThreads(), threadFactory);
                    clientBootstrap.group(eventLoopGroup).channel(EpollSocketChannel.class);
                    return;
                } else {
                    log.info("native enabled,but Epoll not available, case", Epoll.unavailabilityCause());
                }
            }
        }
        log.info("{} setup with Nio", getClass().getSimpleName());
        DefaultThreadFactory threadFactory = new DefaultThreadFactory(getClass().getSimpleName() + "Worker");
        eventLoopGroup = new EpollEventLoopGroup(config.getWorkerThreads(), threadFactory);
        clientBootstrap.group(eventLoopGroup).channel(NioSocketChannel.class);
    }

    public IConnection<M> connect(String host, int port) {
        try {
            Channel channel = clientBootstrap.connect(host, port).addListener(ChannelFutureListener.CLOSE_ON_FAILURE).sync().channel();
            return connectionManager.addConnection(channel, host, port);
        } catch (Throwable throwable) {
            log.error("connect to {}:{} failed,cause={}", host, port, NetSuppressErrors.suppress(throwable));
            return null;
        }
    }

}
