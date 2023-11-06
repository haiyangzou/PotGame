package org.pot.common.communication.strategy;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.pot.common.util.AppVersionUtil;
import org.pot.common.util.CollectionUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class StrategyUtil {
    @Setter
    @Getter
    private static volatile Map<String, Map<String, StrategyVersion>> versionMap = Collections.emptyMap();
    @Setter
    @Getter
    private static volatile List<StrategyVersion> versionList = Collections.emptyList();
    @Setter
    @Getter
    private static volatile Map<String, StrategyDeviceName> deviceNameMap = Collections.emptyMap();
    @Setter
    @Getter
    private static volatile List<StrategyDeviceName> deviceNameList = Collections.emptyList();
    @Setter
    @Getter
    private static volatile Map<String, StrategyIp> ipMap = Collections.emptyMap();
    @Setter
    @Getter
    private static volatile List<StrategyIp> ipList = Collections.emptyList();

    public static StrategyVersion getVersion(String version, String packageName) {
        if (StringUtils.isBlank(version)) {
            return null;
        }
        Map<String, Map<String, StrategyVersion>> map = versionMap;
        if (map != null) {
            Map<String, StrategyVersion> package2versionMap = map.get(version);
            if (package2versionMap != null) {
                StrategyVersion strategy = package2versionMap.get(packageName);
                if (strategy != null) return strategy;
                strategy = package2versionMap.get("");
                if (strategy != null) return strategy;
            }
        }
        List<StrategyVersion> list = versionList;
        for (StrategyVersion strategyVersion : list) {
            if (AppVersionUtil.isEquals(version, strategyVersion.getVersion())) {
                return strategyVersion;
            }
        }
        return null;
    }

    public static StrategyDeviceName getDeviceName(String deviceName) {
        if (StringUtils.isBlank(deviceName)) {
            return null;
        }
        Map<String, StrategyDeviceName> map = deviceNameMap;
        if (map != null) {
            StrategyDeviceName strategyVersion = map.get(deviceName);
            if (strategyVersion != null) return strategyVersion;
        }
        List<StrategyDeviceName> list = deviceNameList;
        for (StrategyDeviceName strategyVersion : list) {
            if (StringUtils.equals(deviceName, strategyVersion.getDeviceName())) {
                return strategyVersion;
            }
        }
        return null;
    }

    public static StrategyIp getIp(String ip) {
        if (StringUtils.isBlank(ip)) {
            return null;
        }
        Map<String, StrategyIp> map = ipMap;
        if (map != null) {
            StrategyIp strategyVersion = map.get(ip);
            if (strategyVersion != null) return strategyVersion;
        }
        List<StrategyIp> list = ipList;
        for (StrategyIp strategyVersion : list) {
            if (StringUtils.equals(ip, strategyVersion.getIp())) {
                return strategyVersion;
            }
        }
        return null;
    }

    public static StrategyVersion getLatestVersion(Predicate<StrategyVersion> predicate) {
        List<StrategyVersion> list = versionList;
        StrategyVersion result = null;
        for (StrategyVersion strategyVersion : list) {
            if (predicate != null && !predicate.test(strategyVersion)) {
                continue;
            }
            if (result == null || AppVersionUtil.isGreatThan(strategyVersion.getVersion(), result.getVersion())) {
                result = strategyVersion;
            }
        }
        return result;
    }

    public static List<StrategyVersion> getVersionRange(String from, String to, Predicate<StrategyVersion> predicate) {
        List<StrategyVersion> list = versionList;
        List<StrategyVersion> result = new ArrayList<>();
        for (StrategyVersion strategyVersion : list) {
            if (predicate != null && !predicate.test(strategyVersion)) {
                continue;
            }
            boolean b1 = AppVersionUtil.isGreatThan(strategyVersion.getVersion(), from);
            boolean b2 = AppVersionUtil.isEqualsOrLessThan(strategyVersion.getVersion(), to);
            if (b1 && b2) {
                result.add(strategyVersion);
            }
        }
        return CollectionUtil.sort(result);
    }
}
