package org.pot.remote.thrift.client.manager;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.pot.cache.server.ServerListCache;
import org.pot.common.communication.server.Server;
import org.pot.common.communication.server.ServerId;
import org.pot.common.communication.server.ServerType;
import org.pot.common.concurrent.executor.ScheduledExcutor;
import org.pot.remote.thrift.RemoteUtil;
import org.pot.remote.thrift.server.RemoteServer;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
public class RpcClientManager {
    public static final RpcClientManager instance = new RpcClientManager();
    @Getter
    private ServerId localServerId;

    @Getter
    private RemoteServer localRemoteServer;

    private volatile ScheduledExcutor executor;

    private volatile ScheduledFuture<?> ensureFuture;

    private final Map<ServerType, RpcServerTypeCache> servertypeCacheMap = new ConcurrentHashMap<>();

    public RpcClientManager() {
    }

    public void open(ServerId localServerId, RemoteServer localRemoteServer) {
        RemoteUtil.init();
        this.localServerId = localServerId;
        this.localRemoteServer = localRemoteServer;
        this.executor = ScheduledExcutor.newScheduledExecutor(10, RpcClientManager.class.getSimpleName());
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
}
