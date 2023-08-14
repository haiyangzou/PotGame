package org.pot.game.engine.player;

import org.pot.common.concurrent.executor.AsyncRunner;
import org.pot.game.gate.PlayerSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerGroup extends Thread {
    private final Map<Long, Player> players = new ConcurrentHashMap<>();
    private final AsyncRunner asyncRunner;
    private volatile long nextPingTime = -1L;
    private volatile boolean shutdown = false;
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
}
