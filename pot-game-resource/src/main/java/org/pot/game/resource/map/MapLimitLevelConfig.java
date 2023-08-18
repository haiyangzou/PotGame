package org.pot.game.resource.map;

import org.pot.game.resource.GameConfigSupport;
import org.pot.game.resource.InitJsonConfig;

import java.util.function.Function;

public class MapLimitLevelConfig extends InitJsonConfig<MapLimitLevel> {
    public static MapLimitLevelConfig getInstance() {
        return GameConfigSupport.getConfig(MapLimitLevelConfig.class);
    }

    public int getMaxMonsterLevel(int days) {
        return getMaxLevel(days, MapLimitLevel::getMonsterLevel);
    }

    private static int getMaxLevel(int openedDays, Function<MapLimitLevel, Integer> function) {
        int maxLevel = 1;
        for (MapLimitLevel mapLimitLevel : getInstance().getSpecList()) {
            if (openedDays >= mapLimitLevel.getServerDay()) {
                maxLevel = function.apply(mapLimitLevel);
            } else {
                break;
            }
        }
        return maxLevel;
    }
}
