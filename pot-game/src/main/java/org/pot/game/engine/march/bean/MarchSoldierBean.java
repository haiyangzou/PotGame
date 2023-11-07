package org.pot.game.engine.march.bean;

import org.pot.message.protocol.world.MarchSoldier;

public class MarchSoldierBean {
    public MarchSoldier buildProtoMessage() {
        MarchSoldier.Builder builder = MarchSoldier.newBuilder();
        return builder.build();
    }
}
