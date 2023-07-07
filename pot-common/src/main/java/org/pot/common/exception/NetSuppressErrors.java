package org.pot.common.exception;

import org.apache.commons.lang3.StringUtils;
import org.pot.common.concurrent.exception.ExceptionUtil;

public class NetSuppressErrors {
    public static boolean isSuppressed(Throwable cause) {
        String msg = ExceptionUtil.getRootCase(cause).toString();
        if (StringUtils.containsIgnoreCase(msg, "Connection reset by peer")) {
            return true;
        } else if (StringUtils.containsIgnoreCase(msg, "Connection refused")) {
            return true;
        } else if (StringUtils.containsIgnoreCase(msg, "Connection timed out")) {
            return true;
        } else if (StringUtils.containsIgnoreCase(msg, "Connect timed out")) {
            return true;
        } else if (StringUtils.containsIgnoreCase(msg, "magic not match")) {
            return true;
        }
        return false;
    }

    public static String suppress(Throwable cause) {
        String msg = ExceptionUtil.getRootCase(cause).toString();
        if (StringUtils.containsIgnoreCase(msg, "Connection reset by peer")) {
            return replaceNetException(msg);
        } else if (StringUtils.containsIgnoreCase(msg, "Connection refused")) {
            return replaceNetException(msg);
        } else if (StringUtils.containsIgnoreCase(msg, "Connection timed out")) {
            return replaceNetException(msg);
        } else if (StringUtils.containsIgnoreCase(msg, "Connect timed out")) {
            return replaceNetException(msg);
        } else if (StringUtils.containsIgnoreCase(msg, "magic not match")) {
            return replaceNetException(msg);
        }
        return msg;
    }

    private static String replaceNetException(String msg) {
        return StringUtils.replace(msg, "Exception", "ignoredNetErr", 1);
    }
}
