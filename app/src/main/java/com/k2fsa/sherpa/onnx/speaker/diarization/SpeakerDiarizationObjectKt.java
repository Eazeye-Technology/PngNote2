// SpeakerDiarizationObjectKt.java
package com.k2fsa.sherpa.onnx.speaker.diarization;

import org.jetbrains.annotations.NotNull;

public final class SpeakerDiarizationObjectKt {
    @NotNull
    private static final String segmentationModel = "segmentation.onnx";
    @NotNull
    private static final String embeddingModel = "embedding.onnx";

    @NotNull
    public static final String getSegmentationModel() {
        return segmentationModel;
    }

    @NotNull
    public static final String getEmbeddingModel() {
        return embeddingModel;
    }
}
