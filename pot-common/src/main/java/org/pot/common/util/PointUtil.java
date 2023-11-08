package org.pot.common.util;

import org.pot.common.geometry.GeometryUtil;

public class PointUtil {
    private static final int POINT_OFFSET = 30000;
    public static final int INVALID_POINT_ID = -1;

    public static int getPointId(int x, int y) {
        return ((x + POINT_OFFSET) << 16 | (y + POINT_OFFSET) & 0xFFFF);
    }

    public static int getPointX(int pointId) {
        return (pointId >>> 16) - POINT_OFFSET;
    }

    public static int getPointY(int pointId) {
        return (pointId & 0xFFFF) - POINT_OFFSET;
    }

    public static double calcPointDistance(int startPoint, int endPoint) {
        int sx = PointUtil.getPointX(startPoint);
        int sy = PointUtil.getPointY(startPoint);
        int ex = PointUtil.getPointX(endPoint);
        int ey = PointUtil.getPointY(endPoint);
        return calcPointDistance(sx, sy, ex, ey);
    }

    public static double calcPointDistance(double startPointX, double startPointY, double endPointX, double endPointY) {
        return GeometryUtil.calculateDistance2D(startPointX, startPointY, endPointX, endPointY);
    }
}
