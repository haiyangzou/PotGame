package org.pot.strategy.component;

import lombok.extern.slf4j.Slf4j;
import org.pot.common.communication.server.ServerId;
import org.pot.remote.api.rpc.IRpcApiHandler;
import org.pot.remote.api.rpc.RpcApi;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RpcApiOps {
    public <T extends IRpcApiHandler> T newRpcApiHandler(Class<T> clazz, ServerId serverId, String host, int port) {
        return RpcApi.newRpcApiHandlerProxy(clazz, serverId, host, port);
    }

}
