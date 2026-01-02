package com.outsbook.libs.canvaseditor.listeners;

import android.view.MotionEvent;
import com.outsbook.libs.canvaseditor.models.DrawObject;
import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;

@Metadata(
        mv = {2, 0, 0},
        k = 1,
        xi = 48,
        d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0007\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\b`\u0018\u00002\u00020\u0001J\b\u0010\u0002\u001a\u00020\u0003H&J\u0010\u0010\u0004\u001a\u00020\u00032\u0006\u0010\u0005\u001a\u00020\u0006H&J\b\u0010\u0007\u001a\u00020\u0003H&J\b\u0010\b\u001a\u00020\u0003H&J\u0018\u0010\t\u001a\u00020\u00032\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\u000bH&J\u0010\u0010\r\u001a\u00020\u00032\u0006\u0010\u000e\u001a\u00020\u000fH&¨\u0006\u0010"},
        d2 = {"Lcom/outsbook/libs/canvaseditor/listeners/StickerViewListener;", "", "onRemove", "", "onDone", "obj", "Lcom/outsbook/libs/canvaseditor/models/DrawObject;", "onZoomAndRotate", "onFlip", "onClickStickerOutside", "x", "", "y", "onTouchEvent", "event", "Landroid/view/MotionEvent;", "Sources of canvaseditor.app.main"}
)
public interface StickerViewListener {
    void onRemove();

    void onDone(@NotNull DrawObject var1);

    void onZoomAndRotate();

    void onFlip();

    void onClickStickerOutside(float var1, float var2);

    void onTouchEvent(@NotNull MotionEvent var1);
}
