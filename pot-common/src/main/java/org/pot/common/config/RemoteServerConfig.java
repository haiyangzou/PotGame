package org.pot.common.config;

import lombok.Getter;

@Getter
public class RemoteServerConfig extends EndPointConfig {
    private int port = 0;
    private int clientTimeout = 1000;
    private int selectorThreads = 10;
    private int acceptQueueSizePerThread = 10;
}
