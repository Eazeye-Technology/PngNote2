// OfflineSpeakerDiarizationSegment.java
package com.k2fsa.sherpa.onnx;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class OfflineSpeakerDiarizationSegment {
    private final float start;
    private final float end;
    private final int speaker;
//    public static final int $stable;

    public OfflineSpeakerDiarizationSegment(float start, float end, int speaker) {
        this.start = start;
        this.end = end;
        this.speaker = speaker;
    }

    public final float getStart() {
        return this.start;
    }

    public final float getEnd() {
        return this.end;
    }

    public final int getSpeaker() {
        return this.speaker;
    }

    public final float component1() {
        return this.start;
    }

    public final float component2() {
        return this.end;
    }

    public final int component3() {
        return this.speaker;
    }

    @NotNull
    public final OfflineSpeakerDiarizationSegment copy(float start, float end, int speaker) {
        return new OfflineSpeakerDiarizationSegment(start, end, speaker);
    }

    // $FF: synthetic method
    public static OfflineSpeakerDiarizationSegment copy$default(OfflineSpeakerDiarizationSegment var0, float var1, float var2, int var3, int var4, Object var5) {
        if ((var4 & 1) != 0) {
            var1 = var0.start;
        }

        if ((var4 & 2) != 0) {
            var2 = var0.end;
        }

        if ((var4 & 4) != 0) {
            var3 = var0.speaker;
        }

        return var0.copy(var1, var2, var3);
    }

    @NotNull
    public String toString() {
        return "OfflineSpeakerDiarizationSegment(start=" + this.start + ", end=" + this.end + ", speaker=" + this.speaker + ')';
    }

    public int hashCode() {
        int result = Float.hashCode(this.start);
        result = result * 31 + Float.hashCode(this.end);
        result = result * 31 + Integer.hashCode(this.speaker);
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        } else if (!(other instanceof OfflineSpeakerDiarizationSegment)) {
            return false;
        } else {
            OfflineSpeakerDiarizationSegment var2 = (OfflineSpeakerDiarizationSegment)other;
            if (Float.compare(this.start, var2.start) != 0) {
                return false;
            } else if (Float.compare(this.end, var2.end) != 0) {
                return false;
            } else {
                return this.speaker == var2.speaker;
            }
        }
    }
}
