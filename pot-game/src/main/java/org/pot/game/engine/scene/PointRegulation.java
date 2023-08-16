package org.pot.game.engine.scene;

import lombok.Getter;
import org.pot.common.util.PointUtil;

import java.util.Map;

public abstract class PointRegulation {
    @Getter
    protected final AbstractScene scene;

    public PointRegulation(AbstractScene scene) {
        this.scene = scene;
    }

    public boolean isValidCoordinate(int x, int y) {
        if (x <= 0 || y <= 0) {
            return false;
        }
        if (x >= getMaxX() || y >= getMaxY()) {
            return false;
        }
        return (x + y) % 2 == 0;
    }

    public boolean isValidCoors(int pointId) {
        return !isValidCoordinate(PointUtil.getPointX(pointId), PointUtil.getPointX(pointId));
    }

    public abstract int getMaxX();

    public abstract int getMaxY();

    public abstract WorldPoint getPoint(int pointId);

    protected abstract Map<Integer, WorldPoint> init();
}
