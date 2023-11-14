package org.pot.strategy;

import org.pot.cache.server.ServerListCache;
import org.pot.common.Constants;
import org.pot.common.communication.server.DefinedServerSupplier;
import org.pot.common.communication.server.ServerType;
import org.pot.common.config.GlobalServerConfig;
import org.pot.common.net.ipv4.Ipv4Util;
import org.pot.common.spring.SpringContextUtils;
import org.pot.strategy.beans.ServerConst;
import org.pot.strategy.component.WorkOps;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class InitializeRunner implements ApplicationRunner, DisposableBean {
    @Resource
    private ReactiveStringRedisTemplate globalReactiveRedisTemplate;
    @Resource
    private ServerConst serverConst;
    @Resource
    private WorkOps workOps;
    @Resource
    private GlobalServerConfig globalServerConfig;

    @Override
    public void destroy() throws Exception {
        ServerListCache.shutdown();
        workOps.destroy();
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        ServerListCache.init(globalServerConfig);
        Environment environment = SpringContextUtils.getApplicationContext().getEnvironment();
        String host = Ipv4Util.join(Constants.Env.getLocalhostIp());
        int httpPort = environment.getRequiredProperty("server.port", Integer.class);
        String url = globalServerConfig.getServerInfoUrl();
        serverConst.setServer(DefinedServerSupplier.getServerInfo(url, ServerType.STRATEGY, 0, httpPort, 0));
    }
}
