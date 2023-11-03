package org.pot.game.engine.march;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.pot.common.databind.json.JsonObject;
import org.pot.game.engine.enums.MarchState;
import org.pot.game.engine.enums.MarchType;
import org.pot.game.engine.enums.PointType;
import org.pot.game.engine.point.PointExtraData;
import org.pot.message.protocol.world.WorldMarchInfo;

import java.io.Serializable;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
public interface March extends JsonObject, Serializable {
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
}
