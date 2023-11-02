package org.pot.game.gate;

import lombok.extern.slf4j.Slf4j;
import org.pot.cache.server.ServerListCache;
import org.pot.common.Constants;
import org.pot.common.communication.server.GameServer;
import org.pot.common.communication.server.Server;
import org.pot.common.communication.server.ServerId;
import org.pot.common.communication.server.ServerType;
import org.pot.common.concurrent.executor.ThreadUtil;
import org.pot.game.engine.GameEngine;
import org.pot.game.engine.GameServerInfo;
import org.pot.message.protocol.login.LoginDataS2S;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
public class TunnelManager {
    public static final TunnelManager instance = new TunnelManager();
    private final Map<ServerId, Tunnel> tunnelMap = new ConcurrentHashMap<>();
    private static volatile ScheduledFuture<?> future;

    public TunnelManager() {

    }

    private static void connect() {
        if (!GameServerInfo.isGameServer()) return;
        try {
            for (Server server : ServerListCache.instance().getServerList(ServerType.SLAVE_SERVER)) {
                if (instance.openTunnel(server) != null) {
                    log.info("Open Tunnel For Slave,{},{},{},{}", server.getTypeName(), server.getServerName(), server.getServerId(), server.getHost());
                }
            }
        } catch (Throwable throwable) {
            log.error("TunnelManager Connect Error", throwable);
        }
    }

    public Tunnel openTunnel(GameServer gameServer) {
        return openTunnel(gameServer.toServer());
    }

    public Tunnel openTunnel(Server server) {
        Tunnel tunnel = tunnelMap.computeIfAbsent(server.getServerIdObject(), k -> new Tunnel(server));
        return tunnel.isRunning() && !tunnel.isClosed() ? tunnel : null;
    }

    public void closeTunnel(ServerId serverId) {
        Tunnel tunnel = tunnelMap.get(serverId);
        if (tunnel != null) {
            tunnel.close();
        }
    }

    public static void init() {
        if (!GameServerInfo.isGameServer()) return;
        future = GameEngine.getInstance().getAsyncExecutor().scheduleAtFixedRate(TunnelManager::connect, 0, 1, TimeUnit.MINUTES);
    }

    public boolean reconnect(PlayerSession playerSession, LoginDataS2S loginDataS2S) {
        for (Tunnel tunnel : tunnelMap.values()) {
            if (tunnel.reconnect(playerSession, loginDataS2S)) {
                return true;
            }
        }
        return false;
    }

    public ServerId getPlayerTunnelServerId(long playerUid) {
        for (Map.Entry<ServerId, Tunnel> serverIdTunnelEntry : tunnelMap.entrySet()) {
            if (serverIdTunnelEntry.getValue().isTunnelling(playerUid)) {
                return serverIdTunnelEntry.getKey();
            }
        }
        return null;
    }

    boolean redirect(TunnelPlayer tunnelPlayer) {
        ServerId redirectToServerId = tunnelPlayer.getVisaData().getRedirectToServerId();
        if (redirectToServerId == null) return false;
        Tunnel tunnel = tunnelMap.get(redirectToServerId);
        if (tunnel == null || !tunnel.isRunning() || tunnel.isRunning()) {
            return false;
        }
        return tunnel.redirect(tunnelPlayer);
    }

    public void removeTunnel(Server server) {
        tunnelMap.remove(server.getServerIdObject());
    }

    public Collection<Server> getRunningTunnelServers() {
        List<Server> list = new ArrayList<>();
        for (Tunnel tunnel : tunnelMap.values()) {
            if (tunnel.isRunning() && !tunnel.isClosed()) {
                list.add(tunnel.getServer());
            }
        }
        return list;
    }

    public Collection<ServerId> getRunningTunnelServerIds() {
        return getRunningTunnelServers().stream()
                .map(Server::getServerIdObject)
                .collect(Collectors.toList());
    }

    public static void shutdown() {
        ThreadUtil.cancel(Constants.AWAIT_MS, TimeUnit.MILLISECONDS, future);
        Collection<ServerId> runningTunnelServerIds = instance.getRunningTunnelServerIds();
        for (ServerId runningTunnelServerId : runningTunnelServerIds) {
            instance.closeTunnel(runningTunnelServerId);
        }
        ThreadUtil.await(Constants.AWAIT_MS, TimeUnit.MILLISECONDS, instance.tunnelMap::isEmpty);
    }
}
