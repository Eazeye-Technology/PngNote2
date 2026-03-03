package com.txkj.drawingapp.activity;

//search UUID.randomUUID()
public class BookActivity4Config {
    public final static boolean USE_UUID_PAGE_NAME = true;
    public final static boolean USE_VECJ = USE_UUID_PAGE_NAME; //uuid page name and .vecj are applied in the same version
    public final static boolean USE_NO_POINT_SIMPLIFY = true;
    public final static boolean USE_SKETCH = true; //new sketch format
    public final static String USE_SKETCH_PREFIX = "SKETCH_";
    public final static String USE_PAGE_PREFIX  = "PAGE_";
    public final static String USE_SKETCH_CONFIG = "sketch.meta";
    public final static String USE_SKETCH_CONFIG_DISPNAME = "dispName";
    public final static String USE_SKETCH_CONFIG_PAGEORDER = "pageOrder";
    public final static String USE_SKETCH_CONFIG_LASTPAGEINDEX = "lastPageIndex";
    public final static String USE_SKETCH_CONFIG_MEETING_SUMMARY = "meetingSummary";
    public final static String USE_SKETCH_CONFIG_MEETING_DATE = "meetingDate";
    public final static String USE_SKETCH_CONFIG_MEETING_DURATION = "meetingDuration";

    public final static boolean USE_RECORD_META_TO_NOTES_DB = true;

    public final static boolean LOAD_OLD_PAGE_NO_UNDO = true;
}
