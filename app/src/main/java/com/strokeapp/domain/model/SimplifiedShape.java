package com.strokeapp.domain.model;

import java.util.List;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SimplifiedShape {
    @NotNull
    private final ShapeType type;
    @NotNull
    private final List<StrokePoint> points;
    private final long color;
    public static final int $stable = 8;

    public SimplifiedShape(@NotNull ShapeType type, @NotNull List<StrokePoint> points, long color) {
//        Intrinsics.checkNotNullParameter(type, "type");
//        Intrinsics.checkNotNullParameter(points, "points");
        super();
        this.type = type;
        this.points = points;
        this.color = color;
    }

    @NotNull
    public final ShapeType getType() {
        return this.type;
    }

    @NotNull
    public final List<StrokePoint> getPoints() {
        return this.points;
    }

    public final long getColor() {
        return this.color;
    }

    @NotNull
    public final ShapeType component1() {
        return this.type;
    }

    @NotNull
    public final List<StrokePoint> component2() {
        return this.points;
    }

    public final long component3() {
        return this.color;
    }

    @NotNull
    public final SimplifiedShape copy(@NotNull ShapeType type, @NotNull List<StrokePoint> points, long color) {
//        Intrinsics.checkNotNullParameter(type, "type");
//        Intrinsics.checkNotNullParameter(points, "points");
        return new SimplifiedShape(type, points, color);
    }

    // $FF: synthetic method
    public static SimplifiedShape copy$default(SimplifiedShape var0, ShapeType var1, List<StrokePoint> var2, long var3, int var5, Object var6) {
        if ((var5 & 1) != 0) {
            var1 = var0.type;
        }

        if ((var5 & 2) != 0) {
            var2 = var0.points;
        }

        if ((var5 & 4) != 0) {
            var3 = var0.color;
        }

        return var0.copy(var1, var2, var3);
    }

    @NotNull
    public String toString() {
        return "SimplifiedShape(type=" + this.type + ", points=" + this.points + ", color=" + this.color + ')';
    }

    public int hashCode() {
        int result = this.type.hashCode();
        result = result * 31 + this.points.hashCode();
        result = result * 31 + Long.hashCode(this.color);
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        } else if (!(other instanceof SimplifiedShape)) {
            return false;
        } else {
            SimplifiedShape var2 = (SimplifiedShape)other;
            if (this.type != var2.type) {
                return false;
            } else if (!Intrinsics.areEqual(this.points, var2.points)) {
                return false;
            } else {
                return this.color == var2.color;
            }
        }
    }
}
