// SpeakerEmbeddingManager.java
package com.k2fsa.sherpa.onnx;

import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

public final class SpeakerEmbeddingManager {
    @NotNull
    public static final Companion Companion = new Companion((DefaultConstructorMarker)null);
    private final int dim;
    private long ptr;
    public static final int $stable = 8;

    public SpeakerEmbeddingManager(int dim) {
        this.dim = dim;
        this.ptr = this.create(this.dim);
    }

    public final int getDim() {
        return this.dim;
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

    public final boolean add(@NotNull String name, @NotNull float[] embedding) {
        Intrinsics.checkNotNullParameter(name, "name");
        Intrinsics.checkNotNullParameter(embedding, "embedding");
        return this.add(this.ptr, name, embedding);
    }

    public final boolean add(@NotNull String name, @NotNull float[][] embedding) {
        Intrinsics.checkNotNullParameter(name, "name");
        Intrinsics.checkNotNullParameter(embedding, "embedding");
        return this.addList(this.ptr, name, embedding);
    }

    public final boolean remove(@NotNull String name) {
        Intrinsics.checkNotNullParameter(name, "name");
        return this.remove(this.ptr, name);
    }

    @NotNull
    public final String search(@NotNull float[] embedding, float threshold) {
        Intrinsics.checkNotNullParameter(embedding, "embedding");
        return this.search(this.ptr, embedding, threshold);
    }

    public final boolean verify(@NotNull String name, @NotNull float[] embedding, float threshold) {
        Intrinsics.checkNotNullParameter(name, "name");
        Intrinsics.checkNotNullParameter(embedding, "embedding");
        return this.verify(this.ptr, name, embedding, threshold);
    }

    public final boolean contains(@NotNull String name) {
        Intrinsics.checkNotNullParameter(name, "name");
        return this.contains(this.ptr, name);
    }

    public final int numSpeakers() {
        return this.numSpeakers(this.ptr);
    }

    @NotNull
    public final String[] allSpeakerNames() {
        return this.allSpeakerNames(this.ptr);
    }

    private final native long create(int var1);

    private final native void delete(long var1);

    private final native boolean add(long var1, String var3, float[] var4);

    private final native boolean addList(long var1, String var3, float[][] var4);

    private final native boolean remove(long var1, String var3);

    private final native String search(long var1, float[] var3, float var4);

    private final native boolean verify(long var1, String var3, float[] var4, float var5);

    private final native boolean contains(long var1, String var3);

    private final native int numSpeakers(long var1);

    private final native String[] allSpeakerNames(long var1);

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
