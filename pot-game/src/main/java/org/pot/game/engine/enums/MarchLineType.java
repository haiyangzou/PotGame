package org.pot.game.engine.enums;

import lombok.Getter;
import org.pot.common.enums.IntEnum;

public enum MarchLineType implements IntEnum {
    FRIENDLY(0),
    OTHER(1),
    ENEMY(2),
    ;
    @Getter
    int id;

    MarchLineType(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }
}
