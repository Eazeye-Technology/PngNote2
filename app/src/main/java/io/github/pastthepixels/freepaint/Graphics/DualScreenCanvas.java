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
import android.view.EinkPWInterface;
import android.view.GestureDetector;
import android.view.InputDevice;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import com.dseink.EinkUtils;
import com.txkj.drawingapp.activity.BookActivity4Config;
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
import java.util.concurrent.CopyOnWriteArrayList;

import io.github.pastthepixels.freepaint.Tools.EraserTool;
import io.github.pastthepixels.freepaint.Tools.PaintTool;
import io.github.pastthepixels.freepaint.Tools.PanTool;
import io.github.pastthepixels.freepaint.Tools.SelectionTool;
import io.github.pastthepixels.freepaint.Tools.Tool;

public class DualScreenCanvas extends View implements IDrawCanvas {
    public Bitmap initialBmp = null;
    public int penEraserBrush = 1; //0==eraser, 1==pen, 2==brush

    public int pageIdx;

    public boolean disableCenter = false;

    public boolean old_gScaleBegin = false;

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

    public CopyOnWriteArrayList<DrawPath> paths = new CopyOnWriteArrayList<>();
    public CopyOnWriteArrayList<DrawPath> getPaths() {
        return paths;
    }
    public int documentColor = Color.WHITE;
    public/*private*/ int version_index = -1;
    public DrawCanvas.TOOLS tool = DrawCanvas.TOOLS.none;

    public Activity mAct = null;
    public Activity getActivity() {
        return mAct;
    }
    EinkPWInterface einkPWInterface = null;

    /**
     * Constructor
     */
    public DualScreenCanvas(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setFocusable(true);
        setFocusableInTouchMode(true);

        if (true) {
            try {
                einkPWInterface = EinkUtils.getEinkPWInterfaceWithView(this);
            } catch (NoSuchMethodError e) {
                einkPWInterface = null;
            }
        }
        //if one view is not drawable, need to xxx.addOnTopView(view)
//        if (einkPWInterface != null) {
//            einkPWInterface.addOnTopView(this);
//        }
    }

    /**
     * Constructor
     */
    public DualScreenCanvas(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Constructor
     */
    public DualScreenCanvas(Context context) {
        this(context, null, 0);
    }

    public void initAct(Activity act) {
        this.mAct = act;
    }

    public void saveFile(Uri uri) throws IOException {

    }

    public synchronized String getVecJson() {
        return null;
    }

    public synchronized void loadVecJson(String strVecJson) {

    }

    public void loadFile(Uri uri) throws IOException {

    }

    public void versionBackup() {

    }

    public LinkedList<DrawPath> cloneDrawPathList(LinkedList<DrawPath> listToClone) {
        return null;
    }
    public CopyOnWriteArrayList<DrawPath> cloneDrawPathList(CopyOnWriteArrayList<DrawPath> listToClone) {
        return null;
    }

    public void undo(boolean skipVersionUndo) {

    }

    public void onVersionChanged() {

    }

    public void redo() {

    }

    public Bitmap toBitmap(boolean isDrawBG) {
        return null;
    }

    public Tool getTool() {
        return null;
    }

    private Tool getTool_(DrawCanvas.TOOLS tool_) {
        return null;
    }

    public void setTool(DrawCanvas.TOOLS tool) {

    }

    public void setIsTyping(boolean isTyping) {

    }

    public Point mapPoint(float x, float y, float pressure) {
        return null;
    }

    public Point mapPointScreen(float x, float y, float pressure) {
        return null;
    }

    public float getScaleFactor() {
        return 0;
    }

    public Point getPosition() {
        return null;
    }

    //------------
    public void setBackgroundMode(String mBackgroundMode) {

    }
    public String getBackgroundMode() {
        return null;
    }

    public void drawBackground(Canvas canvas, String backgroundMode, float w, float h) {

    }

    public static int dpToPx__(final int dp) {
        return 0;
    }

    public void setPenType(int penType) {

    }
    public int getPenType() {
        return 0;
    }
    public boolean getEraserMode() {
        return false;
    }
    public void setEraserMode(boolean eraserMode) {

    }

    public boolean getScaleMode() {
        return false;
    }
    public void setScaleMode(boolean scaleMode) {

    }
    public void drawText(String text, int textType, float x, float y, Paint p_, boolean isBold,
                         boolean isItalics,
                         boolean isUnderline,
                         int styleType, int pointsTextColor, float pointsTextSize) {

    }

    public static Spanned fromHtml(String text) {
        return null;
    }

    public static SizeF calculateTextSizes(String text, Paint p, int textType) {
        return null;
    }
    public static void drawTextSizes(Canvas temp, String text, float xcoords, float ycoords, Paint p, int textType) {

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

    }

    public DrawPath drawImage(int x, int y, int width, int height, Bitmap pic, boolean needMap) {
        return null;
    }
    public void onPageIdx(int idx, CanvasBoox.OnLoadBitmapListener bitmapLoader, boolean forceReload) {

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