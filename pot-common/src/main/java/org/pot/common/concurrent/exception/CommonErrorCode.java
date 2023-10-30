package org.pot.common.concurrent.exception;

public enum CommonErrorCode implements IErrorCode {
    UNKNOWN_ERROR(1000),
    INVALID_REQUEST(1001),
    VERSION_LOW(1006),
    CONNECT_FAIL(1007),
    IDLE_KICK(1021),
    LOGIN_KICK(1019),
    SERVER_MAINTAIN(1004),
    LOGIN_FAIL(1016),
    ;
    public final int errorCode;

    CommonErrorCode(int errorCode) {
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
