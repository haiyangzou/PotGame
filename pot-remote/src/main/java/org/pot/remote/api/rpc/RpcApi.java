package org.pot.remote.api.rpc;

import com.esotericsoftware.reflectasm.MethodAccess;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang3.StringUtils;
import org.pot.PotPackage;
import org.pot.common.communication.server.ServerId;
import org.pot.common.util.ClassUtil;
import org.pot.remote.api.Api;
import org.springframework.cglib.proxy.Callback;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.NoOp;
import org.springframework.cglib.reflect.FastClass;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Api
public class RpcApi {
    private static final Map<Class<? extends IRpcApiHandler>, FastClass> handlerFastClassMap;

    static {
        Map<Class<? extends IRpcApiHandler>, FastClass> tempHandlerFastClassMap = new HashMap<>();
        Set<Class<? extends IRpcApiHandler>> handlerClassSet = ClassUtil.getSubTypeOf(PotPackage.class, IRpcApiHandler.class);
        for (Class<? extends IRpcApiHandler> handlerClass : handlerClassSet) {
            try {
                IRpcApiHandler handler = null;
                if (ClassUtil.isConcrete(handlerClass)) {
                    FastClass fastClass = FastClass.create(handlerClass);
                    tempHandlerFastClassMap.putIfAbsent(handlerClass, fastClass);
                    handler = (IRpcApiHandler) fastClass.newInstance();
                } else {
                    Set<Class<?>> handlerImplClassSet = handlerClassSet.stream()
                            .filter(handlerClass::isAssignableFrom)
                            .filter(ClassUtil::isConcrete)
                            .collect(Collectors.toSet());
                    int size = handlerImplClassSet.size();
                    if (size <= 0) {
                        continue;
                    }
                    if (size > 1) {
                        throw new IllegalStateException("RpcApiHandler" + ClassUtil.getAbbreviatedName(handlerClass) + "is null");
                    }
                    for (Class<? extends IRpcApiHandler> handlerImplClass : handlerClassSet) {
                        FastClass fastClass = FastClass.create(handlerImplClass);
                        tempHandlerFastClassMap.putIfAbsent(handlerClass, fastClass);
                        handler = (IRpcApiHandler) fastClass.newInstance();
                    }
                }
                if (handler == null) {
                    throw new IllegalStateException("RpcApiHandler" + ClassUtil.getAbbreviatedName(handlerClass) + "is null");
                }
            } catch (Exception e) {
                throw new IllegalStateException("RpcApiHandler" + ClassUtil.getAbbreviatedName(handlerClass) + "is null");
            }
        }
        handlerFastClassMap = ImmutableMap.copyOf(tempHandlerFastClassMap);
    }

    private static final Map<Class<? extends IRpcApiHandler>, Map<String, Object>> handlerProxyObjectMap = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public static <T extends IRpcApiHandler> T newRpcApiHandlerProxy(Class<T> clazz, ServerId serverId, String host, int port) {
        if (clazz == null) throw new IllegalArgumentException("class is invalid");
        if (serverId == null) throw new IllegalArgumentException("ServerId is invalid");
        if (StringUtils.isBlank(host)) throw new IllegalArgumentException("host is invalid");
        if (port <= 0 || port > 65535) throw new IllegalArgumentException("Port is invalid");
        String identity = StringUtils.joinWith("|", serverId.serverType.name(), serverId.id, host, port);
        Map<String, Object> proxyObjectMap = handlerProxyObjectMap.computeIfAbsent(clazz, k -> new ConcurrentHashMap<>());
        return (T) proxyObjectMap.computeIfAbsent(identity, k -> {
            Enhancer enhancer = new Enhancer();
            enhancer.setInterfaces(new Class[]{clazz});
            Set<Method> methods = ImmutableSet.copyOf(clazz.getDeclaredMethods());
            enhancer.setCallbackFilter(method -> methods.contains(method) ? 0 : 1);
            enhancer.setCallbacks(new Callback[]{new RpcApiHandlerProxy(serverId, host, port), NoOp.INSTANCE});
            return enhancer.create();
        });
    }

    private static final Map<Class<?>, MethodAccess> handlerMethodAccessMap = new ConcurrentHashMap<>();

//    @Api
//    public Object invoke(@ApiParam("args") String args) throws Exception {
//        RpcMessage rpcMessage = KryoSerializer.insta
//    }


}
