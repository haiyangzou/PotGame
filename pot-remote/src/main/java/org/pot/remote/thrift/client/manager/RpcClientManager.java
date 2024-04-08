package org.pot.remote.thrift.client.manager;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.pot.cache.server.ServerListCache;
import org.pot.common.Constants;
import org.pot.common.communication.server.Server;
import org.pot.common.communication.server.ServerId;
import org.pot.common.communication.server.ServerType;
import org.pot.common.concurrent.exception.ExceptionUtil;
import org.pot.common.concurrent.executor.ScheduledExecutor;
import org.pot.common.concurrent.executor.ThreadUtil;
import org.pot.common.util.ClassUtil;
import org.pot.remote.thrift.RemoteUtil;
import org.pot.remote.thrift.define.IRemote;
import org.pot.remote.thrift.server.RemoteServer;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Slf4j
public class RpcClientManager {
    public static final RpcClientManager instance = new RpcClientManager();
    @Getter
    private ServerId localServerId;

    @Getter
    private RemoteServer localRemoteServer;

    private volatile ScheduledExecutor executor;
    private volatile ScheduledFuture<?> ensureFuture;

    private final Map<ServerType, RpcServerTypeCache> servertypeCacheMap = new ConcurrentHashMap<>();

    public RpcClientManager() {
    }

    public void open(ServerId localServerId, RemoteServer localRemoteServer) {
        RemoteUtil.init();
        this.localServerId = localServerId;
        this.localRemoteServer = localRemoteServer;
        this.executor = ScheduledExecutor.newScheduledExecutor(10, RpcClientManager.class.getSimpleName());
        this.ensureFuture = this.executor.scheduleAtFixedRate(this::ensureAvailable, 0, 2, TimeUnit.SECONDS);
    }

    public void ensureAvailable() {
        try {
            List<Server> servers = ServerListCache.instance().getServerList();
            Set<ServerType> serverTypes = servers.stream().map(server -> ServerType.valueOf(server.getTypeId())).collect(Collectors.toSet());
            for (ServerType serverType : serverTypes) {
                List<Server> typeServerList = servers.stream().filter(server -> server.getTypeId() == serverType.getId()).collect(Collectors.toList());
                RpcServerTypeCache rpcServerTypeCache = servertypeCacheMap.computeIfAbsent(serverType, (key) -> new RpcServerTypeCache(serverType));
                rpcServerTypeCache.ensureAvailable(executor, typeServerList);
            }
        } catch (Throwable e) {
            log.error("ensure rpc available error", e);
        }
    }

    public <T extends IRemote> T randomService(ServerType serverType, Class<T> serviceInterface) {
        try {
            RpcServerTypeCache rpcServerTypeCache = servertypeCacheMap.computeIfAbsent(serverType, RpcServerTypeCache::new);
            RpcServerCache rpcServerCache = rpcServerTypeCache.randomRpcServerCache();
            if (rpcServerCache != null) {
                return rpcServerCache.getService(serviceInterface);
            } else {
                String caller = ExceptionUtil.abbreviate(ExceptionUtil.getCaller(RpcClientManager.class));
                log.error("get remote service null:caller={},{} {}", caller, serverType, ClassUtil.getAbbreviatedName(serviceInterface));
            }
        } catch (Throwable throwable) {
            log.error("get remote service error:{} {}", serverType, ClassUtil.getAbbreviatedName(serviceInterface), throwable);
        }
        return null;
    }

    public <T extends IRemote> T getService(ServerType serverType, int serverId, Class<T> serviceInterface) {
        try {
            RpcServerTypeCache rpcServerTypeCache = servertypeCacheMap.computeIfAbsent(serverType, RpcServerTypeCache::new);
            RpcServerCache rpcServerCache = rpcServerTypeCache.getRpcServerCache(serverId);
            if (rpcServerCache != null) {
                return rpcServerCache.getService(serviceInterface);
            } else {
                String caller = ExceptionUtil.abbreviate(ExceptionUtil.getCaller(RpcClientManager.class));
                log.error("get remote service null:caller={},{}[{}] {}", caller, serverType, serverId, ClassUtil.getAbbreviatedName(serviceInterface));
            }
        } catch (Throwable throwable) {
            log.error("get remote service error:{}[{}] {}", serverType, serverId, ClassUtil.getAbbreviatedName(serviceInterface), throwable);
        }
        return null;
    }

    public <T extends IRemote> T getService(ServerId serverId, Class<T> serviceInterface) {
        return serverId == null ? null : this.getService(serverId.serverType, serverId.id, serviceInterface);
    }

    public <T extends IRemote> void ifPresentService(ServerId serverId, Class<T> serviceInterface, Consumer<T> func) {
        T remote = this.getService(serverId.serverType, serverId.id, serviceInterface);
        if (remote != null) func.accept(remote);
    }

    public <T extends IRemote> void ifPresentService(ServerType serverType, Class<T> serviceInterface, Consumer<T> func) {
        T remote = this.randomService(serverType, serviceInterface);
        if (remote != null) func.accept(remote);
    }

    public void shutdown() {
        servertypeCacheMap.forEach((k, v) -> v.close());
        ThreadUtil.cancel(Constants.AWAIT_MS, TimeUnit.MILLISECONDS, ensureFuture);
    }
}
