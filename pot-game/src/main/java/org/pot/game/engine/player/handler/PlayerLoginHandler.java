package org.pot.game.engine.player.handler;

import org.pot.common.concurrent.exception.IErrorCode;
import org.pot.game.engine.player.Player;
import org.pot.game.engine.player.PlayerRequestHandler;
import org.pot.message.protocol.login.LoginDataS2S;

public final class PlayerLoginHandler extends PlayerRequestHandler<LoginDataS2S> {
    public IErrorCode handleRequest(Player player, LoginDataS2S request) throws Exception {
        return player.onLogin(request);
    }
}