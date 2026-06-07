package io.github.pastthepixels.freepaint.Graphics;

import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;

//FIXME: You **MUST** keep boolean onTouchEvent(MotionEvent event) return true
//see https://cloud.tencent.com/developer/ask/sof/100130489
public class ThreeFingerDoubleTapDetector {

    private static final long DOUBLE_TAP_TIMEOUT = 300;

    private long mLastThreeFingerTapTime = 0;
    private boolean mIsFirstTap = true;
    private Runnable mTimeoutRunnable;

    public interface OnThreeFingerDoubleTapListener {
        void onThreeFingerDoubleTap();
    }

    private OnThreeFingerDoubleTapListener mListener;

    public ThreeFingerDoubleTapDetector(OnThreeFingerDoubleTapListener listener) {
        this.mListener = listener;
        mTimeoutRunnable = () -> mIsFirstTap = true;
    }

    public boolean onTouchEvent(MotionEvent event) {
        int pointerCount = event.getPointerCount();
        int action = event.getActionMasked();

        if ((action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_UP)
                && pointerCount == 3) { //2 //3 //1
            long now = System.currentTimeMillis();
            Handler mainHandler = new Handler(Looper.getMainLooper());

            if (mIsFirstTap) {
                mLastThreeFingerTapTime = now;
                mIsFirstTap = false;
//                if (mListener != null) {
//                    mListener.onThreeFingerDoubleTap();
//                }

                mainHandler.removeCallbacks(mTimeoutRunnable);
                mainHandler.postDelayed(mTimeoutRunnable, DOUBLE_TAP_TIMEOUT);
            } else {
                long timeDiff = now - mLastThreeFingerTapTime;
                if (timeDiff > 0 && timeDiff < DOUBLE_TAP_TIMEOUT) {
                    if (mListener != null) {
                        mListener.onThreeFingerDoubleTap();
                    }
                    mIsFirstTap = true;
                    mainHandler.removeCallbacks(mTimeoutRunnable);
                }
            }
            return true;
        }
        return false;
    }
}
