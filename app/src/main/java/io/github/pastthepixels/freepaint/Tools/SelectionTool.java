package io.github.pastthepixels.freepaint.Tools;

import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.Drawable;
import android.util.SizeF;
import android.view.MotionEvent;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.txkj.drawingapp.R;
import com.txkj.drawingapp.activity.BookActivity4Utils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import io.github.pastthepixels.freepaint.Graphics.DrawAppearance;
import io.github.pastthepixels.freepaint.Graphics.DrawCanvas;
import io.github.pastthepixels.freepaint.Graphics.DrawPath;
import io.github.pastthepixels.freepaint.Graphics.Point;
import io.github.pastthepixels.freepaint.Utils;

public class SelectionTool implements Tool {
    public final static boolean HIDE_DELETE_BUTTON = true;
    private final static boolean SELECT_REGION_NO_CLIP = true;
    //stop text being deleted, see also region.setPath(p, null);

    //可变，需要用clone
    private final DrawAppearance APPEARANCE = new DrawAppearance(Color.GRAY, Color.argb(32, 64, 64, 64));

    //不可变
    public final DrawAppearance APPEARANCE_SELECTED = new DrawAppearance(Color.BLUE, -1);

    private final LinkedList<DrawPath> toolPaths = new LinkedList<>();
    private final LinkedList<DrawPath> selectedPaths = new LinkedList<>();

    public final DrawPath currentPath = new DrawPath(null, DrawPath.POINTS_TYPE_STROKE);
    private final DrawCanvas canvas;
    public Point originalPoint = new Point(0, 0);
    public Point previousPoint = null;
    boolean changedDrawPaths = false;
    public TOUCH_MODES mode;

    public Point scalePoint;

    public Drawable doneIcon;
    public final static int doneIcon_index = 0;

    public Drawable deleteIcon;
    public final static int deleteIcon_index = 1;

    public Drawable zoomIcon;
    public final static int zoomIcon_index = 2;

    public Drawable editIcon;
    public final static int editIcon_index = 3;

    public float iconRadius = 10;

    /**
     * Creates a SelectionTool instance, saying that the selection path has to be closed (it's a rectangle)
     *
     * @param canvas The DrawCanvas to bind to
     */
    public SelectionTool(DrawCanvas canvas) {
        this.canvas = canvas;
        currentPath.isClosed = true;
        APPEARANCE.useDP = APPEARANCE_SELECTED.useDP = true;
        APPEARANCE.strokeSize = APPEARANCE_SELECTED.strokeSize = 3;
        APPEARANCE_SELECTED.effect = DrawAppearance.EFFECTS.dashed;

        deleteIcon = ContextCompat.getDrawable(canvas.getContext(), R.drawable.ic_close_white_20dp);
//        if (deleteIcon != null) {
//            deleteIcon.setAlpha(255);
//            //deleteIcon.setColorFilter(0xFF000000, PorterDuff.Mode.SRC_ATOP);
//        }
        doneIcon = ContextCompat.getDrawable(canvas.getContext(), R.drawable.ic_done_white_20dp);
        zoomIcon = ContextCompat.getDrawable(canvas.getContext(), R.drawable.ic_rotate_scale_white_17dp);
        editIcon = ContextCompat.getDrawable(canvas.getContext(), R.drawable.ic_baseline_edit_24_white); //R.drawable.ic_flip_white_20dp);
    }

    /**
     * Returns a list of paths entirely used by the tool for visual aid purposes
     * (e.g. showing selected paths) so that it can be drawn by a DrawCanvas
     *
     * @return A list of paths for the DrawCanvas to draw
     */
    @Override
    public LinkedList<DrawPath> getToolPaths() {
        return toolPaths;
    }

    /**
     * Every time we select the selection tool (heh), it clears the previous selection.
     * One of the reasons for doing this is that if we selected a path and its shape changed/it's no longer there,
     * we can be lazy and don't have to recompute a bounding box or check if it's still there.
     */
    public void init() {
        selectedPaths.clear();
        currentPath.clear();
        toolPaths.clear();
        toolPaths.add(currentPath);
    }

    /**
     * When the user touches the canvas while the tool is selected:
     * (1) Create a new selection if the touch point is outside the current
     * (1a) When the user lifts their finger, resize the selection square to fit the selection,
     * effectively repurposing the square from showing the region to select to showing the
     * bounds of the selected paths.
     * (2) If the touch point is in in the selected square, move the selection.
     * (3) TODO: If the touch point is on the edges of the square (draw circle "handles" that can be used to determine this), scale the selection.
     *
     * @param event MotionEvent passed from the DrawCanvas
     * @return Boolean return value passed to the DrawCanvas
     */
    public boolean onTouchEvent(MotionEvent event) {
        // Checks for the event that occurs
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                changedDrawPaths = false;
                // If the touch action is outside the currently selected rectangle, we're not trying to manipulate it
                // -- we're trying to make a new one
                originalPoint = canvas.mapPoint(event.getX(), event.getY(), event.getPressure());

                if (true) {
                    int icon = findCurrentIconTouched(originalPoint.x, originalPoint.y);
                    if (icon == deleteIcon_index) { //
                        //Toast.makeText(canvas.getContext(), "deleteIcon", Toast.LENGTH_SHORT).show();
                        if (HIDE_DELETE_BUTTON) {

                        } else {
                            for (DrawPath path : selectedPaths) {
                                path.erasePath();
                            }
                            currentPath.clear();
                        }
                        break;
                    } else if (icon == doneIcon_index) {
                        currentPath.appearance = APPEARANCE.clone();
                        mode = TOUCH_MODES.none;
                        selectedPaths.clear();
                        currentPath.clear();
                        break;
                    } else if (icon == zoomIcon_index) {
                        //FIXME:暂时不允许多个对象缩放
                        if (selectedPaths.size() == 1) {
                            DrawPath path = selectedPaths.get(0);
                            if (path != null &&
                                    (path.pointsType == DrawPath.POINTS_TYPE_STROKE || //allow stroke scale
                                            path.pointsType == DrawPath.POINTS_TYPE_IMAGE ||
                                            path.pointsType == DrawPath.POINTS_TYPE_TEXT)) {
                                isIconDrag = true;
                                path.setScaleBegin();
//                                this.downMatrix.set(path.getMatrix());
                                if (path.pointsType == DrawPath.POINTS_TYPE_STROKE) {
                                    //path.pointsType == DrawPath.POINTS_TYPE_STROKE, need to calculate
                                    this.midPoint.set(
                                            //see path.setScaleBegin();
                                            (path.tempPointXMin + path.tempPointXMax) / 2.0F,
                                            (path.tempPointYMin + path.tempPointYMax) / 2.0F);
                                } else {
                                    //DrawPath.POINTS_TYPE_IMAGE is center point, so must scale with center point
                                    //DrawPath.POINTS_TYPE_TEXT is left top, so must scale with left top point
                                    this.midPoint.set(path.pointsTextX, path.pointsTextY);
                                }
                            } else {
//                                this.downMatrix.set(new Matrix());
                                this.midPoint.set(0.0F, 0.0F);
                            }
                            this.oldDistance = this.calculateDistance(this.midPoint.x, this.midPoint.y,
                                    originalPoint.x, originalPoint.y);
                        } else if (selectedPaths.size() > 1) {
                            //only all strokes can scale
                            boolean allStroke = true;
                            if (DrawPath.ALLOW_MULTI_SCALE) {
                                //skip
                            } else {
                                for (DrawPath itemPath : selectedPaths) {
                                    if (itemPath == null ||
                                            itemPath.pointsType != DrawPath.POINTS_TYPE_STROKE) {
                                        allStroke = false;
                                        break;
                                    }
                                }
                            }
                            if (allStroke) {
                                float tempPointXMin = 0;
                                float tempPointXMax = 0;
                                float tempPointYMin = 0;
                                float tempPointYMax = 0;
                                for (int i = 0; i < selectedPaths.size(); ++i) {
                                    DrawPath itemPath = selectedPaths.get(i);
                                    if (itemPath != null) {
                                        itemPath.setScaleBegin();
                                        if (i == 0) {
                                            tempPointXMin = itemPath.tempPointXMin;
                                            tempPointXMax = itemPath.tempPointXMax;
                                            tempPointYMin = itemPath.tempPointYMin;
                                            tempPointYMax = itemPath.tempPointYMax;
                                        } else {
                                            tempPointXMin = Math.min(tempPointXMin, itemPath.tempPointXMin);
                                            tempPointXMax = Math.max(tempPointXMax, itemPath.tempPointXMax);
                                            tempPointYMin = Math.min(tempPointYMin, itemPath.tempPointYMin);
                                            tempPointYMax = Math.max(tempPointYMax, itemPath.tempPointYMax);
                                        }
                                    }
                                }
                                this.midPoint.set(
                                        //see path.setScaleBegin();
                                        (tempPointXMin + tempPointXMax) / 2.0F,
                                        (tempPointYMin + tempPointYMax) / 2.0F);
                                isIconDrag = true;
                                isIconDragMulti = true;
                                this.oldDistance = this.calculateDistance(this.midPoint.x, this.midPoint.y,
                                        originalPoint.x, originalPoint.y);
                            }
                        }
                        break;
                    } else if (icon == editIcon_index) {
                        if (selectedPaths.size() == 1) {
                            DrawPath path = selectedPaths.get(0);
                            if (path != null && path.pointsType == DrawPath.POINTS_TYPE_TEXT) {
                                path.tempHidden = true;
                                this.canvas.invalidate(); //hide path
                                this.canvas.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        editText(path);
                                    }
                                }, 200);
                            }
                        }
                        currentPath.clear();
                        currentPath.cachePath();
                        canvas.invalidate();
                        break;
                    }
                }
                if (!currentPath.contains(originalPoint)) {
                    currentPath.appearance = APPEARANCE.clone();
                    mode = TOUCH_MODES.define;
                    selectedPaths.clear();
                    currentPath.clear();

                    {
                        //FIXME:added, for point selection
                        if (mode == TOUCH_MODES.define) {
                            // If we're trying to define a new selection, redraw the current path with the bounds
                            currentPath.clear();
                            currentPath.addPoint(originalPoint);
                            currentPath.addPoint(new Point(originalPoint.x + 1, originalPoint.y));
                            currentPath.addPoint(new Point(originalPoint.x + 1, originalPoint.y + 1));
                            currentPath.addPoint(new Point(originalPoint.x, originalPoint.y + 1));
                            currentPath.cachePath();
                        }
                    }
                } else {
                    //FIXME:要在区域内才能移动，或者更改这个条件
                    mode = TOUCH_MODES.move;
                    previousPoint = null;
                    {
                        currentPath.translateBegin();
                        for (DrawPath path : selectedPaths) {
                            path.translateBegin();
                            //path.cachePath();
                        }
                    }

                    if (getScaleMode()) {
//                    Point touchPoint = canvas.mapPoint(event.getX(), event.getY());
//                    scalePoint = touchPoint;
                        currentPath.beginScale();
                        for (DrawPath path : selectedPaths) {
                            path.beginScale();
                        }
                    }
                }
                break;

            case MotionEvent.ACTION_MOVE:
                Point touchPoint = canvas.mapPoint(event.getX(), event.getY(), event.getPressure());
                if (isIconDrag) {
                    //drag icon to scale
                    zoomAndRotateSticker(touchPoint.x, touchPoint.y);
                    rebuildStrokeSelectFrame();
                    rebuildMultiStrokesSelectFrame(touchPoint.x, touchPoint.y);
                    rebuildImageSelectFrame();
                    rebuildTextSelectFrame();
                } else {
                    //drag empty to select
                    if (mode == TOUCH_MODES.define) {
                        // If we're trying to define a new selection, redraw the current path with the bounds
                        currentPath.clear();
                        currentPath.addPoint(originalPoint);
                        currentPath.addPoint(new Point(touchPoint.x, originalPoint.y));
                        currentPath.addPoint(touchPoint);
                        currentPath.addPoint(new Point(originalPoint.x, touchPoint.y));
                    }
                    //drag object to move
                    if (mode == TOUCH_MODES.move && previousPoint != null) { //FIXME:???
                        if (getScaleMode()) {
                            if (scalePoint != null) {
                                currentPath.scale(scalePoint, touchPoint.clone().applySubtract(scalePoint));
                                for (DrawPath path : selectedPaths) {
                                    path.scale(scalePoint, touchPoint.clone().applySubtract(scalePoint));
                                    path.cachePath();
                                }
                            }
                        } else {
                            // If we're trying to move all the paths we selected... well, move them!
                            changedDrawPaths = true;
                            if (false) {
                                currentPath.translate(touchPoint.clone().applySubtract(previousPoint));
                                for (DrawPath path : selectedPaths) {
                                    path.translate(touchPoint.clone().applySubtract(previousPoint));
                                    path.cachePath();
                                }
                            } else {
                                if (false) {
                                    currentPath.translateSave(touchPoint.clone().applySubtract(originalPoint));
                                } else {
                                    rebuildImageSelectFrame();
                                    rebuildTextSelectFrame();
                                    if (true) {
                                        DrawPath pathText = null;
                                        if (selectedPaths.size() == 1) {
                                            pathText = selectedPaths.get(0);
                                        }
                                        if (selectedPaths.size() > 1 ||
                                                (pathText != null && pathText.pointsType == DrawPath.POINTS_TYPE_STROKE)) {
                                            currentPath.translateSave(touchPoint.clone().applySubtract(originalPoint));
                                        }
                                    }
                                }

                                for (DrawPath path : selectedPaths) {
                                    path.translateSave(touchPoint.clone().applySubtract(originalPoint));
                                    path.cachePath();
                                }
                            }
                        }
                    }
                }
                // Important for second if statement
                previousPoint = touchPoint.clone();
                break;

            case MotionEvent.ACTION_UP:
                isIconDrag = false;
                isIconDragMulti = false;
                if (mode == TOUCH_MODES.define) {
                    // If we're releasing our finger from selecting a bunch of paths, we need to
                    // do math to actually select those paths.
                    selectPaths();
                    currentPath.appearance = APPEARANCE_SELECTED;
                } else {
                    //---------------
                    if (getScaleMode()) {
                        if (mode == TOUCH_MODES.move) {
                            scalePoint = null;
                            if (currentPath != null) {
                                currentPath.endScale();
                            }
                        }
                    }
                    //---------------
                }
                mode = TOUCH_MODES.none;
                previousPoint = null;
                break; // Usually we would say we consumed the input and we shouldn't do a redraw
            // but this is also when we lift our finger a.k.a when we make backups of
            // DrawCanvas.drawPaths.

            default:
                return false;
        }
        return true;
    }

    private void editText(DrawPath path) {
        BookActivity4Utils.editText(this.canvas.mAct, path);
    }

    private void rebuildStrokeSelectFrame() {
        float w = 0;
        float h = 0;
        DrawPath pathStroke = null;
        if (selectedPaths.size() == 1) {
            pathStroke = selectedPaths.get(0);
        }
        if (pathStroke != null && pathStroke.pointsType == DrawPath.POINTS_TYPE_STROKE) {
            Matrix matrix = new Matrix();
            matrix.setScale(
                    pathStroke.tempScaleX, //see setScaleBegin()
                    pathStroke.tempScaleY,
                    (pathStroke.tempPointXMin + pathStroke.tempPointXMax) / 2,
                    (pathStroke.tempPointYMin + pathStroke.tempPointYMax) / 2);
            Point boundsTop_old = new Point(
                    pathStroke.tempPointXMin,
                    pathStroke.tempPointYMin);
            Point boundsBottom_old = new Point(
                    pathStroke.tempPointXMax,
                    pathStroke.tempPointYMax);
            float[] dst = new float[2];
            matrix.mapPoints(dst, new float[]{boundsTop_old.x, boundsTop_old.y});
            Point boundsTop = new Point(dst[0], dst[1]);
            matrix.mapPoints(dst, new float[]{boundsBottom_old.x, boundsBottom_old.y});
            Point boundsBottom = new Point(dst[0], dst[1]);
            this.currentPath.clear();
            this.currentPath.addPoint(boundsTop);
            this.currentPath.addPoint(new Point(boundsBottom.x, boundsTop.y));
            this.currentPath.addPoint(boundsBottom);
            this.currentPath.addPoint(new Point(boundsTop.x, boundsBottom.y));
            this.currentPath.appearance =
                    this.canvas.getSelectionTool().APPEARANCE_SELECTED;
        }
    }
    private void rebuildMultiStrokesSelectFrame(float x, float y) {
        if (selectedPaths.size() > 1) {
            boolean allStroke = true;
            if (DrawPath.ALLOW_MULTI_SCALE) {
                //skip
            } else {
                for (DrawPath itemPath : selectedPaths) {
                    if (itemPath == null ||
                            itemPath.pointsType != DrawPath.POINTS_TYPE_STROKE) {
                        allStroke = false;
                        break;
                    }
                }
            }
            if (allStroke/* && this.oldDistance != 0*/) {
//                float newDistance = this.calculateDistance(this.midPoint.x, this.midPoint.y, x, y);
//                float tempScaleX = newDistance / this.oldDistance;
//                float tempScaleY = newDistance / this.oldDistance;

                float tempPointXMin = 0;
                float tempPointXMax = 0;
                float tempPointYMin = 0;
                float tempPointYMax = 0;
                boolean isInit = false;
                for (int i = 0; i < selectedPaths.size(); ++i) {
                    DrawPath itemPath = selectedPaths.get(i);
                    if (itemPath != null) {
                        if (DrawPath.ALLOW_MULTI_SCALE) {
                            if (itemPath.pointsType == DrawPath.POINTS_TYPE_IMAGE) {
                                float w = itemPath.pointsBitmap != null ? itemPath.pointsBitmap.getWidth() : 0;
                                float h = itemPath.pointsBitmap != null ? itemPath.pointsBitmap.getHeight() : 0;
                                if (!isInit) {
                                    tempPointXMin = itemPath.pointsTextX - itemPath.pointsScaleX * w / 2.0F;
                                    tempPointXMax = itemPath.pointsTextX + itemPath.pointsScaleX * w / 2.0F;
                                    tempPointYMin = itemPath.pointsTextY - itemPath.pointsScaleX * h / 2.0F;
                                    tempPointYMax = itemPath.pointsTextY + itemPath.pointsScaleX * h / 2.0F;
                                    isInit = true;
                                } else {
                                    tempPointXMin = Math.min(tempPointXMin, itemPath.pointsTextX - itemPath.pointsScaleX * w / 2.0F);
                                    tempPointXMax = Math.max(tempPointXMax, itemPath.pointsTextX + itemPath.pointsScaleX * w / 2.0F);
                                    tempPointYMin = Math.min(tempPointYMin, itemPath.pointsTextY - itemPath.pointsScaleX * h / 2.0F);
                                    tempPointYMax = Math.max(tempPointYMax, itemPath.pointsTextY + itemPath.pointsScaleX * h / 2.0F);
                                }
                            } else if (itemPath.pointsType == DrawPath.POINTS_TYPE_TEXT) {
                                float w = 0;
                                float h = 0;
                                if (itemPath.pointsText != null) {
                                    Paint p = new Paint();
                                    DrawCanvas.getTextPaint(p,
                                            itemPath.isBold,
                                            itemPath.isItalics,
                                            itemPath.styleType,
                                            itemPath.isUnderline,
                                            itemPath.pointsTextColor,
                                            itemPath.pointsTextSize);
                                    SizeF size = DrawCanvas.calculateTextSizes(itemPath.pointsText, p, itemPath.pointsTextType);
                                    w = size.getWidth();
                                    h = size.getHeight();
                                }
                                if (!isInit) {
                                    tempPointXMin = itemPath.pointsTextX;
                                    tempPointXMax = itemPath.pointsTextX + itemPath.pointsScaleX * w ;
                                    tempPointYMin = itemPath.pointsTextY;
                                    tempPointYMax = itemPath.pointsTextY + itemPath.pointsScaleX * h;
                                    isInit = true;
                                } else {
                                    tempPointXMin = Math.min(tempPointXMin, itemPath.pointsTextX);
                                    tempPointXMax = Math.max(tempPointXMax, itemPath.pointsTextX + itemPath.pointsScaleX * w);
                                    tempPointYMin = Math.min(tempPointYMin, itemPath.pointsTextY);
                                    tempPointYMax = Math.max(tempPointYMax, itemPath.pointsTextY + itemPath.pointsScaleX * h);
                                }
                            }
                        }
                        if (itemPath.pointsType == DrawPath.POINTS_TYPE_STROKE) {
                            for (Point pt : itemPath.points) {
                                if (pt != null) {
                                    if (!isInit) {
                                        tempPointXMin = pt.x;
                                        tempPointXMax = pt.x;
                                        tempPointYMin = pt.y;
                                        tempPointYMax = pt.y;
                                        isInit = true;
                                    } else {
                                        tempPointXMin = Math.min(tempPointXMin, pt.x);
                                        tempPointXMax = Math.max(tempPointXMax, pt.x);
                                        tempPointYMin = Math.min(tempPointYMin, pt.y);
                                        tempPointYMax = Math.max(tempPointYMax, pt.y);
                                    }
                                }
                            }
                        }
                    }
                }
                Matrix matrix = new Matrix();
//                matrix.setScale(
//                        tempScaleX, //see setScaleBegin()
//                        tempScaleY,
//                        (tempPointXMin + tempPointXMax) / 2,
//                        (tempPointYMin + tempPointYMax) / 2);
                Point boundsTop_old = new Point(
                        tempPointXMin,
                        tempPointYMin);
                Point boundsBottom_old = new Point(
                        tempPointXMax,
                        tempPointYMax);
                float[] dst = new float[2];
                matrix.mapPoints(dst, new float[]{boundsTop_old.x, boundsTop_old.y});
                Point boundsTop = new Point(dst[0], dst[1]);
                matrix.mapPoints(dst, new float[]{boundsBottom_old.x, boundsBottom_old.y});
                Point boundsBottom = new Point(dst[0], dst[1]);
                this.currentPath.clear();
                this.currentPath.addPoint(boundsTop);
                this.currentPath.addPoint(new Point(boundsBottom.x, boundsTop.y));
                this.currentPath.addPoint(boundsBottom);
                this.currentPath.addPoint(new Point(boundsTop.x, boundsBottom.y));
                this.currentPath.appearance =
                        this.canvas.getSelectionTool().APPEARANCE_SELECTED;
            }
        }
    }
    private void rebuildImageSelectFrame() {
        float w = 0;
        float h = 0;
        DrawPath pathImage = null;
        if (selectedPaths.size() == 1) {
            pathImage = selectedPaths.get(0);
        }
        if (pathImage != null && pathImage.pointsType == DrawPath.POINTS_TYPE_IMAGE) {
            if (pathImage.pointsBitmap != null) {
                w = pathImage.pointsBitmap.getWidth();// * pathImage.scaleX;
                h = pathImage.pointsBitmap.getHeight();// * pathImage.scaleY;
            }
            Point boundsTop = new Point(
                    pathImage.pointsTextX - w / 2 * pathImage.pointsScaleX,
                    pathImage.pointsTextY - h / 2 * pathImage.pointsScaleX);
            Point boundsBottom = new Point(
                    boundsTop.x + w * pathImage.pointsScaleX,
                    boundsTop.y + h * pathImage.pointsScaleX);
            this.currentPath.clear();
            this.currentPath.addPoint(boundsTop);
            this.currentPath.addPoint(new Point(boundsBottom.x, boundsTop.y));
            this.currentPath.addPoint(boundsBottom);
            this.currentPath.addPoint(new Point(boundsTop.x, boundsBottom.y));
            this.currentPath.appearance =
                    this.canvas.getSelectionTool().APPEARANCE_SELECTED;
        }
    }
    private void rebuildTextSelectFrame() {
        DrawPath pathText = null;
        float w = 0;
        float h = 0;

        if (selectedPaths.size() == 1) {
            pathText = selectedPaths.get(0);
        }
        if (pathText != null && pathText.pointsType == DrawPath.POINTS_TYPE_TEXT) {
            w = 0;
            h = 0;
            if (pathText.pointsText != null) {
                Paint p = new Paint();
                DrawCanvas.getTextPaint(p,
                        pathText.isBold,
                        pathText.isItalics,
                        pathText.styleType,
                        pathText.isUnderline,
                        pathText.pointsTextColor,
                        pathText.pointsTextSize);
                SizeF size = DrawCanvas.calculateTextSizes(pathText.pointsText, p, pathText.pointsTextType);
                w = size.getWidth();
                h = size.getHeight();
            }
            Point boundsTop = new Point(
                    pathText.pointsTextX,
                    pathText.pointsTextY);
            Point boundsBottom = new Point(
                    boundsTop.x + w * pathText.pointsScaleX,
                    boundsTop.y + h * pathText.pointsScaleY);
            this.currentPath.clear();
            this.currentPath.addPoint(boundsTop);
            this.currentPath.addPoint(new Point(boundsBottom.x, boundsTop.y));
            this.currentPath.addPoint(boundsBottom);
            this.currentPath.addPoint(new Point(boundsTop.x, boundsBottom.y));
            this.currentPath.addPoint(boundsTop);
            this.currentPath.appearance =
                    this.canvas.getSelectionTool().APPEARANCE_SELECTED;
        }
    }

    //FIXME:scale
    public boolean isIconDrag = false;
    public boolean isIconDragMulti = false;
    final static boolean NO_ROTATE = true;
//    private Matrix moveMatrix = new Matrix();
    private float oldDistance = 0;
//    private Matrix downMatrix = new Matrix();
    private PointF midPoint = new PointF();
    private void zoomAndRotateSticker(float x, float y) {
        if (this.oldDistance != 0) {
            float newDistance = this.calculateDistance(this.midPoint.x, this.midPoint.y, x, y);
            //float newRotation = this.calculateRotation(this.midPoint.x, this.midPoint.y, event.getX(), event.getY());
//            this.moveMatrix.set(this.downMatrix);
            float scaleX = newDistance / this.oldDistance;
            float scaleY = newDistance / this.oldDistance;
            float midX = this.midPoint.x;
            float midY = this.midPoint.y;
//            this.moveMatrix.postScale(scaleX, scaleY, midX, midY);
            //if (!NO_ROTATE) {
            //    this.moveMatrix.postRotate(newRotation - this.oldRotation, this.midPoint.x, this.midPoint.y);
            //}
            if (!selectedPaths.isEmpty()) {
                if (isIconDragMulti) {
                    //only multi strokes
                    for (DrawPath path : selectedPaths) {
                        if (path != null) {
                            path.setScale(/*this.moveMatrix, */scaleX, scaleY, midX, midY);
                        }
                    }
                } else {
                    DrawPath path = selectedPaths.get(0);
                    if (path != null) {
                        path.setScale(/*this.moveMatrix, */scaleX, scaleY, midX, midY);
                    }
                }
            }
        }
    }

    private float calculateDistance(float x1, float y1, float x2, float y2) {
        double x = (double)x1 - (double)x2;
        double y = (double)y1 - (double)y2;
        return (float)Math.sqrt(x * x + y * y);
    }

    private int findCurrentIconTouched(float downX, float downY) {
//        Matrix selectedMatrix = new Matrix();
//        if (!getSelectedPaths().isEmpty()) {
//            DrawPath path = getSelectedPaths().get(0);
//            if (path != null) {
//                selectedMatrix.set(path.getMatrix());
//            }
//        }
        for (DrawPath path : getToolPaths()) {
            float radius = this.iconRadius;
            for (int i = 0; i < path.points.size(); ++i) {
                Point pt = path.points.get(i);
                float[] dst = new float[2];
//                selectedMatrix.mapPoints(dst, new float[]{pt.x, pt.y});
                dst[0] = pt.x;
                dst[1] = pt.y;
                //canvas.drawCircle(pt.x, pt.y, radius, iconPaint);
                float x = dst[0]/*pt.x*/ - downX;
                float y = dst[1]/*pt.y*/ - downY;
                float distancePow2 = x * x + y * y;
                if ((double)distancePow2 <= Math.pow((double)radius + (double)radius, (double)2.0F)) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * Selects paths that overlap with the selection square, then resizes the square
     * so it now represents the bounds of the selection.
     */
    public void selectPaths() {
        boolean isSingleSelect = false;
        if (currentPath.points != null && currentPath.points.size() >= 2) {
            float x1 = currentPath.points.get(0).x;
            float x2 = currentPath.points.get(1).x;
            if (x2 - x1 < 2) {
                if (canvas.mAct != null) {
                    //Toast.makeText(canvas.mAct, "single select", Toast.LENGTH_LONG).show();
                }
                isSingleSelect = true;
            }
        }

        Point startPoint = canvas.mapPoint(0, 0, 1.0f);
        Point endPoint = canvas.mapPoint(canvas.getWidth(), canvas.getHeight(), 1.0f);
        Region clip = new Region(Math.round(startPoint.x), Math.round(startPoint.y), Math.round(endPoint.x), Math.round(endPoint.y));

        // Top left of a bounding box for all selections ('cause we're rebuilding currentPath after this!)
        Point boundsTop = null;
        // Bottom right
        Point boundsBottom = null;

        // Creates a Region from the current path to do bounding box math
        Region currentPathRegion = new Region();
        currentPathRegion.setPath(currentPath.generatePath(), clip);

        // Bounding box math! (If a path collides with the current path, add it to the selection.)
        for (int k = canvas.paths.size() - 1; k >= 0; --k) {
            DrawPath path = canvas.paths.get(k);
            //----------------------
            //Removed objects are hidden
            if (path.pointsType == DrawPath.POINTS_TYPE_IMAGE && path.pointsBitmap == null) {
                continue; //skip
            } else if (path.pointsType == DrawPath.POINTS_TYPE_STROKE && path.points.size() == 0) {
                continue; //skip
            } else if (path.pointsType == DrawPath.POINTS_TYPE_TEXT &&
                    (path.pointsText == null || path.pointsText.length() == 0)) {
                continue; //skip
            }
            //----------------------

            if (path.getPath() != null ||
                    path.pointsType == DrawPath.POINTS_TYPE_IMAGE ||
                    path.pointsType == DrawPath.POINTS_TYPE_TEXT) {
                Region region = new Region();

//                Matrix selectedMatrix = path.getMatrix();
                float[] pointsRect = new float[] {
                        path.pointsTextX, path.pointsTextY,
                        path.pointsTextX, path.pointsTextY
                };
                List<Point> points = new ArrayList<>();
                if (path.pointsType == DrawPath.POINTS_TYPE_IMAGE) {
                    float width = path.pointsBitmap != null ? path.pointsBitmap.getWidth() : 0;
                    float height = path.pointsBitmap != null ? path.pointsBitmap.getHeight() : 0;
                    points.add(new Point(path.pointsTextX - width / 2 * path.pointsScaleX, path.pointsTextY - height / 2 * path.pointsScaleY));
                    points.add(new Point(path.pointsTextX + width / 2 * path.pointsScaleX, path.pointsTextY - height / 2 * path.pointsScaleY));
                    points.add(new Point(path.pointsTextX + width / 2 * path.pointsScaleX, path.pointsTextY + height / 2 * path.pointsScaleY));
                    points.add(new Point(path.pointsTextX - width / 2 * path.pointsScaleX, path.pointsTextY + height / 2 * path.pointsScaleY));
                    points.add(new Point(path.pointsTextX - width / 2 * path.pointsScaleX, path.pointsTextY - height / 2 * path.pointsScaleY));
                    pointsRect = new float[] {
                            path.pointsTextX - width / 2 * path.pointsScaleX, path.pointsTextY - height / 2 * path.pointsScaleY,
                            path.pointsTextX + width / 2 * path.pointsScaleX, path.pointsTextY + height / 2 * path.pointsScaleY
                    };
                } else if (path.pointsType == DrawPath.POINTS_TYPE_TEXT) {
                    DrawPath pathText = path;
                    float width = 0;
                    float height = 0;
                    Paint p = new Paint();
                    DrawCanvas.getTextPaint(p,
                            pathText.isBold,
                            pathText.isItalics,
                            pathText.styleType,
                            pathText.isUnderline,
                            pathText.pointsTextColor,
                            pathText.pointsTextSize);
                    SizeF size = DrawCanvas.calculateTextSizes(pathText.pointsText, p, pathText.pointsTextType);
                    width = size.getWidth();
                    height = size.getHeight();
                    points.add(new Point(path.pointsTextX - width * 0 * path.pointsScaleX, path.pointsTextY - height * 0 * path.pointsScaleY));
                    points.add(new Point(path.pointsTextX + width * 1 * path.pointsScaleX, path.pointsTextY - height * 0 * path.pointsScaleY));
                    points.add(new Point(path.pointsTextX + width * 1 * path.pointsScaleX, path.pointsTextY + height * 1 * path.pointsScaleY));
                    points.add(new Point(path.pointsTextX - width * 0 * path.pointsScaleX, path.pointsTextY + height * 1 * path.pointsScaleY));
                    points.add(new Point(path.pointsTextX - width * 0 * path.pointsScaleX, path.pointsTextY - height * 0 * path.pointsScaleY));
                    pointsRect = new float[] {
                            path.pointsTextX - width * 0 * path.pointsScaleX, path.pointsTextY - height * 0 * path.pointsScaleY,
                            path.pointsTextX + width * 1 * path.pointsScaleX, path.pointsTextY + height * 1 * path.pointsScaleY
                    };
                }
                float[] pointsRect2 = new float[4];
//                selectedMatrix.mapPoints(pointsRect2, pointsRect);
                pointsRect2[0] = pointsRect[0];
                pointsRect2[1] = pointsRect[1];
                pointsRect2[2] = pointsRect[2];
                pointsRect2[3] = pointsRect[3];

                RectF rect2 = new RectF(pointsRect2[0], pointsRect2[1], pointsRect2[2], pointsRect2[3]);
                RectF rect3 = new RectF();
                if (originalPoint != null && previousPoint != null) {
                    if (false) {
                        rect3 = new RectF(originalPoint.x, originalPoint.y, previousPoint.x, previousPoint.y);
                    } else {
                        rect3 = new RectF(
                                Math.min(originalPoint.x, previousPoint.x),
                                Math.min(originalPoint.y, previousPoint.y),
                                Math.max(originalPoint.x, previousPoint.x),
                                Math.max(originalPoint.y, previousPoint.y)
                        );
                    }
                } else if (originalPoint != null && previousPoint == null) {
                    rect3 = new RectF(originalPoint.x, originalPoint.y, originalPoint.x + 1, originalPoint.y + 1);
                }
                boolean isSelected = false;
                if (Utils.isIntersects(rect2, rect3)) {  //BE CAREFUL:rect2 is changed
                    isSelected = true;
                }

                RectF bounds2 = new RectF();
                if (path.pointsType == DrawPath.POINTS_TYPE_IMAGE ||
                        path.pointsType == DrawPath.POINTS_TYPE_TEXT) {
                    Path p = new Path();
                    for (int i = 0; i < points.size(); ++i) {
                        Point pt = points.get(i);
                        float[] dst = new float[2];
                        dst[0] = pt.x;
                        dst[1] = pt.y;
                        if (i == 0) {
                            p.moveTo(dst[0], dst[1]);
                        } else {
                            p.lineTo(dst[0], dst[1]);
                        }
                    }
                    p.close();
                    p.computeBounds(bounds2, false); //改用路径外框选中
                    if (!SELECT_REGION_NO_CLIP) {
                        region.setPath(p, clip);
                    } else {
                        Rect rectRound = new Rect();
                        bounds2.roundOut(rectRound);
                        region.set(rectRound); //20260321, image and text don't be clipped by screen
                    }
                } else {
                    if (path.getPath() != null) {
                        //FIXME:added, because region.op(currentPathRegion, Region.Op.INTERSECT) not good
                        path.getPath().computeBounds(bounds2, false); //改用路径外框选中
                        Path p = new Path();
                        if (true) {
                            p.moveTo(bounds2.left, bounds2.top);
                            p.lineTo(bounds2.right, bounds2.top);
                            p.lineTo(bounds2.right, bounds2.bottom);
                            p.lineTo(bounds2.left, bounds2.bottom);
                        } else {
                            //don't use this, just begin and end point
                            if (path.points.size() > 0) {
                                Point p1 = path.points.get(0);
                                Point p2 = path.points.get(path.points.size() - 1);
                                p.moveTo(p1.x, p1.y);
                                p.lineTo(p2.x, p1.y);
                                p.lineTo(p2.x, p2.y);
                                p.lineTo(p1.x, p2.y);
                            }
                        }
                        p.close();
                        if (!SELECT_REGION_NO_CLIP) {
                            region.setPath(p, clip);
                        } else {
                            Rect rectRound = new Rect();
                            bounds2.roundOut(rectRound);
                            region.set(rectRound); //20260321, image and text don't be clipped by screen
                        }

                        if (Utils.isIntersects(bounds2, rect3)) {  //BE CAREFUL:bounds2 is changed
                            isSelected = true;
                        }
                    } else {
                        //region.setPath(path.getPath(), clip);
                    }
                }
                Rect bounds = region.getBounds();//BE CAREFUL:if empty, (x, y) = (0, 0), not good
                if (bounds.height() == 0 || bounds.width() == 0) { //bounds == (0, 0, 0, 0)
                    bounds = new Rect(
                            (int)bounds2.left,
                            (int)bounds2.top,
                            (int)bounds2.right,
                            (int)bounds2.bottom);
                }

                /*
                 ||
                        (!region.quickReject(currentPathRegion) && region.op(currentPathRegion, Region.Op.INTERSECT))
                 */
                if (isSelected) {
                    selectedPaths.add(path);
                    // Checks to see if the bounding box for all selections can be expanded.
                    // Speaking of expanding things, you should click the minimise button the left for each if statement.
                    if (boundsTop == null) {
                        boundsTop = new Point(bounds.left, bounds.top);
                    }
                    if (boundsBottom == null) {
                        boundsBottom = new Point(bounds.right, bounds.bottom);
                    }
                    if (bounds.top < boundsTop.y) {
                        boundsTop.y = bounds.top;
                    }
                    if (bounds.left < boundsTop.x) {
                        boundsTop.x = bounds.left;
                    }
                    if (bounds.bottom > boundsBottom.y) {
                        boundsBottom.y = bounds.bottom;
                    }
                    if (bounds.right > boundsBottom.x) {
                        boundsBottom.x = bounds.right;
                    }

                    if (isSingleSelect) {
                        break;
                    }
                }
            }
        }

        if (canvas.getEraserMode()) {
            eraseCurrentPath();
        } else {
            // Yep, we are rebuilding the current path to reflect not the bounds the user selected,
            // but the bounds of the *paths* the user selected.
            currentPath.clear();
            if (boundsTop != null) {
                currentPath.addPoint(boundsTop);
                currentPath.addPoint(new Point(boundsBottom.x, boundsTop.y));
                currentPath.addPoint(boundsBottom);
                currentPath.addPoint(new Point(boundsTop.x, boundsBottom.y));

                if (getScaleMode()) {
                    scalePoint = new Point((boundsTop.x + boundsBottom.x) / 2,
                            (boundsTop.y + boundsBottom.y) / 2);
                }
            }
        }
    }

    public boolean allowVersionBackup() {
        return changedDrawPaths;
    }

    /**
     * You can either define a new selection or move a selection. Each touch mode is set from
     * different conditions and reset once you lift your finger off the screen.
     */
    public enum TOUCH_MODES {none, define, move}

    public void eraseCurrentPath() {
        for (DrawPath path : canvas.paths) {
            path.erase(currentPath);
            path.cachePath();
        }
        currentPath.clear();
        init();
    }
    private boolean getScaleMode() {
        return canvas.getScaleMode();
    }

    public LinkedList<DrawPath> getSelectedPaths() {
        return selectedPaths;
    }

    public void exitSelect() {
        changedDrawPaths = false;
        // If the touch action is outside the currently selected rectangle, we're not trying to manipulate it
        // -- we're trying to make a new one
        currentPath.appearance = APPEARANCE.clone();
        mode = TOUCH_MODES.none; //TOUCH_MODES.define;
        selectedPaths.clear();
        currentPath.clear();
    }
}
