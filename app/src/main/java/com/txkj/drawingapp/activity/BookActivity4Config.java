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
    public final static String USE_SKETCH_CONFIG_MEETING_SPEAKER = "meetingSpeaker";
    public final static String USE_SKETCH_CONFIG_MEETING_SUMMARY = "meetingSummary";
    public final static String USE_SKETCH_CONFIG_MEETING_DATE = "meetingDate";
    public final static String USE_SKETCH_CONFIG_MEETING_DURATION = "meetingDuration";
    public final static String USE_SKETCH_CONFIG_MEETING_DURATION_TEMP = "meetingDurationTemp";
    public final static String USE_SKETCH_CONFIG_MEETING_HOUR = "meetingHour";
    public final static String USE_SKETCH_CONFIG_MEETING_MINUTE = "meetingMinute";
    public final static String USE_SKETCH_CONFIG_PATTERN = "pattern";
    public final static String USE_DIARIZATION_RESULT_CONFIG = "diarization_result.meta";
    public final static String USE_DIARIZATION_RESULT_ADD_CONFIG = "diarization_result_add.meta";
    public final static String USE_DIARIZATION_SPEAKERS_CONFIG = "diarization_speakers.meta";

    public final static boolean USE_RECORD_META_TO_NOTES_DB = true;

    public final static boolean LOAD_OLD_PAGE_NO_UNDO = true;

    public final static String CONFIG_SAVING_TEST = "configSavingTest"; //1:yes; other:no;
    public final static String CONFIG_TYPE_ASR_TEST = "configTypeASRTest";


    //    Double tap with three fingers: Toggle focus mode
    public final static boolean ENABLE_GESTURE_FOCUS_MODE = true;
    //    Swipe right on the right side of canvas: Next page
    public final static boolean ENABLE_GESTURE_NEXT_PAGE = true;
    //    Swipe left on the left side of canvas: Previous page
    public final static boolean ENABLE_GESTURE_PREVIOUS_PAGE = true;
    //    Pinch and rotate with two fingers: Pinch and rotate with two fingers
    public final static boolean ENABLE_GESTURE_PINCH_ROTATE = true;
    //    Two finger pinch out: Zoom out
    public final static boolean ENABLE_GESTURE_ZOOM_OUT = true;
    //    Two finger pinch in: Zoom in
    public final static boolean ENABLE_GESTURE_ZOOM_IN = true;
    //    Swipe up from bottom of screen: Open navigation panel
    public final static boolean ENABLE_GESTURE_NAVIGATION_PANEL = true;

}
