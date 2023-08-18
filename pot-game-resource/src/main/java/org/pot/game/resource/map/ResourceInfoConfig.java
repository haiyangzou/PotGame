package org.pot.game.resource.map;

import org.pot.game.resource.GameConfigSupport;
import org.pot.game.resource.InitJsonConfig;

public class ResourceInfoConfig extends InitJsonConfig<ResourceInfo> {
    public static ResourceInfoConfig getInstance() {
        return GameConfigSupport.getConfig(ResourceInfoConfig.class);
    }
}
