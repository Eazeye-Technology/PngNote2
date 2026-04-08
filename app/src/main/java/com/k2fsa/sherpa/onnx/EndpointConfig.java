package com.k2fsa.sherpa.onnx;

import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class EndpointConfig {
    @NotNull
    private EndpointRule rule1;
    @NotNull
    private EndpointRule rule2;
    @NotNull
    private EndpointRule rule3;

    public EndpointConfig(@NotNull EndpointRule rule1, @NotNull EndpointRule rule2, @NotNull EndpointRule rule3) {
//        Intrinsics.checkNotNullParameter(rule1, "rule1");
//        Intrinsics.checkNotNullParameter(rule2, "rule2");
//        Intrinsics.checkNotNullParameter(rule3, "rule3");
        super();
        this.rule1 = rule1;
        this.rule2 = rule2;
        this.rule3 = rule3;
    }

    // $FF: synthetic method
    public EndpointConfig(EndpointRule var1, EndpointRule var2, EndpointRule var3, int var4, DefaultConstructorMarker var5) {
        this(
                (var4 & 1) != 0 ? new EndpointRule(false, 2.4F, 0.0F) : var1,
                (var4 & 2) != 0 ? new EndpointRule(true, 1.4F, 0.0F) : var2,
                (var4 & 4) != 0 ? new EndpointRule(false, 0.0F, 20.0F) : var3);
    }

    @NotNull
    public final EndpointRule getRule1() {
        return this.rule1;
    }

    public final void setRule1(@NotNull EndpointRule var1) {
        Intrinsics.checkNotNullParameter(var1, "<set-?>");
        this.rule1 = var1;
    }

    @NotNull
    public final EndpointRule getRule2() {
        return this.rule2;
    }

    public final void setRule2(@NotNull EndpointRule var1) {
        Intrinsics.checkNotNullParameter(var1, "<set-?>");
        this.rule2 = var1;
    }

    @NotNull
    public final EndpointRule getRule3() {
        return this.rule3;
    }

    public final void setRule3(@NotNull EndpointRule var1) {
        Intrinsics.checkNotNullParameter(var1, "<set-?>");
        this.rule3 = var1;
    }

    @NotNull
    public final EndpointRule component1() {
        return this.rule1;
    }

    @NotNull
    public final EndpointRule component2() {
        return this.rule2;
    }

    @NotNull
    public final EndpointRule component3() {
        return this.rule3;
    }

    @NotNull
    public final EndpointConfig copy(@NotNull EndpointRule rule1, @NotNull EndpointRule rule2, @NotNull EndpointRule rule3) {
        Intrinsics.checkNotNullParameter(rule1, "rule1");
        Intrinsics.checkNotNullParameter(rule2, "rule2");
        Intrinsics.checkNotNullParameter(rule3, "rule3");
        return new EndpointConfig(rule1, rule2, rule3);
    }

    // $FF: synthetic method
    public static EndpointConfig copy$default(EndpointConfig var0, EndpointRule var1, EndpointRule var2, EndpointRule var3, int var4, Object var5) {
        if ((var4 & 1) != 0) {
            var1 = var0.rule1;
        }

        if ((var4 & 2) != 0) {
            var2 = var0.rule2;
        }

        if ((var4 & 4) != 0) {
            var3 = var0.rule3;
        }

        return var0.copy(var1, var2, var3);
    }

    @NotNull
    public String toString() {
        return "EndpointConfig(rule1=" + this.rule1 + ", rule2=" + this.rule2 + ", rule3=" + this.rule3 + ')';
    }

    public int hashCode() {
        int result = this.rule1.hashCode();
        result = result * 31 + this.rule2.hashCode();
        result = result * 31 + this.rule3.hashCode();
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        } else if (!(other instanceof EndpointConfig)) {
            return false;
        } else {
            EndpointConfig var2 = (EndpointConfig)other;
            if (!Intrinsics.areEqual(this.rule1, var2.rule1)) {
                return false;
            } else if (!Intrinsics.areEqual(this.rule2, var2.rule2)) {
                return false;
            } else {
                return Intrinsics.areEqual(this.rule3, var2.rule3);
            }
        }
    }

    public EndpointConfig() {
        this((EndpointRule)null, (EndpointRule)null, (EndpointRule)null, 7, (DefaultConstructorMarker)null);
    }
}
