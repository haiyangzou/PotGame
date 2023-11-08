package org.pot.game.resource.parameter;

import org.pot.config.Configure;
import org.pot.game.resource.GameConfigSupport;
import org.pot.game.resource.StringJsonConfig;

@Configure(file = "StringParameter.json")
public class StringParameterConfig extends StringJsonConfig<Parameter> {
    public static StringParameterConfig getInstance() {
        return GameConfigSupport.getConfig(StringParameterConfig.class);
    }
}
