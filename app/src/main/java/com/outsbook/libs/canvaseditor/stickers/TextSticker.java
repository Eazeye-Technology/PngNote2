package com.outsbook.libs.canvaseditor.stickers;

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

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class TextSticker extends Sticker {
    @NotNull
    private final Context context;
    public Drawable drawable;
    @Nullable
    private String text;
    @NotNull
    private final String mEllipsis;
    @NotNull
    private final Rect realBounds;
    @NotNull
    private final Rect textRect;
    @NotNull
    private final TextPaint textPaint;
    @Nullable
    private StaticLayout staticLayout;
    @NotNull
    private Layout.Alignment alignment;
    private float maxTextSizePixels;
    private float minTextSizePixels;
    private float lineSpacingMultiplier;
    private float lineSpacingExtra;

    public TextSticker(@NotNull Context context, @Nullable Drawable drawable) {
        //Intrinsics.checkNotNullParameter(context, "context");
        super();
        this.context = context;
        this.mEllipsis = "…";
        this.lineSpacingMultiplier = 1.0F;
        if (drawable == null) {
            Drawable var10001 = ContextCompat.getDrawable(this.context, R.drawable.shape_transfarent_background);
            Intrinsics.checkNotNull(var10001);
            this.setDrawable_(var10001);
        } else {
            this.setDrawable_(drawable);
        }

        this.textPaint = new TextPaint(1);
        this.realBounds = new Rect(0, 0, this.getWidth(), this.getHeight());
        this.textRect = new Rect(0, 0, this.getWidth(), this.getHeight());
        this.minTextSizePixels = this.convertSpToPx(6.0F);
        this.maxTextSizePixels = this.convertSpToPx(32.0F);
        this.alignment = Alignment.ALIGN_CENTER;
        this.textPaint.setTextSize(this.maxTextSizePixels);
    }

    @NotNull
    public Drawable getDrawable() {
        Drawable var10000 = this.drawable;
        if (var10000 != null) {
            return var10000;
        } else {
            Intrinsics.throwUninitializedPropertyAccessException("drawable");
            return null;
        }
    }

    public void setDrawable_(@NotNull Drawable var1) {
        Intrinsics.checkNotNullParameter(var1, "<set-?>");
        this.drawable = var1;
    }

    public int getWidth() {
        return this.getDrawable().getIntrinsicWidth();
    }

    public int getHeight() {
        return this.getDrawable().getIntrinsicHeight();
    }

    public void draw(@NotNull Canvas canvas) {
        Intrinsics.checkNotNullParameter(canvas, "canvas");
        Matrix matrix = this.getMatrix();
        canvas.save();
        canvas.concat(matrix);
        this.getDrawable().setBounds(this.realBounds);
        this.getDrawable().draw(canvas);
        canvas.restore();
        canvas.save();
        canvas.concat(matrix);
        if (this.textRect.width() == this.getWidth()) {
            int var10000 = this.getHeight() / 2;
            StaticLayout var10001 = this.staticLayout;
            Intrinsics.checkNotNull(var10001);
            int dy = var10000 - var10001.getHeight() / 2;
            canvas.translate(0.0F, (float)dy);
        } else {
            int dx = this.textRect.left;
            int var6 = this.textRect.top + this.textRect.height() / 2;
            StaticLayout var8 = this.staticLayout;
            Intrinsics.checkNotNull(var8);
            int dy = var6 - var8.getHeight() / 2;
            canvas.translate((float)dx, (float)dy);
        }

        StaticLayout var7 = this.staticLayout;
        Intrinsics.checkNotNull(var7);
        var7.draw(canvas);
        canvas.restore();
    }

    @NotNull
    public TextSticker setAlpha(int alpha) {
        this.textPaint.setAlpha(alpha);
        return this;
    }

    @NotNull
    public TextSticker setDrawable(@NotNull Drawable drawable) {
        Intrinsics.checkNotNullParameter(drawable, "drawable");
        this.setDrawable_(drawable);
        this.realBounds.set(0, 0, this.getWidth(), this.getHeight());
        this.textRect.set(0, 0, this.getWidth(), this.getHeight());
        return this;
    }

    @NotNull
    public final TextSticker setDrawable(@NotNull Drawable drawable, @Nullable Rect region) {
        Intrinsics.checkNotNullParameter(drawable, "drawable");
        this.setDrawable_(drawable);
        this.realBounds.set(0, 0, this.getWidth(), this.getHeight());
        if (region == null) {
            this.textRect.set(0, 0, this.getWidth(), this.getHeight());
        } else {
            this.textRect.set(region.left, region.top, region.right, region.bottom);
        }

        return this;
    }

    @NotNull
    public final TextSticker setText(@NotNull String text) {
        Intrinsics.checkNotNullParameter(text, "text");
        this.text = text;
        return this;
    }

    @NotNull
    public final TextSticker setTypeface(@Nullable Typeface typeface) {
        this.textPaint.setTypeface(typeface);
        return this;
    }

    @NotNull
    public final TextSticker setTextColor(int color) {
        this.textPaint.setColor(color);
        return this;
    }

    @NotNull
    public final TextSticker setTextAlign(@NotNull Layout.Alignment alignment) {
        Intrinsics.checkNotNullParameter(alignment, "alignment");
        this.alignment = alignment;
        return this;
    }

    @NotNull
    public final TextSticker setMaxTextSize(float size) {
        this.textPaint.setTextSize(this.convertSpToPx(size));
        this.maxTextSizePixels = this.textPaint.getTextSize();
        return this;
    }

    @NotNull
    public final TextSticker setMinTextSize(float minTextSizeScaledPixels) {
        this.minTextSizePixels = this.convertSpToPx(minTextSizeScaledPixels);
        return this;
    }

    @NotNull
    public final TextSticker setLineSpacing(float add, float multiplier) {
        this.lineSpacingMultiplier = multiplier;
        this.lineSpacingExtra = add;
        return this;
    }

    @NotNull
    public final TextSticker resizeText() {
        int availableHeightPixels = this.textRect.height();
        int availableWidthPixels = this.textRect.width();
        CharSequence text = (CharSequence)this.text;
        if (text != null && text.length() != 0 && availableHeightPixels > 0 && availableWidthPixels > 0 && !(this.maxTextSizePixels <= 0.0F)) {
            float targetTextSizePixels = this.maxTextSizePixels;

            int targetTextHeightPixels;
            for(targetTextHeightPixels = this.getTextHeightPixels(text, availableWidthPixels, targetTextSizePixels); targetTextHeightPixels > availableHeightPixels && targetTextSizePixels > this.minTextSizePixels; targetTextHeightPixels = this.getTextHeightPixels(text, availableWidthPixels, targetTextSizePixels)) {
                targetTextSizePixels = Math.max(targetTextSizePixels - (float)2, this.minTextSizePixels);
            }

            if (targetTextSizePixels == this.minTextSizePixels && targetTextHeightPixels > availableHeightPixels) {
                TextPaint textPaintCopy = new TextPaint((Paint)this.textPaint);
                textPaintCopy.setTextSize(targetTextSizePixels);
                StaticLayout staticLayout = VERSION.SDK_INT >= 23 ? Builder.obtain(text, 0, text.length(), textPaintCopy, availableWidthPixels).setAlignment(Alignment.ALIGN_NORMAL).setLineSpacing(this.lineSpacingExtra, this.lineSpacingMultiplier).setIncludePad(false).build() : new StaticLayout(text, textPaintCopy, availableWidthPixels, Alignment.ALIGN_NORMAL, this.lineSpacingMultiplier, this.lineSpacingExtra, false);
                Intrinsics.checkNotNull(staticLayout);
                if (staticLayout.getLineCount() > 0) {
                    int lastLine = staticLayout.getLineForVertical(availableHeightPixels) - 1;
                    if (lastLine >= 0) {
                        int startOffset = staticLayout.getLineStart(lastLine);
                        int endOffset = staticLayout.getLineEnd(lastLine);
                        float lineWidthPixels = staticLayout.getLineWidth(lastLine);

                        for(float ellipseWidth = textPaintCopy.measureText(this.mEllipsis); (float)availableWidthPixels < lineWidthPixels + ellipseWidth; lineWidthPixels = textPaintCopy.measureText(text.subSequence(startOffset, endOffset + 1).toString())) {
                            --endOffset;
                        }

                        this.setText(text.subSequence(0, endOffset) + this.mEllipsis);
                    }
                }
            }

            this.textPaint.setTextSize(targetTextSizePixels);
            StaticLayout var15;
            if (VERSION.SDK_INT >= 23) {
                String var10001 = this.text;
                Intrinsics.checkNotNull(var10001);
                CharSequence var14 = (CharSequence)var10001;
                String var10003 = this.text;
                Intrinsics.checkNotNull(var10003);
                var15 = Builder.obtain(var14, 0, var10003.length(), this.textPaint, this.textRect.width()).setAlignment(this.alignment).setLineSpacing(this.lineSpacingExtra, this.lineSpacingMultiplier).setIncludePad(true).build();
            } else {
                String var16 = this.text;
                Intrinsics.checkNotNull(var16);
                var15 = new StaticLayout((CharSequence)var16, this.textPaint, availableWidthPixels, this.alignment, this.lineSpacingMultiplier, this.lineSpacingExtra, true);
            }

            this.staticLayout = var15;
            return this;
        } else {
            return this;
        }
    }

    private final int getTextHeightPixels(CharSequence source, int availableWidthPixels, float textSizePixels) {
        this.textPaint.setTextSize(textSizePixels);
        StaticLayout staticLayout = VERSION.SDK_INT >= 23 ? Builder.obtain(source, 0, 0, this.textPaint, availableWidthPixels).setAlignment(Alignment.ALIGN_NORMAL).setLineSpacing(this.lineSpacingExtra, this.lineSpacingMultiplier).setIncludePad(true).build() : new StaticLayout(source, this.textPaint, availableWidthPixels, Alignment.ALIGN_NORMAL, this.lineSpacingMultiplier, this.lineSpacingExtra, true);
        Intrinsics.checkNotNull(staticLayout);
        return staticLayout.getHeight();
    }

    private final float convertSpToPx(float scaledPixels) {
        return scaledPixels * this.context.getResources().getDisplayMetrics().scaledDensity;
    }

//    // $FF: synthetic method
//    // $FF: bridge method
//    public Sticker setAlpha(int alpha) {
//        return this.setAlpha(alpha);
//    }
//
//    // $FF: synthetic method
//    // $FF: bridge method
//    public Sticker setDrawable(Drawable drawable) {
//        return this.setDrawable(drawable);
//    }
}
