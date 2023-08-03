package org.pot.common.util;

import java.math.RoundingMode;
import java.text.NumberFormat;

public class NumberUtil {
    public static String byteCountToDisplaySize(long size) {
        if (size < 1024) {
            return size + "B";
        } else {
            size = size / 1024;
        }
        if (size < 1024) {
            return size + "KB";
        } else {
            size = size / 1024;
        }
        if (size < 1024) {
            size = size * 100;
            return size / 100 + "." + size % 100 + "MB";
        } else {
            size = size * 100 / 1024;
            return size / 100 + "." + size % 100 + "GB";
        }
    }

    public static double convertPrecision(double value, int digits) {
        return convertPrecision(value, digits, RoundingMode.DOWN);
    }

    public static double convertPrecision(double value, int digits, RoundingMode roundingMode) {
        if (digits >= 0) {
            return Double.parseDouble(formatFraction(value, digits, roundingMode));
        }
        throw new IllegalStateException("digits must be positive (" + digits + ")");
    }

    public static String formatFraction(Object number, int digits, RoundingMode roundingMode) {
        return formatFraction(number, digits, digits, roundingMode);
    }

    public static String formatFraction(Object number, int digits) {
        return formatFraction(number, digits, digits, null);
    }

    public static String formatFraction(Object number, int minDigits, int maxDigits) {
        return formatFraction(number, minDigits, maxDigits, null);
    }

    public static String formatFraction(Object number, int minDigits, int maxDigits, RoundingMode roundingMode) {
        return formatNumber(number, -1, -1, minDigits, maxDigits, roundingMode);
    }

    public static String formatNumber(Object number, int minIntDigits, int maxInitDigits, int minFraDigits, int maxFraDigits, RoundingMode roundingMode) {
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setGroupingUsed(false);
        if (minIntDigits > -1) numberFormat.setMinimumIntegerDigits(minIntDigits);
        if (maxInitDigits > -1) numberFormat.setMaximumIntegerDigits(maxInitDigits);
        if (minFraDigits > -1) numberFormat.setMinimumFractionDigits(minFraDigits);
        if (minFraDigits > -1) numberFormat.setMaximumFractionDigits(minFraDigits);
        numberFormat.setRoundingMode(roundingMode == null ? RoundingMode.HALF_EVEN : roundingMode);
        return numberFormat.format(number);

    }
}
