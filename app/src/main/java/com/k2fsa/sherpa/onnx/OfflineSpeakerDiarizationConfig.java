// OfflineSpeakerDiarizationConfig.java
package com.k2fsa.sherpa.onnx;

import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class OfflineSpeakerDiarizationConfig {
    @NotNull
    private OfflineSpeakerSegmentationModelConfig segmentation;
    @NotNull
    private SpeakerEmbeddingExtractorConfig embedding;
    @NotNull
    private FastClusteringConfig clustering;
    private float minDurationOn;
    private float minDurationOff;
    public static final int $stable = 8;

    public OfflineSpeakerDiarizationConfig(@NotNull OfflineSpeakerSegmentationModelConfig segmentation, @NotNull SpeakerEmbeddingExtractorConfig embedding, @NotNull FastClusteringConfig clustering, float minDurationOn, float minDurationOff) {
//        Intrinsics.checkNotNullParameter(segmentation, "segmentation");
//        Intrinsics.checkNotNullParameter(embedding, "embedding");
//        Intrinsics.checkNotNullParameter(clustering, "clustering");
        super();
        this.segmentation = segmentation;
        this.embedding = embedding;
        this.clustering = clustering;
        this.minDurationOn = minDurationOn;
        this.minDurationOff = minDurationOff;
    }

    // $FF: synthetic method
    public OfflineSpeakerDiarizationConfig(OfflineSpeakerSegmentationModelConfig var1, SpeakerEmbeddingExtractorConfig var2, FastClusteringConfig var3, float var4, float var5, int var6, DefaultConstructorMarker var7) {
        this((var6 & 1) != 0 ? new OfflineSpeakerSegmentationModelConfig((OfflineSpeakerSegmentationPyannoteModelConfig)null, 0, false, (String)null, 15, (DefaultConstructorMarker)null) : var1,
                (var6 & 2) != 0 ? new SpeakerEmbeddingExtractorConfig((String)null, 0, false, (String)null, 15, (DefaultConstructorMarker)null) : var2,
                (var6 & 4) != 0 ? new FastClusteringConfig(0, 0.0F, 3, (DefaultConstructorMarker)null) : var3,
                (var6 & 8) != 0 ? 0.2F : var4,
                (var6 & 16) != 0 ? 0.5F : var5);
    }

    @NotNull
    public final OfflineSpeakerSegmentationModelConfig getSegmentation() {
        return this.segmentation;
    }

    public final void setSegmentation(@NotNull OfflineSpeakerSegmentationModelConfig var1) {
        Intrinsics.checkNotNullParameter(var1, "<set-?>");
        this.segmentation = var1;
    }

    @NotNull
    public final SpeakerEmbeddingExtractorConfig getEmbedding() {
        return this.embedding;
    }

    public final void setEmbedding(@NotNull SpeakerEmbeddingExtractorConfig var1) {
        Intrinsics.checkNotNullParameter(var1, "<set-?>");
        this.embedding = var1;
    }

    @NotNull
    public final FastClusteringConfig getClustering() {
        return this.clustering;
    }

    public final void setClustering(@NotNull FastClusteringConfig var1) {
        Intrinsics.checkNotNullParameter(var1, "<set-?>");
        this.clustering = var1;
    }

    public final float getMinDurationOn() {
        return this.minDurationOn;
    }

    public final void setMinDurationOn(float var1) {
        this.minDurationOn = var1;
    }

    public final float getMinDurationOff() {
        return this.minDurationOff;
    }

    public final void setMinDurationOff(float var1) {
        this.minDurationOff = var1;
    }

    @NotNull
    public final OfflineSpeakerSegmentationModelConfig component1() {
        return this.segmentation;
    }

    @NotNull
    public final SpeakerEmbeddingExtractorConfig component2() {
        return this.embedding;
    }

    @NotNull
    public final FastClusteringConfig component3() {
        return this.clustering;
    }

    public final float component4() {
        return this.minDurationOn;
    }

    public final float component5() {
        return this.minDurationOff;
    }

    @NotNull
    public final OfflineSpeakerDiarizationConfig copy(@NotNull OfflineSpeakerSegmentationModelConfig segmentation, @NotNull SpeakerEmbeddingExtractorConfig embedding, @NotNull FastClusteringConfig clustering, float minDurationOn, float minDurationOff) {
        Intrinsics.checkNotNullParameter(segmentation, "segmentation");
        Intrinsics.checkNotNullParameter(embedding, "embedding");
        Intrinsics.checkNotNullParameter(clustering, "clustering");
        return new OfflineSpeakerDiarizationConfig(segmentation, embedding, clustering, minDurationOn, minDurationOff);
    }

    // $FF: synthetic method
    public static OfflineSpeakerDiarizationConfig copy$default(OfflineSpeakerDiarizationConfig var0, OfflineSpeakerSegmentationModelConfig var1, SpeakerEmbeddingExtractorConfig var2, FastClusteringConfig var3, float var4, float var5, int var6, Object var7) {
        if ((var6 & 1) != 0) {
            var1 = var0.segmentation;
        }

        if ((var6 & 2) != 0) {
            var2 = var0.embedding;
        }

        if ((var6 & 4) != 0) {
            var3 = var0.clustering;
        }

        if ((var6 & 8) != 0) {
            var4 = var0.minDurationOn;
        }

        if ((var6 & 16) != 0) {
            var5 = var0.minDurationOff;
        }

        return var0.copy(var1, var2, var3, var4, var5);
    }

    @NotNull
    public String toString() {
        return "OfflineSpeakerDiarizationConfig(segmentation=" + this.segmentation + ", embedding=" + this.embedding + ", clustering=" + this.clustering + ", minDurationOn=" + this.minDurationOn + ", minDurationOff=" + this.minDurationOff + ')';
    }

    public int hashCode() {
        int result = this.segmentation.hashCode();
        result = result * 31 + this.embedding.hashCode();
        result = result * 31 + this.clustering.hashCode();
        result = result * 31 + Float.hashCode(this.minDurationOn);
        result = result * 31 + Float.hashCode(this.minDurationOff);
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        } else if (!(other instanceof OfflineSpeakerDiarizationConfig)) {
            return false;
        } else {
            OfflineSpeakerDiarizationConfig var2 = (OfflineSpeakerDiarizationConfig)other;
            if (!Intrinsics.areEqual(this.segmentation, var2.segmentation)) {
                return false;
            } else if (!Intrinsics.areEqual(this.embedding, var2.embedding)) {
                return false;
            } else if (!Intrinsics.areEqual(this.clustering, var2.clustering)) {
                return false;
            } else if (Float.compare(this.minDurationOn, var2.minDurationOn) != 0) {
                return false;
            } else {
                return Float.compare(this.minDurationOff, var2.minDurationOff) == 0;
            }
        }
    }

    public OfflineSpeakerDiarizationConfig() {
        this((OfflineSpeakerSegmentationModelConfig)null, (SpeakerEmbeddingExtractorConfig)null, (FastClusteringConfig)null, 0.0F, 0.0F, 31, (DefaultConstructorMarker)null);
    }
}

