package com.txkj.drawingapp.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.txkj.drawingapp.R;
import com.txkj.notemobile2.colorpicker.FileMeta;

public class BookActivity4TranscriptionLanguage {
    //private final static int WIN_WIDTH = 718;//670 + 24 * 2;//312;

    private Activity mContext;

    public BookActivity4TranscriptionLanguage(Activity ctx) {
        this.mContext = ctx;
    }

    public AlertDialog create() {
        AlertDialog dialog = new MaterialAlertDialogBuilder(this.mContext, BookActivity4Utils.getCenteredTitleThemeOverlay())
                //.setTitle(title)
                .setView(R.layout.activity_book4_transcript_language)
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
                BookActivity4Utils.runFullScreen(mContext);

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
            }
        });
        try {
            Window window = dialog.getWindow();
            if (window != null) {
                int WIN_WIDTH = mContext.getResources().getDimensionPixelSize(R.dimen.activity_dialog_upgrade_min_width);
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

    }
}
