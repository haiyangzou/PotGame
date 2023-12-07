package org.pot.game.engine.player.module.event.event;

import lombok.Builder;
import org.pot.game.engine.player.module.event.PlayerEvent;

@Builder
public class SpeedUpTime extends PlayerEvent {
    private final int type;

    private final long speedUpTime;

    SpeedUpTime(int type, long speedUpTime) {
        this.type = type;
        this.speedUpTime = speedUpTime;
    }
}
