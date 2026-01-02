package com.outsbook.libs.canvaseditor.listeners;

import android.view.MotionEvent;
import com.outsbook.libs.canvaseditor.stickers.StickerView;
import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
        mv = {2, 0, 0},
        k = 1,
        xi = 48,
        d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\b`\u0018\u00002\u00020\u0001J\u001c\u0010\u0002\u001a\u00020\u00032\b\u0010\u0004\u001a\u0004\u0018\u00010\u00052\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007H&J\u0018\u0010\b\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H&J\u001a\u0010\t\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007H&¨\u0006\n"},
        d2 = {"Lcom/outsbook/libs/canvaseditor/listeners/StickerIconListener;", "", "onActionDown", "", "stickerView", "Lcom/outsbook/libs/canvaseditor/stickers/StickerView;", "event", "Landroid/view/MotionEvent;", "onActionMove", "onActionUp", "Sources of canvaseditor.app.main"}
)
public interface StickerIconListener {
    void onActionDown(@Nullable StickerView var1, @Nullable MotionEvent var2);

    void onActionMove(@NotNull StickerView var1, @NotNull MotionEvent var2);

    void onActionUp(@NotNull StickerView var1, @Nullable MotionEvent var2);
}
