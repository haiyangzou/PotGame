package org.pot.game.engine.world.module.map.rally;

import org.pot.game.engine.world.AbstractWorldModule;
import org.pot.game.engine.world.WorldModuleType;

public class WorldRallyModule extends AbstractWorldModule {
    public static WorldRallyModule getInstance() {
        return WorldModuleType.WORLD_RALLY.getInstance();
    }

}
