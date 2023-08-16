package org.pot.game.engine.world.module.map.scene;

import org.pot.game.engine.scene.AbstractScene;

public class WorldMapScene extends AbstractScene {
    public static final WorldMapScene instance = new WorldMapScene();

    public WorldMapScene() {
        super(WorldMapScene.class.getSimpleName(), WorldMapCityRegulation::new, WorldMapPointRegulation::new);

    }
}
