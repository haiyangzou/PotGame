package org.pot.core.net.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.NetUtil;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.pot.common.concurrent.executor.ExecutorUtil;
import org.pot.common.concurrent.executor.ScheduledExcutor;
import org.pot.common.exception.NetSuppressErrors;
import org.pot.core.config.NettyConfig;
import org.pot.core.net.connection.ConnectionManager;
import org.pot.core.net.connection.EvictConnectionTask;
import org.pot.core.net.connection.IConnection;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Slf4j
public abstract class NettyServerEngine<M extends FrameMessage> extends NettyBaseEngine<M> {
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private List<Channel> serverChannels;

    public NettyServerEngine(NettyConfig config, Function<NettyBaseEngine<M>, FrameCodec<M>> codeFactory) {
        super(config, codeFactory);
    }

    public void stop() {
        try {
            for (Channel serverChannel : serverChannels) {
                ChannelFuture f = serverChannel.close();
                f.awaitUninterruptibly();
            }
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
        ExecutorUtil.shutdownExecutor(executor);
    }

    public void start() {
        ServerBootstrap bootstrap = new ServerBootstrap();
        configureWorkerAndChannel(bootstrap);
        bootstrap.childHandler(this);
        if (config.getBacklog() > NetUtil.SOMAXCONN) {
            log.warn("config netty backlog size as {} but systemSOMAXCONN is{}", config.getBacklog(), NetUtil.SOMAXCONN);
        }
        bootstrap.option(ChannelOption.SO_REUSEADDR, true)
                .option(ChannelOption.SO_BACKLOG, config.getBacklog())
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true);
        bindServer(bootstrap);
    }

    private void bindServer(ServerBootstrap bootstrap) {
        String host = config.getHost();
        if (config.getPort() <= 0) {
            throw new RuntimeException("netty server port must be positive");
        }
        int[] ports = new int[]{config.getPort()};
        if (ArrayUtils.isEmpty(ports)) {
            throw new RuntimeException("netty server ports is empty");
        }
        serverChannels = new ArrayList<>(ports.length);
        for (int port : ports) {
            try {
                Channel f;
                if (StringUtils.isBlank(host) || "*".equals(host)) {
                    log.info("netty server bind on port {}", port);
                    f = bootstrap.bind(port).sync().channel();
                } else {
                    log.info("netty server bind on port {}:{}", host, port);
                    f = bootstrap.bind(host, port).sync().channel();
                }
                serverChannels.add(f);
            } catch (Exception e) {
                throw new RuntimeException("netty server bind on port" + port + "error", e);
            }
        }
    }

    private void configureWorkerAndChannel(ServerBootstrap bootstrap) {
        if (config.isEnableNative()) {
            return;
        }
        log.info("{} setup with Nio", getClass().getSimpleName());
        DefaultThreadFactory bossThreadFactory = new DefaultThreadFactory(getClass().getSimpleName() + "Worker");
        bossGroup = new NioEventLoopGroup(config.getBossThreads(), bossThreadFactory);
        DefaultThreadFactory workerThreadFactory = new DefaultThreadFactory(getClass().getSimpleName() + "Worker");
        workerGroup = new NioEventLoopGroup(config.getWorkerThreads(), workerThreadFactory);
        bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class);
    }
}
