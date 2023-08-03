package org.pot.common.date;

import org.pot.common.units.TimeUnitsConst;
import org.pot.common.util.NumberUtil;

import java.sql.Date;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class DateTimeUtil {
    public static LocalDateTime toLocalDateTime(long date) {
        return toLocalDateTime(Instant.ofEpochMilli(date), ZoneId.systemDefault());
    }

    public static long getUnixTimestamp() {
        return toUnixTimestamp(System.currentTimeMillis());
    }

    public static long toUnixTimestamp(long time) {
        return time / TimeUnitsConst.MILLIS_OF_SECOND;
    }

    public static LocalDateTime toLocalDateTime(final Instant date) {
        return toLocalDateTime(date, ZoneId.systemDefault());
    }

    public static LocalDateTime toLocalDateTime(final Date date, final ZoneId zoneId) {
        return toLocalDateTime(date, zoneId);
    }

    public static LocalDateTime toLocalDateTime(final Date date) {
        return toLocalDateTime(date, ZoneId.systemDefault());
    }

    public static LocalDateTime toLocalDateTime(final Instant instant, final ZoneId zoneId) {
        return LocalDateTime.ofInstant(instant, zoneId);
    }

    public static double computeNanosToMillis(final long nanoseconds) {
        double nanosecondsPerMillisecond = TimeUnitsConst.NANOS_OF_MILLISECOND;
        double milliseconds = nanoseconds / nanosecondsPerMillisecond;
        return NumberUtil.convertPrecision(milliseconds, 3);
    }
}
