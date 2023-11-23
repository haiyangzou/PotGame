package org.pot.remote.thrift.server;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.thrift.TApplicationException;
import org.apache.thrift.TException;
import org.pot.remote.thrift.Remote;
import org.pot.remote.thrift.define.IRemote;
import org.pot.remote.thrift.protocol.RPCRequest;
import org.pot.remote.thrift.protocol.RPCResponse;

import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class RemoteServerHandler implements Remote.Iface {
    private final Map<String, HandlerInvoker> handlerInvokerMap = new ConcurrentHashMap<>();

    @Override
    public ByteBuffer call(ByteBuffer message) throws TException {
        RPCRequest rpcRequest;
        try {
            rpcRequest = RPCRequest.parseByteBuff(message);
        } catch (Throwable throwable) {
            log.error("thrift request protocol error", throwable);
            throw new TApplicationException(TApplicationException.PROTOCOL_ERROR, "proto error");
        }
        RPCResponse rpcResponse;
        try {
            if (StringUtils.isBlank(rpcRequest.getService())) {
                throw new IllegalArgumentException("service empty");
            }
            HandlerInvoker handlerInvoker = handlerInvokerMap.get(rpcRequest.getService());
            if (handlerInvoker == null) {
                throw new IllegalArgumentException("service not found");
            }
            rpcResponse = new RPCResponse(handlerInvoker.call(rpcRequest));
        } catch (Throwable throwable) {
            log.error("thrift handler error", throwable);
            throw new TApplicationException(TApplicationException.PROTOCOL_ERROR, "proto error");
        }
        try {
            ByteBuffer response = RPCResponse.toByteBuff(rpcResponse);
            return response;
        } catch (Throwable e) {
            throw new TApplicationException(TApplicationException.PROTOCOL_ERROR, "proto error");
        }
    }

    public void addHandler(String serviceName, Object handler) {
        handlerInvokerMap.computeIfAbsent(serviceName, k -> new HandlerInvoker(handler));
    }

    public void removeHandler(String serviceName) {
        handlerInvokerMap.remove(serviceName);
    }

    public Object localCall(String serviceName, Method method, Object[] args) {
        HandlerInvoker handlerInvoker = handlerInvokerMap.get(serviceName);
        if (handlerInvoker == null) {
            throw new IllegalArgumentException("local service not found");
        }
        return handlerInvoker.localCall(method, args);
    }

    public boolean containsRemote(Class<? extends IRemote> remoteClass) {
        return handlerInvokerMap.containsKey(remoteClass.getName());
    }
}
