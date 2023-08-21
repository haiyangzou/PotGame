package org.pot.game.resource.map;

import org.pot.game.resource.GameConfigSupport;
import org.pot.game.resource.InitJsonConfig;

public class SlgRallyInfoConfig extends InitJsonConfig<SlgRallyInfo> {
    public static SlgRallyInfoConfig getInstance() {
        return GameConfigSupport.getConfig(SlgRallyInfoConfig.class);
    }
}
