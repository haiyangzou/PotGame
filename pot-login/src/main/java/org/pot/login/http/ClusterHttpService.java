package org.pot.login.http;

import lombok.extern.slf4j.Slf4j;
import org.pot.core.netty.config.NettyProperties;
import org.pot.core.netty.http.HttpServer;
import org.pot.core.script.ScriptManager;
import org.pot.core.service.HttpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * http服务器
 * <p>
 * </p>
 * @author JiangZhiYong
 * @mail 359135103@qq.com
 */
@Service
@Slf4j
public class ClusterHttpService extends HttpService {

    @Autowired
    private HttpServer httpServer;
    @Autowired
    private ClusterHttpChannelInitializer clusterHttpChannelInititialier;
    @Autowired
    private NettyProperties nettyProperties;
    @Autowired
    private ScriptManager scriptService;

    @PostConstruct
    public void init() {
        scriptService.init((str) -> {
            log.error("脚本加载错误:{}", str);
            System.exit(0);
        });
        if (CollectionUtils.isEmpty(nettyProperties.getServerConfigs())) {
            log.error("server config error");
        }
        httpServer.setNettyServerConfig(nettyProperties.getServerConfigs().get(1));
        httpServer.setChannelInitializer(clusterHttpChannelInititialier);
        httpServer.start();
    }

    @PreDestroy
    public void destroy(){
        httpServer.stop();
    }

}
