package com.k2fsa.sherpa.onnx;

import android.content.res.AssetManager;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class OnlineRecognizer {
    @NotNull
    public static final Companion Companion = new Companion((DefaultConstructorMarker)null);
    @NotNull
    private final OnlineRecognizerConfig config;
    private long ptr;

    public OnlineRecognizer(@Nullable AssetManager assetManager, @NotNull OnlineRecognizerConfig config) {
//        Intrinsics.checkNotNullParameter(config, "config");
        super();
        this.config = config;
        this.ptr = assetManager != null ? this.newFromAsset(assetManager, this.config) : this.newFromFile(this.config);
    }

    // $FF: synthetic method
    public OnlineRecognizer(AssetManager var1, OnlineRecognizerConfig var2, int var3, DefaultConstructorMarker var4) {
        this((var3 & 1) != 0 ? null : var1, var2);
    }

    @NotNull
    public final OnlineRecognizerConfig getConfig() {
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

    @NotNull
    public final OnlineStream createStream(@NotNull String hotwords) {
        Intrinsics.checkNotNullParameter(hotwords, "hotwords");
        long p = this.createStream(this.ptr, hotwords);
        return new OnlineStream(p);
    }

    // $FF: synthetic method
    public static OnlineStream createStream$default(OnlineRecognizer var0, String var1, int var2, Object var3) {
        if ((var2 & 1) != 0) {
            var1 = "";
        }
        return var0.createStream(var1);
    }

    public final void reset(@NotNull OnlineStream stream) {
        Intrinsics.checkNotNullParameter(stream, "stream");
        this.reset(this.ptr, stream.getPtr());
    }

    public final void decode(@NotNull OnlineStream stream) {
        Intrinsics.checkNotNullParameter(stream, "stream");
        this.decode(this.ptr, stream.getPtr());
    }

    public final boolean isEndpoint(@NotNull OnlineStream stream) {
        Intrinsics.checkNotNullParameter(stream, "stream");
        return this.isEndpoint(this.ptr, stream.getPtr());
    }

    public final boolean isReady(@NotNull OnlineStream stream) {
        Intrinsics.checkNotNullParameter(stream, "stream");
        return this.isReady(this.ptr, stream.getPtr());
    }

    @NotNull
    public final OnlineRecognizerResult getResult(@NotNull OnlineStream stream) {
        Intrinsics.checkNotNullParameter(stream, "stream");
        return this.getResult(this.ptr, stream.getPtr());
    }

    private final native void delete(long var1);

    private final native long newFromAsset(AssetManager var1, OnlineRecognizerConfig var2);

    private final native long newFromFile(OnlineRecognizerConfig var1);

    private final native long createStream(long var1, String var3);

    private final native void reset(long var1, long var3);

    private final native void decode(long var1, long var3);

    private final native boolean isEndpoint(long var1, long var3);

    private final native boolean isReady(long var1, long var3);

    private final native OnlineRecognizerResult getResult(long var1, long var3);

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

