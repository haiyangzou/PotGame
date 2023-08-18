package org.pot.game.engine.point;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeName;
import org.pot.game.engine.enums.PointType;

@Slf4j
@Getter
@JsonTypeName("WorldMonster")
public class PointMonsterData extends PointExtraData {
    @JsonProperty("monsterId")
    private int monsterId;
    @JsonProperty("timeout")
    private long timeout;

    public PointMonsterData(int monsterId) {
        super(PointType.MONSTER);
        this.monsterId = monsterId;
    }
}
