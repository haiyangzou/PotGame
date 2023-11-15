package org.pot.game.resource.map;

import org.pot.config.Configure;
import org.pot.game.resource.GameConfigSupport;
import org.pot.game.resource.InitJsonConfig;

@Configure(file = "RallyRefresh.json")
public class RallyRefreshConfig extends InitJsonConfig<RallyRefresh> {
    public static RallyRefreshConfig getInstance() {
        return GameConfigSupport.getConfig(RallyRefreshConfig.class);
    }

    public static RallyRefresh getRallyRefresh(int dayId, int blockBandId) {
        return getInstance().getSpecList().stream().filter(v -> v.getDayId() == dayId && v.getBlockId() == blockBandId)
                .findFirst().orElse(null);
    }

}