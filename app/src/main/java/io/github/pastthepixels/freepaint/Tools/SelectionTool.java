package io.github.pastthepixels.freepaint.Tools;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.SizeF;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.github.guanpy.wblib.bean.DrawPoint;
import com.github.guanpy.wblib.widget.DrawTextView;
import com.txkj.drawingapp.R;
import com.txkj.drawingapp.activity.BookActivity4Fragment;
import com.txkj.drawingapp.activity.BookActivity4Utils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import io.github.pastthepixels.freepaint.Graphics.DrawAppearance;
import io.github.pastthepixels.freepaint.Graphics.DrawCanvas;
import io.github.pastthepixels.freepaint.Graphics.DrawPath;
import io.github.pastthepixels.freepaint.Graphics.Point;

public class SelectionTool implements Tool {
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
                        for (DrawPath path : selectedPaths) {
                            path.erasePath();
                        }
                        currentPath.clear();
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
                                    (path.pointsType == DrawPath.POINTS_TYPE_IMAGE ||
                                            path.pointsType == DrawPath.POINTS_TYPE_TEXT)) {
                                isIconDrag = true;
                                path.setScaleBegin();
//                                this.downMatrix.set(path.getMatrix());
                                this.midPoint.set(path.pointsTextX, path.pointsTextY);
                            } else {
//                                this.downMatrix.set(new Matrix());
                                this.midPoint.set(0.0F, 0.0F);
                            }
                            this.oldDistance = this.calculateDistance(this.midPoint.x, this.midPoint.y,
                                    originalPoint.x, originalPoint.y);
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
                    zoomAndRotateSticker(touchPoint.x, touchPoint.y);
                    rebuildImageSelctFrame();
                    rebuildTextSelectFrame();
                } else {
                    if (mode == TOUCH_MODES.define) {
                        // If we're trying to define a new selection, redraw the current path with the bounds
                        currentPath.clear();
                        currentPath.addPoint(originalPoint);
                        currentPath.addPoint(new Point(touchPoint.x, originalPoint.y));
                        currentPath.addPoint(touchPoint);
                        currentPath.addPoint(new Point(originalPoint.x, touchPoint.y));
                    }
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
                                    rebuildImageSelctFrame();
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

    private void rebuildImageSelctFrame() {
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
                SizeF size = DrawCanvas.calculateTextSizes(pathText.pointsText, p);
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
                DrawPath path = selectedPaths.get(0);
                if (path != null) {
                    path.setScale(/*this.moveMatrix, */scaleX, scaleY, midX, midY);
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
        for (DrawPath path : canvas.paths) {
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
                    SizeF size = DrawCanvas.calculateTextSizes(pathText.pointsText, p);
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
                    rect3 = new RectF(originalPoint.x, originalPoint.y, previousPoint.x, previousPoint.y);
                } else if (originalPoint != null && previousPoint == null) {
                    rect3 = new RectF(originalPoint.x, originalPoint.y, originalPoint.x + 1, originalPoint.y + 1);
                }
                boolean isSelected = false;
                if (rect2.intersect(rect3)) {
                    isSelected = true;
                }

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
                    region.setPath(p, clip);
                } else {
                    region.setPath(path.getPath(), clip);
                }
                Rect bounds = region.getBounds();



                if (isSelected ||
                        (!region.quickReject(currentPathRegion) && region.op(currentPathRegion, Region.Op.INTERSECT))) {
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
