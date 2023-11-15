package org.pot.game.resource.switchcontrol;

import org.pot.config.Configure;
import org.pot.game.resource.GameConfigSupport;
import org.pot.game.resource.StringJsonConfig;

@Configure(file = "SwitchControl.json")
public class SwitchControlConfig extends StringJsonConfig<SwitchControl> {
    public static SwitchControlConfig getInstance() {
        return GameConfigSupport.getConfig(SwitchControlConfig.class);
    }
}
