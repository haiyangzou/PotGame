package org.pot.remote.thrift.client;

import com.google.common.collect.ImmutableSet;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.layered.TFastFramedTransport;
import org.pot.common.exception.NetSuppressErrors;
import org.pot.remote.thrift.client.manager.RpcClientManager;
import org.pot.remote.thrift.client.pool.TTransportPool;
import org.pot.remote.thrift.config.RemoteClientConfig;
import org.pot.remote.thrift.define.alive.IKeepAliveRemote;
import org.springframework.cglib.proxy.Callback;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.NoOp;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class RPCClient {
    private final Map<Class<?>, Object> proxyObjectMap = new ConcurrentHashMap<>();
    @Getter
    private final RemoteClientConfig remoteClientConfig;
    @Getter
    private volatile TTransportPool pool;
    @Getter
    private volatile boolean available;
    private final IKeepAliveRemote keepAliveRemote;

    public RPCClient(RemoteClientConfig remoteClientConfig) {
        this.remoteClientConfig = remoteClientConfig;
        this.keepAliveRemote = createProxy(IKeepAliveRemote.class);
        this.pool = new TTransportPool(null, this.remoteClientConfig.getHost(), this.remoteClientConfig.getPort(), this.remoteClientConfig.getTimeout());

    }

    TTransport getTransport() throws Exception {
        TTransport transport;
        if (pool != null) {
            transport = pool.borrowObject();
        } else {
            transport = new TSocket(remoteClientConfig.getHost(), remoteClientConfig.getPort(), remoteClientConfig.getTimeout());
            transport = new TFastFramedTransport(transport);
            transport.open();
        }
        return transport;
    }

    public void ensureAvailable() {
        try {
            if (pool != null) {
                pool.evict();
                pool.prepare();
            }
            try {
                keepAliveRemote.ping();
            } catch (Throwable cause) {
                available = false;
                if (NetSuppressErrors.isSuppressed(cause)) return;
                log.error("thrift client {}:{} ping error cause:{}", remoteClientConfig.getHost(), remoteClientConfig.getPoolConfig(), NetSuppressErrors.suppress(cause));
            }
        } catch (Throwable cause) {
            available = false;
            if (NetSuppressErrors.isSuppressed(cause)) return;
            log.error("thrift client {}:{} pool error cause:{}", remoteClientConfig.getHost(), remoteClientConfig.getPoolConfig(), NetSuppressErrors.suppress(cause));
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T createProxy(Class<?> clazz) {
        return (T) proxyObjectMap.computeIfAbsent(clazz, k -> {
            Enhancer enhancer = new Enhancer();
            enhancer.setInterfaces(new Class[]{clazz});
            Set<Method> methods = ImmutableSet.copyOf(clazz.getDeclaredMethods());
            enhancer.setCallbackFilter(method -> methods.contains(method) ? 0 : 1);
            enhancer.setCallbacks(new Callback[]{new RPCClientInvocationHandler(RPCClient.this), NoOp.INSTANCE});
            return enhancer.create();
        });
    }

    public boolean isLocal() {
        if (RpcClientManager.instance.getLocalServerId().serverType != this.remoteClientConfig.getServerId().serverType) {
            return false;
        }
        return Objects.equals(RpcClientManager.instance.getLocalServerId().id, this.remoteClientConfig.getServerId().id);
    }

    public void close() {
        proxyObjectMap.clear();
        if (pool != null) {
            pool.destroy();
            pool = null;
        }
    }
}
