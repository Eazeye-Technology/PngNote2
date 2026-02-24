package com.mypen;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;

import java.util.List;

public class Test1 {
//NeoFountainPen
//TouchHelper.STROKE_STYLE_PENCIL
    public Test1() {
    }

    public void test1() {
        Canvas canvas = null;
        Paint paint = null;
        float STROKE_WIDTH = 3.0f;
        float maxPressure = 1024.0f;//EpdController.getMaxTouchPressure();
        List<TouchPoint> list = null;
        float FLOAT_ONE = 1.0f;
        NeoFountainPenMod.drawStroke(canvas, paint, list, FLOAT_ONE, STROKE_WIDTH, maxPressure, false);

    }

    public void test2() {
        Canvas canvas = null;
        Paint paint = null;
        List<TouchPoint> list = null;
        Path path = new Path();
        PointF prePoint = new PointF(list.get(0).x, list.get(0).y);
        path.moveTo(prePoint.x, prePoint.y);
        for (TouchPoint point : list) {
            path.quadTo(prePoint.x, prePoint.y, point.x, point.y);
            prePoint.x = point.x;
            prePoint.y = point.y;
        }
        canvas.drawPath(path, paint);
    }
}
