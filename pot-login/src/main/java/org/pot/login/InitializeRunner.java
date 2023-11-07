package org.pot.login;

import org.pot.cache.player.PlayerCaches;
import org.pot.common.Constants;
import org.pot.common.communication.server.ServerType;
import org.pot.common.net.ipv4.Ipv4Util;
import org.pot.common.spring.SpringContextUtils;
import org.pot.login.beans.ServerConst;
import org.pot.login.component.WorkOps;
import org.pot.login.config.PlayerCacheConfiguration;
import org.pot.login.service.ServerListService;
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
    private PlayerCacheConfiguration playerCacheConfiguration;
    @Resource
    private ServerListService serverListService;
    @Resource
    private ServerConst serverConst;
    @Resource
    private WorkOps workOps;

    @Override
    public void destroy() throws Exception {
        PlayerCaches.shutdown();
        workOps.destroy();
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        PlayerCaches.init(playerCacheConfiguration.toPlayerCacheConfig(), globalReactiveRedisTemplate, null, null);
        Environment environment = SpringContextUtils.getApplicationContext().getEnvironment();
        String host = Ipv4Util.join(Constants.Env.getLocalhostIp());
        int httpPort = environment.getRequiredProperty("server.port", Integer.class);
        serverConst.setServer(serverListService.findServerOnNotExistsAutoCreate(ServerType.GLOBAL_SERVER.getId(), host, 0, httpPort, 0));
    }
}
