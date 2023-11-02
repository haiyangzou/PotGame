package org.pot.common.util;

import org.apache.commons.lang3.math.NumberUtils;

public class MathUtil {
    public static int max(final int... data) {
        return NumberUtils.max(data);
    }

    public static long limit(final long number, final long min, final long max) {
        return number < min ? min : number > max ? max : number;
    }

    public static int limit(final int number, final int min, final int max) {
        return number < min ? min : number > max ? max : number;
    }

    public static int divideAndCeil(final int a, final int b) {
        return (a / b) + (a % b > 0 ? 1 : 0);
    }

    public static int getIntValue(final double value) {
        if (value >= Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        } else if (value <= Integer.MIN_VALUE) {
            return Integer.MIN_VALUE;
        }
        return (int) value;
    }
}
