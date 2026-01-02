package com.outsbook.examplekotlin;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.outsbook.libs.canvaseditor.CanvasEditorView;
import com.outsbook.libs.canvaseditor.listeners.CanvasEditorListener;
import com.txkj.drawingapp.R;

import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class MainActivity extends AppCompatActivity {
    private float strokeWidth = 20.0F;
    private ImageButton buttonUndo;
    private ImageButton buttonRedo;
    private CanvasEditorView canvasEditor;
    private ImageButton buttonClose;
    private ImageButton buttonDelete;
    private ImageButton buttonSave;
    private ImageButton buttonMinus;
    private ImageButton buttonPlus;
    private ImageButton buttonYellow;
    private ImageButton buttonBlack;
    private ImageButton buttonStickerText;
    private ImageButton buttonText;
    private ImageButton buttonSticker;
    private ImageView imageView;
    private RelativeLayout viewImagePreview;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main_canvaseditor_kotlin);

        Objects.requireNonNull(getSupportActionBar()).hide();

        View var10001 = this.findViewById(R.id.buttonUndo);
        Intrinsics.checkNotNullExpressionValue(var10001, "findViewById(...)");
        this.buttonUndo = (ImageButton)var10001;
        var10001 = this.findViewById(R.id.buttonRedo);
        Intrinsics.checkNotNullExpressionValue(var10001, "findViewById(...)");
        this.buttonRedo = (ImageButton)var10001;
        var10001 = this.findViewById(R.id.canvasEditor);
        Intrinsics.checkNotNullExpressionValue(var10001, "findViewById(...)");
        this.canvasEditor = (CanvasEditorView)var10001;
        var10001 = this.findViewById(R.id.buttonClose);
        Intrinsics.checkNotNullExpressionValue(var10001, "findViewById(...)");
        this.buttonClose = (ImageButton)var10001;
        var10001 = this.findViewById(R.id.buttonDelete);
        Intrinsics.checkNotNullExpressionValue(var10001, "findViewById(...)");
        this.buttonDelete = (ImageButton)var10001;
        var10001 = this.findViewById(R.id.buttonSave);
        Intrinsics.checkNotNullExpressionValue(var10001, "findViewById(...)");
        this.buttonSave = (ImageButton)var10001;
        var10001 = this.findViewById(R.id.buttonMinus);
        Intrinsics.checkNotNullExpressionValue(var10001, "findViewById(...)");
        this.buttonMinus = (ImageButton)var10001;
        var10001 = this.findViewById(R.id.buttonPlus);
        Intrinsics.checkNotNullExpressionValue(var10001, "findViewById(...)");
        this.buttonPlus = (ImageButton)var10001;
        var10001 = this.findViewById(R.id.buttonYellow);
        Intrinsics.checkNotNullExpressionValue(var10001, "findViewById(...)");
        this.buttonYellow = (ImageButton)var10001;
        var10001 = this.findViewById(R.id.buttonBlack);
        Intrinsics.checkNotNullExpressionValue(var10001, "findViewById(...)");
        this.buttonBlack = (ImageButton)var10001;
        var10001 = this.findViewById(R.id.buttonStickerText);
        Intrinsics.checkNotNullExpressionValue(var10001, "findViewById(...)");
        this.buttonStickerText = (ImageButton)var10001;
        var10001 = this.findViewById(R.id.buttonText);
        Intrinsics.checkNotNullExpressionValue(var10001, "findViewById(...)");
        this.buttonText = (ImageButton)var10001;
        var10001 = this.findViewById(R.id.buttonSticker);
        Intrinsics.checkNotNullExpressionValue(var10001, "findViewById(...)");
        this.buttonSticker = (ImageButton)var10001;
        var10001 = this.findViewById(R.id.imageView);
        Intrinsics.checkNotNullExpressionValue(var10001, "findViewById(...)");
        this.imageView = (ImageView)var10001;
        var10001 = this.findViewById(R.id.viewImagePreview);
        Intrinsics.checkNotNullExpressionValue(var10001, "findViewById(...)");
        this.viewImagePreview = (RelativeLayout)var10001;
        this.initValue();
        this.initClickListener();
        this.initCanvasEditorListener();
    }

    private final void initValue() {
        ImageButton var10000 = this.buttonUndo;
        if (var10000 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("buttonUndo");
            var10000 = null;
        }

        var10000.setImageAlpha(50);
        var10000 = this.buttonRedo;
        if (var10000 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("buttonRedo");
            var10000 = null;
        }

        var10000.setImageAlpha(50);
        CanvasEditorView var2 = this.canvasEditor;
        if (var2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("canvasEditor");
            var2 = null;
        }

        var2.setStrokeWidth(this.strokeWidth);
        var2 = this.canvasEditor;
        if (var2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("canvasEditor");
            var2 = null;
        }

        var2.setPaintColor(ContextCompat.getColor((Context)this, R.color.colorBlack));
    }

    private final void initClickListener() {
        ImageButton var10000 = this.buttonSticker;
        if (var10000 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("buttonSticker");
            var10000 = null;
        }

        var10000.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Drawable drawable = ContextCompat.getDrawable(MainActivity.this, R.drawable.app_icon);
                if (drawable != null) {
                    int var4 = 0;
                    CanvasEditorView var10000 = canvasEditor;
                    if (var10000 == null) {
                        Intrinsics.throwUninitializedPropertyAccessException("canvasEditor");
                        var10000 = null;
                    }

                    var10000.addDrawableSticker(drawable);
                }
            }
        });
        var10000 = this.buttonText;
        if (var10000 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("buttonText");
            var10000 = null;
        }

        var10000.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = "Canvas";
                int textColor = 0xFFDB2659; //ContextCompat.getColor((Context)MainActivity.this, R.color.colorPrimary);
                CanvasEditorView var10000 = canvasEditor;
                if (var10000 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("canvasEditor");
                    var10000 = null;
                }

                var10000.addTextSticker(text, textColor, (Typeface)null);
            }
        });
        var10000 = this.buttonStickerText;
        if (var10000 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("buttonStickerText");
            var10000 = null;
        }

        var10000.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Drawable drawable = ContextCompat.getDrawable((Context)MainActivity.this, R.drawable.ic_panorama_240dp);
                String text = "Canvas";
                int textColor = 0xFFFEC63B; //ContextCompat.getColor((Context)MainActivity.this, R.color.colorAccent);
                if (drawable != null) {
                    int var6 = 0;
                    CanvasEditorView var10000 = canvasEditor;
                    if (var10000 == null) {
                        Intrinsics.throwUninitializedPropertyAccessException("canvasEditor");
                        var10000 = null;
                    }

                    var10000.addDrawableTextSticker(drawable, text, textColor, (Typeface)null);
                }
            }
        });
        var10000 = this.buttonBlack;
        if (var10000 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("buttonBlack");
            var10000 = null;
        }

        var10000.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageButton var10000 = buttonPlus;
                if (var10000 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("buttonPlus");
                    var10000 = null;
                }

                var10000.setImageDrawable(ContextCompat.getDrawable((Context)MainActivity.this, R.drawable.ic_plus_black_24dp));
                var10000 = buttonMinus;
                if (var10000 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("buttonMinus");
                    var10000 = null;
                }

                var10000.setImageDrawable(ContextCompat.getDrawable((Context)MainActivity.this, R.drawable.ic_minus_black_24dp));
                int color = ContextCompat.getColor((Context)MainActivity.this, R.color.colorBlack);
                CanvasEditorView var4 = canvasEditor;
                if (var4 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("canvasEditor");
                    var4 = null;
                }

                var4.setPaintColor(color);
            }
        });
        var10000 = this.buttonYellow;
        if (var10000 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("buttonYellow");
            var10000 = null;
        }

        var10000.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageButton var10000 = buttonPlus;
                if (var10000 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("buttonPlus");
                    var10000 = null;
                }

                var10000.setImageDrawable(ContextCompat.getDrawable((Context)MainActivity.this, R.drawable.ic_plus_yellow_24dp));
                var10000 = buttonMinus;
                if (var10000 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("buttonMinus");
                    var10000 = null;
                }

                var10000.setImageDrawable(ContextCompat.getDrawable((Context)MainActivity.this, R.drawable.ic_minus_yellow_24dp));
                int color = ContextCompat.getColor((Context)MainActivity.this, R.color.colorYellow);
                CanvasEditorView var4 = canvasEditor;
                if (var4 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("canvasEditor");
                    var4 = null;
                }

                var4.setPaintColor(color);
            }
        });
        var10000 = this.buttonPlus;
        if (var10000 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("buttonPlus");
            var10000 = null;
        }

        var10000.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                strokeWidth += 10.0F;
                CanvasEditorView var10000 = canvasEditor;
                if (var10000 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("canvasEditor");
                    var10000 = null;
                }

                var10000.setStrokeWidth(strokeWidth);
            }
        });
        var10000 = this.buttonMinus;
        if (var10000 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("buttonMinus");
            var10000 = null;
        }

        var10000.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                strokeWidth -= 10.0F;
                CanvasEditorView var10000 = canvasEditor;
                if (var10000 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("canvasEditor");
                    var10000 = null;
                }

                var10000.setStrokeWidth(strokeWidth);
            }
        });
        var10000 = this.buttonSave;
        if (var10000 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("buttonSave");
            var10000 = null;
        }

        var10000.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CanvasEditorView var10000 = canvasEditor;
                if (var10000 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("canvasEditor");
                    var10000 = null;
                }

                Bitmap bitmap = var10000.downloadBitmap();
                ImageView var3 = imageView;
                if (var3 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("imageView");
                    var3 = null;
                }

                var3.setImageBitmap(bitmap);
                RelativeLayout var4 = viewImagePreview;
                if (var4 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("viewImagePreview");
                    var4 = null;
                }

                var4.setVisibility(0);
            }
        });
        var10000 = this.buttonUndo;
        if (var10000 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("buttonUndo");
            var10000 = null;
        }

        var10000.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CanvasEditorView var10000 = canvasEditor;
                if (var10000 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("canvasEditor");
                    var10000 = null;
                }

                var10000.undo();
            }
        });
        var10000 = this.buttonDelete;
        if (var10000 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("buttonDelete");
            var10000 = null;
        }

        var10000.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CanvasEditorView var10000 = canvasEditor;
                if (var10000 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("canvasEditor");
                    var10000 = null;
                }

                var10000.removeAll();
            }
        });
        var10000 = this.buttonRedo;
        if (var10000 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("buttonRedo");
            var10000 = null;
        }

        var10000.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CanvasEditorView var10000 = canvasEditor;
                if (var10000 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("canvasEditor");
                    var10000 = null;
                }

                var10000.redo();
            }
        });
        var10000 = this.buttonClose;
        if (var10000 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("buttonClose");
            var10000 = null;
        }

        var10000.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RelativeLayout var10000 = viewImagePreview;
                if (var10000 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("viewImagePreview");
                    var10000 = null;
                }

                var10000.setVisibility(8);
            }
        });
    }

    private final void initCanvasEditorListener() {
        CanvasEditorView var10000 = this.canvasEditor;
        if (var10000 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("canvasEditor");
            var10000 = null;
        }

        var10000.setListener(new CanvasEditorListener() {
            public void onEnableUndo(boolean isEnable) {
                ImageButton var10000 = MainActivity.this.buttonUndo;
                if (var10000 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("buttonUndo");
                    var10000 = null;
                }

                var10000.setImageAlpha(isEnable ? 255 : 50);
            }

            public void onEnableRedo(boolean isEnable) {
                ImageButton var10000 = MainActivity.this.buttonRedo;
                if (var10000 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("buttonRedo");
                    var10000 = null;
                }

                var10000.setImageAlpha(isEnable ? 255 : 50);
            }

            public void onTouchEvent(MotionEvent event) {
                Intrinsics.checkNotNullParameter(event, "event");
            }

            public void onStickerActive() {
            }

            public void onStickerRemove() {
            }

            public void onStickerDone() {
            }

            public void onStickerZoomAndRotate() {
            }

            public void onStickerFlip() {
            }
        });
    }
}
