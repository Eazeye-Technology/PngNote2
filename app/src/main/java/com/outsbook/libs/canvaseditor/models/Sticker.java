package com.outsbook.libs.canvaseditor.models;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

public abstract class Sticker {
    public abstract int getWidth();
    public abstract int getHeight();
    public abstract Drawable getDrawable();
    public abstract void draw(Canvas var1);
    public abstract Sticker setDrawable(Drawable var1);
    public abstract Sticker setAlpha(int var1);

    private Matrix matrix = new Matrix();
    private boolean isFlippedHorizontally;
    private boolean isFlippedVertically;
    private float[] matrixValues = new float[9];
    private float[] unrotatedWrapperCorner = new float[8];
    private float[] unrotatedPoint = new float[2];
    private float[] boundPoints = new float[8];
    private float[] mappedBounds = new float[8];
    private RectF trappedRect = new RectF();

    public Matrix getMatrix() {
        return this.matrix;
    }

    public boolean isFlippedHorizontally() {
        return this.isFlippedHorizontally;
    }

    public boolean isFlippedVertically() {
        return this.isFlippedVertically;
    }

    public float[] getMappedBoundPoints() {
        float[] dst = new float[8];
        this.getMappedPoints(dst, this.getBoundPoints());
        return dst;
    }

    private RectF getBound() {
        RectF bound = new RectF();
        this.getBound(bound);
        return bound;
    }

    public RectF getMappedBound() {
        RectF dst = new RectF();
        this.getMappedBound(dst, this.getBound());
        return dst;
    }

    private PointF getCenterPoint() {
        PointF center = new PointF();
        this.getCenterPoint(center);
        return center;
    }

    public PointF getMappedCenterPoint() {
        PointF pointF = this.getCenterPoint();
        this.getMappedCenterPoint(pointF, new float[2], new float[2]);
        return pointF;
    }

    public float getCurrentScale() {
        return this.getMatrixScale(this.matrix);
    }

    public float getCurrentHeight() {
        return this.getMatrixScale(this.matrix) * (float)this.getHeight();
    }

    public float getCurrentWidth() {
        return this.getMatrixScale(this.matrix) * (float)this.getWidth();
    }

    private float getCurrentAngle() {
        return this.getMatrixAngle(this.matrix);
    }

    public Sticker setMatrix(Matrix matrix) {
        this.matrix.set(matrix);
        return this;
    }

    public Sticker setFlippedHorizontally(boolean flippedHorizontally) {
        this.isFlippedHorizontally = flippedHorizontally;
        return this;
    }

    public Sticker setFlippedVertically(boolean flippedVertically) {
        this.isFlippedVertically = flippedVertically;
        return this;
    }

    private float[] getBoundPoints() {
        float[] points = new float[8];
        this.getBoundPoints(points);
        return points;
    }

    public void getBoundPoints(float[] points) {
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

    public float[] getMappedPoints(float[] src) {
        float[] dst = new float[src.length];
        this.matrix.mapPoints(dst, src);
        return dst;
    }

    public void getMappedPoints(float[] dst, float[] src) {
        this.matrix.mapPoints(dst, src);
    }

    private void getBound(RectF dst) {
        dst.set(0.0F, 0.0F, (float)this.getWidth(), (float)this.getHeight());
    }

    private void getMappedBound(RectF dst, RectF bound) {
        this.matrix.mapRect(dst, bound);
    }

    public void getCenterPoint(PointF dst) {
        dst.set((float)this.getWidth() * 1.0F / (float)2, (float)this.getHeight() * 1.0F / (float)2);
    }

    public void getMappedCenterPoint(PointF dst, float[] mappedPoints, float[] src) {
        this.getCenterPoint(dst);
        src[0] = dst.x;
        src[1] = dst.y;
        this.getMappedPoints(mappedPoints, src);
        dst.set(mappedPoints[0], mappedPoints[1]);
    }

    private float getMatrixScale(Matrix matrix) {
        return (float)Math.sqrt(Math.pow((double)this.getMatrixValue(matrix, 0), (double)2.0F) + Math.pow((double)this.getMatrixValue(matrix, 3), (double)2.0F));
    }

    private float getMatrixAngle(Matrix matrix) {
        return (float)Math.toDegrees(-Math.atan2((double)this.getMatrixValue(matrix, 1), (double)this.getMatrixValue(matrix, 0)));
    }

    private float getMatrixValue(Matrix matrix, int valueIndex) {
        matrix.getValues(this.matrixValues);
        return this.matrixValues[valueIndex];
    }

    public boolean contains(float x, float y) {
        return this.contains(new float[]{x, y});
    }

    public boolean contains(float[] point) {
        Matrix tempMatrix = new Matrix();
        tempMatrix.setRotate(-this.getCurrentAngle());
        this.getBoundPoints(this.boundPoints);
        this.getMappedPoints(this.mappedBounds, this.boundPoints);
        tempMatrix.mapPoints(this.unrotatedWrapperCorner, this.mappedBounds);
        tempMatrix.mapPoints(this.unrotatedPoint, point);
        this.trapToRect(this.trappedRect, this.unrotatedWrapperCorner);
        return this.trappedRect.contains(this.unrotatedPoint[0], this.unrotatedPoint[1]);
    }

    private void trapToRect(RectF r, float[] array) {
        r.set(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY);

        for (int i = 1; i < array.length; i += 2) {
            float x = (float) Math.round(array[i - 1] * (float)10) / 10.0F;
            float y = (float) Math.round(array[i] * (float)10) / 10.0F;
            r.left = x < r.left ? x : r.left;
            r.top = y < r.top ? y : r.top;
            r.right = x > r.right ? x : r.right;
            r.bottom = y > r.bottom ? y : r.bottom;
        }

        r.sort();
    }
}
