package org.pot.remote.thrift.client;

import lombok.extern.slf4j.Slf4j;
import org.pot.common.Constants;
import org.pot.remote.thrift.client.manager.RpcClientManager;
import org.pot.remote.thrift.protocol.RPCRequest;
import org.springframework.cglib.proxy.InvocationHandler;

import java.lang.reflect.Method;
import java.nio.ByteBuffer;

@Slf4j
public class RPCClientInvocationHandler implements InvocationHandler {
    private final RPCClient rpcClient;

    public RPCClientInvocationHandler(RPCClient rpcClient) {
        this.rpcClient = rpcClient;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        long time = System.currentTimeMillis();
        String serviceName = method.getDeclaringClass().getName();
        if (rpcClient.isLocal()) {
            Object result = RpcClientManager.instance.getLocalRemoteServer().localInvoke(serviceName, method, args);
            long elapsed = System.currentTimeMillis() - time;
            if (elapsed > Constants.RUN_SLOW_MS) {
                log.warn("onLocalCallFinish SLow");
            }
            return result;
        }
        RPCRequest rpcRequest = new RPCRequest();
        rpcRequest.setService(serviceName);
        rpcRequest.setMethodName(method.getName());
        rpcRequest.setParameterValues(args);
        String host = rpcClient.getRemoteClientConfig().getHost();
        int port = rpcClient.getRemoteClientConfig().getPort();
        try {
            ByteBuffer request = RPCRequest.toByteBuff(rpcRequest);
            rpcClient.getTransport();
        } catch (Throwable throwable) {
            throw throwable;
        }
        return null;
    }
}
