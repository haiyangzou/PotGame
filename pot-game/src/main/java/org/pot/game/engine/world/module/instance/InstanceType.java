package org.pot.game.engine.world.module.instance;

import lombok.Getter;
import org.pot.common.function.Operation;
import org.pot.game.engine.enums.MapType;

public enum InstanceType {
    BURST_BATTLE(MapType.BURST_BATTLE_MAP, null),
    ;
    @Getter
    private final MapType mapType;
    @Getter
    private final Operation initOperation;

    InstanceType(MapType mapType, Operation initOperation) {
        this.mapType = mapType;
        this.initOperation = initOperation;
    }
}
