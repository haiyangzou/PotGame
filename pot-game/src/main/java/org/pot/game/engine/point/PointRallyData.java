package org.pot.game.engine.point;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeName;
import org.pot.game.engine.enums.PointType;

@Slf4j
@Getter
@JsonTypeName("WorldRally")
public class PointRallyData extends PointExtraData {
    @JsonProperty("rallyId")
    private int rallyId;
    @JsonProperty("timeout")
    private long timeout;

    public PointRallyData(int rallyId) {
        super(PointType.RALLY);
        this.rallyId = rallyId;
    }
}
