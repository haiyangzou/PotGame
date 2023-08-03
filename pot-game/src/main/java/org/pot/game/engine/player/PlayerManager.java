package org.pot.game.engine.player;

import lombok.Getter;
import org.pot.game.engine.gate.PlayerSession;
import org.pot.message.protocol.login.LoginDataS2S;

public class PlayerManager {
    @Getter
    private static final PlayerManager instance = new PlayerManager();

    public PlayerManager() {
    }

    public static Player fetchPlayer(long gameUid) {
        return getInstance().getPlayer(gameUid);
    }

    public Player getPlayer(long gameUid) {
        return null;
    }

    public PlayerGroup getPlayerGroup(long gameUid) {
        return null;
    }

    public LoginDataS2S loginPlayer(PlayerSession playerSession, LoginDataS2S loginDataS2S) {
        return null;
    }
}
