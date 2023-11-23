package org.pot.remote.thrift.config;

import lombok.Getter;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.pot.common.communication.server.ServerId;
import org.pot.common.config.EndPointConfig;
import org.pot.remote.thrift.client.pool.TTransportWrap;

@Getter
public class RemoteClientConfig extends EndPointConfig {
    protected int port = -1;
    private int timeout = 1000;
    private ServerId serverId;
    private boolean useConnectionPool;
    private GenericObjectPoolConfig<TTransportWrap> poolConfig;
}
