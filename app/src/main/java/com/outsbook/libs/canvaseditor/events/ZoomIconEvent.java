package com.outsbook.libs.canvaseditor.events;

import android.view.MotionEvent;
import com.outsbook.libs.canvaseditor.listeners.StickerIconListener;
import com.outsbook.libs.canvaseditor.stickers.StickerView;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ZoomIconEvent implements StickerIconListener {
    public void onActionDown(@Nullable StickerView stickerView, @Nullable MotionEvent event) {
    }

    public void onActionMove(@NotNull StickerView stickerView, @NotNull MotionEvent event) {
        Intrinsics.checkNotNullParameter(stickerView, "stickerView");
        Intrinsics.checkNotNullParameter(event, "event");
        stickerView.zoomAndRotate(event);
    }

    public void onActionUp(@NotNull StickerView stickerView, @Nullable MotionEvent event) {
        Intrinsics.checkNotNullParameter(stickerView, "stickerView");
    }
}
