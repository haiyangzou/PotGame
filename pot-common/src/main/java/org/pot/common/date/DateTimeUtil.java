package org.pot.common.date;

import org.pot.common.units.TimeUnitsConst;
import org.pot.common.util.DateTimeUnit;
import org.pot.common.util.MathUtil;
import org.pot.common.util.NumberUtil;

import java.sql.Date;
import java.time.*;

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

    public static ZonedDateTime toZonedDateTime(final LocalDateTime localDateTime, final ZoneId zoneId) {
        return localDateTime.atZone(ZoneId.systemDefault()).withZoneSameInstant(zoneId);
    }

    public static ZonedDateTime toZonedDateTime(final ZoneId zoneId) {
        return toZonedDateTime(LocalDateTime.now(), zoneId);
    }

    public static ZonedDateTime toSystemZonedDateTime(final LocalDateTime localDateTime) {
        return toZonedDateTime(localDateTime, ZoneId.systemDefault());
    }

    public static long toMills(final ZonedDateTime zonedDateTime) {
        return zonedDateTime.toInstant().toEpochMilli();
    }

    public static long toMills(final LocalDateTime localDateTime) {
        return toMills(toSystemZonedDateTime(localDateTime));
    }

    public static Date toUtilDate(final Long utcMilli) {
        return utcMilli == null ? null : new Date(utcMilli);
    }

    public static Date toUtilDate(final LocalDateTime localDateTime) {
        return toUtilDate(toMills(localDateTime));
    }

    public static long adjust(final DateTimeUnit dateTimeUnit, final long utcMilli, final boolean startWithZero, final long amount) {
        return toMills(dateTimeUnit.adjust(toLocalDateTime(utcMilli), startWithZero, amount));
    }

    public static int differentDays(LocalDateTime localDateTime1) {
        return differentDays(localDateTime1, LocalDateTime.now());
    }

    public static int differentDays(long utcMilli1, long utcMilli2) {
        if (utcMilli1 == utcMilli2) return 0;
        return differentDays(toLocalDateTime(utcMilli1), toLocalDateTime(utcMilli2));
    }

    public static int differentDays(LocalDateTime localDateTime1, LocalDateTime localDateTime2) {
        LocalDateTime localDate1 = localDateTime1.toLocalDate().atStartOfDay();
        LocalDateTime localDate2 = localDateTime2.toLocalDate().atStartOfDay();
        Duration duration = Duration.between(localDate1, localDate2);
        return MathUtil.getIntValue(duration.toDays());
    }
}
