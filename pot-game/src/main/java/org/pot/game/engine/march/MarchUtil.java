package org.pot.game.engine.march;

import org.pot.common.util.MapUtil;
import org.pot.common.util.PointUtil;
import org.pot.common.util.RandomUtil;
import org.pot.game.engine.march.bean.MarchHeroBean;
import org.pot.game.engine.march.bean.MarchSoldierBean;
import org.pot.game.engine.march.bean.MarchTroopBean;
import org.pot.game.engine.player.Player;
import org.pot.game.resource.parameter.GameParameters;
import org.pot.message.protocol.world.MarchHero;
import org.pot.message.protocol.world.MarchSoldier;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class MarchUtil {
    public static final long MAX_DURATION = TimeUnit.DAYS.toMillis(730);
    public static final int DEFAULT_MARCH_SPEED = 200;

    public static long floor(long millis) {
        return millis == 0 ? 0 : (Math.abs(millis) / 1000L) * 1000L;
    }

    public static MarchTroopBean transformTroopBean(Player player, List<Integer> heroList, Map<Integer, Long> soldierList) {
        List<MarchHeroBean> heroBeans = transformHeroBean(player, heroList);
        List<MarchSoldierBean> soldierBeans = transformSoldierBean(player, soldierList);
        return new MarchTroopBean(heroBeans, soldierBeans);
    }

    public static long calcMarchDuration(int startPoint, int endPoint, double speed, double inBlackEarthDistance) {
        final double distance = PointUtil.calcPointDistance(startPoint, endPoint);
        if (inBlackEarthDistance > 0) {
            if (inBlackEarthDistance > distance) {
                inBlackEarthDistance = distance;
            }
            double blackEarthSpeedPercent = GameParameters.black_soil_marching_speed_coefficient.getDouble(0.25);
            long inBlackEarthMillis = (long) Math.ceil(inBlackEarthDistance * 3600 * 1000 / speed * blackEarthSpeedPercent);
            double outBlackEarthDistance = distance - inBlackEarthDistance;
            long outBlackEarthMillis = (long) Math.ceil(outBlackEarthDistance * 3600 * 1000 / speed);
            return ceil(inBlackEarthMillis + outBlackEarthMillis);
        } else {
            long millis = (long) Math.ceil(distance * 3600 * 1000 / speed);
            return ceil(millis);
        }
    }

    public static long ceil(long millis) {
        return millis == 0 ? 0 : ((Math.abs(millis) / 1000) + 1) * 1000L;
    }

    public static long calcMarchStartMillis() {
        long now = System.currentTimeMillis();
        long div = now % 1000L;
        int dif = 100;
        return div < dif ? now : (now / 1000L * 1000L) + RandomUtil.randomBetweenInt(1, dif);
    }

    public static List<MarchSoldierBean> transformSoldierBean(Player player, Map<Integer, Long> soldierMap) {
        return null;
    }

    public static List<MarchHeroBean> transformHeroBean(Player player, List<Integer> heroList) {
        return null;
    }

    public static MarchTroopBean transformTroopBean(Player player, List<MarchHero> heroList, List<MarchSoldier> soldierList) {
        List<Integer> h = heroList.stream().map(MarchHero::getHeroId).collect(Collectors.toList());
        Map<Integer, Long> m = MapUtil.toHashMap(soldierList, MarchSoldier::getSoldierId, marchSoldier -> (long) marchSoldier.getSoldierAmount());
        return transformTroopBean(player, h, m);
    }

}
