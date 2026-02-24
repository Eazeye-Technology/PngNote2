package com.mypen;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.ArrayList;
import java.util.List;

/* loaded from: onyxsdk-pen-1.3.8.jar:com/onyx/android/sdk/pen/NeoFountainPen.class */
public class NeoFountainPenMod {
    public static void drawStroke(Canvas canvas, Paint paint, List<TouchPoint> points, float displayScale, float strokeWidth, float maxTouchPressure, boolean erase) {
        List<TouchPoint> computeStrokePoints = computeStrokePoints(points, displayScale, strokeWidth, maxTouchPressure);
        if (computeStrokePoints == null) {
            return;
        }
        PenUtils.drawStrokeByPointSize(canvas, paint, computeStrokePoints, erase);
    }

    public static List<TouchPoint> computeStrokePoints(List<TouchPoint> points, float displayScale, float strokeWidth, float maxTouchPressure) {
        if (hasPressure(points)) {
            return computeStrokePoints(points, strokeWidth, maxTouchPressure);
        }
        return a(points, displayScale);
    }

    private static List<TouchPoint> a(List<TouchPoint> points, float scale) {
        ArrayList arrayList = new ArrayList();
        for (TouchPoint touchPoint : points) {
            TouchPoint touchPoint2 = new TouchPoint(touchPoint);
            touchPoint2.size = touchPoint.size * scale;
            arrayList.add(touchPoint2);
        }
        return arrayList;
    }

    public static boolean hasPressure(List<TouchPoint> points) {
        return points.get(0).getPressure() > 0.0f;
    }

    public static List<TouchPoint> computeStrokePoints(List<TouchPoint> points, float strokeWidth, float maxTouchPressure) {
        return NeoPenUtils.computeStrokePoints(2, points, strokeWidth, maxTouchPressure);
    }
}
