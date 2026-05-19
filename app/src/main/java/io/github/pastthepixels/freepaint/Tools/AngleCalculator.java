package io.github.pastthepixels.freepaint.Tools;

public class AngleCalculator {

    /**
     * 计算从点 (x1, y1) 指向点 (x2, y2) 的向量与 x 轴正方向的夹角（弧度）。
     * @return 若两点重合，返回 0.0；否则返回范围 (-π, π] 的弧度值。
     */
    public static double angleBetweenPoints(double x1, double y1, double x2, double y2) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        if (dx == 0 && dy == 0) {
            return 0.0;   // 或 throw new IllegalArgumentException("两点重合，无法定义角度")
        }
        return Math.atan2(dy, dx);
    }

//    public static void main(String[] args) {
//        // 示例1：从 (0,0) 到 (1,1)
//        double rad1 = angleBetweenPoints(0, 0, 1, 1);
//        System.out.printf("弧度: %.6f, 角度: %.2f°\n", rad1, Math.toDegrees(rad1));
//        // 输出: 弧度: 0.785398, 角度: 45.00°
//
//        // 示例2：从 (0,0) 到 (0, -1)
//        double rad2 = angleBetweenPoints(0, 0, 0, -1);
//        System.out.printf("弧度: %.6f, 角度: %.2f°\n", rad2, Math.toDegrees(rad2));
//        // 输出: 弧度: -1.570796, 角度: -90.00°
//
//        // 示例3：从 (1,1) 到 (0,0)
//        double rad3 = angleBetweenPoints(1, 1, 0, 0);
//        System.out.printf("弧度: %.6f, 角度: %.2f°\n", rad3, Math.toDegrees(rad3));
//        // 输出: 弧度: -2.356194, 角度: -135.00°
//    }
}
