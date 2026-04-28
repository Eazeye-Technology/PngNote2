package com.strokeapp.domain.model;

import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import org.jetbrains.annotations.NotNull;

public enum ShapeType {
    LINE,
    RECTANGLE,
    CIRCLE,
    TRIANGLE,
    POLYGON,
    UNKNOWN;

    // $FF: synthetic field
    private static final EnumEntries $ENTRIES = EnumEntriesKt.enumEntries($values());

    @NotNull
    public static EnumEntries getEntries() {
        return $ENTRIES;
    }

    // $FF: synthetic method
    private static final ShapeType[] $values() {
        ShapeType[] var0 = new ShapeType[]{LINE, RECTANGLE, CIRCLE, TRIANGLE, POLYGON, UNKNOWN};
        return var0;
    }
}
