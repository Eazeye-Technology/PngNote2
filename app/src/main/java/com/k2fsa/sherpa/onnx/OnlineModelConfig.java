package com.k2fsa.sherpa.onnx;

import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class OnlineModelConfig {
    @NotNull
    private OnlineTransducerModelConfig transducer;
    @NotNull
    private OnlineParaformerModelConfig paraformer;
    @NotNull
    private OnlineZipformer2CtcModelConfig zipformer2Ctc;
    @NotNull
    private OnlineNeMoCtcModelConfig neMoCtc;
    @NotNull
    private OnlineToneCtcModelConfig toneCtc;
    @NotNull
    private String tokens;
    private int numThreads;
    private boolean debug;
    @NotNull
    private String provider;
    @NotNull
    private String modelType;
    @NotNull
    private String modelingUnit;
    @NotNull
    private String bpeVocab;

    public OnlineModelConfig(@NotNull OnlineTransducerModelConfig transducer, @NotNull OnlineParaformerModelConfig paraformer, @NotNull OnlineZipformer2CtcModelConfig zipformer2Ctc, @NotNull OnlineNeMoCtcModelConfig neMoCtc, @NotNull OnlineToneCtcModelConfig toneCtc, @NotNull String tokens, int numThreads, boolean debug, @NotNull String provider, @NotNull String modelType, @NotNull String modelingUnit, @NotNull String bpeVocab) {
//        Intrinsics.checkNotNullParameter(transducer, "transducer");
//        Intrinsics.checkNotNullParameter(paraformer, "paraformer");
//        Intrinsics.checkNotNullParameter(zipformer2Ctc, "zipformer2Ctc");
//        Intrinsics.checkNotNullParameter(neMoCtc, "neMoCtc");
//        Intrinsics.checkNotNullParameter(toneCtc, "toneCtc");
//        Intrinsics.checkNotNullParameter(tokens, "tokens");
//        Intrinsics.checkNotNullParameter(provider, "provider");
//        Intrinsics.checkNotNullParameter(modelType, "modelType");
//        Intrinsics.checkNotNullParameter(modelingUnit, "modelingUnit");
//        Intrinsics.checkNotNullParameter(bpeVocab, "bpeVocab");
        super();
        this.transducer = transducer;
        this.paraformer = paraformer;
        this.zipformer2Ctc = zipformer2Ctc;
        this.neMoCtc = neMoCtc;
        this.toneCtc = toneCtc;
        this.tokens = tokens;
        this.numThreads = numThreads;
        this.debug = debug;
        this.provider = provider;
        this.modelType = modelType;
        this.modelingUnit = modelingUnit;
        this.bpeVocab = bpeVocab;
    }

    // $FF: synthetic method
    public OnlineModelConfig(OnlineTransducerModelConfig var1, OnlineParaformerModelConfig var2, OnlineZipformer2CtcModelConfig var3, OnlineNeMoCtcModelConfig var4, OnlineToneCtcModelConfig var5, String var6, int var7, boolean var8, String var9, String var10, String var11, String var12, int var13, DefaultConstructorMarker var14) {
        this((var13 & 1) != 0 ? new OnlineTransducerModelConfig((String)null, (String)null, (String)null, 7, (DefaultConstructorMarker)null) : var1,
                (var13 & 2) != 0 ? new OnlineParaformerModelConfig((String)null, (String)null, 3, (DefaultConstructorMarker)null) : var2,
                (var13 & 4) != 0 ? new OnlineZipformer2CtcModelConfig((String)null, 1, (DefaultConstructorMarker)null) : var3,
                (var13 & 8) != 0 ? new OnlineNeMoCtcModelConfig((String)null, 1, (DefaultConstructorMarker)null) : var4,
                (var13 & 16) != 0 ? new OnlineToneCtcModelConfig((String)null, 1, (DefaultConstructorMarker)null) : var5,
                (var13 & 32) != 0 ? "" : var6,
                (var13 & 64) != 0 ? 1 : var7,
                (var13 & 128) != 0 ? false : var8,
                (var13 & 256) != 0 ? "cpu" : var9,
                (var13 & 512) != 0 ? "" : var10,
                (var13 & 1024) != 0 ? "" : var11,
                (var13 & 2048) != 0 ? "" : var12);
    }

    @NotNull
    public final OnlineTransducerModelConfig getTransducer() {
        return this.transducer;
    }

    public final void setTransducer(@NotNull OnlineTransducerModelConfig var1) {
        Intrinsics.checkNotNullParameter(var1, "<set-?>");
        this.transducer = var1;
    }

    @NotNull
    public final OnlineParaformerModelConfig getParaformer() {
        return this.paraformer;
    }

    public final void setParaformer(@NotNull OnlineParaformerModelConfig var1) {
        Intrinsics.checkNotNullParameter(var1, "<set-?>");
        this.paraformer = var1;
    }

    @NotNull
    public final OnlineZipformer2CtcModelConfig getZipformer2Ctc() {
        return this.zipformer2Ctc;
    }

    public final void setZipformer2Ctc(@NotNull OnlineZipformer2CtcModelConfig var1) {
        Intrinsics.checkNotNullParameter(var1, "<set-?>");
        this.zipformer2Ctc = var1;
    }

    @NotNull
    public final OnlineNeMoCtcModelConfig getNeMoCtc() {
        return this.neMoCtc;
    }

    public final void setNeMoCtc(@NotNull OnlineNeMoCtcModelConfig var1) {
        Intrinsics.checkNotNullParameter(var1, "<set-?>");
        this.neMoCtc = var1;
    }

    @NotNull
    public final OnlineToneCtcModelConfig getToneCtc() {
        return this.toneCtc;
    }

    public final void setToneCtc(@NotNull OnlineToneCtcModelConfig var1) {
        Intrinsics.checkNotNullParameter(var1, "<set-?>");
        this.toneCtc = var1;
    }

    @NotNull
    public final String getTokens() {
        return this.tokens;
    }

    public final void setTokens(@NotNull String var1) {
        Intrinsics.checkNotNullParameter(var1, "<set-?>");
        this.tokens = var1;
    }

    public final int getNumThreads() {
        return this.numThreads;
    }

    public final void setNumThreads(int var1) {
        this.numThreads = var1;
    }

    public final boolean getDebug() {
        return this.debug;
    }

    public final void setDebug(boolean var1) {
        this.debug = var1;
    }

    @NotNull
    public final String getProvider() {
        return this.provider;
    }

    public final void setProvider(@NotNull String var1) {
        Intrinsics.checkNotNullParameter(var1, "<set-?>");
        this.provider = var1;
    }

    @NotNull
    public final String getModelType() {
        return this.modelType;
    }

    public final void setModelType(@NotNull String var1) {
        Intrinsics.checkNotNullParameter(var1, "<set-?>");
        this.modelType = var1;
    }

    @NotNull
    public final String getModelingUnit() {
        return this.modelingUnit;
    }

    public final void setModelingUnit(@NotNull String var1) {
        Intrinsics.checkNotNullParameter(var1, "<set-?>");
        this.modelingUnit = var1;
    }

    @NotNull
    public final String getBpeVocab() {
        return this.bpeVocab;
    }

    public final void setBpeVocab(@NotNull String var1) {
        Intrinsics.checkNotNullParameter(var1, "<set-?>");
        this.bpeVocab = var1;
    }

    @NotNull
    public final OnlineTransducerModelConfig component1() {
        return this.transducer;
    }

    @NotNull
    public final OnlineParaformerModelConfig component2() {
        return this.paraformer;
    }

    @NotNull
    public final OnlineZipformer2CtcModelConfig component3() {
        return this.zipformer2Ctc;
    }

    @NotNull
    public final OnlineNeMoCtcModelConfig component4() {
        return this.neMoCtc;
    }

    @NotNull
    public final OnlineToneCtcModelConfig component5() {
        return this.toneCtc;
    }

    @NotNull
    public final String component6() {
        return this.tokens;
    }

    public final int component7() {
        return this.numThreads;
    }

    public final boolean component8() {
        return this.debug;
    }

    @NotNull
    public final String component9() {
        return this.provider;
    }

    @NotNull
    public final String component10() {
        return this.modelType;
    }

    @NotNull
    public final String component11() {
        return this.modelingUnit;
    }

    @NotNull
    public final String component12() {
        return this.bpeVocab;
    }

    @NotNull
    public final OnlineModelConfig copy(@NotNull OnlineTransducerModelConfig transducer, @NotNull OnlineParaformerModelConfig paraformer, @NotNull OnlineZipformer2CtcModelConfig zipformer2Ctc, @NotNull OnlineNeMoCtcModelConfig neMoCtc, @NotNull OnlineToneCtcModelConfig toneCtc, @NotNull String tokens, int numThreads, boolean debug, @NotNull String provider, @NotNull String modelType, @NotNull String modelingUnit, @NotNull String bpeVocab) {
        Intrinsics.checkNotNullParameter(transducer, "transducer");
        Intrinsics.checkNotNullParameter(paraformer, "paraformer");
        Intrinsics.checkNotNullParameter(zipformer2Ctc, "zipformer2Ctc");
        Intrinsics.checkNotNullParameter(neMoCtc, "neMoCtc");
        Intrinsics.checkNotNullParameter(toneCtc, "toneCtc");
        Intrinsics.checkNotNullParameter(tokens, "tokens");
        Intrinsics.checkNotNullParameter(provider, "provider");
        Intrinsics.checkNotNullParameter(modelType, "modelType");
        Intrinsics.checkNotNullParameter(modelingUnit, "modelingUnit");
        Intrinsics.checkNotNullParameter(bpeVocab, "bpeVocab");
        return new OnlineModelConfig(transducer, paraformer, zipformer2Ctc, neMoCtc, toneCtc, tokens, numThreads, debug, provider, modelType, modelingUnit, bpeVocab);
    }

    // $FF: synthetic method
    public static OnlineModelConfig copy$default(OnlineModelConfig var0, OnlineTransducerModelConfig var1, OnlineParaformerModelConfig var2, OnlineZipformer2CtcModelConfig var3, OnlineNeMoCtcModelConfig var4, OnlineToneCtcModelConfig var5, String var6, int var7, boolean var8, String var9, String var10, String var11, String var12, int var13, Object var14) {
        if ((var13 & 1) != 0) {
            var1 = var0.transducer;
        }

        if ((var13 & 2) != 0) {
            var2 = var0.paraformer;
        }

        if ((var13 & 4) != 0) {
            var3 = var0.zipformer2Ctc;
        }

        if ((var13 & 8) != 0) {
            var4 = var0.neMoCtc;
        }

        if ((var13 & 16) != 0) {
            var5 = var0.toneCtc;
        }

        if ((var13 & 32) != 0) {
            var6 = var0.tokens;
        }

        if ((var13 & 64) != 0) {
            var7 = var0.numThreads;
        }

        if ((var13 & 128) != 0) {
            var8 = var0.debug;
        }

        if ((var13 & 256) != 0) {
            var9 = var0.provider;
        }

        if ((var13 & 512) != 0) {
            var10 = var0.modelType;
        }

        if ((var13 & 1024) != 0) {
            var11 = var0.modelingUnit;
        }

        if ((var13 & 2048) != 0) {
            var12 = var0.bpeVocab;
        }

        return var0.copy(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12);
    }

    @NotNull
    public String toString() {
        StringBuilder var1 = new StringBuilder();
        var1.append("OnlineModelConfig(transducer=").append(this.transducer).append(", paraformer=").append(this.paraformer).append(", zipformer2Ctc=").append(this.zipformer2Ctc).append(", neMoCtc=").append(this.neMoCtc).append(", toneCtc=").append(this.toneCtc).append(", tokens=").append(this.tokens).append(", numThreads=").append(this.numThreads).append(", debug=").append(this.debug).append(", provider=").append(this.provider).append(", modelType=").append(this.modelType).append(", modelingUnit=").append(this.modelingUnit).append(", bpeVocab=");
        var1.append(this.bpeVocab).append(')');
        return var1.toString();
    }

    public int hashCode() {
        int result = this.transducer.hashCode();
        result = result * 31 + this.paraformer.hashCode();
        result = result * 31 + this.zipformer2Ctc.hashCode();
        result = result * 31 + this.neMoCtc.hashCode();
        result = result * 31 + this.toneCtc.hashCode();
        result = result * 31 + this.tokens.hashCode();
        result = result * 31 + Integer.hashCode(this.numThreads);
        result = result * 31 + Boolean.hashCode(this.debug);
        result = result * 31 + this.provider.hashCode();
        result = result * 31 + this.modelType.hashCode();
        result = result * 31 + this.modelingUnit.hashCode();
        result = result * 31 + this.bpeVocab.hashCode();
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        } else if (!(other instanceof OnlineModelConfig)) {
            return false;
        } else {
            OnlineModelConfig var2 = (OnlineModelConfig)other;
            if (!Intrinsics.areEqual(this.transducer, var2.transducer)) {
                return false;
            } else if (!Intrinsics.areEqual(this.paraformer, var2.paraformer)) {
                return false;
            } else if (!Intrinsics.areEqual(this.zipformer2Ctc, var2.zipformer2Ctc)) {
                return false;
            } else if (!Intrinsics.areEqual(this.neMoCtc, var2.neMoCtc)) {
                return false;
            } else if (!Intrinsics.areEqual(this.toneCtc, var2.toneCtc)) {
                return false;
            } else if (!Intrinsics.areEqual(this.tokens, var2.tokens)) {
                return false;
            } else if (this.numThreads != var2.numThreads) {
                return false;
            } else if (this.debug != var2.debug) {
                return false;
            } else if (!Intrinsics.areEqual(this.provider, var2.provider)) {
                return false;
            } else if (!Intrinsics.areEqual(this.modelType, var2.modelType)) {
                return false;
            } else if (!Intrinsics.areEqual(this.modelingUnit, var2.modelingUnit)) {
                return false;
            } else {
                return Intrinsics.areEqual(this.bpeVocab, var2.bpeVocab);
            }
        }
    }

    public OnlineModelConfig() {
        this((OnlineTransducerModelConfig)null, (OnlineParaformerModelConfig)null, (OnlineZipformer2CtcModelConfig)null, (OnlineNeMoCtcModelConfig)null, (OnlineToneCtcModelConfig)null, (String)null, 0, false, (String)null, (String)null, (String)null, (String)null, 4095, (DefaultConstructorMarker)null);
    }
}
