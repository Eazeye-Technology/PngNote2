// FastClusteringConfig.java
package com.k2fsa.sherpa.onnx;

import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class FastClusteringConfig {
    private int numClusters;
    private float threshold;
    public static final int $stable = 8;

    public FastClusteringConfig(int numClusters, float threshold) {
        this.numClusters = numClusters;
        this.threshold = threshold;
    }

    // $FF: synthetic method
    public FastClusteringConfig(int var1, float var2, int var3, DefaultConstructorMarker var4) {
        this((var3 & 1) != 0 ? -1 : var1, (var3 & 2) != 0 ? 0.5F : var2);
    }

    public final int getNumClusters() {
        return this.numClusters;
    }

    public final void setNumClusters(int var1) {
        this.numClusters = var1;
    }

    public final float getThreshold() {
        return this.threshold;
    }

    public final void setThreshold(float var1) {
        this.threshold = var1;
    }

    public final int component1() {
        return this.numClusters;
    }

    public final float component2() {
        return this.threshold;
    }

    @NotNull
    public final FastClusteringConfig copy(int numClusters, float threshold) {
        return new FastClusteringConfig(numClusters, threshold);
    }

    // $FF: synthetic method
    public static FastClusteringConfig copy$default(FastClusteringConfig var0, int var1, float var2, int var3, Object var4) {
        if ((var3 & 1) != 0) {
            var1 = var0.numClusters;
        }

        if ((var3 & 2) != 0) {
            var2 = var0.threshold;
        }

        return var0.copy(var1, var2);
    }

    @NotNull
    public String toString() {
        return "FastClusteringConfig(numClusters=" + this.numClusters + ", threshold=" + this.threshold + ')';
    }

    public int hashCode() {
        int result = Integer.hashCode(this.numClusters);
        result = result * 31 + Float.hashCode(this.threshold);
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        } else if (!(other instanceof FastClusteringConfig)) {
            return false;
        } else {
            FastClusteringConfig var2 = (FastClusteringConfig)other;
            if (this.numClusters != var2.numClusters) {
                return false;
            } else {
                return Float.compare(this.threshold, var2.threshold) == 0;
            }
        }
    }

    public FastClusteringConfig() {
        this(0, 0.0F, 3, (DefaultConstructorMarker)null);
    }
}
