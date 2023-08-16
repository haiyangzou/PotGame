package org.pot.common.util;

import org.apache.commons.lang3.math.NumberUtils;

public class MathUtil {
    public static int max(final int... data) {
        return NumberUtils.max(data);
    }

    public static long limit(final long number, final long min, final long max) {
        return number < min ? min : number > max ? max : number;
    }

    public static int divideAndCeil(final int a, final int b) {
        return (a / b) + (a % b > 0 ? 1 : 0);
    }
}
