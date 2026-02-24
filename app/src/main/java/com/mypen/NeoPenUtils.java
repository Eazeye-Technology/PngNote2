package com.mypen;

//import com.onyx.android.sdk.data.note.TouchPoint;

import java.util.ArrayList;
import java.util.List;

/* loaded from: onyxsdk-pen-1.3.8.jar:com/onyx/android/sdk/pen/NeoPenUtils.class */
public class NeoPenUtils {
    /* JADX WARN: Type inference failed for: r0v4, types: [java.lang.Throwable, boolean] */
    public static List<TouchPoint> computeStrokePoints(int type, List<TouchPoint> points, float strokeWidth, float maxTouchPressure) {
        if (NeoPen.initPen(new NeoPenConfig().setType(type).setWidth(strokeWidth).setMaxTouchPressure(maxTouchPressure)) == false) {
            return new ArrayList();
        }
        NeoRenderPoint[] computeRenderPoints = NeoPen.computeRenderPoints(points);
        if (computeRenderPoints == null) {
            ArrayList arrayList = new ArrayList();
            NeoPen.destroyPen();
            return arrayList;
        }
        ArrayList<TouchPoint> touchPoints = PenUtils.toTouchPoints(computeRenderPoints);
        NeoPen.destroyPen();
        return touchPoints;
    }
}
