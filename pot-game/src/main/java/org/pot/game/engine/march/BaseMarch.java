package org.pot.game.engine.march;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.impl.StringCollectionSerializer;
import lombok.extern.slf4j.Slf4j;
import org.pot.common.concurrent.exception.IErrorCode;
import org.pot.game.engine.GameEngine;
import org.pot.game.engine.GameServerInfo;
import org.pot.game.engine.enums.MarchState;
import org.pot.game.engine.enums.MarchType;
import org.pot.game.engine.enums.PointType;
import org.pot.game.engine.march.bean.MarchTroopBean;
import org.pot.game.engine.point.PointExtraData;
import org.pot.game.engine.scene.WorldPoint;
import org.pot.message.protocol.world.WorldMarchInfo;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
public abstract class BaseMarch implements March {
    @JsonIgnore
    protected volatile MarchManager manager;
    @JsonProperty("id")
    protected volatile String id;
    @JsonProperty("createTime")
    protected volatile long createTime;
    @JsonProperty("ownerId")
    protected volatile long ownerId;
    @JsonProperty("sourcePoint")
    protected volatile int sourcePoint;
    @JsonProperty("sourcePointType")
    protected volatile PointType sourcePointType;
    @JsonProperty("targetPoint")
    protected volatile int targetPoint;
    @JsonProperty("targetPointType")
    protected volatile PointType targetPointType;
    @JsonProperty("targetHashCode")
    protected volatile int targetHashCode;
    @JsonProperty("type")
    protected volatile MarchType type;
    @JsonProperty("state")
    protected volatile MarchState state;
    @JsonProperty("marchTroopBean")
    protected volatile MarchTroopBean marchTroopBean;
    @JsonProperty("startPoint")
    protected volatile int startPoint;
    @JsonProperty("endPoint")
    protected volatile int endPoint;
    @JsonProperty("startTime")
    protected volatile long startTime;
    @JsonProperty("endTime")
    protected volatile long endTime;
    @JsonProperty("initialEndTime")
    protected volatile long initialEndTime;
    @JsonProperty("recallTime")
    protected volatile long recallTime;
    @JsonProperty("skinId")
    protected volatile int skinId;
    @JsonProperty("parentMarchId")
    protected volatile String parentMarchId;
    @JsonProperty("subMarchIdList")
    @JsonSerialize(using = StringCollectionSerializer.class)
    protected volatile List<String> subMarchIdList = new CopyOnWriteArrayList<>();

    public BaseMarch() {
    }

    public BaseMarch(MarchManager marchManager, MarchType type, int sourcePoint, int targetPoint) {
        this.id = String.valueOf(GameEngine.getInstance().nextId());
        this.createTime = MarchUtil.calcMarchStartMillis();
        this.manager = marchManager;
        this.type = type;
        this.sourcePoint = sourcePoint;
        this.sourcePointType = marchManager.getScene().getPointManager().getPointType(sourcePoint);
        this.targetPoint = targetPoint;
        this.targetPointType = marchManager.getScene().getPointManager().getPointType(targetPoint);
        PointExtraData targetPointExtraData = getTargetPointExtraData();
        this.targetHashCode = targetPointExtraData == null ? 0 : targetPointExtraData.hashCode();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public long getOwnerId() {
        return ownerId;
    }

    @Override
    public void onAdd(MarchManager marchManager) {
        MarchManager prevManager = this.manager;
        if (prevManager != null && prevManager != marchManager) {
            log.error("March add to another manager");
        }
        this.manager = marchManager;
        March parentMarch = getParentMarch();
        if (parentMarch != null) {
            parentMarch.addSubMarch(this);
        }
        PointExtraData targetPointExtraData = this.getTargetPointExtraData();
        if (targetPointExtraData != null) {
            targetPointExtraData.addMarchId(this.id);
        }
        this.targetHashCode = targetPointExtraData == null ? 0 : targetPointExtraData.hashCode();
    }

    @Override
    public void onRemove(MarchManager marchManager) {
        March parentMarch = getParentMarch();
        if (parentMarch != null) {
            parentMarch.removeSubMarch(this);
        }
        this.setParentMarchId(null);
        PointExtraData targetPointExtraData = this.getTargetPointExtraData();
        if (targetPointExtraData != null) {
            targetPointExtraData.removeMarchId(this.id);
        }
    }

    @Override
    public void setType(MarchType type) {
        this.type = type;
    }

    @Override
    public MarchManager getManager() {
        return manager;
    }

    @Override
    public void onInitPlayerData() {

    }

    @Override
    public MarchState getState() {
        return state;
    }

    @Override
    public void onError() {
        returnTroop();
    }

    @Override
    public int getTargetPoint() {
        return targetPoint;
    }

    @Override
    public void setSourcePoint(int sourcePoint) {
        this.sourcePoint = sourcePoint;
        this.sourcePointType = manager.getScene().getPointManager().getPointType(sourcePoint);
    }

    @Override
    public int getSourcePoint() {
        return sourcePoint;
    }

    @Override
    public WorldMarchInfo.Builder buildWorldMarchInfo(long viewerId) {
        WorldMarchInfo.Builder builder = WorldMarchInfo.newBuilder();
        builder.setUid(this.id);
        builder.setState(this.state.getId());
        builder.setType(this.type.getId());
        MarchTroopBean troopBean = this.marchTroopBean;
        if (troopBean != null && troopBean.getMarchHeroBeans() != null) {
            troopBean.getMarchHeroBeans().forEach(marchHeroBean -> builder.addHeroes(marchHeroBean.buildProtoMessage()));
        }
        if (troopBean != null && troopBean.getMarchSoldierBeans() != null) {
            troopBean.getMarchSoldierBeans().forEach(marchSoldierBean -> builder.addSoldiers(marchSoldierBean.buildProtoMessage()));
        }
        List<String> subMarchIdList = this.getSubMarchIdList();
        for (String subMarchId : subMarchIdList) {
            March subMarch = manager.getMarch(subMarchId);
            if (subMarch == null) {
                continue;
            }
            MarchTroopBean subMarchTroop = subMarch.getTroop();
            if (subMarchTroop == null) {
                continue;
            }
            if (subMarchTroop != null && subMarchTroop.getMarchHeroBeans() != null) {
                subMarchTroop.getMarchHeroBeans().forEach(marchHeroBean -> builder.addHeroes(marchHeroBean.buildProtoMessage()));
            }
            if (subMarchTroop != null && subMarchTroop.getMarchSoldierBeans() != null) {
                subMarchTroop.getMarchSoldierBeans().forEach(marchSoldierBean -> builder.addSoldiers(marchSoldierBean.buildProtoMessage()));
            }
        }
        builder.setStartPoint(WorldPoint.buildWorldPointStruct(GameServerInfo.getServerId(), startPoint));
        PointExtraData startPointExtraData = manager.getScene().getPointManager().getPointExtraData(startPoint);
        builder.setStartPointType(startPointExtraData == null ? PointType.LAND.getId() : startPointExtraData.getPointType().getId());
        builder.setEndPoint(WorldPoint.buildWorldPointStruct(GameServerInfo.getServerId(), endPoint));
        PointExtraData endPointExtraData = manager.getScene().getPointManager().getPointExtraData(endPoint);
        builder.setEndPointType(endPointExtraData == null ? PointType.LAND.getId() : endPointExtraData.getPointType().getId());
        builder.setCreateTime(this.createTime);
        return builder;
    }

    @Override
    public void tick() {

    }

    @Override
    public MarchType getType() {
        return null;
    }

    @Override
    public PointType getSourcePointType() {
        return null;
    }

    @Override
    public PointExtraData getSourcePointExtraData() {
        return null;
    }

    @Override
    public PointExtraData getTargetPointExtraData() {
        return null;
    }

    @Override
    public void fireUpdate() {

    }

    @Override
    public void setControlData(MarchState state, int startPoint, int endPoint) {

    }

    @Override
    public void setControlData(MarchState state, long start, long duration) {

    }

    @Override
    public long getPower() {
        return 0;
    }

    @Override
    public long getLoad() {
        return 0;
    }

    @Override
    public double getSpeed() {
        return 0;
    }

    @Override
    public void speedup(double coefficient) {

    }

    @Override
    public void walkBack() {

    }

    @Override
    public IErrorCode checkRecall(long playerId) {
        return null;
    }

    @Override
    public void recall() {

    }

    @Override
    public long getRecallTime() {
        return 0;
    }

    @Override
    public void immediateArrive() {

    }

    @Override
    public void immediateReturn() {

    }

    @Override
    public void regularReturn(long duration) {

    }

    @Override
    public void setTroop(MarchTroopBean marchTroopBean) {

    }

    @Override
    public MarchTroopBean getTroop() {
        return null;
    }

    @Override
    public long getSoldierAmount() {
        return 0;
    }

    @Override
    public boolean isTroopEmpty() {
        return false;
    }

    @Override
    public void returnTroop() {

    }

    @Override
    public long getInitialDuration() {
        return 0;
    }

    @Override
    public long getDuration() {
        return 0;
    }

    @Override
    public long getSpeedupTime() {
        return 0;
    }

    @Override
    public long getRemainingTime() {
        return 0;
    }

    @Override
    public int getRallyType() {
        return 0;
    }

    @Override
    public void setParentMarchId(String parentMarchId) {

    }

    @Override
    public String getParentMarchId() {
        return null;
    }

    @Override
    public void addSubMarchId(String subMarchId) {

    }

    @Override
    public void removeSubMarchId(String subMarchId) {

    }

    @Override
    public List<String> getSubMarchIdList() {
        return null;
    }

    @Override
    public void detach() {

    }
}
