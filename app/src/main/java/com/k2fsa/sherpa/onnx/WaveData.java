// WaveData.java
package com.k2fsa.sherpa.onnx;

import android.content.res.AssetManager;

import java.util.Arrays;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class WaveData {
    @NotNull
    private final float[] samples;
    private final int sampleRate;

    public WaveData(@NotNull float[] samples, int sampleRate) {
        //Intrinsics.checkNotNullParameter(samples, "samples");
        super();
        this.samples = samples;
        this.sampleRate = sampleRate;
    }

    @NotNull
    public final float[] getSamples() {
        return this.samples;
    }

    public final int getSampleRate() {
        return this.sampleRate;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        } else if (!Intrinsics.areEqual(this.getClass(), other != null ? other.getClass() : null)) {
            return false;
        } else {
            Intrinsics.checkNotNull(other, "null cannot be cast to non-null type com.k2fsa.sherpa.onnx.WaveData");
            WaveData var10000 = (WaveData)other;
            if (!Arrays.equals(this.samples, ((WaveData)other).samples)) {
                return false;
            } else {
                return this.sampleRate == ((WaveData)other).sampleRate;
            }
        }
    }

    public int hashCode() {
        int result = Arrays.hashCode(this.samples);
        result = 31 * result + this.sampleRate;
        return result;
    }

    @NotNull
    public final float[] component1() {
        return this.samples;
    }

    public final int component2() {
        return this.sampleRate;
    }

    @NotNull
    public final WaveData copy(@NotNull float[] samples, int sampleRate) {
        Intrinsics.checkNotNullParameter(samples, "samples");
        return new WaveData(samples, sampleRate);
    }

    // $FF: synthetic method
    public static WaveData copy$default(WaveData var0, float[] var1, int var2, int var3, Object var4) {
        if ((var3 & 1) != 0) {
            var1 = var0.samples;
        }

        if ((var3 & 2) != 0) {
            var2 = var0.sampleRate;
        }

        return var0.copy(var1, var2);
    }

    @NotNull
    public String toString() {
        return "WaveData(samples=" + Arrays.toString(this.samples) + ", sampleRate=" + this.sampleRate + ')';
    }
}

