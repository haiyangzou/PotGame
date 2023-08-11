package org.pot.game.engine.switchcontrol;

public interface ISwitchListener {
    void onPreviewStart(SwitchRecord switchRecord);

    void onOpen(SwitchRecord switchRecord);

    void onClose(SwitchRecord switchRecord);

    void onShowEnd(SwitchRecord switchRecord);
}
