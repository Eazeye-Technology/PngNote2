package com.outsbook.libs.canvaseditor.listeners;

import android.view.MotionEvent;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(
        mv = {2, 0, 0},
        k = 1,
        xi = 48,
        d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0006\bf\u0018\u00002\u00020\u0001J\u0010\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H&J\u0010\u0010\u0006\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H&J\u0010\u0010\u0007\u001a\u00020\u00032\u0006\u0010\b\u001a\u00020\tH\u0016J\b\u0010\n\u001a\u00020\u0003H\u0016J\b\u0010\u000b\u001a\u00020\u0003H\u0016J\b\u0010\f\u001a\u00020\u0003H\u0016J\b\u0010\r\u001a\u00020\u0003H\u0016J\b\u0010\u000e\u001a\u00020\u0003H\u0016¨\u0006\u000f"},
        d2 = {"Lcom/outsbook/libs/canvaseditor/listeners/CanvasEditorListener;", "", "onEnableUndo", "", "isEnable", "", "onEnableRedo", "onTouchEvent", "event", "Landroid/view/MotionEvent;", "onStickerActive", "onStickerRemove", "onStickerDone", "onStickerZoomAndRotate", "onStickerFlip", "Sources of canvaseditor.app.main"}
)
public interface CanvasEditorListener {
    void onEnableUndo(boolean var1);

    void onEnableRedo(boolean var1);

    void onTouchEvent(@NotNull MotionEvent var1);

    void onStickerActive();

    void onStickerRemove();

    void onStickerDone();

    void onStickerZoomAndRotate();

    void onStickerFlip();

    @Metadata(
            mv = {2, 0, 0},
            k = 3,
            xi = 48
    )
    public static final class DefaultImpls {
        public static void onTouchEvent(@NotNull CanvasEditorListener $this, @NotNull MotionEvent event) {
            Intrinsics.checkNotNullParameter(event, "event");
        }

        public static void onStickerActive(@NotNull CanvasEditorListener $this) {
        }

        public static void onStickerRemove(@NotNull CanvasEditorListener $this) {
        }

        public static void onStickerDone(@NotNull CanvasEditorListener $this) {
        }

        public static void onStickerZoomAndRotate(@NotNull CanvasEditorListener $this) {
        }

        public static void onStickerFlip(@NotNull CanvasEditorListener $this) {
        }
    }
}
