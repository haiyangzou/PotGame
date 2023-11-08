package org.pot.game.resource.parameter;

import org.pot.config.Configure;
import org.pot.game.resource.GameConfigSupport;
import org.pot.game.resource.StringJsonConfig;

@Configure(file = "Parameter.json")
public class ParameterConfig extends StringJsonConfig<Parameter> {
    public static ParameterConfig getInstance() {
        return GameConfigSupport.getConfig(ParameterConfig.class);
    }
}
