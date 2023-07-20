package org.pot.game.engine.gate;

import com.mongodb.connection.ServerId;
import org.pot.common.communication.server.Server;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TunnelManager {
    public static final TunnelManager instance = new TunnelManager();
    private final Map<ServerId, Tunnel> tunnelMap = new ConcurrentHashMap<>();

    public TunnelManager() {

    }

    public void removeTunnel(Server server) {
        tunnelMap.remove(server.getServerIdObject());

    }
}
