package org.pot.common.concurrent.exception;

import org.apache.commons.lang3.StringUtils;

public interface IErrorCode {
    static int getErrorCode(IErrorCode errorCode) {
        return errorCode == null ? 0 : errorCode.getErrorCode();
    }

    static String getErrorName(IErrorCode errorCode) {
        return errorCode == null ? StringUtils.EMPTY : errorCode.getErrorName();
    }

    int getErrorCode();

    String getErrorName();
}
