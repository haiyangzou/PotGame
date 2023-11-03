package org.pot.game.engine.world.module.instance.scene;

import lombok.extern.slf4j.Slf4j;
import org.pot.game.engine.scene.AbstractScene;
import org.pot.game.engine.scene.CityRegulation;
import org.pot.game.engine.scene.PointRegulation;

import java.util.function.Function;

@Slf4j
public abstract class InstanceScene extends AbstractScene {

    public InstanceScene(String name, Function<AbstractScene, CityRegulation> cityRegulation, Function<AbstractScene, PointRegulation> pointRegulation) {
        super(name, cityRegulation, pointRegulation);
    }
}
