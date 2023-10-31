package org.pot.cache.server;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.pot.common.Constants;
import org.pot.common.communication.server.*;
import org.pot.common.concurrent.executor.AsyncExecutor;
import org.pot.common.concurrent.executor.ThreadUtil;
import org.pot.common.config.GlobalServerConfig;
import org.pot.common.util.MapUtil;

import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
public class ServerListCache {
    private static volatile ServerListCache instance;
    private final GlobalServerConfig globalServerConfig;
    private final AsyncExecutor executor;
    private final ScheduledFuture<?> refreshFuture;
    @Getter
    private volatile List<Server> serverList = Collections.emptyList();
    @Getter
    private volatile Map<Integer, Map<Integer, Server>> serverMap = Collections.emptyMap();
    @Getter
    private volatile List<GameServer> gameServerList = Collections.emptyList();
    @Getter
    private volatile Map<Integer, GameServer> gameServerMap = Collections.emptyMap();

    public synchronized static void init(GlobalServerConfig globalServerConfig) {
        if (instance == null) {
            instance = new ServerListCache(globalServerConfig);
        }
    }

    public static ServerListCache instance() {
        return instance;
    }

    public ServerListCache(GlobalServerConfig globalServerConfig) {
        this.globalServerConfig = globalServerConfig;
        this.refresh();
        this.executor = new AsyncExecutor(ServerListCache.class.getSimpleName(), 1);
        this.refreshFuture = this.executor.scheduleAtFixedRate(this::refresh, 1, 1, TimeUnit.MINUTES);
    }

    private void refresh() {
        this.requestServerList();
        this.requestGameServerList();
    }

    private void requestServerList() {
        String url = globalServerConfig.getGameServerListUrl();
        try {
            List<Server> servers = DefinedServerSupplier.getServerList(url);
            serverList = Collections.unmodifiableList(servers.stream().sorted(Comparator.naturalOrder()).collect(Collectors.toList()));
            Map<Integer, Map<Integer, Server>> map = new TreeMap<>();
            servers.forEach(server -> map.computeIfAbsent(server.getTypeId(), k -> new TreeMap<>()).put(server.getServerId(), server));
            serverMap = MapUtil.immutableMapMap(map);
        } catch (Throwable throwable) {
            log.error("Get Server List Error.url={}", url, throwable);
        }
    }

    private void requestGameServerList() {
        String url = globalServerConfig.getGameServerListUrl();
        try {
            List<GameServer> servers = DefinedServerSupplier.getGameServerList(url);
            gameServerList = Collections.unmodifiableList(servers.stream().sorted(Comparator.naturalOrder()).collect(Collectors.toList()));
            gameServerMap = Collections.unmodifiableMap(MapUtil.toTreeMap(Integer::compareTo, servers, GameServer::getServerId, server -> server));
        } catch (Throwable throwable) {
            log.error("Get Game Server List Error.url={}", url, throwable);
        }
    }

    private void close() {
        this.refreshFuture.cancel(false);
        ThreadUtil.await(Constants.AWAIT_MS, TimeUnit.MILLISECONDS, refreshFuture::isDone);
        ThreadUtil.await(Constants.AWAIT_MS, TimeUnit.MILLISECONDS, executor::isIdle);
        this.executor.shutdown();
    }

    public List<Server> getServerList(ServerType serverType) {
        return getServerList(serverType, null);
    }

    public List<Server> getServerList(ServerType serverType, Predicate<ServerId> predicate) {
        return serverMap.getOrDefault(serverType.getId(), Collections.emptyMap()).values().stream()
                .filter(server -> predicate == null || predicate.test(server.getServerIdObject()))
                .collect(Collectors.toList());
    }
}
