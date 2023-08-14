package org.pot.game.gate;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.pot.common.concurrent.exception.IErrorCode;
import org.pot.common.util.RunSignal;
import org.pot.game.engine.WorldManager;
import org.pot.game.engine.player.Player;
import org.pot.game.engine.player.PlayerData;
import org.pot.game.engine.player.PlayerManager;

import java.util.Objects;

@Slf4j
@Getter
public class TunnelPlayer {
    private final long playerUid;
    private final RunSignal runSignal = new RunSignal(1000);
    private volatile PlayerData playerData;
    private volatile TunnelVisaData visaData;
    private volatile PlayerSession playerSession;
    private volatile TunnelPlayerState state = TunnelPlayerState.PREPARE;

    public TunnelPlayer(long playerUid, TunnelVisaData visaData) {
        this.playerUid = playerUid;
        this.visaData = visaData;
    }

    public TunnelVisaData getVisaData() {
        return visaData;
    }

    void setVisaData(TunnelVisaData visaData) {
        this.visaData = visaData;
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

    void setPlayerData(PlayerData playerData) {
        this.playerData = playerData;
        this.playerData.asyncUpdate(
                () -> log.info("Tunnel Player Dta Save Success. uid={}", playerUid),
                () -> log.info("Tunnel Player Dta Save Fail. uid={}", playerUid)
        );
    }

    void join() {
        //TODO 从地图上移除玩家，记录位置，由于基础玩家可能导致行军召回,delay tick,防止行军合批导致尾移除
        Player player = PlayerManager.fetchPlayer(playerUid);
        player.submit(() -> {
            WorldManager.getInstance().submit(() -> {
                setPlayerData(player.save());
                setPlayerSession(player.setPlayerSession(null));
                setState(TunnelPlayerState.READY);
                log.info("Tunnel Player join Operation Finished,PlayerUid={}", playerUid);
            });
        });
    }

    void recover() {
        Player player = PlayerManager.getInstance().buildPlayer(playerSession, playerData);
        player.submit(() -> {
            WorldManager.getInstance().submit(() -> {
                //迁城，随机或者指定
                player.submit(() -> {
                    //需要在世界线程执行后，在玩家线程执行的，在这里

                });
            });
        });
    }

    public boolean isOnline() {
        PlayerSession atomic = this.playerSession;
        return atomic != null && atomic.isOnline();
    }
}
