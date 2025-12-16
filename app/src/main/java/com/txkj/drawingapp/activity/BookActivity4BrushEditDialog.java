package com.txkj.drawingapp.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.slider.Slider;
import com.txkj.drawingapp.R;
import com.txkj.notemobile2.BookListActivity;

public class BookActivity4BrushEditDialog {
    public BookActivity4BrushEditDialog(Activity ctx, int brushId) {
        onCreateAct(ctx, brushId);
    }

    public AlertDialog create() {
        //
        AlertDialog dialog = new MaterialAlertDialogBuilder(mContext, BookListActivity.getCenteredTitleThemeOverlay())
                //.setTitle(title)
                .setView(R.layout.activity_book4_brush2_edit)
                .setCancelable(true)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        AlertDialog dialog = (AlertDialog) dialogInterface;
                        Slider slider = dialog.findViewById(R.id.slider);
                        outputBurshSize = (int)slider.getValue();
                        outputIsSave = true;
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                AlertDialog dialog = (AlertDialog) dialogInterface;

                setupPenSize(dialog);
                setupTitle(dialog);
                setupColors(dialog);
                updatePreview(dialogInterface);
            }
        });
        return dialog;
    }

    private void onCreateAct(Activity ctx, int brushId) {
        this.mContext = ctx;
        this.mBrushId = brushId;
    }
    private Activity mContext;
    private int mBrushId;
    private int outputColor = 0xff000000;
    private int outputBurshSize = 1;
    private boolean outputIsSave = false;

    public void updatePreview(DialogInterface dialogInterface) {
        AlertDialog dialog = (AlertDialog) dialogInterface;

        PointF[] points = BookActivity4PreviewPath.path;
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
        paint.setColor(outputColor);//Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(dp2px(dialog.getContext(), 5) * outputBurshSize);
        //408*150
        Bitmap bitmap = Bitmap.createBitmap(450, 200, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawPath(path, paint);

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
        outputBurshSize = (int)slider.getValue();
        slider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                outputBurshSize = (int)slider.getValue();
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
    }
}
