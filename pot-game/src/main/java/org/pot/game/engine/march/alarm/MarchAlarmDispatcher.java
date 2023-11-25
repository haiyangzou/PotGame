package org.pot.game.engine.march.alarm;

import org.apache.commons.lang3.StringUtils;
import org.pot.game.engine.enums.MarchState;
import org.pot.game.engine.march.March;
import org.pot.game.engine.march.MarchManager;
import org.pot.game.engine.player.Player;
import org.pot.game.engine.player.PlayerManager;
import org.pot.game.engine.point.PointCityData;
import org.pot.game.engine.point.PointExtraData;
import org.pot.game.engine.point.PointUnionBuildingData;
import org.pot.game.engine.scene.AbstractScene;

import java.util.List;

public class MarchAlarmDispatcher {
    private enum Option {
        Alert {
            @Override
            void city(MarchAlarmReceiver marchAlarmReceiver, March march) {
                marchAlarmReceiver.alertCity(march);
            }

            @Override
            void march(MarchAlarmReceiver marchAlarmReceiver, March selfMarch, March march) {
                marchAlarmReceiver.alertMarch(selfMarch, march);
            }
        },
        Relax {
            @Override
            void city(MarchAlarmReceiver marchAlarmReceiver, March march) {
                marchAlarmReceiver.relaxCity(march);
            }

            @Override
            void march(MarchAlarmReceiver marchAlarmReceiver, March selfMarch, March march) {
                marchAlarmReceiver.relaxMarch(selfMarch, march);
            }
        };

        abstract void city(MarchAlarmReceiver marchAlarmReceiver, March march);

        abstract void march(MarchAlarmReceiver marchAlarmReceiver, March selfMarch, March march);

    }

    private void operate(March march, Option opt) {
        MarchManager marchManager = march.getManager();
        if (marchManager == null) {
            return;
        }
        AbstractScene scene = march.getScene();
        PointExtraData pointExtraData = scene.getPointManager().getPointExtraData(march.getTargetPoint());
        if (pointExtraData == null) {
            return;
        }
        if (pointExtraData instanceof PointUnionBuildingData) {

        }
        if (pointExtraData instanceof PointCityData) {
            MarchAlarmReceiver marchAlarmReceiver = getMarchAlarmReceiver(((PointCityData) pointExtraData).getPlayerId());
            if (marchAlarmReceiver != null) {
                opt.city(marchAlarmReceiver, march);
            }
        }
        List<String> pointMarchIdList = pointExtraData.getMarchIdList();
        for (String pointMarchId : pointMarchIdList) {
            March pointMarch = marchManager.getMarch(pointMarchId);
            if (pointMarch == null) {
                continue;
            }
            if (march.getOwnerId() > 0 && march.getOwnerId() == pointMarch.getOwnerId()) {
                continue;
            }
            if (StringUtils.equals(march.getId(), pointMarch.getId())) {
                continue;
            }
            if (pointMarch.getState() == MarchState.RETURNING) {
                continue;
            }
            MarchAlarmReceiver pointMarchAlam = getMarchAlarmReceiver(pointMarch.getOwnerId());
            if (pointMarchAlam != null) {
                opt.march(pointMarchAlam, pointMarch, march);
            }
            MarchAlarmReceiver marchAlarmReceiver = getMarchAlarmReceiver(march.getOwnerId());
            if (marchAlarmReceiver != null) {
                if (march.getState() == MarchState.RETURNING || march.getState() == MarchState.HOMED) {
                    Option.Relax.march(marchAlarmReceiver, march, pointMarch);
                } else {
                    Option.Alert.march(marchAlarmReceiver, march, pointMarch);
                }
            }
        }
    }

    public void onMarchAdd(March march) {
        onMarchUpdate(march);
    }

    public void onMarchRemove(March march) {
        operate(march, Option.Relax);
    }

    public void onMarchUpdate(March march) {
        if (MarchState.ASSEMBLING == march.getState()) {
            if (march.isSubMarch()) {
                operate(march, Option.Relax);
            } else {
                operate(march, Option.Alert);
            }
        } else if (MarchState.MARCHING == march.getState()) {
            operate(march, Option.Alert);
        } else {
            operate(march, Option.Relax);
        }
    }

    private MarchAlarmReceiver getMarchAlarmReceiver(long playerId) {
        Player player = PlayerManager.fetchPlayer(playerId);
        return player == null ? null : player.towerAgent.getTowerMarchObserver();
    }
}
