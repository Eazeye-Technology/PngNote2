package com.txkj.drawingapp.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.agsw.FabricView.FabricView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.txkj.drawingapp.R;
import com.txkj.notemobile2.book.BookIO;
import com.txkj.notemobile2.colorpicker.FileMeta;

public class BookActivity4BackgroundDialog {
    private final static int WIN_WIDTH = 670 + 24 * 2;//312;

    private Activity mContext;
    private int mBackgroundMode = -1;

    public BookActivity4BackgroundDialog(Activity ctx, int backgroundMode) {
        this.mContext = ctx;
        this.mBackgroundMode = backgroundMode;
    }

    public AlertDialog create() {
        AlertDialog dialog = new MaterialAlertDialogBuilder(this.mContext, BookActivity4Utils.getCenteredTitleThemeOverlay())
                //.setTitle(title)
                .setView(R.layout.activity_book4_bg)
                .setCancelable(true)
//                .setPositiveButton("Done", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int which) {
//                        onDone(dialogInterface);
//                    }
//                })
//                .setNegativeButton("Cancel", null)
                .create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                AlertDialog dialog = (AlertDialog) dialogInterface;

                Button btnSave = (Button) dialog.findViewById(R.id.btnSave);
                Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
                btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onDone(dialogInterface);
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

                int[] ll_ids = {
                        R.id.llradio1,
                        R.id.llradio2,
                        R.id.llradio3,
                        R.id.llradio4,
                        R.id.llradio5,
                        R.id.llradio6,
                };
                int[] ids = {
                        R.id.radio1, //Blank
                        R.id.radio2, //Lined==Note book
                        R.id.radio3, //Lined long dash ~=Note book
                        R.id.radio4, //lined dash ~=Note book
                        R.id.radio5, //dot grid==Dotted (x)Infinite canvas
                        R.id.radio6, //lined dot grid~=Grid
                };
                for (int index = 0; index < ids.length; ++index) {
                    int ll_id = ll_ids[index];
                    int id = ids[index];
                    View ll = dialog.findViewById(ll_id);
                    RadioButton input = dialog.findViewById(id);
                    ll.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.findViewById(id).performClick();
                        }
                    });
                    input.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            for (int id2 : ids) {
                                if (id2 != id) {
                                    RadioButton input = (RadioButton)dialog.findViewById(id2);
                                    input.setChecked(false);
                                }
                            }
                            RadioButton input = (RadioButton)dialog.findViewById(id);
                            input.setChecked(true);
                        }
                    });
                }
                {
                    if (mBackgroundMode == FabricView.BACKGROUND_STYLE_GRAPH_PAPER) {
                        RadioButton input = (RadioButton)dialog.findViewById(R.id.radio6);
                        input.setChecked(true);
                    } else if (mBackgroundMode == FabricView.BACKGROUND_STYLE_NOTEBOOK_PAPER) {
                        RadioButton input = (RadioButton)dialog.findViewById(R.id.radio2);
                        input.setChecked(true);
                    } else if (mBackgroundMode == FabricView.BACKGROUND_STYLE_DOT_PAPER) {
                        RadioButton input = (RadioButton)dialog.findViewById(R.id.radio5);
                        input.setChecked(true);
                    } else if (mBackgroundMode == FabricView.BACKGROUND_STYLE_BLANK) {
                        RadioButton input = (RadioButton)dialog.findViewById(R.id.radio1);
                        input.setChecked(true);
                    }
                }
            }
        });
        try {
            Window window = dialog.getWindow();
            if (window != null) {
                window.setLayout(WIN_WIDTH, WindowManager.LayoutParams.WRAP_CONTENT);
            }
        } catch (Throwable eee) {
            eee.printStackTrace();
        }
        return dialog;
    }

    public void setBookBackText(DialogInterface dialogInterface, String backText_) {
        BookActivity4Utils.setBookBackText(mContext, backText_);
    }

    private void onDone(DialogInterface dialogInterface) {
        AlertDialog dialog = (AlertDialog) dialogInterface;
        RadioButton radio1 = dialog.findViewById(R.id.radio1); //blank
        RadioButton radio2 = dialog.findViewById(R.id.radio2); //Lined
        RadioButton radio3 = dialog.findViewById(R.id.radio3); //Lined long dash
        RadioButton radio4 = dialog.findViewById(R.id.radio4); //Lined dash
        RadioButton radio5 = dialog.findViewById(R.id.radio5); //dot grid
        RadioButton radio6 = dialog.findViewById(R.id.radio6); //dot lined grid
        String backText_ = null;
        if (radio1.isChecked()) {
            backText_ = FileMeta.NONE;
        } else if (radio2.isChecked()) {
            backText_ = FileMeta.LINED;
        } else if (radio3.isChecked()) {
            backText_ = FileMeta.LINED; //FIXME:
        } else if (radio4.isChecked()) {
            backText_ = FileMeta.LINED; //FIXME:
        } else if (radio5.isChecked()) {
            backText_ = FileMeta.DOTTED;
        } else if (radio6.isChecked()) {
            backText_ = FileMeta.GRAPH;
        }
        setBookBackText(dialogInterface, backText_);
    }
}
