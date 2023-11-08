package org.pot.game.engine.point;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.Setter;
import org.pot.game.engine.enums.PointType;

@Getter
@JsonTypeName("WorldEncampment")
public class PointEncampmentData extends PointExtraData {
    @Setter
    @JsonProperty("playerId")
    private volatile long playerId;
    @Setter
    @JsonProperty("marchId")
    private volatile String marchId;

    public PointEncampmentData(PointType pointType) {
        super(pointType);
    }
}
