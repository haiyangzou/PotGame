package org.pot.common.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;

public enum DateTimeUnit {
    /**
     * 表示1秒概念的单位
     */
    SECOND,
    MINUTE,
    HOUR,
    DAY,
    WEEK,
    MONTH,
    YEAR,
    ;

    public final LocalDate adjust(final LocalDate baseDate, final long amount) {
        if (amount == 0) {
            return baseDate;
        }
        switch (this) {
            case DAY:
                return baseDate.plusDays(amount);
            case WEEK:
                return baseDate.plusWeeks(amount);
            case MONTH:
                return baseDate.plusMonths(amount);
            case YEAR:
                return baseDate.plusYears(amount);
            default:
                throw new IllegalArgumentException(LocalDate.class.getName() + "Cannot Adjust" + this.name());
        }
    }

    public final LocalDateTime adjust(final LocalDateTime baseDateTime, final boolean startWithZero, final long amount) {
        LocalDateTime op = startWithZero ? baseDateTime.withNano(0) : baseDateTime;
        switch (this) {
            case SECOND:
                return op.plusSeconds(amount);
            case MINUTE:
                return startWithZero ? op.withSecond(0).plusMinutes(amount) : op.plusMinutes(amount);
            case HOUR:
                return startWithZero ? op.withMinute(0).withSecond(0).plusHours(amount) : op.plusHours(amount);
            case DAY:
                return startWithZero ? op.withHour(0).withMinute(0).withSecond(0).plusDays(amount) : op.plusDays(amount);
            case WEEK:
                return startWithZero ? op.with(WeekFields.ISO.getFirstDayOfWeek()).withHour(0).withMinute(0).withSecond(0).plusWeeks(amount) : op.plusWeeks(amount);
            case MONTH:
                return startWithZero ? LocalDateTime.of(op.getYear(), op.getMonth(), 1, 0, 0, 0).plusMonths(amount) : op.plusMonths(amount);
            case YEAR:
                return startWithZero ? LocalDateTime.of(op.getYear(), 1, 1, 0, 0, 0).plusYears(amount) : op.plusYears(amount);
            default:
                throw new IllegalArgumentException(LocalDate.class.getName() + "Cannot Adjust" + this.name());
        }
    }
}
