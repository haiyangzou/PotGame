package org.pot.game.engine.player.common;

import lombok.Getter;
import org.pot.game.engine.player.Player;
import org.pot.game.engine.player.PlayerAgentAdapter;

public class PlayerCommonAgent extends PlayerAgentAdapter {
    @Getter
    private final PlayerCommonInfo playerCommonInfo = new PlayerCommonInfo();

    public PlayerCommonAgent(Player player) {
        super(player);
    }


    public int getLevel() {
        return this.playerCommonInfo.getLevel();
    }

}
