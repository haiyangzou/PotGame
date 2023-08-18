package org.pot.game.resource.map;

import org.pot.game.resource.GameConfigSupport;
import org.pot.game.resource.InitJsonConfig;

public class ResourceRefreshConfig extends InitJsonConfig<ResourceRefresh> {
    public static ResourceRefreshConfig getInstance() {
        return GameConfigSupport.getConfig(ResourceRefreshConfig.class);
    }
}
