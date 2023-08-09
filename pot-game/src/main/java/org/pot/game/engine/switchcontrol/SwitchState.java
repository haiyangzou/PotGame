package org.pot.game.engine.switchcontrol;

import lombok.Getter;

public enum SwitchState {
    PREVIEWING(0),
    OPENING(1),
    SHOWING(0),
    CLOSED(0),
    ;
    @Getter
    private int status;

    SwitchState(int status) {
        this.status = status;
    }
}
