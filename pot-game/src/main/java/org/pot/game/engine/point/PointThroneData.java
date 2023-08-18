package org.pot.game.engine.point;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeName;
import org.pot.game.engine.enums.PointType;
import org.pot.game.engine.world.module.map.scene.WorldBuilding;

@Slf4j
@Getter
@JsonTypeName("WorldThrone")
public class PointThroneData extends PointExtraData {
    @JsonProperty("worldBuilding")
    private WorldBuilding worldBuilding;

    public PointThroneData(WorldBuilding worldBuilding) {
        super(PointType.THRONE);
        this.worldBuilding = worldBuilding;
    }
}
