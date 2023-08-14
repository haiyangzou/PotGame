package org.pot.game.engine;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class GameServerInfo {
    static volatile GameServer gameServer;
    static volatile ScheduledFuture<?> infoFuture;

    static {
        requestGameServerInfo();
        if (gameServer == null) {

        }
        infoFuture = GameEngine.getInstance().getAsyncExecutor().scheduleAtFixedRate(GameServerInfo::requestGameServerInfo, 1, 1, TimeUnit.MINUTES);
    }

    private static void requestGameServerInfo() {

    }

    public static long getOpenTimestamp() {
        return gameServer.getOpenTimestamp();
    }

    public static int getServerId() {
        return gameServer.getServerId();
    }
}
