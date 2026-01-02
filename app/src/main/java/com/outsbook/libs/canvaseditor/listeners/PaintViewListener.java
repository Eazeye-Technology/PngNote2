package com.outsbook.libs.canvaseditor.listeners;

import android.view.MotionEvent;
import com.outsbook.libs.canvaseditor.models.DrawObject;
import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;

public interface PaintViewListener {
    void onTouchUp(@NotNull DrawObject var1);

    void onClick(float var1, float var2);

    void onTouchEvent(@NotNull MotionEvent var1);
}
