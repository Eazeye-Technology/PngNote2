package com.txkj.notemobile2.colorpicker;

public class FileMeta {
    public final static String NONE = "None";
    public final static String LINED = "Lined";
    public final static String GRAPH = "Graph";
    public final static String DOTTED = "Dotted";
    public final static String LINED_LONG_DASH = "LinedLongDash";
    public final static String LINED_SHORT_DASH = "LinedShortDash";

    public String name;
    public int drawable;

    public FileMeta(String name, int drawable) {
        this.name = name;
        this.drawable = drawable;
    }
}
