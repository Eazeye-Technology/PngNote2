// StrokePoint.java
package com.strokeapp.domain.model;

import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class StrokePoint {
    private final float x;
    private final float y;
//    public static final int $stable;

    public StrokePoint(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public final float getX() {
        return this.x;
    }

    public final float getY() {
        return this.y;
    }

//    public final long toOffset_F1C5BW0/* $FF was: toOffset-F1C5BW0*/() {
//        return OffsetKt.Offset(this.x, this.y);
//    }

    public final float component1() {
        return this.x;
    }

    public final float component2() {
        return this.y;
    }

    @NotNull
    public final StrokePoint copy(float x, float y) {
        return new StrokePoint(x, y);
    }

    // $FF: synthetic method
    public static StrokePoint copy$default(StrokePoint var0, float var1, float var2, int var3, Object var4) {
        if ((var3 & 1) != 0) {
            var1 = var0.x;
        }

        if ((var3 & 2) != 0) {
            var2 = var0.y;
        }

        return var0.copy(var1, var2);
    }

    @NotNull
    public String toString() {
        return "StrokePoint(x=" + this.x + ", y=" + this.y + ')';
    }

    public int hashCode() {
        int result = Float.hashCode(this.x);
        result = result * 31 + Float.hashCode(this.y);
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        } else if (!(other instanceof StrokePoint)) {
            return false;
        } else {
            StrokePoint var2 = (StrokePoint)other;
            if (Float.compare(this.x, var2.x) != 0) {
                return false;
            } else {
                return Float.compare(this.y, var2.y) == 0;
            }
        }
    }
}

