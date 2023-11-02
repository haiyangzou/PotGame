package org.pot.common.logback;

import ch.qos.logback.core.joran.spi.NoAutoStart;
import ch.qos.logback.core.rolling.DefaultTimeBasedFileNamingAndTriggeringPolicy;
import org.pot.common.util.ClassUtil;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@NoAutoStart
public class PeriodTimeBasedFileNamingAndTriggeringPolicy<E> extends DefaultTimeBasedFileNamingAndTriggeringPolicy<E> {
    private String type;
    private int periods;

    public PeriodTimeBasedFileNamingAndTriggeringPolicy(String type, int periods) {
        this.type = type;
        this.periods = periods;
    }

    @Override
    public void start() {
        periods = Math.max(periods, 1);
        super.start();
    }

    @Override
    protected void setDateInCurrentPeriod(long now) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(now);
        if (PeriodTimeBasedType.hours.equals(type)) {
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) / periods * periods);
        } else if (PeriodTimeBasedType.minutes.equals(type)) {
            calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) / periods * periods);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
        } else if (PeriodTimeBasedType.seconds.equals(type)) {
            calendar.set(Calendar.MILLISECOND, 0);
            calendar.set(Calendar.SECOND, calendar.get(Calendar.SECOND) / periods * periods);
        }
    }

    @Override
    public void setDateInCurrentPeriod(Date _dateInCurrentPeriod) {
        setDateInCurrentPeriod(_dateInCurrentPeriod);
    }

    @Override
    protected void computeNextCheck() {
        if (PeriodTimeBasedType.hours.equals(type)) {
            nextCheck = dateInCurrentPeriod.getTime() + TimeUnit.HOURS.toMillis(periods);
        } else if (PeriodTimeBasedType.minutes.equals(type)) {
            nextCheck = dateInCurrentPeriod.getTime() + TimeUnit.MINUTES.toMillis(periods);
        } else if (PeriodTimeBasedType.seconds.equals(type)) {
            nextCheck = dateInCurrentPeriod.getTime() + TimeUnit.SECONDS.toMillis(periods);
        } else {
            nextCheck = rc.getEndOfNextNthPeriod(dateInCurrentPeriod, periods).getTime();
        }
    }

    @Override
    public String toString() {
        return ClassUtil.getAbbreviatedName(this.getClass());
    }
}
