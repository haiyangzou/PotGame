package org.pot.game.engine.player.module.tower;

import lombok.Getter;
import org.pot.game.engine.player.Player;
import org.pot.game.engine.player.PlayerAgentAdapter;

public class PlayerTowerAgent extends PlayerAgentAdapter {
    @Getter
    private TowerMarchObserver towerMarchObserver;

    public PlayerTowerAgent(Player player) {
        super(player);
    }

    public void sendTotalArmys() {

    }
}
