package org.pot.login.beans;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.pot.common.http.HttpResult;

@RequiredArgsConstructor
public enum ServiceError {
    UNKNOWN_ERROR(9001),
    SIGNATURE_ERROR(9002),
    PARAMETER_ERROR(9003),
    SERVER_INFO_NOT_FOUND_ERROR(9004),
    ;
    @Getter
    public final int code;

    public void warp(HttpResult httpResult) {
        httpResult.setSuccess(false);
        httpResult.setErrorCode(this.code);
        httpResult.setErrorMessage(this.name());
    }
}
