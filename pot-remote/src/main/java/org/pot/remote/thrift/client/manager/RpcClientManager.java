package org.pot.remote.thrift.client.manager;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.pot.common.communication.server.ServerId;
import org.pot.remote.thrift.RemoteUtil;
import org.pot.remote.thrift.server.RemoteServer;

@Slf4j
public class RpcClientManager {
    public static final RpcClientManager instance = new RpcClientManager();
    @Getter
    private ServerId localServerId;

    @Getter
    private RemoteServer localRemoteServer;

    public RpcClientManager() {
    }

    public void open(ServerId localServerId, RemoteServer localRemoteServer) {
        RemoteUtil.init();
        this.localServerId = localServerId;
        this.localRemoteServer = localRemoteServer;

    }
}
