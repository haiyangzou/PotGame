package org.pot.game.engine.player;

import com.google.protobuf.Message;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.pot.common.concurrent.exception.CommonErrorCode;
import org.pot.common.concurrent.executor.AsyncRunner;
import org.pot.common.concurrent.executor.ThreadUtil;
import org.pot.core.util.SignalLight;
import org.pot.dal.DbSupport;
import org.pot.dal.dao.SqlSession;
import org.pot.game.engine.GameEngine;
import org.pot.game.gate.PlayerSession;
import org.pot.game.persistence.GameDb;
import org.pot.game.persistence.entity.PlayerProfileEntity;
import org.pot.game.persistence.mapper.PlayerProfileEntityMapper;
import org.pot.message.protocol.Ping;
import org.pot.message.protocol.login.LoginDataS2S;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Slf4j
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

    private void ping() {
        if (this.nextPingTime > System.currentTimeMillis())
            return;
        this.nextPingTime = (System.currentTimeMillis() / 1000L + 1L) * 1000L;
        for (Player player : this.players.values())
            if(player.isOnline()){
                player.sendMessage(Ping.newBuilder().setTime(System.currentTimeMillis()).build());
            }
    }

    @Override
    public void run() {
        while (!this.shutdown) {
            try {
                long intervalMillis = (GameEngine.getInstance().getConfig()).getPlayerTickIntervalMillis();
                ThreadUtil.run(false, intervalMillis, this::execute);
            } catch (Throwable ex) {
                log.error("PlayerGroup run occur an error.", ex);
            }
        }
        shutdown();
        this.closed = true;
    }

    private void shutdown() {
        Iterator<Player> iterator = this.players.values().iterator();
        while (iterator.hasNext()) {
            Player player = iterator.next();
            try {
                player.save();
            } catch (Exception ex) {
                log.error("Player Save Occur An Error. uid={}", player.getUid(), ex);
            }
            iterator.remove();
        }
    }

    private void execute() {
        SignalLight.setOn(this.PING);
        ping();
        SignalLight.setOff(this.PING);
        SignalLight.setOn(this.TICK);
        List<Long> removes = new ArrayList<>();
        for (Player player : this.players.values()) {
            String flag = this.TICK_P + player.getUid();
            SignalLight.setOn(flag);
            try {
                player.tick();
            } catch (Exception ex) {
                log.error("Player Tick Occur An Error. uid={}", player.getUid(), ex);
            }
            PlayerState playerState = player.getState().get();
            if (playerState == PlayerState.loadError) {
                removes.add(player.getUid());
                player.disconnect(CommonErrorCode.INVALID_STATE);
                log.error("Invalid Player State. uid={}, state={}", player.getUid(), playerState);
            } else if (playerState == PlayerState.registerError) {
                removes.add(player.getUid());
                player.disconnect(CommonErrorCode.INVALID_STATE);
                log.error("Invalid Player State. uid={}, state={}", player.getUid(), playerState);
            }
            SignalLight.setOff(flag);
        }
        removes.forEach(this.players::remove);
        SignalLight.setOff(this.TICK);
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
        PlayerProfileEntity profile;
        Player player = players.get(loginDataS2S.getGameUid());
        if (player != null && player.getState().get() == PlayerState.running) {
            profile = player.getProfile();
        } else {
            SqlSession sqlSession = GameDb.local().getSqlSession(loginDataS2S.getGameUid());
            profile = sqlSession.getMapper(PlayerProfileEntityMapper.class).select(loginDataS2S.getGameUid());
        }
        if (loginDataS2S.getIsNewRole()) {
            if (profile != null) {
                log.error("Player Login Create Role Error! account={}, uid={}", loginDataS2S.getAccountUid(), loginDataS2S.getGameUid());
                playerSession.disconnect(CommonErrorCode.INVALID_LOGIN_INFO);
                return loginDataS2S;
            }
        } else if (profile == null) {
            LoginDataS2S.Builder builder = loginDataS2S.toBuilder();
            builder.setIsNewRole(true);
            loginDataS2S = builder.build();
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
