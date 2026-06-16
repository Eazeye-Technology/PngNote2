package com.txkj.drawingapp.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.sherpaasr.data.ModelItem;
import com.example.sherpaasr.data.ModelRepository;
import com.example.sherpaasr.utils.DownloadManager;
import com.foobnix.pdf.info.IMG;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.txkj.drawingapp.R;
import com.txkj.notemobile2.colorpicker.FileMeta;

import java.io.File;
import java.text.DecimalFormat;

public class BookActivity4TranscriptionLanguage {
    //private final static int WIN_WIDTH = 718;//670 + 24 * 2;//312;

    private Activity mContext;

    public BookActivity4TranscriptionLanguage(Activity ctx) {
        this.mContext = ctx;
    }

    int[] ids = new int[] {
            R.id.llLanguageItem1,
            R.id.llLanguageItem2,
            R.id.llLanguageItem3,
    };
    ModelRepository repository;
    private DownloadManager downloadManager;
    private AlertDialog mDialog = null;
    public AlertDialog create() {
        repository = new ModelRepository(mContext, false);
        downloadManager = new DownloadManager();

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
        mDialog = dialog;
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if (downloadManager != null) {
                    downloadManager.cancelAll();
                }
            }
        });
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

                RadioButton rbDefaultChoose = dialog.findViewById(R.id.rbDefaultChoose);
                TextView tvDefaultChoose = dialog.findViewById(R.id.tvDefaultChoose);
                rbDefaultChoose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onClickRB(-1, true, null);
                    }
                });
                tvDefaultChoose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onClickRB(-1, true, null);
                    }
                });
                for (int idx = 0; idx < ids.length; ++idx) {
                    final int idxChoose = idx;
                    int id = ids[idx];
                    final ModelItem item = repository.getModels().get(idx);

                    LinearLayout linearLayoutRoot = (LinearLayout)dialog.findViewById(id);
                    ImageView ivDownload = linearLayoutRoot.findViewWithTag("ivDownload");
                    ImageView ivDelete = linearLayoutRoot.findViewWithTag("ivDelete");
                    ImageView ivStop = linearLayoutRoot.findViewWithTag("ivStop");
                    RadioButton rbChoose = linearLayoutRoot.findViewWithTag("rbChoose");
                    TextView tvChoose = linearLayoutRoot.findViewWithTag("tvChoose");
                    rbChoose.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            onClickRB(idxChoose, true, item);
                        }
                    });
                    tvChoose.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            onClickRB(idxChoose, true, item);
                        }
                    });
                    ivDownload.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startDownload(item);
                        }
                    });
                    ivDelete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            deleteDownload(item);
                        }
                    });
                    ivStop.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            deleteDownload(item);
                        }
                    });
                }

                {
                    int initIdx = -1;
                    String lang = BookActivity4Utils.getTranscriptLang(mContext);
                    if (lang != null) {
                        if (lang.equals("fr")) {
                            initIdx = 0; //Franch
                        } else if (lang.equals("de")) {
                            initIdx = 1; //German
                        } else if (lang.equals("es")) {
                            initIdx = 2; //Spanish
                        }
                    }
                    onClickRB(initIdx, false, null);
                }

                notifyDataSetChanged();

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

    private void onClickRB(int idx, boolean isSave, ModelItem item) {
        if (item != null) {
            if (item.getStatus() != ModelItem.Status.DOWNLOADED) {
                return;
            }
        }
        RadioButton rbDefaultChoose = mDialog.findViewById(R.id.rbDefaultChoose);
        RadioButton[] radioButtons = new RadioButton[ids.length];
        for (int idx_ = 0; idx_ < ids.length; ++idx_) {
            final int idxChoose = idx_;
            int id = ids[idx_];
            LinearLayout linearLayoutRoot = (LinearLayout) mDialog.findViewById(id);
            RadioButton rbChoose = linearLayoutRoot.findViewWithTag("rbChoose");
            radioButtons[idx_] = rbChoose;
        }


        if (rbDefaultChoose != null) {
            rbDefaultChoose.setChecked(false);
        }
        for (int i = 0; i < radioButtons.length; ++i) {
            if (radioButtons[i] != null) {
                radioButtons[i].setChecked(false);
            }
        }
        if (idx == -1) {
            if (rbDefaultChoose != null) {
                rbDefaultChoose.setChecked(true);
            }
            if (isSave) {
                BookActivity4Utils.setTranscriptLang(mContext, "en"); //English
            }
        } else {
            if (radioButtons[idx] != null) {
                radioButtons[idx].setChecked(true);
            }
            if (isSave) {
                if (idx == 0) {
                    BookActivity4Utils.setTranscriptLang(mContext, "fr"); //French
                } else if (idx == 1) {
                    BookActivity4Utils.setTranscriptLang(mContext, "de"); //Genman
                } else if (idx == 2) {
                    BookActivity4Utils.setTranscriptLang(mContext, "es"); //Spanish
                }
            }
        }
    }

    public void setBookBackText(DialogInterface dialogInterface, String backText_) {
        BookActivity4Utils.setBookBackText(mContext, backText_);
    }

    private void onDone(DialogInterface dialogInterface) {

    }



    private final DownloadManager.Callback cb = new DownloadManager.Callback() {
        @Override public void onProgress(ModelItem item, int percent) {
            int idx = repository.getModels().indexOf(item);
            if (idx >= 0) notifyItemChanged(idx); //adapter.notifyItemChanged(idx);
        }
        @Override public void onComplete(ModelItem item, boolean success, String message) {
            if (success) repository.markDownloaded(item, true);
            else item.setStatus(ModelItem.Status.NOT_DOWNLOADED);
            notifyDataSetChanged();//adapter.notifyDataSetChanged();
            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
        }
    };
    private void startDownload(ModelItem item) {
        item.setStatus(ModelItem.Status.DOWNLOADING);
        item.setPaused(false);
        //adapter.notifyDataSetChanged();
        notifyDataSetChanged();
        File dest = repository.getModelFile(item);
        downloadManager.download(item, dest, cb);
    }
    private void pauseDownload(ModelItem item) {
        downloadManager.pause(item);
        //adapter.notifyDataSetChanged();
        notifyDataSetChanged();
    }
    private void resumeDownload(ModelItem item) {
        downloadManager.resume(item, repository.getModelFile(item), cb);
        //adapter.notifyDataSetChanged();
        notifyDataSetChanged();
    }
    private void deleteDownload(ModelItem item) {
        downloadManager.cancel(item);
        repository.deleteModel(item);
        //adapter.notifyDataSetChanged();
        notifyDataSetChanged();
        Toast.makeText(mContext, "Deleted", Toast.LENGTH_SHORT).show();
        String curModel = BookActivity4Utils.getTranscriptLang(mContext);
        if (item.getLanguage() != null && curModel != null &&
                item.getLanguage().equals(curModel)) {
            //BookActivity4Utils.setTranscriptLang(mContext, "en");
            onClickRB(-1, true, item);
        }
    }

    private void notifyDataSetChanged() {
        for (int i = 0; i < ids.length; ++i) {
            notifyItemChanged(i);
        }
    }
    private void notifyItemChanged(int idx) {
        int id = ids[idx];
        final ModelItem item = repository.getModels().get(idx);
        LinearLayout linearLayoutRoot = (LinearLayout)mDialog.findViewById(id);
        int id_test = R.id.llLanguageItem1;
        TextView tvStatus = linearLayoutRoot.findViewWithTag("tvStatus");
        RadioButton rbChoose = linearLayoutRoot.findViewWithTag("rbChoose");
        ImageView ivDownload = linearLayoutRoot.findViewWithTag("ivDownload");
        ImageView ivDelete = linearLayoutRoot.findViewWithTag("ivDelete");
        ImageView ivStop = linearLayoutRoot.findViewWithTag("ivStop");
        tvStatus.setText(statusText(item));
        switch (item.getStatus()) {
            case DOWNLOADED:
                rbChoose.setVisibility(View.VISIBLE);
                ivDownload.setVisibility(View.INVISIBLE);
                ivDelete.setVisibility(View.GONE);//VISIBLE);
                ivStop.setVisibility(View.VISIBLE);//INVISIBLE);
                break;
            case DOWNLOADING:
                rbChoose.setVisibility(View.INVISIBLE);
                ivDownload.setVisibility(View.INVISIBLE);
                ivDelete.setVisibility(View.GONE);//GONE);
                ivStop.setVisibility(View.INVISIBLE);//VISIBLE);
                break;
            default: //not downloaded
                rbChoose.setVisibility(View.INVISIBLE);
                ivDownload.setVisibility(View.VISIBLE);
                ivDelete.setVisibility(View.GONE);//.GONE);
                ivStop.setVisibility(View.VISIBLE);//INVISIBLE);
                break;
        }
    }

    private String statusText(ModelItem item) {
        switch (item.getStatus()) {
            case DOWNLOADED:
                //return "";//"Downloaded";
                File f = repository.getModelFile(item);
                File modelDir = //"models/"
                        new File(f.getParent(), "" + f.getName().replace(".tar.bz2", ""));
                return getReadableFileSize(getFileSize(modelDir));

            case DOWNLOADING:
                if (item.getStatusExtract()) {
                    return "Extracting, please wait...";
                } else {
                    return item.isPaused() ?
                            "Paused " + item.getProgress() + "%" :
                            ""  + item.getProgress() + "%"; //"Downloading " + item.getProgress() + "%";
                }
            default:
                return "";//"Not downloaded";
        }
    }

    public static long getFileSize(File file) {
        if (file == null) {
            return 0;
        }
        if (file.isFile()) {
            return file.length();
        }
        if (file.isDirectory()) {
            long size = 0;
            File[] children = file.listFiles();
            if (children != null) {
                for (File child : children) {
                    size += getFileSize(child);
                }
            }
            return size;
        }
        return 0;
    }

    public static String getReadableFileSize(long size) {
        if (size <= 0) return "0 B";
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#.##").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }
}
