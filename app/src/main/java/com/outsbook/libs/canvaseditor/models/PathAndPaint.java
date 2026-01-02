package com.outsbook.libs.canvaseditor.models;

import android.graphics.Paint;
import android.graphics.Path;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class PathAndPaint {
    @NotNull
    private final Path path;
    @NotNull
    private final Paint paint;

    public PathAndPaint(@NotNull Path path, @NotNull Paint paint) {
        //Intrinsics.checkNotNullParameter(path, "path");
        //Intrinsics.checkNotNullParameter(paint, "paint");
        super();
        this.path = path;
        this.paint = paint;
    }

    @NotNull
    public final Path getPath() {
        return this.path;
    }

    @NotNull
    public final Paint getPaint() {
        return this.paint;
    }

    @NotNull
    public final Path component1() {
        return this.path;
    }

    @NotNull
    public final Paint component2() {
        return this.paint;
    }

    @NotNull
    public final PathAndPaint copy(@NotNull Path path, @NotNull Paint paint) {
        Intrinsics.checkNotNullParameter(path, "path");
        Intrinsics.checkNotNullParameter(paint, "paint");
        return new PathAndPaint(path, paint);
    }

    // $FF: synthetic method
    public static PathAndPaint copy$default(PathAndPaint var0, Path var1, Paint var2, int var3, Object var4) {
        if ((var3 & 1) != 0) {
            var1 = var0.path;
        }
        if ((var3 & 2) != 0) {
            var2 = var0.paint;
        }
        return var0.copy(var1, var2);
    }

    @NotNull
    public String toString() {
        return "PathAndPaint(path=" + this.path + ", paint=" + this.paint + ')';
    }

    public int hashCode() {
        int result = this.path.hashCode();
        result = result * 31 + this.paint.hashCode();
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        } else if (!(other instanceof PathAndPaint)) {
            return false;
        } else {
            PathAndPaint var2 = (PathAndPaint)other;
            if (!Intrinsics.areEqual(this.path, var2.path)) {
                return false;
            } else {
                return Intrinsics.areEqual(this.paint, var2.paint);
            }
        }
    }
}
