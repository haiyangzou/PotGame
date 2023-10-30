package org.pot.common.task;

import org.pot.common.util.DateTimeUnit;

public interface PeriodTask {
    void doPeriodicTask();

    DateTimeUnit getDateTimeUnit();

    default long getInterval() {
        return 1L;
    }

    default boolean isDestroyable() {
        return false;
    }

    default boolean isAsync() {
        return false;
    }

    default String getPeriodicTaskName() {
        return this.getClass().getName();
    }
}
