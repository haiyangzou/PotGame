package org.pot.common.concurrent.exception;

import lombok.Getter;

public class ServiceException extends RuntimeException {
    @Getter
    private IErrorCode errorCode = CommonErrorCode.UNKNOWN_ERROR;

    public ServiceException(String message) {
        super(message);
    }
}