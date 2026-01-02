package com.outsbook.libs.canvaseditor.stickers;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import com.outsbook.libs.canvaseditor.constants.ConstantStickerIcon;
import com.outsbook.libs.canvaseditor.listeners.StickerIconListener;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class StickerIcon extends DrawableSticker implements StickerIconListener {
    private float iconRadius;
    private float x;
    private float y;
    private int position;
    @Nullable
    private StickerIconListener iconListener;

    public StickerIcon(@Nullable Drawable drawable, int gravity) {
        //Intrinsics.checkNotNull(drawable);
        super(drawable);
        this.iconRadius = ConstantStickerIcon.Companion.getDEFAULT_ICON_RADIUS();
        this.position = gravity;
    }

    public float getIconRadius() {
        return this.iconRadius;
    }

    public void setIconRadius(float var1) {
        this.iconRadius = var1;
    }

    public float getX() {
        return this.x;
    }

    public void setX(float var1) {
        this.x = var1;
    }

    public float getY() {
        return this.y;
    }

    public void setY(float var1) {
        this.y = var1;
    }

    public int getPosition() {
        return this.position;
    }

    public final void setPosition(int var1) {
        this.position = var1;
    }

    @Nullable
    public StickerIconListener getIconListener() {
        return this.iconListener;
    }

    public void setIconListener(@Nullable StickerIconListener var1) {
        this.iconListener = var1;
    }

    public void draw(@NotNull Canvas canvas, @Nullable Paint paint) {
        Intrinsics.checkNotNullParameter(canvas, "canvas");
        Intrinsics.checkNotNull(paint);
        canvas.drawCircle(this.x, this.y, this.iconRadius, paint);
        super.draw(canvas);
    }

    @Override
    public void onActionDown(@Nullable StickerView stickerView, @Nullable MotionEvent event) {
        if (this.iconListener != null) {
            this.iconListener.onActionDown(stickerView, event);
        }
    }

    @Override
    public void onActionMove(@NotNull StickerView stickerView, @NotNull MotionEvent event) {
        Intrinsics.checkNotNullParameter(stickerView, "stickerView");
        Intrinsics.checkNotNullParameter(event, "event");
        if (this.iconListener != null) {
            this.iconListener.onActionMove(stickerView, event);
        }
    }

    @Override
    public void onActionUp(@NotNull StickerView stickerView, @Nullable MotionEvent event) {
        Intrinsics.checkNotNullParameter(stickerView, "stickerView");
        if (this.iconListener != null) {
            this.iconListener.onActionUp(stickerView, event);
        }
    }
}
