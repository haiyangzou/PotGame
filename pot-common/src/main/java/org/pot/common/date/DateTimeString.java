package org.pot.common.date;

import java.sql.Date;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public enum DateTimeString {
    DATA_TIME_MILLIS_ZONE(false, "yyy-MM-dd HH:mm:ss,SSS Z");

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

    public final String toString(final Long date){
        return toString(DateTimeUtil.toLocalDateTime(date));
    }
}