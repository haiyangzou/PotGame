package org.pot.core.net.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.ByteToMessageCodec;
import io.netty.handler.codec.haproxy.HAProxyMessageDecoder;

public abstract class FrameCodec<M extends FrameMessage> extends ByteToMessageCodec<M> {
    protected final NettyBaseEngine<M> engine;

    FrameCodec(NettyBaseEngine<M> engine) {
        this.engine = engine;
    }

    protected boolean detectHAProxy(ChannelHandlerContext ctx, ByteBuf in) {
        switch (HAProxyMessageDecoder.detectProtocol(in).state()) {
            case NEEDS_MORE_DATA:
                return true;
            case DETECTED:
                ctx.pipeline().addAfter(ctx.name(), null, new HAProxyMessageDecoder());
                ctx.fireChannelRead(in.retain());
                return true;
            case INVALID:
                break;
        }
        return false;
    }
}
