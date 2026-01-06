package com.outsbook.libs.canvaseditor.models;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class BitmapSticker extends Sticker {
    private Rect realBounds;
    private Drawable drawable;

    public BitmapSticker(Context context, Bitmap bitmap) {
        this.drawable = new BitmapDrawable(context.getResources(), bitmap);
        this.realBounds = new Rect(0, 0, this.getWidth(), this.getHeight());
    }

    @Override
    public Drawable getDrawable() {
        return this.drawable;
    }

    @Override
    public int getWidth() {
        return this.getDrawable().getIntrinsicWidth();
    }

    @Override
    public int getHeight() {
        return this.getDrawable().getIntrinsicHeight();
    }

    @Override
    public BitmapSticker setDrawable(Drawable drawable) {
        this.drawable = drawable;
        return this;
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.save();
        canvas.concat(this.getMatrix());
        this.getDrawable().setBounds(this.realBounds);
        this.getDrawable().draw(canvas);
        canvas.restore();
    }

    @Override
    public BitmapSticker setAlpha(int alpha) {
        this.getDrawable().setAlpha(alpha);
        return this;
    }
}
