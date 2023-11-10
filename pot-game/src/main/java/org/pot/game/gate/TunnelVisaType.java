package org.pot.game.gate;

import lombok.Getter;
import org.pot.common.enums.IntEnum;

public enum TunnelVisaType implements IntEnum {
    TEMPLE_BATTLE(1),
    ;
    @Getter
    private int id;

    TunnelVisaType(int id) {
        this.id = id;
    }
}
