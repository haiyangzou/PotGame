package org.pot.game.resource.army;

import org.pot.config.Configure;
import org.pot.game.resource.GameConfigSupport;
import org.pot.game.resource.InitJsonConfig;

@Configure(file = "SoldierAttr.json")
public class SoldierAttrConfig extends InitJsonConfig<SoldierAttr> {
    public static SoldierAttrConfig getInstance() {
        return GameConfigSupport.getConfig(SoldierAttrConfig.class);
    }
}
