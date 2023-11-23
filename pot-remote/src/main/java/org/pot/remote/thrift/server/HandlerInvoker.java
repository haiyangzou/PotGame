package org.pot.remote.thrift.server;

import com.esotericsoftware.reflectasm.MethodAccess;
import lombok.Getter;
import org.pot.remote.thrift.protocol.RPCRequest;

import java.lang.reflect.Method;

@Getter
public class HandlerInvoker {
    private final Object handler;
    private final MethodAccess methodAccess;

    public HandlerInvoker(Object handler) {
        this.handler = handler;
        this.methodAccess = MethodAccess.get(handler.getClass());
    }

    Object call(RPCRequest rpcRequest) {
        return methodAccess.invoke(handler, rpcRequest.getMethodName(), rpcRequest.getParameterValues());
    }

    Object localCall(Method method, Object[] args) {
        return methodAccess.invoke(handler, method.getName(), args);
    }
}
