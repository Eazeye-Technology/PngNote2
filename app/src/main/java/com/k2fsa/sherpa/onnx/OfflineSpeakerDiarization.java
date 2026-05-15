// OfflineSpeakerDiarization.java
package com.k2fsa.sherpa.onnx;

import android.content.res.AssetManager;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class OfflineSpeakerDiarization {
    @NotNull
    public static final Companion Companion = new Companion((DefaultConstructorMarker)null);
    @NotNull
    private final OfflineSpeakerDiarizationConfig config;
    private long ptr;
    public static final int $stable = 8;

    public OfflineSpeakerDiarization(@Nullable AssetManager assetManager, @NotNull OfflineSpeakerDiarizationConfig config) {
//        Intrinsics.checkNotNullParameter(config, "config");
        super();
        this.config = config;
        this.ptr = assetManager != null ? this.newFromAsset(assetManager, this.config) : this.newFromFile(this.config);
    }

    // $FF: synthetic method
    public OfflineSpeakerDiarization(AssetManager var1, OfflineSpeakerDiarizationConfig var2, int var3, DefaultConstructorMarker var4) {
        this((var3 & 1) != 0 ? null : var1, var2);
    }

    @NotNull
    public final OfflineSpeakerDiarizationConfig getConfig() {
        return this.config;
    }

    protected final void finalize() {
        if (this.ptr != 0L) {
            this.delete(this.ptr);
            this.ptr = 0L;
        }

    }

    public final void release() {
        this.finalize();
    }

    public final void setConfig(@NotNull OfflineSpeakerDiarizationConfig config) {
        Intrinsics.checkNotNullParameter(config, "config");
        this.setConfig(this.ptr, config);
    }

    public final int sampleRate() {
        return this.getSampleRate(this.ptr);
    }

    @NotNull
    public final OfflineSpeakerDiarizationSegment[] process(@NotNull float[] samples) {
        Intrinsics.checkNotNullParameter(samples, "samples");
        return this.process(this.ptr, samples);
    }

    @NotNull
    public final OfflineSpeakerDiarizationSegment[] processWithCallback(@NotNull float[] samples, @NotNull Function3 callback, long arg) {
        Intrinsics.checkNotNullParameter(samples, "samples");
        Intrinsics.checkNotNullParameter(callback, "callback");
        return this.processWithCallback(this.ptr, samples, callback, arg);
    }

    // $FF: synthetic method
    public static OfflineSpeakerDiarizationSegment[] processWithCallback$default(OfflineSpeakerDiarization var0, float[] var1, Function3 var2, long var3, int var5, Object var6) {
        if ((var5 & 4) != 0) {
            var3 = 0L;
        }

        return var0.processWithCallback(var1, var2, var3);
    }

    private final native void delete(long var1);

    private final native long newFromAsset(AssetManager var1, OfflineSpeakerDiarizationConfig var2);

    private final native long newFromFile(OfflineSpeakerDiarizationConfig var1);

    private final native void setConfig(long var1, OfflineSpeakerDiarizationConfig var3);

    private final native int getSampleRate(long var1);

    private final native OfflineSpeakerDiarizationSegment[] process(long var1, float[] var3);

    private final native OfflineSpeakerDiarizationSegment[] processWithCallback(long var1, float[] var3, Function3 var4, long var5);

    static {
        System.loadLibrary("sherpa-onnx-jni");
    }

    public static final class Companion {
        private Companion() {
        }

        // $FF: synthetic method
        public Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }
    }
}
