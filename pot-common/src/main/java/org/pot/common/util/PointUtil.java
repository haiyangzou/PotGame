package org.pot.common.util;

public class PointUtil {
    private static final int POINT_OFFSET = 30000;

    public static int getPointId(int x, int y) {
        return ((x + POINT_OFFSET) << 16 | (y + POINT_OFFSET) & 0xFFFF);
    }

    public static int getPointX(int pointId) {
        return (pointId >>> 16) - POINT_OFFSET;
    }

    public static int getPointY(int pointId) {
        return (pointId & 0xFFFF) - POINT_OFFSET;
    }
}
