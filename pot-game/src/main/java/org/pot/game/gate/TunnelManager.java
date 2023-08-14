package org.pot.game.gate;

import org.pot.common.communication.server.Server;
import org.pot.common.communication.server.ServerId;
import org.pot.message.protocol.login.LoginDataS2S;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TunnelManager {
    public static final TunnelManager instance = new TunnelManager();
    private final Map<ServerId, Tunnel> tunnelMap = new ConcurrentHashMap<>();

    public TunnelManager() {

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
}
