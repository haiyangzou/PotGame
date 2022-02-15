package org.pot.core.http;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import org.pot.core.netty.http.StreamChunkAggregator;
import org.pot.core.script.ScriptManager;
import org.pot.core.service.HttpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class ClusterHttpChannelInitializer extends ChannelInitializer<SocketChannel> {

  @Autowired
  HttpService httpService;
  @Autowired
  ScriptManager scriptService;

  public ClusterHttpChannelInitializer() {
  }


  @Override
  protected void initChannel(SocketChannel e) throws Exception {
    ChannelPipeline pipeline = e.pipeline();
    pipeline.addLast("decoder", new HttpRequestDecoder());
    pipeline.addLast("aggregator", new StreamChunkAggregator());
    pipeline.addLast("encoder", new HttpResponseEncoder());
    ClusterHttpServerHandler clusterHttpServerHandler = new ClusterHttpServerHandler();
    clusterHttpServerHandler.setHttpService(httpService);
    clusterHttpServerHandler.setScriptService(scriptService);
    pipeline.addLast(clusterHttpServerHandler);
  }
}
