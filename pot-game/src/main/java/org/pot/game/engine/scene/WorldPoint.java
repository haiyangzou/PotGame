package org.pot.game.engine.scene;


import lombok.Getter;
import org.pot.common.util.PointUtil;
import org.pot.game.engine.GameServerInfo;
import org.pot.game.engine.enums.PointType;
import org.pot.game.engine.point.PointExtraData;
import org.pot.game.persistence.entity.WorldPointEntity;
import org.pot.message.protocol.world.WorldPointInfo;
import org.pot.message.protocol.world.WorldPointStruct;

import java.io.Serializable;
import java.util.Objects;

@Getter
public class WorldPoint implements Serializable {
    private AbstractScene scene;
    private PointType type;
    private int id;
    private int x;
    private int y;
    private int mainX;
    private int mainY;
    private PointExtraData extraData;
    private final long timestamp = System.currentTimeMillis();
    private WorldPointEntity worldPointEntity;

    public WorldPoint(AbstractScene scene, PointType type, int pid, int mainX, int mainY, PointExtraData extraData) {
        this(scene, type.getId(), PointUtil.getPointX(pid), PointUtil.getPointY(pid), mainX, mainY, extraData);
    }

    public WorldPoint(AbstractScene scene, int type, int x, int y, int mainX, int mainY, PointExtraData extraData) {
        this.scene = Objects.requireNonNull(scene);
        this.type = Objects.requireNonNull(PointType.find(type));
        this.id = PointUtil.getPointId(x, y);
        this.x = x;
        this.y = y;
        this.mainX = mainX;
        this.mainY = mainY;
        this.extraData = extraData;
        if (this.extraData != null) {
            this.extraData.setScene(scene);
        }
    }

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

    public WorldPointStruct toWorldPointStruct() {
        return buildWorldPointStruct(GameServerInfo.getServerId(), getX(), getY());
    }

    public static WorldPointStruct buildWorldPointStruct(int sid, int pid) {
        int x = PointUtil.getPointX(pid);
        int y = PointUtil.getPointY(pid);
        return buildWorldPointStruct(sid, x, y);
    }

    public static WorldPointStruct buildWorldPointStruct(int sid, int x, int y) {
        return buildWorldPointStruct(sid, PointUtil.getPointId(x, y));
    }

    public WorldPointInfo.Builder toWorldPointInfo() {
        WorldPointInfo.Builder builder = WorldPointInfo.newBuilder();
        builder.setTimestamp(timestamp);
        builder.setPoint(toWorldPointStruct());
        builder.setType(getType().getId());
        return builder;
    }

    public boolean isClientVisible() {
        return isMainPoint() && getRawExtraData() != null && getRawExtraData().isClientVisible();
    }
}
