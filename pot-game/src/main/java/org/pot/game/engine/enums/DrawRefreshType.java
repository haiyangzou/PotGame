package org.pot.game.engine.enums;

public enum DrawRefreshType {
    INITIAL_FREE(1),
    INITIAL_NOT_FREE(2);

    final int type;

    public int getType() {
        return this.type;
    }

    DrawRefreshType(int type) {
        this.type = type;
    }
}
