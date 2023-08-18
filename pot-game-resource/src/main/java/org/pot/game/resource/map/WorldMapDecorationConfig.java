package org.pot.game.resource.map;

import org.pot.game.resource.GameConfigSupport;
import org.pot.game.resource.InitJsonConfig;

public class WorldMapDecorationConfig extends InitJsonConfig<WorldMapDecoration> {
    public static WorldMapDecorationConfig getInstance() {
        return GameConfigSupport.getConfig(WorldMapDecorationConfig.class);
    }
}
