package com.outsbook.libs.canvaseditor.models;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;

import com.outsbook.libs.canvaseditor.views.StickerView;

public class StickerIcon extends DrawableSticker {
    public static final float DEFAULT_ICON_RADIUS = 14.0F * Resources.getSystem().getDisplayMetrics().density;

    public static final int LEFT_TOP = 0;
    public static final int RIGHT_TOP = 1;
    public static final int LEFT_BOTTOM = 2;
    public static final int RIGHT_BOTTOM = 3;

    private float iconRadius;
    private float x;
    private float y;
    private int position;
    public interface StickerIconListener {
        void onActionDown(StickerView var1, MotionEvent var2);
        void onActionMove(StickerView var1, MotionEvent var2);
        void onActionUp(StickerView var1, MotionEvent var2);
    }
    private StickerIconListener iconListener;

    public StickerIcon(Drawable drawable, int gravity) {
        super(drawable);
        this.iconRadius = DEFAULT_ICON_RADIUS;
        this.position = gravity;
    }

    public float getIconRadius() {
        return this.iconRadius;
    }

    public void setIconRadius(float iconRadius) {
        this.iconRadius = iconRadius;
    }

    public float getX() {
        return this.x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return this.y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public int getPosition() {
        return this.position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public StickerIconListener getIconListener() {
        return this.iconListener;
    }

    public void setIconListener(StickerIconListener iconListener) {
        this.iconListener = iconListener;
    }

    public void draw(Canvas canvas, Paint paint) {
        canvas.drawCircle(this.x, this.y, this.iconRadius, paint);
        super.draw(canvas);
    }

    public void onActionDown(StickerView stickerView, MotionEvent event) {
        if (this.iconListener != null) {
            this.iconListener.onActionDown(stickerView, event);
        }
    }

    public void onActionMove(StickerView stickerView, MotionEvent event) {
        if (this.iconListener != null) {
            this.iconListener.onActionMove(stickerView, event);
        }
    }

    public void onActionUp(StickerView stickerView, MotionEvent event) {
        if (this.iconListener != null) {
            this.iconListener.onActionUp(stickerView, event);
        }
    }
}
