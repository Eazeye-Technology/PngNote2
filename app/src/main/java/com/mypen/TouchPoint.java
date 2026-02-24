package com.mypen;

import android.graphics.Matrix;
import android.graphics.RectF;
import android.view.MotionEvent;

import androidx.annotation.FloatRange;
import androidx.annotation.Nullable;

//import com.onyx.android.sdk.data.ReaderTextStyle;
//import com.onyx.android.sdk.data.point.TinyPoint;
//import com.onyx.android.sdk.utils.CollectionUtils;
//import com.onyx.android.sdk.utils.Debug;
//import com.onyx.android.sdk.utils.LineUtils;
//
//import org.nustaq.serialization.annotations.Flat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

//@Flat
/* loaded from: onyxsdk-base-1.6.21.jar:com/onyx/android/sdk/data/note/TouchPoint.class */
public class TouchPoint implements Serializable, Cloneable {
    public static final int OBJECT_BYTE_COUNT = 32;

//    @Flat
    public float x;

//    @Flat
    public float y;

//    @Flat
    public float pressure;

//    @Flat
    public float size;

//    @Flat
    public int tiltX;

//    @Flat
    public int tiltY;

//    @Flat
    public long timestamp;

    public TouchPoint() {
    }
//
//    public static TouchPoint create(MotionEvent motionEvent) {
//        return new TouchPoint(motionEvent);
//    }
//
//    public static TouchPoint fromHistorical(MotionEvent motionEvent, int i) {
//        return new TouchPoint(motionEvent.getHistoricalX(i), motionEvent.getHistoricalY(i), motionEvent.getHistoricalPressure(i), motionEvent.getHistoricalSize(i), motionEvent.getHistoricalEventTime(i));
//    }
//
//    public static List<TouchPoint> renderPointArray(Matrix matrix, List<TouchPoint> touchPoints) {
//        if (matrix == null) {
//            return touchPoints;
//        }
//        float[] fArr = new float[2];
//        float[] fArr2 = new float[2];
//        ArrayList arrayList = new ArrayList();
//        for (int i = 0; i < touchPoints.size(); i++) {
//            TouchPoint touchPoint = touchPoints.get(i);
//            float f = touchPoint.x;
//            fArr[0] = f;
//            fArr2[0] = f;
//            float f2 = touchPoint.y;
//            fArr[1] = f2;
//            fArr2[1] = f2;
//            matrix.mapPoints(fArr2, fArr);
//            TouchPoint touchPoint2 = new TouchPoint(touchPoint);
//            touchPoint2.x = fArr2[0];
//            touchPoint2.y = fArr2[1];
//            arrayList.add(touchPoint2);
//        }
//        return arrayList;
//    }
//
//    public static float getPointAngle(TouchPoint start, TouchPoint end) {
//        return (float) ((Math.atan2(end.x - start.x, start.y - end.y) * 180.0d) / 3.141592653589793d);
//    }
//
//    @FloatRange(from = 0.0d, to = 90.0d)
//    public static float getHorizontalAngle(TouchPoint start, TouchPoint end) {
//        return LineUtils.getHorizontalAngle(start.x, start.y, end.x, end.y);
//    }
//
//    public static float getPointDistance(float x1, float y1, float x2, float y2) {
//        return (float) Math.sqrt(Math.pow(x1 - x2, 2.0d) + Math.pow(y1 - y2, 2.0d));
//    }
//
//    public static float[] getTransformRectPoints(RectF originRect, Matrix matrix) {
//        float[] fArr = new float[8];
//        float[] fArr2 = new float[8];
//        float[] fArr3 = new float[8];
//        float f = originRect.left;
//        float f2 = originRect.top;
//        float f3 = originRect.right;
//        float f4 = originRect.bottom;
//        float[] fArr4 = {f, f2, f3, f2, f3, f4, f, f4};
//        if (matrix == null) {
//            return fArr4;
//        }
//        matrix.mapPoints(fArr, fArr4);
//        matrix.mapRect(originRect);
//        Matrix matrix2 = new Matrix();
//        matrix2.postRotate(90.0f, fArr[0], fArr[1]);
//        matrix2.mapPoints(fArr2, fArr);
//        TouchPoint intersection = getIntersection(new TouchPoint(fArr[0], fArr[1]), new TouchPoint(fArr2[2], fArr2[3]), new TouchPoint(fArr[4], fArr[5]), new TouchPoint(fArr[6], fArr[7]));
//        Matrix matrix3 = new Matrix();
//        float[] fArr5 = new float[8];
//        matrix3.postRotate(90.0f, fArr[4], fArr[5]);
//        matrix3.mapPoints(fArr5, fArr);
//        TouchPoint intersection2 = getIntersection(new TouchPoint(fArr[4], fArr[5]), new TouchPoint(fArr5[6], fArr5[7]), new TouchPoint(fArr[0], fArr[1]), new TouchPoint(fArr[2], fArr[3]));
//        if (intersection != null && !originRect.contains(intersection.x, intersection.y) && intersection2 != null && !originRect.contains(intersection2.x, intersection2.y)) {
//            fArr3[0] = fArr[0];
//            fArr3[1] = fArr[1];
//            fArr3[2] = intersection2.x;
//            fArr3[3] = intersection2.y;
//            fArr3[4] = fArr[4];
//            fArr3[5] = fArr[5];
//            fArr3[6] = intersection.x;
//            fArr3[7] = intersection.y;
//            return fArr3;
//        }
//        Matrix matrix4 = new Matrix();
//        float[] fArr6 = new float[8];
//        matrix4.postRotate(90.0f, fArr[2], fArr[3]);
//        matrix4.mapPoints(fArr6, fArr);
//        TouchPoint intersection3 = getIntersection(new TouchPoint(fArr6[4], fArr6[5]), new TouchPoint(fArr[2], fArr[3]), new TouchPoint(fArr[6], fArr[7]), new TouchPoint(fArr[0], fArr[1]));
//        Matrix matrix5 = new Matrix();
//        float[] fArr7 = new float[8];
//        matrix5.postRotate(90.0f, fArr[6], fArr[7]);
//        matrix5.mapPoints(fArr7, fArr);
//        TouchPoint intersection4 = getIntersection(new TouchPoint(fArr[6], fArr[7]), new TouchPoint(fArr7[0], fArr7[1]), new TouchPoint(fArr[2], fArr[3]), new TouchPoint(fArr[4], fArr[5]));
//        fArr3[0] = intersection3.x;
//        fArr3[1] = intersection3.y;
//        fArr3[2] = fArr[2];
//        fArr3[3] = fArr[3];
//        fArr3[4] = intersection4.x;
//        fArr3[5] = intersection4.y;
//        fArr3[6] = fArr[6];
//        fArr3[7] = fArr[7];
//        return fArr3;
//    }
//
//    public static TouchPoint getIntersection(TouchPoint a, TouchPoint b, TouchPoint c, TouchPoint d) {
//        TouchPoint touchPoint = new TouchPoint(a);
//        if (Math.abs(b.y - a.y) + Math.abs(b.x - a.x) + Math.abs(d.y - c.y) + Math.abs(d.x - c.x) == ReaderTextStyle.FONT_EMBOLDEN_NORMAL) {
//            if ((c.x - a.x) + (c.y - a.y) == ReaderTextStyle.FONT_EMBOLDEN_NORMAL) {
//                Debug.d("ABCD is the same point!");
//            } else {
//                Debug.d("AB is a point, CD is a point, and AC is different!");
//            }
//            return touchPoint;
//        }
//        if (Math.abs(b.y - a.y) + Math.abs(b.x - a.x) == ReaderTextStyle.FONT_EMBOLDEN_NORMAL) {
//            float f = a.x;
//            float f2 = d.x;
//            float f3 = f - f2;
//            float f4 = c.y;
//            float f5 = d.y;
//            if ((f3 * (f4 - f5)) - ((a.y - f5) * (c.x - f2)) == ReaderTextStyle.FONT_EMBOLDEN_NORMAL) {
//                Debug.d("A, B is a point, and on the CD line segment!");
//            } else {
//                Debug.d("A, B is a point, and not on the CD line segment!");
//            }
//            return touchPoint;
//        }
//        if (Math.abs(d.y - c.y) + Math.abs(d.x - c.x) == ReaderTextStyle.FONT_EMBOLDEN_NORMAL) {
//            float f6 = d.x;
//            float f7 = b.x;
//            float f8 = f6 - f7;
//            float f9 = a.y;
//            float f10 = b.y;
//            if ((f8 * (f9 - f10)) - ((d.y - f10) * (a.x - f7)) == ReaderTextStyle.FONT_EMBOLDEN_NORMAL) {
//                Debug.d("C, D is a point, and on the AB line segment!");
//            } else {
//                Debug.d("C, D is a point, and not on the AB line segment!");
//            }
//            return touchPoint;
//        }
//        float f11 = b.y;
//        float f12 = a.y;
//        float f13 = f11 - f12;
//        float f14 = c.x;
//        float f15 = d.x;
//        float f16 = f13 * (f14 - f15);
//        float f17 = b.x;
//        float f18 = a.x;
//        float f19 = f17 - f18;
//        float f20 = c.y;
//        float f21 = d.y;
//        if (f16 - (f19 * (f20 - f21)) == ReaderTextStyle.FONT_EMBOLDEN_NORMAL) {
//            Debug.d("Line segments are parallel, no intersections!");
//            return touchPoint;
//        }
//        touchPoint.x = (((((f17 - f18) * (f14 - f15)) * (f20 - f12)) - ((f14 * (f17 - f18)) * (f20 - f21))) + ((f18 * (f11 - f12)) * (f14 - f15))) / (((f11 - f12) * (f14 - f15)) - ((f17 - f18) * (f20 - f21)));
//        float f22 = (f11 - f12) * (f20 - f21);
//        float f23 = c.x;
//        float f24 = a.x;
//        float f25 = d.x;
//        float f26 = (f22 * (f23 - f24)) - ((f20 * (f11 - f12)) * (f23 - f25));
//        float f27 = b.x;
//        touchPoint.y = (f26 + ((f12 * (f27 - f24)) * (f20 - f21))) / (((f27 - f24) * (f20 - f21)) - ((f11 - f12) * (f23 - f25)));
//        return touchPoint;
//    }
//
//    public static float[] realPointArray(List<TouchPoint> points) {
//        return realPointArray(points, 1.0f);
//    }
//
//    public static TouchPoint fromTinyPoint(TinyPoint tinyPoint) {
//        return new TouchPoint(tinyPoint.getX(), tinyPoint.getY(), tinyPoint.getPressure(), tinyPoint.getSize(), tinyPoint.getTime());
//    }
//
//    public static int getTouchPointCoordinatesHashCode(TouchPoint touchPoint, int maxXY, int blockSize) {
//        return (int) ((maxXY * ((int) (touchPoint.x / blockSize))) + (touchPoint.y / blockSize)); //FIXME:???
//    }
//
//    public static int computePointByteSize(List<TouchPoint> touchPointList) {
//        if (CollectionUtils.isNullOrEmpty(touchPointList)) {
//            return 0;
//        }
//        return touchPointList.size() * 32;
//    }

    public void set(TouchPoint point) {
        this.x = point.x;
        this.y = point.y;
        this.pressure = point.pressure;
        this.size = point.size;
        this.tiltX = point.tiltX;
        this.tiltY = point.tiltY;
        this.timestamp = point.timestamp;
    }

    public void offset(int dx, int dy) {
        this.x += dx;
        this.y += dy;
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public float getPressure() {
        return this.pressure;
    }

    public float getSize() {
        return this.size;
    }

    public int getTiltX() {
        return this.tiltX;
    }

    public int getTiltY() {
        return this.tiltY;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

//    public void normalize(com.onyx.android.sdk.data.PageInfo pageInfo) {
//        this.x = (this.x - pageInfo.getDisplayRect().left) / pageInfo.getActualScale();
//        this.y = (this.y - pageInfo.getDisplayRect().top) / pageInfo.getActualScale();
//    }
//
//    public void origin(com.onyx.android.sdk.data.PageInfo pageInfo) {
//        this.x = (this.x * pageInfo.getActualScale()) + pageInfo.getDisplayRect().left;
//        this.y = (this.y * pageInfo.getActualScale()) + pageInfo.getDisplayRect().top;
//    }
//
//    public void invertMatrix(@Nullable Matrix matrix) {
//        if (matrix == null) {
//            return;
//        }
//        Matrix matrix2 = new Matrix();
//        if (matrix.invert(matrix2)) {
//            applyMatrix(matrix2);
//        }
//    }
//
//    public void applyMatrix(@Nullable Matrix matrix) {
//        if (matrix == null) {
//            return;
//        }
//        float[] fArr = {this.x, this.y};
//        matrix.mapPoints(fArr);
//        this.x = fArr[0];
//        this.y = fArr[1];
//    }
//
//    public void translate(float dx, float dy) {
//        this.x += dx;
//        this.y += dy;
//    }
//
//    public TouchPoint setTimestamp(long timestamp) {
//        this.timestamp = timestamp;
//        return this;
//    }
//
//    public TouchPoint scale(float scaleValue) {
//        this.x *= scaleValue;
//        this.y *= scaleValue;
//        return this;
//    }
//
//    public void mapMatrix(@Nullable Matrix matrix) {
//        if (matrix == null) {
//            return;
//        }
//        float[] fArr = {this.x, this.y};
//        matrix.mapPoints(fArr);
//        this.x = fArr[0];
//        this.y = fArr[1];
//    }
//
//    public String toString() {
//        return "x:" + this.x + " y:" + this.y + "pressure:" + this.pressure + " size:" + this.size;
//    }
//
//    public boolean equals(Object o) {
//        if (this == o) {
//            return true;
//        }
//        if (o == null || TouchPoint.class != o.getClass()) {
//            return false;
//        }
//        TouchPoint touchPoint = (TouchPoint) o;
//        return Float.compare(touchPoint.x, this.x) == 0 && Float.compare(touchPoint.y, this.y) == 0 && Float.compare(touchPoint.pressure, this.pressure) == 0 && Float.compare(touchPoint.size, this.size) == 0 && this.timestamp == touchPoint.timestamp;
//    }
//
//    public int hashCode() {
//        return Objects.hash(Float.valueOf(this.x), Float.valueOf(this.y), Float.valueOf(this.pressure), Float.valueOf(this.size), Long.valueOf(this.timestamp));
//    }
//
    public TouchPoint(float x, float y) {
        this.x = x;
        this.y = y;
    }
//
//    public static float[] realPointArray(List<TouchPoint> points, float pointScale) {
//        if (CollectionUtils.isNullOrEmpty(points)) {
//            return new float[0];
//        }
//        long j = points.get(0).timestamp;
//        float[] fArr = new float[points.size() * 5];
//        for (int i = 0; i < points.size(); i++) {
//            int i2 = i;
//            int i3 = i2 * 5;
//            TouchPoint touchPoint = points.get(i2);
//            fArr[i3] = touchPoint.getX() * pointScale;
//            fArr[i3 + 1] = touchPoint.getY() * pointScale;
//            fArr[i3 + 2] = touchPoint.getSize();
//            fArr[i3 + 3] = touchPoint.getPressure();
//            fArr[i3 + 4] = (float) (touchPoint.getTimestamp() - j);
//        }
//        return fArr;
//    }
//
//    /* JADX WARN: Multi-variable type inference failed */
//    /* JADX WARN: Type inference failed for: r0v0, types: [java.lang.Object] */
//    /* JADX WARN: Type inference failed for: r0v4, types: [com.onyx.android.sdk.data.note.TouchPoint] */
//    /* renamed from: clone, reason: merged with bridge method [inline-methods] */
//    public TouchPoint m43clone() {
//        TouchPoint touchPoint = null;
//        try {
//            touchPoint = (TouchPoint) super.clone();
//        } catch (CloneNotSupportedException e) {
//            e.printStackTrace();
//        }
//        return touchPoint;
//    }

    public TouchPoint(float px, float py, float p, float s, long ts) {
        this.x = px;
        this.y = py;
        this.pressure = p;
        this.size = s;
        this.timestamp = ts;
    }

    public TouchPoint(float px, float py, float p, float s, int tx, int ty, long ts) {
        this.x = px;
        this.y = py;
        this.pressure = p;
        this.size = s;
        this.tiltX = tx;
        this.tiltY = ty;
        this.timestamp = ts;
    }

//    public static float[] renderPointArray(List<TouchPoint> points) {
//        return renderPointArray(points, 1.0f);
//    }
//
//    public static float[] renderPointArray(List<TouchPoint> points, float pointScale) {
//        float[] fArr = new float[points.size() * 3];
//        for (int i = 0; i < points.size(); i++) {
//            int i2 = i;
//            int i3 = i2 * 3;
//            TouchPoint touchPoint = points.get(i2);
//            fArr[i3] = touchPoint.getX() * pointScale;
//            fArr[i3 + 1] = touchPoint.getY() * pointScale;
//            fArr[i3 + 2] = touchPoint.getSize();
//        }
//        return fArr;
//    }

    public TouchPoint(MotionEvent motionEvent) {
        this.x = motionEvent.getX();
        this.y = motionEvent.getY();
        this.pressure = motionEvent.getPressure();
        this.size = motionEvent.getSize();
        this.timestamp = motionEvent.getEventTime();
    }

    public TouchPoint(TouchPoint source) {
        this.x = source.getX();
        this.y = source.getY();
        this.pressure = source.getPressure();
        this.size = source.getSize();
        this.tiltX = source.getTiltX();
        this.tiltY = source.getTiltY();
        this.timestamp = source.getTimestamp();
    }
}
