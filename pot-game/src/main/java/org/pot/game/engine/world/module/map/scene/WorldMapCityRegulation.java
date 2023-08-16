package org.pot.game.engine.world.module.map.scene;

import org.pot.game.engine.point.PointCityData;
import org.pot.game.engine.scene.AbstractScene;
import org.pot.game.engine.scene.CityRegulation;

public class WorldMapCityRegulation extends CityRegulation {
    public WorldMapCityRegulation(AbstractScene scene) {
        super(scene);
    }

    @Override
    protected int randomPutCity(PointCityData city) {
        return 0;
    }
}
