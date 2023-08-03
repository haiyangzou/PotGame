package org.pot.game.engine.gate;

import org.pot.common.communication.server.Server;
import org.pot.common.communication.server.ServerId;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TunnelManager {
    public static final TunnelManager instance = new TunnelManager();
    private final Map<ServerId, Tunnel> tunnelMap = new ConcurrentHashMap<>();

    public TunnelManager() {

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
