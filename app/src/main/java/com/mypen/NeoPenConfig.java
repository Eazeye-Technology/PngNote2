package com.mypen;

/* loaded from: onyxsdk-pen-1.3.8.jar:com/onyx/android/sdk/pen/NeoPenConfig.class */
public class NeoPenConfig {
    public static final int NEOPEN_PEN_TYPE_BRUSH = 1;
    public static final int NEOPEN_PEN_TYPE_FOUNTAIN = 2;
    public static final int NEOPEN_PEN_TYPE_MARKER = 3;
    public static final int NEOPEN_PEN_TYPE_CHARCOAL = 4;
    public int type = 1;
    public int color = -16777216;
    public float width = 3.0f;
    public int rotateAngle = 0;
    public boolean tiltEnabled = false;
    public float tiltScale = 3.0f;
    public float maxTouchPressure = 4095.0f;

    public int getType() {
        return this.type;
    }

    public NeoPenConfig setType(int type) {
        this.type = type;
        return this;
    }

    public int getColor() {
        return this.color;
    }

    public NeoPenConfig setColor(int color) {
        this.color = color;
        return this;
    }

    public float getWidth() {
        return this.width;
    }

    public NeoPenConfig setWidth(float width) {
        this.width = width;
        return this;
    }

    public int getRotateAngle() {
        return this.rotateAngle;
    }

    public NeoPenConfig setRotateAngle(int rotateAngle) {
        this.rotateAngle = rotateAngle;
        return this;
    }

    public boolean isTiltEnabled() {
        return this.tiltEnabled;
    }

    public NeoPenConfig setTiltEnabled(boolean tiltEnabled) {
        this.tiltEnabled = tiltEnabled;
        return this;
    }

    public float getTiltScale() {
        return this.tiltScale;
    }

    public NeoPenConfig setTiltScale(float tiltScale) {
        this.tiltScale = tiltScale;
        return this;
    }

    public float getMaxTouchPressure() {
        return this.maxTouchPressure;
    }

    public NeoPenConfig setMaxTouchPressure(float maxTouchPressure) {
        this.maxTouchPressure = maxTouchPressure;
        return this;
    }
}
