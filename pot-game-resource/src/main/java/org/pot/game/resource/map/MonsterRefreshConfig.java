package org.pot.game.resource.map;

import org.pot.config.Configure;
import org.pot.game.resource.GameConfigSupport;
import org.pot.game.resource.InitJsonConfig;

@Configure(file = "MonsterRefresh.json")
public class MonsterRefreshConfig extends InitJsonConfig<MonsterRefresh> {
    public static MonsterRefreshConfig getInstance() {
        return GameConfigSupport.getConfig(MonsterRefreshConfig.class);
    }

    public static MonsterRefresh getMonsterRefresh(int dayId, int blockBandId) {
        return getInstance().getSpecList().stream().filter(v -> v.getDayId() == dayId && v.getBlockId() == blockBandId)
                .findFirst().orElse(null);
    }

}