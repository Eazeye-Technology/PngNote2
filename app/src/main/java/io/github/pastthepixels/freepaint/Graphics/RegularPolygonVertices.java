package io.github.pastthepixels.freepaint.Graphics;

import java.util.ArrayList;
import java.util.List;

public class RegularPolygonVertices {

    public static List<Point> getVertices(int n, double radius) {
        if (n < 3) {
            throw new IllegalArgumentException("n must >= 3");
        }
        List<Point> vertices = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            double angle = 2 * Math.PI * i / n;
            double x = radius * Math.cos(angle);
            double y = radius * Math.sin(angle);
            vertices.add(new Point(x, y));
        }
        return vertices;
    }

    public static class Point {
        private final double x;
        private final double y;

        public Point(double x, double y) {
            this.x = x;
            this.y = y;
        }

        public double getX() { return x; }
        public double getY() { return y; }

        @Override
        public String toString() {
            return String.format("(%.6f, %.6f)", x, y);
        }
    }
}
