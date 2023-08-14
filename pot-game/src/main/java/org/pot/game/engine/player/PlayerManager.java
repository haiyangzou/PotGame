package org.pot.game.engine.player;

import lombok.Getter;
import org.pot.common.id.UniqueIdUtil;
import org.pot.game.gate.PlayerSession;
import org.pot.message.protocol.login.LoginDataS2S;

public class PlayerManager {
    @Getter
    private static final PlayerManager instance = new PlayerManager();
    private PlayerGroup[] playerGroups;

    public PlayerManager() {
    }

    public static Player fetchPlayer(long gameUid) {
        return getInstance().getPlayer(gameUid);
    }

    public Player getPlayer(long gameUid) {
        return null;
    }

    public PlayerGroup getPlayerGroup(int index) {
        return playerGroups[index];
    }

    public PlayerGroup getPlayerGroup(long gameUid) {
        return getPlayerGroup(UniqueIdUtil.index(gameUid, playerGroups.length));
    }

    public LoginDataS2S loginPlayer(PlayerSession playerSession, LoginDataS2S loginDataS2S) {
        return getPlayerGroup(loginDataS2S.getGameUid()).loginPlayer(playerSession, loginDataS2S);
    }

    public Player buildPlayer(PlayerSession playerSession, PlayerData playerData) {
        return getPlayerGroup(playerData.getUid()).buildPlayer(playerSession, playerData);
    }

    public boolean isPlayerRunning(long gameUid) {
        return getPlayerGroup(gameUid).isPlayerRunning(gameUid);
    }

    public boolean isPlayerExists(long gameUid) {
        return getPlayerGroup(gameUid).isPlayerExists(gameUid);
    }
}
