// SpeakerRecognition.java
package com.k2fsa.sherpa.onnx;

import android.content.res.AssetManager;
import android.util.Log;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SpeakerRecognition {
    @NotNull
    public static final SpeakerRecognition INSTANCE = new SpeakerRecognition();
    @Nullable
    private static SpeakerEmbeddingExtractor _extractor;
    @Nullable
    private static SpeakerEmbeddingManager _manager;
    public static final int $stable = 8;

    private SpeakerRecognition() {
    }

    @Nullable
    public final SpeakerEmbeddingExtractor get_extractor() {
        return _extractor;
    }

    public final void set_extractor(@Nullable SpeakerEmbeddingExtractor var1) {
        _extractor = var1;
    }

    @Nullable
    public final SpeakerEmbeddingManager get_manager() {
        return _manager;
    }

    public final void set_manager(@Nullable SpeakerEmbeddingManager var1) {
        _manager = var1;
    }

    @NotNull
    public final SpeakerEmbeddingExtractor getExtractor() {
        SpeakerEmbeddingExtractor var10000 = _extractor;
        Intrinsics.checkNotNull(var10000);
        return var10000;
    }

    @NotNull
    public final SpeakerEmbeddingManager getManager() {
        SpeakerEmbeddingManager var10000 = _manager;
        Intrinsics.checkNotNull(var10000);
        return var10000;
    }

    public final void initExtractor(@Nullable AssetManager assetManager) {
        synchronized(this){}

        try {
            int var3 = 0;
            SpeakerRecognition var10000 = INSTANCE;
            if (_extractor == null) {
                Log.i("sherpa-onnx", "Initializing speaker embedding extractor");
                var10000 = INSTANCE;
                _extractor = new SpeakerEmbeddingExtractor(assetManager, new SpeakerEmbeddingExtractorConfig(SpeakerKt.access$getModelName$p(), 2, false, "cpu"));
                var10000 = INSTANCE;
                SpeakerRecognition var10002 = INSTANCE;
                SpeakerEmbeddingExtractor var9 = _extractor;
                Intrinsics.checkNotNull(var9);
                _manager = new SpeakerEmbeddingManager(var9.dim());
                Unit var6 = Unit.INSTANCE;
                return;
            }
        } finally {
            ;
        }

    }

    // $FF: synthetic method
    public static void initExtractor$default(SpeakerRecognition var0, AssetManager var1, int var2, Object var3) {
        if ((var2 & 1) != 0) {
            var1 = null;
        }

        var0.initExtractor(var1);
    }
}
