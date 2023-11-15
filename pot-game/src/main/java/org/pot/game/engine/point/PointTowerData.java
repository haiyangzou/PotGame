package org.pot.game.engine.point;

import lombok.Getter;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeName;
import org.pot.game.engine.enums.PointType;
import org.pot.game.engine.world.module.map.scene.WorldBuilding;

@Getter
@JsonTypeName("WorldTower")
public class PointTowerData extends PointExtraData {
    @JsonProperty("worldBuilding")
    private WorldBuilding worldBuilding;

    public PointTowerData(WorldBuilding worldBuilding) {
        super(PointType.TOWER);
        this.worldBuilding = worldBuilding;
    }
}
