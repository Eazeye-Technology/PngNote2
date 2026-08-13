package com.txkj.drawingapp.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.txkj.drawingapp.R;
import com.txkj.notemobile2.BookListActivity;

public class BookActivity4MeetingDurationDialog {
    //private final static int WIN_WIDTH = 312 + 24 * 2;//312;

    private Activity mContext;
    private String mOldName;

    public BookActivity4MeetingDurationDialog(Activity ctx, String oldName) {
        this.mContext = ctx;
        this.mOldName = oldName;
    }

    public AlertDialog create() {
        AlertDialog dialog = new MaterialAlertDialogBuilder(this.mContext, BookActivity4Utils.getCenteredTitleThemeOverlay())
                //.setTitle(title)
                .setView(R.layout.activity_book4_meeting_duration)
                .setCancelable(true)
//                .setPositiveButton("Done", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        TextView input = ((androidx.appcompat.app.AlertDialog) dialog).findViewById(R.id.textState);
//                        //Toast.makeText(BookListActivity.this, input.getText(), Toast.LENGTH_LONG).show();
//                        renameBook(dialog, input.getText().toString());
//                    }
//                })
//                .setNegativeButton("Cancel", null)
                .create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                BookActivity4Utils.runFullScreen(mContext);

//                                TextView tvDialogTitle = ((AlertDialog) dialog).findViewById(R.id.tvDialogTitle);
//                                tvDialogTitle.setText("Rename note");
                AlertDialog dialog = (AlertDialog) dialogInterface;
                TextInputEditText input = dialog.findViewById(R.id.textState);
                try {
                    if (mOldName != null) {
                        input.setText(mOldName);
                        input.setSelection(mOldName.length());
                    }
                    input.requestFocus();
                } catch (Throwable eee) {
                    eee.printStackTrace();
                }

                Button btnSave = (Button) dialog.findViewById(R.id.btnSave);
                Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
                btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        TextView input = dialog.findViewById(R.id.textState);
                        //Toast.makeText(BookListActivity.this, input.getText(), Toast.LENGTH_LONG).show();
                        editMeetingDuration(dialog, input.getText().toString());
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
                int WIN_WIDTH = mContext.getResources().getDimensionPixelSize(R.dimen.activity_dialog_upgrade_min_width);
                window.setLayout(WIN_WIDTH, WindowManager.LayoutParams.WRAP_CONTENT);
            }
        } catch (Throwable eee) {
            eee.printStackTrace();
        }
        return dialog;
    }

    public void editMeetingDuration(DialogInterface dialog, String newName) {
        BookActivity4Utils.editMeetingDuration(mContext, newName);
    }
}
