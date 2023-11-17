package org.pot.core.net.netty.websocket;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.handler.codec.haproxy.HAProxyMessageDecoder;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import org.pot.core.net.netty.FrameMessage;

public abstract class WebCodec<M extends FrameMessage> extends MessageToMessageCodec<WebSocketFrame, M> {
    protected final WebBaseEngine<M> engine;
    protected volatile boolean decode = false;

    protected boolean detectHAProxy(ChannelHandlerContext ctx, ByteBuf in) {
        if (decode) {
            return false;
        }
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

    WebCodec(WebBaseEngine<M> engine) {
        this.engine = engine;
    }
}
