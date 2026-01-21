package com.txkj.drawingapp.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.txkj.drawingapp.R;

public class BookActivity4TipsDialog {
    private final static int WIN_WIDTH = 630;// + 24 * 2;

    private Activity mAct;

    public BookActivity4TipsDialog(Activity context) {
        this.mAct = context;
    }

    public AlertDialog create() {
        AlertDialog dialog = new MaterialAlertDialogBuilder(this.mAct, BookActivity4Utils.getCenteredTitleThemeOverlay())
                //.setTitle(title)
                .setView(R.layout.activity_book4_dialog_tips)
                .setCancelable(true)
                /*
                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })*/
                .create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                AlertDialog dialog = (AlertDialog) dialogInterface;
                //TextInputEditText input = dialog.findViewById(R.id.textState);
                Button btnSave = (Button) dialog.findViewById(R.id.btnSave); //Close
                //Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
                btnSave.setOnClickListener(new View.OnClickListener() {
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
                window.setLayout(WIN_WIDTH, WindowManager.LayoutParams.WRAP_CONTENT);
            }
        } catch (Throwable eee) {
            eee.printStackTrace();
        }
        return dialog;
    }
}