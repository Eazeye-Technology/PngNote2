package com.strokeapp.domain.model;

import java.util.List;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class Stroke {
    @NotNull
    private final List<StrokePoint> points;
    private final int id;
    public static final int $stable = 8;

    public Stroke(@NotNull List<StrokePoint> points, int id) {
//        Intrinsics.checkNotNullParameter(points, "points");
        super();
        this.points = points;
        this.id = id;
    }

    // $FF: synthetic method
    public Stroke(List<StrokePoint> var1, int var2, int var3, DefaultConstructorMarker var4) {
        this(var1, (var3 & 2) != 0 ? 0 : var2);
    }

    @NotNull
    public final List<StrokePoint> getPoints() {
        return this.points;
    }

    public final int getId() {
        return this.id;
    }

    @NotNull
    public final List<StrokePoint> component1() {
        return this.points;
    }

    public final int component2() {
        return this.id;
    }

    @NotNull
    public final Stroke copy(@NotNull List<StrokePoint> points, int id) {
//        Intrinsics.checkNotNullParameter(points, "points");
        return new Stroke(points, id);
    }

    // $FF: synthetic method
    public static Stroke copy$default(Stroke var0, List<StrokePoint> var1, int var2, int var3, Object var4) {
        if ((var3 & 1) != 0) {
            var1 = var0.points;
        }

        if ((var3 & 2) != 0) {
            var2 = var0.id;
        }

        return var0.copy(var1, var2);
    }

    @NotNull
    public String toString() {
        return "Stroke(points=" + this.points + ", id=" + this.id + ')';
    }

    public int hashCode() {
        int result = this.points.hashCode();
        result = result * 31 + Integer.hashCode(this.id);
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        } else if (!(other instanceof Stroke)) {
            return false;
        } else {
            Stroke var2 = (Stroke)other;
            if (!Intrinsics.areEqual(this.points, var2.points)) {
                return false;
            } else {
                return this.id == var2.id;
            }
        }
    }
}
