package io.github.pastthepixels.freepaint.Graphics;

import java.util.ArrayList;
import java.util.List;

public class RegularPolygonVertices2 {

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

    public static List<Point> getVertices(int n, double shapeRotate) {
//        if (n < 3) {
//            throw new IllegalArgumentException("The side parameter must be at least 3");
//        }
        boolean isRotate = false;
        if (n == 1) {
            n = 2;
            isRotate = true;
        }

        List<Point> rawVertices = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            double angle = 2 * Math.PI * i / n;
            //if (n == 1) angle = 2 * Math.PI / 2;
            if (n == 4) {
                angle -= (Math.PI / 4 + shapeRotate); //rotate 45 degrees
            } else if (isRotate) {
                angle -= (Math.PI + shapeRotate); //rotate 180 degrees
            } else {
                angle -= (Math.PI / 2 + shapeRotate); //rotate 90 degrees
            }
            double x = Math.cos(angle);
            double y = Math.sin(angle);
            rawVertices.add(new Point(x, y));
        }

        double maxAbsX = 0.0, maxAbsY = 0.0;
        for (Point p : rawVertices) {
            maxAbsX = Math.max(maxAbsX, Math.abs(p.getX()));
            maxAbsY = Math.max(maxAbsY, Math.abs(p.getY()));
        }

        double scale = 0.5 / Math.max(maxAbsX, maxAbsY);

        List<Point> vertices = new ArrayList<>();
        for (Point p : rawVertices) {
            vertices.add(new Point(p.getX() * scale, p.getY() * scale));
        }
        return vertices;
    }
}
