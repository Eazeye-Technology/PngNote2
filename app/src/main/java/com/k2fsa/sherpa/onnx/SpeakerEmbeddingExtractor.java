// SpeakerEmbeddingExtractor.java
package com.k2fsa.sherpa.onnx;

import android.content.res.AssetManager;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SpeakerEmbeddingExtractor {
    @NotNull
    public static final Companion Companion = new Companion((DefaultConstructorMarker)null);
    private long ptr;
    public static final int $stable = 8;

    public SpeakerEmbeddingExtractor(@Nullable AssetManager assetManager, @NotNull SpeakerEmbeddingExtractorConfig config) {
//        Intrinsics.checkNotNullParameter(config, "config");
        super();
        this.ptr = assetManager != null ? this.newFromAsset(assetManager, config) : this.newFromFile(config);
    }

    // $FF: synthetic method
    public SpeakerEmbeddingExtractor(AssetManager var1, SpeakerEmbeddingExtractorConfig var2, int var3, DefaultConstructorMarker var4) {
        this((var3 & 1) != 0 ? null : var1, var2);
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
    public final OnlineStream createStream() {
        long p = this.createStream(this.ptr);
        return new OnlineStream(p);
    }

    public final boolean isReady(@NotNull OnlineStream stream) {
        Intrinsics.checkNotNullParameter(stream, "stream");
        return this.isReady(this.ptr, stream.getPtr());
    }

    @NotNull
    public final float[] compute(@NotNull OnlineStream stream) {
        Intrinsics.checkNotNullParameter(stream, "stream");
        return this.compute(this.ptr, stream.getPtr());
    }

    public final int dim() {
        return this.dim(this.ptr);
    }

    private final native long newFromAsset(AssetManager var1, SpeakerEmbeddingExtractorConfig var2);

    private final native long newFromFile(SpeakerEmbeddingExtractorConfig var1);

    private final native void delete(long var1);

    private final native long createStream(long var1);

    private final native boolean isReady(long var1, long var3);

    private final native float[] compute(long var1, long var3);

    private final native int dim(long var1);

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
