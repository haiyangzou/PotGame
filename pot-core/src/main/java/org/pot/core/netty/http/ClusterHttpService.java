package org.pot.core.netty.http;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.pot.core.netty.config.NettyProperties;
import org.pot.core.service.HttpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * http服务器
 * <p>
 * </p>
 * @author JiangZhiYong
 * @mail 359135103@qq.com
 */
@Service
public class ClusterHttpService extends HttpService {

    @Autowired
    private HttpServer httpServer;
    @Autowired
    private ClusterHttpChannelInitializer clusterHttpChannelInititialier;
    @Autowired
    private NettyProperties nettyProperties;

    @PostConstruct
    public void init(){
        httpServer.setNettyServerConfig(nettyProperties.getServerConfigs().get(1));
        httpServer.setChannelInitializer(clusterHttpChannelInititialier);
        httpServer.start();
    }

    @PreDestroy
    public void destroy(){
        httpServer.stop();
    }

}
