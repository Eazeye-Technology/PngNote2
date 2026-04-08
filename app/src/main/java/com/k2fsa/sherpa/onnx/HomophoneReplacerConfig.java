package com.k2fsa.sherpa.onnx;

import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class HomophoneReplacerConfig {
    @NotNull
    private String dictDir;
    @NotNull
    private String lexicon;
    @NotNull
    private String ruleFsts;

    public HomophoneReplacerConfig(@NotNull String dictDir, @NotNull String lexicon, @NotNull String ruleFsts) {
//        Intrinsics.checkNotNullParameter(dictDir, "dictDir");
//        Intrinsics.checkNotNullParameter(lexicon, "lexicon");
//        Intrinsics.checkNotNullParameter(ruleFsts, "ruleFsts");
        super();
        this.dictDir = dictDir;
        this.lexicon = lexicon;
        this.ruleFsts = ruleFsts;
    }

    // $FF: synthetic method
    public HomophoneReplacerConfig(String var1, String var2, String var3, int var4, DefaultConstructorMarker var5) {
        this((var4 & 1) != 0 ? "" : var1, (var4 & 2) != 0 ? "" : var2, (var4 & 4) != 0 ? "" : var3);
    }

    @NotNull
    public final String getDictDir() {
        return this.dictDir;
    }

    public final void setDictDir(@NotNull String var1) {
        Intrinsics.checkNotNullParameter(var1, "<set-?>");
        this.dictDir = var1;
    }

    @NotNull
    public final String getLexicon() {
        return this.lexicon;
    }

    public final void setLexicon(@NotNull String var1) {
        Intrinsics.checkNotNullParameter(var1, "<set-?>");
        this.lexicon = var1;
    }

    @NotNull
    public final String getRuleFsts() {
        return this.ruleFsts;
    }

    public final void setRuleFsts(@NotNull String var1) {
        Intrinsics.checkNotNullParameter(var1, "<set-?>");
        this.ruleFsts = var1;
    }

    @NotNull
    public final String component1() {
        return this.dictDir;
    }

    @NotNull
    public final String component2() {
        return this.lexicon;
    }

    @NotNull
    public final String component3() {
        return this.ruleFsts;
    }

    @NotNull
    public final HomophoneReplacerConfig copy(@NotNull String dictDir, @NotNull String lexicon, @NotNull String ruleFsts) {
        Intrinsics.checkNotNullParameter(dictDir, "dictDir");
        Intrinsics.checkNotNullParameter(lexicon, "lexicon");
        Intrinsics.checkNotNullParameter(ruleFsts, "ruleFsts");
        return new HomophoneReplacerConfig(dictDir, lexicon, ruleFsts);
    }

    // $FF: synthetic method
    public static HomophoneReplacerConfig copy$default(HomophoneReplacerConfig var0, String var1, String var2, String var3, int var4, Object var5) {
        if ((var4 & 1) != 0) {
            var1 = var0.dictDir;
        }

        if ((var4 & 2) != 0) {
            var2 = var0.lexicon;
        }

        if ((var4 & 4) != 0) {
            var3 = var0.ruleFsts;
        }

        return var0.copy(var1, var2, var3);
    }

    @NotNull
    public String toString() {
        return "HomophoneReplacerConfig(dictDir=" + this.dictDir + ", lexicon=" + this.lexicon + ", ruleFsts=" + this.ruleFsts + ')';
    }

    public int hashCode() {
        int result = this.dictDir.hashCode();
        result = result * 31 + this.lexicon.hashCode();
        result = result * 31 + this.ruleFsts.hashCode();
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        } else if (!(other instanceof HomophoneReplacerConfig)) {
            return false;
        } else {
            HomophoneReplacerConfig var2 = (HomophoneReplacerConfig)other;
            if (!Intrinsics.areEqual(this.dictDir, var2.dictDir)) {
                return false;
            } else if (!Intrinsics.areEqual(this.lexicon, var2.lexicon)) {
                return false;
            } else {
                return Intrinsics.areEqual(this.ruleFsts, var2.ruleFsts);
            }
        }
    }

    public HomophoneReplacerConfig() {
        this((String)null, (String)null, (String)null, 7, (DefaultConstructorMarker)null);
    }
}
