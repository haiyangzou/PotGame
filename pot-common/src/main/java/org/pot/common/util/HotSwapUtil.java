package org.pot.common.util;

import org.apache.commons.lang3.StringUtils;
import org.pot.common.Constants;

public class HotSwapUtil {
    public static String hotSwap(String version) {
        version = StringUtils.stripToEmpty(version);
        String classFileFolderPath = Constants.Env.getContextPath();
        if (StringUtils.isBlank(version)) {
            classFileFolderPath = classFileFolderPath + "patches";
        } else {
            classFileFolderPath = classFileFolderPath + "patches_" + version;
        }
        return "HotSwapAgent.ho";

    }
}
