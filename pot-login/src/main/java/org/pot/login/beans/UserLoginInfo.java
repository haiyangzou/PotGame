package org.pot.login.beans;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.pot.common.client.AppUpdatePolicy;
import org.pot.common.concurrent.exception.IErrorCode;
import org.pot.core.util.GeoIpUtil;
import org.pot.login.entity.UserAccount;
import org.pot.message.protocol.login.LoginReqC2S;

@Setter
@Getter
public class UserLoginInfo {
    public static final int DEFAULT_LOGIN = 0;
    public static final int SPECIFY_SERVER_LOGIN = 1;
    public static final int SPECIFY_ROLE_LOGIN = 2;
    private final LoginReqC2S loginReqC2S;
    private final boolean debugRole;
    private final String ip;
    private final String country;
    private final String deviceLanguage;
    private long accountUid;
    private int serverId;
    private long gameUid;
    private IErrorCode errorCode;
    private String errorMessage;
    private UserAccount userAccount;
    private boolean newRole;
    private int guidePolicy;
    private int appUpdatePolicy = AppUpdatePolicy.NORMAL.ordinal();
    private String appUpdateVersion = StringUtils.EMPTY;
    private String appUpdateUrl = StringUtils.EMPTY;
    private boolean maintain;
    private String maintainNoticeTitle = StringUtils.EMPTY;
    private String maintainNoticeDetail = StringUtils.EMPTY;
    private int maintainRemainingTime;
    private long banEndTime;


    public UserLoginInfo(LoginReqC2S loginReqC2S, boolean debugRole, int serverId, String ip) {
        this.loginReqC2S = loginReqC2S;
        this.ip = ip;
        this.serverId = serverId;
        this.country = GeoIpUtil.getCountryIsoCode(ip);
        this.deviceLanguage = StringUtils.stripToEmpty(loginReqC2S.getDeviceLanguage());
        this.gameUid = loginReqC2S.getGameUid();
        this.debugRole = debugRole;
    }
}
