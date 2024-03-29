package org.pot.core.netty.http;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.*;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
@Slf4j
public class StreamChunkAggregator extends MessageToMessageDecoder<HttpObject> {

    private volatile FullHttpMessage curMsg;
    public static final int DEFAULT_MAX_COMPOSITEBUFFER_COMPONENTS = 1024;
    private int maxNumComponents = DEFAULT_MAX_COMPOSITEBUFFER_COMPONENTS;

    @Override
    protected void decode(ChannelHandlerContext ctx, HttpObject msg,
                          List<Object> out) {
        try {
            FullHttpMessage curMsg = this.curMsg;
            if (msg instanceof HttpMessage) {
                HttpRequest header = (HttpRequest) msg;
                this.curMsg = curMsg = new DefaultFullHttpRequest(header.protocolVersion(),
                        header.method(), header.uri(), Unpooled.compositeBuffer(maxNumComponents));
                if (header.headers() != null) {
                    curMsg.headers().set(header.headers());
                }
            } else if (msg instanceof HttpContent) {
                assert curMsg != null;
                HttpContent chunk = (HttpContent) msg;
                if (chunk.content().isReadable()) {
                    chunk.retain();
                }
                if (null != chunk.content()) {
                    curMsg.content().writeBytes(chunk.content());
                } else {
                    System.out.println("chunk.content()  is null");
                }
                final boolean last;
                if (null != chunk.content() && !chunk.decoderResult().isSuccess()) {
                    curMsg.setDecoderResult(
                            DecoderResult.failure(chunk.decoderResult().cause()));
                    last = true;
                } else {
                    last = chunk instanceof LastHttpContent;
                }
                if (last) {
                    this.curMsg = null;
                    out.add(curMsg);
                }
            } else {
                throw new Error();
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        try {
            super.channelInactive(ctx);
            if (curMsg != null) {
                curMsg.release();
                curMsg = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        try {
            super.handlerRemoved(ctx);
            if (curMsg != null) {
                curMsg.release();
                curMsg = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
    }
}
