package org.pot.game.engine;

import org.pot.common.communication.server.Server;
import org.pot.common.communication.server.ServerId;
import org.pot.common.communication.server.ServerType;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class GameServerInfo {
    static volatile Server server;
    static volatile ScheduledFuture<?> infoFuture;

    static void init() {
        requestGameServerInfo();
        infoFuture = GameEngine.getInstance().getAsyncExecutor().scheduleAtFixedRate(GameServerInfo::requestGameServerInfo, 1, 1, TimeUnit.MINUTES);
    }

    private static void requestGameServerInfo() {

    }

    public static long getOpenTimestamp() {
        return server.getOpenTimestamp();
    }

    public static int getServerId() {
        return server.getServerId();
    }

    public static boolean isGameServer() {
        return getServerIdObject().serverType == ServerType.GAME_SERVER;
    }

    public static ServerId getServerIdObject() {
        return server.getServerIdObject();
    }
}
