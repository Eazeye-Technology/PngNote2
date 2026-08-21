package io.github.pastthepixels.freepaint.Graphics;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.net.Uri;
import android.text.Spanned;
import android.util.AttributeSet;
import android.util.SizeF;
import android.view.EinkPWInterface;
import android.view.PWDrawObjectHandler;
import android.view.View;

import androidx.annotation.NonNull;

import com.dseink.EinkUtils;
import com.txkj.notemobile2.colorpicker.FileMeta;
import com.txkj.notemobile2.ui.CanvasBoox;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.CopyOnWriteArrayList;

import io.github.pastthepixels.freepaint.Tools.EraserTool;
import io.github.pastthepixels.freepaint.Tools.PaintTool;
import io.github.pastthepixels.freepaint.Tools.PanTool;
import io.github.pastthepixels.freepaint.Tools.SelectionTool;
import io.github.pastthepixels.freepaint.Tools.Tool;

public class DualScreenCanvas extends View implements IDrawCanvas {
    public void setStrokeSize(int strokeSize) {
        if (einkPWInterface != null) {
            einkPWInterface.setPenSettingWidth(strokeSize);
        }
    }

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
    public EinkPWInterface einkPWInterface = null;

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
        if (einkPWInterface != null) {
            einkPWInterface.unDo();
        }
    }

    public void onVersionChanged() {

    }

    public void redo() {
        if (einkPWInterface != null) {
            einkPWInterface.reDo();
        }
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
    private String mBackgroundMode = FileMeta.NONE;//FabricView.BACKGROUND_STYLE_BLANK;
    public void setBackgroundMode(String mBackgroundMode) {
        this.mBackgroundMode = mBackgroundMode;
        invalidate();
    }
    public String getBackgroundMode() {
        return mBackgroundMode;
    }

    private int mPenType = DrawAppearance.PEN_TYPE_0;
    public void setPenType(int penType) {
        this.mPenType = penType;
        if (einkPWInterface != null) {
            if (penType == DrawAppearance.PEN_TYPE_1) { //pen1 pen
                einkPWInterface.setDrawObjectType(PWDrawObjectHandler.DRAW_OBJ_RANDOM_PEN);
            } else if (penType == DrawAppearance.PEN_TYPE_2) {
                einkPWInterface.setDrawObjectType(PWDrawObjectHandler.DRAW_OBJ_RANDOM_PEN);
            } else if (penType == DrawAppearance.PEN_TYPE_3) { //highlight //pen2 mark
                einkPWInterface.setDrawObjectType(PWDrawObjectHandler.DRAW_OBJ_RANDOM_MARK);
            } else if (penType == DrawAppearance.PEN_TYPE_4) { //pen3 ball
                einkPWInterface.setDrawObjectType(PWDrawObjectHandler.DRAW_OBJ_RANDOM_PENCIL);
            } else if (penType == DrawAppearance.PEN_TYPE_5) {
                einkPWInterface.setDrawObjectType(PWDrawObjectHandler.DRAW_OBJ_RANDOM_PEN);
            } else if (penType == DrawAppearance.PEN_TYPE_6) { //pen4 shape
                einkPWInterface.setDrawObjectType(PWDrawObjectHandler.DRAW_OBJ_RANDOM_PEN);
            }
        }
    }
    public int getPenType() {
        return this.mPenType;
    }
    private boolean mIsEraserMode = false;
    public boolean getEraserMode() {
        return this.mIsEraserMode;
    }
    public void setEraserMode(boolean eraserMode) {
        this.mIsEraserMode = eraserMode;
        if (eraserMode) {
            einkPWInterface.setDrawObjectType(PWDrawObjectHandler.DRAW_OBJ_CHOICERASE);
        } else {
            setPenType(mPenType);
        }
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



















    //---------------
    protected void onDraw(@NonNull Canvas canvas) {
        // Allows us to do things like setting a custom background
        if (!drawMinimal) {
            super.onDraw(canvas);
        }
        canvas.drawColor(0xFFFFFFFF);
        drawBackground(canvas, mBackgroundMode, documentSize.x, documentSize.y);
    }


    // Drawing flags
    // Draws only the document, without any tool paths, or any rotation/translation.
    private boolean drawMinimal = false;
    private boolean drawMinimalBG = false;

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
}