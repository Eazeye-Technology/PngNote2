package io.github.pastthepixels.freepaint.Graphics;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.text.Html;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SizeF;
import android.view.GestureDetector;
import android.view.InputDevice;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import com.txkj.drawingapp.activity.BookActivity4Fragment;
import com.txkj.drawingapp.activity.BookActivity4Utils;
import com.txkj.notemobile2.colorpicker.FileMeta;
import com.txkj.notemobile2.ui.CanvasBoox;

import net.nightwhistler.htmlspanner.HtmlSpanner;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

import io.github.pastthepixels.freepaint.File.SVG;
import io.github.pastthepixels.freepaint.File.VecJson;
import io.github.pastthepixels.freepaint.Tools.EraserTool;
import io.github.pastthepixels.freepaint.Tools.PaintTool;
import io.github.pastthepixels.freepaint.Tools.PanTool;
import io.github.pastthepixels.freepaint.Tools.SelectionTool;
import io.github.pastthepixels.freepaint.Tools.Tool;

public final class DrawCanvas extends View {
    private final static boolean USE_JUMP_PAGE_CENTER = true;//跳转页面后居中

    private final static double INIT_SCALE = 1.0;//0.8;
    private final static boolean DEBUG_EVENT = true;
    private final static String TAG = "DrawCanvas";

    public final Paint paint = new Paint();
    // Stores previous "versions" of DrawCanvas.paths you can restore
    // You can move back and forth between this, but every time you create a new change
    // it removes everything after the current index (solving the grandfather paradox, btw)
//    public final ArrayList<LinkedList<DrawPath>> versions = new ArrayList<>();
    public final ArrayList<CopyOnWriteArrayList<DrawPath>> versions = new ArrayList<>();
    public int oldVersionsSize = 0;
    public final static int MAX_VERSIONS = 17; //256;
    public final Point documentSize = new Point(0, 0);
    private final PaintTool paintTool = new PaintTool(this);
    private final EraserTool eraserTool = new EraserTool(this);
    private final PanTool panTool = new PanTool(this);
    private final SelectionTool selectionTool = new SelectionTool(this);
    private final SVG svgHelper = new SVG(this);
    private final VecJson vecJsonHelper = new VecJson(this);

    // public void createJson() {
    //for (DrawPath path : canvas.paths)
    /*
    FATAL EXCEPTION: Thread-115
Process: com.txkj.drawingapp, PID: 16713
java.util.ConcurrentModificationException
	at java.util.LinkedList$ListItr.checkForComodification(LinkedList.java:970)
	at java.util.LinkedList$ListItr.next(LinkedList.java:892)
	at io.github.pastthepixels.freepaint.File.VecJson.createJson(VecJson.java:83)
	at io.github.pastthepixels.freepaint.Graphics.DrawCanvas.getVecJson(DrawCanvas.java:186)
	at com.txkj.drawingapp.activity.BookActivity4Fragment.getVecJson(BookActivity4Fragment.java:3927)
	at com.txkj.drawingapp.activity.BookActivity4Fragment$3.run(BookActivity4Fragment.java:369)
	at java.lang.Thread.run(Thread.java:1012)
     */
    //public LinkedList<DrawPath> paths = new LinkedList<>();
    public CopyOnWriteArrayList<DrawPath> paths = new CopyOnWriteArrayList<>();
    public int documentColor = Color.WHITE;
    public/*private*/ int version_index = -1;
    public TOOLS tool = TOOLS.none;

    // Drawing flags
    // Draws only the document, without any tool paths, or any rotation/translation.
    private boolean drawMinimal = false;
    private boolean drawMinimalBG = false;

    /**
     * Constructor
     */
    public DrawCanvas(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setFocusable(true);
        setFocusableInTouchMode(true);
        // Initialises documentSize with the size in the last used document
        if (false) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
            documentSize.set(
                    Float.parseFloat(prefs.getString("documentWidth", "816")),
                    Float.parseFloat(prefs.getString("documentHeight", "1056"))
            );
        } else {
            //use screen width height
            DisplayMetrics DM = new DisplayMetrics();
            int w = 816;
            int h = 1056;
            if (context instanceof Activity) {
                ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(DM);
                if (DM.heightPixels > 0 && DM.widthPixels > 0) {
                    if (DM.heightPixels > DM.widthPixels) {
                        w = DM.widthPixels;
                        h = DM.heightPixels;
                    } else {
                        w = DM.heightPixels;
                        h = DM.widthPixels;
                    }
                }
            }
            documentSize.set(w, h);
        }

        gestureDetector = new GestureDetector(getContext(), gestureListener);
        //gestureDetector.setIsLongpressEnabled(false);
        this.scaleDetector = new ScaleGestureDetector(getContext(), scaleListener);
    }

    /**
     * Constructor
     */
    public DrawCanvas(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Constructor
     */
    public DrawCanvas(Context context) {
        this(context, null, 0);
    }

    public Activity mAct = null;
    public void initAct(Activity act) {
        this.mAct = act;
    }

    /**
     * Re-centers document when the size of the View changes
     *
     * @param w    Current width of this view.
     * @param h    Current height of this view.
     * @param oldw Old width of this view.
     * @param oldh Old height of this view.
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (disableCenter) {
            return;
        }
        centerDocument();
    }

    /**
     * Changes the pan tool's scale factor and offset so that the document is in the middle of the screen.
     */
    private void centerDocument() {
        // Scales the canvas so that the document width takes up 80% of the screen width
        panTool.scaleFactor = (float) ((INIT_SCALE/*0.8*/) * (getWidth() / documentSize.x));
        panTool.updatePanOffset();
        panTool.offset.set(
                ((float) (getWidth()) / 2 - documentSize.x / 2),
                ((float) (getHeight()) / 2 - documentSize.y / 2)
        );
    }

    /**
     * Saves a DrawCanvas as an SVG at a URI
     *
     * @param uri URI of the SVG
     * @throws IOException May be thrown if the file path is invalid or the program can't write to it
     */
    public void saveFile(Uri uri) throws IOException {
        svgHelper.createSVG();
        svgHelper.writeFile(Objects.requireNonNull(getContext().getContentResolver().openOutputStream(uri, "wt")));
    }
    //synchronized is mainly for .createJson, for (DrawPath path : canvas.paths)
    public synchronized String getVecJson() {
        vecJsonHelper.createJson();
        return vecJsonHelper.writeString();
    }
    public synchronized void loadVecJson(String strVecJson) {
        vecJsonHelper.parseFile(strVecJson);
    }

    /**
     * Loads path data from an SVG
     *
     * @param uri URI of the SVG
     * @throws IOException May be thrown if the file path is invalid or the program can't read from it.
     */
    @SuppressLint("DefaultLocale")
    public void loadFile(Uri uri) throws IOException {
        // Clear path list/history
        paths.clear();
        versions.clear();
        version_index = -1;
        // Load the file
        svgHelper.createSVG();
        svgHelper.loadFile(getContext().getContentResolver().openInputStream(uri));
        if (getTool() != null) getTool().init();
        centerDocument();
        // Sets settings for document width/height to new document size
        @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
        editor.putString("documentWidth", String.format("%d", (int) documentSize.x));
        editor.putString("documentHeight", String.format("%d", (int) documentSize.y));
        editor.apply();
        // Save everything in the version history
        versions.add(cloneDrawPathList(paths));
        oldVersionsSize = versions.size();
        version_index += 1;
        onVersionChanged();
    }

    boolean isPan = false;
    int lastSource = 0;


    public boolean gScaleBegin = false;
    private ScaleGestureDetector scaleDetector;
    private ScaleGestureDetector.SimpleOnScaleGestureListener scaleListener = new ScaleGestureDetector.SimpleOnScaleGestureListener() {
//        @Override
//        public boolean onScaleBegin(@NonNull ScaleGestureDetector detector) {
//            //Toast.makeText(getContext(), "onScaleBegin", Toast.LENGTH_LONG).show();
//            gScaleBegin = true; //don't use this method, easy to trigger
//            return super.onScaleBegin(detector);
//        }
        @Override
        public boolean onScale(@NonNull ScaleGestureDetector detector) {
            //Toast.makeText(getContext(), "onScale", Toast.LENGTH_LONG).show();
            if (detector != null && detector.getScaleFactor() > 2.0f) {
                gScaleBegin = true;
            }
            return super.onScale(detector);
        }
    };
    private GestureDetector gestureDetector;
    private GestureDetector.SimpleOnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onDoubleTap(@NonNull MotionEvent e) {
            Toast.makeText(getContext(), "onDoubleTap", Toast.LENGTH_LONG).show();
            return super.onDoubleTap(e);
        }

        @Override
        public boolean onSingleTapConfirmed(@NonNull MotionEvent e) {
            Toast.makeText(getContext(), "onSingleTapConfirmed", Toast.LENGTH_LONG).show();
            return super.onSingleTapConfirmed(e);
        }
    };

    /**
     * Adds touch points when the user touches the screen.
     *
     * @param event The motion event.
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (DEBUG_EVENT) {
            Log.e(TAG, "event.getDeviceId() == " + event.getDeviceId());
            Log.e(TAG, "event.getSource() == " + event.getSource());
            Log.e(TAG, "InputDevice.SOURCE_STYLUS == " + ((event.getSource() & InputDevice.SOURCE_STYLUS) == InputDevice.SOURCE_STYLUS) +
                    ", event.getPressure() == " + event.getPressure() +
                    ", event.getToolType() == " + event.getToolType(0));

                /*
touch:
event.getDeviceId() == 4
event.getSource() == 4098
InputDevice.SOURCE_STYLUS == false, event.getPressure() == 1.0
stylus:
event.getDeviceId() == 4
event.getSource() == 20482
InputDevice.SOURCE_STYLUS == true, event.getPressure() == 0.06642247
stylus eraser:
event.getDeviceId() == 4
event.getSource() == 20482
InputDevice.SOURCE_STYLUS == true, event.getPressure() == 0.25006106


    public static final int TOOL_TYPE_ERASER = 4;
    public static final int TOOL_TYPE_FINGER = 1;
    public static final int TOOL_TYPE_MOUSE = 3;
    public static final int TOOL_TYPE_STYLUS = 2;
    public static final int TOOL_TYPE_UNKNOWN = 0;
                 */
/*
wacom:
event.getDeviceId() == 6
event.getSource() == 20482
InputDevice.SOURCE_STYLUS == true, event.getPressure() == 0.17948718, event.getToolType() == 2
event.getDeviceId() == 6
event.getSource() == 20482
InputDevice.SOURCE_STYLUS == true, event.getPressure() == 0.24566546, event.getToolType() == 2
event.getDeviceId() == 6
event.getSource() == 20482
InputDevice.SOURCE_STYLUS == true, event.getPressure() == 0.24810746, event.getToolType() == 2
...
InputDevice.SOURCE_STYLUS == true, event.getPressure() == 0.43516487, event.getToolType() == 2
event.getDeviceId() == 6
event.getSource() == 20482
InputDevice.SOURCE_STYLUS == true, event.getPressure() == 0.43956047, event.getToolType() == 2
event.getDeviceId() == 6
event.getSource() == 20482
InputDevice.SOURCE_STYLUS == true, event.getPressure() == 0.43956047, event.getToolType() == 2
event.getDeviceId() == 6
event.getSource() == 20482
InputDevice.SOURCE_STYLUS == true, event.getPressure() == 0.23565325, event.getToolType() == 2
event.getDeviceId() == 6
event.getSource() == 20482
InputDevice.SOURCE_STYLUS == true, event.getPressure() == 0.08693529, event.getToolType() == 2
event.getDeviceId() == 6
event.getSource() == 20482
InputDevice.SOURCE_STYLUS == true, event.getPressure() == 0.08693529, event.getToolType() == 2



adb:
event.getDeviceId() == -1
event.getSource() == 20482
InputDevice.SOURCE_STYLUS == true, event.getPressure() == 1.0, event.getToolType() == 1
event.getDeviceId() == -1
event.getSource() == 20482
InputDevice.SOURCE_STYLUS == true, event.getPressure() == 0.0, event.getToolType() == 1
event.getDeviceId() == -1
event.getSource() == 20482
InputDevice.SOURCE_STYLUS == true, event.getPressure() == 0.0, event.getToolType() == 1
event.getDeviceId() == -1
event.getSource() == 20482
InputDevice.SOURCE_STYLUS == true, event.getPressure() == 1.0, event.getToolType() == 1


hand:
InputDevice.SOURCE_STYLUS == false, event.getPressure() == 0.5546875, event.getToolType() == 1
event.getDeviceId() == 4
event.getSource() == 4098
InputDevice.SOURCE_STYLUS == false, event.getPressure() == 0.5546875, event.getToolType() == 1
event.getDeviceId() == 4
event.getSource() == 4098
InputDevice.SOURCE_STYLUS == false, event.getPressure() == 0.546875, event.getToolType() == 1
event.getDeviceId() == 4
event.getSource() == 4098
InputDevice.SOURCE_STYLUS == false, event.getPressure() == 0.390625, event.getToolType() == 1
event.getDeviceId() == 4
event.getSource() == 4098
InputDevice.SOURCE_STYLUS == false, event.getPressure() == 0.390625, event.getToolType() == 1

 */
        }
        // Runs chosenTool.onTouchEvent if it exists, otherwise don't update the screen.
        TOOLS curTool = this.tool; //temporary, don't modify current Tool
        if (tool == TOOLS.paint) {
            if (isEmulator()) {
                //skip
            } else {
                boolean lastSourceChanged = false;
                if (event != null && event.getSource() != lastSource) {
                    lastSource = event.getSource();
                    lastSourceChanged = true;
                }
                if (isPan) {
                    curTool = TOOLS.pan;
                    if (lastSourceChanged || (event != null && event.getAction() == MotionEvent.ACTION_UP)) {
                        isPan = false;
                        curTool = this.tool;
                    }
                } else if (event != null && event.getAction() == MotionEvent.ACTION_DOWN) {
                    //don't check SOURCE_TOUCHSCREEN, because SOURCE_STYLUS contains SOURCE_TOUCHSCREEN
                    boolean isStylus = ((event.getSource() & InputDevice.SOURCE_STYLUS) == InputDevice.SOURCE_STYLUS);
                    boolean isStylusScreen = (((event.getSource() & InputDevice.SOURCE_STYLUS) == InputDevice.SOURCE_STYLUS) &&
                            event.getPointerCount() > 0 &&
                            event.getToolType(0) == MotionEvent.TOOL_TYPE_FINGER);
                    boolean isTouchScreen = (((event.getSource() & InputDevice.SOURCE_TOUCHSCREEN) == InputDevice.SOURCE_TOUCHSCREEN) &&
                            event.getPointerCount() > 0 &&
                            event.getToolType(0) == MotionEvent.TOOL_TYPE_FINGER);
                    if (isEmulator()) {
                        //skip
                    }
                    if ((!isStylus || isStylusScreen) && !isTouchScreen) { //FIXME:
                        curTool = TOOLS.pan;
                        isPan = true;
                    }
                }
            }
        } else if (this.tool == TOOLS.select) {
            scaleDetector.onTouchEvent(event);
            if (gScaleBegin) {
                curTool = TOOLS.pan;
                getSelectionTool().getToolPaths().clear();
                isPan = true;
            }
        }

        if (tool == TOOLS.none || !Objects.requireNonNull(getTool_(curTool)).onTouchEvent(event)) {
            return false;
        } else {
            if (getTool().allowVersionBackup() && event.getAction() == MotionEvent.ACTION_UP) {
                versionBackup();
            }
            postInvalidate(); // Indicate view should be redrawn
            return true; // Indicate we've consumed the touch
        }
    }

    public void versionBackup() {
        // Remove any edits after the current.
        while (versions.size() > version_index + 1 + oldVersionsSize) {
            versions.remove(versions.size() - 1);
        }
        versions.add(cloneDrawPathList(paths)); // adds to the end ∴ newest changes are at the end of the list
        System.out.println(versions + " " + versions.size());
        if (versions.size() < (MAX_VERSIONS + 2) - 1 + oldVersionsSize) {
            version_index += 1;
        }
        if (versions.size() >= (MAX_VERSIONS + 2) - 1 + oldVersionsSize) {
            versions.remove(0); // delete the oldest change if the list has grown too much
        }
        onVersionChanged();
        BookActivity4Utils.clearRestorePages(mAct);
    }

    //@SuppressLint("ClickableViewAccessibility")
    //@Override
    public boolean onTouchEvent_test(MotionEvent event) {
        if (DEBUG_EVENT) {
            Log.e(TAG, "event.getActionMasked() == " + event.getActionMasked());
            Log.e(TAG, "event.getPointerCount() == " + event.getPointerCount());
            Log.e(TAG, "event.getDeviceId() == " + event.getDeviceId());
            Log.e(TAG, "event.getSource() == " + event.getSource());
            Log.e(TAG, "InputDevice.SOURCE_STYLUS == " + ((event.getSource() & InputDevice.SOURCE_STYLUS) == InputDevice.SOURCE_STYLUS) +
                    ", event.getPressure() == " + event.getPressure() +
                    ", event.getToolType() == " + event.getToolType(0));

                /*
touch:
event.getDeviceId() == 4
event.getSource() == 4098
InputDevice.SOURCE_STYLUS == false, event.getPressure() == 1.0
stylus:
event.getDeviceId() == 4
event.getSource() == 20482
InputDevice.SOURCE_STYLUS == true, event.getPressure() == 0.06642247
stylus eraser:
event.getDeviceId() == 4
event.getSource() == 20482
InputDevice.SOURCE_STYLUS == true, event.getPressure() == 0.25006106


    public static final int TOOL_TYPE_ERASER = 4;
    public static final int TOOL_TYPE_FINGER = 1;
    public static final int TOOL_TYPE_MOUSE = 3;
    public static final int TOOL_TYPE_STYLUS = 2;
    public static final int TOOL_TYPE_UNKNOWN = 0;
                 */
        }

        if (false) {
            gestureDetector.onTouchEvent(event);
            return true;
        } else if (true) {
            // Runs chosenTool.onTouchEvent if it exists, otherwise don't update the screen.
            TOOLS curTool = this.tool; //temporary, don't modify current Tool
            if (tool == TOOLS.paint || tool == TOOLS.pan) {
                if (isEmulator()) {
                    //skip
                } else {
                    boolean lastSourceChanged = false;
                    if (event != null && event.getSource() != lastSource) {
                        lastSource = event.getSource();
                        lastSourceChanged = true;
                    }
                    if (isPan) {
                        curTool = TOOLS.pan;
                        if (lastSourceChanged || (event != null && event.getAction() == MotionEvent.ACTION_UP)) {
                            boolean result = Objects.requireNonNull(getTool_(curTool)).onTouchEvent(event);
                            isPan = false;
                            curTool = this.tool;
                            return result;
                        } else {
                            return Objects.requireNonNull(getTool_(curTool)).onTouchEvent(event);
                        }
                    } else if (event != null && event.getAction() == MotionEvent.ACTION_DOWN) {
                        //don't check SOURCE_TOUCHSCREEN, because SOURCE_STYLUS contains SOURCE_TOUCHSCREEN
                        boolean isStylus = ((event.getSource() & InputDevice.SOURCE_STYLUS) == InputDevice.SOURCE_STYLUS);
                        boolean isStylusScreen = (((event.getSource() & InputDevice.SOURCE_STYLUS) == InputDevice.SOURCE_STYLUS) &&
                                event.getPointerCount() > 0 &&
                                event.getToolType(0) == MotionEvent.TOOL_TYPE_FINGER);
                        boolean isTouchScreen = (((event.getSource() & InputDevice.SOURCE_TOUCHSCREEN) == InputDevice.SOURCE_TOUCHSCREEN) &&
                                event.getPointerCount() > 0 &&
                                event.getToolType(0) == MotionEvent.TOOL_TYPE_FINGER);
                        if (isEmulator()) {
                            //skip
                        }
                        int pointerCount = event.getPointerCount();
                        if ((!isStylus || isStylusScreen) && !isTouchScreen) { //FIXME:
                            curTool = TOOLS.pan;
                            isPan = true;
                            return Objects.requireNonNull(getTool_(curTool)).onTouchEvent(event);
                        }
                    }
                }
            }

            if (tool == TOOLS.none || !Objects.requireNonNull(getTool_(curTool)).onTouchEvent(event)) {
                return false;
            } else {
                if (getTool().allowVersionBackup() && event.getAction() == MotionEvent.ACTION_UP) {
                    versionBackup();
                }
                postInvalidate(); // Indicate view should be redrawn
                return true; // Indicate we've consumed the touch
            }
        } else {
            return super.onTouchEvent(event);
        }
    }

    /**
     * (deep) Clones a list of DrawPaths.
     * TODO: Instead of making a new list, with pointers to the same DrawPaths, clone those DrawPaths (deep clone the list).
     *       This is so that if you erase a part of a path, and modify it, you can undo that.
     *
     * @param listToClone The list you want to clone.
     * @return A deep cloned version of the list.
     */
    public LinkedList<DrawPath> cloneDrawPathList(LinkedList<DrawPath> listToClone) {
        LinkedList<DrawPath> list = new LinkedList<>();
        for (DrawPath pathToClone : listToClone) {
            list.add(pathToClone.clone());
        }
        return list;
    }
    public CopyOnWriteArrayList<DrawPath> cloneDrawPathList(CopyOnWriteArrayList<DrawPath> listToClone) {
        CopyOnWriteArrayList<DrawPath> list = new CopyOnWriteArrayList<>();
        for (DrawPath pathToClone : listToClone) {
            list.add(pathToClone.clone());
        }
        return list;
    }

    /**
     * Undoes an operation by resetting DrawCanvas.paths to what it looked like after the previous operation.
     */
    public void undo(boolean skipVersionUndo) {
        if (!skipVersionUndo) {
            if (oldVersionsSize + version_index > 0) {
                System.out.println(versions.toString() + (oldVersionsSize + version_index - 1));
                version_index -= 1;
                paths = cloneDrawPathList(versions.get(oldVersionsSize + version_index));
            } else {
                if (oldVersionsSize == 0) { //if no history, clear
                    version_index = -1;
                    paths.clear();
                } else {
                    //if history exists
                    //FIXME: not clear
                }
            }
        }
        // Force redraw
        postInvalidate();
        // Re-initialise tools
        if (tool == TOOLS.eraser) getTool().init();


        //FIXME:added, unselect all
        this.getSelectionTool().getSelectedPaths().clear();
        this.getSelectionTool().currentPath.clear();
        onVersionChanged();
        BookActivity4Utils.clearRestorePages(mAct);
    }

    public void onVersionChanged() {
        boolean isUndoActive = true;
        boolean isRedoActive = true;
        if (version_index >= 0) {
            isUndoActive = true;
        } else {
            isUndoActive = false;
        }
        // - oldVersionsSize
        if (versions.size() > oldVersionsSize && version_index < versions.size() - 1 - oldVersionsSize && version_index >= -1 /* + oldVersionsSize*/) {
            isRedoActive = true;
//        } else if (version_index <= 0 && !versions.isEmpty()) {
//            isRedoActive = true;
        } else {
            isRedoActive = false;
        }
        BookActivity4Utils.onVersionChanged(mAct, isUndoActive, isRedoActive,
                ((version_index) - (-1)),
                ((versions.size() - 1 - oldVersionsSize) - (version_index))
                );
    }

    /**
     * Redoes an operation by setting DrawCanvas.paths to what it looked like after an operation you undid to.
     */
    public void redo() {
        if (version_index < versions.size() - 1 - oldVersionsSize) {
            version_index += 1;
            paths = cloneDrawPathList(versions.get(version_index + oldVersionsSize));
        } else if (version_index <= 0 && !versions.isEmpty()) {
            version_index = 0;
            paths = cloneDrawPathList(versions.get(0));
        }
        // Force redraw
        postInvalidate();
        // Re-initialise tools
        if (tool == TOOLS.eraser) {
            getTool().init();
        }
        onVersionChanged();
        BookActivity4Utils.clearRestorePages(mAct);
    }

    /**
     * Gets a bitmap from a DrawCanvas.
     */
    @SuppressLint("WrongCall")
    public Bitmap toBitmap(boolean isDrawBG) {
        //FIXME:可能大小不对
        Bitmap bitmap = Bitmap.createBitmap((int) this.documentSize.x, (int) this.documentSize.y, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        this.drawMinimal = true;
        if (isDrawBG) {
            this.drawMinimalBG = true;
        } else {
            this.drawMinimalBG = false;
        }
        //this.draw(canvas);
        this.onDraw(canvas);
        this.drawMinimal = false;
        this.drawMinimalBG = false;
        return bitmap;
    }

    /**
     * Gets the chosen tool
     *
     * @return A Tool instance, depending on DrawCanvas.tool
     */
    public Tool getTool() {
        switch (tool) {
            case none:
                return null;
            case paint:
                return paintTool;
            case eraser:
                return eraserTool;
            case pan:
                return panTool;
            case select:
                return selectionTool;
        }
        return null;
    }

    private Tool getTool_(TOOLS tool_) {
        switch (tool_) {
            case none:
                return null;
            case paint:
                return paintTool;
            case eraser:
                return eraserTool;
            case pan:
                return panTool;
            case select:
                return selectionTool;
        }
        return null;
    }

    /**
     * Setting tools
     *
     * @param tool Not a Tool instance, but rather from an Enum at <code>DrawCanvas.TOOLS</code>
     */
    public void setTool(TOOLS tool) {
        this.tool = tool;
        if (tool != TOOLS.none && getTool() != null) {
            getTool().init();
        }
        postInvalidate(); // Indicate view should be redrawn
    }

    /**
     * Maps a point from screen coordinates to Canvas coordinates (accounts for transformations)
     *
     * @param x X of the screen point (top left corner = origin point)
     * @param y Y of the screen point (top left corner = origin point)
     * @return New Point instance for the point in Canvas coordinates (has its own origin point)
     */
    public Point mapPoint(float x, float y, float pressure) {
        return new Point(
                (x / panTool.scaleFactor) - panTool.offset.x - panTool.panOffset.x,
                (y / panTool.scaleFactor) - panTool.offset.y - panTool.panOffset.y,
                pressure
        );
    }

    public Point mapPointScreen(float x, float y, float pressure) {
        return new Point(
                (x + panTool.offset.x + panTool.panOffset.x) * panTool.scaleFactor,
                (y + panTool.offset.y + panTool.panOffset.y) * panTool.scaleFactor,
                pressure
        );
    }

    /**
     * Gets the pan tool's scale factor.
     *
     * @return The scale of the pan tool (1 == 1x)
     */
    public float getScaleFactor() {
        return panTool.scaleFactor;
    }

    /**
     * Gets the pan tool's offset
     *
     * @return The offset of the pan tool
     */
    public Point getPosition() {
        return panTool.offset;
    }

    /**
     * Draws the document rectangle, paths, and then custom paths that tools create (ex. to show bounds of a selection)
     *
     * @param canvas the canvas on which the background will be drawn
     */
    protected void onDraw(@NonNull Canvas canvas) {
        // Allows us to do things like setting a custom background
        if (!drawMinimal) {
            super.onDraw(canvas);
        }
        float screenDensity = getResources().getDisplayMetrics().density;
        //
        BookActivity4Utils.updateInfoBar(getContext());
        // Draws things on the screen
        canvas.save();
        // SCALES, THEN TRANSLATES (translations are independent of scales)
        if (!drawMinimal) {
            canvas.translate(0,  - panTool.mDeltaY);
            canvas.scale(panTool.scaleFactor, panTool.scaleFactor);
            canvas.translate(panTool.offset.x + panTool.panOffset.x, panTool.offset.y + panTool.panOffset.y);
        }
        // Draws what the page will look like
        paint.setColor(documentColor);
        paint.setStyle(Paint.Style.FILL);
        if (!drawMinimal) {
            paint.setShadowLayer(12, 0, 0, Color.argb(200, 0, 0, 0));
        }
        if (!drawMinimal) {
            canvas.drawRect(0, 0, documentSize.x, documentSize.y, paint);
        }
        paint.reset();
        if (true) {
            //FIXME:added
            canvas.clipRect(0, 0, documentSize.x, documentSize.y); //don't show outer
        }
        // Draws a stroke for the page
//        if (!drawMinimal) {
//            paint.setColor(Color.GRAY);
//            paint.setStrokeWidth(5 / panTool.scaleFactor); // Always five pixels no matter scale
//            paint.setStyle(Paint.Style.STROKE);
//            canvas.drawRect(0, 0, documentSize.x, documentSize.y, paint);
//            paint.reset();
//        }
        drawBackground(canvas, mBackgroundMode, documentSize.x, documentSize.y);
        // Draws every path, then tool path
        for (DrawPath path : paths) {
            paint.reset();
            path.draw(canvas, paint, screenDensity, getScaleFactor(), drawMinimal);
        }

        boolean menuHidden = false;
        DrawPath selectedPath = null;
        if (!getSelectionTool().getSelectedPaths().isEmpty()) {
            selectedPath = getSelectionTool().getSelectedPaths().get(0);
        }
//        if (getSelectionTool().getSelectedPaths().size() == 1 &&
//                selectedPath != null && selectedPath.pointsType == DrawPath.POINTS_TYPE_STROKE) {
//            menuHidden = true;
//        }

        if (true) {
//            Matrix selectedMatrix = new Matrix();
//            if (selectedPath != null) {
//                selectedMatrix.set(selectedPath.getMatrix());
//            }
            canvas.save();
            //Matrix matrix = new Matrix();
            //int w = path.pointsBitmap != null ? path.pointsBitmap.getWidth() : 0;
            //int h = path.pointsBitmap != null ? path.pointsBitmap.getHeight() : 0;
            //matrix.postScale(path.scaleX, path.scaleY,  0, 0);
//            canvas.concat(selectedMatrix);

            if (!drawMinimal && getTool() != null && getTool().getToolPaths() != null) {
                if (EraserTool.USE_SIMPLE_IMPL) {
                    //skip, 不显示上方的全局灰色遮罩层
                } else {
                    if (getTool() instanceof EraserTool) {
                        paint.setARGB(150, 0, 0, 0);
                        paint.setStyle(Paint.Style.FILL);
                        canvas.drawPaint(paint);
                    }
                }
                for (DrawPath path : getTool().getToolPaths()) {
                    paint.reset();
                    path.draw(canvas, paint, screenDensity, getScaleFactor(), drawMinimal);

                    //draw menu on four corners
                    if (!menuHidden && getTool() instanceof SelectionTool) {
                        float radius = getSelectionTool().iconRadius / panTool.scaleFactor;
                        getSelectionTool().iconRadiusActual = radius;
                        if (getSelectionTool().mode != SelectionTool.TOUCH_MODES.define) {
                            for (int i = 0; i < path.points.size() && i < 4; ++i) {
                                Point pt = path.points.get(i);
                                Drawable drawable = null;
                                if (i == SelectionTool.rotateIcon_index || i == SelectionTool.deleteIcon_index) {
                                    if (SelectionTool.HIDE_DELETE_BUTTON) {
                                        //hide this button
                                    } else {
                                        drawable = getSelectionTool().deleteIcon;
                                    }
                                    //FIXME:don't allow multi objects to rotate, just allow shape pen to ratate
                                    drawable = null;
                                    if (getSelectionTool().getSelectedPaths().size() == 1) {
                                        DrawPath path0 = getSelectionTool().getSelectedPaths().get(0);
                                        if (path0 != null &&
                                                (path0.pointsType == DrawPath.POINTS_TYPE_STROKE &&
                                                        path0.appearance.penType == DrawAppearance.PEN_TYPE_6 &&
                                                        BookActivity4Fragment.ENABLE_NO_DETECT_SHAPE_PEN)) {
                                            drawable = getSelectionTool().rotateIcon;
                                        }
                                    }
                                } else if (i == SelectionTool.doneIcon_index) {
                                    drawable = getSelectionTool().doneIcon;
                                } else if (i == SelectionTool.zoomIcon_index) {
                                    //FIXME:暂时不允许多个对象缩放, 只允许单个文本和图片缩放
                                    if (getSelectionTool().getSelectedPaths().size() == 1) {
                                        DrawPath path0 = getSelectionTool().getSelectedPaths().get(0);
                                        if (path0 != null &&
                                                (path0.pointsType == DrawPath.POINTS_TYPE_STROKE ||
                                                        path0.pointsType == DrawPath.POINTS_TYPE_IMAGE ||
                                                        path0.pointsType == DrawPath.POINTS_TYPE_TEXT)) {
                                            drawable = getSelectionTool().zoomIcon;
                                        }
                                    } else {
                                        if (getSelectionTool().getSelectedPaths().size() > 1) {
                                            boolean allStroke = true;
                                            if (DrawPath.ALLOW_MULTI_SCALE) {
                                                //skip
                                            } else {
                                                for (DrawPath itemPath : getSelectionTool().getSelectedPaths()) {
                                                    if (itemPath == null ||
                                                            itemPath.pointsType != DrawPath.POINTS_TYPE_STROKE) {
                                                        allStroke = false;
                                                        break;
                                                    }
                                                }
                                            }
                                            if (allStroke) {
                                                //if all are strokes, they can be zoomed
                                                drawable = getSelectionTool().zoomIcon;
                                            }
                                        }
                                    }
                                } else if (i == SelectionTool.editIcon_index) {
                                    //FIXME:暂时不允许多个对象编辑, 只允许单个文本编辑
                                    if (getSelectionTool().getSelectedPaths().size() == 1) {
                                        DrawPath path0 = getSelectionTool().getSelectedPaths().get(0);
                                        if (path0 != null &&
                                                (path0.pointsType == DrawPath.POINTS_TYPE_TEXT)) {
                                            drawable = getSelectionTool().editIcon;
                                        }
                                    }
                                }



                                {
                                    if (true) {
                                        Paint iconPaint = new Paint();
                                        iconPaint.setAntiAlias(true);
                                        iconPaint.setColor(0xFF000000);
                                        iconPaint.setAlpha(100);//50);
                                        canvas.drawCircle(pt.x, pt.y, radius, iconPaint);
                                    }
                                    if (drawable != null) {
                                        Rect bounds = new Rect(
                                                (int) pt.x - (int) radius / 3 * 2,
                                                (int) pt.y - (int) radius / 3 * 2,
                                                (int) pt.x + (int) radius / 3 * 2,
                                                (int) pt.y + (int) radius / 3 * 2);
                                        drawable.setBounds(bounds);
                                        drawable.draw(canvas);
                                        if (false) {
                                            Paint p = new Paint();
                                            p.setStyle(Paint.Style.STROKE);
                                            p.setColor(0xFF0000FF);
                                            canvas.drawRect(0, 0, 300, 400, p);
                                        }
                                    }
                                }





                            }
                        }
                    }
                }
            }

            canvas.restore();
        }

        canvas.restore();
    }

    public enum TOOLS {none, paint, eraser, pan, select}

    //------------
    public Bitmap initialBmp = null;
    public int penEraserBrush = 1; //0==eraser, 1==pen, 2==brush
    public void setBackgroundMode(String mBackgroundMode) {
        this.mBackgroundMode = mBackgroundMode;
        invalidate();
    }
    public String getBackgroundMode() {
        return mBackgroundMode;
    }
    private String mBackgroundMode = FileMeta.NONE;//FabricView.BACKGROUND_STYLE_BLANK;
    private Paint.Style mStyle = Paint.Style.STROKE;
    private float mSize = 5f;
    public void drawBackground(Canvas canvas, String backgroundMode, float w, float h) {
        if (!drawMinimal || drawMinimalBG) {
            //FileMeta.NONE == FabricView.BACKGROUND_STYLE_BLANK
            //FileMeta.LINED == FabricView.BACKGROUND_STYLE_NOTEBOOK_PAPER
            //FileMeta.DOTTED == FabricView.BACKGROUND_STYLE_DOT_PAPER
            //FileMeta.GRAPH == FabricView.BACKGROUND_STYLE_GRAPH_PAPER
            if (backgroundMode != null &&
                    !backgroundMode.equals(FileMeta.NONE)) {
                Paint linePaint = new Paint();
                linePaint.setColor(Color.argb(50, 0, 0, 0));
                linePaint.setStyle(mStyle);
                linePaint.setStrokeJoin(Paint.Join.ROUND);
                linePaint.setStrokeWidth(mSize - 2f);
                switch (backgroundMode) {
                    case FileMeta.GRAPH:
                        drawGraphPaperBackground(canvas, linePaint, w, h);
                        break;

                    case FileMeta.LINED:
                        drawNotebookPaperBackground(canvas, linePaint, w, h);
                        break;

                    case FileMeta.LINED_LONG_DASH:
                        drawNotebookPaperBackgroundLongDash(canvas, linePaint, w, h);
                        break;

                    case FileMeta.LINED_SHORT_DASH:
                        drawNotebookPaperBackgroundShortDash(canvas, linePaint, w, h);
                        break;

                    case FileMeta.DOTTED:
                        drawDotPaperBackground(canvas, linePaint, w, h);
                        break;

                    default:
                        break;
                }
            }
        }
        //FIXME:
        //mRedrawBackground = false;

        //FIXME:added
        if (this.initialBmp != null) {
            canvas.drawBitmap(this.initialBmp, 0, 0, null);
        }
    }

    private final static int LINE_HEIGHT = 20;//Dips.dpToPx(25)
    private final static int DOT_HEIGHT = 1;//Dips.dpToPx(2)
    private final static int LINE_COLOR = 0xFFEBE7E7;
    public static int dpToPx__(final int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    /**
     * Draws a graph paper background on the view
     *
     * @param canvas the canvas to draw on
     * @param paint  the paint to use
     */
    private void drawGraphPaperBackground(Canvas canvas, Paint paint, float w, float h) {
/*
                final paint = Paint()
      ..color = Colors.grey[500].withOpacity(.3)
      ..strokeWidth = 1;
    // 1 because no line at the top
    for (int i = 1; i < size.height / XppPageSize.pt2mm(5); i++) {
      canvas.drawLine(Offset(0, i * XppPageSize.pt2mm(5).toDouble()),
          Offset(size.width, i * XppPageSize.pt2mm(5).toDouble()), paint);
    }
    // 1 because no line at the beginning
    for (int i = 1; i < size.width / XppPageSize.pt2mm(5); i++) {
      canvas.drawLine(Offset(i * XppPageSize.pt2mm(5).toDouble(), 0),
          Offset(i * XppPageSize.pt2mm(5).toDouble(), size.height), paint);
    }
                 */
        //Canvas canvas = new Canvas(bmp);
        //Paint paint = new Paint();
        paint.setColor(LINE_COLOR);//0xFFCCCCCC);
        paint.setStrokeWidth(1);
        paint.setAntiAlias(true);
        paint.setPathEffect(new DashPathEffect(new float[]{5, 5}, 0));
        //1 because no line at the top
        for (int i = 1; i < h / LINE_HEIGHT/* + 1*/; i++) {
            canvas.drawLine(0, i * LINE_HEIGHT,
                    w, i * LINE_HEIGHT, paint);
        }
        // 1 because no line at the beginning
        for (int i = 1; i < w / LINE_HEIGHT/* + 1*/; i++) {
            canvas.drawLine(i * LINE_HEIGHT, 0,
                    i * LINE_HEIGHT, h, paint);
        }
    }

    private void drawDotPaperBackground(Canvas canvas, Paint paint, float w, float h) {
/*
final paint = Paint()
        ..color = Colors.grey[500].withOpacity(.3)
        ..strokeWidth = 1;
// 1 because no line at the top
for (int i = 1; i < size.height / XppPageSize.pt2mm(5); i++) {
    // 1 because no line at the beginning
    for (int j = 1; j < size.width / XppPageSize.pt2mm(5); j++) {
        canvas.drawCircle(
                Offset(j * XppPageSize.pt2mm(5).toDouble(),
                        i * XppPageSize.pt2mm(5).toDouble()),
                XppPageSize.pt2mm(.5).toDouble(),
                paint);
    }
}*/
        //Canvas canvas = new Canvas(bmp);
//        Paint paint = new Paint();
        paint.setColor(LINE_COLOR);//0xFFCCCCCC);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        for (int i = 1; i < h / LINE_HEIGHT/* + 1*/; i++) {
            // 1 because no line at the beginning
            for (int j = 1; j < w / LINE_HEIGHT/* + 1*/; j++) {
                canvas.drawCircle(j * LINE_HEIGHT, i * LINE_HEIGHT, DOT_HEIGHT, paint); //Dips.dpToPx(2)
            }
        }
    }

    /**
     * Draws a notebook paper background on the view
     *
     * @param canvas the canvas to draw on
     * @param paint  the paint to use
     */
    private void drawNotebookPaperBackground(Canvas canvas, Paint paint, float w, float h) {
/*
    final paint = Paint()
      ..color = Colors.grey[500].withOpacity(.3)
      ..strokeWidth = 1;
    // 1 because no line at the top
    for (int i = 1; i < size.height / 24; i++) {
      canvas.drawLine(Offset(0, i * 24.toDouble()),
          Offset(size.width, i * 24.toDouble()), paint);
    }
 */
        //Canvas canvas = new Canvas(bmp);
        //Paint paint = new Paint();
        paint.setColor(LINE_COLOR);//0xFFCCCCCC);
        paint.setStrokeWidth(1);
        paint.setAntiAlias(true);
        // 1 because no line at the top
        for (int i = 1; i < h / LINE_HEIGHT; i++) {
            canvas.drawLine(0, i * LINE_HEIGHT,
                    w, i * LINE_HEIGHT, paint);
        }
    }

    private void drawNotebookPaperBackgroundLongDash(Canvas canvas, Paint paint, float w, float h) {
/*
    final paint = Paint()
      ..color = Colors.grey[500].withOpacity(.3)
      ..strokeWidth = 1;
    // 1 because no line at the top
    for (int i = 1; i < size.height / 24; i++) {
      canvas.drawLine(Offset(0, i * 24.toDouble()),
          Offset(size.width, i * 24.toDouble()), paint);
    }
 */
        //Canvas canvas = new Canvas(bmp);
        //Paint paint = new Paint();
        paint.setColor(LINE_COLOR);//0xFFCCCCCC);
        paint.setStrokeWidth(1);
        paint.setAntiAlias(true);
        paint.setPathEffect(new DashPathEffect(new float[]{20, 20}, 0));
        // 1 because no line at the top
        for (int i = 1; i < h / LINE_HEIGHT; i++) {
            canvas.drawLine(0, i * LINE_HEIGHT,
                    w, i * LINE_HEIGHT, paint);
        }
    }

    private void drawNotebookPaperBackgroundShortDash(Canvas canvas, Paint paint, float w, float h) {
/*
    final paint = Paint()
      ..color = Colors.grey[500].withOpacity(.3)
      ..strokeWidth = 1;
    // 1 because no line at the top
    for (int i = 1; i < size.height / 24; i++) {
      canvas.drawLine(Offset(0, i * 24.toDouble()),
          Offset(size.width, i * 24.toDouble()), paint);
    }
 */
        //Canvas canvas = new Canvas(bmp);
        //Paint paint = new Paint();
        paint.setColor(LINE_COLOR);//0xFFCCCCCC);
        paint.setStrokeWidth(1);
        paint.setAntiAlias(true);
        paint.setPathEffect(new DashPathEffect(new float[]{5, 5}, 0));
        // 1 because no line at the top
        for (int i = 1; i < h / LINE_HEIGHT; i++) {
            canvas.drawLine(0, i * LINE_HEIGHT,
                    w, i * LINE_HEIGHT, paint);
        }
    }

    private int penType = DrawAppearance.PEN_TYPE_0;
    public void setPenType(int penType) {
        this.penType = penType;
    }
    public int getPenType() {
        return this.penType;
    }
    private boolean eraserMode = false;
    public boolean getEraserMode() {
        return eraserMode;
    }
    public void setEraserMode(boolean eraserMode) {
        this.eraserMode = eraserMode;
    }
    private boolean scaleMode = false;
    public boolean getScaleMode() {
        return scaleMode;
    }
    public void setScaleMode(boolean scaleMode) {
        this.scaleMode = scaleMode;
    }
    public void drawText(String text, int textType, float x, float y, Paint p_, boolean isBold,
        boolean isItalics,
        boolean isUnderline,
        int styleType, int pointsTextColor, float pointsTextSize) {
        DrawAppearance appearance = new DrawAppearance(Color.BLACK, -1);
        appearance.loadFromSettings(getContext());
        appearance.penType = DrawAppearance.PEN_TYPE_4; //getPenType();
        // Starts a new line in the path -- whether or not it is closed is taken from the preferences (defaults to false)
        DrawPath currentPath = new DrawPath(null, DrawPath.POINTS_TYPE_TEXT);
        currentPath.isClosed = PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean("drawFilledShapes", false);
        currentPath.appearance = appearance.clone();

        currentPath.pointsType = DrawPath.POINTS_TYPE_TEXT;
        Point mapP = this.mapPoint(x, y, 1.0f);
        currentPath.pointsTextX = mapP.x;
        currentPath.pointsTextY = mapP.y;
        currentPath.pointsText = text;
        currentPath.pointsTextType = textType;
        currentPath.isBold = isBold;
        currentPath.isItalics = isItalics;
        currentPath.isUnderline = isUnderline;
        currentPath.styleType = styleType;
        currentPath.pointsTextColor = pointsTextColor;
        currentPath.pointsTextSize = pointsTextSize;
        if (DrawPath.USE_TEMP_PAINT) {
            currentPath.tempPaint = new Paint(p_);
        }

        {
            Paint p = new Paint(); //FIXME:TODO:remove new
            if (p_ != null) {
                p = p_;
            } else {
                getTextPaint(p, isBold, isItalics, styleType, isUnderline, pointsTextColor, pointsTextSize);
            }
            SizeF size = calculateTextSizes(text, p, textType);
            float w = size.getWidth();
            float h = size.getHeight();
            currentPath.addPoint(this.mapPoint(x, y, 1.0f));
            currentPath.addPoint(this.mapPoint(x + w, y, 1.0f));
            currentPath.addPoint(this.mapPoint(x + w, y + h, 1.0f));
            currentPath.addPoint(this.mapPoint(x, y + h, 1.0f));
            currentPath.addPoint(this.mapPoint(x, y, 1.0f));
            currentPath.cachePath();
            this.paths.add(currentPath);
        }
        if (true) { //FIXME:???
            invalidate();
        }
    }

    public static Spanned fromHtml(String text) {
        Spanned textViewText;
        if (false) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                //Html.FROM_HTML_MODE_COMPACT));//
                textViewText = (Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT));//Html.FROM_HTML_MODE_LEGACY));
            } else {
                textViewText = (Html.fromHtml(text));
            }
        } else {
            //https://nightwhistler.net/HtmlSpanner/
            textViewText = (new HtmlSpanner()).fromHtml(text);
            SpannableStringBuilder builder = new SpannableStringBuilder(textViewText);
            int length = builder.length();
            if (length > 0 && builder.charAt(length - 1) == '\n') {
                builder.delete(length - 1, length);
            }
            textViewText = builder;
        }
        return textViewText;
    }

    private final static String SPLIT_REGXP = "\r\n|\n|\r";
    public static SizeF calculateTextSizes(String text, Paint p, int textType) {
        if (text == null) {
            text = "";
        }
        if (BookActivity4Utils.USE_STATIC_LAYOUT) {
            if (text == null) {
                text = "";
            }
            Spanned textViewText = null;
            if (textType == DrawPath.POINTS_TEXT_TYPE_RICH) {
                textViewText = fromHtml(text);
            }
            TextPaint textPaint = new TextPaint(p);
            float lineSpacingExtra = 0F;
            float lineSpacingMultiplier = 1.0F;
            int availableWidthPixels = BookActivity4Utils.STATIC_LAYOUT_WIDTH;
            CharSequence text_ = text;
            if (textType == DrawPath.POINTS_TEXT_TYPE_RICH && textViewText != null) {
                text_ = textViewText;
            }
            StaticLayout staticLayout =
                    Build.VERSION.SDK_INT >= 23 ?
                            StaticLayout.Builder.obtain(text_, 0, text_.length(), textPaint, availableWidthPixels)
                                    .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                                    .setLineSpacing(lineSpacingExtra, lineSpacingMultiplier)
                                    .setIncludePad(true)
                                    .build() :
                            new StaticLayout(text_,
                                    textPaint,
                                    availableWidthPixels,
                                    Layout.Alignment.ALIGN_NORMAL,
                                    lineSpacingMultiplier,
                                    lineSpacingExtra,
                                    true);
            float maxLineWidth = 0;
            for (int i = 0; i < staticLayout.getLineCount(); i++) {
                float lineWidth = staticLayout.getLineWidth(i);
                if (lineWidth > maxLineWidth) {
                    maxLineWidth = lineWidth;
                }
            }
            return new SizeF(maxLineWidth, staticLayout.getHeight());
        } else {
            if (text == null) {
                text = "";
            }
            String[] lines = text.split(SPLIT_REGXP);
            float tX2 = 0;
            float tY2 = 0;
            for (String line : lines) {
                tX2 = Math.max(getFontlength(p, line), tX2);
                tY2 += getFontHeight(p);
            }
            return new SizeF(tX2, tY2);
        }
    }
    public static void drawTextSizes(Canvas temp, String text, float xcoords, float ycoords, Paint p, int textType) {
        if (text == null) {
            text = "";
        }
        if (!(textType == DrawPath.POINTS_TEXT_TYPE_RICH)) {
            if (text == null) {
                text = "";
            }
            float tW = getFontlength(p, text);
            float tH = getFontHeight(p);
            float tX = xcoords;
            float tY = ycoords + getFontLeading(p);
            String[] lines = text.split(SPLIT_REGXP);
            float tY2 = tY;
            for (String line : lines) {
                temp.drawText(line, tX, tY2, p);
                tY2 += getFontHeight(p);
            }
        } else {
            if (text == null) {
                text = "";
            }
//            Log.e(TAG, "drawText == " + text);
            //BookActivity4Utils.USE_HTML_EDIT
            Spanned textViewText = null;
            textViewText = fromHtml(text);
            TextPaint textPaint = new TextPaint(p);
            float lineSpacingExtra = 0F;
            float lineSpacingMultiplier = 1.0F;
            int availableWidthPixels = BookActivity4Utils.STATIC_LAYOUT_WIDTH;
            StaticLayout staticLayout =
                    Build.VERSION.SDK_INT >= 23 ?
                            StaticLayout.Builder.obtain(textViewText, 0, textViewText.length(), textPaint, availableWidthPixels)
                                    .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                                    .setLineSpacing(lineSpacingExtra, lineSpacingMultiplier)
                                    .setIncludePad(false) //true)
                                    .build() :
                            new StaticLayout(textViewText,
                                    textPaint,
                                    availableWidthPixels,
                                    Layout.Alignment.ALIGN_NORMAL,
                                    lineSpacingMultiplier,
                                    lineSpacingExtra,
                                    false);//true);
            if (false) {
                temp.drawText(textViewText, 0, textViewText.length(), xcoords, ycoords, p);
            } else {
                temp.save();
                temp.translate(xcoords, ycoords);
                staticLayout.draw(temp); //Text(temp);
                temp.restore();
            }
        }
    }
    //package com.immomo.momo.android.util;
    //public class PhotoUtils {
    //andli0626/Android_App_MoMo
    private static float getFontlength(Paint paint, String str) {
        return paint.measureText(str);
    }
    private static float getFontHeight(Paint paint) {
        Paint.FontMetrics fm = paint.getFontMetrics();
        return fm.descent - fm.ascent;
    }
    private static float getFontLeading(Paint paint) {
        Paint.FontMetrics fm = paint.getFontMetrics();
        return fm.leading - fm.ascent;
    }
    //FIXME:TODO: this method need to sync with DrawPath.draw()
    public static void getTextPaint(Paint paint,
                                     boolean isBold,
                                     boolean isItalics,
                                     int styleType,
                                     boolean isUnderline,
                                     int pointsTextColor,
                                     float pointsTextSize
    ) {
        if (paint == null) {
            return;
        }
        paint.reset();
        //paint.setFlags(Paint.FAKE_BOLD_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(28);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL); //FIXME:draw text don't use stroke style
        paint.setAntiAlias(true);
        //text is left top align
        int style = Typeface.NORMAL;
        if (isBold) {
            style |= Typeface.BOLD;
        }
        if (isItalics) {
            style |= Typeface.ITALIC;
        }
        Typeface family = Typeface.DEFAULT;
        if (styleType == BookActivity4Fragment.STYLE_TYPE_NONE) {

        } else if (styleType == BookActivity4Fragment.STYLE_TYPE_HAND) {

        } else if (styleType == BookActivity4Fragment.STYLE_TYPE_SERIF) {
            family = Typeface.SERIF;
        } else if (styleType == BookActivity4Fragment.STYLE_TYPE_SANS) {
            family = Typeface.SANS_SERIF;
        }
        Typeface font = Typeface.create(family, style);
        paint.setTypeface(font);
        if (isUnderline) {
            paint.setUnderlineText(true);
        }
        if (pointsTextColor != 0) {
            paint.setColor(pointsTextColor);
        }
        if (pointsTextSize != 0) {
            paint.setTextSize(pointsTextSize);
        }
    }



    //if x, y are from mapPoint, then needMap = false
    public DrawPath drawImage(int x, int y, int width, int height, Bitmap pic, boolean needMap) {
        //final boolean debug = true;
        //boolean needMap = false;
        DrawAppearance appearance = new DrawAppearance(Color.BLACK, -1);
        appearance.loadFromSettings(getContext());
        appearance.penType = DrawAppearance.PEN_TYPE_4; //getPenType();
        // Starts a new line in the path -- whether or not it is closed is taken from the preferences (defaults to false)
        DrawPath currentPath = new DrawPath(null, DrawPath.POINTS_TYPE_IMAGE);
        currentPath.isClosed = PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean("drawFilledShapes", false);
        currentPath.appearance = appearance.clone();

        currentPath.pointsType = DrawPath.POINTS_TYPE_IMAGE;
        Point mapP = null;
        if (needMap) {
            mapP = this.mapPoint(x, y, 1.0f);
        } else {
            mapP = new Point(x, y);
        }
        currentPath.pointsTextX = mapP.x;
        currentPath.pointsTextY = mapP.y;
        currentPath.pointsBitmap = pic;

        if (true) { //debug) {
            if (needMap) {
                currentPath.addPoint(this.mapPoint(x - width / 2, y - height / 2, 1.0f));
                currentPath.addPoint(this.mapPoint(x + width / 2, y - height / 2, 1.0f));
                currentPath.addPoint(this.mapPoint(x + width / 2, y + height / 2, 1.0f));
                currentPath.addPoint(this.mapPoint(x - width / 2, y + height / 2, 1.0f));
                currentPath.addPoint(this.mapPoint(x - width / 2, y - height / 2, 1.0f));
            } else {
                currentPath.addPoint(new Point(x - width / 2, y - height / 2, 1.0f));
                currentPath.addPoint(new Point(x + width / 2, y - height / 2, 1.0f));
                currentPath.addPoint(new Point(x + width / 2, y + height / 2, 1.0f));
                currentPath.addPoint(new Point(x - width / 2, y + height / 2, 1.0f));
                currentPath.addPoint(new Point(x - width / 2, y - height / 2, 1.0f));
            }
            if (false) {
                currentPath.finalise(); //don't use finalise
            }
            currentPath.cachePath();
            this.paths.add(currentPath);
        } else {
//            currentPath.addPoint(this.mapPoint(x, y));
//            currentPath.cachePath();
            this.paths.add(currentPath);
        }
        if (true) { //FIXME:???
            invalidate();
        }
        return currentPath;
    }
    public int pageIdx;
    public void onPageIdx(int idx, CanvasBoox.OnLoadBitmapListener bitmapLoader, boolean forceReload) {
        if (bitmapLoader != null) {
            if (true) { //if (forceReload || this.pageIdx != idx) {
                this.pageIdx = idx;

                // Clear path list/history
                paths.clear();
                versions.clear();
                oldVersionsSize = versions.size();
                version_index = -1;

                BitmapVector result = bitmapLoader.onLoadBitmap(idx);
                Bitmap newBmp = result.bitmap;
                if (result != null && result.strVecJson != null && result.strVecJson.length() > 0) {
                    this.initialBmp = null;
                    this.loadVecJson(result.strVecJson);
                } else if (result != null) {
                    this.initialBmp = newBmp;
                }

                if (USE_JUMP_PAGE_CENTER) {
                    centerDocument();
                }
                this.invalidate();
                onVersionChanged();
                if (false) BookActivity4Utils.clearRestorePages(mAct);
            }
        }
    }
    public boolean disableCenter = false;

    public static boolean isEmulator() {
        String manufacturer = android.os.Build.MANUFACTURER;
        String product = android.os.Build.PRODUCT;
        return Build.MODEL.startsWith("Android SDK") ||
                Build.DEVICE.contains("emulator") ||
                Build.MODEL.contains("sdk_gphone64_x86_64") ||
                manufacturer.contains("Genymotion") ||
                manufacturer.contains("unknown") ||
                product.contains("google_sdk") ||
                product.contains("sdk_gphone") ||
                product.contains("sdk_x86") ||
                product.contains("vbox86p");
    }

    public PaintTool getPaintTool() {
        return paintTool;
    }
    public SelectionTool getSelectionTool() {
        return selectionTool;
    }
    public PanTool getPanTool() {
        return panTool;
    }
}