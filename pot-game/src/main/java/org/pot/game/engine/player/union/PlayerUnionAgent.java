package org.pot.game.engine.player.union;

import org.pot.game.engine.player.Player;
import org.pot.game.engine.player.PlayerAgentAdapter;

public class PlayerUnionAgent extends PlayerAgentAdapter {
    public PlayerUnionAgent(Player player) {
        super(player);
    }

    public int getUnionId() {
        return player.getProfile().getUnionId();
    }
}
