package org.pot.common.id;

public class UniqueIdUtil {
    private static final int SERVER_ID_BITS = 13;

    public static long getUid(long uniqueId) {
        return uniqueId >> SERVER_ID_BITS;
    }

    public static int index(long uniqueId, int buckets) {
        long uid = UniqueIdUtil.getUid(uniqueId) >> 3;
        return (int) Math.abs(uid % buckets);
    }
}
