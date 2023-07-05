package org.pot.common.units;

import java.time.temporal.ChronoUnit;

public final class TimeUnitsConst {
    public static final long MILLIS_OF_MINUTE = ChronoUnit.MINUTES.getDuration().toMillis();
    public static final long MILLIS_OF_30_MINUTES = 30 * MILLIS_OF_MINUTE;
}