package org.pot.login.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.pot.login.beans.UserLoginInfo;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRoleReg implements Serializable {
    private Long gameUid;
    private Long accountUid;
    private String account;
    private String device;
    private String appId;
    private String appName;
    private String appVersion;
    private String appPackageName;
    private String channel;
    private String platform;
    private String country;
    private String language;
    private String ip;
    private String network;
    private String phoneModel;
    private String deviceInfo;
    private String deviceOs;
    private String osVersion;
    private String appsFlyerId;
    private String advertisingId;
    private Date createTime;
    private Date updateTime;

    public UserRoleReg(UserLoginInfo userLoginInfo) {
        this.gameUid = userLoginInfo.getGameUid();
        this.accountUid = userLoginInfo.getUserAccount().getUid();
        this.account = userLoginInfo.getUserAccount().getAccount();
        this.device = userLoginInfo.getUserAccount().getDevice();
        this.appId = userLoginInfo.getUserAccount().getAppId();
        this.appName = userLoginInfo.getUserAccount().getAppName();
        this.appVersion = userLoginInfo.getUserAccount().getAppVersion();
        this.appPackageName = userLoginInfo.getUserAccount().getAppPackageName();
        this.channel = userLoginInfo.getUserAccount().getChannel();
        this.platform = userLoginInfo.getUserAccount().getPlatform();
        this.country = userLoginInfo.getUserAccount().getCountry();
        this.language = userLoginInfo.getUserAccount().getLanguage();
        this.ip = userLoginInfo.getUserAccount().getIp();
        this.network = userLoginInfo.getUserAccount().getNetwork();
        this.phoneModel = userLoginInfo.getUserAccount().getPhoneModel();
        this.deviceInfo = userLoginInfo.getUserAccount().getDeviceInfo();
        this.deviceOs = userLoginInfo.getUserAccount().getDeviceOs();
        this.osVersion = userLoginInfo.getUserAccount().getOsVersion();
        this.appsFlyerId = userLoginInfo.getUserAccount().getAppsFlyerId();
        this.advertisingId = userLoginInfo.getUserAccount().getAdvertisingId();
    }
}
