package org.pot.game.engine.player;

import lombok.Getter;
import org.pot.common.concurrent.executor.AsyncRunner;
import org.pot.game.engine.gate.PlayerSession;

public class Player {
    @Getter
    private final long uid;
    private final AsyncRunner asyncRunner;
    private volatile PlayerSession playerSession;

    public Player(long uid) {
        this.uid = uid;
        this.playerSession = null;
        this.asyncRunner = new AsyncRunner(uid, Player.class);
    }

    public boolean isPlayerThread() {
        return Thread.currentThread() == PlayerManager.getInstance().getPlayerGroup(uid);
    }

    public void submit(Runnable runnable) {
        if (isPlayerThread()) {
            runnable.run();
        } else {
            asyncRunner.submit(runnable);
        }
    }

    public PlayerData save() {
        PlayerData temp = new PlayerData(uid);
        return temp;
    }

    public PlayerSession setPlayerSession(PlayerSession playerSession) {
        PlayerSession prev = this.playerSession;
        this.playerSession = playerSession;
        return prev;
    }
}
