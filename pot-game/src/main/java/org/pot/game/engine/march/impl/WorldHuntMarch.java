package org.pot.game.engine.march.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.pot.game.engine.enums.MarchType;
import org.pot.game.engine.march.BaseMarch;
import org.pot.game.engine.march.MarchManager;
import org.pot.game.engine.march.bean.MarchTroopBean;
import org.pot.message.protocol.world.WorldMarchInfo;

@JsonTypeName("WorldHuntMarch")
public class WorldHuntMarch extends BaseMarch {
    @JsonProperty("expendPower")
    private long expendPower;

    public WorldHuntMarch(MarchManager marchManager, long ownerId, int sourcePoint, int targetPoint, MarchTroopBean marchTroopBean, long expendPower) {
        super(marchManager, MarchType.ATTACK, sourcePoint, targetPoint);
        this.expendPower = expendPower;
    }

    @Override
    public WorldMarchInfo.Builder buildWorldMarchInfo(long viewerId) {
        return null;
    }
}
