package org.pot.game.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.x.discovery.ServiceCache;
import org.apache.curator.x.discovery.details.ServiceCacheListener;
import org.pot.core.config.ServiceConfig;
import org.pot.core.constant.GlobalProperties;
import org.pot.core.constant.ZKNode;
import org.pot.core.service.ZkClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
@Slf4j
public class GameService {
    @Autowired
    private ZkClientService zkClientService;
    @Autowired
    private GlobalProperties globalProperties;
    /**
     * 网关连接缓存
     */
    private ServiceCache<ServiceConfig> loginServiceCache;
    @PostConstruct
    public void init() throws Exception {
        initZkService();
    }
    /**
     * 初始化zookeeper
     *
     * @throws Exception
     */
    private void initZkService() throws Exception {
        zkClientService.starService(ZKNode.ServicePath.getKey(globalProperties.getProfile()), null);
        loginServiceCache = zkClientService.getServiceDiscovery().serviceCacheBuilder().name("login").build();
        loginServiceCache.addListener(new ServiceCacheListener() {
            @Override
            public void cacheChanged() {
                log.info("gate service change {}", loginServiceCache.getInstances().size());
                loginServiceCache.getInstances().forEach(it -> {
                    log.info("now gate:{} {}:{}", it.getId(), it.getAddress(), it.getPort());
                });
//                gateInfoService.updateGateServer(loginServiceCache.getInstances());
            }

            @Override
            public void stateChanged(CuratorFramework client, ConnectionState newState) {

            }
        });
        loginServiceCache.start();
    }
}
