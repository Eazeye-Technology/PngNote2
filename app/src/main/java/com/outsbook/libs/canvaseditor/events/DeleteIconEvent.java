package com.outsbook.libs.canvaseditor.events;

import android.view.MotionEvent;
import com.outsbook.libs.canvaseditor.listeners.StickerIconListener;
import com.outsbook.libs.canvaseditor.stickers.StickerView;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
        mv = {2, 0, 0},
        k = 1,
        xi = 48,
        d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0000\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u001c\u0010\u0004\u001a\u00020\u00052\b\u0010\u0006\u001a\u0004\u0018\u00010\u00072\b\u0010\b\u001a\u0004\u0018\u00010\tH\u0016J\u0018\u0010\n\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tH\u0016J\u001a\u0010\u000b\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\b\u0010\b\u001a\u0004\u0018\u00010\tH\u0016¨\u0006\f"},
        d2 = {"Lcom/outsbook/libs/canvaseditor/events/DeleteIconEvent;", "Lcom/outsbook/libs/canvaseditor/listeners/StickerIconListener;", "<init>", "()V", "onActionDown", "", "stickerView", "Lcom/outsbook/libs/canvaseditor/stickers/StickerView;", "event", "Landroid/view/MotionEvent;", "onActionMove", "onActionUp", "Sources of canvaseditor.app.main"}
)
public final class DeleteIconEvent implements StickerIconListener {
    public void onActionDown(@Nullable StickerView stickerView, @Nullable MotionEvent event) {
    }

    public void onActionMove(@NotNull StickerView stickerView, @NotNull MotionEvent event) {
        Intrinsics.checkNotNullParameter(stickerView, "stickerView");
        Intrinsics.checkNotNullParameter(event, "event");
    }

    public void onActionUp(@NotNull StickerView stickerView, @Nullable MotionEvent event) {
        Intrinsics.checkNotNullParameter(stickerView, "stickerView");
        stickerView.remove();
    }
}
