package io.github.pastthepixels.freepaint.Tools;

public class AngleCalculator {
    public static double angleBetweenPoints(double x1, double y1, double x2, double y2) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        if (dx == 0 && dy == 0) {
            return 0.0;   // or throw new IllegalArgumentException()
        }
        return Math.atan2(dy, dx);
    }
}
