package com.outsbook.libs.canvaseditor.models;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

public class DrawableSticker extends Sticker {
    private Drawable drawable;
    private Rect realBounds;

    public DrawableSticker(Drawable drawable) {
        this.drawable = drawable;
        this.realBounds = new Rect(0, 0, this.getWidth(), this.getHeight());
    }

    public Drawable getDrawable() {
        return this.drawable;
    }

    public int getWidth() {
        return this.getDrawable().getIntrinsicWidth();
    }

    public int getHeight() {
        return this.getDrawable().getIntrinsicHeight();
    }

    @Override
    public DrawableSticker setDrawable(Drawable drawable) {
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
    public DrawableSticker setAlpha(int alpha) {
        this.getDrawable().setAlpha(alpha);
        return this;
    }
}
