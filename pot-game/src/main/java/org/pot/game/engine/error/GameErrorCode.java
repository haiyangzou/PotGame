package org.pot.game.engine.error;

import org.pot.common.concurrent.exception.IErrorCode;

public enum GameErrorCode implements IErrorCode {
    UNLOCK(100_00),
    PLAYER_NOT_FOUND(100_06),

    MARCH_NOT_EXISTS(810_00),
    ;
    public final int errorCode;

    GameErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public int getErrorCode() {
        return errorCode;
    }

    @Override
    public String getErrorName() {
        return name();
    }
}
