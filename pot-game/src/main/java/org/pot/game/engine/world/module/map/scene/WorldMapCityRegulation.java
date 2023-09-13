package org.pot.game.engine.world.module.map.scene;

import lombok.Getter;
import org.pot.game.engine.point.PointCityData;
import org.pot.game.engine.scene.AbstractScene;
import org.pot.game.engine.scene.CityRegulation;
import org.pot.game.engine.world.module.map.clean.WorldMapCleaner;

public class WorldMapCityRegulation extends CityRegulation {
    @Getter
    private final WorldMapCleaner cleaner = new WorldMapCleaner();

    public WorldMapCityRegulation(AbstractScene scene) {
        super(scene);
    }

    @Override
    protected int randomPutCity(PointCityData city) {
        return 0;
    }
}
