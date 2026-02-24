package com.mypen;

/* loaded from: onyxsdk-pen-1.3.8.jar:com/onyx/android/sdk/pen/NeoRenderPoint.class */
public class NeoRenderPoint {
    public float x;
    public float y;
    public float size;
    public int bitmapIndex;

    public static NeoRenderPoint create(float x, float y, float size, int bitmapIndex) {
        NeoRenderPoint neoRenderPoint = new NeoRenderPoint();
        neoRenderPoint.x = x;
        neoRenderPoint.y = y;
        neoRenderPoint.size = size;
        neoRenderPoint.bitmapIndex = bitmapIndex;
        return neoRenderPoint;
    }
}
