package io.github.pastthepixels.freepaint.Graphics;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import com.agsw.FabricView.DrawableObjects.CText;
import com.agsw.FabricView.FabricView;
import com.txkj.drawingapp.activity.BookActivity4;
import com.txkj.notemobile2.book.BookIO;
import com.txkj.notemobile2.colorpicker.Dips;
import com.txkj.notemobile2.ui.CanvasBoox;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Objects;

import io.github.pastthepixels.freepaint.File.SVG;
import io.github.pastthepixels.freepaint.MainActivity;
import io.github.pastthepixels.freepaint.Tools.EraserTool;
import io.github.pastthepixels.freepaint.Tools.PaintTool;
import io.github.pastthepixels.freepaint.Tools.PanTool;
import io.github.pastthepixels.freepaint.Tools.SelectionTool;
import io.github.pastthepixels.freepaint.Tools.Tool;

public final class DrawCanvas extends View {

    public final Paint paint = new Paint();
    // Stores previous "versions" of DrawCanvas.paths you can restore
    // You can move back and forth between this, but every time you create a new change
    // it removes everything after the current index (solving the grandfather paradox, btw)
    public final ArrayList<LinkedList<DrawPath>> versions = new ArrayList<>();
    public final int MAX_VERSIONS = 256;
    public final Point documentSize = new Point(0, 0);
    private final PaintTool paintTool = new PaintTool(this);
    private final EraserTool eraserTool = new EraserTool(this);
    private final PanTool panTool = new PanTool(this);
    private final SelectionTool selectionTool = new SelectionTool(this);
    private final SVG svgHelper = new SVG(this);
    public LinkedList<DrawPath> paths = new LinkedList<>();
    public int documentColor = Color.WHITE;
    private int version_index = -1;
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
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        documentSize.set(
                Float.parseFloat(prefs.getString("documentWidth", "816")),
                Float.parseFloat(prefs.getString("documentHeight", "1056"))
        );
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
        panTool.scaleFactor = (float) ((0.8) * (getWidth() / documentSize.x));
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
        version_index += 1;
    }

    /**
     * Adds touch points when the user touches the screen.
     *
     * @param event The motion event.
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Runs chosenTool.onTouchEvent if it exists, otherwise don't update the screen.
        if (tool == TOOLS.none || !Objects.requireNonNull(getTool()).onTouchEvent(event)) {
            return false;
        } else {
            if (getTool().allowVersionBackup() && event.getAction() == MotionEvent.ACTION_UP) {
                // Remove any edits after the current.
                while (versions.size() > version_index + 1) {
                    versions.remove(versions.size() - 1);
                }
                versions.add(cloneDrawPathList(paths)); // adds to the end ∴ newest changes are at the end of the list
                System.out.println(versions + " " + versions.size());
                if (versions.size() < MAX_VERSIONS - 1) version_index += 1;
                if (versions.size() > MAX_VERSIONS)
                    versions.remove(0); // delete the oldest change if the list has grown too much
            }
            postInvalidate(); // Indicate view should be redrawn
            return true; // Indicate we've consumed the touch
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

    /**
     * Undoes an operation by resetting DrawCanvas.paths to what it looked like after the previous operation.
     */
    public void undo() {
        if (version_index > 0) {
            System.out.println(versions.toString() + (version_index - 1));
            version_index -= 1;
            paths = cloneDrawPathList(versions.get(version_index));
        } else {
            version_index = -1;
            paths.clear();
        }
        // Force redraw
        postInvalidate();
        // Re-initialise tools
        if (tool == TOOLS.eraser) getTool().init();
    }

    /**
     * Redoes an operation by setting DrawCanvas.paths to what it looked like after an operation you undid to.
     */
    public void redo() {
        if (version_index < versions.size() - 1) {
            version_index += 1;
            paths = cloneDrawPathList(versions.get(version_index));
        } else if (version_index <= 0 && !versions.isEmpty()) {
            version_index = 0;
            paths = cloneDrawPathList(versions.get(0));
        }
        // Force redraw
        postInvalidate();
        // Re-initialise tools
        if (tool == TOOLS.eraser) getTool().init();
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
    public Point mapPoint(float x, float y) {
        return new Point(
                (x / panTool.scaleFactor) - panTool.offset.x - panTool.panOffset.x,
                (y / panTool.scaleFactor) - panTool.offset.y - panTool.panOffset.y
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
        if (getContext() instanceof MainActivity) {
            ((MainActivity) getContext()).updateInfoBar();
        } else if (getContext() instanceof BookActivity4) {
            ((BookActivity4) getContext()).updateInfoBar();
        }
        // Draws things on the screen
        canvas.save();
        // SCALES, THEN TRANSLATES (translations are independent of scales)
        if (!drawMinimal) {
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
        // Draws a stroke for the page
        if (!drawMinimal) {
            paint.setColor(Color.GRAY);
            paint.setStrokeWidth(5 / panTool.scaleFactor); // Always five pixels no matter scale
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawRect(0, 0, documentSize.x, documentSize.y, paint);
            paint.reset();
        }
        drawBackground(canvas, mBackgroundMode, documentSize.x, documentSize.y);
        // Draws every path, then tool path
        for (DrawPath path : paths) {
            paint.reset();
            path.draw(canvas, paint, screenDensity, getScaleFactor());
        }
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
                path.draw(canvas, paint, screenDensity, getScaleFactor());
            }
        }

        canvas.restore();
    }

    public enum TOOLS {none, paint, eraser, pan, select}

    //------------
    public Bitmap initialBmp = null;
    public int penEraserBrush = 1; //0==eraser, 1==pen, 2==brush
    public void setBackgroundMode(int mBackgroundMode) {
        this.mBackgroundMode = mBackgroundMode;
        invalidate();
    }
    public int getBackgroundMode() {
        return mBackgroundMode;
    }
    private int mBackgroundMode = FabricView.BACKGROUND_STYLE_BLANK;
    private Paint.Style mStyle = Paint.Style.STROKE;
    private float mSize = 5f;
    public void drawBackground(Canvas canvas, int backgroundMode, float w, float h) {
        if (!drawMinimal || drawMinimalBG) {
            if (backgroundMode != FabricView.BACKGROUND_STYLE_BLANK) {
                Paint linePaint = new Paint();
                linePaint.setColor(Color.argb(50, 0, 0, 0));
                linePaint.setStyle(mStyle);
                linePaint.setStrokeJoin(Paint.Join.ROUND);
                linePaint.setStrokeWidth(mSize - 2f);
                switch (backgroundMode) {
                    case FabricView.BACKGROUND_STYLE_GRAPH_PAPER:
                        drawGraphPaperBackground(canvas, linePaint, w, h);
                        break;

                    case FabricView.BACKGROUND_STYLE_NOTEBOOK_PAPER:
                        drawNotebookPaperBackground(canvas, linePaint, w, h);
                        break;

                    case FabricView.BACKGROUND_STYLE_DOT_PAPER:
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
        paint.setColor(0xFFCCCCCC);
        paint.setStrokeWidth(1);
        paint.setAntiAlias(true);
        //1 because no line at the top
        for (int i = 1; i < h / Dips.dpToPx(25)/* + 1*/; i++) {
            canvas.drawLine(0, i * Dips.dpToPx(25),
                    w, i * Dips.dpToPx(25), paint);
        }
        // 1 because no line at the beginning
        for (int i = 1; i < w / Dips.dpToPx(25)/* + 1*/; i++) {
            canvas.drawLine(i * Dips.dpToPx(25), 0,
                    i * Dips.dpToPx(25), h, paint);
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
        paint.setColor(0xFFCCCCCC);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        for (int i = 1; i < h / Dips.dpToPx(25)/* + 1*/; i++) {
            // 1 because no line at the beginning
            for (int j = 1; j < w / Dips.dpToPx(25)/* + 1*/; j++) {
                canvas.drawCircle(j * Dips.dpToPx(25), i * Dips.dpToPx(25), Dips.dpToPx(2), paint); //Dips.dpToPx(2)
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
        paint.setColor(0xFFCCCCCC);
        paint.setStrokeWidth(1);
        paint.setAntiAlias(true);
        // 1 because no line at the top
        for (int i = 1; i < h / Dips.dpToPx(25); i++) {
            canvas.drawLine(0, i * Dips.dpToPx(25),
                    w, i * Dips.dpToPx(25), paint);
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
    public void drawText(String text, int x, int y, Paint p, boolean isBold,
        boolean isItalics,
        boolean isUnderline,
        int styleType, int pointsTextColor) {
        if (p == null) {
            int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 20, getContext().getResources().getDisplayMetrics());
            p = new Paint();
            p.setTextSize(px);
            p.setColor(Color.BLACK);
        }
        final boolean debug = true;
        DrawAppearance appearance = new DrawAppearance(Color.BLACK, -1);
        appearance.loadFromSettings(getContext());
        appearance.penType = DrawAppearance.PEN_TYPE_4; //getPenType();
        // Starts a new line in the path -- whether or not it is closed is taken from the preferences (defaults to false)
        DrawPath currentPath = new DrawPath(null, DrawPath.POINTS_TYPE_TEXT);
        currentPath.isClosed = PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean("drawFilledShapes", false);
        currentPath.appearance = appearance.clone();

        currentPath.pointsType = DrawPath.POINTS_TYPE_TEXT;
        Point mapP = this.mapPoint(x, y);
        currentPath.pointsTextX = mapP.x;
        currentPath.pointsTextY = mapP.y;
        currentPath.pointsText = text;
        currentPath.isBold = isBold;
        currentPath.isItalics = isItalics;
        currentPath.isUnderline = isUnderline;
        currentPath.styleType = styleType;
        currentPath.pointsTextColor = pointsTextColor;


            if (debug) {
            currentPath.addPoint(this.mapPoint(x, y));
            currentPath.addPoint(this.mapPoint(x + 100, y));
            currentPath.addPoint(this.mapPoint(x + 100, y + 100));
            currentPath.addPoint(this.mapPoint(x, y + 100));
            currentPath.addPoint(this.mapPoint(x, y));
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
    }
    public void drawImage(int x, int y, int width, int height, Bitmap pic, boolean needMap) {
        final boolean debug = true;
        DrawAppearance appearance = new DrawAppearance(Color.BLACK, -1);
        appearance.loadFromSettings(getContext());
        appearance.penType = DrawAppearance.PEN_TYPE_4; //getPenType();
        // Starts a new line in the path -- whether or not it is closed is taken from the preferences (defaults to false)
        DrawPath currentPath = new DrawPath(null, DrawPath.POINTS_TYPE_TEXT);
        currentPath.isClosed = PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean("drawFilledShapes", false);
        currentPath.appearance = appearance.clone();

        currentPath.pointsType = DrawPath.POINTS_TYPE_IMAGE;
        Point mapP = null;
        if (needMap) {
            mapP = this.mapPoint(x, y);
        } else {
            mapP = new Point(x, y);
        }
        currentPath.pointsTextX = mapP.x;
        currentPath.pointsTextY = mapP.y;
        currentPath.pointsBitmap = pic;

        if (debug) {
            currentPath.addPoint(this.mapPoint(x, y));
            currentPath.addPoint(this.mapPoint(x + width, y));
            currentPath.addPoint(this.mapPoint(x + width, y + height));
            currentPath.addPoint(this.mapPoint(x, y + height));
            currentPath.addPoint(this.mapPoint(x, y));
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
    }
    public int pageIdx;
    public void onPageIdx(int idx, CanvasBoox.OnLoadBitmapListener bitmapLoader) {
        if (bitmapLoader != null) {
            if (this.pageIdx != idx) {
                this.pageIdx = idx;
                Bitmap newBmp = bitmapLoader.onLoadBitmap(idx);
                this.initialBmp = newBmp;

                // Clear path list/history
                paths.clear();
                versions.clear();
                version_index = -1;

                this.invalidate();
            }
        }
    }
    public boolean disableCenter = false;
}