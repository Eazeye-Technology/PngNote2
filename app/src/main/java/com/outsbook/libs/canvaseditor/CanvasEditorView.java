package com.outsbook.libs.canvaseditor;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import com.outsbook.libs.canvaseditor.enums.DrawType;
import com.outsbook.libs.canvaseditor.listeners.CanvasEditorListener;
import com.outsbook.libs.canvaseditor.listeners.PaintViewListener;
import com.outsbook.libs.canvaseditor.listeners.StickerViewListener;
import com.outsbook.libs.canvaseditor.models.DrawObject;
import com.outsbook.libs.canvaseditor.models.PathAndPaint;
import com.outsbook.libs.canvaseditor.paints.PaintView;
import com.outsbook.libs.canvaseditor.stickers.BitmapSticker;
import com.outsbook.libs.canvaseditor.stickers.DrawableSticker;
import com.outsbook.libs.canvaseditor.stickers.Sticker;
import com.outsbook.libs.canvaseditor.stickers.StickerView;
import com.outsbook.libs.canvaseditor.stickers.TextSticker;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.JvmOverloads;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class CanvasEditorView extends RelativeLayout {
    @NotNull
    private final List<DrawObject> mUndoList;
    @NotNull
    private final List<DrawObject> mRedoList;
    @NotNull
    private final PaintViewListener paintViewListener;
    @NotNull
    private final StickerViewListener stickerViewListener;
    @NotNull
    private final PaintView mPaintView;
    @NotNull
    private final StickerView mStickerView;
    @Nullable
    private CanvasEditorListener mListener;

    @JvmOverloads
    public CanvasEditorView(@NotNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        //Intrinsics.checkNotNullParameter(context, "context");
        super(context, attrs, defStyleAttr);
        this.mUndoList = new ArrayList<DrawObject>();
        this.mRedoList = new ArrayList<DrawObject>();
        this.paintViewListener = new PaintViewListener() {
            @Override
            public void onTouchUp(@NonNull DrawObject obj) {
                Intrinsics.checkNotNullParameter(obj, "obj");
                CanvasEditorView.this.mUndoList.add(obj);
                CanvasEditorView.this.mRedoList.clear();
                if (CanvasEditorView.this.mListener != null) {
                    CanvasEditorView.this.mListener.onEnableUndo(true);
                }
                if (CanvasEditorView.this.mListener != null) {
                    CanvasEditorView.this.mListener.onEnableRedo(false);
                }
            }

            @Override
            public void onClick(float x, float y) {
                int pos = CanvasEditorView.this.findTapedSticker(x, y);
                if (pos > -1) {
                    CanvasEditorView.this.enableEditModeSticker(pos);
                }
            }

            public void onTouchEvent(@NonNull MotionEvent event) {
                Intrinsics.checkNotNullParameter(event, "event");
                if (CanvasEditorView.this.mListener != null) {
                    CanvasEditorView.this.mListener.onTouchEvent(event);
                }
            }
        };
        this.stickerViewListener = new StickerViewListener() {
            @Override
            public void onRemove() {
                if (CanvasEditorView.this.mListener != null) {
                    CanvasEditorView.this.mListener.onStickerRemove();
                }
                if (CanvasEditorView.this.mListener != null) {
                    CanvasEditorView.this.mListener.onEnableUndo(!CanvasEditorView.this.mUndoList.isEmpty());
                }
            }

            @Override
            public void onDone(@NonNull DrawObject obj) {
                Intrinsics.checkNotNullParameter(obj, "obj");
                CanvasEditorView.this.addStickerToPaint(obj);
                if (CanvasEditorView.this.mListener != null) {
                    CanvasEditorView.this.mListener.onStickerDone();
                }
            }

            public void onZoomAndRotate() {
                CanvasEditorListener var10000 = CanvasEditorView.this.mListener;
                if (var10000 != null) {
                    var10000.onStickerZoomAndRotate();
                }
            }

            @Override
            public void onFlip() {
                if (CanvasEditorView.this.mListener != null) {
                    CanvasEditorView.this.mListener.onStickerFlip();
                }
            }

            @Override
            public void onClickStickerOutside(float x, float y) {
                int pos = CanvasEditorView.this.findTapedSticker(x, y);
                if (pos > -1) {
                    CanvasEditorView.this.enableEditModeSticker(pos);
                }
            }

            @Override
            public void onTouchEvent(@NonNull MotionEvent event) {
                Intrinsics.checkNotNullParameter(event, "event");
                if (CanvasEditorView.this.mListener != null) {
                    CanvasEditorView.this.mListener.onTouchEvent(event);
                }
            }
        };
        Context var10003 = this.getContext();
        Intrinsics.checkNotNullExpressionValue(var10003, "getContext(...)");
        this.mPaintView = new PaintView(var10003, this.paintViewListener);
        var10003 = this.getContext();
        Intrinsics.checkNotNullExpressionValue(var10003, "getContext(...)");
        this.mStickerView = new StickerView(var10003, this.stickerViewListener);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(-1, -1);
        this.mPaintView.setLayoutParams((ViewGroup.LayoutParams)params);
        this.mPaintView.setBackgroundColor(ContextCompat.getColor(this.getContext(), 17170443));
        this.addView((View)this.mPaintView);
        this.mStickerView.setLayoutParams((ViewGroup.LayoutParams)params);
        this.mStickerView.setBackgroundColor(ContextCompat.getColor(this.getContext(), 17170445));
        this.addView((View)this.mStickerView);
        this.mStickerView.setVisibility(8);
    }

    // $FF: synthetic method
    public CanvasEditorView(Context var1, AttributeSet var2, int var3, int var4, DefaultConstructorMarker var5) {
        this(var1, (var4 & 2) != 0 ? null : var2, (var4 & 4) != 0 ? 0 : var3);
    }

    @TargetApi(21)
    public CanvasEditorView(@NotNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        //Intrinsics.checkNotNullParameter(context, "context");
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mUndoList = new ArrayList<>();
        this.mRedoList = new ArrayList<>();
        this.paintViewListener = new PaintViewListener() {
            public void onTouchUp(@NonNull DrawObject obj) {
                Intrinsics.checkNotNullParameter(obj, "obj");
                CanvasEditorView.this.mUndoList.add(obj);
                CanvasEditorView.this.mRedoList.clear();
                CanvasEditorListener var10000 = CanvasEditorView.this.mListener;
                if (var10000 != null) {
                    var10000.onEnableUndo(true);
                }

                var10000 = CanvasEditorView.this.mListener;
                if (var10000 != null) {
                    var10000.onEnableRedo(false);
                }

            }

            public void onClick(float x, float y) {
                int pos = CanvasEditorView.this.findTapedSticker(x, y);
                if (pos > -1) {
                    CanvasEditorView.this.enableEditModeSticker(pos);
                }

            }

            public void onTouchEvent(@NonNull MotionEvent event) {
                Intrinsics.checkNotNullParameter(event, "event");
                CanvasEditorListener var10000 = CanvasEditorView.this.mListener;
                if (var10000 != null) {
                    var10000.onTouchEvent(event);
                }

            }
        };
        this.stickerViewListener = new StickerViewListener() {
            public void onRemove() {
                CanvasEditorListener var10000 = CanvasEditorView.this.mListener;
                if (var10000 != null) {
                    var10000.onStickerRemove();
                }

                var10000 = CanvasEditorView.this.mListener;
                if (var10000 != null) {
                    var10000.onEnableUndo(!((Collection)CanvasEditorView.this.mUndoList).isEmpty());
                }

            }

            public void onDone(@NonNull DrawObject obj) {
                Intrinsics.checkNotNullParameter(obj, "obj");
                CanvasEditorView.this.addStickerToPaint(obj);
                CanvasEditorListener var10000 = CanvasEditorView.this.mListener;
                if (var10000 != null) {
                    var10000.onStickerDone();
                }

            }

            public void onZoomAndRotate() {
                CanvasEditorListener var10000 = CanvasEditorView.this.mListener;
                if (var10000 != null) {
                    var10000.onStickerZoomAndRotate();
                }

            }

            public void onFlip() {
                CanvasEditorListener var10000 = CanvasEditorView.this.mListener;
                if (var10000 != null) {
                    var10000.onStickerFlip();
                }

            }

            public void onClickStickerOutside(float x, float y) {
                int pos = CanvasEditorView.this.findTapedSticker(x, y);
                if (pos > -1) {
                    CanvasEditorView.this.enableEditModeSticker(pos);
                }

            }

            public void onTouchEvent(@NonNull MotionEvent event) {
                Intrinsics.checkNotNullParameter(event, "event");
                CanvasEditorListener var10000 = CanvasEditorView.this.mListener;
                if (var10000 != null) {
                    var10000.onTouchEvent(event);
                }

            }
        };
        Context var10003 = this.getContext();
        Intrinsics.checkNotNullExpressionValue(var10003, "getContext(...)");
        this.mPaintView = new PaintView(var10003, this.paintViewListener);
        var10003 = this.getContext();
        Intrinsics.checkNotNullExpressionValue(var10003, "getContext(...)");
        this.mStickerView = new StickerView(var10003, this.stickerViewListener);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(-1, -1);
        this.mPaintView.setLayoutParams((ViewGroup.LayoutParams)params);
        this.mPaintView.setBackgroundColor(ContextCompat.getColor(this.getContext(), 17170443));
        this.addView((View)this.mPaintView);
        this.mStickerView.setLayoutParams((ViewGroup.LayoutParams)params);
        this.mStickerView.setBackgroundColor(ContextCompat.getColor(this.getContext(), 17170445));
        this.addView((View)this.mStickerView);
        this.mStickerView.setVisibility(8);
    }

    public final void setListener(@NotNull CanvasEditorListener listener) {
        Intrinsics.checkNotNullParameter(listener, "listener");
        this.mListener = listener;
    }

    public final void setPaintColor(int color) {
        this.doneStickerEdit();
        this.mPaintView.getPaint().setColor(color);
    }

    public final void setStrokeWidth(float strokeWidth) {
        this.doneStickerEdit();
        this.mPaintView.getPaint().setStrokeWidth(strokeWidth);
    }

    public final void setStrokeCap(@NotNull Paint.Cap strokeCap) {
        Intrinsics.checkNotNullParameter(strokeCap, "strokeCap");
        this.doneStickerEdit();
        this.mPaintView.getPaint().setStrokeCap(strokeCap);
    }

    public final void addDrawableSticker(@NotNull Drawable drawable) {
        Intrinsics.checkNotNullParameter(drawable, "drawable");
        this.doneStickerEdit();
        this.mStickerView.setVisibility(0);
        DrawableSticker sticker = new DrawableSticker(drawable);
        this.mStickerView.addSticker((Sticker)sticker);
        CanvasEditorListener var10000 = this.mListener;
        if (var10000 != null) {
            var10000.onEnableUndo(true);
        }

        var10000 = this.mListener;
        if (var10000 != null) {
            var10000.onEnableRedo(false);
        }

        var10000 = this.mListener;
        if (var10000 != null) {
            var10000.onStickerActive();
        }

    }

    public void addBitmapSticker(@NotNull Bitmap bitmap) {
        Intrinsics.checkNotNullParameter(bitmap, "bitmap");
        this.doneStickerEdit();
        this.mStickerView.setVisibility(View.VISIBLE);
        Intrinsics.checkNotNullExpressionValue(this.getContext(), "getContext(...)");
        BitmapSticker sticker = new BitmapSticker(this.getContext(), bitmap);
        this.mStickerView.addSticker(sticker);
        if (this.mListener != null) {
            this.mListener.onEnableUndo(true);
        }
        if (this.mListener != null) {
            this.mListener.onEnableRedo(false);
        }
        if (this.mListener != null) {
            this.mListener.onStickerActive();
        }
    }

    public void addTextSticker(@NotNull String text, int textColor, @Nullable Typeface typeface) {
        Intrinsics.checkNotNullParameter(text, "text");
        this.doneStickerEdit();
        this.mStickerView.setVisibility(View.VISIBLE);
        Context var10002 = this.getContext();
        Intrinsics.checkNotNullExpressionValue(var10002, "getContext(...)");
        TextSticker sticker = new TextSticker(var10002, (Drawable)null);
        sticker.setText(text);
        sticker.setTextColor(textColor);
        if (typeface != null) {
            sticker.setTypeface(typeface);
        }
        sticker.setAlpha(255);
        sticker.resizeText();
        this.mStickerView.addSticker(sticker);
        if (this.mListener != null) {
            this.mListener.onEnableUndo(true);
        }
        if (this.mListener != null) {
            this.mListener.onEnableRedo(false);
        }
        if (this.mListener != null) {
            this.mListener.onStickerActive();
        }
    }

    public void addDrawableTextSticker(@NotNull Drawable drawable, @NotNull String text, int textColor, @Nullable Typeface typeface) {
        Intrinsics.checkNotNullParameter(drawable, "drawable");
        Intrinsics.checkNotNullParameter(text, "text");
        this.doneStickerEdit();
        this.mStickerView.setVisibility(View.VISIBLE);
        Intrinsics.checkNotNullExpressionValue(this.getContext(), "getContext(...)");
        TextSticker sticker = new TextSticker(this.getContext(), drawable);
        sticker.setText(text);
        sticker.setTextColor(textColor);
        if (typeface != null) {
            sticker.setTypeface(typeface);
        }
        sticker.resizeText();
        this.mStickerView.addSticker(sticker);
        if (this.mListener != null) {
            this.mListener.onEnableUndo(true);
        }
        if (this.mListener != null) {
            this.mListener.onEnableRedo(false);
        }
        if (this.mListener != null) {
            this.mListener.onStickerActive();
        }
    }
    public void doneActiveSticker() {
        if (this.mStickerView.getVisibility() == 0) {
            this.mStickerView.done();
        }
    }

    public final void removeActiveSticker() {
        if (this.mStickerView.getVisibility() == 0) {
            this.mStickerView.remove();
        }

    }

    public final void zoomAndRotateActiveSticker(@NotNull MotionEvent motionEvent) {
        Intrinsics.checkNotNullParameter(motionEvent, "motionEvent");
        if (this.mStickerView.getVisibility() == 0) {
            this.mStickerView.zoomAndRotate(motionEvent);
        }

    }

    public final void flipActiveSticker() {
        if (this.mStickerView.getVisibility() == 0) {
            this.mStickerView.flip();
        }

    }

    public final void undo() {
        if (this.mStickerView.getVisibility() == 0) {
            this.mStickerView.remove();
        } else {
            if (!((Collection)this.mUndoList).isEmpty()) {
                this.mRedoList.add(CollectionsKt.last(this.mUndoList));
                this.mUndoList.remove(CollectionsKt.getLastIndex(this.mUndoList));
                this.mPaintView.initCanvas();
                Iterable $this$forEach$iv = (Iterable)this.mUndoList;
                int $i$f$forEach = 0;

                for(Object element$iv : $this$forEach$iv) {
                    DrawObject it = (DrawObject)element$iv;
                    int var6 = 0;
                    this.drawObject(it);
                }

                CanvasEditorListener var10000 = this.mListener;
                if (var10000 != null) {
                    var10000.onEnableUndo(!((Collection)this.mUndoList).isEmpty());
                }

                var10000 = this.mListener;
                if (var10000 != null) {
                    var10000.onEnableRedo(!((Collection)this.mRedoList).isEmpty());
                }
            }

        }
    }

    public final void redo() {
        if (!((Collection)this.mRedoList).isEmpty()) {
            DrawObject obj = (DrawObject)CollectionsKt.last(this.mRedoList);
            this.mUndoList.add(obj);
            this.mRedoList.remove(CollectionsKt.getLastIndex(this.mRedoList));
            this.drawObject(obj);
            CanvasEditorListener var10000 = this.mListener;
            if (var10000 != null) {
                var10000.onEnableUndo(!((Collection)this.mUndoList).isEmpty());
            }

            var10000 = this.mListener;
            if (var10000 != null) {
                var10000.onEnableRedo(!((Collection)this.mRedoList).isEmpty());
            }
        }

    }

    public final void removeAll() {
        this.mUndoList.clear();
        this.mRedoList.clear();
        this.mStickerView.remove();
        this.mPaintView.initCanvas();
        CanvasEditorListener var10000 = this.mListener;
        if (var10000 != null) {
            var10000.onEnableUndo(false);
        }

        var10000 = this.mListener;
        if (var10000 != null) {
            var10000.onEnableRedo(false);
        }

    }

    @NotNull
    public Bitmap downloadBitmap() {
        this.doneStickerEdit();
        return this.mPaintView.getExtraBitmap();
    }

    private void drawObject(DrawObject obj) {
        switch (CanvasEditorView.WhenMappings.$EnumSwitchMapping$0[obj.getDrawType().ordinal()]) {
            case 1:
                PaintView var2 = this.mPaintView;
                PathAndPaint var3 = obj.getPathAndPaint();
                Intrinsics.checkNotNull(var3);
                var2.drawPath(var3);
                break;

            case 2:
                PaintView var10000 = this.mPaintView;
                Sticker var10001 = obj.getSticker();
                Intrinsics.checkNotNull(var10001);
                var10000.drawSticker(var10001);
                break;

            default:
                throw new NoWhenBranchMatchedException();
        }

    }

    private int findTapedSticker(float x, float y) {
        for (int i = this.mUndoList.size() - 1; i >= 0; --i) {
            DrawObject obj = this.mUndoList.get(i);
            if (obj.getDrawType() == DrawType.STICKER) {
                Intrinsics.checkNotNull(obj.getSticker());
                Sticker sticker = obj.getSticker();
                if (Objects.requireNonNull(sticker).contains(x, y)) {
                    return i;
                }
            }
        }
        return -1;
    }

    private void enableEditModeSticker(int pos) {
        DrawObject obj = this.mUndoList.get(pos);
        Intrinsics.checkNotNull(obj.getSticker());
        this.mStickerView.setVisibility(View.VISIBLE);
        this.mStickerView.setCurrentSticker(obj.getSticker());
        this.mUndoList.remove(pos);
        this.mPaintView.initCanvas();

        for (DrawObject element : this.mUndoList) {
            this.drawObject(element);
        }

        this.mRedoList.clear();
        if (this.mListener != null) {
            this.mListener.onEnableUndo(true);
        }

        if (this.mListener != null) {
            this.mListener.onEnableRedo(!this.mRedoList.isEmpty());
        }
        if (this.mListener != null) {
            this.mListener.onStickerActive();
        }
    }

    private void addStickerToPaint(DrawObject obj) {
        Intrinsics.checkNotNull(obj.getSticker());
        this.mPaintView.drawSticker(Objects.requireNonNull(obj.getSticker()));
        this.mUndoList.add(obj);
        this.mRedoList.clear();
        if (this.mListener != null) {
            this.mListener.onEnableUndo(true);
        }
        if (this.mListener != null) {
            this.mListener.onEnableRedo(false);
        }
    }

    private final void doneStickerEdit() {
        if (this.mStickerView.getVisibility() == 0) {
            this.mStickerView.done();
        }
    }

    @JvmOverloads
    public CanvasEditorView(@NotNull Context context, @Nullable AttributeSet attrs) {
        //Intrinsics.checkNotNullParameter(context, "context");
        this(context, attrs, 0, 4, (DefaultConstructorMarker)null);
    }

    @JvmOverloads
    public CanvasEditorView(@NotNull Context context) {
        //Intrinsics.checkNotNullParameter(context, "context");
        this(context, (AttributeSet)null, 0, 6, (DefaultConstructorMarker)null);
    }

    // $FF: synthetic class
    @Metadata(
            mv = {2, 0, 0},
            k = 3,
            xi = 48
    )
    public static final class WhenMappings {
        // $FF: synthetic field
        public static final int[] $EnumSwitchMapping$0;

        static {
            int[] var0 = new int[DrawType.values().length];

            try {
                var0[DrawType.PATH.ordinal()] = 1;
            } catch (NoSuchFieldError var3) {
            }

            try {
                var0[DrawType.STICKER.ordinal()] = 2;
            } catch (NoSuchFieldError var2) {
            }

            $EnumSwitchMapping$0 = var0;
        }
    }
}
