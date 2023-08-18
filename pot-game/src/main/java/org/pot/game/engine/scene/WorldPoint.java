package org.pot.game.engine.scene;


import lombok.Getter;
import org.pot.common.util.PointUtil;
import org.pot.game.engine.enums.PointType;
import org.pot.game.engine.point.PointExtraData;
import org.pot.game.persistence.entity.WorldPointEntity;

import java.io.Serializable;

public class WorldPoint implements Serializable {
    @Getter
    private AbstractScene scene;
    @Getter
    private final long timestamp = System.currentTimeMillis();
    @Getter
    private WorldPointEntity worldPointEntity;

    public WorldPoint(AbstractScene scene, WorldPointEntity worldPointEntity) {
        this.scene = scene;
        this.worldPointEntity = worldPointEntity;
        PointExtraData data = worldPointEntity.getExtraData();
        if (data != null) {
            data.setScene(scene);
        }
    }

    public PointExtraData getRawExtraData() {
        return null;
    }

    public int getId() {
        return 0;
    }

    public int getX() {
        return 0;
    }

    public int getY() {
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

    public void validate() {
        PointExtraData extraData = this.getRawExtraData();
        if (extraData != null) extraData.validate(this);
    }
}
