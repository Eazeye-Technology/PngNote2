package com.mypen;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

//import com.onyx.android.sdk.data.note.TouchPoint;
//import com.onyx.android.sdk.utils.ColorUtils;

import java.util.ArrayList;
import java.util.List;

/* loaded from: onyxsdk-pen-1.3.8.jar:com/onyx/android/sdk/pen/PenUtils.class */
public class PenUtils {
    public static final float ERASE_EXTRA_STROKE_WIDTH = 1.0f;
    private static Bitmap a;

    public static Bitmap ensurePenBitmapCreated(Rect drawRect) {
        Bitmap bitmap = a;
        if (bitmap == null || bitmap.getWidth() != drawRect.width() || a.getHeight() != drawRect.height()) {
            a = Bitmap.createBitmap(drawRect.width(), drawRect.height(), Bitmap.Config.ARGB_8888);
        }
        a.eraseColor(0);
        return a;
    }

//    public static int getColorSafely(int shapeColor) {
//        if (!ColorUtils.isNeutralColor(shapeColor)) {
//            shapeColor = -16777216;
//        }
//        return shapeColor;
//    }

    public static void drawStrokeByPointSize(Canvas canvas, Paint paint, List<TouchPoint> points, boolean erase) {
        int color = paint.getColor();
        Paint.Style style = paint.getStyle();
        Paint.Cap strokeCap = paint.getStrokeCap();
        Paint.Join strokeJoin = paint.getStrokeJoin();
        boolean isAntiAlias = paint.isAntiAlias();
        float strokeWidth = paint.getStrokeWidth();
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setAntiAlias(true);
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 < points.size() - 1) {
                int i3 = i2 + 1;
                float f = points.get(i3).size;
                if (erase) {
                    f += 1.0f;
                }
                paint.setStrokeWidth(f);
                canvas.drawLine(points.get(i2).x, points.get(i2).y, points.get(i3).x, points.get(i3).y, paint);
                i = i3;
            } else {
                paint.setColor(color);
                paint.setStyle(style);
                paint.setStrokeCap(strokeCap);
                paint.setStrokeJoin(strokeJoin);
                paint.setAntiAlias(isAntiAlias);
                paint.setStrokeWidth(strokeWidth);
                return;
            }
        }
    }

    public static ArrayList<TouchPoint> toTouchPoints(NeoRenderPoint[] points) {
        ArrayList<TouchPoint> arrayList = new ArrayList<>();
        for (NeoRenderPoint neoRenderPoint : points) {
            arrayList.add(new TouchPoint(neoRenderPoint.x, neoRenderPoint.y, 0.0f, neoRenderPoint.size, 0L));
        }
        return arrayList;
    }

    public static float[] getPointArray(List<TouchPoint> points, float maxTouchPressure) {
        float[] fArr = new float[points.size() * 5];
        for (int i = 0; i < points.size(); i++) {
            int i2 = i;
            int i3 = i2 * 5;
            fArr[i3] = points.get(i2).x;
            fArr[i3 + 1] = points.get(i).y;
            fArr[i3 + 2] = points.get(i).pressure / maxTouchPressure;
            fArr[i3 + 3] = points.get(i).size;
            fArr[i3 + 4] = (float) points.get(i).timestamp;
        }
        return fArr;
    }

    public static double[] getPointDoubleArray(TouchPoint point, float maxTouchPressure) {
        double[] dArr = new double[5];
        dArr[0] = point.x;
        dArr[1] = point.y;
        dArr[2] = point.pressure / maxTouchPressure;
        dArr[3] = point.size;
        dArr[4] = point.tiltX;
        dArr[5] = point.tiltY;
        dArr[6] = point.timestamp;
        return dArr;
    }

    public static double[] getPointDoubleArray(List<TouchPoint> points, float maxTouchPressure) {
        double[] dArr = new double[points.size() * 7];
        for (int i = 0; i < points.size(); i++) {
            int i2 = i * 7;
            dArr[i2] = points.get(i).x;
            dArr[i2 + 1] = points.get(i).y;
            dArr[i2 + 2] = Math.min(1.0f, points.get(i).pressure / maxTouchPressure);
            dArr[i2 + 3] = points.get(i).size;
            dArr[i2 + 4] = points.get(i).tiltX;
            dArr[i2 + 5] = points.get(i).tiltY;
            dArr[i2 + 6] = points.get(i).timestamp;
        }
        return dArr;
    }
}
