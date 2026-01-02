package com.outsbook.libs.canvaseditor.models;

import com.outsbook.libs.canvaseditor.enums.DrawType;
import com.outsbook.libs.canvaseditor.stickers.Sticker;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class DrawObject {
    @Nullable
    private final PathAndPaint pathAndPaint;
    @Nullable
    private final Sticker sticker;
    @NotNull
    private final DrawType drawType;

    public DrawObject(@Nullable PathAndPaint pathAndPaint, @Nullable Sticker sticker, @NotNull DrawType drawType) {
        //Intrinsics.checkNotNullParameter(drawType, "drawType");
        super();
        this.pathAndPaint = pathAndPaint;
        this.sticker = sticker;
        this.drawType = drawType;
    }

    @Nullable
    public final PathAndPaint getPathAndPaint() {
        return this.pathAndPaint;
    }

    @Nullable
    public final Sticker getSticker() {
        return this.sticker;
    }

    @NotNull
    public final DrawType getDrawType() {
        return this.drawType;
    }

    @Nullable
    public final PathAndPaint component1() {
        return this.pathAndPaint;
    }

    @Nullable
    public final Sticker component2() {
        return this.sticker;
    }

    @NotNull
    public final DrawType component3() {
        return this.drawType;
    }

    @NotNull
    public final DrawObject copy(@Nullable PathAndPaint pathAndPaint, @Nullable Sticker sticker, @NotNull DrawType drawType) {
        Intrinsics.checkNotNullParameter(drawType, "drawType");
        return new DrawObject(pathAndPaint, sticker, drawType);
    }

    // $FF: synthetic method
    public static DrawObject copy$default(DrawObject var0, PathAndPaint var1, Sticker var2, DrawType var3, int var4, Object var5) {
        if ((var4 & 1) != 0) {
            var1 = var0.pathAndPaint;
        }
        if ((var4 & 2) != 0) {
            var2 = var0.sticker;
        }
        if ((var4 & 4) != 0) {
            var3 = var0.drawType;
        }
        return var0.copy(var1, var2, var3);
    }

    @NotNull
    public String toString() {
        return "DrawObject(pathAndPaint=" + this.pathAndPaint + ", sticker=" + this.sticker + ", drawType=" + this.drawType + ')';
    }

    public int hashCode() {
        int result = this.pathAndPaint == null ? 0 : this.pathAndPaint.hashCode();
        result = result * 31 + (this.sticker == null ? 0 : this.sticker.hashCode());
        result = result * 31 + this.drawType.hashCode();
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        } else if (!(other instanceof DrawObject)) {
            return false;
        } else {
            DrawObject var2 = (DrawObject)other;
            if (!Intrinsics.areEqual(this.pathAndPaint, var2.pathAndPaint)) {
                return false;
            } else if (!Intrinsics.areEqual(this.sticker, var2.sticker)) {
                return false;
            } else {
                return this.drawType == var2.drawType;
            }
        }
    }
}
