package com.k2fsa.sherpa.onnx;

import kotlin.Metadata;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

public final class OnlineStream {
    @NotNull
    public static final Companion Companion = new Companion((DefaultConstructorMarker)null);
    private long ptr;

    public OnlineStream(long ptr) {
        this.ptr = ptr;
    }

    // $FF: synthetic method
    public OnlineStream(long var1, int var3, DefaultConstructorMarker var4) {
        this((var3 & 1) != 0 ? 0L : var1);
    }

    public final long getPtr() {
        return this.ptr;
    }

    public final void setPtr(long var1) {
        this.ptr = var1;
    }

    public final void acceptWaveform(@NotNull float[] samples, int sampleRate) {
        Intrinsics.checkNotNullParameter(samples, "samples");
        this.acceptWaveform(this.ptr, samples, sampleRate);
    }

    public final void inputFinished() {
        this.inputFinished(this.ptr);
    }

    public final void setOption(@NotNull String key, @NotNull String value) {
        Intrinsics.checkNotNullParameter(key, "key");
        Intrinsics.checkNotNullParameter(value, "value");
        this.setOption(this.ptr, key, value);
    }

    @NotNull
    public final String getOption(@NotNull String key) {
        Intrinsics.checkNotNullParameter(key, "key");
        return this.getOption(this.ptr, key);
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

    public final void use(@NotNull Function1 block) {
        Intrinsics.checkNotNullParameter(block, "block");

        try {
            block.invoke(this);
        } finally {
            this.release();
        }

    }

    private final native void acceptWaveform(long var1, float[] var3, int var4);

    private final native void inputFinished(long var1);

    private final native void setOption(long var1, String var3, String var4);

    private final native String getOption(long var1, String var3);

    private final native void delete(long var1);

    public OnlineStream() {
        this(0L, 1, (DefaultConstructorMarker)null);
    }

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
