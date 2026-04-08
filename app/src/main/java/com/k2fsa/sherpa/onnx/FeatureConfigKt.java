package com.k2fsa.sherpa.onnx;


import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import org.jetbrains.annotations.NotNull;

public final class FeatureConfigKt {
    @NotNull
    public static final FeatureConfig getFeatureConfig(int sampleRate, int featureDim) {
        return new FeatureConfig(sampleRate, featureDim, 0.0F, 4, (DefaultConstructorMarker)null);
    }
}
