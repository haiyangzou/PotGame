package org.pot.gateway.remote;

import lombok.extern.slf4j.Slf4j;
import org.pot.common.Constants;
import org.pot.common.communication.server.DefinedServerSupplier;
import org.pot.common.communication.server.GameServer;
import org.pot.common.concurrent.exception.ServiceException;
import org.pot.common.concurrent.executor.ExecutorUtil;
import org.pot.common.concurrent.executor.ScheduledExcutor;
import org.pot.common.concurrent.executor.StandardExecutor;
import org.pot.common.concurrent.executor.ThreadUtil;
import org.pot.common.util.LogUtil;
import org.pot.common.util.MathUtil;
import org.pot.gateway.engine.GatewayEngine;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
public class RemoteServerManager {
    private final StandardExecutor remoteServerConnector;
    private final ScheduledExcutor remoteServerRunner;
    private final ScheduledFuture<?> requestAndCorrectFuture;
    private final Map<Integer, RemoteServer> remoteServers = new ConcurrentHashMap<>();
    private final Map<Integer, GameServer> definedGameServers = new ConcurrentHashMap<>();

    public RemoteServerManager() {
        requestDefinedGameServers();
        int serverSize = MathUtil.max(definedGameServers.size(), ThreadUtil.AVAILABLE_PROCESSORS);
        int executorSize = MathUtil.divideAndCeil(serverSize, 10);
        remoteServerConnector = new StandardExecutor(executorSize, "RemoteServerCon");
        int schedulerSIze = MathUtil.divideAndCeil(serverSize, 2);
        remoteServerRunner = ScheduledExcutor.newScheduledExecutor(schedulerSIze, "RemoteServerRun");
        requestAndCorrectFuture = remoteServerRunner.scheduleAtFixedRate(this::requestAndCorrect, 1, 60, TimeUnit.SECONDS);

    }

    ScheduledFuture<?> runRemoteServer(RemoteServer remoteServer) {
        long intervalMillis = GatewayEngine.getInstance().getConfig().getGatewayTickIntervalMillis();
        return remoteServerRunner.scheduleAtFixedRate(remoteServer, 0, intervalMillis, TimeUnit.MILLISECONDS);
    }

    private void requestAndCorrect() {
        try {
            requestDefinedGameServers();
            correctRemoteServers();
        } catch (Throwable t) {
            log.error("requestAndCorrect error", t);
        }
    }

    private void correctRemoteServers() {
        Iterator<RemoteServer> iterator = remoteServers.values().iterator();
        while (iterator.hasNext()) {
            RemoteServer remoteServer = iterator.next();
            if (remoteServer.isClosed()) {
                iterator.remove();
            } else if (!definedGameServers.containsKey(remoteServer.getServerId())) {
                remoteServer.close();
            }
        }
        for (GameServer server : definedGameServers.values()) {
            Integer serverId = server.getServerId();
            RemoteServer remoteServer = remoteServers.computeIfAbsent(serverId,
                    k -> new RemoteServer(serverId, server.getHost(), server.getPort()));
            remoteServer.setHost(server.getHost());
            remoteServer.setPort(server.getPort());
        }
    }

    private void requestDefinedGameServers() {
        String url = GatewayEngine.getInstance().getConfig().getGlobalServerConfig().getGameServerListUrl();
        try {
            List<GameServer> servers = DefinedServerSupplier.getGameServerList(url);
            Set<Integer> serverIds = new HashSet<>(servers.size());
            for (GameServer server : servers) {
                Integer serverId = server.getServerId();
                definedGameServers.put(serverId, server);
                serverIds.add(serverId);
            }
            definedGameServers.keySet().retainAll(serverIds);
            log.info("request game server list");
        } catch (Exception e) {
            throw new ServiceException(LogUtil.format("request game server list error. url={}", url));
        }
    }

    RemoteServer getRemoteServer(Integer serverId) {
        GameServer server = definedGameServers.get(serverId);
        if (server == null) {
            return null;
        }
        return remoteServers.computeIfAbsent(serverId,
                k -> new RemoteServer(server.getServerId(), server.getHost(), server.getPort()));
    }

    public void close() {
        requestAndCorrectFuture.cancel(false);
        ThreadUtil.await(Constants.AWAIT_MS, TimeUnit.MILLISECONDS, requestAndCorrectFuture::isDone);
        ThreadUtil.await(Constants.AWAIT_MS, TimeUnit.MILLISECONDS, () -> {
            remoteServers.values().forEach(RemoteServer::close);
            return remoteServers.values().stream().allMatch(RemoteServer::isClosed);
        });
        remoteServers.values().removeIf(RemoteServer::isClosed);
        shutdownExecutor(remoteServerConnector.getName(), remoteServerConnector);
        shutdownExecutor(remoteServerRunner.getName(), remoteServerRunner);
    }

    private void shutdownExecutor(String name, ExecutorService executorService) {
        if (!ExecutorUtil.shutdownExecutor(executorService)) {
            executorService.shutdownNow().forEach(runnable -> log.warn("{} shutdown not completed task{}", name, runnable));
        }
    }
}
