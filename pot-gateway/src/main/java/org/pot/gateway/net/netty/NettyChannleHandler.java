package org.pot.gateway.net.netty;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.SocketChannel;

public class NettyChannleHandler<M extends FrameMessage> extends ChannelDuplexHandler {
    private final NettyBaseEngine<M> engine;

    public NettyChannleHandler(NettyBaseEngine<M> engine) {
        this.engine = engine;
    }
    
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        SocketChannel socketChannel = (SocketChannel)ctx.channel();

    }

    
}
