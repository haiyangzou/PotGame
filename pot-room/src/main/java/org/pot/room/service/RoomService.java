package org.pot.room.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.UriSpec;
import org.pot.core.config.ServiceConfig;
import org.pot.core.constant.GlobalProperties;
import org.pot.core.constant.ZKNode;
import org.pot.core.service.ZkClientService;
import org.pot.core.util.TimeUtil;
import org.pot.room.config.RoomConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Service
@Slf4j
public class RoomService {
    @Autowired
    ZkClientService zkClientService;

    private ServiceInstance<ServiceConfig> serviceInstance;
    @Autowired
    private GlobalProperties globalProperties;
    @Autowired
    private RoomConfig roomConfig;

    @PostConstruct
    public void init() {
        try {
            serviceInstance = ServiceInstance.<ServiceConfig>builder()
                    .id(String.valueOf(roomConfig.getId()))
                    .registrationTimeUTC(TimeUtil.currentTimeMillis())
                    .name("login")
                    .address(roomConfig.getPrivateIp())
                    .payload(new ServiceConfig())
                    .port(roomConfig.getRpcPort())
                    .uriSpec(new UriSpec("{address}:{port}"))
                    .build();
            zkClientService.starService(ZKNode.ServicePath.getKey(globalProperties.getProfile()), serviceInstance);
        } catch (Exception e) {
            log.error("game start", e);
        }
    }

    /**
     * 销毁
     */
    @PreDestroy
    public void destroy() {
        zkClientService.unregisterService(serviceInstance);
        log.info("login server stop：{}-->{} ", roomConfig.getId(), roomConfig.toString());
    }
}
