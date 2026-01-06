package com.outsbook.libs.canvaseditor.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

import com.outsbook.libs.canvaseditor.models.DrawObject;
import com.outsbook.libs.canvaseditor.models.Sticker;
import com.outsbook.libs.canvaseditor.models.StickerIcon;
import com.txkj.drawingapp.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StickerView extends FrameLayout {
    public interface StickerViewListener {
        void onRemove();
        void onDone(DrawObject var1);
        void onZoomAndRotate();
        void onFlip();
        void onClickStickerOutside(float var1, float var2);
        void onTouchEvent(MotionEvent var1);
    }
    private StickerViewListener stickerViewListener;
    private Sticker currentSticker;

    public static int NONE = 0;
    public static int DRAG = 1;
    public static int ZOOM_WITH_TWO_FINGER = 2;
    public static int ICON = 3;
    public static int CLICK = 4;
    private int currentMode;

    private boolean isTouchInsideSticker;
    private RectF stickerRect;
    private List<StickerIcon> icons;
    private float[] bitmapPoints;
    private float[] bounds;
    private float[] point;
    private PointF currentCenterPoint;
    private float[] tmp;
    private PointF midPoint;
    private Matrix sizeMatrix;
    private Matrix downMatrix;
    private Matrix moveMatrix;
    private float downX;
    private float downY;
    private float oldDistance;
    private float oldRotation;
    private Paint borderPaint;
    private Paint iconPaint;
    private int touchSlop;
    private StickerIcon currentIcon;

    public StickerView(@NonNull Context context) {
        super(context);
        initView();
    }

    public StickerView(@NonNull Context context, @androidx.annotation.Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public StickerView(@NonNull Context context, @androidx.annotation.Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public StickerView(@NonNull Context context, @androidx.annotation.Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    public void initView() {
        this.currentMode = NONE;
        this.stickerRect = new RectF();
        this.icons = new ArrayList<>();
        this.bitmapPoints = new float[8];
        this.bounds = new float[8];
        this.point = new float[2];
        this.currentCenterPoint = new PointF();
        this.tmp = new float[2];
        this.midPoint = new PointF();
        this.sizeMatrix = new Matrix();
        this.downMatrix = new Matrix();
        this.moveMatrix = new Matrix();

        this.iconPaint = new Paint();
        this.iconPaint.setAntiAlias(true);
        this.iconPaint.setColor(0xFF000000);
        this.iconPaint.setAlpha(50);

        this.borderPaint = new Paint();
        this.borderPaint.setAntiAlias(true);
        this.borderPaint.setColor(0xFF000000);
        this.borderPaint.setAlpha(128);

        this.touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        this.configDefaultIcons();
    }

    public void init(StickerViewListener stickerViewListener) {
        this.stickerViewListener = stickerViewListener;
    }

    public Sticker getCurrentSticker() {
        return this.currentSticker;
    }

    public void setCurrentSticker(Sticker var1) {
        this.currentSticker = var1;
    }

    private void configDefaultIcons() {
        StickerIcon deleteIcon = new StickerIcon(ContextCompat.getDrawable(this.getContext(), R.drawable.ic_close_white_20dp), StickerIcon.LEFT_TOP);
        deleteIcon.setIconListener(new StickerIcon.StickerIconListener() {
            public void onActionDown(StickerView stickerView, MotionEvent event) {
            }

            public void onActionMove(StickerView stickerView, MotionEvent event) {
            }

            public void onActionUp(StickerView stickerView, MotionEvent event) {
                stickerView.remove();
            }
        });
        StickerIcon doneIcon = new StickerIcon(ContextCompat.getDrawable(this.getContext(), R.drawable.ic_done_white_20dp), StickerIcon.RIGHT_TOP);
        doneIcon.setIconListener(new StickerIcon.StickerIconListener() {
            public void onActionDown(StickerView stickerView, MotionEvent event) {

            }

            public void onActionMove(StickerView stickerView, MotionEvent event) {
            }

            public void onActionUp(StickerView stickerView, MotionEvent event) {
                stickerView.done();
            }
        });
        StickerIcon zoomIcon = new StickerIcon(ContextCompat.getDrawable(this.getContext(), R.drawable.ic_rotate_scale_white_17dp), StickerIcon.RIGHT_BOTTOM);
        zoomIcon.setIconListener(new StickerIcon.StickerIconListener() {
            public void onActionDown(StickerView stickerView, MotionEvent event) {
            }

            public void onActionMove(StickerView stickerView, MotionEvent event) {
                stickerView.zoomAndRotate(event);
            }

            public void onActionUp(StickerView stickerView, MotionEvent event) {
            }
        });
        StickerIcon flipIcon = new StickerIcon(ContextCompat.getDrawable(this.getContext(), R.drawable.ic_flip_white_20dp), StickerIcon.LEFT_BOTTOM);
        flipIcon.setIconListener(new StickerIcon.StickerIconListener() {
            public void onActionDown(StickerView stickerView, MotionEvent event) {
            }

            public void onActionMove(StickerView stickerView, MotionEvent event) {
            }

            public void onActionUp(StickerView stickerView, MotionEvent event) {
                stickerView.flip();
            }
        });
        this.icons.clear();
        this.icons.add(deleteIcon);
        this.icons.add(doneIcon);
        this.icons.add(zoomIcon);
        this.icons.add(flipIcon);
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (this.currentSticker != null) {
            this.transformSticker(this.currentSticker);
        }
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            this.stickerRect.left = (float)left;
            this.stickerRect.top = (float)top;
            this.stickerRect.right = (float)right;
            this.stickerRect.bottom = (float)bottom;
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        this.drawStickers(canvas);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() != 0) {
            return super.onInterceptTouchEvent(ev);
        } else {
            this.downX = ev.getX();
            this.downY = ev.getY();
            return this.findCurrentIconTouched() != null || this.currentSticker != null;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.stickerViewListener.onTouchEvent(event);
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                if (!this.onTouchDown(event)) {
                    return false;
                }
                break;

            case MotionEvent.ACTION_UP:
                this.onTouchUp(event);
                break;

            case MotionEvent.ACTION_MOVE:
                this.handleCurrentMode(event);
                this.invalidate();
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                this.oldDistance = this.calculateDistance(event);
                this.oldRotation = this.calculateRotation(event);
                this.midPoint = this.calculateMidPoint(event);
                if (this.currentSticker != null) {
                    if (this.isInStickerArea(this.currentSticker,
                            event.getX(1),
                            event.getY(1)) &&
                            this.findCurrentIconTouched() == null) {
                        this.currentMode = ZOOM_WITH_TWO_FINGER;
                    }
                }
                break;

            case MotionEvent.ACTION_POINTER_UP:
                this.currentMode = NONE;
                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_OUTSIDE:
            default:
                break;
        }

        return true;
    }

    private void drawStickers(Canvas canvas) {
        if (this.currentSticker != null) {
            this.currentSticker.draw(canvas);
        }
        if (this.currentSticker != null) {
            this.getStickerPoints(this.currentSticker, this.bitmapPoints);
            float x1 = this.bitmapPoints[0];
            float y1 = this.bitmapPoints[1];

            float x2 = this.bitmapPoints[2];
            float y2 = this.bitmapPoints[3];

            float x3 = this.bitmapPoints[4];
            float y3 = this.bitmapPoints[5];

            float x4 = this.bitmapPoints[6];
            float y4 = this.bitmapPoints[7];

            canvas.drawLine(x1, y1, x2, y2, this.borderPaint);
            canvas.drawLine(x1, y1, x3, y3, this.borderPaint);
            canvas.drawLine(x2, y2, x4, y4, this.borderPaint);
            canvas.drawLine(x4, y4, x3, y3, this.borderPaint);

            float rotation = this.calculateRotation(x4, y4, x3, y3);
            for (int i = 0; i < this.icons.size(); ++i) {
                StickerIcon icon = this.icons.get(i);
                switch (icon.getPosition()) {
                    case StickerIcon.LEFT_TOP:
                        this.configIconMatrix(icon, x1, y1, rotation);
                        break;

                    case StickerIcon.RIGHT_TOP:
                        this.configIconMatrix(icon, x2, y2, rotation);
                        break;

                    case StickerIcon.LEFT_BOTTOM:
                        this.configIconMatrix(icon, x3, y3, rotation);
                        break;

                    case StickerIcon.RIGHT_BOTTOM:
                        this.configIconMatrix(icon, x4, y4, rotation);
                        break;
                }

                icon.draw(canvas, this.iconPaint);
            }
        }

    }

    private void getStickerPoints(Sticker sticker, float[] dst) {
        if (sticker == null) {
            Arrays.fill(dst, 0.0F);
        } else {
            sticker.getBoundPoints(this.bounds);
            sticker.getMappedPoints(dst, this.bounds);
        }
    }

    private float calculateDistance(MotionEvent event) {
        return event != null && event.getPointerCount() >= 2 ? this.calculateDistance(event.getX(0), event.getY(0), event.getX(1), event.getY(1)) : 0.0F;
    }

    private float calculateDistance(float x1, float y1, float x2, float y2) {
        double x = (double)x1 - (double)x2;
        double y = (double)y1 - (double)y2;
        return (float)Math.sqrt(x * x + y * y);
    }

    private float calculateRotation(MotionEvent event) {
        return event != null && event.getPointerCount() >= 2 ? this.calculateRotation(event.getX(0), event.getY(0), event.getX(1), event.getY(1)) : 0.0F;
    }

    private float calculateRotation(float x1, float y1, float x2, float y2) {
        double x = (double)x1 - (double)x2;
        double y = (double)y1 - (double)y2;
        double radians = Math.atan2(y, x);
        return (float)Math.toDegrees(radians);
    }

    private void configIconMatrix(StickerIcon icon, float x, float y, float rotation) {
        icon.setX(x);
        icon.setY(y);
        icon.getMatrix().reset();
        icon.getMatrix().postRotate(rotation, (float)icon.getWidth() / 2.0F, (float)icon.getHeight() / 2.0F);
        icon.getMatrix().postTranslate(x - (float)(icon.getWidth() / 2), y - (float)(icon.getHeight() / 2));
    }

    private void transformSticker(Sticker sticker) {
        this.sizeMatrix.reset();
        float width = (float)this.getWidth();
        float height = (float)this.getHeight();
        float stickerWidth = (float)sticker.getWidth();
        float stickerHeight = (float)sticker.getHeight();
        float offsetX = (width - stickerWidth) / (float)2;
        float offsetY = (height - stickerHeight) / (float)2;
        this.sizeMatrix.postTranslate(offsetX, offsetY);
        float scaleFactor = 0.0F;
        scaleFactor = width < height ? width / stickerWidth : height / stickerHeight;
        this.sizeMatrix.postScale(scaleFactor / 2.0F, scaleFactor / 2.0F, width / 2.0F, height / 2.0F);
        sticker.getMatrix().reset();
        sticker.setMatrix(this.sizeMatrix);
        this.invalidate();
    }

    private StickerIcon findCurrentIconTouched() {
        for (StickerIcon icon : this.icons) {
            float x = icon.getX() - this.downX;
            float y = icon.getY() - this.downY;
            float distancePow2 = x * x + y * y;
            if ((double)distancePow2 <= Math.pow((double)icon.getIconRadius() + (double)icon.getIconRadius(), (double)2.0F)) {
                return icon;
            }
        }
        return null;
    }

    private boolean onTouchDown(MotionEvent event) {
        this.currentMode = DRAG;
        this.downX = event.getX();
        this.downY = event.getY();
        this.midPoint = this.calculateMidPoint();
        this.oldDistance = this.calculateDistance(this.midPoint.x, this.midPoint.y, this.downX, this.downY);
        this.oldRotation = this.calculateRotation(this.midPoint.x, this.midPoint.y, this.downX, this.downY);
        this.currentIcon = this.findCurrentIconTouched();
        if (this.currentIcon != null) {
            this.currentMode = ICON;
            this.currentIcon.onActionDown(this, event);
        }

        if (this.currentSticker != null) {
            this.isTouchInsideSticker = this.currentSticker.contains(this.downX, this.downY);
            this.downMatrix.set(this.currentSticker.getMatrix());
        }

        if (this.currentIcon == null && !this.isTouchInsideSticker) {
            this.doneSticker(this.currentSticker);
            return false;
        } else {
            this.invalidate();
            return true;
        }
    }

    private void handleCurrentMode(MotionEvent event) {
        if (this.currentMode != NONE && this.currentMode != CLICK) {
            if (this.currentMode == DRAG) {
                if (this.currentSticker != null && this.isTouchInsideSticker) {
                    this.moveMatrix.set(this.downMatrix);
                    this.moveMatrix.postTranslate(event.getX() - this.downX, event.getY() - this.downY);
                    this.currentSticker.setMatrix(this.moveMatrix);
                }
            } else if (this.currentMode == ZOOM_WITH_TWO_FINGER) {
                if (this.currentSticker != null && this.isTouchInsideSticker) {
                    float newDistance = this.calculateDistance(event);
                    float newRotation = this.calculateRotation(event);
                    this.moveMatrix.set(this.downMatrix);
                    this.moveMatrix.postScale(newDistance / this.oldDistance, newDistance / this.oldDistance, this.midPoint.x, this.midPoint.y);
                    this.moveMatrix.postRotate(newRotation - this.oldRotation, this.midPoint.x, this.midPoint.y);
                    this.currentSticker.setMatrix(this.moveMatrix);
                }
            } else if (this.currentMode == ICON && this.currentSticker != null && this.currentIcon != null) {
                this.currentIcon.onActionMove(this, event);
            }
        }

    }

    private void onTouchUp(MotionEvent event) {
        if (this.currentMode == ICON && this.currentIcon != null && this.currentSticker != null) {
            this.currentIcon.onActionUp(this, event);
        }
        if (this.currentMode == DRAG && Math.abs(event.getX() - this.downX) < (float)this.touchSlop && Math.abs(event.getY() - this.downY) < (float)this.touchSlop && this.currentSticker != null) {
            if (!this.isTouchInsideSticker) {
                this.stickerViewListener.onClickStickerOutside(event.getX(), event.getY());
            }
            this.currentMode = CLICK;
        }
        this.currentMode = NONE;
    }

    private PointF calculateMidPoint(MotionEvent event) {
        if (event != null && event.getPointerCount() >= 2) {
            float x = (event.getX(0) + event.getX(1)) / (float)2;
            float y = (event.getY(0) + event.getY(1)) / (float)2;
            this.midPoint.set(x, y);
            return this.midPoint;
        } else {
            this.midPoint.set(0.0F, 0.0F);
            return this.midPoint;
        }
    }

    private PointF calculateMidPoint() {
        if (this.currentSticker == null) {
            this.midPoint.set(0.0F, 0.0F);
            return this.midPoint;
        } else {
            if (this.currentSticker != null) {
                this.currentSticker.getMappedCenterPoint(this.midPoint, this.point, this.tmp);
            }
            return this.midPoint;
        }
    }

    private boolean isInStickerArea(Sticker sticker, float downX, float downY) {
        this.tmp[0] = downX;
        this.tmp[1] = downY;
        return sticker.contains(this.tmp);
    }

    public StickerView addSticker(Sticker sticker) {
        return this.addSticker(sticker, CENTER);
    }

    private StickerView addSticker(Sticker sticker, int position) {
        if (ViewCompat.isLaidOut((View)this)) {
            this.addStickerImmediately(sticker, position);
        } else {
            this.post(new Runnable() {
                @Override
                public void run() {
                    addStickerImmediately(sticker, position);
                }
            });
        }

        return this;
    }

    private void addStickerImmediately(Sticker sticker, int position) {
        this.setStickerPosition(sticker, position);
        float scaleFactor = 0.0F;
        float widthScaleFactor = (float)this.getWidth() / (float)sticker.getDrawable().getIntrinsicWidth();
        float heightScaleFactor = (float)this.getHeight() / (float)sticker.getDrawable().getIntrinsicHeight();
        scaleFactor = widthScaleFactor > heightScaleFactor ? heightScaleFactor : widthScaleFactor;
        sticker.getMatrix().postScale(scaleFactor / (float)2, scaleFactor / (float)2, (float)this.getWidth() / 2.0F, (float)this.getHeight() / 2.0F);
        this.currentSticker = sticker;
        this.invalidate();
    }

    public static int CENTER = 1;
    public static int TOP = 2;
    public static int LEFT = 4;
    public static int RIGHT = 8;
    public static int BOTTOM = 16;
    private void setStickerPosition(Sticker sticker, int position) {
        float width = (float)this.getWidth();
        float height = (float)this.getHeight();
        float offsetX = width - (float)sticker.getWidth();
        float offsetY = height - (float)sticker.getHeight();
        if ((position & TOP) > 0) {
            offsetY /= 4.0F;
        } else if ((position & BOTTOM) > 0) {
            offsetY *= 0.75F;
        } else {
            offsetY /= 2.0F;
        }
        if ((position & LEFT) > 0) {
            offsetX /= 4.0F;
        } else if ((position & RIGHT) > 0) {
            offsetX *= 0.75F;
        } else {
            offsetX /= 2.0F;
        }
        sticker.getMatrix().postTranslate(offsetX, offsetY);
    }

    private void removeSticker(Sticker sticker) {
        if (sticker != null) {
            this.currentSticker = null;
            this.setVisibility(View.GONE);
            this.stickerViewListener.onRemove();
        }
    }

    private void doneSticker(Sticker sticker) {
        if (sticker != null) {
            this.currentSticker = null;
            this.setVisibility(View.GONE);
            DrawObject obj = new DrawObject(null, sticker, DrawObject.DrawType.STICKER);
            this.stickerViewListener.onDone(obj);
        }
    }

    //FIXME:scale
    final static boolean NO_ROTATE = true;
    private void zoomAndRotateSticker(Sticker sticker, MotionEvent event) {
        if (sticker != null) {
            float newDistance = this.calculateDistance(this.midPoint.x, this.midPoint.y, event.getX(), event.getY());
            float newRotation = this.calculateRotation(this.midPoint.x, this.midPoint.y, event.getX(), event.getY());
            this.moveMatrix.set(this.downMatrix);
            this.moveMatrix.postScale(newDistance / this.oldDistance, newDistance / this.oldDistance, this.midPoint.x, this.midPoint.y);
            if (!NO_ROTATE) {
                this.moveMatrix.postRotate(newRotation - this.oldRotation, this.midPoint.x, this.midPoint.y);
            }
            this.currentSticker.setMatrix(this.moveMatrix);
            this.stickerViewListener.onZoomAndRotate();
        }
    }

    private void flipSticker(Sticker sticker) {
        if (sticker != null) {
            sticker.getCenterPoint(this.midPoint);
            sticker.getMatrix().preScale(-1.0F, 1.0F, this.midPoint.x, this.midPoint.y);
            sticker.setFlippedHorizontally(!sticker.isFlippedHorizontally());
            this.invalidate();
            this.stickerViewListener.onFlip();
        }
    }

    public void remove() {
        this.removeSticker(this.currentSticker);
    }

    public void done() {
        this.doneSticker(this.currentSticker);
    }

    public void zoomAndRotate(MotionEvent event) {
        this.zoomAndRotateSticker(this.currentSticker, event);
    }

    public void flip() {
        this.flipSticker(this.currentSticker);
    }
}
