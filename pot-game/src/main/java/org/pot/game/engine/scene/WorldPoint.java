package org.pot.game.engine.scene;


import lombok.Getter;
import org.pot.common.util.PointUtil;
import org.pot.game.engine.enums.PointType;
import org.pot.game.engine.point.PointExtraData;

import java.io.Serializable;

public class WorldPoint implements Serializable {
    @Getter
    private AbstractScene scene;
    @Getter
    private final long timestamp = System.currentTimeMillis();

    public WorldPoint(AbstractScene scene) {
        this.scene = scene;
    }

    public int getId() {
        return 0;
    }

    public int getMainX() {
        return 0;
    }

    public int getMainY() {
        return 0;
    }

    public int getMainId() {
        return PointUtil.getPointId(getMainX(), getMainY());
    }

    public boolean isMainPoint() {
        return getId() == getMainId();
    }

    public PointType getType() {
        return PointType.find(0);
    }

    public <T extends PointExtraData> T getExtraData(Class<T> cls) {
        Class<? extends PointExtraData> extraDataType = getType().getExtraDataType();
        if (extraDataType != null && cls.isAssignableFrom(extraDataType)) {
            return (T) null;
        }
        return null;
    }
}
