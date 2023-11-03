package org.pot.game.engine.world.module.map.scene;

import lombok.extern.slf4j.Slf4j;
import org.pot.game.engine.scene.AbstractScene;
import org.pot.game.engine.scene.MarchRegulation;

@Slf4j
public class WorldMapMarchRegulation extends MarchRegulation {
    public WorldMapMarchRegulation(AbstractScene scene) {
        super(scene);
    }

    @Override
    public AbstractScene getScene() {
        return null;
    }

    @Override
    public void tick() {

    }
}
