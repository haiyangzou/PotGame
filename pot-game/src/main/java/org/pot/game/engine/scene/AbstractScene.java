package org.pot.game.engine.scene;

import lombok.Getter;
import org.pot.game.engine.march.MarchManager;

import java.util.function.Function;

public abstract class AbstractScene {
    @Getter
    protected final String name;
    @Getter
    protected final MarchManager marchManager;
    @Getter
    protected final PointManager pointManager;
    @Getter
    protected final PointRegulation pointRegulation;
    @Getter
    protected final CityRegulation cityRegulation;

    public AbstractScene(String name, Function<AbstractScene, CityRegulation> cityRegulation, Function<AbstractScene, PointRegulation> pointRegulation) {
        this.name = name;
        this.marchManager = new MarchManager(this);
        this.pointManager = new PointManager(this);
        this.pointRegulation = pointRegulation.apply(this);
        this.cityRegulation = cityRegulation.apply(this);
    }

    public void init() {
    }

}
