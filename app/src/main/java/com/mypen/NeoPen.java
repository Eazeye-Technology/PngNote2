package com.mypen;

import android.graphics.Bitmap;

//import com.onyx.android.sdk.data.note.TouchPoint;

import java.util.List;

/* loaded from: onyxsdk-pen-1.3.8.jar:com/onyx/android/sdk/pen/NeoPen.class */
public class NeoPen {
    private static NeoPenConfig a;

    private static native boolean nativeInitPen(NeoPenConfig neoPenConfig);

    private static native void nativeDestroyPen();

    private static native NeoRenderPoint[] nativeOnPenDown(double[] dArr);

    private static native NeoRenderPoint[] nativeOnPenMove(double[] dArr);

    private static native NeoRenderPoint[] nativeOnPenUp(double[] dArr);

    private static native NeoRenderPoint[] nativeComputeRenderPoints(double[] dArr);

    private static native Bitmap[] nativeGetRenderedBitmaps();

    public static boolean initPen(NeoPenConfig config) {
        a = config;
        return nativeInitPen(config);
    }

    public static void destroyPen() {
        nativeDestroyPen();
    }

    public static NeoRenderPoint[] onPenDown(TouchPoint point) {
        return nativeOnPenDown(PenUtils.getPointDoubleArray(point, a.maxTouchPressure));
    }

    public static NeoRenderPoint[] onPenMove(TouchPoint point) {
        return nativeOnPenMove(PenUtils.getPointDoubleArray(point, a.maxTouchPressure));
    }

    public static NeoRenderPoint[] onPenUp(TouchPoint point) {
        return nativeOnPenUp(PenUtils.getPointDoubleArray(point, a.maxTouchPressure));
    }

    public static NeoRenderPoint[] computeRenderPoints(List<TouchPoint> points) {
        return nativeComputeRenderPoints(PenUtils.getPointDoubleArray(points, a.maxTouchPressure));
    }

    public static Bitmap[] getRenderedBitmaps() {
        return nativeGetRenderedBitmaps();
    }

    static {
        System.loadLibrary("neo_pen");
    }
}
