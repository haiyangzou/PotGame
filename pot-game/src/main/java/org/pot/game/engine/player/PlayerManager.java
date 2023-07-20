package org.pot.game.engine.player;

import lombok.Getter;
import org.pot.game.engine.gate.PlayerSession;
import org.pot.message.protocol.login.LoginDataS2S;

public class PlayerManager {
    @Getter
    private static final PlayerManager instance = new PlayerManager();

    public PlayerManager() {
    }

    public LoginDataS2S loginPlayer(PlayerSession playerSession, LoginDataS2S loginDataS2S) {
        return null;
    }
}
