package com.outsbook.libs.canvaseditor.stickers;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

public class DrawableSticker extends Sticker {
    @NotNull
    private Drawable drawable;
    @NotNull
    private final Rect realBounds;

    public DrawableSticker(@NotNull Drawable drawable) {
        //Intrinsics.checkNotNullParameter(drawable, "drawable");
        super();
        this.drawable = drawable;
        this.realBounds = new Rect(0, 0, this.getWidth(), this.getHeight());
    }

    @NotNull
    public Drawable getDrawable() {
        return this.drawable;
    }

    public void setDrawable_(@NotNull Drawable var1) {
        Intrinsics.checkNotNullParameter(var1, "<set-?>");
        this.drawable = var1;
    }

    public final int getWidth() {
        return this.getDrawable().getIntrinsicWidth();
    }

    public final int getHeight() {
        return this.getDrawable().getIntrinsicHeight();
    }

    @NotNull
    public DrawableSticker setDrawable(@NotNull Drawable drawable) {
        Intrinsics.checkNotNullParameter(drawable, "drawable");
        this.setDrawable_(drawable);
        return this;
    }

    public void draw(@NotNull Canvas canvas) {
        Intrinsics.checkNotNullParameter(canvas, "canvas");
        canvas.save();
        canvas.concat(this.getMatrix());
        this.getDrawable().setBounds(this.realBounds);
        this.getDrawable().draw(canvas);
        canvas.restore();
    }

    @NotNull
    public DrawableSticker setAlpha(int alpha) {
        this.getDrawable().setAlpha(alpha);
        return this;
    }

//    // $FF: synthetic method
//    // $FF: bridge method
//    public Sticker setDrawable(Drawable drawable) {
//        return this.setDrawable(drawable);
//    }
//
//    // $FF: synthetic method
//    // $FF: bridge method
//    public Sticker setAlpha(int alpha) {
//        return this.setAlpha(alpha);
//    }
}
