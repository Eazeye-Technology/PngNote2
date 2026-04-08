package com.k2fsa.sherpa.onnx;

import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class OnlineRecognizerConfig {
    @NotNull
    private FeatureConfig featConfig;
    @NotNull
    private OnlineModelConfig modelConfig;
    @NotNull
    private OnlineLMConfig lmConfig;
    @NotNull
    private OnlineCtcFstDecoderConfig ctcFstDecoderConfig;
    @NotNull
    private HomophoneReplacerConfig hr;
    @NotNull
    private EndpointConfig endpointConfig;
    private boolean enableEndpoint;
    @NotNull
    private String decodingMethod;
    private int maxActivePaths;
    @NotNull
    private String hotwordsFile;
    private float hotwordsScore;
    @NotNull
    private String ruleFsts;
    @NotNull
    private String ruleFars;
    private float blankPenalty;

    public OnlineRecognizerConfig(@NotNull FeatureConfig featConfig, @NotNull OnlineModelConfig modelConfig, @NotNull OnlineLMConfig lmConfig, @NotNull OnlineCtcFstDecoderConfig ctcFstDecoderConfig, @NotNull HomophoneReplacerConfig hr, @NotNull EndpointConfig endpointConfig, boolean enableEndpoint, @NotNull String decodingMethod, int maxActivePaths, @NotNull String hotwordsFile, float hotwordsScore, @NotNull String ruleFsts, @NotNull String ruleFars, float blankPenalty) {
//        Intrinsics.checkNotNullParameter(featConfig, "featConfig");
//        Intrinsics.checkNotNullParameter(modelConfig, "modelConfig");
//        Intrinsics.checkNotNullParameter(lmConfig, "lmConfig");
//        Intrinsics.checkNotNullParameter(ctcFstDecoderConfig, "ctcFstDecoderConfig");
//        Intrinsics.checkNotNullParameter(hr, "hr");
//        Intrinsics.checkNotNullParameter(endpointConfig, "endpointConfig");
//        Intrinsics.checkNotNullParameter(decodingMethod, "decodingMethod");
//        Intrinsics.checkNotNullParameter(hotwordsFile, "hotwordsFile");
//        Intrinsics.checkNotNullParameter(ruleFsts, "ruleFsts");
//        Intrinsics.checkNotNullParameter(ruleFars, "ruleFars");
        super();
        this.featConfig = featConfig;
        this.modelConfig = modelConfig;
        this.lmConfig = lmConfig;
        this.ctcFstDecoderConfig = ctcFstDecoderConfig;
        this.hr = hr;
        this.endpointConfig = endpointConfig;
        this.enableEndpoint = enableEndpoint;
        this.decodingMethod = decodingMethod;
        this.maxActivePaths = maxActivePaths;
        this.hotwordsFile = hotwordsFile;
        this.hotwordsScore = hotwordsScore;
        this.ruleFsts = ruleFsts;
        this.ruleFars = ruleFars;
        this.blankPenalty = blankPenalty;
    }

    // $FF: synthetic method
    public OnlineRecognizerConfig(FeatureConfig var1, OnlineModelConfig var2, OnlineLMConfig var3, OnlineCtcFstDecoderConfig var4, HomophoneReplacerConfig var5, EndpointConfig var6, boolean var7, String var8, int var9, String var10, float var11, String var12, String var13, float var14, int var15, DefaultConstructorMarker var16) {
        this((var15 & 1) != 0 ? new FeatureConfig(0, 0, 0.0F, 7, (DefaultConstructorMarker)null) : var1,
                (var15 & 2) != 0 ? new OnlineModelConfig((OnlineTransducerModelConfig)null, (OnlineParaformerModelConfig)null, (OnlineZipformer2CtcModelConfig)null, (OnlineNeMoCtcModelConfig)null, (OnlineToneCtcModelConfig)null, (String)null, 0, false, (String)null, (String)null, (String)null, (String)null, 4095, (DefaultConstructorMarker)null) : var2,
                (var15 & 4) != 0 ? new OnlineLMConfig((String)null, 0.0F, 3, (DefaultConstructorMarker)null) : var3,
                (var15 & 8) != 0 ? new OnlineCtcFstDecoderConfig((String)null, 0, 3, (DefaultConstructorMarker)null) : var4,
                (var15 & 16) != 0 ? new HomophoneReplacerConfig((String)null, (String)null, (String)null, 7, (DefaultConstructorMarker)null) : var5,
                (var15 & 32) != 0 ? new EndpointConfig((EndpointRule)null, (EndpointRule)null, (EndpointRule)null, 7, (DefaultConstructorMarker)null) : var6,
                (var15 & 64) != 0 ? true : var7,
                (var15 & 128) != 0 ? "greedy_search" : var8,
                (var15 & 256) != 0 ? 4 : var9,
                (var15 & 512) != 0 ? "" : var10,
                (var15 & 1024) != 0 ? 1.5F : var11,
                (var15 & 2048) != 0 ? "" : var12,
                (var15 & 4096) != 0 ? "" : var13,
                (var15 & 8192) != 0 ? 0.0F : var14);
    }

    @NotNull
    public final FeatureConfig getFeatConfig() {
        return this.featConfig;
    }

    public final void setFeatConfig(@NotNull FeatureConfig var1) {
        Intrinsics.checkNotNullParameter(var1, "<set-?>");
        this.featConfig = var1;
    }

    @NotNull
    public final OnlineModelConfig getModelConfig() {
        return this.modelConfig;
    }

    public final void setModelConfig(@NotNull OnlineModelConfig var1) {
        Intrinsics.checkNotNullParameter(var1, "<set-?>");
        this.modelConfig = var1;
    }

    @NotNull
    public final OnlineLMConfig getLmConfig() {
        return this.lmConfig;
    }

    public final void setLmConfig(@NotNull OnlineLMConfig var1) {
        Intrinsics.checkNotNullParameter(var1, "<set-?>");
        this.lmConfig = var1;
    }

    @NotNull
    public final OnlineCtcFstDecoderConfig getCtcFstDecoderConfig() {
        return this.ctcFstDecoderConfig;
    }

    public final void setCtcFstDecoderConfig(@NotNull OnlineCtcFstDecoderConfig var1) {
        Intrinsics.checkNotNullParameter(var1, "<set-?>");
        this.ctcFstDecoderConfig = var1;
    }

    @NotNull
    public final HomophoneReplacerConfig getHr() {
        return this.hr;
    }

    public final void setHr(@NotNull HomophoneReplacerConfig var1) {
        Intrinsics.checkNotNullParameter(var1, "<set-?>");
        this.hr = var1;
    }

    @NotNull
    public final EndpointConfig getEndpointConfig() {
        return this.endpointConfig;
    }

    public final void setEndpointConfig(@NotNull EndpointConfig var1) {
        Intrinsics.checkNotNullParameter(var1, "<set-?>");
        this.endpointConfig = var1;
    }

    public final boolean getEnableEndpoint() {
        return this.enableEndpoint;
    }

    public final void setEnableEndpoint(boolean var1) {
        this.enableEndpoint = var1;
    }

    @NotNull
    public final String getDecodingMethod() {
        return this.decodingMethod;
    }

    public final void setDecodingMethod(@NotNull String var1) {
        Intrinsics.checkNotNullParameter(var1, "<set-?>");
        this.decodingMethod = var1;
    }

    public final int getMaxActivePaths() {
        return this.maxActivePaths;
    }

    public final void setMaxActivePaths(int var1) {
        this.maxActivePaths = var1;
    }

    @NotNull
    public final String getHotwordsFile() {
        return this.hotwordsFile;
    }

    public final void setHotwordsFile(@NotNull String var1) {
        Intrinsics.checkNotNullParameter(var1, "<set-?>");
        this.hotwordsFile = var1;
    }

    public final float getHotwordsScore() {
        return this.hotwordsScore;
    }

    public final void setHotwordsScore(float var1) {
        this.hotwordsScore = var1;
    }

    @NotNull
    public final String getRuleFsts() {
        return this.ruleFsts;
    }

    public final void setRuleFsts(@NotNull String var1) {
        Intrinsics.checkNotNullParameter(var1, "<set-?>");
        this.ruleFsts = var1;
    }

    @NotNull
    public final String getRuleFars() {
        return this.ruleFars;
    }

    public final void setRuleFars(@NotNull String var1) {
        Intrinsics.checkNotNullParameter(var1, "<set-?>");
        this.ruleFars = var1;
    }

    public final float getBlankPenalty() {
        return this.blankPenalty;
    }

    public final void setBlankPenalty(float var1) {
        this.blankPenalty = var1;
    }

    @NotNull
    public final FeatureConfig component1() {
        return this.featConfig;
    }

    @NotNull
    public final OnlineModelConfig component2() {
        return this.modelConfig;
    }

    @NotNull
    public final OnlineLMConfig component3() {
        return this.lmConfig;
    }

    @NotNull
    public final OnlineCtcFstDecoderConfig component4() {
        return this.ctcFstDecoderConfig;
    }

    @NotNull
    public final HomophoneReplacerConfig component5() {
        return this.hr;
    }

    @NotNull
    public final EndpointConfig component6() {
        return this.endpointConfig;
    }

    public final boolean component7() {
        return this.enableEndpoint;
    }

    @NotNull
    public final String component8() {
        return this.decodingMethod;
    }

    public final int component9() {
        return this.maxActivePaths;
    }

    @NotNull
    public final String component10() {
        return this.hotwordsFile;
    }

    public final float component11() {
        return this.hotwordsScore;
    }

    @NotNull
    public final String component12() {
        return this.ruleFsts;
    }

    @NotNull
    public final String component13() {
        return this.ruleFars;
    }

    public final float component14() {
        return this.blankPenalty;
    }

    @NotNull
    public final OnlineRecognizerConfig copy(@NotNull FeatureConfig featConfig, @NotNull OnlineModelConfig modelConfig, @NotNull OnlineLMConfig lmConfig, @NotNull OnlineCtcFstDecoderConfig ctcFstDecoderConfig, @NotNull HomophoneReplacerConfig hr, @NotNull EndpointConfig endpointConfig, boolean enableEndpoint, @NotNull String decodingMethod, int maxActivePaths, @NotNull String hotwordsFile, float hotwordsScore, @NotNull String ruleFsts, @NotNull String ruleFars, float blankPenalty) {
        Intrinsics.checkNotNullParameter(featConfig, "featConfig");
        Intrinsics.checkNotNullParameter(modelConfig, "modelConfig");
        Intrinsics.checkNotNullParameter(lmConfig, "lmConfig");
        Intrinsics.checkNotNullParameter(ctcFstDecoderConfig, "ctcFstDecoderConfig");
        Intrinsics.checkNotNullParameter(hr, "hr");
        Intrinsics.checkNotNullParameter(endpointConfig, "endpointConfig");
        Intrinsics.checkNotNullParameter(decodingMethod, "decodingMethod");
        Intrinsics.checkNotNullParameter(hotwordsFile, "hotwordsFile");
        Intrinsics.checkNotNullParameter(ruleFsts, "ruleFsts");
        Intrinsics.checkNotNullParameter(ruleFars, "ruleFars");
        return new OnlineRecognizerConfig(featConfig, modelConfig, lmConfig, ctcFstDecoderConfig, hr, endpointConfig, enableEndpoint, decodingMethod, maxActivePaths, hotwordsFile, hotwordsScore, ruleFsts, ruleFars, blankPenalty);
    }

    // $FF: synthetic method
    public static OnlineRecognizerConfig copy$default(OnlineRecognizerConfig var0, FeatureConfig var1, OnlineModelConfig var2, OnlineLMConfig var3, OnlineCtcFstDecoderConfig var4, HomophoneReplacerConfig var5, EndpointConfig var6, boolean var7, String var8, int var9, String var10, float var11, String var12, String var13, float var14, int var15, Object var16) {
        if ((var15 & 1) != 0) {
            var1 = var0.featConfig;
        }

        if ((var15 & 2) != 0) {
            var2 = var0.modelConfig;
        }

        if ((var15 & 4) != 0) {
            var3 = var0.lmConfig;
        }

        if ((var15 & 8) != 0) {
            var4 = var0.ctcFstDecoderConfig;
        }

        if ((var15 & 16) != 0) {
            var5 = var0.hr;
        }

        if ((var15 & 32) != 0) {
            var6 = var0.endpointConfig;
        }

        if ((var15 & 64) != 0) {
            var7 = var0.enableEndpoint;
        }

        if ((var15 & 128) != 0) {
            var8 = var0.decodingMethod;
        }

        if ((var15 & 256) != 0) {
            var9 = var0.maxActivePaths;
        }

        if ((var15 & 512) != 0) {
            var10 = var0.hotwordsFile;
        }

        if ((var15 & 1024) != 0) {
            var11 = var0.hotwordsScore;
        }

        if ((var15 & 2048) != 0) {
            var12 = var0.ruleFsts;
        }

        if ((var15 & 4096) != 0) {
            var13 = var0.ruleFars;
        }

        if ((var15 & 8192) != 0) {
            var14 = var0.blankPenalty;
        }

        return var0.copy(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, var14);
    }

    @NotNull
    public String toString() {
        StringBuilder var1 = new StringBuilder();
        var1.append("OnlineRecognizerConfig(featConfig=").append(this.featConfig).append(", modelConfig=").append(this.modelConfig).append(", lmConfig=").append(this.lmConfig).append(", ctcFstDecoderConfig=").append(this.ctcFstDecoderConfig).append(", hr=").append(this.hr).append(", endpointConfig=").append(this.endpointConfig).append(", enableEndpoint=").append(this.enableEndpoint).append(", decodingMethod=").append(this.decodingMethod).append(", maxActivePaths=").append(this.maxActivePaths).append(", hotwordsFile=").append(this.hotwordsFile).append(", hotwordsScore=").append(this.hotwordsScore).append(", ruleFsts=");
        var1.append(this.ruleFsts).append(", ruleFars=").append(this.ruleFars).append(", blankPenalty=").append(this.blankPenalty).append(')');
        return var1.toString();
    }

    public int hashCode() {
        int result = this.featConfig.hashCode();
        result = result * 31 + this.modelConfig.hashCode();
        result = result * 31 + this.lmConfig.hashCode();
        result = result * 31 + this.ctcFstDecoderConfig.hashCode();
        result = result * 31 + this.hr.hashCode();
        result = result * 31 + this.endpointConfig.hashCode();
        result = result * 31 + Boolean.hashCode(this.enableEndpoint);
        result = result * 31 + this.decodingMethod.hashCode();
        result = result * 31 + Integer.hashCode(this.maxActivePaths);
        result = result * 31 + this.hotwordsFile.hashCode();
        result = result * 31 + Float.hashCode(this.hotwordsScore);
        result = result * 31 + this.ruleFsts.hashCode();
        result = result * 31 + this.ruleFars.hashCode();
        result = result * 31 + Float.hashCode(this.blankPenalty);
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        } else if (!(other instanceof OnlineRecognizerConfig)) {
            return false;
        } else {
            OnlineRecognizerConfig var2 = (OnlineRecognizerConfig)other;
            if (!Intrinsics.areEqual(this.featConfig, var2.featConfig)) {
                return false;
            } else if (!Intrinsics.areEqual(this.modelConfig, var2.modelConfig)) {
                return false;
            } else if (!Intrinsics.areEqual(this.lmConfig, var2.lmConfig)) {
                return false;
            } else if (!Intrinsics.areEqual(this.ctcFstDecoderConfig, var2.ctcFstDecoderConfig)) {
                return false;
            } else if (!Intrinsics.areEqual(this.hr, var2.hr)) {
                return false;
            } else if (!Intrinsics.areEqual(this.endpointConfig, var2.endpointConfig)) {
                return false;
            } else if (this.enableEndpoint != var2.enableEndpoint) {
                return false;
            } else if (!Intrinsics.areEqual(this.decodingMethod, var2.decodingMethod)) {
                return false;
            } else if (this.maxActivePaths != var2.maxActivePaths) {
                return false;
            } else if (!Intrinsics.areEqual(this.hotwordsFile, var2.hotwordsFile)) {
                return false;
            } else if (Float.compare(this.hotwordsScore, var2.hotwordsScore) != 0) {
                return false;
            } else if (!Intrinsics.areEqual(this.ruleFsts, var2.ruleFsts)) {
                return false;
            } else if (!Intrinsics.areEqual(this.ruleFars, var2.ruleFars)) {
                return false;
            } else {
                return Float.compare(this.blankPenalty, var2.blankPenalty) == 0;
            }
        }
    }

    public OnlineRecognizerConfig() {
        this((FeatureConfig)null, (OnlineModelConfig)null, (OnlineLMConfig)null, (OnlineCtcFstDecoderConfig)null, (HomophoneReplacerConfig)null, (EndpointConfig)null, false, (String)null, 0, (String)null, 0.0F, (String)null, (String)null, 0.0F, 16383, (DefaultConstructorMarker)null);
    }
}
