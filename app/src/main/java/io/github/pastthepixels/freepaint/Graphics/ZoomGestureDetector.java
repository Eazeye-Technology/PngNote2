package io.github.pastthepixels.freepaint.Graphics;

import android.app.Activity;
import android.content.Context;
import android.view.ScaleGestureDetector;

import androidx.annotation.NonNull;

import com.txkj.drawingapp.activity.BookActivity4Config;
import com.txkj.drawingapp.activity.BookActivity4Utils;

//refer to io.github.pastthepixels.freepaint.Tools.PanTool
public class ZoomGestureDetector extends ScaleGestureDetector {
    public ZoomGestureDetector(final Context context) {
        super(context, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            private final Point lastFocus = new Point(0, 0);
            private float mTranslationX, mTranslationY;

            @Override
            public boolean onScaleBegin(@NonNull ScaleGestureDetector detector) {
                lastFocus.set(
                        detector.getFocusX(),
                        detector.getFocusY()
                );
                return super.onScaleBegin(detector);
            }

            /**
             * This absolute bit of math just makes sure that zooming is from the center of the canvas.
             * Zooming is typically done from the top left corner, but we can use panOffset to move
             * the canvas by an amount to make it look like we zoomed from the center.
             */
            @Override
            public boolean onScale(@NonNull ScaleGestureDetector detector) {
//                scaleFactor *= detector.getScaleFactor();
//                updatePanOffset();
//                canvas.invalidate();

                //FIXME:dont' use getCurrentSpanX, use getFocusX instead
//                float deltaX = detector.getCurrentSpanX() - detector.getPreviousSpanX();
//                float deltaY = detector.getCurrentSpanY() - detector.getPreviousSpanY();
                float deltaX = detector.getFocusX() - lastFocus.x;
                float deltaY = detector.getFocusY() - lastFocus.y;
                lastFocus.set(
                        detector.getFocusX(),
                        detector.getFocusY()
                );
                mTranslationX += deltaX;
                mTranslationY += deltaY;

                if (BookActivity4Config.ENABLE_GESTURE_ZOOM_OUT ||
                        BookActivity4Config.ENABLE_GESTURE_ZOOM_IN) {
                    BookActivity4Utils.scale((Activity) context, detector.getScaleFactor(),
                            mTranslationX, mTranslationY, deltaX, deltaY);
                }
                return true;
            }
        });
    }
}
