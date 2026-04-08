package com.k2fsa.sherpa.onnx;

import java.util.Arrays;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class OnlineRecognizerResult {
    @NotNull
    private final String text;
    @NotNull
    private final String[] tokens;
    @NotNull
    private final float[] timestamps;
    @NotNull
    private final float[] ysProbs;

    public OnlineRecognizerResult(@NotNull String text, @NotNull String[] tokens, @NotNull float[] timestamps, @NotNull float[] ysProbs) {
//        Intrinsics.checkNotNullParameter(text, "text");
//        Intrinsics.checkNotNullParameter(tokens, "tokens");
//        Intrinsics.checkNotNullParameter(timestamps, "timestamps");
//        Intrinsics.checkNotNullParameter(ysProbs, "ysProbs");
        super();
        this.text = text;
        this.tokens = tokens;
        this.timestamps = timestamps;
        this.ysProbs = ysProbs;
    }

    @NotNull
    public final String getText() {
        return this.text;
    }

    @NotNull
    public final String[] getTokens() {
        return this.tokens;
    }

    @NotNull
    public final float[] getTimestamps() {
        return this.timestamps;
    }

    @NotNull
    public final float[] getYsProbs() {
        return this.ysProbs;
    }

    @NotNull
    public final String component1() {
        return this.text;
    }

    @NotNull
    public final String[] component2() {
        return this.tokens;
    }

    @NotNull
    public final float[] component3() {
        return this.timestamps;
    }

    @NotNull
    public final float[] component4() {
        return this.ysProbs;
    }

    @NotNull
    public final OnlineRecognizerResult copy(@NotNull String text, @NotNull String[] tokens, @NotNull float[] timestamps, @NotNull float[] ysProbs) {
        Intrinsics.checkNotNullParameter(text, "text");
        Intrinsics.checkNotNullParameter(tokens, "tokens");
        Intrinsics.checkNotNullParameter(timestamps, "timestamps");
        Intrinsics.checkNotNullParameter(ysProbs, "ysProbs");
        return new OnlineRecognizerResult(text, tokens, timestamps, ysProbs);
    }

    // $FF: synthetic method
    public static OnlineRecognizerResult copy$default(OnlineRecognizerResult var0, String var1, String[] var2, float[] var3, float[] var4, int var5, Object var6) {
        if ((var5 & 1) != 0) {
            var1 = var0.text;
        }

        if ((var5 & 2) != 0) {
            var2 = var0.tokens;
        }

        if ((var5 & 4) != 0) {
            var3 = var0.timestamps;
        }

        if ((var5 & 8) != 0) {
            var4 = var0.ysProbs;
        }

        return var0.copy(var1, var2, var3, var4);
    }

    @NotNull
    public String toString() {
        return "OnlineRecognizerResult(text=" + this.text + ", tokens=" + Arrays.toString(this.tokens) + ", timestamps=" + Arrays.toString(this.timestamps) + ", ysProbs=" + Arrays.toString(this.ysProbs) + ')';
    }

    public int hashCode() {
        int result = this.text.hashCode();
        result = result * 31 + Arrays.hashCode(this.tokens);
        result = result * 31 + Arrays.hashCode(this.timestamps);
        result = result * 31 + Arrays.hashCode(this.ysProbs);
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        } else if (!(other instanceof OnlineRecognizerResult)) {
            return false;
        } else {
            OnlineRecognizerResult var2 = (OnlineRecognizerResult)other;
            if (!Intrinsics.areEqual(this.text, var2.text)) {
                return false;
            } else if (!Intrinsics.areEqual(this.tokens, var2.tokens)) {
                return false;
            } else if (!Intrinsics.areEqual(this.timestamps, var2.timestamps)) {
                return false;
            } else {
                return Intrinsics.areEqual(this.ysProbs, var2.ysProbs);
            }
        }
    }
}
