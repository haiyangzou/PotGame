package org.pot.common.geometry;

public class GeometryUtil {
    public static double calculateDistance2D(double x1, double y1, double x2, double y2) {
        double tx = x1 - x2;
        double ty = y1 - y2;
        return Math.sqrt((tx * tx) + (ty * ty));
    }
}
