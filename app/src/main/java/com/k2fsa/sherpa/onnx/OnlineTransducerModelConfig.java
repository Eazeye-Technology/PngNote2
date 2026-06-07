package com.k2fsa.sherpa.onnx;

import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class OnlineTransducerModelConfig {
    @NotNull
    private String encoder;
    @NotNull
    private String decoder;
    @NotNull
    private String joiner;

    public OnlineTransducerModelConfig(@NotNull String encoder, @NotNull String decoder, @NotNull String joiner) {
//        Intrinsics.checkNotNullParameter(encoder, "encoder");
//        Intrinsics.checkNotNullParameter(decoder, "decoder");
//        Intrinsics.checkNotNullParameter(joiner, "joiner");
        super();
        this.encoder = encoder;
        this.decoder = decoder;
        this.joiner = joiner;
    }

    // $FF: synthetic method
    public OnlineTransducerModelConfig(String var1, String var2, String var3, int var4, DefaultConstructorMarker var5) {
        this((var4 & 1) != 0 ? "" : var1,
                (var4 & 2) != 0 ? "" : var2,
                (var4 & 4) != 0 ? "" : var3);
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
    public final String getJoiner() {
        return this.joiner;
    }

    public final void setJoiner(@NotNull String var1) {
        Intrinsics.checkNotNullParameter(var1, "<set-?>");
        this.joiner = var1;
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
    public final String component3() {
        return this.joiner;
    }

    @NotNull
    public final OnlineTransducerModelConfig copy(@NotNull String encoder, @NotNull String decoder, @NotNull String joiner) {
        Intrinsics.checkNotNullParameter(encoder, "encoder");
        Intrinsics.checkNotNullParameter(decoder, "decoder");
        Intrinsics.checkNotNullParameter(joiner, "joiner");
        return new OnlineTransducerModelConfig(encoder, decoder, joiner);
    }

    // $FF: synthetic method
    public static OnlineTransducerModelConfig copy$default(OnlineTransducerModelConfig var0, String var1, String var2, String var3, int var4, Object var5) {
        if ((var4 & 1) != 0) {
            var1 = var0.encoder;
        }

        if ((var4 & 2) != 0) {
            var2 = var0.decoder;
        }

        if ((var4 & 4) != 0) {
            var3 = var0.joiner;
        }

        return var0.copy(var1, var2, var3);
    }

    @NotNull
    public String toString() {
        return "OnlineTransducerModelConfig(encoder=" + this.encoder + ", decoder=" + this.decoder + ", joiner=" + this.joiner + ')';
    }

    public int hashCode() {
        int result = this.encoder.hashCode();
        result = result * 31 + this.decoder.hashCode();
        result = result * 31 + this.joiner.hashCode();
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        } else if (!(other instanceof OnlineTransducerModelConfig)) {
            return false;
        } else {
            OnlineTransducerModelConfig var2 = (OnlineTransducerModelConfig)other;
            if (!Intrinsics.areEqual(this.encoder, var2.encoder)) {
                return false;
            } else if (!Intrinsics.areEqual(this.decoder, var2.decoder)) {
                return false;
            } else {
                return Intrinsics.areEqual(this.joiner, var2.joiner);
            }
        }
    }

    public OnlineTransducerModelConfig() {
        this((String)null, (String)null, (String)null, 7, (DefaultConstructorMarker)null);
    }
}
