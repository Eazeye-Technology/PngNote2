package com.outsbook.libs.canvaseditor.constants;

import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import org.jetbrains.annotations.NotNull;

@Metadata(
        mv = {2, 0, 0},
        k = 1,
        xi = 48,
        d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0004\b\u0000\u0018\u0000 \u00042\u00020\u0001:\u0001\u0004B\u0007¢\u0006\u0004\b\u0002\u0010\u0003¨\u0006\u0005"},
        d2 = {"Lcom/outsbook/libs/canvaseditor/constants/ActionMode;", "", "<init>", "()V", "Companion", "Sources of canvaseditor.app.main"}
)
public final class ActionMode {
    @NotNull
    public static final Companion Companion = new Companion((DefaultConstructorMarker)null);
    private static int NONE;
    private static int DRAG = 1;
    private static int ZOOM_WITH_TWO_FINGER = 2;
    private static int ICON = 3;
    private static int CLICK = 4;

    @Metadata(
            mv = {2, 0, 0},
            k = 1,
            xi = 48,
            d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0011\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R\u001a\u0010\u0004\u001a\u00020\u0005X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0006\u0010\u0007\"\u0004\b\b\u0010\tR\u001a\u0010\n\u001a\u00020\u0005X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000b\u0010\u0007\"\u0004\b\f\u0010\tR\u001a\u0010\r\u001a\u00020\u0005X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000e\u0010\u0007\"\u0004\b\u000f\u0010\tR\u001a\u0010\u0010\u001a\u00020\u0005X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0011\u0010\u0007\"\u0004\b\u0012\u0010\tR\u001a\u0010\u0013\u001a\u00020\u0005X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0014\u0010\u0007\"\u0004\b\u0015\u0010\t¨\u0006\u0016"},
            d2 = {"Lcom/outsbook/libs/canvaseditor/constants/ActionMode$Companion;", "", "<init>", "()V", "NONE", "", "getNONE", "()I", "setNONE", "(I)V", "DRAG", "getDRAG", "setDRAG", "ZOOM_WITH_TWO_FINGER", "getZOOM_WITH_TWO_FINGER", "setZOOM_WITH_TWO_FINGER", "ICON", "getICON", "setICON", "CLICK", "getCLICK", "setCLICK", "Sources of canvaseditor.app.main"}
    )
    public static final class Companion {
        private Companion() {
        }

        public final int getNONE() {
            return ActionMode.NONE;
        }

        public final void setNONE(int var1) {
            ActionMode.NONE = var1;
        }

        public final int getDRAG() {
            return ActionMode.DRAG;
        }

        public final void setDRAG(int var1) {
            ActionMode.DRAG = var1;
        }

        public final int getZOOM_WITH_TWO_FINGER() {
            return ActionMode.ZOOM_WITH_TWO_FINGER;
        }

        public final void setZOOM_WITH_TWO_FINGER(int var1) {
            ActionMode.ZOOM_WITH_TWO_FINGER = var1;
        }

        public final int getICON() {
            return ActionMode.ICON;
        }

        public final void setICON(int var1) {
            ActionMode.ICON = var1;
        }

        public final int getCLICK() {
            return ActionMode.CLICK;
        }

        public final void setCLICK(int var1) {
            ActionMode.CLICK = var1;
        }

        // $FF: synthetic method
        public Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }
    }
}
