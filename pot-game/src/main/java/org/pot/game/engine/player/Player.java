package org.pot.game.engine.player;

import lombok.Getter;

public class Player {
    @Getter
    private final long uid;

    public Player(long uid) {
        this.uid = uid;
    }

}
