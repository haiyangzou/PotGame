package org.pot.common.units;

import java.time.temporal.ChronoUnit;

public final class TimeUnitsConst {
    public static final long MILLIS_OF_MINUTE = ChronoUnit.MINUTES.getDuration().toMillis();
    public static final long MILLIS_OF_30_MINUTES = 30 * MILLIS_OF_MINUTE;
    public static final long MILLIS_OF_5_MINUTES = 5 * MILLIS_OF_MINUTE;
    public static final long MILLIS_OF_SECOND = ChronoUnit.SECONDS.getDuration().toMillis();

    public static final long NANOS_OF_MILLISECOND = ChronoUnit.MILLIS.getDuration().toNanos();
}