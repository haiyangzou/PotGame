package org.pot.strategy.beans;

import lombok.Getter;
import lombok.Setter;
import org.pot.common.Constants;
import org.pot.common.communication.server.Server;

import javax.annotation.PostConstruct;

@Getter
@Setter
public class ServerConst {
    private Server server;
    private boolean debugMode;
    private int workerThreadMin = 10;
    private int workerThreadMax = 10;
    private int workerQueueMaxSize = 1000000;
    private int workerKeepAliveSeconds = 300;

    @PostConstruct
    private void afterPropertiesSet() {
        Constants.Env.setDebugOption(String.valueOf(debugMode));
    }
}
