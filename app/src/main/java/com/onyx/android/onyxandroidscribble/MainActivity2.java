package com.onyx.android.onyxandroidscribble;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;

import androidx.appcompat.app.AppCompatActivity;

import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.data.note.TouchPoint;
import com.onyx.android.sdk.pen.NeoFountainPen;
import com.onyx.android.sdk.pen.RawInputCallback;
import com.onyx.android.sdk.pen.TouchHelper;
import com.onyx.android.sdk.pen.data.TouchPointList;
import com.onyx.android.sdk.rx.RxManager;
import com.onyx.android.sdk.utils.NumberUtils;
import com.onyx.android.sdk.utils.ResManager;
import com.txkj.drawingapp.R;

import java.util.ArrayList;
import java.util.List;

//show black if failed
//show white if success
public class MainActivity2 extends AppCompatActivity {
    private final static boolean USE_TOUCH_AS_STYLUS = true;

    private static final String TAG = MainActivity2.class.getSimpleName();
    /**
     * skip point count
     */
    private static final int INTERVAL = 10;

//    private ActivityPenStylusTouchHelperDemoBinding binding;
    private SurfaceView binding_surfaceview;
    private RadioButton binding_rbBrush, binding_rbPencil;
    private Button binding_buttonEraser, binding_buttonPen;
    private CheckBox binding_cbRender;

    private RxManager rxManager; //FIXME:???

    private Paint paint = new Paint();
    private TouchPoint startPoint;
    private int countRec = 0;

    private Bitmap bitmap;
    private Canvas canvas;

    private final float STROKE_WIDTH = 3.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        {
            //FIXME:added
            Context applicationContext = this.getApplicationContext();
            ResManager.init(applicationContext);
        }

        //binding = DataBindingUtil.setContentView(this, R.layout.activity_pen_stylus_touch_helper_demo);
        //binding.setActivityPenStylusTouchHelper(this);
        binding_surfaceview = (SurfaceView) this.findViewById(R.id.surfaceview);
        //================
        //FIXME: added
        //https://stackoverflow.com/questions/21311573/surfaceview-shows-black-screen-android
        binding_surfaceview.setBackgroundColor(0x00000000);
        //https://stackoverflow.com/questions/3818284/android-surfaceholder-unlockcanvasandpost-does-not-cause-redraw
        binding_surfaceview.setZOrderOnTop(false);
        //================
        binding_rbBrush = (RadioButton) this.findViewById(R.id.rb_brush);
        binding_rbBrush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRadioButtonClicked(view);
            }
        });
        binding_rbPencil = (RadioButton) this.findViewById(R.id.rb_pencil);
        binding_rbPencil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRadioButtonClicked(view);
            }
        });
        binding_buttonEraser = (Button) this.findViewById(R.id.button_eraser);
        binding_buttonEraser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onEraserClick();
            }
        });
        binding_buttonPen = (Button) this.findViewById(R.id.button_pen);
        binding_buttonPen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPenClick();
            }
        });
        binding_cbRender = (CheckBox) this.findViewById(R.id.cb_render);
        binding_cbRender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRenderEnableClick();
            }
        });

        initPaint();
        initSurfaceView();
        initReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (bitmap != null) {
            bitmap.recycle();
            bitmap = null;
        }
        super.onDestroy();
    }

    public RxManager getRxManager() {
        if (rxManager == null) {
            rxManager = RxManager.Builder.sharedSingleThreadManager();
        }
        return rxManager;
    }

    public void renderToScreen(SurfaceView surfaceView, Bitmap bitmap) {
        getRxManager().enqueue(new RendererToScreenRequest(surfaceView, bitmap), null);
    }

    private void initPaint() {
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(STROKE_WIDTH);
    }

    TouchPointList g_touchPointList = new TouchPointList();
    @SuppressLint("ClickableViewAccessibility")
    private void initSurfaceView() {
        binding_surfaceview.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int
                    oldRight, int oldBottom) {
                if (cleanSurfaceView()) {
                    binding_surfaceview.removeOnLayoutChangeListener(this);
                }
                List<Rect> exclude = new ArrayList<>();
                exclude.add(getRelativeRect(binding_surfaceview, binding_buttonEraser));
                exclude.add(getRelativeRect(binding_surfaceview, binding_buttonPen));
                exclude.add(getRelativeRect(binding_surfaceview, binding_cbRender));
                exclude.add(getRelativeRect(binding_surfaceview, binding_rbBrush));
                exclude.add(getRelativeRect(binding_surfaceview, binding_rbPencil));

                Rect limit = new Rect();
                binding_surfaceview.getLocalVisibleRect(limit);
                binding_rbBrush.setChecked(true);
                binding_surfaceview.addOnLayoutChangeListener(this);
            }
        });

        binding_surfaceview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d(TAG, "surfaceView.setOnTouchListener - onTouch::action - " + event.getAction());

                TouchPoint touchPoint = new TouchPoint();
                touchPoint.x = event.getX();
                touchPoint.y = event.getY();
                //touchPoint.pressure = event.getPressure();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_MOVE:
                        countRec++;
                        countRec = countRec % INTERVAL;
                        Log.d(TAG, "countRec = " + countRec);
                        //FIXME:added, no need, see below onRawDrawingTouchPointListReceived()
                        if (true) {
                            if (countRec == INTERVAL - 1) {
                                g_touchPointList.add(touchPoint);
                                drawScribbleToBitmap(g_touchPointList.getPoints());
                                renderToScreen(binding_surfaceview, bitmap);
                            }
                        }
                        return true;

                    case MotionEvent.ACTION_UP:
                        countRec++;
                        countRec = countRec % INTERVAL;
                        Log.d(TAG, "countRec = " + countRec);
                        g_touchPointList.add(touchPoint);
                        drawScribbleToBitmap(g_touchPointList.getPoints());
                        renderToScreen(binding_surfaceview, bitmap);
                        g_touchPointList.clear();
                        return true;
                }
                return false;
            }
        });

        final SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                cleanSurfaceView();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                holder.removeCallback(this);
            }
        };
        binding_surfaceview.getHolder().addCallback(surfaceCallback);
    }

    private void initReceiver() {

    }

    public void onPenClick() {
        onRenderEnableClick();
    }

    public void onEraserClick() {
        if (bitmap != null) {
            bitmap.recycle();
            bitmap = null;
        }
        cleanSurfaceView();
    }

    public void onRenderEnableClick() {
        if (bitmap != null) {
            bitmap.recycle();
            bitmap = null;
        }
        Log.d(TAG, "onRenderEnableClick setRawDrawingRenderEnabled =  " + binding_cbRender.isChecked());
    }

    public void onRadioButtonClicked(View radioButton) {

        boolean checked = ((RadioButton) radioButton).isChecked();
        Log.d(TAG, radioButton.toString());
        if (radioButton.getId() == R.id.rb_brush) {
            if (checked) {
                Log.d(TAG, "STROKE_STYLE_BRUSH");
            }
        } if (radioButton.getId() == R.id.rb_pencil) {
            if (checked) {
                Log.d(TAG, "STROKE_STYLE_PENCIL");
            }
        }
        // refresh ui
        onEraserClick();
        onPenClick();
    }

    public Rect getRelativeRect(final View parentView, final View childView) {
        int[] parent = new int[2];
        int[] child = new int[2];
        parentView.getLocationOnScreen(parent);
        childView.getLocationOnScreen(child);
        Rect rect = new Rect();
        childView.getLocalVisibleRect(rect);
        rect.offset(child[0] - parent[0], child[1] - parent[1]);
        return rect;
    }

    private boolean cleanSurfaceView() {
        if (binding_surfaceview.getHolder() == null) {
            return false;
        }
        if (binding_surfaceview.getHolder().getSurface() != null &&
                !binding_surfaceview.getHolder().getSurface().isValid()) {
            return false;
        }
        Canvas canvas = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            canvas = binding_surfaceview.getHolder().lockHardwareCanvas();
        } else {
            canvas = binding_surfaceview.getHolder().lockCanvas();
        }
        if (canvas == null) {
            return false;
        }
        canvas.drawColor(Color.WHITE);
        binding_surfaceview.getHolder().unlockCanvasAndPost(canvas);
        return true;
    }

    private void drawRect(TouchPoint endPoint) {
        Canvas canvas = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            canvas = binding_surfaceview.getHolder().lockHardwareCanvas();
        } else {
            canvas = binding_surfaceview.getHolder().lockCanvas();
        }
        if (canvas == null) {
            return;
        }

        if (startPoint == null || endPoint == null) {
            binding_surfaceview.getHolder().unlockCanvasAndPost(canvas);
            return;
        }

        canvas.drawColor(Color.WHITE);
        canvas.drawRect(startPoint.getX(), startPoint.getY(), endPoint.getX(), endPoint.getY(), paint);
        Log.d(TAG, "drawRect ");
        binding_surfaceview.getHolder().unlockCanvasAndPost(canvas);
    }

    private RawInputCallback callback = new RawInputCallback() {

        @Override
        public void onBeginRawDrawing(boolean b, TouchPoint touchPoint) {
            Log.d(TAG, "onBeginRawDrawing");
            startPoint = touchPoint;
            Log.d(TAG, touchPoint.getX() + ", " + touchPoint.getY());
            countRec = 0;
            TouchUtils.disableFingerTouch(getApplicationContext());
        }

        @Override
        public void onEndRawDrawing(boolean b, TouchPoint touchPoint) {
            Log.d(TAG, "onEndRawDrawing###");
            if (!binding_cbRender.isChecked()) {
                drawRect(touchPoint);
            }
            Log.d(TAG, touchPoint.getX() + ", " + touchPoint.getY());
            TouchUtils.enableFingerTouch(getApplicationContext());
        }

        @Override
        public void onRawDrawingTouchPointMoveReceived(TouchPoint touchPoint) {
            Log.d(TAG, "onRawDrawingTouchPointMoveReceived");
            Log.d(TAG, touchPoint.getX() + ", " + touchPoint.getY());
            countRec++;
            countRec = countRec % INTERVAL;
            Log.d(TAG, "countRec = " + countRec);
            //FIXME:added, no need, see below onRawDrawingTouchPointListReceived()
            if (false) {
                TouchPointList touchPointList = new TouchPointList();
                touchPointList.add(touchPoint);
                drawScribbleToBitmap(touchPointList.getPoints());

                renderToScreen(binding_surfaceview, bitmap);
            }
        }

        @Override
        public void onRawDrawingTouchPointListReceived(TouchPointList touchPointList) {
            Log.d(TAG, "onRawDrawingTouchPointListReceived");
            drawScribbleToBitmap(touchPointList.getPoints());
            //FIXME:added
            if (true) {
                renderToScreen(binding_surfaceview, bitmap);
                //drawBitmapToSurface();
            }
        }

        @Override
        public void onBeginRawErasing(boolean b, TouchPoint touchPoint) {
            Log.d(TAG, "onBeginRawErasing");
        }

        @Override
        public void onEndRawErasing(boolean b, TouchPoint touchPoint) {
            Log.d(TAG, "onEndRawErasing");
        }

        @Override
        public void onRawErasingTouchPointMoveReceived(TouchPoint touchPoint) {
            Log.d(TAG, "onRawErasingTouchPointMoveReceived");
        }

        @Override
        public void onRawErasingTouchPointListReceived(TouchPointList touchPointList) {
            Log.d(TAG, "onRawErasingTouchPointListReceived");
        }
    };

    private void drawScribbleToBitmap(List<TouchPoint> list) {
        if (!binding_cbRender.isChecked()) {
            return;
        }
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(binding_surfaceview.getWidth(),
                    binding_surfaceview.getHeight(), Bitmap.Config.ARGB_8888);
            canvas = new Canvas(bitmap);
        }

        if (binding_rbBrush.isChecked()) {
            float maxPressure = EpdController.getMaxTouchPressure();
            NeoFountainPen.drawStroke(canvas, paint, list, NumberUtils.FLOAT_ONE, STROKE_WIDTH, maxPressure, false);
        }

        if (binding_rbPencil.isChecked()) {
            Path path = new Path();
            PointF prePoint = new PointF(list.get(0).x, list.get(0).y);
            path.moveTo(prePoint.x, prePoint.y);
            for (TouchPoint point : list) {
                path.quadTo(prePoint.x, prePoint.y, point.x, point.y);
                prePoint.x = point.x;
                prePoint.y = point.y;
            }
            canvas.drawPath(path, paint);
        }
    }

    private void drawBitmapToSurface() {
        if (!binding_cbRender.isChecked()) {
            return;
        }
        if (bitmap == null) {
            return;
        }
        Canvas lockCanvas = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            lockCanvas = binding_surfaceview.getHolder().lockHardwareCanvas();
        } else {
            lockCanvas = binding_surfaceview.getHolder().lockCanvas();
        }
        if (lockCanvas == null) {
            return;
        }
        lockCanvas.drawColor(Color.WHITE);
        lockCanvas.drawBitmap(bitmap, 0f, 0f, paint);
        binding_surfaceview.getHolder().unlockCanvasAndPost(lockCanvas);
        // refresh ui
        if (!binding_cbRender.isChecked()) {

        }
    }
}