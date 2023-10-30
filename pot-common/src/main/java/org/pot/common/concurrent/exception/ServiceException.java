package org.pot.common.concurrent.exception;

import lombok.Getter;

public class ServiceException extends RuntimeException {
    @Getter
    private IErrorCode errorCode = CommonErrorCode.UNKNOWN_ERROR;

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(IErrorCode error) {
        super(error.getErrorName());
        this.errorCode = error;
    }

    public ServiceException(IErrorCode error, Throwable cause) {
        super(error.getErrorName(), cause);
        this.errorCode = error;
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}