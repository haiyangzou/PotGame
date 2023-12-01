package org.pot.game.engine.player.module.event.handler;

import org.pot.game.engine.player.Player;
import org.pot.game.engine.player.module.event.PlayerEventHandler;
import org.pot.game.engine.player.module.event.event.PlayerPowerChange;

public class PlayerPowerChangeEventHandler extends PlayerEventHandler<PlayerPowerChange> {
    @Override
    public void handlerEvent(PlayerPowerChange event) throws Exception {
        Player player = event.getPlayer();
        long newPower = event.getNewPower();
        long oldPower = event.getOldPower();
        long changedPower = newPower - oldPower;
    }
}
