package org.pot.game.engine.player.module.army;

import lombok.Getter;
import org.pot.game.engine.march.March;
import org.pot.game.engine.player.Player;
import org.pot.game.engine.player.PlayerAgentAdapter;
import org.pot.game.engine.player.module.tower.TowerMarchObserver;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Getter
public class PlayerArmyAgent extends PlayerAgentAdapter {
    private final Map<Integer,PlayerArmy> playerArmyMap = new ConcurrentHashMap<>();

    public PlayerArmyAgent(Player player) {
        super(player);
    }
}
