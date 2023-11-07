package org.pot.strategy.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.pot.common.client.AppUpdatePolicy;
import org.pot.common.client.ClientDeviceOS;
import org.pot.common.communication.strategy.StrategyDeviceName;
import org.pot.common.communication.strategy.StrategyIp;
import org.pot.common.communication.strategy.StrategyUtil;
import org.pot.common.communication.strategy.StrategyVersion;
import org.pot.common.util.AppVersionUtil;
import org.pot.strategy.beans.GatewayAddress;
import org.pot.strategy.config.StrategyConfiguration;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

@Slf4j
@Service
public class StrategyService {
    @Resource
    StrategyConfiguration strategyConfiguration;

    private volatile GatewayAddress gatewayAddress = new GatewayAddress();

    public void putStrategyInfo(Map<String, Object> map, String requestIp, String deviceOS, String deviceName,
                                String resVersion, String appVersion, String appPackageName) {
        StrategyVersion strategyVersion = StrategyUtil.getVersion(resVersion, appPackageName);
        if (strategyVersion == null) strategyVersion = StrategyUtil.getVersion(appVersion, appPackageName);
        if (strategyVersion != null && AppVersionUtil.isExamining(strategyVersion, deviceOS)) {
            map.put("app_store_examine", true);
            map.put("resource_url", StringUtils.stripToEmpty(strategyVersion.getExamineResourceUrl()));
            map.put("gateway_host", StringUtils.stripToEmpty(strategyVersion.getExamineGatewayHost()));
            map.put("gateway_port", strategyVersion.getExamineGatewayPort());
            putUpdateInfo(map, deviceOS, resVersion, appVersion, appPackageName, v -> AppVersionUtil.isExamining(v, deviceOS));
            return;
        }
        StrategyDeviceName strategyDeviceName = StrategyUtil.getDeviceName(deviceName);
        if (strategyDeviceName != null) {
            map.put("app_store_examine", false);
            map.put("resource_url", StringUtils.stripToEmpty(strategyDeviceName.getResourceUrl()));
            map.put("gateway_host", StringUtils.stripToEmpty(strategyDeviceName.getGatewayHost()));
            map.put("gateway_port", strategyDeviceName.getGatewayPort());
            putUpdateInfo(map, deviceOS, resVersion, appVersion, appPackageName, AppVersionUtil::isPreviewing);
            return;
        }
        StrategyIp strategyIp = StrategyUtil.getIp(requestIp);
        if (strategyIp != null) {
            map.put("app_store_examine", false);
            map.put("resource_url", StringUtils.stripToEmpty(strategyIp.getResourceUrl()));
            map.put("gateway_host", StringUtils.stripToEmpty(strategyIp.getGatewayHost()));
            map.put("gateway_port", strategyIp.getGatewayPort());
            putUpdateInfo(map, deviceOS, resVersion, appVersion, appPackageName, AppVersionUtil::isPreviewing);
            return;
        }
        map.put("app_store_examine", false);
        map.put("resource_url", StringUtils.stripToEmpty(strategyConfiguration.getClientResourceUrl()));
        map.put("gateway_host", StringUtils.stripToEmpty(strategyConfiguration.getClientGatewayHost()));
        map.put("gateway_port", strategyConfiguration.getClientGatewayPort());
        map.put("gateway_ipv4", gatewayAddress.getNearestGatewayIpv4(requestIp));
        putUpdateInfo(map, deviceOS, resVersion, appVersion, appPackageName, v -> AppVersionUtil.isReleased(v) && AppVersionUtil.isApproved(v, deviceOS));
    }

    private void putUpdateInfo(Map<String, Object> map, String deviceOS, String resVersion, String appVersion, String appPackageName, Predicate<StrategyVersion> predicate) {
        appVersion = StringUtils.isBlank(appVersion) ? resVersion : appVersion;
        map.put("app_update_url", ClientDeviceOS.findAppDownloadUrl(deviceOS));
        map.put("app_update_policy", AppUpdatePolicy.NORMAL.ordinal());
        map.put("app_update_app_version", appVersion);
        map.put("app_update_res_version", resVersion);
        map.put("app_update_version", resVersion);
        map.put("app_update_clean", false);
        StrategyVersion latestVersion = StrategyUtil.getLatestVersion(predicate);
        if (latestVersion == null) {
            return;
        }
        if (AppVersionUtil.isLessThan(resVersion, latestVersion.getVersion())) {
            List<StrategyVersion> resVersionRange = StrategyUtil.getVersionRange(resVersion, latestVersion.getVersion(), predicate);
            int resUpdatePolicy = resVersionRange.stream().map(StrategyVersion::getUpgrade).max(Integer::compare).orElse(AppUpdatePolicy.NORMAL.ordinal());
            map.put("app_update_res_version", latestVersion.getVersion());
            map.put("app_update_policy", resUpdatePolicy);
            map.put("app_update_version", latestVersion.getVersion());
        }
        if (AppVersionUtil.isLessThan(appVersion, latestVersion.getVersion())) {
            StrategyVersion appUpdateVersion = null;
            List<StrategyVersion> resVersionRange = StrategyUtil.getVersionRange(resVersion, latestVersion.getVersion(), predicate);
            for (StrategyVersion strategyVersion : resVersionRange) {
                if (strategyVersion.getUpgrade() >= AppUpdatePolicy.RECOMMENDED.ordinal()) {
                    appUpdateVersion = strategyVersion;
                }
                if (strategyVersion.getUpgrade() >= AppUpdatePolicy.FORCED.ordinal()) {
                    appUpdateVersion = strategyVersion;
                    break;
                }
            }
            if (appUpdateVersion != null) {
                map.put("app_update_app_version", appUpdateVersion.getVersion());
                map.put("app_update_policy", appUpdateVersion.getUpgrade());
            }
        }
    }

}
