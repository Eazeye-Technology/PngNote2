package com.txkj.notemobile2;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.dseink.EinkUtils;
import com.foobnix.pdf.info.Android6Mod;
import com.txkj.contentbrowser.NoteFragment2;
import com.txkj.contentbrowser.NoteFragment3;
import com.txkj.contentbrowser.NoteFragment4;
import com.txkj.drawingapp.R;
import com.txkj.drawingapp.activity.BookActivity4Fragment;
import com.txkj.drawingapp.activity.BookActivity4Utils;
import com.upgradetool.upgrade.UpgradeUtil;

public class BookListActivity extends AppCompatActivity {
    public final static boolean USE_DS = true;

    private final static boolean D = true;
    private final static String TAG = "BookListActivity";

    public void onDialogDismiss() {
        //FIXME:
        EinkUtils.forceEinkFullUpdateWithView(findViewById(R.id.content_layout));
    }

//    public BookListFragment2 bookListFragment2;
    public BookListFragment bookListFragment;
    public NoteFragment2 noteFragment2;
    public NoteFragment3 noteFragment3;
    public NoteFragment4 noteFragment4;

    private FragmentManager fragmentManager = null;
    private FragmentTransaction fragmentTransaction = null;

    public boolean isIntentNew = false;
    public boolean isIntentOpen = false;

    public UpgradeUtil upgradeUtil;

    //FIXME:added
    private void setupPadding() {
        if (true) {
            FrameLayout rootLayout = findViewById(R.id.root_layout);
            rootLayout.setPadding(0, 50, 0, 0);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booklist_body);
        setupPadding();

        if (UpgradeUtil.USE_UPGRADE) {
            upgradeUtil = new UpgradeUtil(this);
            upgradeUtil.onCreate_upgrade();
        }

        ActionBar topAppBar = getSupportActionBar();
        if (topAppBar != null) {
            topAppBar.setTitle("Note List");
            topAppBar.setIcon(R.mipmap.ic_launcher);
            if (!BookActivity4Utils.USE_ACTIONBAR) {
                topAppBar.hide();
            }
        }


        Intent intent = this.getIntent();
        if (intent != null) {
            BookActivity4Utils.APP_OPEN = intent.getStringExtra("APP_OPEN");
            BookActivity4Utils.APP_FILE = intent.getStringExtra("APP_FILE");
            if (D) {
                Log.e(TAG, "APP_OPEN:" + BookActivity4Utils.APP_OPEN);
                Log.e(TAG, "APP_FILE:" + BookActivity4Utils.APP_FILE);
            }
            if (BookActivity4Utils.APP_OPEN != null && BookActivity4Utils.APP_OPEN.equals("NEW")) {
                isIntentNew = true;
            } else if (BookActivity4Utils.APP_FILE != null && !BookActivity4Utils.APP_FILE.equals("")) {
                isIntentOpen = true;
            }
        }
        int stateStarted = 0;
        if (savedInstanceState != null) {
            stateStarted = savedInstanceState.getInt(STATE_STARTED, 0);
            stateStarted_ = stateStarted;
            globalDisableFingerDraw = savedInstanceState.getBoolean(STATE_DISABLE_FINGER_DRAW, false);
        }
        if (stateStarted == 0) {
            checkPermission();

            if (UpgradeUtil.USE_UPGRADE) {
                if (upgradeUtil != null) {
                    upgradeUtil.onCreateUpdateReceiver();
                    upgradeUtil.checkVersion();
                }
            }
        }




        bookListFragment = new BookListFragment();
//        bookListFragment2 = new BookListFragment2();
        noteFragment2 = new NoteFragment2();
        noteFragment3 = new NoteFragment3();
        noteFragment4 = new NoteFragment4();

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        if (false) {
            fragmentTransaction.replace(R.id.content_layout, bookListFragment);
        } else if (false) {
            fragmentTransaction.replace(R.id.content_layout, noteFragment2);
        } else if (false) {
            fragmentTransaction.replace(R.id.content_layout, noteFragment3);
        } else {
            fragmentTransaction.replace(R.id.content_layout, noteFragment4);
        }
        fragmentTransaction.commit();
    }


    //https://blog.csdn.net/zuo_er_lyf/article/details/82659426
    //https://www.dev2qa.com/android-read-write-external-storage-file-example/
    private final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION = 100;
    private void checkPermission(){
        // Check whether this app has write external storage permission or not.
        int writeExternalStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int writeExternalStoragePermission2 = ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS);
        int writeExternalStoragePermission3 = ContextCompat.checkSelfPermission(this, android.Manifest.permission.REQUEST_INSTALL_PACKAGES);
        // If do not grant write external storage permission.
        if (writeExternalStoragePermission != PackageManager.PERMISSION_GRANTED ||
                writeExternalStoragePermission2 != PackageManager.PERMISSION_GRANTED ||
                writeExternalStoragePermission3 != PackageManager.PERMISSION_GRANTED
        ) {
            // Request user to grant write external storage permission.
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.POST_NOTIFICATIONS,
                    Manifest.permission.REQUEST_INSTALL_PACKAGES,
            }, REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION);
        } else {
            getPermission2();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Android6Mod.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION) {
            int grantResultsLength = grantResults.length;
            if (grantResultsLength >= 3 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[2] == PackageManager.PERMISSION_GRANTED
            ) {
                //Toast.makeText(getApplicationContext(), "You grant write external storage permission. Please click original button again to continue.", Toast.LENGTH_LONG).show();
                getPermission2();
            } else {
                //Toast.makeText(getApplicationContext(), "You denied write external storage permission.", Toast.LENGTH_LONG).show();
                getPermission2();
            }
        }
    }

    private final static int REQUEST_CODE = 1111;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {

                } else {

                }
            }
        }
    }

    private void getPermission2(){
        if (false) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (!Environment.isExternalStorageManager()) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                    startActivityForResult(intent, REQUEST_CODE);
                }
            }
        } else {
            if (!Android6Mod.canWrite(this)) {
                Android6Mod.checkPermissions(this, true);
                return;
            }
        }
    }

    /*
com.foobnix.pdf.info.Android6
if (!Android6.canWrite(this)) {
Android6.checkPermissions(this, true);
return;
}
@Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, android.app.Activity
public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
Android6.onRequestPermissionsResult(this, i, strArr, iArr);
}
*/
    private static final String STATE_STARTED = "STATE_STARTED";
    public int stateStarted_ = 0;
    private static final String STATE_DISABLE_FINGER_DRAW = "STATE_DISABLE_FINGER_DRAW";
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_STARTED, 1);
        outState.putBoolean(STATE_DISABLE_FINGER_DRAW, globalDisableFingerDraw);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        Fragment currentFragment = fragmentManager.getFragments().get(fragmentManager.getFragments().size() - 1);
        if (currentFragment instanceof BookActivity4Fragment) {
            if (((BookActivity4Fragment) currentFragment).onKeyDown(keyCode, event)) {
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        Fragment currentFragment = fragmentManager.getFragments().get(fragmentManager.getFragments().size() - 1);
        if (currentFragment instanceof BookActivity4Fragment) {
            if (((BookActivity4Fragment) currentFragment).onKeyUp(keyCode, event)) {
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        if (!BookActivity4Utils.onBackPressed(this)) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (noteFragment2 != null) {
            noteFragment2.refresh();
        }
        if (noteFragment3 != null) {
            noteFragment3.refresh();
        }
        if (noteFragment4 != null) {
            noteFragment4.refresh();
        }
    }












    @Override
    protected Dialog onCreateDialog(int id) {
        if (UpgradeUtil.USE_UPGRADE) {
            Dialog dialog = upgradeUtil.onCreateDailog_upgrade(id);
            if (dialog != null) {
                return dialog;
            }
        }
        return super.onCreateDialog(id);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (UpgradeUtil.USE_UPGRADE && upgradeUtil != null) {
            upgradeUtil.onDestroyUpdateReceiver();
        }
    }

    private ProgressDialog mProgressDialog = null;
    public void createWaitingProgressDialog() {
        if (mProgressDialog == null || !mProgressDialog.isShowing()) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setTitle("");
            mProgressDialog.setMessage("Saving, please wait...");
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }
    }
    public void cancelWaitingProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    boolean globalDisableFingerDraw = false;
    public void setGlobalDisableFingerDraw(boolean isDisable) {
        this.globalDisableFingerDraw = isDisable;
    }
    public boolean getGlobalDisableFingerDraw() {
        return this.globalDisableFingerDraw;
    }
}
