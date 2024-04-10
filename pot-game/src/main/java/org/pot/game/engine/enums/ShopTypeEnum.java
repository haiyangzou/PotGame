package org.pot.game.engine.enums;

public enum ShopTypeEnum {
    DIAMOND(101);
    private final int type;

    public int getType() {
        return this.type;
    }

    ShopTypeEnum(int type) {
        this.type = type;
    }
}
