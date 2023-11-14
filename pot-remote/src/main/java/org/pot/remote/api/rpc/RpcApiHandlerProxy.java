package org.pot.remote.api.rpc;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.pot.common.communication.server.ServerId;
import org.pot.common.rpc.RpcMessage;
import org.pot.common.text.Jackson;
import org.pot.common.util.JsonUtil;
import org.pot.remote.api.ApiSecurity;
import org.springframework.cglib.proxy.InvocationHandler;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.TreeMap;

@Slf4j
@Getter
public class RpcApiHandlerProxy implements InvocationHandler {
    private final ServerId serverId;
    private final String host;
    private final int port;
    private final String url;

    public RpcApiHandlerProxy(ServerId serverId, String host, int port) {
        this.serverId = serverId;
        this.host = host;
        this.port = port;
        this.url = "http://" + host + ":" + port + "/api";
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] objects) throws Throwable {
        Object result = null;
        RpcMessage rpcMessage = new RpcMessage(method.getDeclaringClass().getName(), method.getName(), objects);
        try {
            TreeMap<String, String> params = Maps.newTreeMap();
            params.put("className", rpcMessage.className);
            params.put("methodName", rpcMessage.methodName);
            params.put("args", KryoSerializer.instance.writeObjectToString(rpcMessage));
            Jackson jackson = ApiSecurity.access(url, "RpcApi.invoke", params);
            boolean success = jackson.getBoolean("success");
            if (success) {
                if (result == null) return null;
                Type returnType = method.getGenericReturnType();
                if (returnType == Void.TYPE) return null;
                JavaType resultJavaType = TypeFactory.defaultInstance().constructType(returnType);
                return JsonUtil.parseJson(JsonUtil.toJson(result), resultJavaType);
            } else {
                log.error("RpcApi.invoke failed.server={},url={},className={},methodName={},args={},result={}", serverId, url, rpcMessage.className, rpcMessage.methodName, JsonUtil.toJson(objects), JsonUtil.toJson(result));
                return null;
            }
        } catch (Throwable throwable) {
            log.error("RpcApi.invoke error.server={},url={},className={},methodName={},args={},result={}",
                    serverId, url, rpcMessage.className, rpcMessage.methodName, JsonUtil.toJson(objects), JsonUtil.toJson(result));
            throw throwable;
        }
    }
}
