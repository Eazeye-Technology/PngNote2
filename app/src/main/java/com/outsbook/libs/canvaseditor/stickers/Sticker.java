package com.outsbook.libs.canvaseditor.stickers;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.math.MathKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class Sticker {
    @NotNull
    public static final Companion Companion = new Companion((DefaultConstructorMarker)null);
    @NotNull
    private final Matrix matrix = new Matrix();
    private boolean isFlippedHorizontally;
    private boolean isFlippedVertically;
    @NotNull
    private final float[] matrixValues = new float[9];
    @NotNull
    private final float[] unrotatedWrapperCorner = new float[8];
    @NotNull
    private final float[] unrotatedPoint = new float[2];
    @NotNull
    private final float[] boundPoints = new float[8];
    @NotNull
    private final float[] mappedBounds = new float[8];
    @NotNull
    private final RectF trappedRect = new RectF();

    @NotNull
    public final Matrix getMatrix() {
        return this.matrix;
    }

    public final boolean isFlippedHorizontally() {
        return this.isFlippedHorizontally;
    }

//    public final void setFlippedHorizontally(boolean var1) {
//        this.isFlippedHorizontally = var1;
//    }

    public final boolean isFlippedVertically() {
        return this.isFlippedVertically;
    }

//    public final void setFlippedVertically(boolean var1) {
//        this.isFlippedVertically = var1;
//    }

    public abstract int getWidth();

    public abstract int getHeight();

    @NotNull
    public abstract Drawable getDrawable();

    @NotNull
    public final float[] getMappedBoundPoints() {
        float[] dst = new float[8];
        this.getMappedPoints(dst, this.getBoundPoints());
        return dst;
    }

    private final RectF getBound() {
        RectF bound = new RectF();
        this.getBound(bound);
        return bound;
    }

    @NotNull
    public final RectF getMappedBound() {
        RectF dst = new RectF();
        this.getMappedBound(dst, this.getBound());
        return dst;
    }

    private final PointF getCenterPoint() {
        PointF center = new PointF();
        this.getCenterPoint(center);
        return center;
    }

    @NotNull
    public final PointF getMappedCenterPoint() {
        PointF pointF = this.getCenterPoint();
        this.getMappedCenterPoint(pointF, new float[2], new float[2]);
        return pointF;
    }

    public final float getCurrentScale() {
        return this.getMatrixScale(this.matrix);
    }

    public final float getCurrentHeight() {
        return this.getMatrixScale(this.matrix) * (float)this.getHeight();
    }

    public final float getCurrentWidth() {
        return this.getMatrixScale(this.matrix) * (float)this.getWidth();
    }

    private final float getCurrentAngle() {
        return this.getMatrixAngle(this.matrix);
    }

    public abstract void draw(@NotNull Canvas var1);

    @NotNull
    public abstract Sticker setDrawable(@NotNull Drawable var1);

    @NotNull
    public abstract Sticker setAlpha(int var1);

    @NotNull
    public final Sticker setMatrix(@Nullable Matrix matrix) {
        this.matrix.set(matrix);
        return this;
    }

    @NotNull
    public final Sticker setFlippedHorizontally(boolean flippedHorizontally) {
        this.isFlippedHorizontally = flippedHorizontally;
        return this;
    }

    @NotNull
    public final Sticker setFlippedVertically(boolean flippedVertically) {
        this.isFlippedVertically = flippedVertically;
        return this;
    }

    private final float[] getBoundPoints() {
        float[] points = new float[8];
        this.getBoundPoints(points);
        return points;
    }

    public final void getBoundPoints(@NotNull float[] points) {
        Intrinsics.checkNotNullParameter(points, "points");
        if (!this.isFlippedHorizontally) {
            if (!this.isFlippedVertically) {
                points[0] = 0.0F;
                points[1] = 0.0F;
                points[2] = (float)this.getWidth();
                points[3] = 0.0F;
                points[4] = 0.0F;
                points[5] = (float)this.getHeight();
                points[6] = (float)this.getWidth();
                points[7] = (float)this.getHeight();
            } else {
                points[0] = 0.0F;
                points[1] = (float)this.getHeight();
                points[2] = (float)this.getWidth();
                points[3] = (float)this.getHeight();
                points[4] = 0.0F;
                points[5] = 0.0F;
                points[6] = (float)this.getWidth();
                points[7] = 0.0F;
            }
        } else if (!this.isFlippedVertically) {
            points[0] = (float)this.getWidth();
            points[1] = 0.0F;
            points[2] = 0.0F;
            points[3] = 0.0F;
            points[4] = (float)this.getWidth();
            points[5] = (float)this.getHeight();
            points[6] = 0.0F;
            points[7] = (float)this.getHeight();
        } else {
            points[0] = (float)this.getWidth();
            points[1] = (float)this.getHeight();
            points[2] = 0.0F;
            points[3] = (float)this.getHeight();
            points[4] = (float)this.getWidth();
            points[5] = 0.0F;
            points[6] = 0.0F;
            points[7] = 0.0F;
        }

    }

    @NotNull
    public final float[] getMappedPoints(@NotNull float[] src) {
        Intrinsics.checkNotNullParameter(src, "src");
        float[] dst = new float[src.length];
        this.matrix.mapPoints(dst, src);
        return dst;
    }

    public final void getMappedPoints(@NotNull float[] dst, @NotNull float[] src) {
        Intrinsics.checkNotNullParameter(dst, "dst");
        Intrinsics.checkNotNullParameter(src, "src");
        this.matrix.mapPoints(dst, src);
    }

    private final void getBound(RectF dst) {
        dst.set(0.0F, 0.0F, (float)this.getWidth(), (float)this.getHeight());
    }

    private final void getMappedBound(RectF dst, RectF bound) {
        this.matrix.mapRect(dst, bound);
    }

    public final void getCenterPoint(@NotNull PointF dst) {
        Intrinsics.checkNotNullParameter(dst, "dst");
        dst.set((float)this.getWidth() * 1.0F / (float)2, (float)this.getHeight() * 1.0F / (float)2);
    }

    public final void getMappedCenterPoint(@NotNull PointF dst, @NotNull float[] mappedPoints, @NotNull float[] src) {
        Intrinsics.checkNotNullParameter(dst, "dst");
        Intrinsics.checkNotNullParameter(mappedPoints, "mappedPoints");
        Intrinsics.checkNotNullParameter(src, "src");
        this.getCenterPoint(dst);
        src[0] = dst.x;
        src[1] = dst.y;
        this.getMappedPoints(mappedPoints, src);
        dst.set(mappedPoints[0], mappedPoints[1]);
    }

    private final float getMatrixScale(Matrix matrix) {
        return (float)Math.sqrt(Math.pow((double)this.getMatrixValue(matrix, 0), (double)2.0F) + Math.pow((double)this.getMatrixValue(matrix, 3), (double)2.0F));
    }

    private final float getMatrixAngle(Matrix matrix) {
        return (float)Math.toDegrees(-Math.atan2((double)this.getMatrixValue(matrix, 1), (double)this.getMatrixValue(matrix, 0)));
    }

    private final float getMatrixValue(Matrix matrix, int valueIndex) {
        matrix.getValues(this.matrixValues);
        return this.matrixValues[valueIndex];
    }

    public final boolean contains(float x, float y) {
        float[] var3 = new float[]{x, y};
        return this.contains(var3);
    }

    public final boolean contains(@NotNull float[] point) {
        Intrinsics.checkNotNullParameter(point, "point");
        Matrix tempMatrix = new Matrix();
        tempMatrix.setRotate(-this.getCurrentAngle());
        this.getBoundPoints(this.boundPoints);
        this.getMappedPoints(this.mappedBounds, this.boundPoints);
        tempMatrix.mapPoints(this.unrotatedWrapperCorner, this.mappedBounds);
        tempMatrix.mapPoints(this.unrotatedPoint, point);
        this.trapToRect(this.trappedRect, this.unrotatedWrapperCorner);
        return this.trappedRect.contains(this.unrotatedPoint[0], this.unrotatedPoint[1]);
    }

    private final void trapToRect(RectF r, float[] array) {
        r.set(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY);

        for(int i = 1; i < array.length; i += 2) {
            float x = (float)MathKt.roundToInt(array[i - 1] * (float)10) / 10.0F;
            float y = (float)MathKt.roundToInt(array[i] * (float)10) / 10.0F;
            r.left = x < r.left ? x : r.left;
            r.top = y < r.top ? y : r.top;
            r.right = x > r.right ? x : r.right;
            r.bottom = y > r.bottom ? y : r.bottom;
        }

        r.sort();
    }

    @Metadata(
            mv = {2, 0, 0},
            k = 1,
            xi = 48,
            d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007¨\u0006\b"},
            d2 = {"Lcom/outsbook/libs/canvaseditor/stickers/Sticker$Companion;", "", "<init>", "()V", "getMatrix", "Landroid/graphics/Matrix;", "sticker", "Lcom/outsbook/libs/canvaseditor/stickers/Sticker;", "Sources of canvaseditor.app.main"}
    )
    public static final class Companion {
        private Companion() {
        }

        @NotNull
        public final Matrix getMatrix(@NotNull Sticker sticker) {
            Intrinsics.checkNotNullParameter(sticker, "sticker");
            return sticker.getMatrix();
        }

        // $FF: synthetic method
        public Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }
    }
}
