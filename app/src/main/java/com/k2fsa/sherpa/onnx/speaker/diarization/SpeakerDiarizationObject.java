// SpeakerDiarizationObject.java
package com.k2fsa.sherpa.onnx.speaker.diarization;

import android.content.res.AssetManager;
import android.util.Log;
import com.k2fsa.sherpa.onnx.FastClusteringConfig;
import com.k2fsa.sherpa.onnx.OfflineSpeakerDiarization;
import com.k2fsa.sherpa.onnx.OfflineSpeakerDiarizationConfig;
import com.k2fsa.sherpa.onnx.OfflineSpeakerSegmentationModelConfig;
import com.k2fsa.sherpa.onnx.OfflineSpeakerSegmentationPyannoteModelConfig;
import com.k2fsa.sherpa.onnx.SpeakerEmbeddingExtractorConfig;
import kotlin.Unit;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SpeakerDiarizationObject {
    @NotNull
    public static final SpeakerDiarizationObject INSTANCE = new SpeakerDiarizationObject();
    @Nullable
    private static OfflineSpeakerDiarization _sd;
    public static final int $stable = 8;

    private SpeakerDiarizationObject() {
    }

    @Nullable
    public final OfflineSpeakerDiarization get_sd() {
        return _sd;
    }

    public final void set_sd(@Nullable OfflineSpeakerDiarization var1) {
        _sd = var1;
    }

    @NotNull
    public final OfflineSpeakerDiarization getSd() {
        OfflineSpeakerDiarization var10000 = _sd;
        Intrinsics.checkNotNull(var10000);
        return var10000;
    }

    public final void initSpeakerDiarization(@Nullable AssetManager assetManager) {
        synchronized(this){}

        try {
            int var3 = 0;
            SpeakerDiarizationObject var10000 = INSTANCE;
            if (_sd == null) {
                Log.i("sherpa-onnx-sd", "Initializing sherpa-onnx speaker diarization");
                OfflineSpeakerSegmentationModelConfig var10002 = new OfflineSpeakerSegmentationModelConfig(new OfflineSpeakerSegmentationPyannoteModelConfig(SpeakerDiarizationObjectKt.getSegmentationModel()), 0, true, (String)null, 10, (DefaultConstructorMarker)null);
                String var4 = SpeakerDiarizationObjectKt.getEmbeddingModel();
                OfflineSpeakerDiarizationConfig config = new OfflineSpeakerDiarizationConfig(var10002, new SpeakerEmbeddingExtractorConfig(var4, 2, true, (String)null, 8, (DefaultConstructorMarker)null), new FastClusteringConfig(-1, 0.5F), 0.2F, 0.5F);
                var10000 = INSTANCE;
                _sd = new OfflineSpeakerDiarization(assetManager, config);
                Unit var8 = Unit.INSTANCE;
                return;
            }
        } finally {
            ;
        }

    }

    // $FF: synthetic method
    public static void initSpeakerDiarization$default(SpeakerDiarizationObject var0, AssetManager var1, int var2, Object var3) {
        if ((var2 & 1) != 0) {
            var1 = null;
        }

        var0.initSpeakerDiarization(var1);
    }
}
