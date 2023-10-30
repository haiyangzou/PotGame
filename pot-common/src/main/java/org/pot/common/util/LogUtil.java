package org.pot.common.util;

import org.slf4j.helpers.MessageFormatter;


public class LogUtil {
    private LogUtil() {

    }

    public static String format(String pattern, Object... objects) {
        return MessageFormatter.arrayFormat(pattern, objects).getMessage();
    }
}
