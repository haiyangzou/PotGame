package org.pot.game.engine.enums;

import lombok.Getter;

@Getter
public enum RewardShowType {
    SPRINGBOARD(1),
    FLY(2);
    private final int showType;

    RewardShowType(int showType) {
        this.showType = showType;
    }
}
