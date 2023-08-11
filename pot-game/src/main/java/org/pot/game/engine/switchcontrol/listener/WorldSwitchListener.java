package org.pot.game.engine.switchcontrol.listener;

import org.pot.game.engine.switchcontrol.ISwitchListener;
import org.pot.game.engine.switchcontrol.SwitchRecord;

public class WorldSwitchListener implements ISwitchListener {
    private final ISwitchListener listener;

    public WorldSwitchListener(ISwitchListener listener) {
        this.listener = listener;
    }

    @Override
    public void onPreviewStart(SwitchRecord switchRecord) {

    }

    @Override
    public void onOpen(SwitchRecord switchRecord) {

    }

    @Override
    public void onClose(SwitchRecord switchRecord) {

    }

    @Override
    public void onShowEnd(SwitchRecord switchRecord) {

    }
}
