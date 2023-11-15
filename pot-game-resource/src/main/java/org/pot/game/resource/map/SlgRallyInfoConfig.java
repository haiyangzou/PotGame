package org.pot.game.resource.map;

import org.pot.config.Configure;
import org.pot.game.resource.GameConfigSupport;
import org.pot.game.resource.InitJsonConfig;

@Configure(file = "SlgRallyInfo.json")
public class SlgRallyInfoConfig extends InitJsonConfig<SlgRallyInfo> {
    public static SlgRallyInfoConfig getInstance() {
        return GameConfigSupport.getConfig(SlgRallyInfoConfig.class);
    }
}
