package org.pot.core.net.netty.websocket;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import lombok.Getter;
import org.pot.common.concurrent.executor.ScheduledExecutor;
import org.pot.common.util.IOUtils;
import org.pot.core.config.NettyConfig;
import org.pot.core.net.connection.ConnectionManager;
import org.pot.core.net.connection.EvictConnectionTask;
import org.pot.core.net.netty.FrameMessage;
import org.pot.core.net.netty.NetEngineStatus;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public abstract class WebBaseEngine<M extends FrameMessage> extends ChannelInitializer<SocketChannel> {
    @Getter
    protected final NettyConfig config;
    @Getter
    protected final ConnectionManager<M> connectionManager;
    @Getter
    protected final NetEngineStatus netEngineStatus = new NetEngineStatus();

    protected final ScheduledExecutor executor = ScheduledExecutor.newScheduledExecutor(1,
            EvictConnectionTask.class.getName());
    protected final Function<WebBaseEngine<M>, WebCodec<M>> webCodecFactory;

    public WebBaseEngine(NettyConfig config, Function<WebBaseEngine<M>, WebCodec<M>> codeFactory) {
        this.connectionManager = new ConnectionManager<>(config.getConnectionIdleSeconds());
        this.webCodecFactory = codeFactory;
        this.config = config;
        this.executor.scheduleAtFixedRate(new EvictConnectionTask<>(connectionManager), config.getConnectionEvictSeconds(), config.getConnectionEvictSeconds(), TimeUnit.SECONDS);
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        // 编解码 http 请求
        pipeline.addLast(new HttpServerCodec(8 * IOUtils.BYTES_PER_KB, 16 * IOUtils.BYTES_PER_KB, 16 * IOUtils.BYTES_PER_KB));
        // 聚合解码 HttpRequest/HttpContent/LastHttpContent 到 FullHttpRequest
        // 保证接收的 Http 请求的完整性
        pipeline.addLast(new HttpObjectAggregator(16 * IOUtils.BYTES_PER_MB));
        // 处理其他的 WebSocketFrame
        pipeline.addLast(new WebSocketServerProtocolHandler("/websocket"));
        // 写文件内容，支持异步发送大的码流，一般用于发送文件流
        pipeline.addLast(new ChunkedWriteHandler());
        // 编解码WebSocketFrame二进制协议
        pipeline.addLast(webCodecFactory.apply(this));
        pipeline.addLast(new WebChannelHandler<>(this));
    }
}
