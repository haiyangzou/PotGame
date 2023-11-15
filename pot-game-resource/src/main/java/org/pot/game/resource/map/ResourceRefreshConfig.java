package org.pot.game.resource.map;

import org.pot.config.Configure;
import org.pot.game.resource.GameConfigSupport;
import org.pot.game.resource.InitJsonConfig;

@Configure(file = "ResourceRefresh.json")
public class ResourceRefreshConfig extends InitJsonConfig<ResourceRefresh> {
    public static ResourceRefreshConfig getInstance() {
        return GameConfigSupport.getConfig(ResourceRefreshConfig.class);
    }
}
