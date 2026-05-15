// OfflineSpeakerSegmentationPyannoteModelConfig.java
package com.k2fsa.sherpa.onnx;

import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class OfflineSpeakerSegmentationPyannoteModelConfig {
    @NotNull
    private String model;
    public static final int $stable = 8;

    public OfflineSpeakerSegmentationPyannoteModelConfig(@NotNull String model) {
        //Intrinsics.checkNotNullParameter(model, "model");
        super();
        this.model = model;
    }

    // $FF: synthetic method
    public OfflineSpeakerSegmentationPyannoteModelConfig(String var1, int var2, DefaultConstructorMarker var3) {
        this((var2 & 1) != 0 ? "" :var1);
    }

    @NotNull
    public final String getModel() {
        return this.model;
    }

    public final void setModel(@NotNull String var1) {
        Intrinsics.checkNotNullParameter(var1, "<set-?>");
        this.model = var1;
    }

    @NotNull
    public final String component1() {
        return this.model;
    }

    @NotNull
    public final OfflineSpeakerSegmentationPyannoteModelConfig copy(@NotNull String model) {
        Intrinsics.checkNotNullParameter(model, "model");
        return new OfflineSpeakerSegmentationPyannoteModelConfig(model);
    }

    // $FF: synthetic method
    public static OfflineSpeakerSegmentationPyannoteModelConfig copy$default(OfflineSpeakerSegmentationPyannoteModelConfig var0, String var1, int var2, Object var3) {
        if ((var2 & 1) != 0) {
            var1 = var0.model;
        }

        return var0.copy(var1);
    }

    @NotNull
    public String toString() {
        return "OfflineSpeakerSegmentationPyannoteModelConfig(model=" + this.model + ')';
    }

    public int hashCode() {
        return this.model.hashCode();
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        } else if (!(other instanceof OfflineSpeakerSegmentationPyannoteModelConfig)) {
            return false;
        } else {
            OfflineSpeakerSegmentationPyannoteModelConfig var2 = (OfflineSpeakerSegmentationPyannoteModelConfig)other;
            return Intrinsics.areEqual(this.model, var2.model);
        }
    }

    public OfflineSpeakerSegmentationPyannoteModelConfig() {
        this((String)null, 1, (DefaultConstructorMarker)null);
    }
}




