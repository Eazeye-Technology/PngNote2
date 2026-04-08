package com.k2fsa.sherpa.onnx;

import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class OnlineParaformerModelConfig {
    @NotNull
    private String encoder;
    @NotNull
    private String decoder;

    public OnlineParaformerModelConfig(@NotNull String encoder, @NotNull String decoder) {
//        Intrinsics.checkNotNullParameter(encoder, "encoder");
//        Intrinsics.checkNotNullParameter(decoder, "decoder");
        super();
        this.encoder = encoder;
        this.decoder = decoder;
    }

    // $FF: synthetic method
    public OnlineParaformerModelConfig(String var1, String var2, int var3, DefaultConstructorMarker var4) {
        this((var3 & 1) != 0 ? "" : var1, (var3 & 2) != 0 ? "" : var2);
    }

    @NotNull
    public final String getEncoder() {
        return this.encoder;
    }

    public final void setEncoder(@NotNull String var1) {
        Intrinsics.checkNotNullParameter(var1, "<set-?>");
        this.encoder = var1;
    }

    @NotNull
    public final String getDecoder() {
        return this.decoder;
    }

    public final void setDecoder(@NotNull String var1) {
        Intrinsics.checkNotNullParameter(var1, "<set-?>");
        this.decoder = var1;
    }

    @NotNull
    public final String component1() {
        return this.encoder;
    }

    @NotNull
    public final String component2() {
        return this.decoder;
    }

    @NotNull
    public final OnlineParaformerModelConfig copy(@NotNull String encoder, @NotNull String decoder) {
        Intrinsics.checkNotNullParameter(encoder, "encoder");
        Intrinsics.checkNotNullParameter(decoder, "decoder");
        return new OnlineParaformerModelConfig(encoder, decoder);
    }

    // $FF: synthetic method
    public static OnlineParaformerModelConfig copy$default(OnlineParaformerModelConfig var0, String var1, String var2, int var3, Object var4) {
        if ((var3 & 1) != 0) {
            var1 = var0.encoder;
        }

        if ((var3 & 2) != 0) {
            var2 = var0.decoder;
        }

        return var0.copy(var1, var2);
    }

    @NotNull
    public String toString() {
        return "OnlineParaformerModelConfig(encoder=" + this.encoder + ", decoder=" + this.decoder + ')';
    }

    public int hashCode() {
        int result = this.encoder.hashCode();
        result = result * 31 + this.decoder.hashCode();
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        } else if (!(other instanceof OnlineParaformerModelConfig)) {
            return false;
        } else {
            OnlineParaformerModelConfig var2 = (OnlineParaformerModelConfig)other;
            if (!Intrinsics.areEqual(this.encoder, var2.encoder)) {
                return false;
            } else {
                return Intrinsics.areEqual(this.decoder, var2.decoder);
            }
        }
    }

    public OnlineParaformerModelConfig() {
        this((String)null, (String)null, 3, (DefaultConstructorMarker)null);
    }
}
