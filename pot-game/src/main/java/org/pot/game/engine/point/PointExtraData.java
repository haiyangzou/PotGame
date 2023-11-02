package org.pot.game.engine.point;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import org.pot.common.databind.json.JsonObject;
import org.pot.game.engine.enums.PointType;
import org.pot.game.engine.march.MarchManager;
import org.pot.game.engine.scene.AbstractScene;
import org.pot.game.engine.scene.WorldPoint;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
public abstract class PointExtraData implements JsonObject, Serializable {
    @Getter
    @JsonIgnore
    protected final PointType pointType;
    @Getter
    protected volatile AbstractScene scene;
    @JsonIgnore
    protected final CopyOnWriteArrayList<String> marchIdList = new CopyOnWriteArrayList<>();

    public PointExtraData(PointType pointType) {
        this.pointType = pointType;
    }

    public void validate(WorldPoint worldPoint) {
        if (scene == null) return;
        MarchManager marchManager = scene.getMarchManager();
        marchIdList.removeIf(marchId -> marchManager.getMarch(marchId) == null);
    }

    public void setScene(AbstractScene scene) {
        this.scene = Objects.requireNonNull(scene);
    }

    public List<String> getMarchIdList() {
        return marchIdList;
    }

    public void addMarchId(String marchId) {
        marchIdList.addIfAbsent(marchId);
    }

    public void removeMarchId(String marchId) {
        marchIdList.remove(marchId);
    }

    public boolean isClientVisible() {
        return true;
    }

}
