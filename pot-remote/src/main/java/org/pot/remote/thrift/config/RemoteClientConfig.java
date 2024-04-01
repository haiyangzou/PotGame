package org.pot.remote.thrift.config;

import lombok.Getter;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.pot.common.communication.server.ServerId;
import org.pot.common.config.EndPointConfig;
import org.pot.remote.thrift.client.pool.TTransportPool;
import org.pot.remote.thrift.client.pool.TTransportWrap;

@Getter
public class RemoteClientConfig extends EndPointConfig {
    protected int port;
    private final int timeout = 1000;
    private final ServerId serverId;
    private final boolean useConnectionPool;
    private GenericObjectPoolConfig<TTransportWrap> poolConfig;

    public RemoteClientConfig(String host, int port, int serverType, int serverId) {
        this(host, port, serverType, serverId, true);
    }

    public RemoteClientConfig(String host, int port, int serverType, int serverId, boolean useConnectionPool) {
        this.host = host;
        this.port = port;
        this.serverId = ServerId.of(serverType, serverId);
        this.useConnectionPool = useConnectionPool;
        if (isUseConnectionPool()) {
            this.poolConfig = TTransportPool.defaultConfig();
        }
    }
}
