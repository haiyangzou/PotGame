package org.pot.game.engine.enums;

import lombok.Getter;

@Getter
public enum StatisticsItemEnum implements IStatisticsEnum {
    action(1);

    private final int type;

    StatisticsItemEnum(int type) {
        this.type = type;
    }

    public String getStatisticsName() {
        return name();
    }

}

