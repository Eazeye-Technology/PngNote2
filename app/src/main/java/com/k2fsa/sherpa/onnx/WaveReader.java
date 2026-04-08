package com.k2fsa.sherpa.onnx;

import android.content.res.AssetManager;

import org.jetbrains.annotations.NotNull;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

public final class WaveReader {
    @NotNull
    public static final Companion Companion = new Companion((Object)null);

    static {
        System.loadLibrary("sherpa-onnx-jni");
    }

    public static final class Companion {
        private Companion() {
        }

        @NotNull
        public final WaveData readWave(@NotNull AssetManager assetManager, @NotNull String filename) {
            Intrinsics.checkNotNullParameter(assetManager, "assetManager");
            Intrinsics.checkNotNullParameter(filename, "filename");
            return this.readWaveFromAsset(assetManager, filename);
        }

        @NotNull
        public final WaveData readWave(@NotNull String filename) {
            Intrinsics.checkNotNullParameter(filename, "filename");
            return this.readWaveFromFile(filename);
        }

        @NotNull
        public final native WaveData readWaveFromAsset(@NotNull AssetManager var1, @NotNull String var2);

        @NotNull
        public final native WaveData readWaveFromFile(@NotNull String var1);

        // $FF: synthetic method
        public Companion(Object $constructor_marker) {
            this();
        }
    }
}
