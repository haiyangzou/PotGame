package org.pot.common.concurrent.exception;

public enum CommonErrorCode implements IErrorCode {
    UNKNOWN_ERROR(1000),
    INVALID_REQUEST(1001),
    INVALID_OPERATION(1002),
    INVALID_STATE(1003),
    SERVER_MAINTAIN(1004),
    CONFIG_ERROR(1005),
    VERSION_LOW(1006),
    CONNECT_FAIL(1007),
    EXCEED_MAX_CONCURRENT_CONNECTION(1008),
    HANDLER_NOT_FOUND(1009),
    DEBUG_ROLE_ERROR(1010),
    DEVICE_BANNED(1011),
    USER_BANNED(1012),
    ROLE_BANNED(1013),
    INVALID_SERVER_INFO(1014),
    SERVER_MAX_ROLE(1015),
    LOGIN_FAIL(1016),
    ALREADY_LOGIN(1017),
    INVALID_LOGIN_INFO(1018),
    LOGIN_KICK(1019),
    SHUTDOWN_KICK(1020),
    IDLE_KICK(1021),
    GM_KICK(1022),
    INVALID_PARAM(1023),
    REPEAT_REQUEST(1024),
    PERMISSION_DENIED(1025),
    NAME_EXIST(1026),
    BATTLE_POWER_LIMIT(1027),
    OFFLINE(1028),
    RECONNECT_FAIL(1029);;
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
