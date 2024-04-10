package org.pot.game.engine.enums;

public enum PlayerImageUnlockType {
    UNLOCK(1),
    HERO(2),
    VIP(3),
    POWER(4);
    private int type;

    public int getType() {
        return this.type;
    }

    PlayerImageUnlockType(int type) {
        this.type = type;
    }
}
