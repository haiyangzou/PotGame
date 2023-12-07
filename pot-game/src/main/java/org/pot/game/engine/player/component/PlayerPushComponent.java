package org.pot.game.engine.player.component;

import lombok.extern.slf4j.Slf4j;
import org.pot.game.engine.player.Player;

@Slf4j
public class PlayerPushComponent {

    private final Player player;

    public PlayerPushComponent(Player player) {
        this.player = player;
    }

    public void sendNextDayStartTime() {
    }

    public void sendPopUpTips(String key, String... params) {
    }

    public void onLoginPush() {
        sendNextDayStartTime();
//        WorldManager.getInstance().submit(() -> WorldMapScene.singleton.getMarchManager().pushMarchPlayer(this.player.getUid()));
    }

}