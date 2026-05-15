// OfflineSpeakerSegmentationModelConfig.java
package com.k2fsa.sherpa.onnx;

import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class OfflineSpeakerSegmentationModelConfig {
    @NotNull
    private OfflineSpeakerSegmentationPyannoteModelConfig pyannote;
    private int numThreads;
    private boolean debug;
    @NotNull
    private String provider;
    public static final int $stable = 8;

    public OfflineSpeakerSegmentationModelConfig(@NotNull OfflineSpeakerSegmentationPyannoteModelConfig pyannote, int numThreads, boolean debug, @NotNull String provider) {
//        Intrinsics.checkNotNullParameter(pyannote, "pyannote");
//        Intrinsics.checkNotNullParameter(provider, "provider");
        super();
        this.pyannote = pyannote;
        this.numThreads = numThreads;
        this.debug = debug;
        this.provider = provider;
    }

    // $FF: synthetic method
    public OfflineSpeakerSegmentationModelConfig(OfflineSpeakerSegmentationPyannoteModelConfig var1, int var2, boolean var3, String var4, int var5, DefaultConstructorMarker var6) {
        this((var5 & 1) != 0 ? new OfflineSpeakerSegmentationPyannoteModelConfig((String)null, 1, (DefaultConstructorMarker)null) : var1,
                (var5 & 2) != 0 ? 1 : var2,
                (var5 & 4) != 0 ? false : var3,
                (var5 & 8) != 0 ? "cpu" : var4);
    }

    @NotNull
    public final OfflineSpeakerSegmentationPyannoteModelConfig getPyannote() {
        return this.pyannote;
    }

    public final void setPyannote(@NotNull OfflineSpeakerSegmentationPyannoteModelConfig var1) {
        Intrinsics.checkNotNullParameter(var1, "<set-?>");
        this.pyannote = var1;
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
    public final OfflineSpeakerSegmentationPyannoteModelConfig component1() {
        return this.pyannote;
    }

    public final int component2() {
        return this.numThreads;
    }

    public final boolean component3() {
        return this.debug;
    }

    @NotNull
    public final String component4() {
        return this.provider;
    }

    @NotNull
    public final OfflineSpeakerSegmentationModelConfig copy(@NotNull OfflineSpeakerSegmentationPyannoteModelConfig pyannote, int numThreads, boolean debug, @NotNull String provider) {
        Intrinsics.checkNotNullParameter(pyannote, "pyannote");
        Intrinsics.checkNotNullParameter(provider, "provider");
        return new OfflineSpeakerSegmentationModelConfig(pyannote, numThreads, debug, provider);
    }

    // $FF: synthetic method
    public static OfflineSpeakerSegmentationModelConfig copy$default(OfflineSpeakerSegmentationModelConfig var0, OfflineSpeakerSegmentationPyannoteModelConfig var1, int var2, boolean var3, String var4, int var5, Object var6) {
        if ((var5 & 1) != 0) {
            var1 = var0.pyannote;
        }

        if ((var5 & 2) != 0) {
            var2 = var0.numThreads;
        }

        if ((var5 & 4) != 0) {
            var3 = var0.debug;
        }

        if ((var5 & 8) != 0) {
            var4 = var0.provider;
        }

        return var0.copy(var1, var2, var3, var4);
    }

    @NotNull
    public String toString() {
        return "OfflineSpeakerSegmentationModelConfig(pyannote=" + this.pyannote + ", numThreads=" + this.numThreads + ", debug=" + this.debug + ", provider=" + this.provider + ')';
    }

    public int hashCode() {
        int result = this.pyannote.hashCode();
        result = result * 31 + Integer.hashCode(this.numThreads);
        result = result * 31 + Boolean.hashCode(this.debug);
        result = result * 31 + this.provider.hashCode();
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        } else if (!(other instanceof OfflineSpeakerSegmentationModelConfig)) {
            return false;
        } else {
            OfflineSpeakerSegmentationModelConfig var2 = (OfflineSpeakerSegmentationModelConfig)other;
            if (!Intrinsics.areEqual(this.pyannote, var2.pyannote)) {
                return false;
            } else if (this.numThreads != var2.numThreads) {
                return false;
            } else if (this.debug != var2.debug) {
                return false;
            } else {
                return Intrinsics.areEqual(this.provider, var2.provider);
            }
        }
    }

    public OfflineSpeakerSegmentationModelConfig() {
        this((OfflineSpeakerSegmentationPyannoteModelConfig)null, 0, false, (String)null, 15, (DefaultConstructorMarker)null);
    }
}
