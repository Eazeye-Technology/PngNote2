package io.github.pastthepixels.freepaint.Graphics;

import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;

//FIXME: You **MUST** keep boolean onTouchEvent(MotionEvent event) return true
//see https://cloud.tencent.com/developer/ask/sof/100130489
//android java代码则么判断三指双击
public class ThreeFingerDoubleTapDetector {

    // 双击的最大时间间隔（毫秒）
    private static final long DOUBLE_TAP_TIMEOUT = 300;

    // 记录上一次三指点击的时间
    private long mLastThreeFingerTapTime = 0;
    // 是否为第一次点击的标记
    private boolean mIsFirstTap = true;
    private Runnable mTimeoutRunnable;

    // 定义回调接口
    public interface OnThreeFingerDoubleTapListener {
        void onThreeFingerDoubleTap();
    }

    private OnThreeFingerDoubleTapListener mListener;

    public ThreeFingerDoubleTapDetector(OnThreeFingerDoubleTapListener listener) {
        this.mListener = listener;
        // 创建超时 Runnable，用于重置状态
        mTimeoutRunnable = () -> mIsFirstTap = true;
    }

    /**
     * 在 View 的 onTouchEvent 或 OnTouchListener 中调用此方法
     */
    public boolean onTouchEvent(MotionEvent event) {
        // 获取当前触控点的数量
        int pointerCount = event.getPointerCount();
        // 获取事件类型
        int action = event.getActionMasked();

        // 核心：当有手指抬起，并且当前触摸点数量为 3 时，视为一次“三指点击”
        if ((action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_UP)
                && pointerCount == 3) { //2 //3 //1
            // 获取当前点击发生的时间
            long now = System.currentTimeMillis();
            Handler mainHandler = new Handler(Looper.getMainLooper());

            if (mIsFirstTap) {
                // 第一次点击，记录时间，并准备等待第二次点击
                mLastThreeFingerTapTime = now;
                mIsFirstTap = false;
//                if (mListener != null) {
//                    mListener.onThreeFingerDoubleTap();
//                }

                // 设置一个超时任务，超时后重置标记
                mainHandler.removeCallbacks(mTimeoutRunnable);
                mainHandler.postDelayed(mTimeoutRunnable, DOUBLE_TAP_TIMEOUT);
            } else {
                // 不是第一次点击，检查时间差是否在有效范围内
                long timeDiff = now - mLastThreeFingerTapTime;
                if (timeDiff > 0 && timeDiff < DOUBLE_TAP_TIMEOUT) {
                    // 时间差有效，判定为三指双击，触发回调
                    if (mListener != null) {
                        mListener.onThreeFingerDoubleTap();
                    }
                    // 重置状态
                    mIsFirstTap = true;
                    mainHandler.removeCallbacks(mTimeoutRunnable);
                }
                // 如果时间超时，则什么也不做，状态由超时 Runnable 重置
            }
            return true;
        }
        return false;
    }
}

/*
public class MyActivity extends AppCompatActivity {

    private ThreeFingerDoubleTapDetector mDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化检测器
        mDetector = new ThreeFingerDoubleTapDetector(() -> {
            // 三指双击被触发，在这里执行你的业务逻辑
            Toast.makeText(MyActivity.this, "三指双击！", Toast.LENGTH_SHORT).show();
        });

        // 将检测器绑定到任意视图（如整个根布局）
        View rootView = findViewById(android.R.id.content);
        rootView.setOnTouchListener((v, event) -> mDetector.onTouchEvent(event));
    }
}
 */