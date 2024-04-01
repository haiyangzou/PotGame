package org.pot.game.engine.player.handler;

import lombok.extern.slf4j.Slf4j;
import org.pot.common.concurrent.exception.IErrorCode;
import org.pot.game.engine.player.Player;
import org.pot.game.engine.player.PlayerRequestHandler;
import org.pot.message.protocol.Pong;

@Slf4j
public class PlayerPongHandler extends PlayerRequestHandler<Pong> {
    @Override
    protected IErrorCode handleRequest(Player player, Pong request) throws Exception {
        long cost = System.currentTimeMillis() - request.getPing().getTime();
        if (cost > 10000L)
            log.info("player net connection lag. uid={}, loopback={}ms", player.getUid(), cost);
        return null;
    }
}
