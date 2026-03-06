package io.github.pastthepixels.freepaint;

import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PointF;
import android.graphics.RectF;

import java.util.ArrayList;
import java.util.List;

import io.github.pastthepixels.freepaint.Graphics.Point;

public class Utils {
    public static double distanceFromPointToLine(Point lineStart, Point lineEnd, Point test) {
        double numerator = Math.abs((lineEnd.x - lineStart.x) * (test.y - lineStart.y) - (test.x - lineStart.x) * (lineEnd.y - lineStart.y));
        double denominator = Math.sqrt(Math.pow(lineEnd.x - lineStart.x, 2) + Math.pow(lineEnd.y - lineStart.y, 2));
        return numerator / denominator;
    }

    /**
     * Gets the point of collision between two lines, each defined by two points on the line.
     * Precondition: both lines must intersect.
     * @param aStart
     * @param aEnd
     * @param bStart
     * @param bEnd
     * @return A Point where both lines intersect
     */
    public static Point collisionBetweenLines(Point aStart, Point aEnd, Point bStart, Point bEnd) {
        double detA = aStart.x * aEnd.y - aStart.y * aEnd.x;
        double detB = bStart.x * bEnd.y - bStart.y * bEnd.x;
        Point diffA = aStart.subtract(aEnd);
        Point diffB = bStart.subtract(bEnd);
        double denom = diffA.x * diffB.y - diffA.y * diffB.x;
        return new Point(
                (float) ((detA * diffB.x - diffA.x * detB) / denom),
                (float) ((detA * diffB.y - diffA.y * detB) / denom)
        );
    }

    /**
     * Returns the angle between two vectors in radians.
     * @param vecA
     * @param vecB
     * @return The angle between two vectors, radians
     */
    public static double angleBetweenVectors(Point vecA, Point vecB) {
        return Math.acos((vecA.x * vecB.x + vecA.y * vecB.y) / (vecA.length() * vecB.length()));
    }








    //--------------------------------
    //android 计算path到点距离
    //import android.graphics.Path;
    //import android.graphics.PathMeasure;
    public static float distanceToPoint(Path path, float x, float y) {
        PathMeasure pm = new PathMeasure(path, false);
        float[] pos = new float[2];
        float[] tan = new float[2];
        final float tolerance = 1; // 容忍度，用于找到路径上的点

        for (float dist = 0; dist < pm.getLength(); dist += tolerance) {
            pm.getPosTan(dist, pos, tan);
            float dx = x - pos[0];
            float dy = y - pos[1];
            float distance = (float) Math.sqrt(dx * dx + dy * dy);
            if (distance < tolerance) {
                return dist; // 找到了最接近的点，返回其距离
            }
        }
        return -1; // 如果没有找到接近的点，返回-1或者抛出异常
    }

    //import android.graphics.Path;
    //import android.graphics.RectF;
    public static boolean isPointNearPath(Path path, float x, float y, float tolerance) {
        RectF bounds = new RectF();
        path.computeBounds(bounds, true);
        if (bounds.contains(x, y)) {
            // 点在路径的边界内，可以进一步精确计算距离或使用其他方法判断。
            return true;
        } else {
            // 点可能在路径附近，但不完全在内部。这里可以添加更复杂的逻辑来判断。
            // 比如，你可以通过计算点到路径边界的距离来判断。
            return Math.abs(x - bounds.centerX()) + Math.abs(y - bounds.centerY()) < tolerance;
        }
    }

//import android.graphics.Path;
//import android.graphics.PathMeasure;
//import android.graphics.PointF;
//import java.util.ArrayList;
//import java.util.List;
    public static float findNearestDistance(Path path, float x, float y) {
        PathMeasure pm = new PathMeasure(path, false);
        float[] pos = new float[2];
        float minDistance = Float.MAX_VALUE;
        float currentDist = 0;
        List<PointF> points = new ArrayList<>(); // 存储细分后的点
        float step = 10; // 分割步长，可以根据需要调整以平衡精度和性能
        while (currentDist < pm.getLength()) {
            pm.getPosTan(currentDist, pos, null); // 获取位置，忽略切线方向
            points.add(new PointF(pos[0], pos[1])); // 添加到点列表中
            currentDist += step; // 移动到下一个点
        }
        // 计算所有细分点与目标点的距离，找到最小值
        for (PointF point : points) {
            float dx = x - point.x;
            float dy = y - point.y;
            float distance = (float) Math.sqrt(dx * dx + dy * dy); // 使用欧几里得距离公式计算距离
            if (distance < minDistance) {
                minDistance = distance; // 更新最小距离值
            }
        }
        return minDistance; // 返回最小距离值，即最近点的距离。
    }

    public static boolean isIntersects(RectF rect1, RectF rect2) {
        if (false) {
            return rect1.intersect(rect2); //don't use this
        } else {
            float left1 = Math.min(rect1.left, rect1.right);
            float right1 = Math.max(rect1.left, rect1.right);
            float top1 = Math.min(rect1.top, rect1.bottom);
            float bottom1 = Math.max(rect1.top, rect1.bottom);

            float left2 = Math.min(rect2.left, rect2.right);
            float right2 = Math.max(rect2.left, rect2.right);
            float top2 = Math.min(rect2.top, rect2.bottom);
            float bottom2 = Math.max(rect2.top, rect2.bottom);

            return !(right1 < left2 || left1 > right2 || top1 > bottom2 || bottom1 < top2);
        }
    }
}
