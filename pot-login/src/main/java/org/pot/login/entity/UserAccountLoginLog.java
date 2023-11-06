package org.pot.login.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.pot.login.domain.object.UserAccount;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAccountLoginLog implements Serializable {
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

    public UserAccountLoginLog(UserAccount userAccount) {
        this.accountUid = userAccount.getUid();
        this.account = userAccount.getAccount();
        this.device = userAccount.getDevice();
        this.appId = userAccount.getAppId();
        this.appName = userAccount.getAppName();
        this.appVersion = userAccount.getAppVersion();
        this.appPackageName = userAccount.getAppPackageName();
        this.channel = userAccount.getChannel();
        this.platform = userAccount.getPlatform();
        this.country = userAccount.getCountry();
        this.language = userAccount.getLanguage();
        this.ip = userAccount.getIp();
        this.network = userAccount.getNetwork();
        this.phoneModel = userAccount.getPhoneModel();
        this.deviceInfo = userAccount.getDeviceInfo();
        this.deviceOs = userAccount.getDeviceOs();
        this.osVersion = userAccount.getOsVersion();
        this.appsFlyerId = userAccount.getAppsFlyerId();
        this.advertisingId = userAccount.getAdvertisingId();
    }
}
