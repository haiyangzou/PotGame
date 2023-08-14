package org.pot.game.engine.player;

import lombok.Getter;
import org.pot.common.concurrent.executor.AsyncRunner;
import org.pot.game.gate.PlayerSession;
import org.pot.message.protocol.login.LoginDataS2S;

public class Player {
    @Getter
    private final long uid;
    private final AsyncRunner asyncRunner;
    private volatile PlayerSession playerSession;
    private volatile PlayerData playerData;
    private volatile LoginDataS2S loginDataS2S;

    public Player(long uid) {
        this.uid = uid;
        this.playerSession = null;
        this.asyncRunner = new AsyncRunner(uid, Player.class);
    }

    public Player(PlayerSession playerSession, PlayerData playerData) {
        this.uid = playerData.getUid();
        this.playerSession = playerSession;
        this.asyncRunner = new AsyncRunner(uid, Player.class);
        this.playerData = playerData;
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
