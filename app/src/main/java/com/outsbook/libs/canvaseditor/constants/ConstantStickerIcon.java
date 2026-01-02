package com.outsbook.libs.canvaseditor.constants;

import android.content.res.Resources;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import org.jetbrains.annotations.NotNull;

@Metadata(
        mv = {2, 0, 0},
        k = 1,
        xi = 48,
        d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0004\b\u0000\u0018\u0000 \u00042\u00020\u0001:\u0001\u0004B\u0007¢\u0006\u0004\b\u0002\u0010\u0003¨\u0006\u0005"},
        d2 = {"Lcom/outsbook/libs/canvaseditor/constants/ConstantStickerIcon;", "", "<init>", "()V", "Companion", "Sources of canvaseditor.app.main"}
)
public final class ConstantStickerIcon {
    @NotNull
    public static final Companion Companion = new Companion((DefaultConstructorMarker)null);
    private static final float DEFAULT_ICON_RADIUS;
    public static final int LEFT_TOP = 0;
    public static final int RIGHT_TOP = 1;
    public static final int LEFT_BOTTOM = 2;
    public static final int RIGHT_BOTTOM = 3;

    static {
        DEFAULT_ICON_RADIUS = 14.0F * Resources.getSystem().getDisplayMetrics().density;
    }

    @Metadata(
            mv = {2, 0, 0},
            k = 1,
            xi = 48,
            d1 = {"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0007\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0004\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007R\u000e\u0010\b\u001a\u00020\tX\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\tX\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\tX\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\tX\u0086T¢\u0006\u0002\n\u0000¨\u0006\r"},
            d2 = {"Lcom/outsbook/libs/canvaseditor/constants/ConstantStickerIcon$Companion;", "", "<init>", "()V", "DEFAULT_ICON_RADIUS", "", "getDEFAULT_ICON_RADIUS", "()F", "LEFT_TOP", "", "RIGHT_TOP", "LEFT_BOTTOM", "RIGHT_BOTTOM", "Sources of canvaseditor.app.main"}
    )
    public static final class Companion {
        private Companion() {
        }

        public final float getDEFAULT_ICON_RADIUS() {
            return ConstantStickerIcon.DEFAULT_ICON_RADIUS;
        }

        // $FF: synthetic method
        public Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }
    }
}
