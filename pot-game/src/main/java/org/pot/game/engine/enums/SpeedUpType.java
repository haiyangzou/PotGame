package org.pot.game.engine.enums;

import lombok.Getter;

@Getter
public enum SpeedUpType {
    Building(1),
    Train(2),
    Tech(3),
    Treat(4);

    private final int type;

    SpeedUpType(int type) {
        this.type = type;
    }

    public static SpeedUpType getSpeedUpType(QueueType queueType) {
        if (queueType == QueueType.BUILDING)
            return Building;
        if (queueType == QueueType.TRAIN)
            return Train;
        if (queueType == QueueType.RESEARCH)
            return Tech;
        return Treat;
    }
}
