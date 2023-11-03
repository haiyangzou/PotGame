package org.pot.game.engine.player.handler;

import org.pot.common.concurrent.exception.IErrorCode;
import org.pot.game.engine.error.GameErrorCode;
import org.pot.game.engine.player.Player;
import org.pot.game.engine.player.PlayerRequestHandler;
import org.pot.game.engine.scene.WorldPoint;
import org.pot.message.protocol.world.WorldMapAttackC2S;

public class AttackRequestHandler extends PlayerRequestHandler<WorldMapAttackC2S> {
    @Override
    protected IErrorCode handleRequest(Player player, WorldMapAttackC2S request) throws Exception {
        WorldPoint worldPoint = player.sceneComponent.getScene().getPoint(request.getTarget().getPid());
        if (worldPoint == null) {
            return GameErrorCode.UNLOCK;
        }


        return null;
    }
}
