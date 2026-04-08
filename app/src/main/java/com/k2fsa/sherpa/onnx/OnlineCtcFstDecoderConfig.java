package com.k2fsa.sherpa.onnx;

import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class OnlineCtcFstDecoderConfig {
    @NotNull
    private String graph;
    private int maxActive;

    public OnlineCtcFstDecoderConfig(@NotNull String graph, int maxActive) {
//        Intrinsics.checkNotNullParameter(graph, "graph");
        super();
        this.graph = graph;
        this.maxActive = maxActive;
    }

    // $FF: synthetic method
    public OnlineCtcFstDecoderConfig(String var1, int var2, int var3, DefaultConstructorMarker var4) {
        this((var3 & 1) != 0 ? "" : var1, (var3 & 2) != 0 ? 3000 : var2);
    }

    @NotNull
    public final String getGraph() {
        return this.graph;
    }

    public final void setGraph(@NotNull String var1) {
        Intrinsics.checkNotNullParameter(var1, "<set-?>");
        this.graph = var1;
    }

    public final int getMaxActive() {
        return this.maxActive;
    }

    public final void setMaxActive(int var1) {
        this.maxActive = var1;
    }

    @NotNull
    public final String component1() {
        return this.graph;
    }

    public final int component2() {
        return this.maxActive;
    }

    @NotNull
    public final OnlineCtcFstDecoderConfig copy(@NotNull String graph, int maxActive) {
        Intrinsics.checkNotNullParameter(graph, "graph");
        return new OnlineCtcFstDecoderConfig(graph, maxActive);
    }

    // $FF: synthetic method
    public static OnlineCtcFstDecoderConfig copy$default(OnlineCtcFstDecoderConfig var0, String var1, int var2, int var3, Object var4) {
        if ((var3 & 1) != 0) {
            var1 = var0.graph;
        }

        if ((var3 & 2) != 0) {
            var2 = var0.maxActive;
        }

        return var0.copy(var1, var2);
    }

    @NotNull
    public String toString() {
        return "OnlineCtcFstDecoderConfig(graph=" + this.graph + ", maxActive=" + this.maxActive + ')';
    }

    public int hashCode() {
        int result = this.graph.hashCode();
        result = result * 31 + Integer.hashCode(this.maxActive);
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        } else if (!(other instanceof OnlineCtcFstDecoderConfig)) {
            return false;
        } else {
            OnlineCtcFstDecoderConfig var2 = (OnlineCtcFstDecoderConfig)other;
            if (!Intrinsics.areEqual(this.graph, var2.graph)) {
                return false;
            } else {
                return this.maxActive == var2.maxActive;
            }
        }
    }

    public OnlineCtcFstDecoderConfig() {
        this((String)null, 0, 3, (DefaultConstructorMarker)null);
    }
}
