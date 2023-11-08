package org.pot.game.engine.march.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.pot.game.engine.march.MarchUtil;
import org.pot.game.resource.army.SoldierAttr;
import org.pot.game.resource.army.SoldierAttrConfig;
import org.pot.message.protocol.world.MarchSoldier;

@Getter
public class MarchSoldierBean {
    @JsonProperty("soldierId")
    private int soldierId;
    @JsonProperty("soldierAmount")
    private int soldierAmount;

    public int getSpeed() {
        SoldierAttr soldierAttr = getSoldierAttr();
        return soldierAttr == null ? MarchUtil.DEFAULT_MARCH_SPEED : soldierAttr.getSpeed();
    }

    public MarchSoldier buildProtoMessage() {
        MarchSoldier.Builder builder = MarchSoldier.newBuilder();
        return builder.build();
    }

    private SoldierAttr getSoldierAttr() {
        return SoldierAttrConfig.getInstance().getSpec(soldierId);
    }

    public long getPower() {
        return soldierAmount * getSoldierAttr().getAbility();
    }

    public long getLoad() {
        return 0;
    }
}
