package org.pot.core.net.netty;

import org.pot.core.net.connection.IConnection;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.haproxy.HAProxyMessage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyChannleHandler<M extends FrameMessage> extends ChannelDuplexHandler {
    private final NettyBaseEngine<M> engine;

    public NettyChannleHandler(NettyBaseEngine<M> engine) {
        this.engine = engine;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        SocketChannel socketChannel = (SocketChannel) ctx.channel();
        IConnection<M> connection = engine.getConnectionManager().addConnection(socketChannel);
        if (connection == null) {
            log.error("channelActive channel={}", ctx.channel());
            close(ctx);
        } else {
            log.debug("channelActive Connection={}", connection);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        IConnection<M> connection = engine.getConnectionManager().removeConnection(ctx.channel());
        if (connection == null) {
            log.error("channelActive channel={}", ctx.channel());
            close(ctx);
        } else {
            log.debug("channelActive Connection={}", connection);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        IConnection<M> connection = engine.getConnectionManager().getConnection(ctx.channel());
        if (connection == null) {
            log.error("channelRead channel={}", ctx.channel());
            close(ctx);
            return;
        }
        if (msg instanceof HAProxyMessage) {
            HAProxyMessage haProxyMessage = (HAProxyMessage) msg;
            connection.setRemoteHost(haProxyMessage.sourceAddress());
        }
        if (msg instanceof FrameMessage) {
            int size = connection.getRecvMessageQueueSize();
            int maxSize = engine.getConfig().getConnectionRecvQueueMaxSize();
            if (size > maxSize) {
                log.error("channelRead channel={}", ctx.channel());
                close(ctx);
                return;
            }
            connection.updateLastReadTime();
            M message = (M) msg;
            connection.recvMessage(message);
        }
        ctx.fireChannelRead(msg);
    }

    private void close(ChannelHandlerContext ctx) {
        if (ctx != null && ctx.channel() != null) {
            ctx.close();
        }
    }
}
