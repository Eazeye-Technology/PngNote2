package com.strokeapp.domain.usecase;

import com.strokeapp.domain.model.ShapeType;
import com.strokeapp.domain.model.SimplifiedShape;
import com.strokeapp.domain.model.Stroke;
import com.strokeapp.domain.model.StrokePoint;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.ranges.RangesKt;
import org.jetbrains.annotations.NotNull;

public final class StrokeSimplifierUseCase {
//    public static final int $stable;

    //tolerance=1.f to 50.f, default 10.f
    @NotNull
    public final Stroke simplify(@NotNull Stroke stroke, float tolerance) {
        Intrinsics.checkNotNullParameter(stroke, "stroke");
        if (stroke.getPoints().size() < 3) {
            return stroke;
        } else {
            List<StrokePoint> simplified = this.ramerDouglasPeucker(stroke.getPoints(), tolerance);
            return Stroke.copy$default(stroke, simplified, 0, 2, (Object)null);
        }
    }

    private final List<StrokePoint> ramerDouglasPeucker(List<StrokePoint> points, float epsilon) {
        if (points.size() < 3) {
            return points;
        } else {
            float maxDistance = 0.0F;
            int maxIndex = 0;
            StrokePoint first = (StrokePoint)CollectionsKt.first(points);
            StrokePoint last = (StrokePoint)CollectionsKt.last(points);
            int i = 1;

            for(int var8 = points.size() - 1; i < var8; ++i) {
                float d = this.perpendicularDistance((StrokePoint)points.get(i), first, last);
                if (d > maxDistance) {
                    maxDistance = d;
                    maxIndex = i;
                }
            }

            List<StrokePoint> var10000;
            if (maxDistance > epsilon) {
                List<StrokePoint> left = this.ramerDouglasPeucker(points.subList(0, maxIndex + 1), epsilon);
                List<StrokePoint> right = this.ramerDouglasPeucker(points.subList(maxIndex, points.size()), epsilon);
                var10000 = CollectionsKt.plus((Collection)CollectionsKt.dropLast(left, 1), (Iterable)right);
            } else {
                StrokePoint[] var11 = new StrokePoint[]{first, last};
                var10000 = CollectionsKt.listOf(var11);
            }

            return var10000;
        }
    }

    private final float perpendicularDistance(StrokePoint point, StrokePoint lineStart, StrokePoint lineEnd) {
        float dx = lineEnd.getX() - lineStart.getX();
        float dy = lineEnd.getY() - lineStart.getY();
        float mag = (float)Math.hypot((double)dx, (double)dy);
        if (mag == 0.0F) {
            return (float)Math.hypot((double)(point.getX() - lineStart.getX()), (double)(point.getY() - lineStart.getY()));
        } else {
            float u = ((point.getX() - lineStart.getX()) * dx + (point.getY() - lineStart.getY()) * dy) / (mag * mag);
            float closestX = lineStart.getX() + u * dx;
            float closestY = lineStart.getY() + u * dy;
            return (float)Math.hypot((double)(point.getX() - closestX), (double)(point.getY() - closestY));
        }
    }

    @NotNull
    public final SimplifiedShape recognizeShape(@NotNull Stroke stroke) {
        Intrinsics.checkNotNullParameter(stroke, "stroke");
        List<StrokePoint> points = stroke.getPoints();
        if (points.size() < 2) {
            return new SimplifiedShape(ShapeType.UNKNOWN, points, 4287137928L);
        } else {
            return points.size() == 2 ? new SimplifiedShape(ShapeType.LINE, points, 4280391411L) : (this.isRectangle(points) ? new SimplifiedShape(ShapeType.RECTANGLE, points, 4283215696L) : (this.isCircle(points) ? new SimplifiedShape(ShapeType.CIRCLE, points, 4294940672L) : (this.isTriangle(points) ? new SimplifiedShape(ShapeType.TRIANGLE, points, 4288423856L) : new SimplifiedShape(ShapeType.POLYGON, points, 4286141768L))));
        }
    }

    private final boolean isRectangle(List<StrokePoint> points) {
        if (points.size() != 4) {
            return false;
        } else {
            StrokePoint p0 = (StrokePoint)points.get(0);
            StrokePoint p1 = (StrokePoint)points.get(1);
            StrokePoint p2 = (StrokePoint)points.get(2);
            StrokePoint p3 = (StrokePoint)points.get(3);
            double angle1 = this.getAngle(p0, p1, p2);
            double angle2 = this.getAngle(p1, p2, p3);
            double angle3 = this.getAngle(p2, p3, p0);
            double angle4 = this.getAngle(p3, p0, p1);
            Double[] var16 = new Double[]{angle1, angle2, angle3, angle4};
            Iterable $this$all$iv = (Iterable)CollectionsKt.listOf(var16);
            int $i$f$all = 0;
            boolean var10000;
            if ($this$all$iv instanceof Collection && ((Collection)$this$all$iv).isEmpty()) {
                var10000 = true;
            } else {
                label101: {
                    for(Object element$iv : $this$all$iv) {
                        double it = ((Number)element$iv).doubleValue();
                        int var22 = 0;
                        if (!(Math.abs(it - (Math.PI / 2D)) < 0.3)) {
                            var10000 = false;
                            break label101;
                        }
                    }

                    var10000 = true;
                }
            }

            boolean isRightAngles = var10000;
            Iterable $this$map$iv = (Iterable)points;
            int $i$f$map = 0;
            Collection destination$iv$iv = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault($this$map$iv, 10)));
            int $i$f$mapTo = 0;

            for(Object item$iv$iv : $this$map$iv) {
                StrokePoint it = (StrokePoint)item$iv$iv;
                int var25 = 0;
                destination$iv$iv.add(it.getX());
            }

            List<StrokePoint> it = (List<StrokePoint>)destination$iv$iv;
            int $this$mapTo$iv$iv = 0;
            Float minX = (Float)Collections.min((Collection)it);
            $this$map$iv = (Iterable)points;
            $this$mapTo$iv$iv = 0;
            destination$iv$iv = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault($this$map$iv, 10)));
            $i$f$mapTo = 0;

            for(Object item$iv$iv : $this$map$iv) {
                StrokePoint it_ = (StrokePoint)item$iv$iv;
                int var26 = 0;
                destination$iv$iv.add(it_.getX());
            }

            it = (List)destination$iv$iv;
            $this$mapTo$iv$iv = 0;
            Float maxX = (Float)Collections.max((Collection)it);
            $this$map$iv = (Iterable)points;
            $this$mapTo$iv$iv = 0;
            destination$iv$iv = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault($this$map$iv, 10)));
            $i$f$mapTo = 0;

            for(Object item$iv$iv : $this$map$iv) {
                StrokePoint it_ = (StrokePoint)item$iv$iv;
                int var27 = 0;
                destination$iv$iv.add(it_.getY());
            }

            it = (List)destination$iv$iv;
            $this$mapTo$iv$iv = 0;
            Float minY = (Float)Collections.min((Collection)it);
            $this$map$iv = (Iterable)points;
            $this$mapTo$iv$iv = 0;
            destination$iv$iv = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault($this$map$iv, 10)));
            $i$f$mapTo = 0;

            for(Object item$iv$iv : $this$map$iv) {
                StrokePoint it_ = (StrokePoint)item$iv$iv;
                int var28 = 0;
                destination$iv$iv.add(it_.getY());
            }

            it = (List)destination$iv$iv;
            int var56 = 0;
            Float maxY = (Float)Collections.max((Collection)it);
            float var69 = maxY;
            Intrinsics.checkNotNull(minY);
            if (var69 - minY > 0.0F) {
                var69 = maxX;
                Intrinsics.checkNotNull(minX);
                var69 = (var69 - minX) / (maxY - minY);
            } else {
                var69 = 1.0F;
            }

            float aspectRatio = var69;
            return isRightAngles && (0.3F <= aspectRatio ? aspectRatio <= 3.5F : false);
        }
    }

    private final double getAngle(StrokePoint p1, StrokePoint vertex, StrokePoint p2) {
        float v1x = p1.getX() - vertex.getX();
        float v1y = p1.getY() - vertex.getY();
        float v2x = p2.getX() - vertex.getX();
        float v2y = p2.getY() - vertex.getY();
        float dot = v1x * v2x + v1y * v2y;
        double mag1 = Math.sqrt((double)(v1x * v1x + v1y * v1y));
        double mag2 = Math.sqrt((double)(v2x * v2x + v2y * v2y));
        if (mag1 != (double)0.0F && mag2 != (double)0.0F) {
            double cosAngle = RangesKt.coerceIn((double)dot / (mag1 * mag2), (double)-1.0F, (double)1.0F);
            return Math.acos(cosAngle);
        } else {
            return (double)0.0F;
        }
    }

    private final boolean isCircle(List<StrokePoint> points) {
        Iterable $this$map$iv = (Iterable)points;
        int $i$f$map = 0;
        Collection destination$iv$iv = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault($this$map$iv, 10)));
        int $i$f$mapTo = 0;

        for(Object item$iv$iv : $this$map$iv) {
            StrokePoint it = (StrokePoint)item$iv$iv;
            int var11 = 0;
            destination$iv$iv.add(it.getX());
        }

        float var10000 = (float)CollectionsKt.averageOfFloat((Iterable)((List)destination$iv$iv));
        $this$map$iv = (Iterable)points;
        float var16 = var10000;
        $i$f$map = 0;
        destination$iv$iv = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault($this$map$iv, 10)));
        $i$f$mapTo = 0;

        for(Object item$iv$iv : $this$map$iv) {
            StrokePoint it = (StrokePoint)item$iv$iv;
            int var42 = 0;
            destination$iv$iv.add(it.getY());
        }

        List<StrokePoint> var17 = (List<StrokePoint>)destination$iv$iv;
        float var18 = (float)CollectionsKt.averageOfFloat((Iterable)var17);
        StrokePoint center = new StrokePoint(var16, var18);
        $this$map$iv = (Iterable)points;
        $i$f$map = 0;
        destination$iv$iv = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault($this$map$iv, 10)));
        $i$f$mapTo = 0;

        for(Object item$iv$iv : $this$map$iv) {
            StrokePoint it = (StrokePoint)item$iv$iv;
            int var12 = 0;
            destination$iv$iv.add((float)Math.hypot((double)(it.getX() - center.getX()), (double)(it.getY() - center.getY())));
        }

        List<StrokePoint> distances = (List<StrokePoint>)destination$iv$iv;
        float avgRadius = (float)CollectionsKt.averageOfFloat((Iterable)distances);
        $this$map$iv = (Iterable)distances;
        $i$f$map = 0;
        destination$iv$iv = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault($this$map$iv, 10)));
        $i$f$mapTo = 0;

        for(Object item$iv$iv : $this$map$iv) {
            float it = ((Number)item$iv$iv).floatValue();
            int var14 = 0;
            destination$iv$iv.add((it - avgRadius) * (it - avgRadius));
        }

        float stdDev = (float)Math.sqrt(CollectionsKt.averageOfFloat((Iterable)((List)destination$iv$iv)));
        StrokePoint first = (StrokePoint)CollectionsKt.first(points);
        StrokePoint last = (StrokePoint)CollectionsKt.last(points);
        boolean $i$f$mapTo_ = (float)Math.hypot((double)(first.getX() - last.getX()), (double)(first.getY() - last.getY())) < avgRadius * 0.5F;
        return $i$f$mapTo_ && stdDev < avgRadius * 0.25F && points.size() >= 8;
    }

    private final boolean isTriangle(List<StrokePoint> points) {
        if (points.size() != 3) {
            return false;
        } else {
            StrokePoint p0 = (StrokePoint)points.get(0);
            StrokePoint p1 = (StrokePoint)points.get(1);
            StrokePoint p2 = (StrokePoint)points.get(2);
            float area = Math.abs((p1.getX() - p0.getX()) * (p2.getY() - p0.getY()) - (p2.getX() - p0.getX()) * (p1.getY() - p0.getY())) / 2.0F;
            if (area < 100.0F) {
                return false;
            } else {
                double angle1 = this.getAngle(p0, p1, p2);
                double angle2 = this.getAngle(p1, p2, p0);
                double angle3 = this.getAngle(p2, p0, p1);
                double sumAngles = angle1 + angle2 + angle3;
                return Math.abs(sumAngles - Math.PI) < (double)0.5F;
            }
        }
    }
}
