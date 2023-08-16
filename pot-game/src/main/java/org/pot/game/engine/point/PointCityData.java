package org.pot.game.engine.point;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeName;
import org.pot.game.engine.enums.PointType;

@Slf4j
@Getter
@JsonTypeName("WorldCity")
public class PointCityData extends PointExtraData {
    @JsonProperty("playerId")
    private long playerId;

    public PointCityData() {
        super(PointType.CITY);
    }
}
