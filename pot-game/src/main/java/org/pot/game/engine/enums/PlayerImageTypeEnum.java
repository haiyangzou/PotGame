package org.pot.game.engine.enums;

public enum PlayerImageTypeEnum {
    ICON(1),

    FRAME(2);

    private int type;

    public int getType() {
        return this.type;
    }

    PlayerImageTypeEnum(int type) {
        this.type = type;
    }
}
