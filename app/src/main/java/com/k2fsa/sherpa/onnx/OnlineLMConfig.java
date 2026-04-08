package com.k2fsa.sherpa.onnx;

import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class OnlineLMConfig {
    @NotNull
    private String model;
    private float scale;

    public OnlineLMConfig(@NotNull String model, float scale) {
//        Intrinsics.checkNotNullParameter(model, "model");
        super();
        this.model = model;
        this.scale = scale;
    }

    // $FF: synthetic method
    public OnlineLMConfig(String var1, float var2, int var3, DefaultConstructorMarker var4) {
        this((var3 & 1) != 0 ? "" : var1, (var3 & 2) != 0 ? 0.5F : var2);
    }

    @NotNull
    public final String getModel() {
        return this.model;
    }

    public final void setModel(@NotNull String var1) {
        Intrinsics.checkNotNullParameter(var1, "<set-?>");
        this.model = var1;
    }

    public final float getScale() {
        return this.scale;
    }

    public final void setScale(float var1) {
        this.scale = var1;
    }

    @NotNull
    public final String component1() {
        return this.model;
    }

    public final float component2() {
        return this.scale;
    }

    @NotNull
    public final OnlineLMConfig copy(@NotNull String model, float scale) {
        Intrinsics.checkNotNullParameter(model, "model");
        return new OnlineLMConfig(model, scale);
    }

    // $FF: synthetic method
    public static OnlineLMConfig copy$default(OnlineLMConfig var0, String var1, float var2, int var3, Object var4) {
        if ((var3 & 1) != 0) {
            var1 = var0.model;
        }

        if ((var3 & 2) != 0) {
            var2 = var0.scale;
        }

        return var0.copy(var1, var2);
    }

    @NotNull
    public String toString() {
        return "OnlineLMConfig(model=" + this.model + ", scale=" + this.scale + ')';
    }

    public int hashCode() {
        int result = this.model.hashCode();
        result = result * 31 + Float.hashCode(this.scale);
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        } else if (!(other instanceof OnlineLMConfig)) {
            return false;
        } else {
            OnlineLMConfig var2 = (OnlineLMConfig)other;
            if (!Intrinsics.areEqual(this.model, var2.model)) {
                return false;
            } else {
                return Float.compare(this.scale, var2.scale) == 0;
            }
        }
    }

    public OnlineLMConfig() {
        this((String)null, 0.0F, 3, (DefaultConstructorMarker)null);
    }
}
