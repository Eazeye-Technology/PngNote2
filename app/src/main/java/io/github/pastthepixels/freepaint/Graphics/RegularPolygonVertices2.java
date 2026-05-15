package io.github.pastthepixels.freepaint.Graphics;

import java.util.ArrayList;
import java.util.List;

//给定多边形的边数n和外接正方形的边长为1，中心在原点，用java计算正多边形的所有顶点坐标
/**
 * 计算正多边形的顶点坐标，使其最小外接正方形（轴对齐）的边长为 1，中心在原点。
 */
public class RegularPolygonVertices2 {

    /**
     * 顶点坐标类
     */
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

    /**
     * 计算满足条件的正多边形顶点
     * @param n 边数，n >= 3
     * @return 顶点列表（逆时针方向，第一个顶点在 x 轴正半轴）
     */
    public static List<Point> getVertices(int n) {
        if (n < 3) {
            throw new IllegalArgumentException("边数必须至少为3");
        }

        // 1. 计算半径为 1 的原始顶点坐标
        List<Point> rawVertices = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            double angle = 2 * Math.PI * i / n;  // 从角度 0 开始
            if (n == 4) {
                angle -= Math.PI / 4; //rotate 45 degrees
            } else {
                angle -= Math.PI / 2; //rotate 90 degrees
            }
            double x = Math.cos(angle);
            double y = Math.sin(angle);
            rawVertices.add(new Point(x, y));
        }

        // 2. 找出原始顶点中 |x| 和 |y| 的最大值
        double maxAbsX = 0.0, maxAbsY = 0.0;
        for (Point p : rawVertices) {
            maxAbsX = Math.max(maxAbsX, Math.abs(p.getX()));
            maxAbsY = Math.max(maxAbsY, Math.abs(p.getY()));
        }

        // 3. 计算缩放因子，使得最终 max(|x|,|y|) = 0.5
        double scale = 0.5 / Math.max(maxAbsX, maxAbsY);

        // 4. 缩放得到最终顶点
        List<Point> vertices = new ArrayList<>();
        for (Point p : rawVertices) {
            vertices.add(new Point(p.getX() * scale, p.getY() * scale));
        }
        return vertices;
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("用法: java RegularPolygonVertices <边数>");
            System.out.println("示例: java RegularPolygonVertices 5");
            return;
        }

        int n;
        try {
            n = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            System.out.println("边数必须是整数");
            return;
        }

        try {
            List<Point> vertices = getVertices(n);
            System.out.printf("边数: %d，外接正方形边长: 1\n", n);
            for (int i = 0; i < vertices.size(); i++) {
                System.out.printf("顶点 %d: %s\n", i + 1, vertices.get(i));
            }
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }
}
