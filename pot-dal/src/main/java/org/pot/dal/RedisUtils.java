package org.pot.dal;

import org.apache.commons.lang3.ArrayUtils;

public final class RedisUtils {
    private static final String KEY_SEPARATOR = ":";

    public static String buildRedisKey(String prefix, Object... args) {
        if (ArrayUtils.isEmpty(args)) {
            return prefix;
        }
        StringBuilder sb = new StringBuilder(prefix);
        for (Object arg : args) {
            sb.append(KEY_SEPARATOR).append(arg);
        }
        return sb.toString();
    }
}