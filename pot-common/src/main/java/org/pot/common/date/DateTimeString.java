package org.pot.common.date;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public enum DateTimeString {
    DATA_TIME_MILLIS_ZONE(false, "yyy-MM-dd HH:mm:ss,SSS Z"),
    TIME(true, "HH:mm:ss"),
    DATE_TIME(true, "yyy-MM-dd HH:mm:ss"),
    DATE_NONE_SEPARATOR(true, "yyyMMdd"),

    DATE(true, "yyy-MM-dd");

    private final boolean local;
    private final DateTimeFormatter formatter;

    DateTimeString(final boolean local, final String formatterString) {
        this.local = local;
        this.formatter = DateTimeFormatter.ofPattern(formatterString);
    }

    public final String toString(final LocalDateTime localDateTime) {
        if (local) {
            return localDateTime.format(formatter);
        } else {
            return localDateTime.atZone(ZoneId.systemDefault()).format(formatter);
        }
    }

    public final String toString(final Date date) {
        return toString(DateTimeUtil.toLocalDateTime(date));
    }

    public final String toString(final Long date) {
        return toString(DateTimeUtil.toLocalDateTime(date));
    }

    public final LocalDateTime toDateTime(final CharSequence dateTimeCharSequence) {
        if (this == DATE || this == DATE_NONE_SEPARATOR) {
            return LocalDate.parse(dateTimeCharSequence, formatter).atStartOfDay();
        }
        if (local) {
            return LocalDateTime.parse(dateTimeCharSequence, formatter);
        } else {
            return ZonedDateTime.parse(dateTimeCharSequence, formatter).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
        }
    }

    public final Date toUtilDate(final CharSequence dateTimeCharSequence) {
        return DateTimeUtil.toUtilDate(toDateTime(dateTimeCharSequence));
    }

    public final long toTimestamp(final CharSequence dateTimeCharSequence) {
        return toUtilDate(dateTimeCharSequence).getTime();
    }
}