package com.outsbook.libs.canvaseditor.models;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.Layout.Alignment;
import android.text.StaticLayout.Builder;
import androidx.core.content.ContextCompat;

import com.txkj.drawingapp.R;

public class TextSticker extends Sticker {
    private Context context;
    public Drawable drawable;
    private String text;
    private String mEllipsis;
    private Rect realBounds;
    private Rect textRect;
    private TextPaint textPaint;
    private StaticLayout staticLayout;
    private Layout.Alignment alignment;
    private float maxTextSizePixels;
    private float minTextSizePixels;
    private float lineSpacingMultiplier;
    private float lineSpacingExtra;

    public TextSticker(Context context, Drawable drawable) {
        this.context = context;
        this.mEllipsis = "…";
        this.lineSpacingMultiplier = 1.0F;
        if (drawable == null) {
            this.setDrawable_(ContextCompat.getDrawable(this.context, R.drawable.shape_transfarent_background));
        } else {
            this.setDrawable_(drawable);
        }

        this.textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        this.realBounds = new Rect(0, 0, this.getWidth(), this.getHeight());
        this.textRect = new Rect(0, 0, this.getWidth(), this.getHeight());
        this.minTextSizePixels = this.convertSpToPx(6.0F);
        this.maxTextSizePixels = this.convertSpToPx(32.0F);
        this.alignment = Alignment.ALIGN_CENTER;
        this.textPaint.setTextSize(this.maxTextSizePixels);
    }

    public Drawable getDrawable() {
        return this.drawable;
    }

    public void setDrawable_(Drawable drawable) {
        this.drawable = drawable;
    }

    public int getWidth() {
        return this.getDrawable().getIntrinsicWidth();
    }

    public int getHeight() {
        return this.getDrawable().getIntrinsicHeight();
    }

    public void draw(Canvas canvas) {
        Matrix matrix = this.getMatrix();
        canvas.save();
        canvas.concat(matrix);
        this.getDrawable().setBounds(this.realBounds);
        this.getDrawable().draw(canvas);
        canvas.restore();
        canvas.save();
        canvas.concat(matrix);
        if (this.textRect.width() == this.getWidth()) {
            int dy = this.getHeight() / 2 - this.staticLayout.getHeight() / 2;
            canvas.translate(0.0F, (float)dy);
        } else {
            int dx = this.textRect.left;
            int dy = this.textRect.top + this.textRect.height() / 2 - this.staticLayout.getHeight() / 2;
            canvas.translate((float)dx, (float)dy);
        }
        this.staticLayout.draw(canvas);
        canvas.restore();
    }

    @Override
    public TextSticker setAlpha(int alpha) {
        this.textPaint.setAlpha(alpha);
        return this;
    }

    @Override
    public TextSticker setDrawable(Drawable drawable) {
        this.setDrawable_(drawable);
        this.realBounds.set(0, 0, this.getWidth(), this.getHeight());
        this.textRect.set(0, 0, this.getWidth(), this.getHeight());
        return this;
    }

    public TextSticker setDrawable(Drawable drawable, Rect region) {
        this.setDrawable_(drawable);
        this.realBounds.set(0, 0, this.getWidth(), this.getHeight());
        if (region == null) {
            this.textRect.set(0, 0, this.getWidth(), this.getHeight());
        } else {
            this.textRect.set(region.left, region.top, region.right, region.bottom);
        }
        return this;
    }

    public TextSticker setText(String text) {
        this.text = text;
        return this;
    }

    public TextSticker setTypeface(Typeface typeface) {
        this.textPaint.setTypeface(typeface);
        return this;
    }

    public TextSticker setTextColor(int color) {
        this.textPaint.setColor(color);
        return this;
    }

    public TextSticker setTextAlign(Layout.Alignment alignment) {
        this.alignment = alignment;
        return this;
    }

    public TextSticker setMaxTextSize(float size) {
        this.textPaint.setTextSize(this.convertSpToPx(size));
        this.maxTextSizePixels = this.textPaint.getTextSize();
        return this;
    }

    public TextSticker setMinTextSize(float minTextSizeScaledPixels) {
        this.minTextSizePixels = this.convertSpToPx(minTextSizeScaledPixels);
        return this;
    }

    public TextSticker setLineSpacing(float add, float multiplier) {
        this.lineSpacingMultiplier = multiplier;
        this.lineSpacingExtra = add;
        return this;
    }

    public TextSticker resizeText() {
        int availableHeightPixels = this.textRect.height();
        int availableWidthPixels = this.textRect.width();
        if (this.text != null && this.text.length() != 0 && availableHeightPixels > 0 && availableWidthPixels > 0 && !(this.maxTextSizePixels <= 0.0F)) {
            float targetTextSizePixels = this.maxTextSizePixels;

            int targetTextHeightPixels;
            for (targetTextHeightPixels = this.getTextHeightPixels(this.text, availableWidthPixels, targetTextSizePixels);
                 targetTextHeightPixels > availableHeightPixels && targetTextSizePixels > this.minTextSizePixels;
                 targetTextHeightPixels = this.getTextHeightPixels(this.text, availableWidthPixels, targetTextSizePixels)) {
                targetTextSizePixels = Math.max(targetTextSizePixels - (float)2, this.minTextSizePixels);
            }
            if (targetTextSizePixels == this.minTextSizePixels &&
                    targetTextHeightPixels > availableHeightPixels) {
                TextPaint textPaintCopy = new TextPaint(this.textPaint);
                textPaintCopy.setTextSize(targetTextSizePixels);
                StaticLayout staticLayout =
                        VERSION.SDK_INT >= 23 ?
                        Builder.obtain(text, 0, text.length(), textPaintCopy, availableWidthPixels)
                                .setAlignment(Alignment.ALIGN_NORMAL)
                                .setLineSpacing(this.lineSpacingExtra, this.lineSpacingMultiplier)
                                .setIncludePad(false)
                                .build() :
                        new StaticLayout(text,
                                textPaintCopy,
                                availableWidthPixels,
                                Alignment.ALIGN_NORMAL,
                                this.lineSpacingMultiplier,
                                this.lineSpacingExtra,
                                false);
                if (staticLayout.getLineCount() > 0) {
                    int lastLine = staticLayout.getLineForVertical(availableHeightPixels) - 1;
                    if (lastLine >= 0) {
                        int startOffset = staticLayout.getLineStart(lastLine);
                        int endOffset = staticLayout.getLineEnd(lastLine);
                        float lineWidthPixels = staticLayout.getLineWidth(lastLine);
                        for (float ellipseWidth = textPaintCopy.measureText(this.mEllipsis);
                             (float)availableWidthPixels < lineWidthPixels + ellipseWidth;
                             lineWidthPixels = textPaintCopy.measureText(text.subSequence(startOffset, endOffset + 1).toString())) {
                            --endOffset;
                        }
                        this.setText(text.subSequence(0, endOffset) + this.mEllipsis);
                    }
                }
            }
            this.textPaint.setTextSize(targetTextSizePixels);
            if (VERSION.SDK_INT >= 23) {
                this.staticLayout = Builder.obtain(
                    this.text, 0, this.text.length(),
                    this.textPaint, this.textRect.width())
                    .setAlignment(this.alignment)
                    .setLineSpacing(this.lineSpacingExtra, this.lineSpacingMultiplier)
                    .setIncludePad(true)
                    .build();
            } else {
                this.staticLayout = new StaticLayout(
                        this.text,
                        this.textPaint,
                        availableWidthPixels,
                        this.alignment,
                        this.lineSpacingMultiplier,
                        this.lineSpacingExtra,
                        true);
            }
            return this;
        } else {
            return this;
        }
    }

    private int getTextHeightPixels(CharSequence source, int availableWidthPixels, float textSizePixels) {
        this.textPaint.setTextSize(textSizePixels);
        StaticLayout staticLayout =
                VERSION.SDK_INT >= 23 ?
                Builder.obtain(source, 0, 0, this.textPaint, availableWidthPixels)
                        .setAlignment(Alignment.ALIGN_NORMAL)
                        .setLineSpacing(this.lineSpacingExtra, this.lineSpacingMultiplier)
                        .setIncludePad(true)
                        .build() :
                new StaticLayout(source,
                        this.textPaint,
                        availableWidthPixels,
                        Alignment.ALIGN_NORMAL,
                        this.lineSpacingMultiplier,
                        this.lineSpacingExtra,
                        true);
        return staticLayout.getHeight();
    }

    private float convertSpToPx(float scaledPixels) {
        return scaledPixels * this.context.getResources().getDisplayMetrics().scaledDensity;
    }
}
