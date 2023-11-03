package org.pot.game.engine.player.handler;

import org.pot.common.concurrent.exception.IErrorCode;
import org.pot.game.engine.error.GameErrorCode;
import org.pot.game.engine.march.March;
import org.pot.game.engine.march.MarchManager;
import org.pot.game.engine.march.MarchUtil;
import org.pot.game.engine.march.bean.MarchTroopBean;
import org.pot.game.engine.march.impl.WorldHuntMarch;
import org.pot.game.engine.player.Player;
import org.pot.game.engine.player.PlayerRequestHandler;
import org.pot.game.engine.scene.WorldPoint;
import org.pot.game.engine.world.WorldManager;
import org.pot.game.engine.world.WorldPlayerRequest;
import org.pot.message.protocol.world.WorldMapAttackC2S;

public class AttackRequestHandler extends PlayerRequestHandler<WorldMapAttackC2S> {
    @Override
    protected IErrorCode handleRequest(Player player, WorldMapAttackC2S request) throws Exception {
        WorldPoint worldPoint = player.sceneComponent.getScene().getPoint(request.getTarget().getPid());
        if (worldPoint == null) {
            return GameErrorCode.UNLOCK;
        }
        int sourcePoint = 0;
        int targetPoint = request.getTarget().getPid();
        MarchTroopBean marchTroopBean;
        marchTroopBean = MarchUtil.transformTroopBean(player, request.getHeroesList(), request.getSoldiersList());
        MarchManager marchManager = player.sceneComponent.getScene().getMarchManager();
        int expendPower = 0;
        WorldManager.getInstance().submit(new WorldPlayerRequest(player.getUid(), request) {
            @Override
            protected void rollbackOnError(IErrorCode error) {
                super.rollbackOnError(error);
            }

            @Override
            public IErrorCode handle() {
                March march = new WorldHuntMarch(marchManager, player.getUid(), sourcePoint, targetPoint, marchTroopBean, expendPower);
                marchManager.addMarch(march);
                return null;
            }
        });
        return null;
    }
}
