package com.outsbook.libs.canvaseditor.enums;

import kotlin.Metadata;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import org.jetbrains.annotations.NotNull;

@Metadata(
        mv = {2, 0, 0},
        k = 1,
        xi = 48,
        d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0005\b\u0080\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003j\u0002\b\u0004j\u0002\b\u0005¨\u0006\u0006"},
        d2 = {"Lcom/outsbook/libs/canvaseditor/enums/DrawType;", "", "<init>", "(Ljava/lang/String;I)V", "PATH", "STICKER", "Sources of canvaseditor.app.main"}
)
public enum DrawType {
    PATH,
    STICKER;

    // $FF: synthetic field
    private static final EnumEntries $ENTRIES = EnumEntriesKt.enumEntries($values());

    @NotNull
    public static EnumEntries getEntries() {
        return $ENTRIES;
    }

    // $FF: synthetic method
    private static final DrawType[] $values() {
        DrawType[] var0 = new DrawType[]{PATH, STICKER};
        return var0;
    }
}
