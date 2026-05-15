package io.github.pastthepixels.freepaint.Graphics;

import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;
import android.util.SizeF;

import androidx.annotation.NonNull;

import com.strokeapp.domain.model.ShapeType;
import com.strokeapp.domain.model.SimplifiedShape;
import com.strokeapp.domain.model.Stroke;
import com.strokeapp.domain.model.StrokePoint;
import com.strokeapp.domain.usecase.StrokeSimplifierUseCase;
import com.strokeapp.domain.usecase.TestData1;
import com.txkj.drawingapp.activity.BookActivity4Config;
import com.txkj.drawingapp.activity.BookActivity4Fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import dev.romainguy.graphics.path.PathIterator;
import dev.romainguy.graphics.path.PathSegment;
import dev.romainguy.graphics.path.Paths;
import io.github.pastthepixels.freepaint.Utils;


public class DrawPath {
    public final static boolean ALLOW_MULTI_SCALE = true; //don't stop scaling with mixing image and text

    public final static boolean USE_TEMP_PAINT = false; //don't set true //copy from edittext
    private final static float OFFSET_Y = 3.0f; //why? I don't know

    // watch about adding new variables because you have to add them to the clone function at the bottom!

    /**
     * Appearance of the path (ex. fill/stroke color, stroke width)
     */
    public DrawAppearance appearance = new DrawAppearance(Color.BLACK, -1);

    /**
     * List of points
     */
    //public ArrayList<Point> points = new ArrayList<>();
    public CopyOnWriteArrayList<Point> points = new CopyOnWriteArrayList<>();
    //---------------------
    //added
    public final static int POINTS_TEXT_TYPE_NONE = 0;
    public final static int POINTS_TEXT_TYPE_RICH = 1;
    public int pointsTextType = 0;//0:normal;1:rich
    public String pointsText = "";
    public Bitmap pointsBitmap = null;
    public float pointsTextX = 0, pointsTextY = 0;
    public int pointsType = POINTS_TYPE_STROKE;
    public final static int POINTS_TYPE_STROKE = 0;
    public final static int POINTS_TYPE_TEXT = 1;
    public final static int POINTS_TYPE_IMAGE = 2;

    public boolean isBold = false;
    public boolean isItalics = false;
    public boolean isUnderline = false;
    public int styleType = BookActivity4Fragment.STYLE_TYPE_NONE;
    public int pointsTextColor = 0;
    public float pointsTextSize = 28;
    public Paint tempPaint = null;
    public float tempX = 0, tempY = 0;
//    public Matrix tempMatrix = new Matrix();

    public float tempScaleX = 1.0F;
    public float tempScaleY = 1.0F;
    public float tempMidX = 0.0F;
    public float tempMidY = 0.0F;
    public boolean tempHidden = false;
    ArrayList<Point> tempPoints = new ArrayList<>();

    public int shapeType = 0;
    public final static int SHAPE_TYPE_LINE = 1;
    public final static int SHAPE_TYPE_RECTANGLE = 2;
    public final static int SHAPE_TYPE_CIRCLE = 3;
    public final static int SHAPE_TYPE_TRIANGLE = 4;
    public final static int SHAPE_TYPE_POLYGON = 5;
    public final static int SHAPE_TYPE_UNKNOWN = 6;
    public CopyOnWriteArrayList<Point> preSimplified = new CopyOnWriteArrayList<>();
    public int shapeSide = 0;
    public int shapeWidth = 0;
    public int shapeHeight = 0;

//    private Matrix matrix = new Matrix();
//    public Matrix getMatrix() {
//        return matrix;
//    }

    public float pointsScaleX = 1.0F, pointsScaleY = 1.0F;
    public void setScaleBegin() {
//        this.tempMatrix.set(matrix);
        if (pointsType == POINTS_TYPE_STROKE) {
            //stoke's scaleXY are not used
            //tempScaleXY used for rebuildStrokeSelectFrame
            this.tempScaleX = 1.0F;
            this.tempScaleY = 1.0F;
            tempPoints.clear();
            if (points != null) {
                if (points.size() > 0 && points.get(0) != null) {
                    tempPointXMin = points.get(0).x;
                    tempPointXMax = points.get(0).x;
                    tempPointYMin = points.get(0).y;
                    tempPointYMax = points.get(0).y;
                } else {
                    tempPointXMin = 0;
                    tempPointXMax = 0;
                    tempPointYMin = 0;
                    tempPointYMax = 0;
                }
                for (Point pt : points) {
                    if (pt != null) {
                        if (pt.x < tempPointXMin) {
                            tempPointXMin = pt.x;
                        }
                        if (pt.x > tempPointXMax) {
                            tempPointXMax = pt.x;
                        }
                        if (pt.y < tempPointYMin) {
                            tempPointYMin = pt.y;
                        }
                        if (pt.y > tempPointYMax) {
                            tempPointYMax = pt.y;
                        }
                        tempPoints.add(new Point(pt.x, pt.y));
                    }
                }
            }
        } else {
            if (ALLOW_MULTI_SCALE) {
                //mark
            }
            this.tempScaleX = pointsScaleX;
            this.tempScaleY = pointsScaleY;
            this.tempX = pointsTextX;
            this.tempY = pointsTextY;

            //use for calculate mid point
            if (pointsType == DrawPath.POINTS_TYPE_IMAGE) {
                float w = pointsBitmap != null ? pointsBitmap.getWidth() : 0;
                float h = pointsBitmap != null ? pointsBitmap.getHeight() : 0;
                //for calculating midpoint
                this.tempPointXMin = pointsTextX - pointsScaleX * w / 2.0F;
                this.tempPointXMax = pointsTextX + pointsScaleX * w / 2.0F;
                this.tempPointYMin = pointsTextY - pointsScaleX * h / 2.0F;
                this.tempPointYMax = pointsTextY + pointsScaleX * h / 2.0F;
            } else if (pointsType == DrawPath.POINTS_TYPE_TEXT) {
                float w = 0;
                float h = 0;
                if (pointsText != null) {
                    Paint p = new Paint();
                    DrawCanvas.getTextPaint(p,
                            isBold,
                            isItalics,
                            styleType,
                            isUnderline,
                            pointsTextColor,
                            pointsTextSize);
                    SizeF size = DrawCanvas.calculateTextSizes(pointsText, p, pointsTextType);
                    w = size.getWidth();
                    h = size.getHeight();
                }
                //for calculating midpoint
                this.tempPointXMin = pointsTextX;
                this.tempPointXMax = pointsTextX + pointsScaleX * w;
                this.tempPointYMin = pointsTextY;
                this.tempPointYMax = pointsTextY + pointsScaleX * h;
            }
        }
    }
    public float tempPointXMin = 0;
    public float tempPointXMax = 0;
    public float tempPointYMin = 0;
    public float tempPointYMax = 0;
    //FIXME:midX_, midY_ not used
    public void setScale(/*Matrix matrix, */float scaleX_, float scaleY_, float midX_, float midY_) {
        if (pointsType == POINTS_TYPE_STROKE) {
            this.pointsScaleX = 1.0F;
            this.pointsScaleY = 1.0F; //don't use scaleXY, modify points directly
            this.tempScaleX = scaleX_; //tempScaleXY is not used, so used for rebuildStrokeSelectFrame()
            this.tempScaleY = scaleY_;
            Matrix matrix = new Matrix();
            matrix.setScale(
                    scaleX_,
                    scaleY_,
                    midX_, //(tempPointXMin + tempPointXMax) / 2,
                    midY_ //(tempPointYMin + tempPointYMax) / 2
            );
            if (tempPoints != null) {
                for (int i = 0; i < tempPoints.size(); ++i) {
                    Point tempPoint = tempPoints.get(i);
                    if (i >= 0 && i < points.size()) {
                        Point point = points.get(i);
                        if (tempPoint != null && point != null) {
                            float[] dstPoint = new float[2];
                            matrix.mapPoints(dstPoint, new float[]{tempPoint.x, tempPoint.y});
                            point.x = dstPoint[0];
                            point.y = dstPoint[1];
                        }
                    }
                }
            }
            this.tempMidX = midX_;
            this.tempMidY = midY_;
            cachePath();
        } else {
            //this.matrix.set(matrix);
            this.pointsScaleX = this.tempScaleX * scaleX_;
            this.pointsScaleY = this.tempScaleY * scaleY_;
            this.tempMidX = midX_;
            this.tempMidY = midY_;
//        Matrix nMatrix = new Matrix(this.tempMatrix);
//        nMatrix.postScale(scaleX, scaleY);
//        this.matrix.set(nMatrix);
            if (ALLOW_MULTI_SCALE) {
                Matrix matrix = new Matrix();
                matrix.setScale(
                        scaleX_,
                        scaleY_,
                        midX_, //(tempPointXMin + tempPointXMax) / 2,
                        midY_ //(tempPointYMin + tempPointYMax) / 2
                );
                float[] dstPoint = new float[2];
                matrix.mapPoints(dstPoint, new float[]{this.tempX, this.tempY}); //see setScaleBegin()
                pointsTextX = dstPoint[0];
                pointsTextY = dstPoint[1];
            }
        }
    }
//    public void getMappedCenterPoint(PointF dst, float[] mappedPoints, float[] src) {
//        this.getCenterPoint(dst);
//        src[0] = dst.x;
//        src[1] = dst.y;
//        this.getMappedPoints(mappedPoints, src);
//        dst.set(mappedPoints[0], mappedPoints[1]);
//    }
//    public void getCenterPoint(PointF dst) {
//        int width = pointsBitmap.getWidth();
//        int height = pointsBitmap.getHeight();
//        dst.set((float)width * 1.0F / (float)2, (float)height * 1.0F / (float)2);
//    }
//    public void getMappedPoints(float[] dst, float[] src) {
//        this.matrix.mapPoints(dst, src);
//    }
    //FIXME:FIXME:if add new property, see also public DrawPath clone() {
    //---------------------

    /**
     * Whether or not the path is closed (a line is drawn from the end point to the start point)
     */
    public boolean isClosed = false;

    /**
     * Whether or not to draw each point as a circle.
     */
    public boolean drawPoints = false;

    /**
     * Epsilon value for the simplification algorithm (RDP)
     */
    public double simplificationAmount = 0;

    /**
     * android.graphics.Path instance. FreePaint handles math but this is how we get that math to be shown on the screen.
     */
    private Path path;

    /**
     * Constructor for DrawPath
     */
    public DrawPath(Path path, int pointsType) {
        this.path = path;
        this.pointsType = pointsType;
    }

    /**
     * Adds an instance of <code>io.github.pastthepixels.freepaint.Graphics.Point</code> to the list of points
     */
    public void addPoint(Point point) {
        points.add(point);
    }


    /**
     * Clears all points in a DrawPath, then resets <code>DrawPath.path</code>
     */
    public void clear() {
        points.clear();
        this.path = null;
        this.pointsBitmap = null;
        this.pointsText = null;
    }

    /**
     * Generates a basic path by connecting lines, ideal for previewing
     *
     * @return A generated path
     */
    public Path generatePath() {
        Path path = new Path();
        for (int i = 0; i < points.size(); i++) {
            Point point = points.get(i);
            if (i == 0 || point.command == Point.COMMANDS.move) {
                path.moveTo(point.x, point.y);
            } else {
                Point prev = points.get(i - 1);
                path.cubicTo(
                        prev.getRightHandle().x,
                        prev.getRightHandle().y,
                        point.getLeftHandle().x,
                        point.getLeftHandle().y,
                        point.x,
                        point.y
                );
            }
        }
        if (isClosed) {
            path.close();
        }
        return path;
    }


    /**
     * Accessor for <code>path</code>
     *
     * @return The <code>android.graphics.Path</code> instance used for drawing.
     */
    public Path getPath() {
        return path;
    }

    /**
     * Returns a cached path, or generates a new one.
     */
    private Path getPathOrGenerate() {
        if (getPath() != null) {
            return getPath();
        } else {
            return generatePath();
        }
    }

    /**
     * Caches generatePath() into a thing we can reuse (dp)
     */
    public void cachePath() {
        this.path = generatePath();
    }

    /**
     * Applies operations to the <code>points</code> array to simplify and
     * smoothen lines after they are drawn.
     */
    public void finalise() {
        // Simplifies the path.
        points = simplify(points, simplificationAmount, true);
        if (appearance.penType == DrawAppearance.PEN_TYPE_6) { //shape pen
            if (BookActivity4Fragment.ENABLE_NO_DETECT_SHAPE_PEN) {
                shapeWidth = shapeHeight = 100;
            }
        }
        // Generates handles for each point.
        if (true) {
            for (int i = 0; i < points.size(); i++) {
                try {
                    Point point = points.get(i);
                    if (i == 0) {
                        if (i + 1 >= 0 && i + 1 < points.size()) {
                            Point next = points.get(i + 1);
                            point.setRightHandle(new Point(
                                    ((next.x - point.x) / 3),
                                    ((next.y - point.y) / 3)
                            ));
                        }
                    } else if (i != points.size() - 1) {
                        if (i - 1 >= 0 && i - 1 < points.size() &&
                                i + 1 >= 0 && i + 1 < points.size()) {
                            Point prev = points.get(i - 1);
                            Point next = points.get(i + 1);
                            // Set handles (left handle is mirrored; hermite splines!
                            Point rightHandle = new Point(
                                    ((next.x - prev.x) / 6),
                                    ((next.y - prev.y) / 6)
                            );
                            point.setRightHandle(rightHandle);
                            point.setLeftHandle(rightHandle.multiply(-1));
                            // If the angles between the current point and the next point/current and previous are acute/right, make the corner sharp.
                            double angle = Utils.angleBetweenVectors(prev.subtract(point), point.subtract(next));
                            if (Math.abs(angle) >= Math.PI / 2) { // idk how this works but it does. it shouldn't be this way.
                                point.setLeftHandle(new Point(0, 0));
                                point.setRightHandle(new Point(0, 0));
                            }
                        }
                    }
                } catch (Throwable eee) {
                    eee.printStackTrace();
                }
            }
        }
    }

    private final static boolean USE_OLD_METHOD = false;
    /**
     * Simplifies points using the Ramer-Douglas-Peucker algorithm.
     * Adapted from the pseudocde from https://en.wikipedia.org/wiki/Ramer%E2%80%93Douglas%E2%80%93Peucker_algorithm
     */
    private CopyOnWriteArrayList<Point> simplify(CopyOnWriteArrayList<Point> points, double epsilon, boolean isTop) {
        if (BookActivity4Config.USE_NO_POINT_SIMPLIFY) {
            if (FORCE_PENCIL_SIMPLIFY && appearance.penType == DrawAppearance.PEN_TYPE_4) {
                return old_simplify(points, 10,/*epsilon,*/ isTop);
            } else if (epsilon == 0) {
                return points;
            }
        }
        if (USE_OLD_METHOD) {
            return old_simplify(points, epsilon, isTop);
        } else {
            if (BookActivity4Fragment.ENABLE_SHAPE_PEN) {
                //FIXME:
            }
            //return points;
            boolean IS_TEST = false;
            TestData1 testData1 = null;
            if (IS_TEST) {
                testData1 = new TestData1();
                testData1.init();
            }
            StrokeSimplifierUseCase simplifier = new StrokeSimplifierUseCase();
            List<StrokePoint> strokePointList = new ArrayList<>();
            if (IS_TEST) {
                for (StrokePoint sp : testData1.strokePoints) {
                    strokePointList.add(sp);
                }
            } else {
                for (Point point : points) {
                    strokePointList.add(new StrokePoint(point.x, point.y));
                }
            }
            Stroke stroke = new Stroke(strokePointList, 0);
            Stroke simplified = simplifier.simplify(stroke, IS_TEST ? testData1.tolerance : 10.0f);//50.0f);
            SimplifiedShape shape = simplifier.recognizeShape(simplified);
            List<StrokePoint> points2 = shape.getPoints();
            if (shape.getType() == ShapeType.LINE) {
                this.shapeType = SHAPE_TYPE_LINE;
            } else if (shape.getType() == ShapeType.RECTANGLE) {
                this.shapeType = SHAPE_TYPE_RECTANGLE;
            } else if (shape.getType() == ShapeType.CIRCLE) {
                this.shapeType = SHAPE_TYPE_CIRCLE;
            } else if (shape.getType() == ShapeType.TRIANGLE) {
                this.shapeType = SHAPE_TYPE_TRIANGLE;
            } else if (shape.getType() == ShapeType.POLYGON) {
                this.shapeType = SHAPE_TYPE_POLYGON;
            } else if (shape.getType() == ShapeType.UNKNOWN) {
                this.shapeType = SHAPE_TYPE_UNKNOWN;
            }
            CopyOnWriteArrayList<Point> points_ = new CopyOnWriteArrayList<Point>();
            for (StrokePoint point : points2) {
                points_.add(new Point(point.getX(), point.getY()));
            }
            this.preSimplified.clear();
            this.preSimplified.addAll(points);
            return points_;
        }
    }

    private CopyOnWriteArrayList<Point> old_simplify(CopyOnWriteArrayList<Point> points, double epsilon, boolean isTop) {
        double max_distance = 0;
        int index = 0;
        for (int i = 2; i < points.size() - 1; i++) {
            double distance = Utils.distanceFromPointToLine(points.get(0), points.get(points.size() - 1), points.get(i));
            if (distance > max_distance) {
                index = i;
                max_distance = distance;
            }
        }

        CopyOnWriteArrayList<Point> simplified = new CopyOnWriteArrayList<>();

        if (max_distance > epsilon) {
            // Like merge sort
            CopyOnWriteArrayList<Point> leftHalf = old_simplify(new CopyOnWriteArrayList<Point>(points.subList(0, index)), epsilon, false);
            CopyOnWriteArrayList<Point> rightHalf = old_simplify(new CopyOnWriteArrayList<Point>(points.subList(index, points.size())), epsilon, false);
            Point point = rightHalf.get(0).clone().applySubtract(leftHalf.get(leftHalf.size() - 1));
            leftHalf.remove(leftHalf.size() - 1);
            if (isTop) {
                simplified.add(points.get(0)); //FIXME: added
            }
            simplified.addAll(leftHalf);
            simplified.addAll(rightHalf);
            if (isTop) {
                simplified.add(points.get(points.size() - 1)); //FIXME: added
            }
        } else {
            if (points.size() > 0) {
                simplified.add(points.get(0));
                simplified.add(points.get(points.size() - 1));
            }
        }
        return simplified;
    }

    /**
     * Draws the path.
     *
     * @param canvas      The canvas to draw to.
     * @param paint       The Paint instance to use -- this code is built for reusing the same one so memory can be saved.
     * @param scaleFactor Necessary so we can draw the dots for points to always be the same size
     */
    //onDraw(Canvas)
    public void draw(Canvas canvas, Paint paint, float screenDensity, float scaleFactor, boolean drawMinimal) {
        Path toDraw = getPath();
        if (toDraw == null) {
            toDraw = generatePath();
        }
        // Sets a configuration for the Paint with DrawPath.appearance
        appearance.initialisePaint(paint, screenDensity / scaleFactor);
        // Fills, then...
        if (appearance.fill != -1) {
            paint.setColor(appearance.fill);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawPath(toDraw, paint);
        }
        // Strokes
        if (appearance.stroke != -1) {
            if (appearance.penType == DrawAppearance.PEN_TYPE_3) {
                paint.setColor((appearance.stroke & 0xFFFFFF) | 0x80000000);
            } else {
                paint.setColor(appearance.stroke);
            }
            paint.setStyle(Paint.Style.STROKE);
            if (pointsType == POINTS_TYPE_STROKE) {
                if (appearance.penType == DrawAppearance.PEN_TYPE_1 ||
                        appearance.penType == DrawAppearance.PEN_TYPE_5) {
                    int baseColor = paint.getColor();
                    Paint base = new Paint(paint);
                    for (int i = 0; i < points.size() - 1; ++i) {
                        Point p0 = points.get(i);
                        Point p1 = points.get(i + 1);

                        if (DrawCanvas.isEmulator()) {
                            //emulate pressure
                            if (points.size() - i < paint.getStrokeWidth()) {
                                base.setColor((baseColor & 0xFFFFFF) | (((int) (0xFF/*stroke.opacity*/ * (float) (points.size() - i) / (float) base.getStrokeWidth())) << 24));
                                if (appearance.penType == DrawAppearance.PEN_TYPE_1) {
                                    base.setStrokeWidth(points.size() - i);
                                } else if (appearance.penType == DrawAppearance.PEN_TYPE_5) {
                                    base.setStrokeWidth(paint.getStrokeWidth());
                                }
                            } else {
                                base.setColor((baseColor & 0xFFFFFF) | (((int) (0xFF/*stroke.opacity*/ * 1.0)) << 24));
                                base.setStrokeWidth(paint.getStrokeWidth());
                            }
                        } else {
                            base.setColor((baseColor & 0xFFFFFF) | (((int) (0xFF/*stroke.opacity*/ * 1.0)) << 24));
                            base.setStrokeWidth(paint.getStrokeWidth() * p0.pressure);
                        }
                        base.setStyle(Paint.Style.STROKE);
                        base.setAntiAlias(true);
                        canvas.drawLine(p0.x, p0.y, p1.x, p1.y, base);
                    }
                } else if (appearance.penType == DrawAppearance.PEN_TYPE_2) {
                    //see
                    // Pastel: chalky, layered dabs with grain
                    {
                        int baseColor = paint.getColor();
                        Random rnd = new Random(2718);
                        for (Point p : points) {
                            float w = appearance.strokeSize;//(stroke.width * p.pressure).clamp(0.5, 220.0);
                            if (!DrawCanvas.isEmulator()) {
                                if (false) {
                                    w = appearance.strokeSize * p.pressure;
                                } else {
                                    // 获取手写笔压力（如果支持）
                                    //if (event.getPressure() > 0 && event.getPressure() <= 1.0f) {
                                    //    stylusPressure = event.getPressure();
                                    //    currentStrokeWidth = stylusStrokeWidth * (0.5f + stylusPressure * 0.5f);
                                    //}
                                    //new pressure algorithm
                                    w = appearance.strokeSize * (0.5f + p.pressure * 0.5f);
                                }
                            }
                            // Base smudge
                            Paint base = new Paint();
                            base.setColor((baseColor & 0xFFFFFF) | (((int) (0xFF/*stroke.opacity*/ * 0.35)) << 24));
                            base.setStyle(Paint.Style.FILL);
                            base.setAntiAlias(true);
                            base.setMaskFilter(new BlurMaskFilter(0.8f, BlurMaskFilter.Blur.NORMAL));
                            canvas.drawCircle(p.x, p.y, w * 0.55f, base);

                            // Chalk body
                            Paint body = new Paint();
                            body.setColor((baseColor & 0xFFFFFF) | (((int) (0xFF/*stroke.opacity*/ * 0.55)) << 24));
                            body.setStyle(Paint.Style.FILL);
                            body.setAntiAlias(true);
                            canvas.drawCircle(p.x, p.y, w * 0.42f, body);

                            // Grain speckles around
                            float grainDensity = (/*stroke.pastelGrainDensity ? ?*/ 1.0f);//.clamp(0.3, 3.0);
                            float grains = Math.round(w * 0.8 * grainDensity);//.clamp(4, 50);
                            grains = Math.min(Math.max(grains, 4.0f), 50.0f);
                            for (int i = 0; i < grains; i++) {
                                double ang = rnd.nextDouble() * 2 * Math.PI;
                                double dist = rnd.nextDouble() * w * 0.6 * 2; //FIXME: add *5
                                double gSize = 0.6 + rnd.nextDouble() * 1.4;
                                PointF gOff = new android.graphics.PointF((float) (Math.cos(ang) * dist), (float) (Math.sin(ang) * dist));
                                int alpha = (int) (0xFF/*stroke.opacity*/ * (0.06 + rnd.nextDouble() * 0.24));
                                alpha = Math.min(Math.max(alpha, 0x0), 0xFF);
                                Paint speck = new Paint();
                                speck.setColor((baseColor & 0xFFFFFF) | (alpha << 24));
                                speck.setStyle(Paint.Style.FILL);
                                speck.setAntiAlias(true);
                                canvas.drawCircle(p.x + gOff.x, p.y + gOff.y, (float) gSize, speck);
                            }
                        }
                    }
                    canvas.drawPath(toDraw, paint);
                } else if (appearance.penType == DrawAppearance.PEN_TYPE_4) {
                    int baseColor = paint.getColor();
                    //Paint base = new Paint(paint);
                    boolean useDrawPoints = true;
                    List<Float> toDrawList = new ArrayList<>();
                    for (int i = 0; i < points.size() - 1; ++i) {
                        Point p0 = points.get(i);
                        Point p1 = points.get(i + 1);
                        drawStroke(canvas, p0, p1, appearance.strokeSize, baseColor, useDrawPoints, toDrawList);
                    }
                    if (useDrawPoints) {
                        paint.setStyle(Paint.Style.FILL);
                        paint.setColor((baseColor & 0xFFFFFF) | 0xFF000000);
                        paint.setStrokeWidth(1.1f);
                        //int pos = 0;
                        float[] points = new float[toDrawList.size()];
                        for (int i = 0; i < toDrawList.size(); ++i) {
                            Float p = toDrawList.get(i);
                            points[i] = p;
                        }
                        canvas.drawPoints(points, paint);
                    }
                } else if (appearance.penType == DrawAppearance.PEN_TYPE_6) { //shape pen
                    if (BookActivity4Fragment.ENABLE_NO_DETECT_SHAPE_PEN) {
                        //always circle when created
                        if (points != null && points.size() > 0) {
                            if (shapeSide == 0 /*|| shapeSide == 1 || shapeSide == 2*/) {
                                Point center = points.get(points.size() - 1);
                                float radius = shapeWidth;//100;
                                canvas.drawCircle(center.x, center.y, radius, paint);
                            } else {
                                float radius = shapeWidth * 2;//100 * 2;
                                Point center = points.get(points.size() - 1);
                                List<RegularPolygonVertices2.Point> points_ =
                                        RegularPolygonVertices2.getVertices(shapeSide + 2);
                                Path path = new Path();
                                for (int i = 0; i < points_.size(); i++) {
                                    RegularPolygonVertices2.Point point =
                                            points_.get(i);
                                    if (i == 0) {
                                        path.moveTo(
                                                (float)(center.x + radius * point.getX()),
                                                (float)(center.y + radius * point.getY()));
                                    } else {
//                                        RegularPolygonVertices2.Point prev =
//                                                points_.get(i - 1);
                                        path.lineTo(
                                                (float)(center.x + radius * point.getX()),
                                                (float)(center.y + radius * point.getY()));
                                    }
                                }
                                path.close();
                                canvas.drawPath(path, paint);
                            }
                        }
                    } else {
                        if (shapeType == SHAPE_TYPE_LINE) {
                            if (points.size() >= 2) {
                                canvas.drawLine(points.get(0).x, points.get(0).y,
                                        points.get(1).x, points.get(1).y, paint);
                            } else {
                                canvas.drawPath(toDraw, paint);
                            }
                        } else if (shapeType == SHAPE_TYPE_RECTANGLE) {
                            if (points.size() >= 4) {
                                float minX = Float.MAX_VALUE;
                                float minY = Float.MAX_VALUE;
                                float maxX = Float.MIN_VALUE;
                                float maxY = Float.MIN_VALUE;
                                for (int i = 0; i < 4; ++i) {
                                    if (points.get(i).x < minX) {
                                        minX = points.get(i).x;
                                    }
                                    if (points.get(i).y < minY) {
                                        minY = points.get(i).y;
                                    }
                                    if (points.get(i).x > maxX) {
                                        maxX = points.get(i).x;
                                    }
                                    if (points.get(i).y > maxY) {
                                        maxY = points.get(i).y;
                                    }
                                }
                                canvas.drawRect(minX, minY, maxX, maxY, paint);
                            } else {
                                canvas.drawPath(toDraw, paint);
                            }
                        } else if (shapeType == SHAPE_TYPE_CIRCLE) {
                            //ShapeType.CIRCLE
                            float centerX = 0;
                            float centerY = 0;
                            for (Point point : points) {
                                centerX += point.x;
                                centerY += point.y;
                            }
                            Point center =
                                    points != null && points.size() > 0 ?
                                            new Point(centerX / points.size(),
                                                    centerY / points.size()) :
                                            new Point(0, 0);
                            float radiusIt = 0;
                            for (Point point : points) {
                                radiusIt += Math.hypot(point.x - center.x, point.y - center.y);
                            }
                            float radius =
                                    points != null && points.size() > 0 ?
                                            radiusIt / points.size() :
                                            0;
                            if (radius > 0) {
                                canvas.drawCircle(center.x, center.y, radius, paint);
                            } else {
                                canvas.drawPath(toDraw, paint);
                            }
                        } else if (shapeType == SHAPE_TYPE_TRIANGLE) {
                            if (points.size() >= 3) {
                                Path path = new Path();
                                path.moveTo(points.get(0).x, points.get(0).y);
                                path.lineTo(points.get(1).x, points.get(1).y);
                                path.lineTo(points.get(2).x, points.get(2).y);
                                path.close();
                                canvas.drawPath(path, paint);
                            } else {
                                canvas.drawPath(toDraw, paint);
                            }
                        } else if (shapeType == SHAPE_TYPE_POLYGON) {
                            if (points.size() >= 3) {
                                Path path = new Path();
                                path.moveTo(points.get(0).x, points.get(0).y);
                                for (int i = 1; i < points.size(); ++i) {
                                    path.lineTo(points.get(i).x, points.get(i).y);
                                }
                                path.close();
                                canvas.drawPath(path, paint);
                            } else {
                                canvas.drawPath(toDraw, paint);
                            }
                        } else {
                            canvas.drawPath(toDraw, paint);
                        }
                    }
                } else {
                    canvas.drawPath(toDraw, paint);
                }

                if (false) { //for DEBUG
                    PathMeasure pm = new PathMeasure(path, false);
                    float[] pos = new float[2];
                    float[] tan = new float[2];
                    int count = (int) Math.floor(pm.getLength());
                    float[] pointsArray = new float[count * 2];

                    Log.e("path", "public PointF[] path = {");
                    for (int i = 0; i < count; i++) {
                        pm.getPosTan(i, pos, tan);
                        pointsArray[i * 2] = pos[0];
                        pointsArray[i * 2 + 1] = pos[1];
                        Log.e("path", "new PointF(" + pos[0] + "f, " + pos[1] + "f),");
                    }
                    Log.e("path", "};");
                }
            } else if (pointsType == POINTS_TYPE_TEXT) {
//                if (false) {
//                    //canvas.drawPath(toDraw, paint);
//                    paint.reset();
//                    paint.setTextSize(28);
//                    paint.setColor(Color.BLACK);
//                    paint.setStyle(Paint.Style.FILL); //FIXME:draw text don't use stroke style
//                    paint.setAntiAlias(true);
//                    if (false) {
//                        //text is left bottom align
//                        canvas.drawText(pointsText, pointsTextX, pointsTextY, paint);
//                    } else {
//                        //text is left top align
//                        int style = Typeface.NORMAL;
//                        if (this.isBold) {
//                            style |= Typeface.BOLD;
//                        }
//                        if (this.isItalics) {
//                            style |= Typeface.ITALIC;
//                        }
//                        Typeface family = Typeface.DEFAULT;
//                        if (this.styleType == BookActivity4Fragment.STYLE_TYPE_NONE) {
//
//                        } else if (this.styleType == BookActivity4Fragment.STYLE_TYPE_HAND) {
//
//                        } else if (this.styleType == BookActivity4Fragment.STYLE_TYPE_SERIF) {
//                            family = Typeface.SERIF;
//                        } else if (this.styleType == BookActivity4Fragment.STYLE_TYPE_SANS) {
//                            family = Typeface.SANS_SERIF;
//                        }
//                        Typeface font = Typeface.create(family, style);
//                        paint.setTypeface(font);
//                        if (this.isUnderline) {
//                            paint.setUnderlineText(true);
//                        }
//                        if (this.pointsTextColor != 0) {
//                            paint.setColor(this.pointsTextColor);
//                        }
//                        if (this.pointsTextSize != 0) {
//                            paint.setTextSize(this.pointsTextSize);
//                        }
//
//                        if (false) {
//                            float textWidth = paint.measureText(pointsText);
//                            float x = pointsTextX;
//                            float y = pointsTextY - paint.ascent() - ((paint.descent() - paint.ascent()) / 2);
//                            canvas.drawText(pointsText, x, y, paint);
//                        } else {
//                            //https://blog.csdn.net/wangjiang_qianmo/article/details/73180042
//                            Rect bounds = new Rect();
//                            paint.getTextBounds(pointsText, 0, pointsText.length(), bounds);
//                            Paint.FontMetrics fontMetrics = paint.getFontMetrics();
//                            canvas.drawText(pointsText, pointsTextX,
//                                    pointsTextY + (fontMetrics.bottom - fontMetrics.top + fontMetrics.leading),
//                                    paint);
//                        }
//                    }
//                } else {
                    Paint p = null;
//                    if (USE_TEMP_PAINT && this.tempPaint != null) {
//                        p = this.tempPaint;
//                    } else {
                        DrawCanvas.getTextPaint(paint, this.isBold, this.isItalics,
                                this.styleType, this.isUnderline,
                                this.pointsTextColor, this.pointsTextSize);
                        p = paint;
//                    }
                    SizeF size = DrawCanvas.calculateTextSizes(pointsText, p, pointsTextType);
                    float w = size.getWidth();
                    float h = size.getHeight();
                    canvas.save();
                    Matrix matrix = new Matrix();
                    matrix.setScale(this.pointsScaleX,
                            this.pointsScaleY,
                            this.pointsTextX,// + w / 2.0F * pointsScaleX,
                            this.pointsTextY);// + h / 2.0F * pointsScaleX);
                    canvas.concat(matrix); //canvas.concat(this.getMatrix());
                    if (this.tempHidden && !drawMinimal) {
                        //temp hide when edit text
                    } else {
                        if (pointsTextType == DrawPath.POINTS_TEXT_TYPE_RICH) {
                            //<p dir="ltr" style="margin-top:0; margin-bottom:0;"><u>hello</u></p>
                            DrawCanvas.drawTextSizes(canvas, pointsText,
                                    pointsTextX,// - w / 2.0F,
                                    pointsTextY + OFFSET_Y, // - h / 2.0F,
                                    p, pointsTextType);
                        } else {
                            DrawCanvas.drawTextSizes(canvas, pointsText,
                                    pointsTextX,// - w / 2.0F,
                                    pointsTextY + OFFSET_Y, // - h / 2.0F,
                                    p, pointsTextType);
                        }
                    }
                    canvas.restore();
//                }
            } else if (pointsType == POINTS_TYPE_IMAGE) {
                //canvas.drawPath(toDraw, paint);
                paint.reset();
                paint.setStyle(Paint.Style.FILL);
                paint.setAntiAlias(true);
                //text is left bottom align
                if (pointsBitmap != null) {
                    canvas.save();
                    Matrix matrix = new Matrix();
                    matrix.setScale(this.pointsScaleX, this.pointsScaleY, this.pointsTextX, this.pointsTextY);
                    canvas.concat(matrix); //canvas.concat(this.getMatrix());
                    {
                        canvas.drawBitmap(pointsBitmap, pointsTextX - pointsBitmap.getWidth() / 2, pointsTextY - pointsBitmap.getHeight() / 2, paint);
                        boolean debug = false;
                        if (debug) {
                            paint.setColor(0xCCFF0000); //debug area, red region
                            canvas.drawPath(toDraw, paint);
                        }
                    }
                    canvas.restore();
                }
            }
        } else {

        }
        // If enabled, draw points on top of everything else
        if (drawPoints) {
            // Laggy but provides good contrast
            //paint.setBlendMode(BlendMode.EXCLUSION);
            paint.setStrokeWidth(screenDensity / scaleFactor);
            for (Point pt : points) {
                Path shape = pt.getShape(6 * screenDensity / scaleFactor);
                // Fill
                paint.setColor(pt.color);
                paint.setStyle(Paint.Style.FILL);
                canvas.drawPath(shape, paint);
                // Stroke
                paint.setColor(Color.BLACK);
                paint.setStyle(Paint.Style.STROKE);
                canvas.drawPath(shape, paint);
                // HANDLES
                paint.setColor(pt.color);
                paint.setStyle(Paint.Style.FILL);
                canvas.drawPath(pt.getLeftHandle().getShape(4 * screenDensity / scaleFactor), paint);
                canvas.drawPath(pt.getRightHandle().getShape(4 * screenDensity / scaleFactor), paint);
                // HANDLE LINES
                paint.setStyle(Paint.Style.STROKE);
                canvas.drawLine(pt.getLeftHandle().x, pt.getLeftHandle().y, pt.x, pt.y, paint);
                canvas.drawLine(pt.getRightHandle().x, pt.getRightHandle().y, pt.x, pt.y, paint);
                // Done
            }
        }
    }

    //
    //new pencil implementation, 20260429, from Free-hand Drawing App
    //https://www.figma.com/design/GJTGvpoKP8En3dkw0H2VBl/Free-hand-Drawing-App
    //
    private void drawStroke(Canvas ctx,
                             Point from,
                             Point to,
                             double size,
                             int color,
                             boolean useDrawPoints,
                            List<Float> toDrawList
    ) {
        Random rand = new Random(0);
        Paint paint = new Paint();
        double distance = Math.sqrt(Math.pow(to.x - from.x, 2) + Math.pow(to.y - from.y, 2));
        int steps = (int)Math.max(Math.ceil(distance), 1);
        for (int i = 0; i <= steps; i++) {
            double t = (float)i / steps;
            double x = from.x + (to.x - from.x) * t;
            double y = from.y + (to.y - from.y) * t;
            double pressure = from.pressure + (to.pressure - from.pressure) * t;

            drawPoint(ctx, x, y, pressure, size, color, paint, rand, useDrawPoints, toDrawList);
        }
    }

    private final boolean FORCE_PENCIL_SIMPLIFY = false;
    private void drawPoint(Canvas canvas,
                            double x,
                            double y,
                            double pressure,
                            double size,
                            int color,
                           Paint paint,
                           Random rand,
                           boolean useDrawPoints,
                           List<Float> toDrawList
    ) {
        final double MAX_LOOP = 1.0;//5.0;//Double.MAX_VALUE;//5.0;//1.0;//2.0;//5.0;
        //FIXME:added, loop times don't over MAX_LOOP (like 5.0)

        double effectivePressure = Math.max(0.15, Math.min(1, pressure));
        double radius = size * effectivePressure;

        double particleDensity = 0.3 + effectivePressure * 0.7;
        double particleCount = Math.min(MAX_LOOP, Math.floor(radius * 15 * particleDensity));

        double particleOpacity = 0.08 + effectivePressure * 0.15;

        //canvas.save();
        if (true) {
            for (int i = 0; i < particleCount; i++) {
                if (rand.nextDouble() > particleDensity) continue;

                double angle = rand.nextDouble() * Math.PI * 2;
                double distance = Math.sqrt(rand.nextDouble()) * radius;
                double px = x + Math.cos(angle) * distance;
                double py = y + Math.sin(angle) * distance;

                double particleSize = 0.3 + rand.nextDouble() * (0.8 + effectivePressure * 0.8);
                double opacity = particleOpacity * (0.5 + rand.nextDouble() * 0.5);

//            ctx.globalAlpha = opacity;
//            ctx.fillStyle = color;
                paint.setStyle(Paint.Style.FILL);
                paint.setColor((color & 0xFFFFFF) |
                        ((((int) (0xFF * opacity)) & 0xFF) << 24));
                if (useDrawPoints) {
                    if (toDrawList != null) {
                        toDrawList.add((float) (px));
                        toDrawList.add((float) (py));
                    }
                } else {
                    canvas.drawRect(
                            (float) (px - particleSize / 2),
                            (float) (py - particleSize / 2),
                            (float) (px - particleSize / 2) + (float) (particleSize),
                            (float) (py - particleSize / 2) + (float) (particleSize),
                            paint
                    );
                }
//                canvas.drawCircle(
//                        (float) (px - particleSize / 2),
//                        (float) (py - particleSize / 2),
//                        (float)size,
//                        paint);
            }

            if (true) {
                double edgeParticles = Math.min(MAX_LOOP, Math.floor(radius * 3));
                for (int i = 0; i < edgeParticles; i++) {
                    double angle = rand.nextDouble() * Math.PI * 2;
                    double distance = radius * (0.7 + rand.nextDouble() * 0.6);
                    double px = x + Math.cos(angle) * distance;
                    double py = y + Math.sin(angle) * distance;

                    double particleSize = 0.3 + rand.nextDouble() * 0.8;
                    double opacity = particleOpacity * 0.3 * (0.3 + rand.nextDouble() * 0.7);

                    //ctx.globalAlpha = opacity;
                    //ctx.fillStyle = color;
                    paint.setStyle(Paint.Style.FILL);
                    paint.setColor((color & 0xFFFFFF) |
                            ((((int) (0xFF * opacity)) & 0xFF) << 24));
                    if (useDrawPoints) {
                        if (toDrawList != null) {
                            toDrawList.add((float) (px));
                            toDrawList.add((float) (py));
                        }
                    } else {
                        if (true) {
                            canvas.drawRect(
                                    (float) (px - particleSize / 2),
                                    (float) (py - particleSize / 2),
                                    (float) (px - particleSize / 2) + (float) (particleSize),
                                    (float) (py - particleSize / 2) + (float) (particleSize),
                                    paint
                            );
                        } else {
//                            canvas.drawCircle(
//                                    (float) (px - particleSize / 2),
//                                    (float) (py - particleSize / 2),
//                                    (float) particleSize,
//                                    paint);
                        }
                    }
                }
            }
//            canvas.restore();
        } else {
            paint.setStyle(Paint.Style.FILL);
            paint.setColor((color & 0xFFFFFF) | 0xFF000000);
            canvas.drawCircle((float)x, (float)y, (float)size, paint);
        }
    }

    /**
     * Erases a path from another path -- assumes `path` is closed.
     *
     * @param path The path to erase.
     */
    public boolean erase(DrawPath path) {
        // If there's no path to erase we can't do an erasing operation �
        if (getPath() == null) {
            return false;
        }
        if (isClosed) {
            Path erased = getPathOrGenerate();
            erased.op(path.generatePath(), Path.Op.DIFFERENCE);
            regeneratePoints(erased);
        } else {
            eraseFromStroke(path);
        }
        return false;
    }
    public void erasePath() {
        this.clear();
        this.cachePath();
        if (pointsType == DrawPath.POINTS_TYPE_IMAGE) {
            this.pointsBitmap = null;
        } else if (pointsType == DrawPath.POINTS_TYPE_TEXT) {
            this.pointsText = null;
        }
    }
    public boolean eraseSimple(DrawPath path_) {
        // If there's no path to erase we can't do an erasing operation �
        if (getPath() == null) {
            return false;
        }
        boolean isContain = false;
        if (false) {
            //判断划过的地方是否包含已有笔划
            for (Point point : points) {
                if (path_.containsSimple(point)) {
                    isContain = true;
                    break;
                }
            }
        } else if (false) { //wrong, don't see path_.pointsType, see this.pointsType
            //改成判断已有笔划是否包含划过的地方
//            if (path_.pointsType == DrawPath.POINTS_TYPE_STROKE) {
//                for (Point point : path_.points) {
//                    if (this.containsSimple(point)) {
//                        isContain = true;
//                        break;
//                    }
//                }
//            } else if (path_.pointsType == DrawPath.POINTS_TYPE_IMAGE) {
//                if (pointsBitmap != null) {
//                    float w = pointsBitmap != null ? pointsBitmap.getWidth() : 0;
//                    float h = pointsBitmap != null ? pointsBitmap.getHeight() : 0;
//                    //for calculating midpoint
//                    float tempPointXMin = pointsTextX - pointsScaleX * w / 2.0F;
//                    float tempPointXMax = pointsTextX + pointsScaleX * w / 2.0F;
//                    float tempPointYMin = pointsTextY - pointsScaleX * h / 2.0F;
//                    float tempPointYMax = pointsTextY + pointsScaleX * h / 2.0F;
//                    if (this.containsRect(
//                            new Point(tempPointXMin, tempPointYMin),
//                            new Point(tempPointXMax, tempPointYMax))) {
//                        isContain = true;
//                    }
//                }
//            } else if (path_.pointsType == DrawPath.POINTS_TYPE_TEXT) {
//                float w = 0;
//                float h = 0;
//                if (pointsText != null) {
//                    Paint p = new Paint();
//                    DrawCanvas.getTextPaint(p,
//                            isBold,
//                            isItalics,
//                            styleType,
//                            isUnderline,
//                            pointsTextColor,
//                            pointsTextSize);
//                    SizeF size = DrawCanvas.calculateTextSizes(pointsText, p, pointsTextType);
//                    w = size.getWidth();
//                    h = size.getHeight();
//                    float tempPointXMin = pointsTextX;
//                    float tempPointXMax = pointsTextX + pointsScaleX * w;
//                    float tempPointYMin = pointsTextY;
//                    float tempPointYMax = pointsTextY + pointsScaleX * h;
//                    if (this.containsRect(
//                            new Point(tempPointXMin, tempPointYMin),
//                            new Point(tempPointXMax, tempPointYMax))) {
//                        isContain = true;
//                    }
//                }
//            }
        } else {
            //if true
            //改成判断已有笔划是否包含划过的地方
            for (Point point : path_.points) {
                if (this.pointsType == DrawPath.POINTS_TYPE_STROKE) {
                    if (this.containsSimple(point)) {
                        isContain = true;
                        break;
                    }
                } else if (this.pointsType == DrawPath.POINTS_TYPE_IMAGE) {
                    if (pointsBitmap != null) {
                        float w = pointsBitmap != null ? pointsBitmap.getWidth() : 0;
                        float h = pointsBitmap != null ? pointsBitmap.getHeight() : 0;
                        //for calculating midpoint
                        float tempPointXMin = pointsTextX - pointsScaleX * w / 2.0F;
                        float tempPointXMax = pointsTextX + pointsScaleX * w / 2.0F;
                        float tempPointYMin = pointsTextY - pointsScaleX * h / 2.0F;
                        float tempPointYMax = pointsTextY + pointsScaleX * h / 2.0F;
                        if (false) {
                            if (this.containsRect(
                                    new Point(tempPointXMin, tempPointYMin),
                                    new Point(tempPointXMax, tempPointYMax))) {
                                isContain = true;
                            }
                        } else {
                            if (point.x >= tempPointXMin && point.x <= tempPointXMax &&
                                    point.y >= tempPointYMin && point.y <= tempPointYMax) {
                                isContain = true;
                            }
                        }
                    }
                } else if (this.pointsType == DrawPath.POINTS_TYPE_TEXT) {
                    float w = 0;
                    float h = 0;
                    if (pointsText != null) {
                        Paint p = new Paint();
                        DrawCanvas.getTextPaint(p,
                                isBold,
                                isItalics,
                                styleType,
                                isUnderline,
                                pointsTextColor,
                                pointsTextSize);
                        SizeF size = DrawCanvas.calculateTextSizes(pointsText, p, pointsTextType);
                        w = size.getWidth();
                        h = size.getHeight();
                        float tempPointXMin = pointsTextX;
                        float tempPointXMax = pointsTextX + pointsScaleX * w;
                        float tempPointYMin = pointsTextY;
                        float tempPointYMax = pointsTextY + pointsScaleX * h;
                        if (false) {
                            if (this.containsRect(
                                    new Point(tempPointXMin, tempPointYMin),
                                    new Point(tempPointXMax, tempPointYMax))) {
                                isContain = true;
                            }
                        } else {
                            if (point.x >= tempPointXMin && point.x <= tempPointXMax &&
                                    point.y >= tempPointYMin && point.y <= tempPointYMax) {
                                isContain = true;
                            }
                        }
                    }
                }
            }
        }
        if (isContain) {
            clear();
        }
        return isContain;
    }

    /**
     * Regenerates points[] from DrawPath.path (android.graphics.Path) using Pathway
     * TODO curves?
     */
    public void regeneratePoints(Path path) {
        PathIterator iterator = Paths.iterator(path);
        points.clear();
        float[] pointArray = new float[8];
        while (iterator.hasNext()) {
            PathSegment.Type type = iterator.next(pointArray, 0); // The type of segment
            if (type != PathSegment.Type.Close) {
                Point point = new Point(pointArray[0], pointArray[1]);
                point.command = type == PathSegment.Type.Move ? Point.COMMANDS.move : Point.COMMANDS.line;
                points.add(point);
            }
        }
    }

    /**
     * Erases a closed path from a stroke by removing points in the stroke that are in contact with the
     * filled shape
     *
     * @param erasePath The path to erase.
     */
    public void eraseFromStroke(DrawPath erasePath) {
        int index = 0;
        boolean state = false; // All the points we are looking at (to our knowledge) don't collide with erasePath
        while (index < points.size()) {
            boolean oldState = state;
            Point point = points.get(index);
            if (erasePath.contains(point)) {
                points.remove(index);
                state = true;
            } else {
                state = false;
                index++;
            }
            // If there's a STATE CHANGE
            if (oldState != state) {
                if (!state) {
                    point.command = Point.COMMANDS.move;
                }
            }
        }
    }

    /**
     * Translates all points in a path by an amount, in pixels.
     *
     * @param by The amount to translate all points in the DrawPath by
     */
    public void translate(Point by) {
        for (Point point : points) {
            point.add(by);
        }
        if (pointsType == POINTS_TYPE_IMAGE) {
            pointsTextX += by.x;
            pointsTextY += by.y;
        } else if (pointsType == POINTS_TYPE_TEXT) {
            pointsTextX += by.x;
            pointsTextY += by.y;
        }
    }

    public void translateBegin() {
        for (Point point : points) {
            point.tempX = point.x;
            point.tempY = point.y;
        }
        tempX = pointsTextX;
        tempY = pointsTextY;
//        tempMatrix.set(matrix);
    }

    public void translateSave(Point by) {
        if (pointsType == POINTS_TYPE_IMAGE) {
            pointsTextX = tempX + by.x;// / tempScaleX;
            pointsTextY = tempY + by.y;// / tempScaleY;
//            matrix.set(tempMatrix);
//            matrix.postTranslate(by.x, by.y);
            for (Point point : points) {
                point.x = point.tempX + by.x;// / tempScaleX;
                point.y = point.tempY + by.y;// / tempScaleY;
            }
        } else if (pointsType == POINTS_TYPE_TEXT) {
            pointsTextX = tempX + by.x;// / tempScaleX;
            pointsTextY = tempY + by.y;// / tempScaleY;
//            matrix.set(tempMatrix);
//            matrix.postTranslate(by.x, by.y);
            for (Point point : points) {
                point.x = point.tempX + by.x;// / tempScaleX;
                point.y = point.tempY + by.y;// / tempScaleY;
            }
        } else if (pointsType == POINTS_TYPE_STROKE) {
            for (Point point : points) {
                point.x = point.tempX + by.x;// / tempScaleX;
                point.y = point.tempY + by.y;// / tempScaleY;
            }
        }
    }

    public ArrayList<Point> points_beforeScale = new ArrayList<>();
    public void beginScale() {
        points_beforeScale.clear();
        for (Point point: points) {
            points_beforeScale.add(point.clone());
        }
    }
    public void endScale() {
        points_beforeScale.clear();
    }
    public void scale(Point center, Point by) {
        double scale = Math.sqrt(by.x * by.x + by.y * by.y) / Math.sqrt(2) / 10;
        for (int i = 0; i < points.size(); ++i) {
            Point point = points.get(i);
            Point point_beforeScale = points_beforeScale.get(i);
            point.x = (float)(center.x + (point_beforeScale.x - center.x) * scale);
            point.y = (float)(center.y + (point_beforeScale.y - center.y) * scale);
        }
    }

    /**
     * Point-shape collisions. This should be better than other implementations because by using Path.op we can account
     * for cases where getPath() returns a path with curves instead of a polygon with straight lines!
     * We create a test path with a circle of radius of 1, and then do Point.op with that and the current path.
     * <b>Prioritizes using a generated path, but if it doesn't exist will use generate()</b>
     *
     * @param point The point to test
     * @return Whether or not <code>point</code> is inside of the DrawPath's path.
     */
    public boolean contains(Point point) {
        Path pointPath = new Path();
        if (true) {
            pointPath.addCircle(point.x, point.y, 1, Path.Direction.CW);
        } else {
            //pointPath.addCircle(point.x, point.y, 10, Path.Direction.CW);
        }
        pointPath.op(getPathOrGenerate(), Path.Op.DIFFERENCE);
        return pointPath.isEmpty();
    }
    public boolean containsSimple(Point point) {
        Path pointPath = new Path();
        if (true) {
            pointPath.addCircle(point.x, point.y, 20, Path.Direction.CW);
        } else {
            //pointPath.addCircle(point.x, point.y, 5, Path.Direction.CW);
        }
        Path path_g = getPathOrGenerate();
        pointPath.op(path_g, Path.Op.DIFFERENCE);
        RectF bounds = new RectF();
        path_g.computeBounds(bounds, false);
        boolean result0 = bounds.contains(point.x, point.y);
        boolean result1 = pointPath.isEmpty();
        boolean result2 = false;
        if (this.points != null) {
            for (Point p : this.points) {
                if (p != null) {
                    float dis2 = (p.x - point.x) * (p.x - point.x) +
                            (p.y - point.y) * (p.y - point.y);
                    if (dis2 < 20 * 20) {
                        result2 = true;
                        break;
                    }
                }
            }
        }
        return result0 || result1 || result2;
    }

    public boolean containsRect(Point point1, Point point2) {
        Path pointPath = new Path();
        pointPath.addRect(point1.x, point1.y, point2.x, point2.y, Path.Direction.CW);
        pointPath.op(getPathOrGenerate(), Path.Op.DIFFERENCE);
        return pointPath.isEmpty();
    }

    /**
     * Deep clones a DrawPath.
     *
     * @return A cloned version of the DrawPath.
     */
    @NonNull
    @Override
    public DrawPath clone() {
        DrawPath cloned = new DrawPath(new Path(), this.pointsType);
        // 1. Copy variables.
        cloned.drawPoints = drawPoints;
        cloned.isClosed = isClosed;
        cloned.appearance = appearance.clone();
        // 2. Copy points.
        for (Point point : points) {
            cloned.points.add(point.clone());
        }
        cloned.cachePath();

        //---------------------
        //added
        cloned.pointsTextType = pointsTextType;
        cloned.pointsText = pointsText;
        cloned.pointsBitmap = pointsBitmap;
        cloned.pointsTextX = pointsTextX;
        cloned.pointsTextY = pointsTextY;
        cloned.pointsType = pointsType;
        cloned.isBold = isBold;
        cloned.isItalics = isItalics;
        cloned.isUnderline = isUnderline;
        cloned.styleType = styleType;
        cloned.pointsTextColor = pointsTextColor;
        cloned.pointsTextSize = pointsTextSize;
        cloned.tempPaint = tempPaint;
        cloned.tempX = tempX;
        cloned.tempY = tempY;

        cloned.tempScaleX = tempScaleX;
        cloned.tempScaleY = tempScaleY;
        cloned.tempMidX = tempMidX;
        cloned.tempMidY = tempMidY;

//        cloned.matrix.set(matrix);
        cloned.pointsScaleX = pointsScaleX;
        cloned.pointsScaleY = pointsScaleY;

        cloned.shapeType = shapeType;
        for (Point point : preSimplified) {
            cloned.preSimplified.add(point.clone());
        }

        cloned.shapeSide = shapeSide;
        cloned.shapeWidth = shapeWidth;
        cloned.shapeHeight = shapeHeight;

        return cloned;
    }

}
