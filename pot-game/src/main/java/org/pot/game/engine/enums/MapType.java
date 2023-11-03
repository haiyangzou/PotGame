package org.pot.game.engine.enums;

import lombok.Getter;
import org.pot.common.enums.IntEnum;

public enum MapType implements IntEnum {
    WORLD_MAP(1),
    BURST_BATTLE_MAP(2),
    VOLCANIC_BATTLE_MAP(3),
    ;
    @Getter
    private int id;

    MapType(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }
}
