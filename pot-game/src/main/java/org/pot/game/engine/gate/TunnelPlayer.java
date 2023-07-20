package org.pot.game.engine.gate;

import org.pot.common.concurrent.exception.IErrorCode;
import org.pot.common.util.RandomUtil;
import org.pot.game.engine.player.Player;
import org.pot.game.engine.player.PlayerData;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class TunnelPlayer {
    private final long playerUid;
    private volatile PlayerData playerData;
    private volatile TunnelVisaData visaData;
    private volatile PlayerSession playerSession;
    private volatile long nextKeepAlive = System.currentTimeMillis();
    private volatile TunnelPlayerState state = TunnelPlayerState.PREPARE;

    public TunnelPlayer(long playerUid, TunnelVisaData visaData, PlayerSession playerSession) {
        this.playerUid = playerUid;
        this.playerSession = playerSession;
        this.visaData = visaData;
    }

    public TunnelVisaData getVisaData() {
        return visaData;
    }

    public void setState(TunnelPlayerState state) {
        this.state = Objects.requireNonNull(state);
    }

    public TunnelPlayerState getState() {
        return state;
    }

    public PlayerSession getPlayerSession() {
        return playerSession;
    }

    public PlayerSession setPlayerSession(PlayerSession playerSession) {
        PlayerSession prev = this.playerSession;
        this.playerSession = playerSession;
        return prev;
    }

    public long getPlayerUid() {
        return playerUid;
    }

    public PlayerData getPlayerData() {
        return playerData;
    }

    public void disconnect(IErrorCode errorCode) {
        PlayerSession atomic = this.playerSession;
        if (atomic != null) atomic.disconnect(errorCode);
    }

    public boolean keepAlive(long currentTimeMills) {
        if (nextKeepAlive > currentTimeMills) return false;
        nextKeepAlive = currentTimeMills
                + TimeUnit.SECONDS.toMillis(1)
                + RandomUtil.randomLong(TimeUnit.SECONDS.toMillis(1));
        return true;
    }
}
