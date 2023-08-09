package org.pot.game.engine.switchcontrol.listener;

import org.pot.game.engine.switchcontrol.ISwitchListener;

public class WorldSwitchListener implements ISwitchListener {
    private final ISwitchListener listener;

    public WorldSwitchListener(ISwitchListener listener) {
        this.listener = listener;
    }
}
