package org.pot.game.engine.march;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.apache.commons.lang3.StringUtils;
import org.pot.common.concurrent.exception.IErrorCode;
import org.pot.common.databind.json.JsonObject;
import org.pot.common.util.CollectionUtil;
import org.pot.game.engine.enums.MarchState;
import org.pot.game.engine.enums.MarchType;
import org.pot.game.engine.enums.PointType;
import org.pot.game.engine.march.bean.MarchTroopBean;
import org.pot.game.engine.point.PointExtraData;
import org.pot.message.protocol.world.WorldMarchInfo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
public interface March extends JsonObject, Serializable {
    void setSourcePoint(int sourcePoint);

    void setType(MarchType type);

    String getId();

    long getOwnerId();

    void onAdd(MarchManager marchManager);

    void onRemove(MarchManager marchManager);

    MarchManager getManager();

    void onInitPlayerData();

    int getTargetPoint();

    WorldMarchInfo.Builder buildWorldMarchInfo(final long viewerId);

    void tick();

    MarchState getState();

    MarchType getType();

    void onError();

    int getSourcePoint();

    PointType getSourcePointType();

    PointExtraData getSourcePointExtraData();

    PointExtraData getTargetPointExtraData();

    default <T extends PointExtraData> T getTargetPointExtraData(Class<T> cls) {
        PointExtraData pointExtraData = getTargetPointExtraData();
        if (pointExtraData != null && cls.isAssignableFrom(pointExtraData.getClass())) {
            return (T) pointExtraData;
        }
        return null;
    }

    void fireUpdate();

    void setControlData(MarchState state, int startPoint, int endPoint);

    void setControlData(MarchState state, long start, long duration);

    long getPower();

    long getLoad();

    double getSpeed();

    void speedup(double coefficient);

    void walkBack();

    IErrorCode checkRecall(long playerId);

    void recall();

    long getRecallTime();

    default boolean isRecalled() {
        return getRecallTime() > 0;
    }

    void immediateArrive();

    void immediateReturn();

    void regularReturn(long duration);

    void setTroop(MarchTroopBean marchTroopBean);

    MarchTroopBean getTroop();

    long getSoldierAmount();

    boolean isTroopEmpty();

    void returnTroop();

    long getInitialDuration();

    long getDuration();

    long getSpeedupTime();

    long getRemainingTime();

    int getRallyType();

    void setParentMarchId(String parentMarchId);

    String getParentMarchId();

    default March getParentMarch() {
        return this.getManager().getMarch(this.getParentMarchId());
    }

    void addSubMarchId(String subMarchId);

    default void addSubMarch(March subMarch) {
        if (subMarch != null) {
            this.addSubMarchId(subMarch.getId());
        }
    }

    void removeSubMarchId(String subMarchId);

    default void removeSubMarch(March subMarch) {
        if (subMarch != null) {
            this.removeSubMarchId(subMarch.getId());
        }
    }

    List<String> getSubMarchIdList();

    default List<March> getSubMarchList() {
        List<String> subMarchIdList = this.getSubMarchIdList();
        if (CollectionUtil.isEmpty(subMarchIdList)) {
            return Collections.emptyList();
        }
        List<March> result = new ArrayList<>();
        for (String subMarchId : subMarchIdList) {
            March subMarch = this.getManager().getMarch(subMarchId);
            if (subMarch != null) {
                result.add(subMarch);
            }
        }
        return result;
    }

    default boolean isSubMarch() {
        return StringUtils.isNoneBlank(this.getParentMarchId());
    }

    void detach();
}
