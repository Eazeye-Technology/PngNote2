// WaveData.java
package com.k2fsa.sherpa.onnx.speaker.diarization.screens;

import java.util.Arrays;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class WaveData {
    @Nullable
    private final Integer sampleRate;
    @Nullable
    private final float[] samples;
    @Nullable
    private final String msg;
    public static final int $stable = 8;

    public WaveData(@Nullable Integer sampleRate, @Nullable float[] samples, @Nullable String msg) {
        this.sampleRate = sampleRate;
        this.samples = samples;
        this.msg = msg;
    }

    // $FF: synthetic method
    public WaveData(Integer var1, float[] var2, String var3, int var4, DefaultConstructorMarker var5) {
        this((var4 & 1) != 0 ? null : var1, (var4 & 2) != 0 ? null : var2, (var4 & 4) != 0 ? null : var3);
    }

    @Nullable
    public final Integer getSampleRate() {
        return this.sampleRate;
    }

    @Nullable
    public final float[] getSamples() {
        return this.samples;
    }

    @Nullable
    public final String getMsg() {
        return this.msg;
    }

    @Nullable
    public final Integer component1() {
        return this.sampleRate;
    }

    @Nullable
    public final float[] component2() {
        return this.samples;
    }

    @Nullable
    public final String component3() {
        return this.msg;
    }

    @NotNull
    public final WaveData copy(@Nullable Integer sampleRate, @Nullable float[] samples, @Nullable String msg) {
        return new WaveData(sampleRate, samples, msg);
    }

    // $FF: synthetic method
    public static WaveData copy$default(WaveData var0, Integer var1, float[] var2, String var3, int var4, Object var5) {
        if ((var4 & 1) != 0) {
            var1 = var0.sampleRate;
        }

        if ((var4 & 2) != 0) {
            var2 = var0.samples;
        }

        if ((var4 & 4) != 0) {
            var3 = var0.msg;
        }

        return var0.copy(var1, var2, var3);
    }

    @NotNull
    public String toString() {
        return "WaveData(sampleRate=" + this.sampleRate + ", samples=" + Arrays.toString(this.samples) + ", msg=" + this.msg + ')';
    }

    public int hashCode() {
        int result = this.sampleRate == null ? 0 : this.sampleRate.hashCode();
        result = result * 31 + (this.samples == null ? 0 : Arrays.hashCode(this.samples));
        result = result * 31 + (this.msg == null ? 0 : this.msg.hashCode());
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        } else if (!(other instanceof WaveData)) {
            return false;
        } else {
            WaveData var2 = (WaveData)other;
            if (!Intrinsics.areEqual(this.sampleRate, var2.sampleRate)) {
                return false;
            } else if (!Intrinsics.areEqual(this.samples, var2.samples)) {
                return false;
            } else {
                return Intrinsics.areEqual(this.msg, var2.msg);
            }
        }
    }

    public WaveData() {
        this((Integer)null, (float[])null, (String)null, 7, (DefaultConstructorMarker)null);
    }
}
