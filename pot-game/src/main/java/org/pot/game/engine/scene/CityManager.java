package org.pot.game.engine.scene;

import lombok.Getter;
import org.pot.game.engine.enums.PointType;

import java.util.List;

public class CityManager {
    @Getter
    private final AbstractScene scene;

    public CityManager(AbstractScene scene) {
        this.scene = scene;
    }

    public void init() {
        scene.getCityRegulation().init();
    }

    public void tick() {
        scene.getCityRegulation().tick();
    }

    protected void save(boolean async) {
        scene.getCityRegulation().save(async);
    }

    public List<WorldPoint> getCityPointList() {
        return scene.getPointManager().getMainPoints(PointType.CITY);
    }
}
