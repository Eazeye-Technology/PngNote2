package com.k2fsa.sherpa.onnx;

import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class EndpointRule {
    private boolean mustContainNonSilence;
    private float minTrailingSilence;
    private float minUtteranceLength;

    public EndpointRule(boolean mustContainNonSilence, float minTrailingSilence, float minUtteranceLength) {
        this.mustContainNonSilence = mustContainNonSilence;
        this.minTrailingSilence = minTrailingSilence;
        this.minUtteranceLength = minUtteranceLength;
    }

    public final boolean getMustContainNonSilence() {
        return this.mustContainNonSilence;
    }

    public final void setMustContainNonSilence(boolean var1) {
        this.mustContainNonSilence = var1;
    }

    public final float getMinTrailingSilence() {
        return this.minTrailingSilence;
    }

    public final void setMinTrailingSilence(float var1) {
        this.minTrailingSilence = var1;
    }

    public final float getMinUtteranceLength() {
        return this.minUtteranceLength;
    }

    public final void setMinUtteranceLength(float var1) {
        this.minUtteranceLength = var1;
    }

    public final boolean component1() {
        return this.mustContainNonSilence;
    }

    public final float component2() {
        return this.minTrailingSilence;
    }

    public final float component3() {
        return this.minUtteranceLength;
    }

    @NotNull
    public final EndpointRule copy(boolean mustContainNonSilence, float minTrailingSilence, float minUtteranceLength) {
        return new EndpointRule(mustContainNonSilence, minTrailingSilence, minUtteranceLength);
    }

    // $FF: synthetic method
    public static EndpointRule copy$default(EndpointRule var0, boolean var1, float var2, float var3, int var4, Object var5) {
        if ((var4 & 1) != 0) {
            var1 = var0.mustContainNonSilence;
        }

        if ((var4 & 2) != 0) {
            var2 = var0.minTrailingSilence;
        }

        if ((var4 & 4) != 0) {
            var3 = var0.minUtteranceLength;
        }

        return var0.copy(var1, var2, var3);
    }

    @NotNull
    public String toString() {
        return "EndpointRule(mustContainNonSilence=" + this.mustContainNonSilence + ", minTrailingSilence=" + this.minTrailingSilence + ", minUtteranceLength=" + this.minUtteranceLength + ')';
    }

    public int hashCode() {
        int result = Boolean.hashCode(this.mustContainNonSilence);
        result = result * 31 + Float.hashCode(this.minTrailingSilence);
        result = result * 31 + Float.hashCode(this.minUtteranceLength);
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        } else if (!(other instanceof EndpointRule)) {
            return false;
        } else {
            EndpointRule var2 = (EndpointRule)other;
            if (this.mustContainNonSilence != var2.mustContainNonSilence) {
                return false;
            } else if (Float.compare(this.minTrailingSilence, var2.minTrailingSilence) != 0) {
                return false;
            } else {
                return Float.compare(this.minUtteranceLength, var2.minUtteranceLength) == 0;
            }
        }
    }
}
