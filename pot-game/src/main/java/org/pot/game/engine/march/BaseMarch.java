package org.pot.game.engine.march;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.impl.StringCollectionSerializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.pot.cache.union.UnionCaches;
import org.pot.cache.union.UnionSnapshot;
import org.pot.common.concurrent.exception.IErrorCode;
import org.pot.common.util.JsonUtil;
import org.pot.game.engine.GameEngine;
import org.pot.game.engine.GameServerInfo;
import org.pot.game.engine.enums.MarchLineType;
import org.pot.game.engine.enums.MarchState;
import org.pot.game.engine.enums.MarchType;
import org.pot.game.engine.enums.PointType;
import org.pot.game.engine.error.GameErrorCode;
import org.pot.game.engine.march.bean.MarchTroopBean;
import org.pot.game.engine.player.Player;
import org.pot.game.engine.player.PlayerAsyncTask;
import org.pot.game.engine.player.PlayerManager;
import org.pot.game.engine.player.common.PlayerCommonInfo;
import org.pot.game.engine.point.*;
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
            if (subMarchTroop.getMarchHeroBeans() != null) {
                subMarchTroop.getMarchHeroBeans().forEach(marchHeroBean -> builder.addHeroes(marchHeroBean.buildProtoMessage()));
            }
            if (subMarchTroop.getMarchSoldierBeans() != null) {
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
        builder.setStartTime(this.startTime);
        builder.setEndTime(this.endTime);
        builder.setInitEndTime(this.initialEndTime);
        Player owner = PlayerManager.fetchPlayer(this.ownerId);
        if (owner != null) {
            PlayerCommonInfo commonInfo = owner.commonAgent.getPlayerCommonInfo();
            builder.setOwnerId(this.ownerId);
            builder.setOwnerName(owner.getName());
            builder.setHeadFrameId(commonInfo.getFrameId());
            builder.setHeadIconId(commonInfo.getIconId());
            UnionSnapshot unionSnapshot = UnionCaches.snapshot().getSnapshot(owner.unionAgent.getUnionId());
            if (unionSnapshot != null) {
                builder.setOwnerUnionId(unionSnapshot.getUnionId());
                builder.setOwnerUnionName(unionSnapshot.getName());
                builder.setOwnerUnionShortName(unionSnapshot.getAlias());
            }
        }
        builder.setPower(this.getPower());
        builder.setCapacity(this.getLoad());
        builder.setIsRecalled(isRecalled());
        builder.setRallyId(0);
        builder.setRallyType(getRallyType());
        MarchLineType lineType = getLineType(viewerId);
        builder.setLinType(lineType == null ? MarchLineType.OTHER.getId() : lineType.getId());
        return builder;
    }

    @Override
    public MarchType getType() {
        return type;
    }

    @Override
    public PointType getSourcePointType() {
        return sourcePointType;
    }

    @Override
    public PointExtraData getSourcePointExtraData() {
        return manager.getScene().getPointManager().getPointExtraData(sourcePoint);
    }

    @Override
    public PointExtraData getTargetPointExtraData() {
        return manager.getScene().getPointManager().getPointExtraData(targetPoint);
    }

    @Override
    public void fireUpdate() {
        this.manager.updateMarch(this.id);
    }

    @Override
    public void setControlData(MarchState state, int startPoint, int endPoint) {
        double inBlackEarthDistance;
        if (getType() == MarchType.SCOUT) {
            inBlackEarthDistance = 0;
        } else {
            inBlackEarthDistance = manager.getScene().getPointRegulation().inBlackEarthDistance(startPoint, endPoint);
        }
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        long duration = MarchUtil.calcMarchDuration(startPoint, endPoint, getSpeed(), inBlackEarthDistance);
        setControlData(state, MarchUtil.calcMarchStartMillis(), duration);
    }


    @Override
    public void setControlData(MarchState state, long start, long duration) {
        this.state = state;
        this.startTime = start;
        this.initialEndTime = start + duration;
        this.endTime = start + duration;
    }

    @Override
    public long getPower() {
        long power = 0;
        if (marchTroopBean != null) {
            power = marchTroopBean.getPower();
        }
        List<String> subMarchIdList = getSubMarchIdList();
        for (String subMarchId : subMarchIdList) {
            March subMarch = manager.getMarch(subMarchId);
            if (subMarch == null) {
                continue;
            }
            power = power + subMarch.getPower();
        }
        return power;
    }

    @Override
    public long getLoad() {
        long load = 0;
        if (marchTroopBean != null) {
            load = marchTroopBean.getLoad();
        }
        List<String> subMarchIdList = getSubMarchIdList();
        for (String subMarchId : subMarchIdList) {
            March subMarch = manager.getMarch(subMarchId);
            if (subMarch == null) {
                continue;
            }
            load = load + subMarch.getLoad();
        }
        return load;
    }

    @Override
    public double getSpeed() {
        int speed = Integer.MAX_VALUE;
        if (marchTroopBean != null) {
            int troopSpeed = marchTroopBean.getSpeed();
            if (speed > troopSpeed) {
                speed = troopSpeed;
            }
        }
        List<String> subMarchIdList = getSubMarchIdList();
        for (String subMarchId : subMarchIdList) {
            March subMarch = manager.getMarch(subMarchId);
            if (subMarch == null) {
                continue;
            }

            int troopSpeed = marchTroopBean.getSpeed();
            if (speed > troopSpeed) {
                speed = troopSpeed;
            }
        }
        speed = (speed == Integer.MAX_VALUE || speed <= 0) ? MarchUtil.DEFAULT_MARCH_SPEED : speed;
        return speed * getScene().getMarchRegulation().getMarchSpeedScale();
    }

    @Override
    public void speedup(double coefficient) {
        if (this.state != MarchState.MARCHING && this.state != MarchState.RETURNING) {
            return;
        }
        long remaining = this.getRemainingTime();
        if (remaining <= 0) {
            return;
        }
        long decrement = MarchUtil.floor((long) (remaining * coefficient));
        if (decrement <= 0) {
            return;
        }
        long newEndTIme = this.endTime - decrement;
        this.endTime = Math.max(newEndTIme, this.startTime);
        this.fireUpdate();

    }

    @Override
    public void walkBack() {
        if (this.state == MarchState.HOMED) {
            return;
        }
        if (this.state == MarchState.RETURNING) {
            return;
        }
        if (isTroopEmpty()) {
            this.arrivedHome();
        } else {
            this.setControlData(MarchState.RETURNING, targetPoint, sourcePoint);
            this.fireUpdate();
        }
    }

    protected void arrivedHome() {
        this.state = MarchState.HOMED;
        this.fireUpdate();
        this.returnTroop();
    }

    protected boolean isTargetMiss() {
        PointExtraData targetPointExtraData = this.getTargetPointExtraData();
        if (targetPointExtraData == null) {
            return true;
        }
        if (targetPointType != targetPointExtraData.getPointType()) {
            return true;
        }
        return this.targetHashCode != targetPointExtraData.hashCode();
    }

    @Override
    public IErrorCode checkRecall(long playerId) {
        if (playerId != ownerId) {
            return GameErrorCode.MARCH_NOT_EXISTS;
        }
        if (state == MarchState.HOMED) {
            return GameErrorCode.MARCH_NOT_EXISTS;
        }
        return null;
    }

    @Override
    public void recall() {
        if (state == MarchState.HOMED) {
            return;
        }
        if (state == MarchState.RETURNING) {
            return;
        }
        if (state == MarchState.MARCHING) {
            this.walkBack();
            return;
        }
        this.recallTime = MarchUtil.calcMarchStartMillis();
        long currentDuration = this.getDuration();
        long currentRemaining = this.getRemainingTime();
        if (currentDuration <= 0 || currentRemaining <= 0) {
            setControlData(MarchState.RETURNING, targetPoint, sourcePoint);
            fireUpdate();
            return;
        }
        long initialDuration = this.getInitialDuration();
        double currentWalked = System.currentTimeMillis() - this.startTime;
        long recallDuration = MarchUtil.ceil((long) (currentWalked / currentDuration * initialDuration));
        this.startTime = this.recallTime + recallTime + recallDuration - initialDuration;
        this.endTime = this.recallTime + recallDuration;
        this.initialEndTime = this.recallTime + recallDuration;
        this.state = MarchState.RETURNING;
        this.startPoint = this.targetPoint;
        this.endTime = this.sourcePoint;
        this.fireUpdate();
    }

    @Override
    public long getRecallTime() {
        return recallTime;
    }

    @Override
    public void immediateArrive() {
        this.setControlData(MarchState.ARRIVED, MarchUtil.calcMarchStartMillis(), 0);
    }

    @Override
    public void immediateReturn() {
        if (state == MarchState.HOMED) {
            return;
        }
        if (this.state == MarchState.MARCHING || this.state == MarchState.ASSEMBLING || this.state == MarchState.ENCAMPING) {
            this.recall();
        } else {
            this.walkBack();
        }
        if (this.state != MarchState.HOMED) {
            this.arrivedHome();
        }
    }

    @Override
    public void regularReturn(long duration) {
        if (this.state == MarchState.MARCHING || this.state == MarchState.ASSEMBLING || this.state == MarchState.ENCAMPING) {
            this.recall();
        } else {
            this.walkBack();
        }
        if (this.state != MarchState.RETURNING) {
            if (duration < getRemainingTime()) {
                long newEndTime = System.currentTimeMillis() + duration;
                this.endTime = Math.max(newEndTime, this.startTime);
                this.fireUpdate();
            }
        }
    }

    @Override
    public void setTroop(MarchTroopBean marchTroopBean) {
        this.marchTroopBean = marchTroopBean;
        this.fireUpdate();
    }

    @Override
    public MarchTroopBean getTroop() {
        return marchTroopBean;
    }

    @Override
    public long getSoldierAmount() {
        return marchTroopBean != null ? marchTroopBean.getSoldierAmount() : 0;
    }

    @Override
    public boolean isTroopEmpty() {
        return marchTroopBean == null || marchTroopBean.isDead();
    }

    @Override
    public void returnTroop() {
        final MarchTroopBean troops = this.marchTroopBean;
        if (troops != null) {
            final String marchId = this.getId();
            final long ownerId = this.getOwnerId();
            log.info("submit return march troop,march={},owner={},troops={}", marchId, ownerId, JsonUtil.toJson(troops));
            PlayerAsyncTask.submit(ownerId, owner -> {
                owner.submit(() -> troops.returnTroopData(owner));
            });
        }
    }

    @Override
    public long getInitialDuration() {
        return Math.max(0, this.initialEndTime - this.startTime);
    }

    @Override
    public long getDuration() {
        return Math.max(0, this.endTime - this.startTime);
    }

    @Override
    public long getSpeedupTime() {
        return Math.max(0, this.initialEndTime - this.endTime);
    }

    @Override
    public long getRemainingTime() {
        long end = MarchUtil.floor(this.endTime);
        long now = MarchUtil.floor(System.currentTimeMillis());
        return Math.max(0, end - now);
    }

    @Override
    public int getRallyType() {
        return -1;
    }

    @Override
    public void setParentMarchId(String parentMarchId) {
        this.parentMarchId = parentMarchId;
        this.fireUpdate();
    }

    @Override
    public String getParentMarchId() {
        return this.parentMarchId;
    }

    @Override
    public void addSubMarchId(String subMarchId) {
        this.subMarchIdList.add(subMarchId);
        this.fireUpdate();
    }

    @Override
    public void removeSubMarchId(String subMarchId) {
        this.subMarchIdList.remove(subMarchId);
        this.fireUpdate();
    }

    @Override
    public List<String> getSubMarchIdList() {
        return subMarchIdList;
    }

    @Override
    public void detach() {
        for (String subMarchId : subMarchIdList) {
            March subMarch = manager.getMarch(subMarchId);
            if (subMarch != null && StringUtils.isNotBlank(subMarch.getParentMarchId())) {
                subMarch.setParentMarch(null);
                subMarch.fireUpdate();
            }
        }
        subMarchIdList.clear();
        March parentMarch = getParentMarch();
        if (parentMarch != null) {
            parentMarch.removeSubMarch(this);
        }
        this.setParentMarch(null);
        this.fireUpdate();
    }

    private MarchLineType getLineType(final long viewId) {
        if (Relational.isFriendly(viewId, ownerId)) {
            return MarchLineType.FRIENDLY;
        }
        PointExtraData targetPointExtraData = this.getTargetPointExtraData();
        if (targetPointExtraData instanceof PointCityData) {
            long targetPlayerId = ((PointCityData) targetPointExtraData).getPlayerId();
            if (Relational.isFriendly(viewId, targetPlayerId)) {
                return MarchLineType.ENEMY;
            }
        } else if (targetPointExtraData instanceof PointResourceData) {
            long occupier = ((PointResourceData) targetPointExtraData).getOccupier();
            if (occupier > 0 && Relational.isFriendly(viewId, occupier)) {
                return MarchLineType.ENEMY;
            }
        } else if (targetPointExtraData instanceof PointUnionBuildingData) {
            long viewUnionId = UnionCaches.snapshot().getPlayerUnionId(viewId);
            long targetUnionId = ((PointUnionBuildingData) targetPointExtraData).getUnionId();
            if (viewUnionId > 0 && viewUnionId == targetUnionId) {
                return MarchLineType.ENEMY;
            }
            long leaderId = ((PointUnionBuildingData) targetPointExtraData).getLeaderId();
            if (leaderId > 0 && Relational.isFriendly(viewId, leaderId)) {
                return MarchLineType.ENEMY;
            }
        } else if (targetPointExtraData instanceof PointEncampmentData) {
            long occupier = ((PointEncampmentData) targetPointExtraData).getPlayerId();
            if (occupier > 0 && Relational.isFriendly(viewId, occupier)) {
                return MarchLineType.ENEMY;
            }
        }
        return MarchLineType.OTHER;

    }
}
