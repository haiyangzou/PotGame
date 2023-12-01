package org.pot.game.engine.march.alarm;

import org.pot.game.engine.GameServerInfo;
import org.pot.game.engine.enums.MarchType;
import org.pot.game.engine.march.March;
import org.pot.game.engine.march.MarchManager;
import org.pot.game.engine.player.Player;
import org.pot.game.engine.player.PlayerManager;
import org.pot.game.engine.point.PointExtraData;
import org.pot.message.protocol.world.PlayerOwnerMarchS2C;

import java.util.List;

public class MarchPlayerNotifier {
    public void notifyMarchPlayer(MarchManager marchManager, March cause) {
        notifyMarchOwner(marchManager, cause.getOwnerId());
        notifyMarchReinForcingCity(marchManager, cause);
    }

    private void notifyMarchReinForcingCity(MarchManager marchManager, March march) {
        if (march.getType() != MarchType.REINFORCEMENT) {
            return;
        }
        PointExtraData pointExtraData = march.getTargetPointExtraData();
        notifyMarchReinForcingCity(marchManager, pointExtraData);
    }

    private void notifyMarchReinForcingCity(MarchManager marchManager, PointExtraData pointExtraData) {

    }

    private void notifyMarchOwner(MarchManager marchManager, long ownerId) {
        if (ownerId <= 0) return;
        Player owner = PlayerManager.fetchPlayer(ownerId);
        if (owner == null) return;
        PlayerOwnerMarchS2C.Builder builder = PlayerOwnerMarchS2C.newBuilder();
        builder.setSid(GameServerInfo.getServerId());
        List<March> marches = marchManager.getPlayerMarches(ownerId);
        for (March march : marches) {
            builder.addMarchs(march.buildWorldMarchInfo(ownerId));
        }
        owner.sendMessage(builder.build());
        //推送总兵量变化
//        owner.submit(owner.armyAgent::sendTotalArmys);
    }
}
