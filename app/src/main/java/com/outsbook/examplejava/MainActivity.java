package com.outsbook.examplejava;

import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.outsbook.libs.canvaseditor.models.DrawObject;
import com.outsbook.libs.canvaseditor.views.PaintView;
import com.outsbook.libs.canvaseditor.models.BitmapSticker;
import com.outsbook.libs.canvaseditor.models.DrawableSticker;
import com.outsbook.libs.canvaseditor.views.StickerView;
import com.outsbook.libs.canvaseditor.models.TextSticker;
import com.txkj.drawingapp.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private List<DrawObject> mUndoList;
    private List<DrawObject> mRedoList;
    private PaintView.PaintViewListener paintViewListener;
    private PaintView mPaintView;
    private StickerView mStickerView;
    public interface CanvasEditorListener {
        void onEnableUndo(boolean var1);
        void onEnableRedo(boolean var1);
    }
    private CanvasEditorListener mListener;

    private ImageButton buttonSticker;
    private ImageButton buttonText;
    private ImageButton buttonStickerText;
    private ImageButton buttonBlack;
    private ImageButton buttonYellow;
    private ImageButton buttonPlus;
    private ImageButton buttonMinus;

    private ImageButton buttonSave;
    private ImageButton buttonUndo;
    private ImageButton buttonRedo;
    private ImageButton buttonDelete;

    private View viewImagePreview;
    private ImageButton buttonClose;
    private ImageView imageView;

    private Float strokeWidth = 20f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_canvaseditor_kotlin);

        Objects.requireNonNull(getSupportActionBar()).hide();

        initView();
        initValue();
        initClickListener();
        initCanvasEditorListener();
    }

    private void initView(){
        //canvasEditor = findViewById(R.id.canvasEditor);
        {
            this.mUndoList = new ArrayList<DrawObject>();
            this.mRedoList = new ArrayList<DrawObject>();
            this.paintViewListener = new PaintView.PaintViewListener() {
                @Override
                public void onTouchUp(@NonNull DrawObject obj) {
                    mUndoList.add(obj);
                    mRedoList.clear();
                    if (mListener != null) {
                        mListener.onEnableUndo(true);
                    }
                    if (mListener != null) {
                        mListener.onEnableRedo(false);
                    }
                }

                @Override
                public void onClick(float x, float y) {
                    int pos = findTapedSticker(x, y);
                    if (pos > -1) {
                        enableEditModeSticker(pos);
                    }
                }

                @Override
                public void onTouchEvent(@NonNull MotionEvent event) {

                }
            };
            {
                this.mPaintView = findViewById(R.id.paintView);
                this.mPaintView.init(paintViewListener);
                this.mStickerView = findViewById(R.id.stickerView);
                this.mStickerView.init(new StickerView.StickerViewListener() {
                    @Override
                    public void onRemove() {
                        if (mListener != null) {
                            mListener.onEnableUndo(!mUndoList.isEmpty());
                        }
                    }

                    @Override
                    public void onDone(@NonNull DrawObject obj) {
                        addStickerToPaint(obj);
                    }

                    @Override
                    public void onZoomAndRotate() {

                    }

                    @Override
                    public void onFlip() {

                    }

                    @Override
                    public void onClickStickerOutside(float x, float y) {
                        int pos = findTapedSticker(x, y);
                        if (pos > -1) {
                            enableEditModeSticker(pos);
                        }
                    }

                    @Override
                    public void onTouchEvent(@NonNull MotionEvent event) {

                    }
                });
            }
        }

        buttonSticker = findViewById(R.id.buttonSticker);
        buttonText = findViewById(R.id.buttonText);
        buttonStickerText = findViewById(R.id.buttonStickerText);
        buttonBlack = findViewById(R.id.buttonBlack);
        buttonYellow = findViewById(R.id.buttonYellow);
        buttonPlus = findViewById(R.id.buttonPlus);
        buttonMinus = findViewById(R.id.buttonMinus);

        buttonSave = findViewById(R.id.buttonSave);
        buttonUndo = findViewById(R.id.buttonUndo);
        buttonRedo = findViewById(R.id.buttonRedo);
        buttonDelete = findViewById(R.id.buttonDelete);

        viewImagePreview = findViewById(R.id.viewImagePreview);
        buttonClose = findViewById(R.id.buttonClose);
        imageView = findViewById(R.id.imageView);
    }

    private void initValue(){
        buttonUndo.setImageAlpha(50);
        buttonRedo.setImageAlpha(50);
        //set stroke width
        setStrokeWidth(strokeWidth);
        //set paint color
        setPaintColor(ContextCompat.getColor(this, R.color.colorBlack));
    }

    private void initClickListener(){
        buttonSticker.setOnClickListener(v -> {
            Drawable drawable = ContextCompat.getDrawable(this, R.drawable.app_icon);
            if (drawable != null)
                addDrawableSticker(drawable);
        });
        buttonText.setOnClickListener(v -> {
            String text = "Canvas";
            int color = 0xFFDB2659; //ContextCompat.getColor(this, R.color.colorPrimary);
            addTextSticker(text, color, null);
        });
        buttonStickerText.setOnClickListener(v -> {
            Drawable drawable = ContextCompat.getDrawable(this, R.drawable.ic_panorama_240dp);
            String text = "Canvas";
            int textColor = 0xFFFEC63B; //ContextCompat.getColor(this, R.color.colorAccent);
            if (drawable != null)
                addDrawableTextSticker(drawable, text, textColor, null);
        });
        buttonBlack.setOnClickListener(v -> {
            buttonPlus.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_plus_black_24dp));
            buttonMinus.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_minus_black_24dp));
            int color = ContextCompat.getColor(this, R.color.colorBlack);
            setPaintColor(color);
        });
        buttonYellow.setOnClickListener(v -> {
            buttonPlus.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_plus_yellow_24dp));
            buttonMinus.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_minus_yellow_24dp));
            int color = ContextCompat.getColor(this, R.color.colorYellow);
            setPaintColor(color);
        });
        buttonPlus.setOnClickListener(v -> {
            strokeWidth += 10f;
            setStrokeWidth(strokeWidth);
        });
        buttonMinus.setOnClickListener(v -> {
            strokeWidth -= 10f;
            setStrokeWidth(strokeWidth);
        });

        buttonSave.setOnClickListener(v -> {
            Bitmap bitmap = downloadBitmap();
            imageView.setImageBitmap(bitmap);
            viewImagePreview.setVisibility(View.VISIBLE);
        });
        buttonUndo.setOnClickListener(v -> {
            undo();
        });
        buttonRedo.setOnClickListener(v -> {
            redo();
        });
        buttonDelete.setOnClickListener(v -> {
            removeAll();
        });

        buttonClose.setOnClickListener(v -> {
            viewImagePreview.setVisibility(View.GONE);
        });
    }

    private void initCanvasEditorListener(){
        setListener(new CanvasEditorListener() {
            @Override
            public void onEnableUndo(boolean isEnable) {
                buttonUndo.setImageAlpha(isEnable? 255 : 50);
            }

            @Override
            public void onEnableRedo(boolean isEnable) {
                buttonRedo.setImageAlpha(isEnable? 255 : 50);
            }
        });
    }

    public final void setListener(CanvasEditorListener listener) {
        this.mListener = listener;
    }

    public final void setPaintColor(int color) {
        this.doneStickerEdit();
        this.mPaintView.getPaint().setColor(color);
    }

    public final void setStrokeWidth(float strokeWidth) {
        this.doneStickerEdit();
        this.mPaintView.getPaint().setStrokeWidth(strokeWidth);
    }

    public final void setStrokeCap(Paint.Cap strokeCap) {
        this.doneStickerEdit();
        this.mPaintView.getPaint().setStrokeCap(strokeCap);
    }

    public final void addDrawableSticker(Drawable drawable) {
        this.doneStickerEdit();
        this.mStickerView.setVisibility(View.VISIBLE);
        this.mStickerView.addSticker(new DrawableSticker(drawable));
        if (this.mListener != null) {
            this.mListener.onEnableUndo(true);
        }
        if (this.mListener != null) {
            this.mListener.onEnableRedo(false);
        }
    }

    public void addBitmapSticker(Bitmap bitmap) {
        this.doneStickerEdit();
        this.mStickerView.setVisibility(View.VISIBLE);
        BitmapSticker sticker = new BitmapSticker(this, bitmap);
        this.mStickerView.addSticker(sticker);
        if (this.mListener != null) {
            this.mListener.onEnableUndo(true);
        }
        if (this.mListener != null) {
            this.mListener.onEnableRedo(false);
        }
    }

    public void addTextSticker(String text, int textColor, Typeface typeface) {
        this.doneStickerEdit();
        this.mStickerView.setVisibility(View.VISIBLE);
        TextSticker sticker = new TextSticker(this, null);
        sticker.setText(text);
        sticker.setTextColor(textColor);
        if (typeface != null) {
            sticker.setTypeface(typeface);
        }
        sticker.setAlpha(255);
        sticker.resizeText();
        this.mStickerView.addSticker(sticker);
        if (this.mListener != null) {
            this.mListener.onEnableUndo(true);
        }
        if (this.mListener != null) {
            this.mListener.onEnableRedo(false);
        }
    }

    public void addDrawableTextSticker(Drawable drawable, String text, int textColor, Typeface typeface) {
        this.doneStickerEdit();
        this.mStickerView.setVisibility(View.VISIBLE);
        TextSticker sticker = new TextSticker(this, drawable);
        sticker.setText(text);
        sticker.setTextColor(textColor);
        if (typeface != null) {
            sticker.setTypeface(typeface);
        }
        sticker.resizeText();
        this.mStickerView.addSticker(sticker);
        if (this.mListener != null) {
            this.mListener.onEnableUndo(true);
        }
        if (this.mListener != null) {
            this.mListener.onEnableRedo(false);
        }
    }
    public void doneActiveSticker() {
        if (this.mStickerView.getVisibility() == 0) {
            this.mStickerView.done();
        }
    }

    public final void removeActiveSticker() {
        if (this.mStickerView.getVisibility() == 0) {
            this.mStickerView.remove();
        }

    }

    public final void zoomAndRotateActiveSticker(MotionEvent motionEvent) {
        if (this.mStickerView.getVisibility() == 0) {
            this.mStickerView.zoomAndRotate(motionEvent);
        }
    }

    public final void flipActiveSticker() {
        if (this.mStickerView.getVisibility() == 0) {
            this.mStickerView.flip();
        }
    }

    public final void undo() {
        if (this.mStickerView.getVisibility() == 0) {
            this.mStickerView.remove();
        } else {
            if (!this.mUndoList.isEmpty()) {
                this.mRedoList.add(this.mUndoList.get(this.mUndoList.size() - 1));
                this.mUndoList.remove(this.mUndoList.size() - 1);
                this.mPaintView.initCanvas();
                for (DrawObject it : this.mUndoList) {
                    this.drawObject(it);
                }
                if (this.mListener != null) {
                    this.mListener.onEnableUndo(!this.mUndoList.isEmpty());
                }
                if (this.mListener != null) {
                    this.mListener.onEnableRedo(!this.mRedoList.isEmpty());
                }
            }
        }
    }

    public final void redo() {
        if (!this.mRedoList.isEmpty()) {
            DrawObject obj = this.mRedoList.get(this.mRedoList.size() - 1);
            this.mUndoList.add(obj);
            this.mRedoList.remove(this.mRedoList.size() - 1);
            this.drawObject(obj);
            if (this.mListener != null) {
                this.mListener.onEnableUndo(!this.mUndoList.isEmpty());
            }
            if (this.mListener != null) {
                this.mListener.onEnableRedo(!this.mRedoList.isEmpty());
            }
        }
    }

    public final void removeAll() {
        this.mUndoList.clear();
        this.mRedoList.clear();
        this.mStickerView.remove();
        this.mPaintView.initCanvas();
        if (this.mListener != null) {
            this.mListener.onEnableUndo(false);
        }
        if (this.mListener != null) {
            this.mListener.onEnableRedo(false);
        }
    }

    public Bitmap downloadBitmap() {
        this.doneStickerEdit();
        return this.mPaintView.getExtraBitmap();
    }

    private void drawObject(DrawObject obj) {
        if (obj.getDrawType() == DrawObject.DrawType.PATH) {
            this.mPaintView.drawPath(obj.getPathAndPaint());
        } else if (obj.getDrawType() == DrawObject.DrawType.STICKER) {
            this.mPaintView.drawSticker(obj.getSticker());
        }
    }

    private int findTapedSticker(float x, float y) {
        for (int i = this.mUndoList.size() - 1; i >= 0; --i) {
            DrawObject obj = this.mUndoList.get(i);
            if (obj.getDrawType() == DrawObject.DrawType.STICKER) {
                if (obj.getSticker().contains(x, y)) {
                    return i;
                }
            }
        }
        return -1;
    }

    private void enableEditModeSticker(int pos) {
        DrawObject obj = this.mUndoList.get(pos);
        this.mStickerView.setVisibility(View.VISIBLE);
        this.mStickerView.setCurrentSticker(obj.getSticker());
        this.mUndoList.remove(pos);
        this.mPaintView.initCanvas();

        for (DrawObject element : this.mUndoList) {
            this.drawObject(element);
        }

        this.mRedoList.clear();
        if (this.mListener != null) {
            this.mListener.onEnableUndo(true);
        }
        if (this.mListener != null) {
            this.mListener.onEnableRedo(!this.mRedoList.isEmpty());
        }
    }

    private void addStickerToPaint(DrawObject obj) {
        this.mPaintView.drawSticker(Objects.requireNonNull(obj.getSticker()));
        this.mUndoList.add(obj);
        this.mRedoList.clear();
        if (this.mListener != null) {
            this.mListener.onEnableUndo(true);
        }
        if (this.mListener != null) {
            this.mListener.onEnableRedo(false);
        }
    }

    private void doneStickerEdit() {
        if (this.mStickerView.getVisibility() == View.VISIBLE) {
            this.mStickerView.done();
        }
    }
}
