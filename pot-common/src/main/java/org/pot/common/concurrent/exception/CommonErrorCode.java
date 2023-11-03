package org.pot.common.concurrent.exception;

public enum CommonErrorCode implements IErrorCode {
    UNKNOWN_ERROR(1000),
    INVALID_REQUEST(1001),
    SERVER_MAINTAIN(1004),
    VERSION_LOW(1006),
    CONNECT_FAIL(1007),
    HANDLER_NOT_FOUND(1009),
    IDLE_KICK(1021),
    LOGIN_KICK(1019),
    LOGIN_FAIL(1016),
    ALREADY_LOGIN(1017),
    SHUTDOWN_KICK(1020),
    RECONNECT_FAIL(1029),
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
