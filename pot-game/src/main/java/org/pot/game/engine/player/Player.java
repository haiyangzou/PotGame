package org.pot.game.engine.player;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.pot.common.concurrent.executor.AsyncRunner;
import org.pot.game.engine.player.module.ghost.PlayerGhostAgent;
import org.pot.game.gate.PlayerSession;
import org.pot.message.protocol.login.LoginDataS2S;

import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public class Player {
    @Getter
    private final long uid;
    private final AsyncRunner asyncRunner;
    private volatile PlayerSession playerSession;
    private volatile PlayerData playerData;
    private volatile LoginDataS2S loginDataS2S;
    public final PlayerGhostAgent ghostAgent = new PlayerGhostAgent(this);
    @Getter
    private final AtomicReference<PlayerState> state = new AtomicReference<>(PlayerState.initial);
    /**
     * 卸载检测的时候跳过加载时间超时检测一次
     * 用于临时加载一次player之后需要尽快卸载使用
     * 检测之后立即设置为false
     */
    private boolean skipLoadTimeCheckOnce = false;

    public int getServerId() {
        return 1;
    }

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

    public Player(PlayerSession playerSession, LoginDataS2S loginDataS2S) {
        this.uid = loginDataS2S.getGameUid();
        this.playerSession = playerSession;
        this.asyncRunner = new AsyncRunner(uid, Player.class);
        this.playerData = new PlayerData(this.uid);
        this.loginDataS2S = loginDataS2S;
        if (loginDataS2S.getIsNewRole()) {
            this.state.updateAndGet(s -> PlayerState.registering);
        } else {
            this.playerData.asyncLoad(data -> log.info(""), data -> log.info(""));
            this.state.updateAndGet(s -> PlayerState.loading);
        }
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

    public void setSkipLoadTimeCheckOnce() {
        this.skipLoadTimeCheckOnce = true;
    }

}
