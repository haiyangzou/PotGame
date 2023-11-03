package org.pot.game.engine.player;

import com.google.protobuf.Message;
import lombok.Getter;
import org.pot.common.concurrent.exception.CommonErrorCode;
import org.pot.common.concurrent.executor.AsyncRunner;
import org.pot.game.gate.PlayerSession;
import org.pot.message.protocol.login.LoginDataS2S;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class PlayerGroup extends Thread {
    private final Map<Long, Player> players = new ConcurrentHashMap<>();
    private final AsyncRunner asyncRunner;
    private volatile long nextPingTime = -1L;
    private volatile boolean shutdown = false;
    @Getter
    private boolean closed = false;
    private final int index;
    private final String PING;
    private final String TICK;
    private final String TICK_P;
    private final String ASYNC_RUNNER;

    public PlayerGroup(int index) {
        this.index = index;
        String name = this.getClass().getSimpleName() + "-" + this.index;
        PING = name + "-Ping";
        TICK = name + "-Tick";
        TICK_P = name + "-TickPlayer@";
        ASYNC_RUNNER = name + "-AsyncRunner";
        this.asyncRunner = new AsyncRunner((long) index, PlayerGroup.class);
        this.setDaemon(false);
        this.setName(name);
        this.start();
    }

    public Player buildPlayer(PlayerSession playerSession, PlayerData playerData) {
        return players.computeIfAbsent(playerData.getUid(), key -> new Player(playerSession, playerData));
    }

    boolean isPlayerRunning(long gameUid) {
        Player player = players.get(gameUid);
        return player != null && player.getState().get() == PlayerState.running;
    }

    boolean isPlayerExists(long gameUid) {
        return players.containsKey(gameUid);
    }

    LoginDataS2S loginPlayer(PlayerSession playerSession, LoginDataS2S loginDataS2S) {
        Player player = players.get(loginDataS2S.getGameUid());
        if (player != null && player.getState().get() == PlayerState.running) {
            //TODO 加载内存中的profile
        } else {
            //TODO 查询数据库
        }
        if (loginDataS2S.getIsNewRole()) {

        } else {

        }
        final LoginDataS2S finalLoginDataS2S = loginDataS2S;
        player = players.computeIfAbsent(loginDataS2S.getGameUid(), key -> new Player(playerSession, finalLoginDataS2S));
        PlayerSession oldPlayerSession = player.setPlayerSession(playerSession);
        if (oldPlayerSession != null && oldPlayerSession != playerSession) {
            oldPlayerSession.disconnect(CommonErrorCode.LOGIN_KICK);
        }
        return finalLoginDataS2S;
    }

    long getLoadingPlayerCount() {
        return players.values().stream().filter(player -> player.getState().get() == PlayerState.loading).count();
    }

    long getPlayerCount() {
        return players.size();
    }

    void foreachRunningPlayer(Consumer<Player> consumer) {
        for (Player player : players.values()) {
            if (player.getState().get() == PlayerState.running) {
                player.submit(() -> consumer.accept(player));
            }
        }
    }

    void close() {
        shutdown = true;
    }

    void sendMessage(long gameUid, Message message) {
        Player player = players.get(gameUid);
        if (player != null) {
            player.sendMessage(message);
        }
    }
}
