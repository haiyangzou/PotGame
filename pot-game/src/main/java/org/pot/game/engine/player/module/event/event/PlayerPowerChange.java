package org.pot.game.engine.player.module.event.event;

import lombok.Builder;
import lombok.Getter;
import org.pot.game.engine.player.module.event.PlayerEvent;

@Builder
@Getter
public class PlayerPowerChange extends PlayerEvent {
    private final long newPower;

    private final long oldPower;

}
