package org.pot.game.resource.map;

import org.pot.game.resource.GameConfigSupport;
import org.pot.game.resource.InitJsonConfig;

public class MonsterRefreshConfig extends InitJsonConfig<MonsterRefresh> {
    public static MonsterRefreshConfig getInstance() {
        return GameConfigSupport.getConfig(MonsterRefreshConfig.class);
    }
}