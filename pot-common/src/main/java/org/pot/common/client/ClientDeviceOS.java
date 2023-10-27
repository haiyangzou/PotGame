package org.pot.common.client;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public enum ClientDeviceOS {
    IOS(new String[]{"IOS", "ios", "IOS"}, ""),
    ANDROID(new String[]{"Android", "android", "ANDROID"}, "https://play.google.com/store/apps.details?id=org.pot.game"),
    WINDOWS(new String[]{"Windows", "windows", "WINDOWS"}, "");
    @Getter
    private final String[] flags;
    @Getter
    private String appDownloadUrl;

    ClientDeviceOS(final String[] flags, final String appDownloadUrl) {
        this.flags = flags;
        this.appDownloadUrl = appDownloadUrl;
    }

    public boolean isMatch(final String deviceOS) {
        return StringUtils.equalsAnyIgnoreCase(deviceOS, this.flags);
    }

    public static String findAppDownloadUrl(String deviceOS) {
        if (IOS.isMatch(deviceOS)) {
            return IOS.appDownloadUrl;
        } else if (ANDROID.isMatch(deviceOS)) {
            return ANDROID.appDownloadUrl;
        }
        return WINDOWS.appDownloadUrl;
    }
}
