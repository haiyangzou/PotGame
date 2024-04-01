package org.pot.common.config;

import lombok.Getter;
import org.apache.commons.configuration2.Configuration;

@Getter
public class RemoteServerConfig extends EndPointConfig {
    private int port = 0;
    private final int clientTimeout = 1000;
    private final int selectorThreads = 10;
    private final int acceptQueueSizePerThread = 10;
    private final int backlog = 1024;
    private final int maxReadBufferBytes = 2 * 1024 * 1024;
    private final int workerThreadsMin = 10;
    private final int workerThreadsMax = 200;
    private final int workerQueueMaxSize = 1000000;
    private final int workerKeepAliveSeconds = 300;

    public static RemoteServerConfig loadRemoteConfig(Configuration config) {
        RemoteServerConfig remoteServerConfig = new RemoteServerConfig();
        remoteServerConfig.setProperties(config);
        return remoteServerConfig;
    }

    private void setProperties(Configuration config) {
        this.host = config.getString("rpc.bind", host);
        this.port = config.getInt("rpc.port", port);
    }
}
