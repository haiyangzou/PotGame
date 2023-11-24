package org.pot.remote.thrift.client.manager;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.pot.common.communication.server.Server;
import org.pot.common.communication.server.ServerType;
import org.pot.remote.thrift.RemoteUtil;
import org.pot.remote.thrift.client.RPCClient;
import org.pot.remote.thrift.config.RemoteClientConfig;
import org.pot.remote.thrift.define.IRemote;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class RpcServerCache {
    @Getter
    private Server server;
    @Getter
    private RPCClient rpcClient;

    private final Map<Class<? extends IRemote>, IRemote> serviceProxyMap = new ConcurrentHashMap<>();

    public RpcServerCache(Server server) {
        this.server = server;
        this.rpcClient = new RPCClient(new RemoteClientConfig(server.getHost(), server.getRpcPort(), server.getTypeId(), server.getServerId()));
        ServerType serverType = ServerType.valueOf(server.getTypeId());
        Set<Class<? extends IRemote>> remoteServiceInterfaces = RemoteUtil.getRemoteServiceInterface(serverType);
        for (Class<? extends IRemote> remoteServiceInterface : remoteServiceInterfaces) {
            serviceProxyMap.put(remoteServiceInterface, rpcClient.createProxy(remoteServiceInterface));
        }
    }

    public boolean checkClose(Server server) {
        return server.getRpcPort() <= 0
                || StringUtils.isBlank(server.getHost())
                || !server.getHost().equals(this.server.getHost())
                || !server.getRpcPort().equals(this.server.getRpcPort());
    }

    void ensureAvailable() {
        if (rpcClient != null) {
            rpcClient.ensureAvailable();
        }
    }

    public void close() {
        serviceProxyMap.clear();
        if (this.rpcClient != null) {
            this.rpcClient.close();
            this.rpcClient = null;
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends IRemote> T getService(Class<T> serviceClass) {
        return (T) this.serviceProxyMap.get(serviceClass);
    }

    boolean isAvailable() {
        return rpcClient != null && rpcClient.isAvailable();
    }
}
