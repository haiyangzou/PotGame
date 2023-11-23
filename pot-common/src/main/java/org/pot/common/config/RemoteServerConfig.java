package org.pot.common.config;

import lombok.Getter;
import org.apache.commons.configuration2.Configuration;

@Getter
public class RemoteServerConfig extends EndPointConfig {
    private int port = 0;
    private int clientTimeout = 1000;
    private int selectorThreads = 10;
    private int acceptQueueSizePerThread = 10;
    private int backlog = 1024;
    private int maxReadBufferBytes = 2 * 1024 * 1024;
    private int workerThreadsMin = 10;
    private int workerThreadsMax = 200;
    private int workerQueueMaxSize = 1000000;
    private int workerKeepAliveSeconds = 300;

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
