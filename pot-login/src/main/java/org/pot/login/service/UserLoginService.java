package org.pot.login.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.pot.common.Constants;
import org.pot.common.client.AppUpdatePolicy;
import org.pot.common.client.ClientDeviceOS;
import org.pot.common.communication.server.GameServer;
import org.pot.common.communication.strategy.StrategyUtil;
import org.pot.common.communication.strategy.StrategyVersion;
import org.pot.common.concurrent.exception.CommonErrorCode;
import org.pot.common.date.DateTimeUtil;
import org.pot.common.net.ipv4.FireWall;
import org.pot.common.util.AppVersionUtil;
import org.pot.login.beans.UserLoginInfo;
import org.pot.login.beans.VersionInfo;
import org.pot.login.cache.BlockIpDaoCache;
import org.pot.login.cache.GameServerDaoCache;
import org.pot.login.dao.*;
import org.pot.login.entity.*;
import org.pot.login.util.ThreeSevenUtil;
import org.pot.message.protocol.login.LoginReqC2S;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserLoginService {
    @Resource
    private FireWall loginFirewall;
    @Resource
    private UserRoleDao userRoleDao;
    @Resource
    private UniqueIdService uniqueIdService;
    @Resource
    private UserAccountDao userAccountDao;
    @Resource
    private UserAccountRegDao userAccountRegDao;
    @Resource
    private UserAccountLoginLogDao userAccountLoginLogDao;
    @Resource
    private GameServerDaoCache gameServerDaoCache;
    @Resource
    private UserRegisterService userRegisterService;

    @Resource
    private BlockIpDaoCache blockIpDaoCache;

    @Resource
    private UserRoleRegDao userRoleRegDao;

    private boolean firstValidate(UserLoginInfo userLoginInfo) {
        if (userLoginInfo.isDebugRole()) {
            return true;
        }
        if (blockIpDaoCache.selectOne(userLoginInfo.getIp()) != null) {
            return false;
        }
        if (StringUtils.isBlank(userLoginInfo.getLoginReqC2S().getAccount())) {
            userLoginInfo.setErrorCode(CommonErrorCode.INVALID_LOGIN_INFO);
            userLoginInfo.setErrorMessage("blank account");
            return false;
        }
        if (StringUtils.isBlank(userLoginInfo.getLoginReqC2S().getDevice())) {
            userLoginInfo.setErrorCode(CommonErrorCode.INVALID_LOGIN_INFO);
            userLoginInfo.setErrorMessage("blank device");
            return false;
        }
        if (!validateVersion(userLoginInfo)) {
            return false;
        }
        if (Constants.Env.isDebug()) {
            return true;
        }
        final String sign = StringUtils.stripToEmpty(userLoginInfo.getLoginReqC2S().getSign());
        final String account = StringUtils.stripToEmpty(userLoginInfo.getLoginReqC2S().getAccount());
        final String timestamp = StringUtils.stripToEmpty(userLoginInfo.getLoginReqC2S().getTimestamp());
        boolean pass = ThreeSevenUtil.validateSdk(account + timestamp, sign);
        if (!pass) {
            userLoginInfo.setErrorCode(CommonErrorCode.INVALID_LOGIN_INFO);
            userLoginInfo.setErrorMessage("Validate Login Sign fail");
            return false;
        }
        return true;
    }

    private boolean validateVersion(UserLoginInfo userLoginInfo) {
        VersionInfo versionInfo = getVersionInfo(userLoginInfo.getLoginReqC2S());
        userLoginInfo.setAppUpdateUrl(versionInfo.getAppUpdateUrl());
        return true;
    }

    public VersionInfo getVersionInfo(LoginReqC2S loginReqC2S) {
        String appVersion = StringUtils.stripToEmpty(loginReqC2S.getAppVersion());
        String resVersion = StringUtils.stripToEmpty(loginReqC2S.getVersion());
        String packageName = StringUtils.stripToEmpty(loginReqC2S.getAppPackage());
        appVersion = StringUtils.isBlank(appVersion) ? resVersion : appVersion;
        resVersion = StringUtils.isBlank(resVersion) ? appVersion : resVersion;
        String deviceOS = StringUtils.stripToEmpty(loginReqC2S.getDeviceOS());
        VersionInfo versionInfo = new VersionInfo();
        versionInfo.setAppUpdateUrl(ClientDeviceOS.findAppDownloadUrl(deviceOS));
        versionInfo.setAppUpdatePolicy(AppUpdatePolicy.NORMAL.ordinal());
        versionInfo.setAppUpdateVersion(resVersion);
        if (StringUtils.isBlank(appVersion)) {
            versionInfo.setErrorCode(CommonErrorCode.INVALID_LOGIN_INFO);
            versionInfo.setErrorMessage("app version blank");
            return versionInfo;
        }
        if (StringUtils.isBlank(resVersion)) {
            versionInfo.setErrorCode(CommonErrorCode.INVALID_LOGIN_INFO);
            versionInfo.setErrorMessage("res version blank");
            return versionInfo;
        }
        StrategyVersion appVersionStrategy = StrategyUtil.getVersion(appVersion, packageName);
        StrategyVersion resVersionStrategy = StrategyUtil.getVersion(resVersion, packageName);
        boolean appVersionExamining = appVersionStrategy != null && AppVersionUtil.isExamining(appVersionStrategy, deviceOS);
        boolean resVersionExamining = resVersionStrategy != null && AppVersionUtil.isExamining(resVersionStrategy, deviceOS);
        if (appVersionExamining || resVersionExamining) {
            return versionInfo;
        }
        Predicate<StrategyVersion> predicate = v -> AppVersionUtil.isReleased(v) & AppVersionUtil.isApproved(v, deviceOS) && v.isMatchPackageName(packageName);
        StrategyVersion latestVersion = StrategyUtil.getLatestVersion(predicate);
        if (latestVersion == null) {
            return versionInfo;
        }
        if (AppVersionUtil.isLessThan(appVersion, latestVersion.getVersion())) {
            for (StrategyVersion strategyVersion : StrategyUtil.getVersionRange(appVersion, latestVersion.getVersion(), predicate)) {
                if (strategyVersion.getUpgrade() >= AppUpdatePolicy.FORCED.ordinal()) {
                    versionInfo.setAppUpdateVersion(strategyVersion.getVersion());
                    versionInfo.setAppUpdatePolicy(strategyVersion.getUpgrade());
                    versionInfo.setErrorCode(CommonErrorCode.VERSION_LOW);
                    versionInfo.setErrorMessage("app version low");
                    return versionInfo;
                }
            }
        }
        if (AppVersionUtil.isLessThan(resVersion, latestVersion.getVersion())) {
            for (StrategyVersion strategyVersion : StrategyUtil.getVersionRange(resVersion, latestVersion.getVersion(), predicate)) {
                if (strategyVersion.getUpgrade() >= AppUpdatePolicy.PATCHED.ordinal()) {
                    versionInfo.setAppUpdateVersion(strategyVersion.getVersion());
                    versionInfo.setAppUpdatePolicy(strategyVersion.getUpgrade());
                    versionInfo.setErrorCode(CommonErrorCode.VERSION_LOW);
                    versionInfo.setErrorMessage("res version low");
                    return versionInfo;
                }
            }
        }
        return versionInfo;
    }

    private boolean loadAccountOnOntExistsAutoCreate(UserLoginInfo userLoginInfo) {
        UserAccount userAccount = null;
        if (userLoginInfo.isDebugRole()) {
            if (userLoginInfo.getLoginReqC2S().getLoginType() != UserLoginInfo.SPECIFY_ROLE_LOGIN) {
                userLoginInfo.setErrorCode(CommonErrorCode.DEBUG_ROLE_ERROR);
                userLoginInfo.setErrorMessage("Debug role with wrong Login Type");
                return false;
            }
            UserRole userRole = userRoleDao.selectByUid(userLoginInfo.getGameUid());
            if (userRole == null) {
                userLoginInfo.setErrorCode(CommonErrorCode.DEBUG_ROLE_ERROR);
                userLoginInfo.setErrorMessage("Debug Role Not Exists");
                return false;
            }
            userAccount = userAccountDao.getUserAccountByUid(userRole.getAccountUid());
            if (userAccount == null) {
                userLoginInfo.setErrorCode(CommonErrorCode.DEBUG_ROLE_ERROR);
                userLoginInfo.setErrorMessage("Debug Role Not Exists");
                return false;
            }
            userLoginInfo.setUserAccount(userAccount);
            return true;
        }
        if (StringUtils.isNotBlank(userLoginInfo.getLoginReqC2S().getAccount())) {
            userAccount = userAccountDao.getUserAccountByAccount(userLoginInfo.getLoginReqC2S().getAccount());
        }
        boolean insert = false;
        if (userAccount == null) {
            insert = true;
            userAccount = new UserAccount();
            if (userLoginInfo.getServerId() <= 0) {
                userRegisterService.selectMatchedServer(userLoginInfo);
            }
            if (!validateServer(userLoginInfo)) return false;
            userAccount.setUid(uniqueIdService.newUniqueId(userLoginInfo.getServerId()));
            userAccount.setAccount(userLoginInfo.getLoginReqC2S().getAccount());
            userAccount.setGuidePolicy(0);
        }
        userLoginInfo.setUserAccount(userAccount);
        userAccount.setDevice(StringUtils.stripToEmpty(userLoginInfo.getLoginReqC2S().getDevice()));
        userAccount.setAppPackageName(StringUtils.trimToEmpty(userLoginInfo.getLoginReqC2S().getAppPackage()));
        userAccount.setAppId(StringUtils.stripToEmpty(userLoginInfo.getLoginReqC2S().getAppId()));
        userAccount.setAppName(StringUtils.stripToEmpty(userLoginInfo.getLoginReqC2S().getAppName()));
        userAccount.setAppVersion(StringUtils.stripToEmpty(userLoginInfo.getLoginReqC2S().getAppVersion()));
        userAccount.setChannel(StringUtils.stripToEmpty(userLoginInfo.getLoginReqC2S().getChannel()));
        userAccount.setPlatform(StringUtils.stripToEmpty(userLoginInfo.getLoginReqC2S().getPlatform()));
        userAccount.setCountry(StringUtils.stripToEmpty(userLoginInfo.getCountry()));
        userAccount.setLanguage(StringUtils.stripToEmpty(userLoginInfo.getLoginReqC2S().getDeviceLanguage()));
        userAccount.setIp(StringUtils.stripToEmpty(userLoginInfo.getIp()));
        userAccount.setNetwork(StringUtils.stripToEmpty(userLoginInfo.getLoginReqC2S().getNetwork()));
        userAccount.setPhoneModel(StringUtils.stripToEmpty(userLoginInfo.getLoginReqC2S().getPhoneModel()));
        userAccount.setDeviceInfo(StringUtils.stripToEmpty(userLoginInfo.getLoginReqC2S().getDeviceInfo()));
        userAccount.setDeviceOs(StringUtils.stripToEmpty(userLoginInfo.getLoginReqC2S().getDeviceOS()));
        userAccount.setOsVersion(StringUtils.stripToEmpty(userLoginInfo.getLoginReqC2S().getOsVersion()));
        userAccount.setAppsFlyerId(StringUtils.stripToEmpty(userLoginInfo.getLoginReqC2S().getAppsFlyerId()));
        userAccount.setAdvertisingId(StringUtils.stripToEmpty(userLoginInfo.getLoginReqC2S().getAdvertisingId()));
        if (insert) {
            userAccountRegDao.insertOne(new UserAccountReg(userAccount));
            userAccountDao.insertAccount(userAccount);
        } else {
            userAccountDao.updateAccount(userAccount);
        }
        userAccountLoginLogDao.insertOne(new UserAccountLoginLog(userAccount));
        return true;
    }

    private boolean validateServer(UserLoginInfo userLoginInfo) {
        if (userLoginInfo.getServerId() <= 0) {
            userLoginInfo.setErrorCode(CommonErrorCode.INVALID_SERVER_INFO);
            userLoginInfo.setErrorMessage("invalid server");
            return false;
        }
        GameServer gameServer = gameServerDaoCache.selectOne(userLoginInfo.getServerId());
        if (gameServer == null) {
            userLoginInfo.setErrorCode(CommonErrorCode.INVALID_SERVER_INFO);
            userLoginInfo.setErrorMessage("server not exists");
            return false;
        }
        return true;
    }

    private boolean secondaryValidate(UserLoginInfo userLoginInfo) {
        if (userLoginInfo.isDebugRole()) {
            return true;
        }
        Integer banFlag = userLoginInfo.getUserAccount().getBanFlag();
        Long banEndTime = userLoginInfo.getUserAccount().getBanEndTime();
        if (banFlag != null && banFlag == 1 && banEndTime != null && banEndTime > 0) {
            userLoginInfo.setErrorCode(CommonErrorCode.USER_BANNED);
            userLoginInfo.setErrorMessage("account is banned");
            userLoginInfo.setBanEndTime(userLoginInfo.getUserAccount().getBanEndTime());
            return false;
        }
        return true;
    }

    public void loginUser(UserLoginInfo userLoginInfo) {
        if (!firstValidate(userLoginInfo)) return;
        if (!loadAccountOnOntExistsAutoCreate(userLoginInfo)) return;
        if (!secondaryValidate(userLoginInfo)) return;
        List<UserRole> allRoles = Collections.unmodifiableList(userRoleDao.selectByAccountUid(userLoginInfo.getAccountUid()));
        if (userLoginInfo.getLoginReqC2S().getLoginType() == UserLoginInfo.SPECIFY_SERVER_LOGIN) {
            if (!validateServer(userLoginInfo)) return;
            List<UserRole> matchedRoles = allRoles.stream().filter(r -> Objects.equals(r.getServerId(), userLoginInfo.getServerId())).collect(Collectors.toList());
            if (matchedRoles.isEmpty()) {
                userLoginInfo.setNewRole(true);
                userLoginInfo.setGameUid(uniqueIdService.newUniqueId(userLoginInfo.getServerId()));
                insertRoleOnExistUpdateLastLogin(userLoginInfo);
//                setMaintainNotice(userLoginInfo);
                return;
            }
            UserRole matchedRole = userLoginInfo.getGameUid() <= 0
                    ? matchedRoles.stream().findFirst().orElse(null)
                    : matchedRoles.stream().filter(r -> Objects.equals(r.getUid(), userLoginInfo.getGameUid())).findFirst().orElse(null);
            if (matchedRole == null) {
                userLoginInfo.setErrorCode(CommonErrorCode.INVALID_LOGIN_INFO);
                userLoginInfo.setErrorMessage("role not exists");
            } else if (isBannedRole(userLoginInfo, matchedRole)) {
                userLoginInfo.setErrorCode(CommonErrorCode.ROLE_BANNED);
                userLoginInfo.setErrorMessage("role is banned");
                userLoginInfo.setBanEndTime(matchedRole.getBanEndTime());
            } else {
                userLoginInfo.setNewRole(false);
                userLoginInfo.setGameUid(matchedRole.getUid());
                insertRoleOnExistUpdateLastLogin(userLoginInfo);
            }
        } else if (userLoginInfo.getLoginReqC2S().getLoginType() == UserLoginInfo.SPECIFY_ROLE_LOGIN) {
            UserRole matchedRole = allRoles.stream().filter(r -> Objects.equals(r.getUid(), userLoginInfo.getGameUid())).findFirst().orElse(null);
            if (matchedRole == null) {
                userLoginInfo.setErrorCode(CommonErrorCode.INVALID_LOGIN_INFO);
                userLoginInfo.setErrorMessage("role not exists");
            } else if (isBannedRole(userLoginInfo, matchedRole)) {
                userLoginInfo.setErrorCode(CommonErrorCode.ROLE_BANNED);
                userLoginInfo.setErrorMessage("role is banned");
                userLoginInfo.setBanEndTime(matchedRole.getBanEndTime());
            } else {
                userLoginInfo.setNewRole(false);
                userLoginInfo.setGameUid(matchedRole.getUid());
                insertRoleOnExistUpdateLastLogin(userLoginInfo);
            }
        } else {
            if (allRoles.isEmpty()) {
                if (userLoginInfo.getServerId() <= 0) {
                    userRegisterService.selectMatchedServer(userLoginInfo);
                }
                if (!validateServer(userLoginInfo)) return;
                userLoginInfo.setNewRole(true);
                userLoginInfo.setGameUid(uniqueIdService.newUniqueId(userLoginInfo.getServerId()));
                insertRoleOnExistUpdateLastLogin(userLoginInfo);
//                setMaintainNotice(userLoginInfo);
                return;
            }
            if (DateTimeUtil.differentDays(allRoles.get(0).getLastLoginTime(), System.currentTimeMillis()) >= 90) {

            }
            final UserRole latestRole = allRoles.get(0);
            if (isBannedRole(userLoginInfo, latestRole)) {
                userLoginInfo.setErrorCode(CommonErrorCode.ROLE_BANNED);
                userLoginInfo.setErrorMessage("role is banned");
                userLoginInfo.setBanEndTime(latestRole.getBanEndTime());
            } else {
                userLoginInfo.setNewRole(false);
                userLoginInfo.setGameUid(latestRole.getUid());
                userLoginInfo.setServerId(latestRole.getServerId());
                insertRoleOnExistUpdateLastLogin(userLoginInfo);
            }
        }
    }

    private boolean isBannedRole(UserLoginInfo userLoginInfo, UserRole userRole) {
        if (userLoginInfo.isDebugRole()) {
            return false;
        }
        Integer banFlag = userRole.getBanFlag();
        Long banEndTime = userRole.getBanEndTime();
        return banFlag != null && banFlag == 1 && banEndTime != null && banEndTime > 0;
    }

    private void insertRoleOnExistUpdateLastLogin(UserLoginInfo userLoginInfo) {
        UserRole userRole = new UserRole();
        userRole.setUid(userLoginInfo.getGameUid());
        userRole.setAccountUid(userLoginInfo.getUserAccount().getUid());
        userRole.setServerId(userLoginInfo.getServerId());
        userRole.setLastLoginTime(System.currentTimeMillis());
        userRoleDao.insertRoleOnExistUpdateLastLogin(userRole);
        if (userLoginInfo.isNewRole()) {
            userRoleRegDao.insertOne(new UserRoleReg(userLoginInfo));
        }

    }
}
