package com.outsbook.libs.canvaseditor.paints;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import com.outsbook.libs.canvaseditor.enums.DrawType;
import com.outsbook.libs.canvaseditor.listeners.PaintViewListener;
import com.outsbook.libs.canvaseditor.models.DrawObject;
import com.outsbook.libs.canvaseditor.models.PathAndPaint;
import com.outsbook.libs.canvaseditor.stickers.Sticker;
import com.txkj.drawingapp.R;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

//no tools mode, just draw with a pen
public final class PaintView extends FrameLayout {
    @NotNull
    private final PaintViewListener paintViewListener;
    private final int drawColor;
    @NotNull
    private Path path;
    private float motionTouchEventX;
    private float motionTouchEventY;
    private float currentX;
    private float currentY;
    private boolean isDrawPath;
    private final int touchTolerance;
    private Canvas extraCanvas;
    public Bitmap extraBitmap;
    @NotNull
    private final Paint paint;
    @NotNull
    private final GestureDetector gestureDetector;

    public PaintView(@NotNull Context context, @NotNull PaintViewListener paintViewListener) {
        //Intrinsics.checkNotNullParameter(context, "context");
        //Intrinsics.checkNotNullParameter(paintViewListener, "paintViewListener");
        super(context);

        this.paintViewListener = paintViewListener;
        this.drawColor = ResourcesCompat.getColor(this.getResources(), 17170444, (Resources.Theme)null);
        this.path = new Path();
        this.touchTolerance = ViewConfiguration.get(context).getScaledTouchSlop();

        this.paint = new Paint();
        this.paint.setColor(this.drawColor);
        this.paint.setAntiAlias(true);
        this.paint.setDither(true);
        this.paint.setStyle(Style.STROKE);
        this.paint.setStrokeJoin(Join.ROUND);
        this.paint.setStrokeCap(Cap.ROUND);
        this.paint.setStrokeWidth(10.0F);

        this.gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(@NonNull MotionEvent event) {
                Intrinsics.checkNotNullParameter(event, "event");
                PaintView.this.paintViewListener.onClick(event.getX(), event.getY());
                return super.onSingleTapConfirmed(event);
            }
        });
    }

    @NotNull
    public Bitmap getExtraBitmap() {
        if (this.extraBitmap != null) {
            return this.extraBitmap;
        } else {
            Intrinsics.throwUninitializedPropertyAccessException("extraBitmap");
            return null;
        }
    }

    public void setExtraBitmap(@NotNull Bitmap var1) {
        Intrinsics.checkNotNullParameter(var1, "<set-?>");
        this.extraBitmap = var1;
    }

    @NotNull
    public Paint getPaint() {
        return this.paint;
    }

    public void initCanvas() {
        this.extraBitmap = Bitmap.createBitmap(this.getWidth(), this.getHeight(), Config.ARGB_8888);
        Intrinsics.checkNotNullExpressionValue(this.extraBitmap, "createBitmap(...)");
        this.extraCanvas = new Canvas(this.extraBitmap);
        this.extraCanvas.drawColor(ContextCompat.getColor(this.getContext(), R.color.white));
        this.invalidate();
    }

    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
        this.initCanvas();
    }

    protected void onDraw(@NotNull Canvas canvas) {
        Intrinsics.checkNotNullParameter(canvas, "canvas");
        super.onDraw(canvas);
        canvas.drawBitmap(this.getExtraBitmap(), 0.0F, 0.0F, null);
    }

    public void drawPath(@NotNull PathAndPaint pathAndPaint) {
        Intrinsics.checkNotNullParameter(pathAndPaint, "pathAndPaint");
        if (this.extraCanvas == null) {
            Intrinsics.throwUninitializedPropertyAccessException("extraCanvas");
            this.extraCanvas = null;
        }
        this.extraCanvas.drawPath(pathAndPaint.getPath(), pathAndPaint.getPaint());
        this.invalidate();
    }

    public void drawSticker(@NotNull Sticker sticker) {
        Intrinsics.checkNotNullParameter(sticker, "sticker");
        if (this.extraCanvas == null) {
            Intrinsics.throwUninitializedPropertyAccessException("extraCanvas");
            this.extraCanvas = null;
        }
        sticker.draw(this.extraCanvas);
        this.invalidate();
    }

    //@SuppressLint({"ClickableViewAccessibility"})
    @Override
    public boolean onTouchEvent(@NotNull MotionEvent event) {
        Intrinsics.checkNotNullParameter(event, "event");
        this.paintViewListener.onTouchEvent(event);
        this.motionTouchEventX = event.getX();
        this.motionTouchEventY = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                this.touchStart();
                break;

            case MotionEvent.ACTION_UP:
                this.touchUp();
                break;

            case MotionEvent.ACTION_MOVE:
                this.touchMove();
        }
        this.gestureDetector.onTouchEvent(event);
        return true;
    }

    private void touchStart() {
        this.path.reset();
        this.path.moveTo(this.motionTouchEventX, this.motionTouchEventY);
        this.currentX = this.motionTouchEventX;
        this.currentY = this.motionTouchEventY;
    }

    private void touchMove() {
        float dx = Math.abs(this.motionTouchEventX - this.currentX);
        float dy = Math.abs(this.motionTouchEventY - this.currentY);
        if (dx >= (float)this.touchTolerance || dy >= (float)this.touchTolerance) {
            this.path.quadTo(this.currentX, this.currentY, (this.motionTouchEventX + this.currentX) / (float)2, (this.motionTouchEventY + this.currentY) / (float)2);
            this.currentX = this.motionTouchEventX;
            this.currentY = this.motionTouchEventY;
            if (this.extraCanvas == null) {
                Intrinsics.throwUninitializedPropertyAccessException("extraCanvas");
                this.extraCanvas = null;
            }
            this.extraCanvas.drawPath(this.path, this.paint);
            this.isDrawPath = true;
        }
        this.invalidate();
    }

    private void touchUp() {
        if (this.isDrawPath) {
            //clone this.path and put to DrawObject
            PathAndPaint pap = new PathAndPaint(new Path(this.path), new Paint(this.paint));
            DrawObject obj = new DrawObject(pap, null, DrawType.PATH);
            this.paintViewListener.onTouchUp(obj);
        }
        this.invalidate();
        this.path.reset();
        this.isDrawPath = false;
    }
}
