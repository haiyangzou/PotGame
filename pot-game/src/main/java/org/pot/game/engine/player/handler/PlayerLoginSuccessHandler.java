package org.pot.game.engine.player.handler;

import org.pot.common.concurrent.exception.IErrorCode;
import org.pot.game.engine.player.Player;
import org.pot.game.engine.player.PlayerRequestHandler;
import org.pot.message.protocol.login.LoginSuccessC2S;

public class PlayerLoginSuccessHandler extends PlayerRequestHandler<LoginSuccessC2S> {
    public IErrorCode handleRequest(Player player, LoginSuccessC2S request) throws Exception {
        return player.onLoginSuccess(request);
    }
}
