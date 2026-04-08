// FeatureConfig.java
package com.k2fsa.sherpa.onnx;

import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class FeatureConfig {
    private int sampleRate;
    private int featureDim;
    private float dither;

    public FeatureConfig(int sampleRate, int featureDim, float dither) {
        this.sampleRate = sampleRate;
        this.featureDim = featureDim;
        this.dither = dither;
    }

    // $FF: synthetic method
    public FeatureConfig(int var1, int var2, float var3, int var4, DefaultConstructorMarker var5) {
        this((var4 & 1) != 0 ? 16000 : var1, (var4 & 2) != 0 ? 80 : var2, (var4 & 4) != 0 ? 0.0F : var3);
    }

    public final int getSampleRate() {
        return this.sampleRate;
    }

    public final void setSampleRate(int var1) {
        this.sampleRate = var1;
    }

    public final int getFeatureDim() {
        return this.featureDim;
    }

    public final void setFeatureDim(int var1) {
        this.featureDim = var1;
    }

    public final float getDither() {
        return this.dither;
    }

    public final void setDither(float var1) {
        this.dither = var1;
    }

    public final int component1() {
        return this.sampleRate;
    }

    public final int component2() {
        return this.featureDim;
    }

    public final float component3() {
        return this.dither;
    }

    @NotNull
    public final FeatureConfig copy(int sampleRate, int featureDim, float dither) {
        return new FeatureConfig(sampleRate, featureDim, dither);
    }

    // $FF: synthetic method
    public static FeatureConfig copy$default(FeatureConfig var0, int var1, int var2, float var3, int var4, Object var5) {
        if ((var4 & 1) != 0) {
            var1 = var0.sampleRate;
        }

        if ((var4 & 2) != 0) {
            var2 = var0.featureDim;
        }

        if ((var4 & 4) != 0) {
            var3 = var0.dither;
        }

        return var0.copy(var1, var2, var3);
    }

    @NotNull
    public String toString() {
        return "FeatureConfig(sampleRate=" + this.sampleRate + ", featureDim=" + this.featureDim + ", dither=" + this.dither + ')';
    }

    public int hashCode() {
        int result = Integer.hashCode(this.sampleRate);
        result = result * 31 + Integer.hashCode(this.featureDim);
        result = result * 31 + Float.hashCode(this.dither);
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        } else if (!(other instanceof FeatureConfig)) {
            return false;
        } else {
            FeatureConfig var2 = (FeatureConfig)other;
            if (this.sampleRate != var2.sampleRate) {
                return false;
            } else if (this.featureDim != var2.featureDim) {
                return false;
            } else {
                return Float.compare(this.dither, var2.dither) == 0;
            }
        }
    }

    public FeatureConfig() {
        this(0, 0, 0.0F, 7, (DefaultConstructorMarker)null);
    }
}

