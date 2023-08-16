package org.pot.common.util;

import org.pot.common.date.DateTimeUtil;

public class ExDateTimeUtil {
    public static long getDayStart(long based) {
        return DateTimeUtil.adjust(DateTimeUnit.DAY, based, true, 0);
    }

    public static long getCurDayStart() {
        return DateTimeUtil.adjust(DateTimeUnit.DAY, System.currentTimeMillis(), true, 0);
    }

}
