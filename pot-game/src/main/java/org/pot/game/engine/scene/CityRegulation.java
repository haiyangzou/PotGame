package org.pot.game.engine.scene;

import lombok.Getter;
import org.pot.game.engine.point.PointCityData;

@Getter
public abstract class CityRegulation {
    protected final AbstractScene scene;

    public CityRegulation(AbstractScene scene) {
        this.scene = scene;
    }

    protected abstract int randomPutCity(PointCityData city);

    protected abstract void save(boolean async);

    protected abstract void init();

    protected abstract void tick();
}
