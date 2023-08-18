package org.pot.game.resource.map;

import org.pot.game.resource.GameConfigSupport;
import org.pot.game.resource.InitJsonConfig;

public class SlgMonsterInfoConfig extends InitJsonConfig<SlgMonsterInfo> {
    public static SlgMonsterInfoConfig getInstance() {
        return GameConfigSupport.getConfig(SlgMonsterInfoConfig.class);
    }
}
