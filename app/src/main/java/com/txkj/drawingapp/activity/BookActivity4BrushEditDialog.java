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
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;

import com.foobnix.android.utils.IntegerResponse;
import com.foobnix.pdf.info.view.CustomSeek;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.slider.Slider;
import com.txkj.drawingapp.R;
import com.txkj.notemobile2.BookListActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import io.github.pastthepixels.freepaint.Graphics.Point;

//popupwindow_pen_style.xml
public class BookActivity4BrushEditDialog {
    //private final static int WIN_WIDTH = 544 + 24 * 2;
    final boolean USE_SLIDER = false;

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
                BookActivity4Utils.runFullScreen(mContext);

                onShowDialog(dialog);
            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if (mContext instanceof BookListActivity) {
                    ((BookListActivity) mContext).onDialogDismiss();
                }
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

    public void afterShow(AlertDialog dialog) {
        if (BookListActivity.USE_DS) {
            dialog.findViewById(R.id.tvColor).setVisibility(View.GONE);
            dialog.findViewById(R.id.llColor).setVisibility(View.GONE);
        }
    }

    private void onSave(AlertDialog dialogInterface) {
        AlertDialog dialog = (AlertDialog) dialogInterface;
        if (USE_SLIDER) {
            Slider slider = dialog.findViewById(R.id.slider);
            outputBrushSize = (int) slider.getValue();
        } else {
            CustomSeek slider = dialog.findViewById(R.id.slider_seek2);
            outputBrushSize = (int) slider.getCurrentValue();
        }
        outputIsSave = true;
        BookActivity4Utils.onLongClickSubmenu1_after(mContext,
        BookActivity4BrushEditDialog.this, mBrushId);
    }

    public void onShowDialog(AlertDialog dialogInterface) {
        AlertDialog dialog = (AlertDialog) dialogInterface;

        if (BookListActivity.USE_DS) {
            dialog.findViewById(R.id.tvColor).setVisibility(View.GONE);
            dialog.findViewById(R.id.llColor).setVisibility(View.GONE);
        }

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
        } else if (mBrushId == R.id.left_toolkit_item4) { //pencil //DrawAppearance.PEN_TYPE_4
            //search new pencil implementation

            PointF[] points_ = BookActivity4PreviewPath.getPath();
            Paint paint = new Paint();
            paint.setColor(outputColor);//Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setStrokeWidth(dp2px(dialog.getContext(), 5) * outputBrushSize);
            CopyOnWriteArrayList<Point> points = new CopyOnWriteArrayList<>();
            for (PointF p : points_) {
                points.add(new Point(p.x, p.y));
            }

            int baseColor = paint.getColor();
            //Paint base = new Paint(paint);
            boolean useDrawPoints = true;
            List<Float> toDrawList = new ArrayList<>();
            for (int i = 0; i < points.size() - 1; ++i) {
                Point p0 = points.get(i);
                Point p1 = points.get(i + 1);
                drawStroke(canvas, p0, p1, paint.getStrokeWidth(), baseColor, useDrawPoints, toDrawList);
            }
            if (useDrawPoints) {
                paint.setStyle(Paint.Style.FILL);
                paint.setColor((baseColor & 0xFFFFFF) | 0xFF000000);
                paint.setStrokeWidth(1.1f);
                //int pos = 0;
                float[] points__ = new float[toDrawList.size()];
                for (int i = 0; i < toDrawList.size(); ++i) {
                    Float p = toDrawList.get(i);
                    points__[i] = p;
                }
                canvas.drawPoints(points__, paint);
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
        return (int) (dpValue * scale + 0.5f);
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
            //title = "Edit marker pen brush";
            title = "Edit pencil brush";
        } else if (mBrushId == R.id.left_toolkit_item5) { //hidden
            title = "Edit pen brush";
        } else if (mBrushId == R.id.left_toolkit_item6) {
            title = "Edit pen brush";
        }
        TextView ivTitle = (TextView) dialog.findViewById(R.id.ivTitle);
        ivTitle.setText(title);
    }

    private void setupPenSize(AlertDialog dialog) {
        if (USE_SLIDER) {
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
        } else {
            CustomSeek slider = dialog.findViewById(R.id.slider_seek2);
            if (mBrushId == R.id.left_toolkit_item3) { //highlighter
                slider.init(1, 5 * 5, outputBrushSize);
            } else {
                slider.init(1, 5, outputBrushSize); //not highlighter
            }
            slider.setOnSeekChanged(new IntegerResponse() {
                @Override
                public boolean onResultRecive(int result) {
                    outputBrushSize = (int)result;
                    updatePreview(dialog);
                    return false;
                }
            });
        }
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

































    //
    //new pencil implementation, 20260429, from Free-hand Drawing App
    //https://www.figma.com/design/GJTGvpoKP8En3dkw0H2VBl/Free-hand-Drawing-App
    //
    private void drawStroke(Canvas ctx,
                            Point from,
                            Point to,
                            double size,
                            int color,
                            boolean useDrawPoints,
                            List<Float> toDrawList
    ) {
        Random rand = new Random(0);
        Paint paint = new Paint();
        double distance = Math.sqrt(Math.pow(to.x - from.x, 2) + Math.pow(to.y - from.y, 2));
        int steps = (int)Math.max(Math.ceil(distance), 1);
        for (int i = 0; i <= steps; i++) {
            double t = (float)i / steps;
            double x = from.x + (to.x - from.x) * t;
            double y = from.y + (to.y - from.y) * t;
            double pressure = from.pressure + (to.pressure - from.pressure) * t;

            drawPoint(ctx, x, y, pressure, size, color, paint, rand, useDrawPoints, toDrawList);
        }
    }

    private final boolean FORCE_PENCIL_SIMPLIFY = false;
    private void drawPoint(Canvas canvas,
                           double x,
                           double y,
                           double pressure,
                           double size,
                           int color,
                           Paint paint,
                           Random rand,
                           boolean useDrawPoints,
                           List<Float> toDrawList
    ) {
        //1.0;//5.0;//
        final double MAX_LOOP = 5.0;//Double.MAX_VALUE;//5.0;//1.0;//2.0;//5.0;
        //FIXME:added, loop times don't over MAX_LOOP (like 5.0)

        double effectivePressure = Math.max(0.15, Math.min(1, pressure));
        double radius = size * effectivePressure;

        double particleDensity = 0.3 + effectivePressure * 0.7;
        double particleCount = Math.min(MAX_LOOP, Math.floor(radius * 15 * particleDensity));

        double particleOpacity = 0.08 + effectivePressure * 0.15;

        //canvas.save();
        if (true) {
            for (int i = 0; i < particleCount; i++) {
                if (rand.nextDouble() > particleDensity) continue;

                double angle = rand.nextDouble() * Math.PI * 2;
                double distance = Math.sqrt(rand.nextDouble()) * radius;
                double px = x + Math.cos(angle) * distance;
                double py = y + Math.sin(angle) * distance;

                double particleSize = 0.3 + rand.nextDouble() * (0.8 + effectivePressure * 0.8);
                double opacity = particleOpacity * (0.5 + rand.nextDouble() * 0.5);

//            ctx.globalAlpha = opacity;
//            ctx.fillStyle = color;
                paint.setStyle(Paint.Style.FILL);
                paint.setColor((color & 0xFFFFFF) |
                        ((((int) (0xFF * opacity)) & 0xFF) << 24));
                if (useDrawPoints) {
                    if (toDrawList != null) {
                        toDrawList.add((float) (px));
                        toDrawList.add((float) (py));
                    }
                } else {
                    canvas.drawRect(
                            (float) (px - particleSize / 2),
                            (float) (py - particleSize / 2),
                            (float) (px - particleSize / 2) + (float) (particleSize),
                            (float) (py - particleSize / 2) + (float) (particleSize),
                            paint
                    );
                }
//                canvas.drawCircle(
//                        (float) (px - particleSize / 2),
//                        (float) (py - particleSize / 2),
//                        (float)size,
//                        paint);
            }

            if (true) {
                double edgeParticles = Math.min(MAX_LOOP, Math.floor(radius * 3));
                for (int i = 0; i < edgeParticles; i++) {
                    double angle = rand.nextDouble() * Math.PI * 2;
                    double distance = radius * (0.7 + rand.nextDouble() * 0.6);
                    double px = x + Math.cos(angle) * distance;
                    double py = y + Math.sin(angle) * distance;

                    double particleSize = 0.3 + rand.nextDouble() * 0.8;
                    double opacity = particleOpacity * 0.3 * (0.3 + rand.nextDouble() * 0.7);

                    //ctx.globalAlpha = opacity;
                    //ctx.fillStyle = color;
                    paint.setStyle(Paint.Style.FILL);
                    paint.setColor((color & 0xFFFFFF) |
                            ((((int) (0xFF * opacity)) & 0xFF) << 24));
                    if (useDrawPoints) {
                        if (toDrawList != null) {
                            toDrawList.add((float) (px));
                            toDrawList.add((float) (py));
                        }
                    } else {
                        if (true) {
                            canvas.drawRect(
                                    (float) (px - particleSize / 2),
                                    (float) (py - particleSize / 2),
                                    (float) (px - particleSize / 2) + (float) (particleSize),
                                    (float) (py - particleSize / 2) + (float) (particleSize),
                                    paint
                            );
                        } else {
//                            canvas.drawCircle(
//                                    (float) (px - particleSize / 2),
//                                    (float) (py - particleSize / 2),
//                                    (float) particleSize,
//                                    paint);
                        }
                    }
                }
            }
//            canvas.restore();
        } else {
            paint.setStyle(Paint.Style.FILL);
            paint.setColor((color & 0xFFFFFF) | 0xFF000000);
            canvas.drawCircle((float)x, (float)y, (float)size, paint);
        }
    }
}
