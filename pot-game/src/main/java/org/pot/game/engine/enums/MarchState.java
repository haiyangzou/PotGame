package org.pot.game.engine.enums;

import lombok.Getter;
import org.pot.common.enums.IntEnum;

public enum MarchState implements IntEnum {
    MARCHING(1),
    ARRIVED(2),
    RETURNING(3),
    HOMED(4),
    GATHERING(5),
    ENCAMPING(6),
    REINFORCING(7),
    ASSEMBLING(8),
    GARRISONING(9),
    BUILDING(10),
    DESTROYING(11),
    FOLLOWING(12),
    ;
    @Getter
    int id;

    MarchState(int id) {
        this.id = id;
    }
}
