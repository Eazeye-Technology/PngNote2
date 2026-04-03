package com.txkj.drawingapp.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.slider.Slider;
import com.txkj.drawingapp.R;

//popupwindow_pen_style.xml
public class BookActivity4BrushEditDialog {
    //private final static int WIN_WIDTH = 544 + 24 * 2;

    public BookActivity4BrushEditDialog(Activity ctx, int brushId) {
        onCreateAct(ctx, brushId);
    }

    public AlertDialog create() {
        //
        AlertDialog dialog = new MaterialAlertDialogBuilder(mContext, BookActivity4Utils.getCenteredTitleThemeOverlay())
                //.setTitle(title)
                .setView(R.layout.activity_book4_brush2_edit)
                .setCancelable(true)
//                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                      onSave(dialogInterface);
//                    }
//                })
//                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//
//                    }
//                })
                .create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                onShowDialog(dialog);
            }
        });
        try {
            Window window = dialog.getWindow();
            if (window != null) {
                int WIN_WIDTH = mContext.getResources().getDimensionPixelSize(R.dimen.activity_dialog_upgrade_min_width_brush_edit);
                window.setLayout(WIN_WIDTH, WindowManager.LayoutParams.WRAP_CONTENT);
            }
        } catch (Throwable eee) {
            eee.printStackTrace();
        }
        return dialog;
    }

    private void onSave(AlertDialog dialogInterface) {
        AlertDialog dialog = (AlertDialog) dialogInterface;
        Slider slider = dialog.findViewById(R.id.slider);
        outputBrushSize = (int)slider.getValue();
        outputIsSave = true;
        BookActivity4Utils.onLongClickSubmenu1_after(mContext,
        BookActivity4BrushEditDialog.this, mBrushId);
    }

    public void onShowDialog(AlertDialog dialogInterface) {
        AlertDialog dialog = (AlertDialog) dialogInterface;
        Button btnSave = (Button) dialog.findViewById(R.id.btnSave);
        Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSave(dialogInterface);
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        ImageView ivPenIcon = (ImageView) dialog.findViewById(R.id.ivPenIcon);
        if (mBrushId == R.id.left_toolkit_item1) {
            ivPenIcon.setBackgroundResource(R.drawable.ic_my_pen_001_w);
        } else if (mBrushId == R.id.left_toolkit_item2) {
            ivPenIcon.setBackgroundResource(R.drawable.ic_my_pen_002_w);
        } else if (mBrushId == R.id.left_toolkit_item3) {
            ivPenIcon.setBackgroundResource(R.drawable.ic_my_pen_003_w);
        } else if (mBrushId == R.id.left_toolkit_item4) {
            ivPenIcon.setBackgroundResource(R.drawable.ic_my_pen_004_w);
        } else if (mBrushId == R.id.left_toolkit_item5) {
            ivPenIcon.setBackgroundResource(R.drawable.ic_my_pen_005_w);
        } else if (mBrushId == R.id.left_toolkit_item6) {
            ivPenIcon.setBackgroundResource(R.drawable.ic_my_pen_006_w);
        }

        setupPenSize(dialog);
        setupTitle(dialog);
        setupColors(dialog);
        updatePreview(dialog);
    }

    private void onCreateAct(Activity ctx, int brushId) {
        this.mContext = ctx;
        this.mBrushId = brushId;
    }
    private Activity mContext;
    private int mBrushId;
    public int outputColor = 0xff000000;
    public int outputBrushSize = 1;
    public boolean outputIsSave = false;

    /**
     * @see io.github.pastthepixels.freepaint.Graphics.DrawPath#draw
     * @see io.github.pastthepixels.freepaint.Graphics.DrawAppearance#PEN_TYPE_1
     */
    public void updatePreview(AlertDialog dialog/*DialogInterface dialogInterface*/) {
        //AlertDialog dialog = (AlertDialog) dialogInterface;

        //408*150
        Bitmap bitmap = Bitmap.createBitmap(450, 200, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        if (mBrushId == R.id.left_toolkit_item1 ||
            mBrushId == R.id.left_toolkit_item5) {
            PointF[] points = BookActivity4PreviewPath.getPath();
            Paint paint = new Paint();
            for (int i = 0; i < points.length - 1; ++i) {
                PointF p0 = points[i];
                PointF p1 = points[i + 1];
                paint.setColor(outputColor);//Color.BLACK);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeJoin(Paint.Join.ROUND);
                paint.setStrokeCap(Paint.Cap.ROUND);
                paint.setStrokeWidth(dp2px(dialog.getContext(), 5) * outputBrushSize);
                if (mBrushId == R.id.left_toolkit_item1) {
                    if (points.length - i < paint.getStrokeWidth()) {
                        paint.setStrokeWidth(points.length - i);
                    }
                }
                paint.setAntiAlias(true);
                canvas.drawLine(p0.x, p0.y, p1.x, p1.y, paint);
            }
        } else {
            PointF[] points = BookActivity4PreviewPath.getPath();
            Path path = new Path();
            for (int i = 0; i < points.length; ++i) {
                PointF p = points[i];
                if (i == 0) {
                    path.moveTo(p.x, p.y);
                } else {
                    path.lineTo(p.x, p.y);
                }
            }
            //don't use path.close();
            Paint paint = new Paint();
            if (mBrushId == R.id.left_toolkit_item3) {
                paint.setStrokeJoin(Paint.Join.BEVEL);
                paint.setStrokeCap(Paint.Cap.BUTT);
                paint.setColor((outputColor & 0xFFFFFF) | 0x80000000);//Color.BLACK);
            } else {
                //mBrushId == R.id.left_toolkit_item4
                paint.setStrokeJoin(Paint.Join.ROUND);
                paint.setStrokeCap(Paint.Cap.ROUND);
                paint.setColor(outputColor);//Color.BLACK);
            }
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(dp2px(dialog.getContext(), 5) * outputBrushSize);
            canvas.drawPath(path, paint);
        }


        ImageView imageView = (ImageView) dialog.findViewById(R.id.brush_preview);
        imageView.setImageBitmap(bitmap);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
    }

    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f); // 0.5f用于四舍五入
    }

    private void setupTitle(AlertDialog dialog) {
        String title = "Edit pen brush";
        if (mBrushId == R.id.left_toolkit_item1) {
            title = "Edit fountain pen brush";
        } else if (mBrushId == R.id.left_toolkit_item2) { //hidden
            title = "Edit pen brush";
        } else if (mBrushId == R.id.left_toolkit_item3) {
            title = "Edit highlighter brush";
        } else if (mBrushId == R.id.left_toolkit_item4) {
            title = "Edit marker pen brush";
        } else if (mBrushId == R.id.left_toolkit_item5) { //hidden
            title = "Edit pen brush";
        } else if (mBrushId == R.id.left_toolkit_item6) {
            title = "Edit pen brush";
        }
        TextView ivTitle = (TextView) dialog.findViewById(R.id.ivTitle);
        ivTitle.setText(title);
    }

    private void setupPenSize(AlertDialog dialog) {
        Slider slider = (Slider) dialog.findViewById(R.id.slider);
        if (mBrushId == R.id.left_toolkit_item3) {
            slider.setValueTo(5.0f * 5); //highlighter
        } else {
            slider.setValueTo(5.0f); //not highlighter
        }
        slider.setValue(outputBrushSize);
        slider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                outputBrushSize = (int)slider.getValue();
                updatePreview(dialog);
            }
        });
    }

    private void setupColors(AlertDialog dialog) {
        int[] ids = new int[] {
                R.id.colorBtn001,
                R.id.colorBtn002,
                R.id.colorBtn003,
                R.id.colorBtn004,
        };
        for (int id : ids) {
            dialog.findViewById(id).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for (int id_ : ids) {
                        CardView cardView = ((CardView) dialog.findViewById(id_));
                        cardView.setCardBackgroundColor(0xFFF1EDEC); //not selected
                        if (view.findViewWithTag("binding_1") != null) {
                            ((TextView) cardView.findViewWithTag("binding_1")).setTextColor(0xFF000000); //black font
                        }
                    }
                    if (view != null) {
                        ((CardView) view).setCardBackgroundColor(0xFF1C1B1B); //black bg selected
                        if (view.findViewWithTag("binding_1") != null) {
                            ((TextView) view.findViewWithTag("binding_1")).setTextColor(0xFFFFFFFF); //white font
                        }
                    }
                    if (view.getId() == R.id.colorBtn001) {
                        outputColor = 0xff000000;
                    } else if (view.getId() == R.id.colorBtn002) {
                        outputColor = 0xff808080;
                    } else if (view.getId() == R.id.colorBtn003) {
                        outputColor = 0xffFFFFFF;
                    }
                    updatePreview(dialog);
                }
            });
        }
        if (outputColor == 0xff000000) {
            dialog.findViewById(R.id.colorBtn001).performClick();
        } else if (outputColor == 0xff808080) {
            dialog.findViewById(R.id.colorBtn002).performClick();
        } else if (outputColor == 0xffFFFFFF) {
            dialog.findViewById(R.id.colorBtn003).performClick();
        }
    }
}
