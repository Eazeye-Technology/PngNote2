package io.github.pastthepixels.freepaint.Graphics;

import java.util.ArrayList;
import java.util.List;

//给定多边形的边数，用java计算正多边形的所有顶点坐标，中心在原点
public class RegularPolygonVertices {

    /**
     * 计算正多边形的顶点坐标
     * @param n 边数（n >= 3）
     * @param radius 外接圆半径
     * @return 顶点坐标列表，顺序为从角度0开始逆时针方向
     */
    public static List<Point> getVertices(int n, double radius) {
        if (n < 3) {
            throw new IllegalArgumentException("边数必须至少为3");
        }
        List<Point> vertices = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            double angle = 2 * Math.PI * i / n;  // 从角度0开始
            double x = radius * Math.cos(angle);
            double y = radius * Math.sin(angle);
            vertices.add(new Point(x, y));
        }
        return vertices;
    }

    // 简化的坐标类
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

//    public static void main(String[] args) {
//        if (args.length == 0) {
//            System.out.println("用法: java RegularPolygonVertices <边数> [半径]");
//            System.out.println("示例: java RegularPolygonVertices 5       (半径=1)");
//            System.out.println("示例: java RegularPolygonVertices 6 2.0   (半径=2)");
//            return;
//        }
//
//        int n;
//        double radius = 1.0;  // 默认半径
//
//        try {
//            n = Integer.parseInt(args[0]);
//            if (args.length >= 2) {
//                radius = Double.parseDouble(args[1]);
//            }
//        } catch (NumberFormatException e) {
//            System.out.println("参数格式错误，请输入数字");
//            return;
//        }
//
//        try {
//            List<Point> vertices = getVertices(n, radius);
//            System.out.printf("边数: %d, 半径: %.2f\n", n, radius);
//            for (int i = 0; i < vertices.size(); i++) {
//                System.out.printf("顶点 %d: %s\n", i + 1, vertices.get(i));
//            }
//        } catch (IllegalArgumentException e) {
//            System.out.println(e.getMessage());
//        }
//    }
}
