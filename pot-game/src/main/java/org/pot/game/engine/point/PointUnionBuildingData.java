package org.pot.game.engine.point;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.pot.game.engine.enums.PointType;

@Slf4j
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
public class PointUnionBuildingData extends PointExtraData {
    @Getter
    @JsonProperty("unionId")
    protected int unionId;
    @Getter
    @JsonProperty("buildingId")
    protected int buildingId;
    @Getter
    @JsonProperty("hp")
    protected double hp;
    @Getter
    @JsonProperty("leaderId")
    protected long leaderId;

    public PointUnionBuildingData(PointType pointType) {
        super(pointType);
    }
}
