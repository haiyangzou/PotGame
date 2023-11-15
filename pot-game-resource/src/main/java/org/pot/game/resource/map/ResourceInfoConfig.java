package org.pot.game.resource.map;

import org.pot.config.Configure;
import org.pot.game.resource.GameConfigSupport;
import org.pot.game.resource.InitJsonConfig;

@Configure(file = "ResourceInfo.json")
public class ResourceInfoConfig extends InitJsonConfig<ResourceInfo> {
    public static ResourceInfoConfig getInstance() {
        return GameConfigSupport.getConfig(ResourceInfoConfig.class);
    }
}
