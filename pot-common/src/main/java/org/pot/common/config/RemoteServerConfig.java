package org.pot.common.config;

import lombok.Getter;
import org.apache.commons.configuration2.Configuration;

@Getter
public class RemoteServerConfig extends EndPointConfig {
    private int port = 0;
    private int clientTimeout = 1000;
    private int selectorThreads = 10;
    private int acceptQueueSizePerThread = 10;

    public static RemoteServerConfig loadRemoteConfig(Configuration config) {
        RemoteServerConfig remoteServerConfig = new RemoteServerConfig();
        remoteServerConfig.setProperties(config);
        return remoteServerConfig;
    }

    private void setProperties(Configuration config) {
    }
}
