package com.outsbook.libs.canvaseditor.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import com.outsbook.libs.canvaseditor.models.DrawObject;
import com.outsbook.libs.canvaseditor.models.PathAndPaint;
import com.outsbook.libs.canvaseditor.models.Sticker;
import com.txkj.drawingapp.R;

//no tools mode, just draw with a pen
public class PaintView extends FrameLayout {
    public interface PaintViewListener {
        void onTouchUp(DrawObject var1);
        void onClick(float var1, float var2);
        void onTouchEvent(MotionEvent var1);
    }
    private PaintViewListener paintViewListener;

    private int drawColor;
    private Path path;
    private float motionTouchEventX;
    private float motionTouchEventY;
    private float currentX;
    private float currentY;
    private boolean isDrawPath;
    private int touchTolerance;
    private Canvas extraCanvas;
    public Bitmap extraBitmap;
    private Paint paint;
    private GestureDetector gestureDetector;

    public PaintView(@NonNull Context context) {
        super(context);
        initView();
    }

    public PaintView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public PaintView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public PaintView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    private void initView() {
        this.drawColor = ResourcesCompat.getColor(this.getResources(), R.color.black, null);
        this.path = new Path();
        this.touchTolerance = ViewConfiguration.get(getContext()).getScaledTouchSlop();

        this.paint = new Paint();
        this.paint.setColor(this.drawColor);
        this.paint.setAntiAlias(true);
        this.paint.setDither(true);
        this.paint.setStyle(Style.STROKE);
        this.paint.setStrokeJoin(Join.ROUND);
        this.paint.setStrokeCap(Cap.ROUND);
        this.paint.setStrokeWidth(10.0F);

        this.gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(@NonNull MotionEvent event) {
                paintViewListener.onClick(event.getX(), event.getY());
                return super.onSingleTapConfirmed(event);
            }
        });
    }

    public void init(PaintViewListener paintViewListener) {
        this.paintViewListener = paintViewListener;
    }

    public Bitmap getExtraBitmap() {
        return this.extraBitmap;
    }

    public void setExtraBitmap(Bitmap extraBitmap) {
        this.extraBitmap = extraBitmap;
    }

    public Paint getPaint() {
        return this.paint;
    }

    public void initCanvas() {
        int w = this.getWidth();
        int h = this.getHeight();
        this.extraBitmap = Bitmap.createBitmap(w, h, Config.ARGB_8888);
        this.extraCanvas = new Canvas(this.extraBitmap);
        //this.extraCanvas.drawColor(0xFF00FF00);
        this.invalidate();
    }

    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
        this.initCanvas();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(this.getExtraBitmap(), 0.0F, 0.0F, null);
    }

    public void drawPath(PathAndPaint pathAndPaint) {
        this.extraCanvas.drawPath(pathAndPaint.getPath(), pathAndPaint.getPaint());
        this.invalidate();
    }

    public void drawSticker(Sticker sticker) {
        sticker.draw(this.extraCanvas);
        this.invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
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
            this.extraCanvas.drawPath(this.path, this.paint);
            this.isDrawPath = true;
        }
        this.invalidate();
    }

    private void touchUp() {
        if (this.isDrawPath) {
            //clone this.path and put to DrawObject
            PathAndPaint pap = new PathAndPaint(new Path(this.path), new Paint(this.paint));
            DrawObject obj = new DrawObject(pap, null, DrawObject.DrawType.PATH);
            this.paintViewListener.onTouchUp(obj);
        }
        this.invalidate();
        this.path.reset();
        this.isDrawPath = false;
    }
}
