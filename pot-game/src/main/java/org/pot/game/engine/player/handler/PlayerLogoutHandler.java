package org.pot.game.engine.player.handler;


import org.pot.common.concurrent.exception.IErrorCode;
import org.pot.game.engine.player.Player;
import org.pot.game.engine.player.PlayerRequestHandler;
import org.pot.message.protocol.login.LogoutReqC2S;

public final class PlayerLogoutHandler extends PlayerRequestHandler<LogoutReqC2S> {
    public IErrorCode handleRequest(Player player, LogoutReqC2S request) throws Exception {
        return player.onLogout(request);
    }
}
