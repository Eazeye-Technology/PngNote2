package com.outsbook.libs.canvaseditor.stickers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import com.outsbook.libs.canvaseditor.constants.ActionMode;
import com.outsbook.libs.canvaseditor.constants.ConstantSticker;
import com.outsbook.libs.canvaseditor.enums.DrawType;
import com.outsbook.libs.canvaseditor.events.DeleteIconEvent;
import com.outsbook.libs.canvaseditor.events.DoneIconEvent;
import com.outsbook.libs.canvaseditor.events.FlipIconEvent;
import com.outsbook.libs.canvaseditor.events.ZoomIconEvent;
import com.outsbook.libs.canvaseditor.listeners.StickerIconListener;
import com.outsbook.libs.canvaseditor.listeners.StickerViewListener;
import com.outsbook.libs.canvaseditor.models.DrawObject;
import com.outsbook.libs.canvaseditor.models.PathAndPaint;
import com.txkj.drawingapp.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class StickerView extends FrameLayout {
    @NotNull
    private final StickerViewListener stickerViewListener;
    @Nullable
    private Sticker currentSticker;
    private int currentMode;
    private boolean isTouchInsideSticker;
    @NotNull
    private final RectF stickerRect;
    @NotNull
    private final List<StickerIcon> icons;
    @NotNull
    private final float[] bitmapPoints;
    @NotNull
    private final float[] bounds;
    @NotNull
    private final float[] point;
    @NotNull
    private final PointF currentCenterPoint;
    @NotNull
    private final float[] tmp;
    @NotNull
    private PointF midPoint;
    @NotNull
    private final Matrix sizeMatrix;
    @NotNull
    private final Matrix downMatrix;
    @NotNull
    private final Matrix moveMatrix;
    private float downX;
    private float downY;
    private float oldDistance;
    private float oldRotation;
    @NotNull
    private final Paint borderPaint;
    @NotNull
    private final Paint iconPaint;
    private final int touchSlop;
    @Nullable
    private StickerIcon currentIcon;

    public StickerView(@NotNull Context context, @NotNull StickerViewListener stickerViewListener) {
        //Intrinsics.checkNotNullParameter(context, "context");
        //Intrinsics.checkNotNullParameter(stickerViewListener, "stickerViewListener");
        super(context);
        this.stickerViewListener = stickerViewListener;
        this.currentMode = ActionMode.Companion.getNONE();
        this.stickerRect = new RectF();
        this.icons = (List)(new ArrayList(4));
        this.bitmapPoints = new float[8];
        this.bounds = new float[8];
        this.point = new float[2];
        this.currentCenterPoint = new PointF();
        this.tmp = new float[2];
        this.midPoint = new PointF();
        this.sizeMatrix = new Matrix();
        this.downMatrix = new Matrix();
        this.moveMatrix = new Matrix();
        Paint var3 = new Paint();
        int var5 = 0;
        var3.setAntiAlias(true);
        var3.setColor(-16777216);
        var3.setAlpha(50);
        this.borderPaint = var3;
        var3 = new Paint();
        var5 = 0;
        var3.setAntiAlias(true);
        var3.setColor(-16777216);
        var3.setAlpha(128);
        this.iconPaint = var3;
        this.touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        this.configDefaultIcons();
    }

    @Nullable
    public Sticker getCurrentSticker() {
        return this.currentSticker;
    }

    public void setCurrentSticker(@Nullable Sticker var1) {
        this.currentSticker = var1;
    }

    private void configDefaultIcons() {
        StickerIcon deleteIcon = new StickerIcon(ContextCompat.getDrawable(this.getContext(), R.drawable.ic_close_white_20dp), 0);
        deleteIcon.setIconListener(new DeleteIconEvent());
        StickerIcon doneIcon = new StickerIcon(ContextCompat.getDrawable(this.getContext(), R.drawable.ic_done_white_20dp), 1);
        doneIcon.setIconListener(new DoneIconEvent());
        StickerIcon zoomIcon = new StickerIcon(ContextCompat.getDrawable(this.getContext(), R.drawable.ic_rotate_scale_white_17dp), 3);
        zoomIcon.setIconListener(new ZoomIconEvent());
        StickerIcon flipIcon = new StickerIcon(ContextCompat.getDrawable(this.getContext(), R.drawable.ic_flip_white_20dp), 2);
        flipIcon.setIconListener(new FlipIconEvent());
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

    protected void dispatchDraw(@NotNull Canvas canvas) {
        Intrinsics.checkNotNullParameter(canvas, "canvas");
        super.dispatchDraw(canvas);
        this.drawStickers(canvas);
    }

    public boolean onInterceptTouchEvent(@NotNull MotionEvent ev) {
        Intrinsics.checkNotNullParameter(ev, "ev");
        if (ev.getAction() != 0) {
            return super.onInterceptTouchEvent(ev);
        } else {
            this.downX = ev.getX();
            this.downY = ev.getY();
            return this.findCurrentIconTouched() != null || this.currentSticker != null;
        }
    }

    @SuppressLint({"ClickableViewAccessibility"})
    public boolean onTouchEvent(@NotNull MotionEvent event) {
        Intrinsics.checkNotNullParameter(event, "event");
        this.stickerViewListener.onTouchEvent(event);
        switch (event.getActionMasked()) {
            case 0:
                if (!this.onTouchDown(event)) {
                    return false;
                }
                break;

            case 1:
                this.onTouchUp(event);
                break;

            case 2:
                this.handleCurrentMode(event);
                this.invalidate();
                break;

            case 5:
                this.oldDistance = this.calculateDistance(event);
                this.oldRotation = this.calculateRotation(event);
                this.midPoint = this.calculateMidPoint(event);
                if (this.currentSticker != null) {
                    Intrinsics.checkNotNull(this.currentSticker);
                    if (this.isInStickerArea(this.currentSticker, event.getX(1), event.getY(1)) && this.findCurrentIconTouched() == null) {
                        this.currentMode = ActionMode.Companion.getZOOM_WITH_TWO_FINGER();
                    }
                }
                break;

            case 6:
                this.currentMode = ActionMode.Companion.getNONE();
                break;

            case 3:
            case 4:
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
                    case 0:
                        this.configIconMatrix(icon, x1, y1, rotation);
                        break;

                    case 1:
                        this.configIconMatrix(icon, x2, y2, rotation);
                        break;

                    case 2:
                        this.configIconMatrix(icon, x3, y3, rotation);
                        break;

                    case 3:
                        this.configIconMatrix(icon, x4, y4, rotation);
                        break;
                }

                icon.draw(canvas, this.iconPaint);
            }
        }

    }

    private final void getStickerPoints(Sticker sticker, float[] dst) {
        if (sticker == null) {
            Arrays.fill(dst, 0.0F);
        } else {
            sticker.getBoundPoints(this.bounds);
            sticker.getMappedPoints(dst, this.bounds);
        }
    }

    private final float calculateDistance(MotionEvent event) {
        return event != null && event.getPointerCount() >= 2 ? this.calculateDistance(event.getX(0), event.getY(0), event.getX(1), event.getY(1)) : 0.0F;
    }

    private final float calculateDistance(float x1, float y1, float x2, float y2) {
        double x = (double)x1 - (double)x2;
        double y = (double)y1 - (double)y2;
        return (float)Math.sqrt(x * x + y * y);
    }

    private final float calculateRotation(MotionEvent event) {
        return event != null && event.getPointerCount() >= 2 ? this.calculateRotation(event.getX(0), event.getY(0), event.getX(1), event.getY(1)) : 0.0F;
    }

    private final float calculateRotation(float x1, float y1, float x2, float y2) {
        double x = (double)x1 - (double)x2;
        double y = (double)y1 - (double)y2;
        double radians = Math.atan2(y, x);
        return (float)Math.toDegrees(radians);
    }

    private final void configIconMatrix(StickerIcon icon, float x, float y, float rotation) {
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
        this.currentMode = ActionMode.Companion.getDRAG();
        this.downX = event.getX();
        this.downY = event.getY();
        this.midPoint = this.calculateMidPoint();
        this.oldDistance = this.calculateDistance(this.midPoint.x, this.midPoint.y, this.downX, this.downY);
        this.oldRotation = this.calculateRotation(this.midPoint.x, this.midPoint.y, this.downX, this.downY);
        this.currentIcon = this.findCurrentIconTouched();
        if (this.currentIcon != null) {
            this.currentMode = ActionMode.Companion.getICON();
            StickerIcon var10000 = this.currentIcon;
            Intrinsics.checkNotNull(var10000);
            var10000.onActionDown(this, event);
        }

        if (this.currentSticker != null) {
            Sticker var10001 = this.currentSticker;
            Intrinsics.checkNotNull(var10001);
            this.isTouchInsideSticker = var10001.contains(this.downX, this.downY);
            Matrix var2 = this.downMatrix;
            Sticker.Companion var3 = Sticker.Companion;
            Sticker var10002 = this.currentSticker;
            Intrinsics.checkNotNull(var10002);
            var2.set(var3.getMatrix(var10002));
        }

        if (this.currentIcon == null && !this.isTouchInsideSticker) {
            this.doneSticker(this.currentSticker);
            return false;
        } else {
            this.invalidate();
            return true;
        }
    }

    private final void handleCurrentMode(MotionEvent event) {
        int var2 = this.currentMode;
        if (var2 != ActionMode.Companion.getNONE() && var2 != ActionMode.Companion.getCLICK()) {
            if (var2 == ActionMode.Companion.getDRAG()) {
                if (this.currentSticker != null && this.isTouchInsideSticker) {
                    this.moveMatrix.set(this.downMatrix);
                    this.moveMatrix.postTranslate(event.getX() - this.downX, event.getY() - this.downY);
                    Sticker var10000 = this.currentSticker;
                    Intrinsics.checkNotNull(var10000);
                    var10000.setMatrix(this.moveMatrix);
                }
            } else if (var2 == ActionMode.Companion.getZOOM_WITH_TWO_FINGER()) {
                if (this.currentSticker != null && this.isTouchInsideSticker) {
                    float newDistance = this.calculateDistance(event);
                    float newRotation = this.calculateRotation(event);
                    this.moveMatrix.set(this.downMatrix);
                    this.moveMatrix.postScale(newDistance / this.oldDistance, newDistance / this.oldDistance, this.midPoint.x, this.midPoint.y);
                    this.moveMatrix.postRotate(newRotation - this.oldRotation, this.midPoint.x, this.midPoint.y);
                    Sticker var5 = this.currentSticker;
                    Intrinsics.checkNotNull(var5);
                    var5.setMatrix(this.moveMatrix);
                }
            } else if (var2 == ActionMode.Companion.getICON() && this.currentSticker != null && this.currentIcon != null) {
                StickerIcon var6 = this.currentIcon;
                Intrinsics.checkNotNull(var6);
                var6.onActionMove(this, event);
            }
        }

    }

    private final void onTouchUp(MotionEvent event) {
        if (this.currentMode == ActionMode.Companion.getICON() && this.currentIcon != null && this.currentSticker != null) {
            StickerIcon var10000 = this.currentIcon;
            Intrinsics.checkNotNull(var10000);
            var10000.onActionUp(this, event);
        }

        if (this.currentMode == ActionMode.Companion.getDRAG() && Math.abs(event.getX() - this.downX) < (float)this.touchSlop && Math.abs(event.getY() - this.downY) < (float)this.touchSlop && this.currentSticker != null) {
            if (!this.isTouchInsideSticker) {
                this.stickerViewListener.onClickStickerOutside(event.getX(), event.getY());
            }

            this.currentMode = ActionMode.Companion.getCLICK();
        }

        this.currentMode = ActionMode.Companion.getNONE();
    }

    private final PointF calculateMidPoint(MotionEvent event) {
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

    private final PointF calculateMidPoint() {
        if (this.currentSticker == null) {
            this.midPoint.set(0.0F, 0.0F);
            return this.midPoint;
        } else {
            Sticker var10000 = this.currentSticker;
            if (var10000 != null) {
                var10000.getMappedCenterPoint(this.midPoint, this.point, this.tmp);
            }

            return this.midPoint;
        }
    }

    private final boolean isInStickerArea(Sticker sticker, float downX, float downY) {
        this.tmp[0] = downX;
        this.tmp[1] = downY;
        return sticker.contains(this.tmp);
    }

    @NotNull
    public final StickerView addSticker(@NotNull Sticker sticker) {
        Intrinsics.checkNotNullParameter(sticker, "sticker");
        return this.addSticker(sticker, ConstantSticker.Companion.getCENTER());
    }

    private final StickerView addSticker(Sticker sticker, int position) {
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

    private final void setStickerPosition(Sticker sticker, int position) {
        float width = (float)this.getWidth();
        float height = (float)this.getHeight();
        float offsetX = width - (float)sticker.getWidth();
        float offsetY = height - (float)sticker.getHeight();
        if ((position & ConstantSticker.Companion.getTOP()) > 0) {
            offsetY /= 4.0F;
        } else if ((position & ConstantSticker.Companion.getBOTTOM()) > 0) {
            offsetY *= 0.75F;
        } else {
            offsetY /= 2.0F;
        }

        if ((position & ConstantSticker.Companion.getLEFT()) > 0) {
            offsetX /= 4.0F;
        } else if ((position & ConstantSticker.Companion.getRIGHT()) > 0) {
            offsetX *= 0.75F;
        } else {
            offsetX /= 2.0F;
        }

        sticker.getMatrix().postTranslate(offsetX, offsetY);
    }

    private final void removeSticker(Sticker sticker) {
        if (sticker != null) {
            this.currentSticker = null;
            this.setVisibility(8);
            this.stickerViewListener.onRemove();
        }
    }

    private void doneSticker(Sticker sticker) {
        if (sticker != null) {
            this.currentSticker = null;
            this.setVisibility(8);
            DrawObject obj = new DrawObject(null, sticker, DrawType.STICKER);
            this.stickerViewListener.onDone(obj);
        }
    }

    private void zoomAndRotateSticker(Sticker sticker, MotionEvent event) {
        if (sticker != null) {
            float newDistance = this.calculateDistance(this.midPoint.x, this.midPoint.y, event.getX(), event.getY());
            float newRotation = this.calculateRotation(this.midPoint.x, this.midPoint.y, event.getX(), event.getY());
            this.moveMatrix.set(this.downMatrix);
            this.moveMatrix.postScale(newDistance / this.oldDistance, newDistance / this.oldDistance, this.midPoint.x, this.midPoint.y);
            this.moveMatrix.postRotate(newRotation - this.oldRotation, this.midPoint.x, this.midPoint.y);
            Intrinsics.checkNotNull(this.currentSticker);
            this.currentSticker.setMatrix(this.moveMatrix);
            this.stickerViewListener.onZoomAndRotate();
        }
    }

    private final void flipSticker(Sticker sticker) {
        if (sticker != null) {
            sticker.getCenterPoint(this.midPoint);
            sticker.getMatrix().preScale(-1.0F, 1.0F, this.midPoint.x, this.midPoint.y);
            sticker.setFlippedHorizontally(!sticker.isFlippedHorizontally());
            this.invalidate();
            this.stickerViewListener.onFlip();
        }
    }

    public final void remove() {
        this.removeSticker(this.currentSticker);
    }

    public final void done() {
        this.doneSticker(this.currentSticker);
    }

    public final void zoomAndRotate(@NotNull MotionEvent event) {
        Intrinsics.checkNotNullParameter(event, "event");
        this.zoomAndRotateSticker(this.currentSticker, event);
    }

    public final void flip() {
        this.flipSticker(this.currentSticker);
    }

//    private static final void addSticker$lambda$3(StickerView this$0, Sticker $sticker, int $position) {
//        this$0.addStickerImmediately($sticker, $position);
//    }
}
