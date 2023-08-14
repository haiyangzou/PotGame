package org.pot.game.gate;

import com.google.protobuf.Message;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.pot.common.concurrent.executor.ThreadUtil;
import org.pot.common.util.CollectionUtil;
import org.pot.core.net.connection.IConnection;
import org.pot.core.net.netty.FramePlayerMessage;
import org.pot.core.util.SignalLight;
import org.pot.game.engine.GameEngine;
import org.pot.message.protocol.login.LoginDataS2S;
import org.pot.message.protocol.login.LogoutReqC2S;
import org.pot.message.protocol.tunnel.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class GameConn extends Thread {
    private IConnection<FramePlayerMessage> connection;
    private final String RECV;
    private final String SEND;
    private final String FLUSH;
    private final Map<Long, PlayerSession> playerSessionMap = new ConcurrentHashMap<>();
    @Getter
    private volatile boolean shutdown = false;
    @Getter
    private volatile boolean closed = false;

    public GameConn(IConnection<FramePlayerMessage> connection) {
        String name = this.getClass().getSimpleName() + "-" + connection.getId();
        RECV = name + "-Recv";
        SEND = name + "-Send";
        FLUSH = name + "-Flush";
        this.connection = connection;
        this.connection.setImmediateFlush(false);
        this.setDaemon(false);
        this.setName(name);
        this.start();
    }

    boolean isClose() {
        return closed;
    }

    void close() {
        shutdown = true;
    }

    int getPlayerSessionCount() {
        return playerSessionMap.size();
    }

    @Override
    public void run() {
        long intervalMills = GameEngine.getInstance().getConfig().getGateTIckIntervalMillis();
        while (!shutdown) {
            try {
                ThreadUtil.run(false, intervalMills, this::tick);
            } catch (Throwable ex) {
                log.error("GameConn run occur an error conn={}", connection, ex);
            }
        }
        try {
            this.shutdown();
        } catch (Throwable ex) {
            log.error("GameConn shutdown occur an error conn={}", connection, ex);
        }
    }

    private void tick() {
        FramePlayerMessage framePlayerMessage;
        SignalLight.setOn(RECV);
        while ((framePlayerMessage = connection.pollRecvMessage()) != null) {
            final long uid = framePlayerMessage.getPlayerId();
            if (uid <= 0) {
                log.error("Invalid GameConn Request. PlayerId={} Request={}", uid, framePlayerMessage.getProtoName());
                continue;
            }
            PlayerSession playerSession = playerSessionMap.computeIfAbsent(uid, PlayerSession::new);
            try {
                if (isGhostCmd(framePlayerMessage)) {
                    processGhostCmd(playerSession, framePlayerMessage);
                } else if (framePlayerMessage.isProtoType(LogoutReqC2S.class)) {
                    processLogoutCmd(playerSession, framePlayerMessage);
                } else if (framePlayerMessage.isProtoType(LoginDataS2S.class)) {
                    processLoginCmd(playerSession, framePlayerMessage);
                } else {
                    playerSession.recv(framePlayerMessage);
                }
            } catch (Throwable ex) {
                log.error("GameConn run occur an error conn={}", connection, ex);
            }
        }
        SignalLight.setOff(RECV);
        SignalLight.setOn(SEND);
        List<Long> removes = null;
        for (PlayerSession playerSession : playerSessionMap.values()) {
            while ((framePlayerMessage = playerSession.pollSendMessage()) != null) {
                connection.sendMessage(framePlayerMessage);
            }
            if (playerSession.isIdle()) {
                log.info("idle player session:{}", playerSession);
                if (removes == null) removes = new ArrayList<>();
                removes.add(playerSession.getUid());
            }
        }
        if (CollectionUtil.isNotEmpty(removes)) {
            log.info("evict idle player session:{}", removes.size());
            removes.forEach(playerSessionMap::remove);
        }
        SignalLight.setOff(SEND);
        SignalLight.watch(FLUSH, connection::flush);
    }

    private boolean isGhostCmd(FramePlayerMessage framePlayerMessage) {
        Class<? extends Message> proType = framePlayerMessage.getProtoType();
        return proType == GhostKeepAliveCmd.class
                || proType == GhostEnterCmd.class
                || proType == GhostExitCmd.class
                || proType == GhostDestroyCmd.class
                || proType == GhostReconnectCmd.class;
    }

    private void processGhostCmd(PlayerSession playerSession, FramePlayerMessage message) {

    }

    private void processLogoutCmd(PlayerSession playerSession, FramePlayerMessage message) {
        playerSession.close();
    }

    private void processLoginCmd(PlayerSession playerSession, FramePlayerMessage message) {
        playerSession.initialize();
        GameEngine.getInstance().getAsyncExecutor().execute(new PlayerLoginTask(playerSession, message.getProto()));
    }

    private void shutdown() {
        connection.close();
        for (PlayerSession playerSession : playerSessionMap.values()) {
            playerSession.close();
        }
        playerSessionMap.clear();
    }
}
