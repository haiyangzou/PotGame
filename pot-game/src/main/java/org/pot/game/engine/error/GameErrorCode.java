package org.pot.game.engine.error;

import org.pot.common.concurrent.exception.IErrorCode;

public enum GameErrorCode implements IErrorCode {
    UNLOCK(100_00),
    PLAYER_NOT_FOUND(100_06),

    MARCH_NOT_EXISTS(810_00),

    ILLEGAL_CHAR(10103),
    CHAT_CONTENT_LIMIT(21301),
    CHAT_UNION_NO_EXISTS(21300),
    CHAT_SEND_TIME_LIMIT(21302),
    CHAT_SEND_SAME_CONTENT_TIME_LIMIT(21304),
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
