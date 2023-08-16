package org.pot.game.engine.point;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import org.pot.common.databind.json.JsonObject;
import org.pot.game.engine.enums.PointType;
import org.pot.game.engine.scene.AbstractScene;

import java.io.Serializable;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
public abstract class PointExtraData implements JsonObject, Serializable {
    @Getter
    @JsonIgnore
    protected final PointType pointType;
    @Getter
    protected volatile AbstractScene scene;

    public PointExtraData(PointType pointType) {
        this.pointType = pointType;
    }
}
