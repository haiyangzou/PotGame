package org.pot.login.beans;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.pot.common.client.AppUpdatePolicy;
import org.pot.common.concurrent.exception.IErrorCode;

@Getter
@Setter
public class VersionInfo {
    private IErrorCode errorCode = null;
    private String errorMessage = null;
    private int appUpdatePolicy = AppUpdatePolicy.NORMAL.ordinal();
    private String appUpdateVersion = StringUtils.EMPTY;
    private String appUpdateUrl = StringUtils.EMPTY;
}
