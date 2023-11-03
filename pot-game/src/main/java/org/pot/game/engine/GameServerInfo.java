package org.pot.game.engine;

import org.pot.common.Constants;
import org.pot.common.communication.server.Server;
import org.pot.common.communication.server.ServerId;
import org.pot.common.communication.server.ServerType;
import org.pot.common.concurrent.executor.ThreadUtil;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class GameServerInfo {
    static volatile Server server;
    static volatile ScheduledFuture<?> infoFuture;

    static void init() {
        requestGameServerInfo();
        infoFuture = GameEngine.getInstance().getAsyncExecutor().scheduleAtFixedRate(GameServerInfo::requestGameServerInfo, 1, 1, TimeUnit.MINUTES);
    }

    public static void shutdown() {
        ThreadUtil.cancel(Constants.AWAIT_MS, TimeUnit.MILLISECONDS, infoFuture);
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

    public static boolean isSlaveServer() {
        return getServerIdObject().serverType == ServerType.SLAVE_SERVER;
    }


    public static ServerId getServerIdObject() {
        return server.getServerIdObject();
    }
}
