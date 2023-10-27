package org.pot.common.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.pot.common.client.ClientDeviceOS;
import org.pot.common.communication.strategy.StrategyVersion;

public class AppVersionUtil {
    private static final int IOS_BIT = 1;
    private static final int ANDROID_BIT = 0;

    public static int compare(String compVersion, String baseVersion) {
        String[] compVersionParts = StringUtils.split(compVersion, ".");
        String[] baseVersionParts = StringUtils.split(baseVersion, ".");
        int length = Math.max(compVersionParts.length, baseVersionParts.length);
        for (int i = 0; i < length; i++) {
            int comp = compVersionParts.length > i ? NumberUtils.toInt(StringUtils.strip(compVersionParts[i])) : 0;
            int base = baseVersionParts.length > i ? NumberUtils.toInt(StringUtils.strip(baseVersionParts[i])) : 0;
            if (comp < base) return -1;
            if (comp > base) return 1;
        }
        return 0;
    }

    public static boolean isEquals(String compVersion, String baseVersion) {
        return compare(compVersion, baseVersion) == 0;
    }

    public static boolean isExamining(StrategyVersion version, String deviceOS) {
        return !isApproved(version, deviceOS);
    }

    public static boolean isPreviewing(StrategyVersion version) {
        return version.getPreView() == 1;
    }

    public static boolean isReleased(StrategyVersion version) {
        return version.getPreView() == 0;
    }

    public static boolean isApproved(StrategyVersion version, String deviceOS) {
        int bit = ClientDeviceOS.IOS.isMatch(deviceOS) ? IOS_BIT : ANDROID_BIT;
        int examine = version.getExamine();
        return (1 & (examine >> bit)) > 0;
    }

    public static boolean isGreatThan(String compVersion, String baseVersion) {
        return compare(compVersion, baseVersion) == 1;
    }

    public static boolean isLessThan(String compVersion, String baseVersion) {
        return compare(compVersion, baseVersion) == -1;
    }

    public static boolean isEqualsOrLessThan(String compVersion, String baseVersion) {
        return !isGreatThan(compVersion, baseVersion);
    }

    public static boolean isEqualsOrGreatThan(String compVersion, String baseVersion) {
        return !isLessThan(compVersion, baseVersion);
    }

}
