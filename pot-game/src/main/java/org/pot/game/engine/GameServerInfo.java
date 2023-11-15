package org.pot.game.engine;

import lombok.extern.slf4j.Slf4j;
import org.pot.common.Constants;
import org.pot.common.communication.server.DefinedServerSupplier;
import org.pot.common.communication.server.Server;
import org.pot.common.communication.server.ServerId;
import org.pot.common.communication.server.ServerType;
import org.pot.common.concurrent.exception.ServiceException;
import org.pot.common.concurrent.executor.ThreadUtil;

import java.util.Objects;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
public class GameServerInfo {
    static volatile Server server;
    static volatile ScheduledFuture<?> infoFuture;

    static void init() throws Exception {
        requestGameServerInfo();
        infoFuture = GameEngine.getInstance().getAsyncExecutor().scheduleAtFixedRate(GameServerInfo::schedule, 1, 1, TimeUnit.MINUTES);
    }

    public static void schedule() {
        try {
            requestGameServerInfo();
        } catch (Throwable e) {
            log.error("Schedule GameServerInfo Error", e);
        }
    }

    public static void shutdown() {
        ThreadUtil.cancel(Constants.AWAIT_MS, TimeUnit.MILLISECONDS, infoFuture);
    }

    private static void requestGameServerInfo() throws Exception {
        GameEngineConfig config = GameEngine.getInstance().getConfig();
        ServerType serverType = Objects.requireNonNull(config.getServerType());
        int port = GameEngine.getInstance().getConfig().getPort();
        int httpPort = GameEngine.getInstance().getConfig().getJettyConfig().getPort();
        int rpcPort = GameEngine.getInstance().getConfig().getRemoteServerConfig().getPort();
        if (serverType == ServerType.GAME_SERVER) {
            String url = GameEngine.getInstance().getConfig().getGlobalServerConfig().getGameServerInfoUrl();
            server = DefinedServerSupplier.getGameServerInfo(url, port, httpPort, rpcPort).toServer();
        } else if (serverType == ServerType.SLAVE_SERVER) {
            String url = GameEngine.getInstance().getConfig().getGlobalServerConfig().getServerInfoUrl();
            server = DefinedServerSupplier.getServerInfo(url, serverType, port, httpPort, rpcPort);
        } else {
            throw new IllegalArgumentException("Error Game Server Type" + serverType);
        }
        if (server == null) {
            throw new ServiceException("Request Game Server Info not Found" + serverType);
        }
        log.info("Request Game Server Info Success.ServerType={}", serverType);
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
