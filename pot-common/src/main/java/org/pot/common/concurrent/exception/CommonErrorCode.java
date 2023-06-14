package org.pot.common.concurrent.exception;

public enum CommonErrorCode implements IErrorCode {
    UNKNOWN_ERROR(1000),
    CONNECT_FAIL(1007),
    IDLE_KICK(1021);

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
