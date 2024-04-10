package org.pot.game.engine.enums;

import lombok.Getter;

@Getter
public enum HeroStatus {
    IDLE(0),
    MARCHING(1);

    public final int value;

    HeroStatus(int value) {
        this.value = value;
    }
}
