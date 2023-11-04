package org.pot.login.beans;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.pot.common.concurrent.exception.IErrorCode;
import org.pot.core.util.GeoIpUtil;
import org.pot.login.domain.object.UserAccount;
import org.pot.message.protocol.login.LoginReqC2S;

@Setter
@Getter
public class UserLoginInfo {
    private static final int DEFAULT_LOGIN = 0;
    private static final int SPECIFY_SERVER_LOGIN = 1;
    private static final int SPECIFY_ROLE_LOGIN = 2;
    private final LoginReqC2S loginReqC2S;
    private final String ip;
    private final String country;
    private final String deviceLanguage;
    private long accountUid;
    private int serverId;
    private long gameUid;
    private IErrorCode iErrorCode;
    private String errorMessage;
    private UserAccount userAccount;
    private boolean newRole;

    public UserLoginInfo(LoginReqC2S loginReqC2S, int serverId, String ip) {
        this.loginReqC2S = loginReqC2S;
        this.ip = ip;
        this.serverId = serverId;
        this.country = GeoIpUtil.getCountryIsoCode(ip);
        this.deviceLanguage = StringUtils.stripToEmpty(loginReqC2S.getDeviceLanguage());
        this.gameUid = loginReqC2S.getGameUid();
    }
}
