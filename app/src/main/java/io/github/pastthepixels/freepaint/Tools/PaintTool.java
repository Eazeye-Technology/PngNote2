package io.github.pastthepixels.freepaint.Tools;

import android.graphics.Color;
import android.util.Log;
import android.view.InputDevice;
import android.view.MotionEvent;

import androidx.preference.PreferenceManager;

import com.txkj.drawingapp.activity.BookActivity4Config;
import com.txkj.drawingapp.activity.BookActivity4Fragment;

import java.util.LinkedList;

import io.github.pastthepixels.freepaint.Graphics.DrawAppearance;
import io.github.pastthepixels.freepaint.Graphics.DrawCanvas;
import io.github.pastthepixels.freepaint.Graphics.DrawPath;
import io.github.pastthepixels.freepaint.Graphics.IDrawCanvas;

public class PaintTool implements Tool {
    private final static boolean DEBUG_EVENT = true;
    private final static String TAG = "PaintTool";

    /**
     * The default appearance. You can change this! (through settings)
     */
    private final DrawAppearance appearance = new DrawAppearance(Color.BLACK, -1);
    private final IDrawCanvas canvas;
    private DrawPath currentPath;

    /**
     * Constructor for PaintTool, which binds itself to a DrawCanvas
     *
     * @param canvas DrawCanvas to bind to
     */
    public PaintTool(IDrawCanvas canvas) {
        this.canvas = canvas;
    }

    /**
     * Returns nothing as we don't draw custom tool paths
     */
    @Override
    public LinkedList<DrawPath> getToolPaths() {
        return null;
    }

    /**
     * Nothing to do when we select the paint tool (required because of <code>Tool</code>)
     */
    public void init() {

    }

    /**
     * When the user puts a finger on the screen, we create a new DrawPath.
     * Then, as they move it, we add each point Android is able to poll to the path.
     * (This also means that path resolution == Android's native touch polling speed)
     * Once the user lifts their finger off the screen, we finalise that path's points.
     *
     * @param event MotionEvent passed from a DrawCanvas
     * @return Boolean return value passed to a DrawCanvas
     */
    public boolean onTouchEvent(MotionEvent event) {
        // Checks for the event that occurs
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                initCurrentPath();
                if (appearance.penType == DrawAppearance.PEN_TYPE_6 &&
                        BookActivity4Fragment.ENABLE_NO_DETECT_SHAPE_PEN) {
                    currentPath.addPoint(canvas.mapPoint(event.getX(), event.getY(), event.getPressure()));
                }
                break;

            case MotionEvent.ACTION_MOVE:
                // Draws line between last point and this point
                if (currentPath == null) {
                    initCurrentPath();
                }
                currentPath.addPoint(canvas.mapPoint(event.getX(), event.getY(), event.getPressure()));
                break;

            case MotionEvent.ACTION_UP:
                if (currentPath != null) {
                    currentPath.finalise();
                    currentPath.cachePath();
                }
                break;

            default:
                return false;
        }
        return true;
    }

    public boolean allowVersionBackup() {
        if (isCancel) {
            return false;
        } else {
            return true;
        }
    }
    public void setCancel() {
        this.isCancel = true;
        if (this.currentPath != null) {
            canvas.getPaths().remove(this.currentPath);
        }
    }

    private boolean isCancel = false;
    private void initCurrentPath() {
        isCancel = false;
        appearance.loadFromSettings(canvas.getContext());
        appearance.penType = canvas.getPenType();
        // Starts a new line in the path -- whether or not it is closed is taken from the preferences (defaults to false)
        currentPath = new DrawPath(null, DrawPath.POINTS_TYPE_STROKE);
        if (BookActivity4Config.USE_NO_POINT_SIMPLIFY) {
            //skip
        }
        if (appearance.penType == DrawAppearance.PEN_TYPE_6) {
            if (BookActivity4Fragment.ENABLE_SHAPE_PEN) {
                //FIXME:
            }
            currentPath.simplificationAmount = 100; //shape pen
        } else {
            currentPath.simplificationAmount = 0;
        }
        //currentPath.simplificationAmount = Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(canvas.getContext()).getString("simplificationAmount", "0"));
        currentPath.isClosed = PreferenceManager.getDefaultSharedPreferences(canvas.getContext()).getBoolean("drawFilledShapes", false);
        currentPath.appearance = appearance.clone();
        canvas.getPaths().add(currentPath);
    }

    public DrawAppearance getAppearance() {
        return appearance;
    }
}
