package com.outsbook.libs.canvaseditor.stickers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

public final class BitmapSticker extends Sticker {
    @NotNull
    private final Rect realBounds;
    @NotNull
    private Drawable drawable;

    public BitmapSticker(@NotNull Context context, @NotNull Bitmap bitmap) {
        //Intrinsics.checkNotNullParameter(context, "context");
        //Intrinsics.checkNotNullParameter(bitmap, "bitmap");
        super();
        this.drawable = (Drawable)(new BitmapDrawable(context.getResources(), bitmap));
        this.realBounds = new Rect(0, 0, this.getWidth(), this.getHeight());
    }

    @NotNull
    public Drawable getDrawable() {
        return this.drawable;
    }

    public void setDrawable_(@NotNull Drawable var1) {
        //Intrinsics.checkNotNullParameter(var1, "<set-?>");
        this.drawable = var1;
    }

    public int getWidth() {
        return this.getDrawable().getIntrinsicWidth();
    }

    public int getHeight() {
        return this.getDrawable().getIntrinsicHeight();
    }

    @NotNull
    public BitmapSticker setDrawable(@NotNull Drawable drawable) {
        //Intrinsics.checkNotNullParameter(drawable, "drawable");
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
    public BitmapSticker setAlpha(int alpha) {
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
