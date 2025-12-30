package com.txkj.drawingapp.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.txkj.drawingapp.R;

public class BookActivity4TipsDialog {
    private Activity mAct;

    public BookActivity4TipsDialog(Activity context) {
        this.mAct = context;
    }

    public AlertDialog create() {
        AlertDialog dialog = new MaterialAlertDialogBuilder(this.mAct, BookActivity4Utils.getCenteredTitleThemeOverlay())
                //.setTitle(title)
                .setView(R.layout.activity_book4_dialog_tips)
                .setCancelable(true)
                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {

            }
        });
        try {
            Window window = dialog.getWindow();
            if (window != null) {
                window.setLayout(800, WindowManager.LayoutParams.WRAP_CONTENT);
            }
        } catch (Throwable eee) {
            eee.printStackTrace();
        }
        return dialog;
    }
}