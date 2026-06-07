package io.github.pastthepixels.freepaint.Graphics;

import android.view.MotionEvent;

public class RotateGestureDetector {
    public interface OnRotateGestureListener {
        boolean onRotate(float deltaAngle, float totalAngle, float focusX, float focusY);
    }

    private OnRotateGestureListener listener;
    private float startAngle;
    private float totalAngle;
    private boolean isRotating;
    private float threshold = 15f;

    public RotateGestureDetector(OnRotateGestureListener listener) {
        this.listener = listener;
    }

    public void onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        int pointerCount = event.getPointerCount();

        switch (action) {
            case MotionEvent.ACTION_POINTER_DOWN:
                if (pointerCount == 2) {
                    startAngle = getAngle(event);
                    isRotating = true;
                    totalAngle = 0f;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (isRotating && pointerCount == 2) {
                    float curAngle = getAngle(event);
                    float delta = curAngle - startAngle;
                    if (delta > 180) delta -= 360;
                    if (delta < -180) delta += 360;

                    if (Math.abs(delta) >= threshold) {
                        totalAngle += delta;
                        float focusX = (event.getX(0) + event.getX(1)) / 2;
                        float focusY = (event.getY(0) + event.getY(1)) / 2;
                        if (listener != null) {
                            listener.onRotate(delta, totalAngle, focusX, focusY);
                        }
                        startAngle = curAngle;
                    }
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_UP:
                isRotating = false;
                break;
        }
    }

    private float getAngle(MotionEvent event) {
        float dx = event.getX(0) - event.getX(1);
        float dy = event.getY(0) - event.getY(1);
        return (float) Math.toDegrees(Math.atan2(dy, dx));
    }
}

