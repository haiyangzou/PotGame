package org.pot.game.engine.enums;

import lombok.Getter;
import org.pot.common.enums.IntEnum;

public enum MarchType implements IntEnum {
    ATTACK(3, false),
    ;
    @Getter
    int id;
    @Getter
    boolean overLimit;

    MarchType(int id, boolean overLimit) {
        this.id = id;
        this.overLimit = overLimit;
    }
}
