package com.txkj.drawingapp.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.text.TextPaint;
import android.util.Log;
import android.util.SizeF;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.foobnix.android.utils.KeyboardsMod;
import com.github.guanpy.wblib.bean.DrawPoint;
import com.github.guanpy.wblib.widget.DrawTextView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.slider.LabelFormatter;
import com.google.android.material.slider.Slider;
import com.k2fsa.sherpa.onnx.OfflineSpeakerDiarizationConfig;
import com.k2fsa.sherpa.onnx.OfflineSpeakerDiarizationSegment;
import com.k2fsa.sherpa.onnx.OnlineModelConfig;
import com.k2fsa.sherpa.onnx.OnlineRecognizerKt;
import com.k2fsa.sherpa.onnx.speaker.diarization.SpeakerDiarizationObject;
import com.k2fsa.sherpa.onnx.speaker.diarization.screens.ReadWaveFileKt;
import com.sys.speech.activity.DictResultActivity;
import com.sys.speech.db.SDRecordingsDatabase;
import com.sys.speech.dialog.PlayerDialog;
import com.sys.speech.pojo.RecordingItem;
import com.txkj.contentbrowser.NoteFragment4;
import com.txkj.drawingapp.R;
import com.txkj.drawingapp.db.NoteItem;
import com.txkj.drawingapp.db.SDNotesDatabase;
import com.txkj.notemobile2.Book;
import com.txkj.notemobile2.PageGridActivity;
import com.txkj.notemobile2.book.BookIO;
import com.txkj.notemobile2.book.BookPage;
import com.txkj.notemobile2.book.FastFile;
import com.txkj.notemobile2.colorpicker.FileMeta;
import com.txkj.notemobile2.colorpicker.LineWidthDialog;
import com.txkj.notemobile2.colorpicker.MaterialColorDialog;
import com.txkj.notemobile2.colorpicker.PaintSelectDialog;
import com.txkj.notemobile2.colorpicker.SimpleColorDialog;
import com.txkj.notemobile2.ui.CanvasBoox;
import com.txkj.notemobile2.ui.Page;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import io.github.pastthepixels.freepaint.Graphics.BitmapVector;
import io.github.pastthepixels.freepaint.Graphics.DrawAppearance;
import io.github.pastthepixels.freepaint.Graphics.DrawCanvas;
import io.github.pastthepixels.freepaint.Graphics.DrawPath;
import io.github.pastthepixels.freepaint.Graphics.Point;
import io.github.pastthepixels.freepaint.MainActivity;
import io.github.pastthepixels.freepaint.Tools.EraserTool;
import io.github.pastthepixels.freepaint.Tools.SelectionTool;
import io.material.catalog.windowpreferences.WindowPreferencesManager;
import kotlin.jvm.functions.Function3;

//FIXME:onBackPressed, onCreateOptionsMenu, onDestroy, onKeyDown, onKeyUp

//FIXME:this.isDirty should be always true
//FIXME:I need to remove isDirty var
//isDirty = true; //FIXME: force save
//TODO:notifyForceSave, when view onSizeChanged or other events, need call it
public class BookActivity4Fragment extends Fragment {
    public final static boolean ENABLE_SHAPE_PEN = true; //enable shape pen
    public final static boolean ENABLE_NO_DETECT_SHAPE_PEN = true; //enable shape pen but close auto detect

    private final static boolean INIT_EMPTY_BACK_TEXT_WHEN_ADD_PAGE = false; //empty back texture when adding new page
    private final static boolean NO_PEN_BOTTOM_POPUP = true; //don't show brush bottom popup

    private final static boolean USE_FIRST_HIDE_EDITTEXT = true;
    private final static boolean USE_FLOAT_IME_TOOLBAR = false;
    private final static boolean USE_BOTTOM_IME_TOOLBAR = true;

    private final static boolean TIMER_AUTOSAVE = true;
    public final static boolean SAVING_ASYNC_MULTI = true;
    private final static int SAVING_ASYNC_MULTI_TIMEOUT = 6; //6sec
    private final ReentrantLock saveLock = new ReentrantLock();
    private final static long DELAY_TIME2 = 10 * 1000L;

    BookActivity4FragmentBottom1 mBottom1;
    public FrameLayout frameLayout1, frameLayout2;
    private void initBottom12() {
        if (mBottom1 == null) {
            frameLayout1 = g_rootView.findViewById(R.id.frameLayoutBottom1);
            View view1 = View.inflate(getActivity(), R.layout.activity_book4_bottom1, null);
            frameLayout1.addView(view1);
            frameLayout2 = g_rootView.findViewById(R.id.frameLayoutBottom2);
            View view2 = View.inflate(getActivity(), R.layout.activity_book4_bottom2, null);
            frameLayout2.addView(view2);
            mBottom1 = new BookActivity4FragmentBottom1(this);
            mBottom1.init001(view1);
            mBottom1.init002(view2);
        }
    }

    private final static boolean USE_BOTTOM_SHEET = false;
    private final static boolean USE_RECORDING_FRAGMENT_TEST = false; //need open id/fragment_recording

    private final static boolean TYPE_NO_CHOOSE = true;
    private int type = TYPE_USE_SHERPA_KROKO;//TYPE_USE_SHERPA_KROKO; //TYPE_USE_VOSK;
    public final static int TYPE_USE_RTASR = 0;
    public final static int TYPE_USE_VOSK = 1;
    public final static int TYPE_USE_SHERPA = 2;
    public final static int TYPE_USE_SHERPA_KROKO = 3;

    BookActivity4VoskDialog voskDialog = null;
    BookActivity4SherpaOnnxDialog sherpaOnnxDialog = null;
    private final static boolean USE_LISTEN = false; //listen or recording?

    private final static boolean ENABLE_BOTTOM_SHEET = false;
    
    //TODO:check .setCancelable(false)
    //TODO:android:background="#00000000"
    private final static boolean USE_OLD_PEN_SETTING_PANEL = false;

    private final static boolean D = true;
    private final static String TAG = "BookActivity4";

    private boolean isRecording = false; //FIXME:
    private BookReaderItemsAdapter adapter;
    private final SettingsBottomSheet settingsBottomSheet = new SettingsBottomSheet();
    public static class SettingsBottomSheet extends BottomSheetDialogFragment {
        public static final String TAG = "SettingsBottomSheet";
        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            // Inflates settings XML
            getChildFragmentManager()
                    .beginTransaction()
                    .replace(R.id.moreOptionsPreferences, new MainActivity.PreferencesFragment())
                    .commit();
            return inflater.inflate(R.layout.settings_popup, container, false);
        }

        @Override
        public void onHiddenChanged(boolean hidden) {
            super.onHiddenChanged(hidden);
        }
    }

    private WindowPreferencesManager windowPreferencesManager;
    private BottomSheetDialog bottomSheetDialog1;

    public final static boolean SAVING_ASYNC = true;


    public String curPattern;
    private String backText;

    private Uri dirUrl;
    public String dirUrlPath;
    private int initialPageIdx;
    private FastFile _bookDir;// = bookDir_init();
    FastFile bookDir_init() {
        //FIXME:check this.dirUrlPath null
        return FastFile.fromTreeUri(getActivity(), this.dirUrl, this.dirUrlPath);
    }

    private BookIO _bookIO;// = bookIO_init();
    private BookIO bookIO_init() {
        return new BookIO(getActivity().getContentResolver());
    }

    private Book _book;
    private int _pageIdx;// = pageIdx_init();
    private int pageIdx_init() {
        return initialPageIdx;
    }

    private void onCreateAct(View rootView) {
        _bookDir = bookDir_init();
        _bookIO = bookIO_init();
        _pageIdx = pageIdx_init();
        try {
            ((TextView) rootView.findViewById(R.id.newTitle)).setText(_bookDir.getDisplayName());
        } catch (Throwable eee) {
            eee.printStackTrace();
        }
    }
    private void onPageIdxChange(boolean forceReload) {
        int idx = getPageIdx();
        List<FastFile> pages = getBook().getPages();
        int size = pages.size();
        if (idx < 0) {
            idx = this._pageIdx = 0;
        } else if (idx >= size) {
            idx = this._pageIdx = size - 1;
        }
        if (idx >= 0 && idx < size) { //FIXME: null??
            onPageIdx(canvas, idx, new CanvasBoox.OnLoadBitmapListener() {
                @Override
                public BitmapVector onLoadBitmap(int idx) {
                    BookIO bookIO = getBookIO();
                    BookPage page = getBook().getPage(idx);
                    BitmapVector result = bookIO.loadBitmapOrNull(page);
                    if (result != null && result.strVecJson != null && result.strVecJson.length() > 0) {
                        pageBmp = null;
                        if (canvas != null) {
                            canvas.loadVecJson(result.strVecJson);
                        }
                    } else if (result != null) {
                        pageBmp = result.bitmap;
                    }
                    try {
                        String metaTxt = bookIO.loadMetaPng(page.getFile());
                        JSONObject item = new JSONObject(metaTxt);
                        if (item != null) {
                            curPattern = item.optString(BookActivity4Config.USE_SKETCH_CONFIG_PATTERN);
                            backText = curPattern; //FIXME:added
                        }
                    } catch (Throwable eee) {
                        eee.printStackTrace();
                        if (INIT_EMPTY_BACK_TEXT_WHEN_ADD_PAGE) {
                            curPattern = FileMeta.NONE;
                            backText = curPattern;
                        }
                    }
                    setBackText(canvas, backText); //FIXME:added
                    isDirty = false;
                    BitmapVector result_ = new BitmapVector();
                    result_.bitmap = pageBmp;
                    result_.strVecJson = result != null ? result.strVecJson : null;
                    canvas.onVersionChanged();
                    if (false) clearRestorePages();
                    return result_;
                }
            }, forceReload);
            onUpdatePidxPnum();
        }
    }

    private int pageNum = 0;
    private boolean canRedo = false;
    private boolean canUndo = false;
    private int undoCount = 0;
    private int redoCount = 0;
    private long lastWritten = -1L;
    private Bitmap emptyBmp;
    private Bitmap pageBmp;
    private boolean isDirty;
    private static final long SAVE_INTERVAL_MILL = 5000L;

    private FastFile getBookDir() {
        return this._bookDir;
    }

    private BookIO getBookIO() {
        return this._bookIO;
    }

    private synchronized void set_book(Book newbook) {
        this.pageNum = 0;
        this._book = newbook;
        if (newbook != null) {
            List<FastFile> pages = newbook.getPages();
            if (pages != null) {
                this.pageNum = pages.size();
            }
        }
        this.onUpdatePidxPnum();
    }

    private Book getBook() {
        if (this._book != null) {
            return this._book;
        } else {
            Book it = this.getBookIO().loadBook(this.getBookDir(), getActivity());
            this.set_book(it);
            return it;
        }
    }

    private void showMessage(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
    }

    public int getPageIdx() {
        return this._pageIdx;
    }

    private void notifyBitmapUpdate(Bitmap newBmp) {
        this.isDirty = true;
        this.lastWritten = this.getCurrentMills();
        this.pageBmp = newBmp;
        this.lazySave();
    }

    private void notifyUndoStateChanged(boolean canUndo1, boolean canRedo1) {
        this.canUndo = canUndo1;
        this.canRedo = canRedo1;
    }

    private long getCurrentMills() {
        return (new Date()).getTime();
    }

    private synchronized void savePage(final int pageIdx, Bitmap pageBmp, String vecJson) {
        this.getBookIO().saveBitmap(this.getBook().getPage(pageIdx), pageBmp, vecJson, this.getBook(), getActivity(), this._pageIdx);
        new Thread(new Runnable() {
            @Override
            public void run() {
                set_book(getBook().assignNonEmpty(pageIdx));
            }
        }).start();
    }

    private void savePageInMain(int pageIdx, Bitmap pageBmp, String vecJson) {
        this.getBookIO().saveBitmap(this.getBook().getPage(pageIdx), pageBmp, vecJson, this.getBook(), getActivity(), this._pageIdx);
        this.set_book(this.getBook().assignNonEmpty(pageIdx));
    }

    private void lazySave() {
        new Thread(new Runnable() {
            @Override
            public void run() {
//                try {
//                    Thread.sleep(SAVE_INTERVAL_MILL);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                if (isDirty && getCurrentMills() - lastWritten
                                >= SAVE_INTERVAL_MILL) {
                    isDirty = false;
                    Lock bitmapLock = BookActivity4Utils.getBitmapLock();
                    bitmapLock.lock();
                    Bitmap tempBmp = null;
                    try {
                        tempBmp = pageBmp.copy(pageBmp.getConfig(), false);
                    } catch (Throwable eee) {
                        eee.printStackTrace();
                    } finally {
                        bitmapLock.unlock();
                    }
                    savePage(getPageIdx(), tempBmp, getVecJson(canvas));
                }
            }
        }).start();
    }

    private void ensureSave() {
        try {
//            if (this.isDirty == false) {
//                this.isDirty = true;  //FIXME:added
//            }
            if (this.isDirty) {
                this.isDirty = false;
                this.savePageInMain(this.getPageIdx(), this.pageBmp, getVecJson(canvas));
            }
        } catch (Throwable eee) {
            eee.printStackTrace();
            try {
                Toast.makeText(getActivity(), "Save failed", Toast.LENGTH_SHORT).show();
            } catch (Throwable eee2) {
                eee2.printStackTrace();
            }
        }
    }

    @Override
    public void onStop() {
        recordTempDuration();
        if (this.isBackPressed) {
            recordDuration();
            if (type == TYPE_USE_VOSK) {
                if (voskDialog != null) {
                    voskDialog.onClick_stop();
                    voskDialog = null;
                }
            } else if (type == TYPE_USE_SHERPA ||
                    type == TYPE_USE_SHERPA_KROKO) {
                if (sherpaOnnxDialog != null) {
                    sherpaOnnxDialog.onclick_Stop();
                    sherpaOnnxDialog = null;
                }
            }
        }
        if (true) {
            this.ensureSave();
        } else {
            this.savePageInMain(this.getPageIdx(), this.pageBmp, getVecJson(canvas));
        }
        saveBrushPreset();
        if (this.isBackPressed) {
            SDRecordingsDatabase mDatabase = this.adapter.getDB(); //new SDRecordingsDatabase(getActivity(), _bookDir.getFilePath());
            if (mDatabase != null) {
                mDatabase.saveAll();
            }
        }
        super.onStop();
        cancelWaitingProgressDialog();
        runNormalScreen(getActivity());
        if (this.isBackPressed) {
            try {
                if (refreshRunnable2 != null) {
                    refreshRunnable2.stop();
                }
            } catch (Throwable eee) {
                eee.printStackTrace();
            }
            try {
                if (refreshRunnable3 != null) {
                    refreshRunnable3.stop();
                }
            } catch (Throwable eee) {
                eee.printStackTrace();
            }
            try {
                newFixedThreadPool.shutdown();
                try {
                    if (!newFixedThreadPool.awaitTermination(1, TimeUnit.SECONDS)) {
                        newFixedThreadPool.shutdownNow();
                    }
                } catch (InterruptedException e) {
                    newFixedThreadPool.shutdownNow();
                }
            } catch (Throwable eee) {
                eee.printStackTrace();
            }
        }
    }

    boolean isPause = false;
    @Override
    public void onPause() {
        super.onPause();
        //runNormalScreen(getActivity());
        //setNavBarTintColor(getActivity());
        isPause = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        runFullScreen(getActivity());
        isPause = false;
    }

    //FIXME: remove RequiresApi
    //@RequiresApi(26)
    private void share() {
        this.ensureSave();
        if (this.pageBmp != null) {
            FileOutputStream it = null;
            File path = null;
            try {
                path = File.createTempFile("share", ".png", getActivity().getCacheDir());
                it = new FileOutputStream(path);
                this.pageBmp.compress(Bitmap.CompressFormat.PNG, 100, it);
                it.flush();
            } catch (Throwable e) {
                e.printStackTrace();
                path = null;
            } finally {
                try {
                    if (it != null) {
                        it.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (path != null) {
                Uri u = FileProvider.getUriForFile(getActivity(),
                        getActivity().getApplicationContext().getPackageName() + ".provider",
                        path);
                this.shareImageUri(u);
            }
        }
    }

    private void shareImageUri(Uri uri) {
        Intent intent = new Intent("android.intent.action.SEND");
        intent.putExtra("android.intent.extra.STREAM", (Parcelable)uri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setType("image/png");
//        intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);//FIXME:added, share
        this.startActivity(intent);
    }

    private void addNewPageAndGo(boolean needAsk) {
        if (needAsk) {
            PaintSelectDialog dialog = new PaintSelectDialog(getActivity());
            dialog.show();
        } else {
            addNewPageAndGo_old();
        }
    }

    private void addNewPageAndGo_old() {
        this.ensureSave();
        if (this.emptyBmp == null) {
            if (this.pageBmp != null) {
                this.emptyBmp = Bitmap.createBitmap(this.pageBmp.getWidth(),
                        this.pageBmp.getHeight(), Bitmap.Config.ARGB_8888);
                if (BookIO.USE_META_TXT) {
                    this.emptyBmp.eraseColor(0x00000000);
                } else {
                    this.emptyBmp.eraseColor(0xFFFFFFFF);
                }
                if (!BookIO.USE_META_TXT) {
                    CanvasBoox.initBackText(this.backText, this.emptyBmp, 1);
                }
            }
        } else {
            if (BookIO.USE_META_TXT) {
                this.emptyBmp.eraseColor(0x00000000);
            } else {
                this.emptyBmp.eraseColor(0xFFFFFFFF);
            }
            if (!BookIO.USE_META_TXT) {
                CanvasBoox.initBackText(this.backText, this.emptyBmp, 1);
            }
        }
        this.set_book(this.getBook().addPage());
        if (false) {
            if (this.pageBmp != null) {
                this.savePageInMain(this.pageNum - 1, this.pageBmp, getVecJson(canvas));
            }
        } else {
            if (this.emptyBmp != null) {
                this.pageBmp = this.emptyBmp; //FIXME:???
            }
            //FIXME:clear versions here??? TODO:

            if (canvas != null) {
                // Clear path list/history
                canvas.paths.clear();
                canvas.versions.clear();
                canvas.oldVersionsSize = canvas.versions.size();
                canvas.version_index = -1;
                canvas.onVersionChanged();
                clearRestorePages();
            }

            if (false && this.pageBmp != null) {
                this.savePageInMain(this.pageNum - 1, this.pageBmp, getVecJson(canvas));
                if (BookIO.USE_META_TXT) {
                    getBookIO().saveMeta(backText, this.dirUrlPath, String.format("%04d", this.pageNum - 1) + ".meta");
                }
            }
        }
        this._pageIdx = this.pageNum - 1;
        onPageIdxChange(true);
    }

    private void removeCurrentPageAndGo() {
        if (this.pageNum <= 1) {
            BookPage page = this.getBook().getPage(this._pageIdx);
            addNewPageAndGo(false);
            this.getBook().removePage(page.getFile(), _bookIO);
        } else if (this._pageIdx <= 0) {
            this._pageIdx = 0; //FIXME:???
            this.ensureSave();
            if (this.pageBmp != null) {
                this.emptyBmp = Bitmap.createBitmap(this.pageBmp.getWidth(),
                        this.pageBmp.getHeight(), Bitmap.Config.ARGB_8888);
                if (BookIO.USE_META_TXT) {
                    this.emptyBmp.eraseColor(0x00000000);
                } else {
                    this.emptyBmp.eraseColor(0xFFFFFFFF);
                }
                if (!BookIO.USE_META_TXT) {
                    CanvasBoox.initBackText(this.backText, this.emptyBmp, 1);
                }
            }
            if (this.emptyBmp != null) {
                this.pageBmp = this.emptyBmp; //FIXME:???
            }
            if (false && this.pageBmp != null) {
                this.savePageInMain(this.pageNum - 1, this.pageBmp, getVecJson(canvas));
            }
            onPageIdxChange(false);
        } else {
            BookPage page = this.getBook().getPage(this._pageIdx);
            gotoPrevPage();
            this.getBook().removePage(page.getFile(), _bookIO);
        }

        //--------------
        this._book = null;
        set_book(getBook()); //FIXME:???reload
        //--------------

        onPageIdxChange(false);
    }

    private void gotoFirstPage() {
        this.ensureSave();
        this._pageIdx = 0;
        onPageIdxChange(false);
    }

    private void gotoLastPage() {
        this.ensureSave();
        this._pageIdx = this.pageNum - 1;
        onPageIdxChange(false);
    }

    private void gotoPrevPage() {
        this.ensureSave();
        int var1 = this.getPageIdx();
        if (var1 >= 1) {
            this._pageIdx = var1 - 1;
            onPageIdxChange(false);
        }
    }

    private void gotoNextPage() {
        this.ensureSave();
        int var1 = this.getPageIdx();
        this._pageIdx = var1 + 1;
        onPageIdxChange(false);
    }

    private void gotoGridPage() {
        Intent intent = new Intent(getActivity(), PageGridActivity.class);
        intent.setData(this.dirUrl);
        intent.putExtra(PageGridActivity.EXTRA_DIR_URL_PATH, this.dirUrlPath);
        this.startActivity(intent);
    }

//    @Override
//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//        if (intent != null) {
//            this.handlePageIdxArg(intent);
//        }
//    }
    //--------------------------

    //--------------------------
    private final static int iconsSubmenu1[] = {
            R.id.left_toolkit_item1, //1 pen //PEN_TYPE_1
            R.id.left_toolkit_item2,
            R.id.left_toolkit_item3, //2 highlight //PEN_TYPE_3
            R.id.left_toolkit_item4, //3 pencil //hidden
            R.id.left_toolkit_item5,
            R.id.left_toolkit_item6, //6 shape //hidden //shape pen
    };
    public int getActiveIconIdSubmenu1(int id, boolean isActive) {
        if (id == R.id.left_toolkit_item1) {
            return isActive ? R.drawable.ic_my_pen_001_w : R.drawable.ic_my_pen_001;
        } else if (id == R.id.left_toolkit_item2) {
            return isActive ? R.drawable.ic_my_pen_002_w: R.drawable.ic_my_pen_002;
        } else if (id == R.id.left_toolkit_item3) {
            return isActive ? R.drawable.ic_my_pen_003_w: R.drawable.ic_my_pen_003;
        } else if (id == R.id.left_toolkit_item4) {
            return isActive ? R.drawable.ic_my_pen_004_w: R.drawable.ic_my_pen_004;
        } else if (id == R.id.left_toolkit_item5) {
            return isActive ? R.drawable.ic_my_pen_005_w: R.drawable.ic_my_pen_005;
        } else if (id == R.id.left_toolkit_item6) {
            return isActive ? R.drawable.ic_my_pen_006_w: R.drawable.ic_my_pen_006;
        }
        return 0;
    }
    private int currentTabIdSubmenu1 = iconsSubmenu1[0];
    public void onClickSubmenu1(View rootView, int id, boolean isClick, boolean noShowBottom) {
        if (canvas != null) {
            canvas.old_gScaleBegin = false;
        }
        boolean isShowBottom = false;
        if (!NO_PEN_BOTTOM_POPUP) {
            if (this.currentTabIdSubmenu1 == id && !noShowBottom) {
                isShowBottom = true;
            }
        }
        this.currentTabIdSubmenu1 = id;
        View view = rootView.findViewById(id);
        for (int i = 0; i < iconsSubmenu1.length; ++i) {
            View viewIcon = rootView.findViewById(iconsSubmenu1[i]);
            if (viewIcon != null) {
                ((CardView) viewIcon).setCardBackgroundColor(0x00585858);
                //((CardView) viewIcon).setCardElevation(0.0f);
                if (viewIcon.findViewWithTag("binding_1") != null) {
                    ((AppCompatImageView) viewIcon.findViewWithTag("binding_1")).setBackgroundResource(
                            getActiveIconIdSubmenu1(iconsSubmenu1[i], false));
                }
            }
        }
        if (view != null) {
            ((CardView) view).setCardBackgroundColor(0xFF585858);
            //((CardView) view).setCardElevation(5.0f);

            if (view.findViewWithTag("binding_1") != null) {
                ((AppCompatImageView) view.findViewWithTag("binding_1")).setBackgroundResource(
                        getActiveIconIdSubmenu1(id, true));
            }
        }
        if (isClick) {
            canvas.setTool(DrawCanvas.TOOLS.paint);
            if (id == R.id.left_toolkit_item1) {
                //findViewById(R.id.buttonPen2).performClick();
                canvas.setPenType(DrawAppearance.PEN_TYPE_1);
            } else if (id == R.id.left_toolkit_item2) {
                //findViewById(R.id.buttonEraser2).performClick();
                canvas.setPenType(DrawAppearance.PEN_TYPE_2);
            } else if (id == R.id.left_toolkit_item3) {
                canvas.setPenType(DrawAppearance.PEN_TYPE_3); //highlight
            } else if (id == R.id.left_toolkit_item4) {
                canvas.setPenType(DrawAppearance.PEN_TYPE_4);
            } else if (id == R.id.left_toolkit_item5) {
                canvas.setPenType(DrawAppearance.PEN_TYPE_5);
            } else if (id == R.id.left_toolkit_item6) {
                canvas.setPenType(DrawAppearance.PEN_TYPE_6);
            }
            updateAppear(id);
//            if (bottomSheetDialog1 != null) {
//                bottomSheetDialog1.show();
//            }

            rootView.findViewById(R.id.bottomDialog2).setVisibility(View.GONE);
            if (isShowBottom) {
                //!USE_OLD_PEN_SETTING_PANEL) { //
                if (false) { //id == R.id.left_toolkit_item6) {
                    AlertDialog dialog = new MaterialAlertDialogBuilder(getActivity(), BookActivity4Utils.getCenteredTitleThemeOverlay())
                            //.setTitle(title)
                            .setView(R.layout.activity_book4_brush)
                            .setCancelable(true)
                            .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

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
                            BookActivity4Utils.runFullScreen(getActivity());

                            View llEdit = ((AlertDialog) dialogInterface).findViewById(R.id.llEdit);
                            llEdit.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    showNewBrushDialog();
                                }
                            });
                        }
                    });
                    dialog.show();
                } else {
                    if (rootView.findViewById(R.id.bottomDialog1).getVisibility() == View.VISIBLE) {
                        rootView.findViewById(R.id.bottomDialog1).setVisibility(View.GONE);
                    } else {
                        initBottom12();
                        rootView.findViewById(R.id.bottomDialog1).setVisibility(View.VISIBLE);
                    }
                }
            } else {
                rootView.findViewById(R.id.bottomDialog1).setVisibility(View.GONE);
            }
            //settingsBottomSheet.show(getSupportFragmentManager(), MainActivity.SettingsBottomSheet.TAG);
        }
    }
    private int left_toolkit_item1_color = Color.BLACK;
    private int left_toolkit_item2_color = Color.BLACK;
    private int left_toolkit_item3_color = Color.BLACK;
    private int left_toolkit_item4_color = Color.BLACK;
    private int left_toolkit_item5_color = Color.BLACK;
    private int left_toolkit_item6_color = Color.BLACK;
    private int left_toolkit_item1_size = 1;
    private int left_toolkit_item2_size = 1;
    private int left_toolkit_item3_size = 1;
    private int left_toolkit_item4_size = 1;
    private int left_toolkit_item5_size = 1;
    private int left_toolkit_item6_size = 1;
    public void onLongClickSubmenu1(int id, boolean isClick) {
        BookActivity4BrushEditDialog dialog = new BookActivity4BrushEditDialog(getActivity(), id);
        if (id == R.id.left_toolkit_item1) {
            dialog.outputColor = left_toolkit_item1_color;
            dialog.outputBrushSize = left_toolkit_item1_size;
        } else if (id == R.id.left_toolkit_item2) {
            dialog.outputColor = left_toolkit_item2_color;
            dialog.outputBrushSize = left_toolkit_item2_size;
        } else if (id == R.id.left_toolkit_item3) {
            dialog.outputColor = left_toolkit_item3_color;
            dialog.outputBrushSize = left_toolkit_item3_size; //highlight
        } else if (id == R.id.left_toolkit_item4) {
            dialog.outputColor = left_toolkit_item4_color;
            dialog.outputBrushSize = left_toolkit_item4_size;
        } else if (id == R.id.left_toolkit_item5) {
            dialog.outputColor = left_toolkit_item5_color;
            dialog.outputBrushSize = left_toolkit_item5_size;
        } else if (id == R.id.left_toolkit_item6) {
            dialog.outputColor = left_toolkit_item6_color;
            dialog.outputBrushSize = left_toolkit_item6_size;
        }
        AlertDialog aDialog = dialog.create();
        aDialog.show();
    }
    public void onLongClickSubmenu1_after(BookActivity4BrushEditDialog dialog, int id) {
        if (dialog.outputIsSave) {
            if (id == R.id.left_toolkit_item1) {
                left_toolkit_item1_color = dialog.outputColor;
                left_toolkit_item1_size = dialog.outputBrushSize;
            } else if (id == R.id.left_toolkit_item2) {
                left_toolkit_item2_color = dialog.outputColor;
                left_toolkit_item2_size = dialog.outputBrushSize;
            } else if (id == R.id.left_toolkit_item3) {
                left_toolkit_item3_color = dialog.outputColor;
                left_toolkit_item3_size = dialog.outputBrushSize;
            } else if (id == R.id.left_toolkit_item4) {
                left_toolkit_item4_color = dialog.outputColor;
                left_toolkit_item4_size = dialog.outputBrushSize;
            } else if (id == R.id.left_toolkit_item5) {
                left_toolkit_item5_color = dialog.outputColor;
                left_toolkit_item5_size = dialog.outputBrushSize;
            } else if (id == R.id.left_toolkit_item6) {
                left_toolkit_item6_color = dialog.outputColor;
                left_toolkit_item6_size = dialog.outputBrushSize;
            }
            updateAppear(id);
            saveBrushPreset();
        }
    }
    //--------------------------
    private final static int iconsSubmenu2[] = {
            R.id.left_toolkit_item21,
            R.id.left_toolkit_item22,
    };
    public int getActiveIconIdSubmenu2(int id, boolean isActive) {
        if (id == R.id.left_toolkit_item21) {
            return isActive ? R.drawable.ic_my_typing_031_w : R.drawable.ic_my_typing_031;
        } else if (id == R.id.left_toolkit_item22) {
            return isActive ? R.drawable.ic_my_typing_032_w: R.drawable.ic_my_typing_032;
        }
        return 0;
    }
    private int currentTabIdSubmenu2 = iconsSubmenu2[0];
    public void onClickSubmenu2(View rootView, int id, boolean isClick) {
        if (canvas != null) {
            canvas.old_gScaleBegin = false;
        }
        boolean isShowBottom = false;
        if (this.currentTabIdSubmenu2 == id) {
            isShowBottom = true;
        }
        this.currentTabIdSubmenu2 = id;
        View view = rootView.findViewById(id);
        for (int i = 0; i < iconsSubmenu2.length; ++i) {
            View viewIcon = rootView.findViewById(iconsSubmenu2[i]);
            if (viewIcon != null) {
                ((CardView) viewIcon).setCardBackgroundColor(0x00585858);
                //((CardView) viewIcon).setCardElevation(0.0f);
                if (viewIcon.findViewWithTag("binding_1") != null) {
                    ((AppCompatImageView) viewIcon.findViewWithTag("binding_1")).setBackgroundResource(
                            getActiveIconIdSubmenu2(iconsSubmenu2[i], false));
                }
            }
        }
        if (view != null) {
            ((CardView) view).setCardBackgroundColor(0xFF585858);
            //((CardView) view).setCardElevation(5.0f);

            if (view.findViewWithTag("binding_1") != null) {
                ((AppCompatImageView) view.findViewWithTag("binding_1")).setBackgroundResource(
                        getActiveIconIdSubmenu2(id, true));
            }
        }
        if (isClick) {
            if (id == R.id.left_toolkit_item21) {
                initBottom12();
                rootView.findViewById(R.id.llLeftPanel1).setVisibility(View.VISIBLE);
                rootView.findViewById(R.id.llLeftPanel2).setVisibility(View.GONE);
            } else if (id == R.id.left_toolkit_item22) {
                initBottom12();
                rootView.findViewById(R.id.llLeftPanel1).setVisibility(View.GONE);
                rootView.findViewById(R.id.llLeftPanel2).setVisibility(View.VISIBLE);
            }
            rootView.findViewById(R.id.bottomDialog1).setVisibility(View.GONE);
            if (isShowBottom) {
                if (rootView.findViewById(R.id.bottomDialog2).getVisibility() == View.VISIBLE) {
                    rootView.findViewById(R.id.bottomDialog2).setVisibility(View.GONE);
                } else {
                    initBottom12();
                    rootView.findViewById(R.id.bottomDialog2).setVisibility(View.VISIBLE);
                }
            } else {
                rootView.findViewById(R.id.bottomDialog2).setVisibility(View.GONE);
            }
        }
    }
    //--------------------------
    private final static int iconsSubmenu4[] = {
            R.id.left_toolkit_item41,
            R.id.left_toolkit_item42,
            R.id.left_toolkit_item43,
            R.id.left_toolkit_item44,
            R.id.left_toolkit_item45,
    };
    public int getActiveIconIdSubmenu4(int id, boolean isActive) {
        if (id == R.id.left_toolkit_item41) {
            return isActive ? R.drawable.ic_my_select_021_w : R.drawable.ic_my_select_021;
        } else if (id == R.id.left_toolkit_item42) {
            return isActive ? R.drawable.ic_my_select_022_w: R.drawable.ic_my_select_022;
        } else if (id == R.id.left_toolkit_item43) {
            return isActive ? R.drawable.ic_my_select_023_w: R.drawable.ic_my_select_023;
        } else if (id == R.id.left_toolkit_item44) {
            return isActive ? R.drawable.ic_my_select_024_w: R.drawable.ic_my_select_024;
        } else if (id == R.id.left_toolkit_item45) {
            return isActive ? R.drawable.ic_my_select_025_w: R.drawable.ic_my_select_025;
        }
        return 0;
    }
    private int currentTabIdSubmenu4 = iconsSubmenu4[0];
    public void onClickSubmenu4(View rootView, int id, boolean isClick) {
        if (canvas != null) {
            canvas.old_gScaleBegin = false;
        }
        this.currentTabIdSubmenu4 = id;
        View view = rootView.findViewById(id);
        for (int i = 0; i < iconsSubmenu4.length; ++i) {
            View viewIcon = rootView.findViewById(iconsSubmenu4[i]);
            if (viewIcon != null) {
                ((CardView) viewIcon).setCardBackgroundColor(0x00585858);
                //((CardView) viewIcon).setCardElevation(0.0f);
                if (viewIcon.findViewWithTag("binding_1") != null) {
                    ((AppCompatImageView) viewIcon.findViewWithTag("binding_1")).setBackgroundResource(
                            getActiveIconIdSubmenu4(iconsSubmenu4[i], false));
                }
            }
        }
        if (view != null) {
            ((CardView) view).setCardBackgroundColor(0xFF585858);
            //((CardView) view).setCardElevation(5.0f);

            if (view.findViewWithTag("binding_1") != null) {
                ((AppCompatImageView) view.findViewWithTag("binding_1")).setBackgroundResource(
                        getActiveIconIdSubmenu4(id, true));
            }
        }
        if (isClick) {
            if (id == R.id.left_toolkit_item41) {
                canvas.setTool(DrawCanvas.TOOLS.select);
                canvas.setEraserMode(false);
                canvas.setScaleMode(false);
            } else if (id == R.id.left_toolkit_item42) {
                canvas.setTool(DrawCanvas.TOOLS.select);
                canvas.setEraserMode(false);
                canvas.setScaleMode(false);
            } else if (id == R.id.left_toolkit_item43) {
                canvas.setTool(DrawCanvas.TOOLS.select);
                canvas.setEraserMode(false);
                canvas.setScaleMode(true);
            } else if (id == R.id.left_toolkit_item44) {
                canvas.setTool(DrawCanvas.TOOLS.pan);
            } else if (id == R.id.left_toolkit_item45) {
                if (canvas != null) {
                    canvas.getSelectionTool().exitSelect();
                    canvas.invalidate();
                }

                if (false) {
                    canvas.setTool(DrawCanvas.TOOLS.select);
                    canvas.setEraserMode(true);
                    canvas.setScaleMode(false);
                } else {
                    if (EraserTool.USE_SIMPLE_IMPL) {
                        //use simple impl of EraserTool
                    }
                    canvas.setTool(DrawCanvas.TOOLS.eraser);
                    canvas.setEraserMode(true);
                }
            }
        }
    }

    //--------------------------
    LinearLayout llRichTextTool;
    LinearLayout llRichTextTool2;
    private final static int iconsTopBar[] = {
            R.id.top_toolkit_item1,
            R.id.top_toolkit_item2,
            R.id.top_toolkit_item3,
            R.id.top_toolkit_item4,
    };
    public int getActiveIconIdTopBar(int id, boolean isActive) {
        if (id == R.id.top_toolkit_item1) {
            return isActive ? R.drawable.ic_my_bar_draw_001_w : R.drawable.ic_my_bar_draw_001;
        } else if (id == R.id.top_toolkit_item2) {
            return isActive ? R.drawable.ic_my_bar_typing_002_w : R.drawable.ic_my_bar_typing_002;
        } else if (id == R.id.top_toolkit_item3) {
            return isActive ? R.drawable.ic_my_bar_ai_003_w : R.drawable.ic_my_bar_ai_003;
        } else if (id == R.id.top_toolkit_item4) {
            return isActive ? R.drawable.ic_my_bar_select_004_w: R.drawable.ic_my_bar_select_004;
        }
        return 0;
    }
    private int currentTabIdTopBar = iconsTopBar[0];
    public void onClickTopBar(View rootView, int id, boolean isClick) {
        if (canvas != null) {
            canvas.old_gScaleBegin = false;
        }
        this.currentTabIdTopBar = id;
        View view = rootView.findViewById(id);
        rootView.findViewById(R.id.llTab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //skip
            }
        });
        rootView.findViewById(R.id.llTopBar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //skip
            }
        });
        for (int i = 0; i < iconsTopBar.length; ++i) {
            View viewIcon = rootView.findViewById(iconsTopBar[i]);
            if (viewIcon != null) {
                ((CardView) viewIcon).setCardBackgroundColor(0x00000000);
                //((CardView) viewIcon).setCardElevation(0.0f);
                if (viewIcon.findViewWithTag("binding_1") != null) {
                    ((AppCompatImageView) viewIcon.findViewWithTag("binding_1")).setBackgroundResource(getActiveIconIdTopBar(iconsTopBar[i], false));
                }
                if (viewIcon.findViewWithTag("binding_5") != null) {
                    ((TextView) viewIcon.findViewWithTag("binding_5")).setTextColor(0xFF000000);
                }
            }
        }
        if (view != null) {
            ((CardView) view).setCardBackgroundColor(0xFF000000);
            //((CardView) view).setCardElevation(5.0f);

            if (view.findViewWithTag("binding_1") != null) {
                ((AppCompatImageView) view.findViewWithTag("binding_1")).setBackgroundResource(getActiveIconIdTopBar(id, true));
            }
            if (view.findViewWithTag("binding_5") != null) {
                ((TextView) view.findViewWithTag("binding_5")).setTextColor(0xFFFFFFFF);
            }
        }
        if (id == R.id.top_toolkit_item1) {
            //drawing
            dtViewBottom.setVisibility(View.GONE);
            if (getDtView(false) != null) {
                getDtView(false).setVisibility(View.GONE);
            }
            //typing
            if (canvas != null) {
                canvas.getSelectionTool().exitSelect();
                canvas.invalidate();
            }

            llASR.setVisibility(View.GONE);
            rl_ai.setVisibility(View.GONE);
            btnPanel2.setImageTintList(ColorStateList.valueOf(Color.BLACK));
            rootView.findViewById(R.id.left_toolkit1).setVisibility(View.VISIBLE);
            rootView.findViewById(R.id.left_toolkit2).setVisibility(View.GONE);
            rootView.findViewById(R.id.left_toolkit4).setVisibility(View.GONE);
            canvas.setTool(DrawCanvas.TOOLS.paint);
            canvas.setIsTyping(false);
        } else if (id == R.id.top_toolkit_item2) {
            //typing
            if (canvas != null) {
                canvas.getSelectionTool().exitSelect();
                canvas.invalidate();
            }

            dtViewBottom.setVisibility(View.VISIBLE);
            if (getDtView(false) != null) {
                getDtView(false).setVisibility(View.GONE);
            }

            setBoldItalicsStyle(); //FIXME: ??? put here or put init()???

            llASR.setVisibility(View.GONE);
            rl_ai.setVisibility(View.GONE);
            btnPanel2.setImageTintList(ColorStateList.valueOf(Color.BLACK));
            rootView.findViewById(R.id.left_toolkit1).setVisibility(View.GONE);
            rootView.findViewById(R.id.left_toolkit2).setVisibility(View.VISIBLE);
            rootView.findViewById(R.id.left_toolkit4).setVisibility(View.GONE);
            canvas.setIsTyping(true);
        } else if (id == R.id.top_toolkit_item3) {
            //AI note talking
            dtViewBottom.setVisibility(View.GONE);
            if (getDtView(false) != null) {
                getDtView(false).setVisibility(View.GONE);
            }
            llASR.setVisibility(View.GONE);
            rl_ai.setVisibility(View.VISIBLE);
            llPanel.setVisibility(View.VISIBLE); //added
            btnPanel2.setImageTintList(ColorStateList.valueOf(Color.WHITE));
            rootView.findViewById(R.id.left_toolkit1).setVisibility(View.GONE);
            rootView.findViewById(R.id.left_toolkit2).setVisibility(View.GONE);
            rootView.findViewById(R.id.left_toolkit4).setVisibility(View.GONE);
            updateRecordButtonStatus();
            canvas.setIsTyping(false);
        } else if (id == R.id.top_toolkit_item4) {
            //Selection
            dtViewBottom.setVisibility(View.GONE);
            if (getDtView(false) != null) {
                getDtView(false).setVisibility(View.GONE);
            }
            //default is select, not erase
            onClickSubmenu4(rootView, R.id.left_toolkit_item42, true);
            llASR.setVisibility(View.GONE);
            rl_ai.setVisibility(View.GONE);
            btnPanel2.setImageTintList(ColorStateList.valueOf(Color.BLACK));
            rootView.findViewById(R.id.left_toolkit1).setVisibility(View.GONE);
            rootView.findViewById(R.id.left_toolkit2).setVisibility(View.GONE);
            rootView.findViewById(R.id.left_toolkit4).setVisibility(View.VISIBLE);
            if (currentTabIdSubmenu4 == R.id.left_toolkit_item44) {
                canvas.setTool(DrawCanvas.TOOLS.pan);
            } else {
                canvas.setTool(DrawCanvas.TOOLS.select);
            }
            canvas.setIsTyping(false);
        }
        rootView.findViewById(R.id.bottomDialog1).setVisibility(View.GONE);
        rootView.findViewById(R.id.bottomDialog2).setVisibility(View.GONE);
//        if (dtView != null) {
//            dtView.hideSoftInput();
//        }
    }
    //--------------------------

    private TextView textViewPageInfo;
    DrawCanvas canvas;
    View dtViewBottom;
    DrawTextView dtView_;
    public DrawTextView getDtView(boolean autoCreate) {
        if (g_rootView != null) {
            if (autoCreate && dtView_ == null) {
                dtView_ = new DrawTextView(getActivity());
                FrameLayout frameLayoutDTView = g_rootView.findViewById(R.id.frameLayoutDTView);
                frameLayoutDTView.addView(dtView_);
                //dtView_.hideSoftInput();
            }
            return dtView_;
        } else {
            return null;
        }
    }
    LinearLayout llASR;
    RelativeLayout rl_ai;
    LinearLayout llPanel;
    AppCompatImageView btnPanel2;
    RelativeLayout rlAIIcon;

    View g_rootView;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        //mBottom1 = new BookActivity4FragmentBottom1(this);
        long t0 = System.currentTimeMillis();
        View rootView = inflater.inflate(R.layout.activity_book4, container, false);
        g_rootView = rootView;
        long t1 = System.currentTimeMillis();
        Log.e(TAG, "oncreateview, t1== " + (t1 - t0));
        init0001(rootView);
        long t2 = System.currentTimeMillis();
        Log.e(TAG, "oncreateview, t2== " + (t2 - t1));
        init002(rootView);
        init000(rootView);
        long t3 = System.currentTimeMillis();
        Log.e(TAG, "oncreateview, t3== " + (t3 - t2));
        init001(rootView);
        long t4 = System.currentTimeMillis();
        Log.e(TAG, "oncreateview, t4== " + (t4 - t3));
        long t5 = System.currentTimeMillis();
        Log.e(TAG, "oncreateview, t5== " + (t5 - t4));
        init003(rootView);
        long t6 = System.currentTimeMillis();
        Log.e(TAG, "oncreateview, t6== " + (t6 - t5));
        onClickTopBar(rootView, iconsTopBar[0], false); //init
        runFullScreen(getActivity());
        long t7 = System.currentTimeMillis();
        Log.e(TAG, "oncreateview, t7== " + (t7 - t6));

        if (TIMER_AUTOSAVE) {
            startHandlerTask2();

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String savingTest = preferences.getString(BookActivity4Config.CONFIG_SAVING_TEST, null);
            if (savingTest != null && savingTest.equals("1")) {
                startHandlerTask3();
            }
        }
        return rootView;
    }

    private void init0001(View rootView) {
        setColor(canvas, 0xFF000000);  //reset pen color
        setSize(canvas, 1);  //reset pen size

        newFixedThreadPool = Executors.newFixedThreadPool(6);
        Bundle intent = this.getArguments();
        boolean isInitBackText = false;
        if (intent != null) {
            String dataStr = intent.getString(BookActivity4Utils.EXTRA_DATA);
            if (dataStr != null) {
                this.dirUrl = Uri.parse(dataStr);
            } else {
                this.dirUrl = null;
            }
            this.dirUrlPath = intent.getString(BookActivity4Utils.EXTRA_DIRURLPATH);
            if (false) {
                this.backText = intent.getString(BookActivity4Utils.EXTRA_BACKTEXT);
            } else {
                //TODO: read config
            }
            this.handlePageIdxArg(intent);
            isInitBackText = true;
        }


        setPenColor(0xFF000000);

//        if (this.dirUrlPath != null) {
//            if (false) {
//                ((TextView) rootView.findViewById(R.id.newTitle)).setText(getBookNameNG(this.dirUrlPath));
//            }
//        }
        checkSketchMetaFile();
        onCreateAct(rootView);
        loadBrushPreset();

        if (BookIO.USE_META_TXT) {
            if (isInitBackText && backText != null) {
                //run here if create
                getBookIO().saveMeta(backText, this.dirUrlPath, "0000.meta");
            }
        }
    }
    private void checkSketchMetaFile() {
        //this.dirUrl==file:///storage/emulated/0/txkjnote2/SKETCH_fa5d355e-ba40-40ff-b751-a39b1170661d
        //this.dirUrlPath==/storage/emulated/0/txkjnote2/SKETCH_fa5d355e-ba40-40ff-b751-a39b1170661d
        if (this.dirUrlPath != null) {
            boolean checkResult = false;
            try {
                String file_ = this.dirUrlPath;
                File file_2 = new File(file_, BookActivity4Config.USE_SKETCH_CONFIG);
                if (file_2.exists() && file_2.canRead()) {
                    String metaTxt = FastFile.loadMetaText(file_2);
                    JSONObject item = new JSONObject(metaTxt);
                    String dispMetaName = item.optString(BookActivity4Config.USE_SKETCH_CONFIG_DISPNAME);
                    checkResult = true;
                }
            } catch (Throwable eee) {
                eee.printStackTrace();
                checkResult = false;
            }
            if (checkResult == false) {
                //recover data
                try {
                    if (BookActivity4Config.USE_RECORD_META_TO_NOTES_DB) {
                        try {
                            String dirPath = new File(Environment.getExternalStorageDirectory(), NoteFragment4.APPNAME_NEW).toString();
                            SDNotesDatabase mDatabase = new SDNotesDatabase(getActivity(), dirPath);
                            List<NoteItem> itemNoteFound = new ArrayList<>();
                            List<NoteItem> items = mDatabase.getAllItems();
                            String filePath = new File(this.dirUrlPath, BookActivity4Config.USE_SKETCH_CONFIG).getAbsolutePath();
                            String fullFilePath = filePath;
                            if (filePath.startsWith(dirPath + "/") && filePath.endsWith("/" + BookActivity4Config.USE_SKETCH_CONFIG)) {
                                filePath = filePath.substring((dirPath + "/").length());
                                filePath = filePath.substring(0, filePath.length() - ("/" + BookActivity4Config.USE_SKETCH_CONFIG).length());
                            }
                            for (NoteItem itemNote : items) {
                                if (itemNote != null &&
                                        filePath != null &&
                                        itemNote.getNoteFilePath() != null &&
                                        itemNote.getNoteFilePath().equals(filePath)) {
                                    itemNoteFound.add(itemNote);
                                }
                            }
                            String noteMeta = null;
                            if (itemNoteFound != null) {
                                for (NoteItem item2 : itemNoteFound) {
                                    noteMeta = item2.getNoteMeta();
                                    if (noteMeta != null) {
                                        break;
                                    }
                                }
                            }
                            mDatabase.close();

                            //!!!!begin to recover data!!!
                            if (noteMeta != null && noteMeta.length() > 0) {
                                OutputStream it = null;
                                try {
                                    it = new FileOutputStream(fullFilePath);
                                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(it, StandardCharsets.UTF_8);
                                    BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
                                    bufferedWriter.write(noteMeta);
                                    bufferedWriter.flush();
                                } catch (Throwable e) {
                                    e.printStackTrace();
                                } finally {
                                    try {
                                        if (it != null) {
                                            it.close();
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        } catch (Throwable eee) {
                            eee.printStackTrace();
                        }
                    }
                } catch (Throwable ee) {
                    ee.printStackTrace();
                }
            }
        }
    }

    private void init000(View rootView) {
        if (false) { //for debugging
            rootView.findViewById(R.id.bookTab).setVisibility(View.VISIBLE);
        }
        rootView.findViewById(R.id.btnTitleBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rootView.findViewById(R.id.buttonBack).performClick();
            }
        });
        rootView.findViewById(R.id.btnMore).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(rootView, view);
            }
        });
        rootView.findViewById(R.id.btnTitleUndo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rootView.findViewById(R.id.buttonUndo).performClick();
            }
        });
        rootView.findViewById(R.id.btnTitleRedo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rootView.findViewById(R.id.buttonRedo).performClick();
            }
        });
        for (int id : iconsSubmenu1) {
            rootView.findViewById(id).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickSubmenu1(rootView, id, true, false);
                }
            });
            rootView.findViewById(id).setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    onLongClickSubmenu1(id, true);
                    onClickSubmenu1(rootView, id, true, true);
                    return true;
                    //return false;
                }
            });
        }
        if (ENABLE_SHAPE_PEN) {
            g_rootView.findViewById(R.id.left_toolkit_item6).setVisibility(View.VISIBLE);
        } else {
            g_rootView.findViewById(R.id.left_toolkit_item6).setVisibility(View.GONE);
        }
        onClickSubmenu1(rootView, iconsSubmenu1[0], true, true);
        for (int id : iconsSubmenu2) {
            rootView.findViewById(id).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickSubmenu2(rootView, id, true);
                }
            });
        }
        for (int id : iconsSubmenu4) {
            rootView.findViewById(id).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickSubmenu4(rootView, id, true);
                }
            });
        }
        //FIXME:selection tool default item 2
        onClickSubmenu4(rootView, R.id.left_toolkit_item42, false);
        for (int id : iconsTopBar) {
            rootView.findViewById(id).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickTopBar(rootView, id, true);
                }
            });
        }
        //mBottom1.init001(rootView);
        rootView.findViewById(R.id.btnPanel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (false) {
                    AppCompatImageView btnPanel = (AppCompatImageView) rootView.findViewById(R.id.btnPanel);
                    AnimationDrawable anim = (AnimationDrawable) btnPanel.getDrawable();
                    anim.start();
                }

                LinearLayout llPanel = (LinearLayout) rootView.findViewById(R.id.llPanel);
                if (llPanel != null) {
                    if (llPanel.getVisibility() == View.VISIBLE) {
                        llPanel.setVisibility(View.GONE);
                    } else {
                        llPanel.setVisibility(View.VISIBLE);
                        try {
                            ListView viewListViewBook = (ListView) rootView.findViewById(R.id.viewListViewBook);
                            viewListViewBook.setSelection(adapter.getCount() - 1);
                        } catch (Throwable eee) {
                            eee.printStackTrace();
                        }
                    }
                }
            }
        });
        rootView.findViewById(R.id.llMeetingTitle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //skip
            }
        });
        rootView.findViewById(R.id.closeTrans).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout llPanel = (LinearLayout) rootView.findViewById(R.id.llPanel);
                if (llPanel != null) {
                    llPanel.setVisibility(View.GONE);
                }
            }
        });
        rootView.findViewById(R.id.rlInfo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rootView.findViewById(R.id.llInfo).setVisibility(View.VISIBLE);
                rootView.findViewById(R.id.llTranscript).setVisibility(View.GONE);
                rootView.findViewById(R.id.bottomLineInfo).setVisibility(View.VISIBLE);
                rootView.findViewById(R.id.bottomLineTranscript).setVisibility(View.INVISIBLE);
            }
        });
        rootView.findViewById(R.id.rlTranscript).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rootView.findViewById(R.id.llInfo).setVisibility(View.GONE);
                rootView.findViewById(R.id.llTranscript).setVisibility(View.VISIBLE);
                rootView.findViewById(R.id.bottomLineInfo).setVisibility(View.INVISIBLE);
                rootView.findViewById(R.id.bottomLineTranscript).setVisibility(View.VISIBLE);
            }
        });
        if (BookActivity4Utils.SHOW_TRANSCRIPT_FIRST) {
            rootView.findViewById(R.id.rlTranscript).performClick(); //FIXME:init show transcript
        }
        if (USE_BOTTOM_SHEET) {
            windowPreferencesManager = new WindowPreferencesManager(getActivity());
            bottomSheetDialog1 = new BottomSheetDialog(getActivity());
            bottomSheetDialog1.setContentView(R.layout.cat_bottomsheet_content);
            bottomSheetDialog1.setDismissWithAnimation(true);
            windowPreferencesManager.applyEdgeToEdgePreference(bottomSheetDialog1.getWindow());
        }
        rootView.findViewById(R.id.btnEnterFullscreen).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getFocusModeDemo()) {
                    toggleFocusMode();
                } else {
                    setFocusModeDemo(true);
                    //show focus mode demo
                    rootView.findViewById(R.id.llTab).setVisibility(View.GONE);
                    rootView.findViewById(R.id.llTopBar).setVisibility(View.GONE);
                    //findViewById(R.id.llFullscreen).setVisibility(View.VISIBLE);
                    rootView.findViewById(R.id.llFullscreen2).setVisibility(View.VISIBLE);
                    rootView.findViewById(R.id.llFullscreen2_demo).setVisibility(View.VISIBLE);
                    rootView.findViewById(R.id.left_toolkit_global).setVisibility(View.GONE);
                }
            }
        });
        //not used this exit button
        rootView.findViewById(R.id.btnExitFullscreen).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rootView.findViewById(R.id.llTab).setVisibility(View.VISIBLE);
                rootView.findViewById(R.id.llTopBar).setVisibility(View.VISIBLE);
                //findViewById(R.id.llFullscreen).setVisibility(View.GONE);
                rootView.findViewById(R.id.llFullscreen2).setVisibility(View.GONE);
                rootView.findViewById(R.id.llFullscreen2_demo).setVisibility(View.GONE);
                rootView.findViewById(R.id.left_toolkit_global).setVisibility(View.VISIBLE);
            }
        });
        rootView.findViewById(R.id.btnFullscreenExit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rootView.findViewById(R.id.llTab).setVisibility(View.VISIBLE);
                rootView.findViewById(R.id.llTopBar).setVisibility(View.VISIBLE);
                //findViewById(R.id.llFullscreen).setVisibility(View.GONE);
                rootView.findViewById(R.id.llFullscreen2).setVisibility(View.GONE);
                rootView.findViewById(R.id.llFullscreen2_demo).setVisibility(View.GONE);
                rootView.findViewById(R.id.left_toolkit_global).setVisibility(View.VISIBLE);
            }
        });
        rootView.findViewById(R.id.llFullscreen2_demo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rootView.findViewById(R.id.llFullscreen2_demo).setVisibility(View.GONE);

//                if (true) {
//                    BottomSheet2 bottomSheet = new BottomSheet2();
//                    bottomSheet.setCancelable(false);
//                    bottomSheet.show(getSupportFragmentManager(), "");
//                } else {
//                    MainActivity.ToolsBottomSheet toolsBottomSheet = new MainActivity.ToolsBottomSheet();
//                    toolsBottomSheet.show(getSupportFragmentManager(), "");
//                }
            }
        });
        {
            NestedScrollView arBottomSheet = rootView.findViewById(R.id.ar_bottom_sheet);
            View ar_recording_check_view = rootView.findViewById(R.id.ar_recording_check_view);
            BottomSheetBehavior<NestedScrollView> behavior = BottomSheetBehavior.from(arBottomSheet);
            ar_recording_check_view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (behavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        //textViewPageInfo
                        TextView textViewPageInfoBottom = (TextView) rootView.findViewById(R.id.textViewPageInfoBottom);
                        textViewPageInfoBottom.setText("" + (getPageIdx() + 1) + "/" + pageNum);
                    } else {
                        behavior.setState(BottomSheetBehavior.STATE_HIDDEN);//BottomSheetBehavior.STATE_COLLAPSED);
                    }
                    return true;
                }
            });
            if (ENABLE_BOTTOM_SHEET) {
                ar_recording_check_view.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        rootView.findViewById(R.id.rl_bottom_sheet).requestLayout();
                        behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                        rootView.findViewById(R.id.rl_bottom_sheet).setVisibility(View.VISIBLE);
                    }
                }, 500);
            }
            rootView.findViewById(R.id.addPageButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    behavior.setState(BottomSheetBehavior.STATE_HIDDEN);//BottomSheetBehavior.STATE_COLLAPSED);
                    addNewPageAndGo(false);
                }
            });
            rootView.findViewById(R.id.cardShowPageNumInfo).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    behavior.setState(BottomSheetBehavior.STATE_HIDDEN);//BottomSheetBehavior.STATE_COLLAPSED);
                    beforePageGrid();
                    AlertDialog dialog = new BookActivity4PageGridDialog(getActivity(),
                            BookActivity4Fragment.this.dirUrl,
                            BookActivity4Fragment.this.dirUrlPath)
                            .create();
                    dialog.show();
                }
            });
        }

        llRichTextTool = (LinearLayout) rootView.findViewById(R.id.llRichTextTool);
        llRichTextTool2 = (LinearLayout) rootView.findViewById(R.id.llRichTextTool2);
        if (USE_BOTTOM_IME_TOOLBAR) {
            View decorView = getActivity().getWindow().getDecorView();
            decorView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    Rect r = new Rect();
                    //r will be populated with the coordinates of your view that area still visible.
                    decorView.getWindowVisibleDisplayFrame(r);
                    //canvas.getGlobalVisibleRect()
                    int[] location = new int[2];
                    canvas.getLocationOnScreen(location);
                    Rect rect2 = new Rect();
                    canvas.getGlobalVisibleRect(rect2);
                    int heightDiff = decorView.getRootView().getHeight() - (r.bottom - r.top);
                    //Log.e(TAG, "heightDiff : " + heightDiff);
                    if (heightDiff > 100) { // if more than 100 pixels, it's probably a keyboard...
                        // Keyboard is shown
                        {
                            //hide bottom dialogs
                            if (g_rootView.findViewById(R.id.bottomDialog1).getVisibility() == View.VISIBLE) {
                                g_rootView.findViewById(R.id.bottomDialog1).setVisibility(View.GONE);
                            }
                            if (g_rootView.findViewById(R.id.bottomDialog2).getVisibility() == View.VISIBLE) {
                                g_rootView.findViewById(R.id.bottomDialog2).setVisibility(View.GONE);
                            }
                        }
                        if (llRichTextTool2 != null && g_y_on) {
                            if (lastTimeShowKeyboard == 0) {
                                lastTimeShowKeyboard = System.currentTimeMillis();
                            }
//                        RelativeLayout.LayoutParams pp =
//                                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
//                                        RelativeLayout.LayoutParams.WRAP_CONTENT);

                            //getDtView(true).getHeight();
                            //getDtView(true).getMeasuredHeight();
                            Rect rect3 = new Rect();
                            Rect rect4 = new Rect();
                            getDtView(true).mEtTextEdit.getGlobalVisibleRect(rect3);
                            if (USE_FIRST_HIDE_EDITTEXT) {
                                if (lastTimeShowKeyboard != 0 &&
                                        System.currentTimeMillis() - lastTimeShowKeyboard > 0) { //500) { //5000) {
                                    getDtView(true).setVisibility(View.VISIBLE);
                                } else {
                                    getDtView(true).setVisibility(View.INVISIBLE);
                                }
                            }
                            llRichTextTool2.getGlobalVisibleRect(rect4);


                            RelativeLayout.LayoutParams pp = (RelativeLayout.LayoutParams) llRichTextTool2.getLayoutParams();
                            if (g_y + rect4.height() + rect3.height() > rect2.height() - (heightDiff)) {
                                //edittext too low
                                if (false) {
                                    //if use this method, edittext will be between tool bar and soft keyboard
                                    pp.bottomMargin = (int) (rect2.height() - g_y);
                                } else {
                                    getDtView(true).moveUp((int) (g_y - (rect2.height() - (heightDiff)) + rect4.height() + rect3.height()));
                                    canvas.getPanTool().moveUp((int) (g_y - (rect2.height() - (heightDiff)) + rect4.height() + rect3.height()));
                                    pp.bottomMargin = (int) (Math.max((heightDiff)/*r.bottom - r.top*/, 0));
                                }
                            } else {
                                pp.bottomMargin = (int) (Math.max((heightDiff)/*r.bottom - r.top*/, 0));

                                //edittext not too low
                                getDtView(true).moveUp(0);
                                canvas.getPanTool().moveUp(0);
                            }
                            pp.leftMargin = (int) (Math.max(0 - 0, 0));
                            //pp.alignWithParent = true;
                            llRichTextTool2.setLayoutParams(pp);
                            llRichTextTool2.setVisibility(View.VISIBLE);
//                            RelativeLayout.LayoutParams pp2 = (RelativeLayout.LayoutParams) canvas.getLayoutParams();
//                            pp2.topMargin = -(int) (Math.max((heightDiff)/*r.bottom - r.top*/, 0)); //(int) (rect2.height() - g_y);
//                            canvas.setLayoutParams(pp2);

                        }
                    } else {
                        // Keyboard is hidden
                        if (llRichTextTool2 != null) {
                            llRichTextTool2.setVisibility(View.GONE);
                        }
                        lastTimeShowKeyboard = 0;
                    }
                }
            });
        }

        BookActivity4RichText.initButtons(this);

        Slider sliderVerticalShape = (Slider) rootView.findViewById(R.id.sliderVerticalShape);
        sliderVerticalShape.setVisibility(View.GONE);
        if (BookActivity4Fragment.ENABLE_NO_DETECT_SHAPE_PEN) {
            sliderVerticalShape.setValueTo(10.0f - 2);
            sliderVerticalShape.setLabelFormatter(new LabelFormatter() {
                @NonNull
                @Override
                public String getFormattedValue(float value) {
                    //int intValue = Math.round(value);
                    //return intValue + "%";
                    if (value >= 1) {
                        return "" + (int)(value + 2);
                    } else {
                        return "" + (int)(value);
                    }
                }
            });
        //setThumbTextFormatter
        } else {
            sliderVerticalShape.setValueTo(5.0f);
        }
        sliderVerticalShape.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                if (fromUser) {
                    LinkedList<DrawPath> selectedPaths = canvas.getSelectionTool().getSelectedPaths();
                    if (selectedPaths != null) {
                        for (DrawPath drawPath : selectedPaths) {
                            if (drawPath != null &&
                                    drawPath.pointsType == DrawPath.POINTS_TYPE_STROKE &&
                                    drawPath.appearance != null &&
                                    drawPath.appearance.penType == DrawAppearance.PEN_TYPE_6) {
                                if (ENABLE_NO_DETECT_SHAPE_PEN) {
                                    drawPath.shapeSide = (int)value;
                                } else {
                                    switch ((int) value) {
                                        case 0:
                                            drawPath.shapeType = DrawPath.SHAPE_TYPE_UNKNOWN;
                                            break;
                                        case 1:
                                            drawPath.shapeType = DrawPath.SHAPE_TYPE_LINE;
                                            break;
                                        case 2:
                                            drawPath.shapeType = DrawPath.SHAPE_TYPE_RECTANGLE;
                                            break;
                                        case 3:
                                            drawPath.shapeType = DrawPath.SHAPE_TYPE_CIRCLE;
                                            break;
                                        case 4:
                                            drawPath.shapeType = DrawPath.SHAPE_TYPE_TRIANGLE;
                                            break;
                                        case 5:
                                            drawPath.shapeType = DrawPath.SHAPE_TYPE_POLYGON;
                                            break;
                                    }
                                }
                            }
                        }
                        canvas.invalidate();
                    }
                }
            }
        });
    }
    long lastTimeShowKeyboard = 0;
    float g_y = 0;
    boolean g_y_on = false;
//    private UnderlineSpan underlineSpan;

//    private Runnable refreshRunnable;
//    Handler handler = new Handler();
//    private void startHandlerTask() {
//        refreshRunnable = new Runnable() {
//            @Override
//            public void run() {
//                if (adapter != null) {
//                    adapter.notifyDataSetChanged();
//                }
//                if (rtasrDialog != null) {
//                    handler.postDelayed(this, 2000);
//                }
//            }
//        };
//        handler.postDelayed(refreshRunnable, 2000);
//    }

    private MyRunnable refreshRunnable2;
    Handler handler2 = new Handler();
    class MyRunnable implements Runnable {
        private boolean isStop = false;
        public void stop() {
            isStop = true;
        }
        @Override
        public void run() {
            if (isStop) {
                return;
            }
            if (SAVING_ASYNC) {
                try {
                    if (!SAVING_ASYNC_MULTI) {
                        if (task == null) {
                            task = new SavingTask(false, true);
                            task.executeOnExecutor(newFixedThreadPool);
                            Log.e(TAG, "startHandlerTask2 MyRunnable " + System.currentTimeMillis());
                        }
                    } else {
                        task = new SavingTask(false, true);
                        task.executeOnExecutor(newFixedThreadPool);
                    }
                } catch (Throwable eee) {
                    eee.printStackTrace();
                }
            }
            if (refreshRunnable2 == null || refreshRunnable2 != this) {
                return;
            }
            if (!BookActivity4Fragment.this.isDetached()) {
                handler2.postDelayed(this, DELAY_TIME2);
            }
        }
    }
    private void startHandlerTask2() {
        if (refreshRunnable2 != null) {
            refreshRunnable2.stop();
        }
        refreshRunnable2 = new MyRunnable();
        handler2.postDelayed(refreshRunnable2, DELAY_TIME2);
        Log.e(TAG, "startHandlerTask2 " + System.currentTimeMillis());
    }
    private MyRunnable3 refreshRunnable3;
    Handler handler3 = new Handler();
    class MyRunnable3 implements Runnable {
        private boolean isStop = false;
        public void stop() {
            isStop = true;
        }
        @Override
        public void run() {
            if (isStop) {
                return;
            }
            if (SAVING_ASYNC) {
                try {
                    if (!SAVING_ASYNC_MULTI) {
                        if (task == null) {
                            task = new SavingTask(false, true);
                            task.executeOnExecutor(newFixedThreadPool);
                            Log.e(TAG, "startHandlerTask3 MyRunnable3 " + System.currentTimeMillis());
                        }
                    } else {
                        task = new SavingTask(false, true);
                        task.executeOnExecutor(newFixedThreadPool);
                    }
                } catch (Throwable eee) {
                    eee.printStackTrace();
                }
            }
            if (refreshRunnable3 == null || refreshRunnable3 != this) {
                return;
            }
            if (!BookActivity4Fragment.this.isDetached()) {
                handler3.postDelayed(this, DELAY_TIME2);
            }
        }
    }
    private void startHandlerTask3() {
        if (refreshRunnable3 != null) {
            refreshRunnable3.stop();
        }
        refreshRunnable3 = new MyRunnable3();
        handler3.postDelayed(refreshRunnable3, DELAY_TIME2);
        Log.e(TAG, "startHandlerTask3 " + System.currentTimeMillis());
    }


    private void recordTempDuration() {
        if (lastRecordTime != null) {
            Date now = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(now);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            long diff = calendar.getTime().getTime() - lastRecordTime.getTime();
            long diffMinutes = diff / (60 * 1000);
            editMeetingDurationTemp("" + diffMinutes);
        }
    }
    private Date lastRecordTime = null;
    private void recordDuration() {
        if (lastRecordTime != null) {
            Date now = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(now);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            long diff = calendar.getTime().getTime() - lastRecordTime.getTime();
            long diffMinutes = diff / (60 * 1000);
            editMeetingDuration("" + diffMinutes);
            lastRecordTime = null;
        }
    }
    private void init001(View rootView) {
        {
            View.OnClickListener onClickListenerPause = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!enableRecordButton) {
                        return; //disable record button
                    }
                    if (g_rootView.findViewById(R.id.stopRecord).getVisibility() == View.VISIBLE) {
                        if (g_rootView.findViewById(R.id.pauseRecordOff).getVisibility() == View.VISIBLE) {
                            g_rootView.findViewById(R.id.pauseRecordOff).setVisibility(View.GONE);
                            g_rootView.findViewById(R.id.pauseRecordOn).setVisibility(View.VISIBLE);
                            AnimationDrawable anim2 = (AnimationDrawable) btnPanel2.getDrawable();
                            anim2.stop();
                            anim2.selectDrawable(0);
                        } else {
                            g_rootView.findViewById(R.id.pauseRecordOff).setVisibility(View.VISIBLE);
                            g_rootView.findViewById(R.id.pauseRecordOn).setVisibility(View.GONE);
                            AnimationDrawable anim2 = (AnimationDrawable) btnPanel2.getDrawable();
                            anim2.start();
                        }
                    }
                }
            };

            View.OnClickListener onClickListener_start = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!enableRecordButton) {
                        return; //disable record button
                    }
                    RadioButton rbASR1 = (RadioButton) g_rootView.findViewById(R.id.rbASR1);
                    RadioButton rbASR2 = (RadioButton) g_rootView.findViewById(R.id.rbASR2);
                    RadioButton rbASR3 = (RadioButton) g_rootView.findViewById(R.id.rbASR3);
                    if (TYPE_NO_CHOOSE) {
                        //skip
                    } else {
                        if (rbASR2.isChecked()) {
                            type = TYPE_USE_SHERPA;
                        } else if (rbASR3.isChecked()) {
                            type = TYPE_USE_SHERPA_KROKO;
                        } else {
                            type = TYPE_USE_VOSK;
                        }
                        setTypeASRTest();
                    }

                    g_rootView.findViewById(R.id.rlTranscript).performClick(); //FIXME:added
                    //isRecording
                    if (type == TYPE_USE_VOSK) {
                        if (voskDialog == null) {
                            {
                                Date now = new Date();
                                Date today = beginOfDay(now);
                                SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());//Locale.ENGLISH);
                                String dateStr_ = sdf.format(today);
                                editMeetingDate(dateStr_, today.getTime());
                                Calendar calendar = Calendar.getInstance();
                                calendar.setTime(now);
                                calendar.set(Calendar.SECOND, 0);
                                calendar.set(Calendar.MILLISECOND, 0);
                                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                                int minute = calendar.get(Calendar.MINUTE);
                                editMeetingTime(hour, minute);
                                lastRecordTime = calendar.getTime();
                            }
                            voskDialog = new BookActivity4VoskDialog(getActivity());
                            voskDialog.onClick_audio();
                            //startHandlerTask();
                        }
                    } else if (type == TYPE_USE_SHERPA || type == TYPE_USE_SHERPA_KROKO) {
                        int modelType = (type == TYPE_USE_SHERPA_KROKO ? 21 : 10); //english
                        String lang = getTranscriptLang();
                        if (lang != null) {
                            if (lang.equals("fr")) {
                                modelType = 23; //search 23:
                            } else if (lang.equals("de")) {
                                modelType = 24; //search 24:
                            } else if (lang.equals("es")) {
                                modelType = 22; //search 22:
                            }
                        }
                        OnlineModelConfig var10003 = null;
                        if (modelType == 21 || modelType == 10) { //English is embedded in assets
                            var10003 = OnlineRecognizerKt.getModelConfig(modelType, 0, getActivity());
                        } else {
                            var10003 = OnlineRecognizerKt.getModelConfig(modelType, 1, getActivity());
                        }
                        if (var10003 != null && !(modelType == 21 || modelType == 10)) {
                            if (var10003.getTransducer() != null) {
                                String encoder = var10003.getTransducer().getEncoder();
                                String decoder = var10003.getTransducer().getDecoder();
                                String joiner = var10003.getTransducer().getJoiner();
                                if (encoder != null) {
                                    if (!new File(encoder).exists()) {
                                        Toast.makeText(getActivity(), "Please download transcription model again", Toast.LENGTH_LONG).show();
                                        return;
                                    }
                                }
                                if (decoder != null) {
                                    if (!new File(decoder).exists()) {
                                        Toast.makeText(getActivity(), "Please download transcription model again", Toast.LENGTH_LONG).show();
                                        return;
                                    }
                                }
                                if (joiner != null) {
                                    if (!new File(joiner).exists()) {
                                        Toast.makeText(getActivity(), "Please download transcription model again", Toast.LENGTH_LONG).show();
                                        return;
                                    }
                                }
                            }
                        }

                        if (sherpaOnnxDialog == null) {
                            {
                                Date now = new Date();
                                Date today = beginOfDay(now);
                                SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());//Locale.ENGLISH);
                                String dateStr_ = sdf.format(today);
                                editMeetingDate(dateStr_, today.getTime());
                                Calendar calendar = Calendar.getInstance();
                                calendar.setTime(now);
                                calendar.set(Calendar.SECOND, 0);
                                calendar.set(Calendar.MILLISECOND, 0);
                                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                                int minute = calendar.get(Calendar.MINUTE);
                                editMeetingTime(hour, minute);
                                lastRecordTime = calendar.getTime();
                            }
                            sherpaOnnxDialog = new BookActivity4SherpaOnnxDialog(getActivity(),
                                    modelType);
                            //startHandlerTask();
                        }
                    }

//                    RadioButton rbASR1 = (RadioButton) g_rootView.findViewById(R.id.rbASR1);
//                    RadioButton rbASR2 = (RadioButton) g_rootView.findViewById(R.id.rbASR2);
//                    RadioButton rbASR3 = (RadioButton) g_rootView.findViewById(R.id.rbASR3);
                    if (TYPE_NO_CHOOSE) {
                        //sktip
                    } else {
                        rbASR1.setEnabled(false);
                        rbASR2.setEnabled(false);
                        rbASR3.setEnabled(false);
                    }
                }
            };
            View.OnClickListener onClickListener_stop = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            recordDuration();
                            if (type == TYPE_USE_VOSK) {
                                if (voskDialog != null) {
                                    voskDialog.onClick_stop();
                                    voskDialog = null;
                                } else {
                                    btn_audio_start_setEnabled(true);
                                }
                            } else if (type == TYPE_USE_SHERPA || type == TYPE_USE_SHERPA_KROKO) {
                                if (sherpaOnnxDialog != null) {
                                    sherpaOnnxDialog.onclick_Stop();
                                    sherpaOnnxDialog = null;
                                } else {
                                    btn_audio_start_setEnabled(true);
                                }
                            }
                        }
                    };
                    AlertDialog dialogStopRecord = new BookActivity4StopRecordDialog(getActivity(), runnable).create();
                    dialogStopRecord.show();
                    updateRecordButtonStatus();
                }
            };
            rootView.findViewById(R.id.startRecord).setOnClickListener(onClickListener_start);
            rootView.findViewById(R.id.stopRecord).setOnClickListener(onClickListener_stop);
            rootView.findViewById(R.id.pauseRecordOff).setOnClickListener(onClickListenerPause);
            rootView.findViewById(R.id.pauseRecordOn).setOnClickListener(onClickListenerPause);
            ListView viewListViewBook = (ListView) rootView.findViewById(R.id.viewListViewBook);
            String meetingId = "";
            String agendaId = "";
            adapter = new BookReaderItemsAdapter(getActivity(), meetingId, agendaId, _bookDir.getFilePath());
            viewListViewBook.setAdapter(adapter);
            viewListViewBook.setFastScrollEnabled(true);
            viewListViewBook.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (D) {
                        Log.d(TAG, "onItemClick " + position);
                    }
                    RecordingItem item = (RecordingItem)adapter.getItem(position);
                    if (item != null) {
                        if (item.getRecType() != null && item.getRecType().equals("text")) {

                        } else {
                            PlayerDialog playerDialog = new PlayerDialog(getActivity(), item);
                            playerDialog.show();
                        }

                    }
                }
            });
            if (false) {
                viewListViewBook.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> av, View v, int position, long id) {
                        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
                        final RecordingItem item = (RecordingItem) adapter.getItem(position);
                        if (item.getRecType() != null
                                && item.getRecType().equals("text")) {
                            builder.setTitle("Operation")//""Sync recognition result")
                                    .setItems(new String[]{
                                            "Delete", //0
                                    }, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (item != null) {
                                                switch (which) {
                                                    case 0:
                                                        adapter.remove(item);
                                                        break;
                                                }
                                            }
                                        }
                                    });
                        } else {
                            builder.setTitle(item.getName())
                                    .setItems(new String[]{
                                            "History", //0
                                            "To Mandarin", //1
                                            "To Cantonese", //2
                                            "To English", //3
                                            "", //4
                                            "----", //5
                                            "Clear repeat", //6
                                            "Clear History", //7
                                            "Delete", //8
                                    }, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (item != null) {
                                                switch (which) {
                                                    case 0:
                                                        startActivity(new Intent(getActivity(), DictResultActivity.class).putExtra(DictResultActivity.EXTRA_RECORDING_ID, item.getId()));
                                                        break;

                                                    case 1: {
                                                        //Toast.makeText(MainActivity.this, item.getFilePath(), Toast.LENGTH_SHORT).show();
//                                                        RecognizeDialog recogizeDialog = new RecognizeDialog(getActivity(), item, RecognizeDialog.LANG_CHINESE);
//                                                        recogizeDialog.show();
                                                    }
                                                    break;

                                                    case 2: {
                                                        //Toast.makeText(MainActivity.this, item.getFilePath(), Toast.LENGTH_SHORT).show();
//                                                        RecognizeDialog recogizeDialog = new RecognizeDialog(getActivity(), item, RecognizeDialog.LANG_CHINESE_GD);
//                                                        recogizeDialog.show();
                                                    }
                                                    break;

                                                    case 3: {
                                                        //Toast.makeText(MainActivity.this, item.getFilePath(), Toast.LENGTH_SHORT).show();
//                                                        RecognizeDialog recogizeDialog = new RecognizeDialog(getActivity(), item, RecognizeDialog.LANG_ENGLISH);
//                                                        recogizeDialog.show();
                                                    }
                                                    break;

                                                    case 6:
                                                        //clearHistoryRepeat(item);
                                                        break;

                                                    case 7:
                                                        //clearHistory(item);
                                                        break;

                                                    case 8:
                                                        adapter.remove(item);
                                                        break;
                                                }
                                            }
                                        }
                                    });
                        }
                        builder.show();
                        return true;
                    }
                });
            }
        }


        rootView.findViewById(R.id.btnFullscreenUndo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rootView.findViewById(R.id.btnTitleUndo).performClick();
            }
        });
        rootView.findViewById(R.id.btnFullscreenRedo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rootView.findViewById(R.id.btnTitleRedo).performClick();
            }
        });
        rootView.findViewById(R.id.btnFullscreenBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rootView.findViewById(R.id.btnTitleBack).performClick();
            }
        });
        //this.mBottom1.init002(rootView);

        ActionBar topAppBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        if (topAppBar != null) {
            topAppBar.setIcon(R.mipmap.ic_launcher);
            topAppBar.setHomeButtonEnabled(true);
            topAppBar.setDisplayHomeAsUpEnabled(true);
            if (!BookActivity4Utils.USE_ACTIONBAR) {
                topAppBar.hide();
            }
        }
        rootView.findViewById(R.id.buttonBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        rootView.findViewById(R.id.buttonPen2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPen2(rootView);
            }
        });
        rootView.findViewById(R.id.buttonEraser2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onEraser2(rootView);
            }
        });

        rootView.findViewById(R.id.buttonPenColor).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPenColor();
            }
        });
        rootView.findViewById(R.id.buttonPaint).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSelectPaint();
            }
        });
        rootView.findViewById(R.id.buttonPen).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPen(rootView);
            }
        });
        rootView.findViewById(R.id.buttonBrush).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBrush(rootView);
            }
        });
        rootView.findViewById(R.id.buttonEraser).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onEraser(rootView);
            }
        });

//        selectPenOrEraser(true);

        rootView.findViewById(R.id.buttonUndo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onUndo();
            }
        });
        rootView.findViewById(R.id.buttonRedo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRedo();
            }
        });
        rootView.findViewById(R.id.buttonGrid).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SAVING_ASYNC) {
                    try {
                        if (!SAVING_ASYNC_MULTI) {
                            if (task == null) {
                                task = new SavingTask(false, false);
                                task.executeOnExecutor(newFixedThreadPool);
                            }
                        } else {
                            task = new SavingTask(false, false);
                            task.executeOnExecutor(newFixedThreadPool);
                        }
                    } catch (Throwable eee) {
                        eee.printStackTrace();
                    }
                } else {
                    gotoGridPage();
                }
            }
        });
        rootView.findViewById(R.id.buttonFirstPage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoFirstPage();
            }
        });
        rootView.findViewById(R.id.buttonPrevPage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoPrevPage();
            }
        });
        rootView.findViewById(R.id.buttonNextPage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoNextPage();
            }
        });
        rootView.findViewById(R.id.buttonLastPage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoLastPage();
            }
        });
        rootView.findViewById(R.id.buttonAddPage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewPageAndGo(true);
            }
        });
        rootView.findViewById(R.id.buttonRemovePage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeCurrentPageAndGo();
            }
        });
        rootView.findViewById(R.id.buttonShare).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    share();
                }
            }
        });
        textViewPageInfo = (TextView) rootView.findViewById(R.id.textViewPageInfo);
    }

    private void startResmueRecord_old() {
        if (type == TYPE_USE_VOSK ||
                type == TYPE_USE_SHERPA ||
                type == TYPE_USE_SHERPA_KROKO) {
            //skip
        } else {
            if (USE_RECORDING_FRAGMENT_TEST) {
//                            RecordingFragment fragment = (RecordingFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_recording);
//                            if (fragment != null && fragment.button1 != null) {
//                                fragment.button1.performClick();
//                                isRecording = !isRecording;
//                            }
//                            if (adapter != null) {
//                                adapter.notifyDataSetChanged();
//                            }
//                            AppCompatImageView btnPanel = (AppCompatImageView) rootView.findViewById(R.id.btnPanel);
//                            AnimationDrawable anim = (AnimationDrawable) btnPanel.getDrawable();
//                            if (isRecording) { //FIXME: use var not good
//                                anim.start();
//                                rootView.findViewById(R.id.startRecord).setVisibility(View.GONE);
//                                rootView.findViewById(R.id.stopRecord).setVisibility(View.VISIBLE);
//                            } else {
//                                anim.stop();
//                                rootView.findViewById(R.id.startRecord).setVisibility(View.VISIBLE);
//                                rootView.findViewById(R.id.stopRecord).setVisibility(View.GONE);
//                            }
//                            Toast.makeText(getActivity(), "total : " + adapter.getCount(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void init002(View rootView) {
        //FIXME:throw new RuntimeException("not implemented");
        canvas = (DrawCanvas) rootView.findViewById(R.id.canvas);
        canvas.initAct(getActivity());
        canvas.setPenType(DrawAppearance.PEN_TYPE_1);
        canvas.onVersionChanged();
        clearRestorePages();
//        dtView = (DrawTextView) rootView.findViewById(R.id.dtView);
//        dtView.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if (dtView != null) {
//                    dtView.hideSoftInput();
//                }
//            }
//        }, 100);
        llASR = (LinearLayout) rootView.findViewById(R.id.llASR);
        rl_ai = (RelativeLayout) rootView.findViewById(R.id.rl_ai);
        llPanel = (LinearLayout) rootView.findViewById(R.id.llPanel);
        btnPanel2 = (AppCompatImageView) rootView.findViewById(R.id.btnPanel2);
        rlAIIcon = (RelativeLayout) rootView.findViewById(R.id.rlAIIcon);
        dtViewBottom = (View) rootView.findViewById(R.id.dtViewBottom);
        dtViewBottom.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int ea = event.getAction();
                switch (ea) {
                    case MotionEvent.ACTION_DOWN:
                        DrawPath selectedText = null;
                        float tempW = 0;
                        float tempH = 0;
                        Point originalPoint = canvas.mapPoint(event.getX(), event.getY(), event.getPressure());
                        for (DrawPath path : canvas.paths) {
                            if (path != null && path.pointsType == DrawPath.POINTS_TYPE_TEXT) {
                                if (path.pointsText != null) {
                                    Paint p = new Paint();
                                    DrawCanvas.getTextPaint(p,
                                            isBold,
                                            isItalics,
                                            styleType,
                                            isUnderline,
                                            path.pointsTextColor,
                                            path.pointsTextSize);
                                    SizeF size = DrawCanvas.calculateTextSizes(path.pointsText, p, path.pointsTextType);
                                    tempW = size.getWidth();
                                    tempH = size.getHeight();

                                    //for calculating midpoint
                                    float tempPointXMin = path.pointsTextX;
                                    float tempPointXMax = path.pointsTextX + path.pointsScaleX * tempW;
                                    float tempPointYMin = path.pointsTextY;
                                    float tempPointYMax = path.pointsTextY + path.pointsScaleX * tempH;
                                    RectF rectF = new RectF(tempPointXMin, tempPointYMin, tempPointXMax, tempPointYMax);
                                    if (rectF.contains(originalPoint.x, originalPoint.y)) {
                                        selectedText = path;
                                        break;
                                    }
                                }
                            }
                        }
                        if (selectedText != null) {
                            dtViewBottom.setVisibility(View.GONE);
                            canvas.setTool(DrawCanvas.TOOLS.select);
                            onClickTopBar(rootView, iconsTopBar[3], false); //init

                            canvas.getSelectionTool().exitSelect();
                            canvas.getSelectionTool().getSelectedPaths().add(selectedText);
                            Point boundsTop = new Point(selectedText.pointsTextX, selectedText.pointsTextY);
                            Point boundsBottom = new Point(boundsTop.x + selectedText.pointsScaleX * tempW, boundsTop.y + selectedText.pointsScaleY * tempH);
                            canvas.getSelectionTool().currentPath.addPoint(boundsTop);
                            canvas.getSelectionTool().currentPath.addPoint(new Point(boundsBottom.x, boundsTop.y));
                            canvas.getSelectionTool().currentPath.addPoint(boundsBottom);
                            canvas.getSelectionTool().currentPath.addPoint(new Point(boundsTop.x, boundsBottom.y));
                            canvas.getSelectionTool().currentPath.appearance =
                                    canvas.getSelectionTool().APPEARANCE_SELECTED;
                            canvas.invalidate();
                            //enter re-edit mode
                            //llRichTextTool.setVisibility(View.VISIBLE);
                        } else {
                            //enter first edit mode
                            if (USE_FLOAT_IME_TOOLBAR) {
                                llRichTextTool.setVisibility(View.VISIBLE);
                                RelativeLayout.LayoutParams pp =
                                        new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                                                RelativeLayout.LayoutParams.WRAP_CONTENT);
                                pp.topMargin = (int) (Math.max(event.getY() - 100, 60));
                                pp.leftMargin = (int) (Math.max(event.getX() - 0, 0));
                                llRichTextTool.setLayoutParams(pp);
                            }
                            if (USE_BOTTOM_IME_TOOLBAR) {
                                g_y = event.getY();

                                g_y_on = true;
                            }
                            g_rootView.findViewById(R.id.left_toolkit_global).setVisibility(View.GONE);
                            canvas.getPanTool().moveUp(0);

                            float lastX = event.getX();
                            float lastY = event.getY();
                            dtViewBottom.setVisibility(View.GONE);
                            getDtView(true).setVisibility(View.VISIBLE);
                            if (USE_FIRST_HIDE_EDITTEXT) {
                                if (g_y_on) {
                                    getDtView(true).setVisibility(View.INVISIBLE);
                                }
                            }
                            setBoldItalicsStyle();
                            setAlignType(alignType);
                            setSizeType(sizeType);
                            setEditTextColor(editTextColor);
                            getDtView(true).init2(lastX, lastY, "",
                                    BookActivity4Utils.USE_HTML_EDIT ? DrawPath.POINTS_TEXT_TYPE_RICH : DrawPath.POINTS_TEXT_TYPE_NONE,
                                    BookActivity4Fragment.this.editTextColor,
                                    (float) (BookActivity4Fragment.this.editTextSize * canvas.getScaleFactor()),
                                    new DrawTextView.CallBackListener() {
                                        @Override
                                        public void onUpdate(DrawPoint drawPoint) {

                                        }

                                        @Override
                                        public void onSave(DrawPoint drawPoint) {
                                            if (USE_FLOAT_IME_TOOLBAR) {
                                                llRichTextTool.setVisibility(View.GONE);
                                            }
                                            if (USE_BOTTOM_IME_TOOLBAR) {
                                                g_y = 0;
                                                g_y_on = false;
                                            }
                                            g_rootView.findViewById(R.id.left_toolkit_global).setVisibility(View.VISIBLE);

                                            canvas.getPanTool().moveUp(0);
                                            if (getDtView(false) != null) {
                                                getDtView(false).setVisibility(View.GONE);
                                            }

                                            if (drawPoint != null && drawPoint.getDrawText() != null) {
                                                if (true) {
                                                    //Paint paint = new Paint();
                                                    TextPaint paint = getDtView(true).mEtTextEdit.getPaint();
                                                    //                                        paint.setColor(0xFFFF0000);
                                                    //                                        paint.setTextSize(sp2px(BookActivity4.this, 24));
                                                    drawText(canvas,
                                                            drawPoint.getDrawText().getStr(drawPoint.getDrawText().getUseHtml()),
                                                            drawPoint.getDrawText().getUseHtml() ? DrawPath.POINTS_TEXT_TYPE_RICH : DrawPath.POINTS_TEXT_TYPE_NONE,
                                                            drawPoint.getDrawText().getX(),
                                                            drawPoint.getDrawText().getY(),
                                                            paint,
                                                            isBold, isItalics, isUnderline, styleType,
                                                            editTextColor,
                                                            editTextSize// * canvas.getScaleFactor())
                                                    );
                                                } else {
                                                    drawText(canvas, "hello", DrawPath.POINTS_TEXT_TYPE_RICH, 100, 100, null,
                                                            false, false, false, 0,
                                                            0xFFFF0000, 18 * 5);
                                                }
                                                if (canvas != null) {
                                                    canvas.versionBackup();
                                                }
                                            }
                                            if (false) {
                                                rootView.findViewById(R.id.top_toolkit_item1).performClick();
                                            } else {
                                                if (true) {
                                                    rootView.findViewById(R.id.top_toolkit_item2).performClick();
                                                } else {
                                                    dtViewBottom.setVisibility(View.VISIBLE);
                                                    getDtView(true).setVisibility(View.GONE);
                                                    llASR.setVisibility(View.GONE);
                                                    rl_ai.setVisibility(View.GONE);
                                                    rootView.findViewById(R.id.left_toolkit1).setVisibility(View.GONE);
                                                    rootView.findViewById(R.id.left_toolkit2).setVisibility(View.VISIBLE);
                                                    rootView.findViewById(R.id.left_toolkit4).setVisibility(View.GONE);
                                                }
                                                //skip, keep in text toolkit
                                            }
                                        }
                                    });
                        }
                        break;

                    case MotionEvent.ACTION_MOVE:
                        break;

                    case MotionEvent.ACTION_UP:
                        break;
                }
                return true;
            }
        });
    }

    public void editText(DrawPath path) {
        if (USE_FLOAT_IME_TOOLBAR) {
            llRichTextTool.setVisibility(View.VISIBLE);
            RelativeLayout.LayoutParams pp =
                    new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT);
            float x = path.pointsTextX;
            float y = path.pointsTextY;
            Point originalPoint = canvas.mapPointScreen(x, y, 1.0F);
            pp.topMargin = (int) (Math.max(originalPoint.y - 100, 60));
            pp.leftMargin = (int) (Math.max(originalPoint.x - 0, 0));
            llRichTextTool.setLayoutParams(pp);
        }
        if (USE_BOTTOM_IME_TOOLBAR) {
            float x = path.pointsTextX;
            float y = path.pointsTextY;
            Point originalPoint = canvas.mapPointScreen(x, y, 1.0F);
            g_y = originalPoint.y;

            g_y_on = true;
        }
        g_rootView.findViewById(R.id.left_toolkit_global).setVisibility(View.GONE);

        Point point = canvas.mapPointScreen(
                path.pointsTextX,
                path.pointsTextY,
                1.0f);
        float lastX = point.x;
        float lastY = point.y;

        dtViewBottom.setVisibility(View.GONE);
        getDtView(true).setVisibility(View.VISIBLE);
        if (USE_FIRST_HIDE_EDITTEXT) {
            if (g_y_on) {
                getDtView(true).setVisibility(View.INVISIBLE);
            }
        }
        setBoldItalicsStyle();
        setAlignType(alignType);
        setSizeType(sizeType);
        setEditTextColor(editTextColor);
        getDtView(true).init2(lastX, lastY, path.pointsText, path.pointsTextType, path.pointsTextColor,
                (float)(path.pointsTextSize * canvas.getScaleFactor() *
                        path.pointsScaleY), //FIXME: scaleY
                new DrawTextView.CallBackListener() {
                    @Override
                    public void onUpdate(DrawPoint drawPoint) {

                    }

                    @Override
                    public void onSave(DrawPoint drawPoint) {
                        if (USE_FLOAT_IME_TOOLBAR) {
                            llRichTextTool.setVisibility(View.GONE);
                        }
                        if (USE_BOTTOM_IME_TOOLBAR) {
                            g_y = 0;
                            g_y_on = false;
                        }
                        g_rootView.findViewById(R.id.left_toolkit_global).setVisibility(View.VISIBLE);
                        canvas.getPanTool().moveUp(0);

                        if (getDtView(false) != null) {
                            getDtView(false).setVisibility(View.GONE);
                        }
                        if (drawPoint != null && drawPoint.getDrawText() != null) {
                            if (true) {
                                //Paint paint = new Paint();
                                TextPaint paint = getDtView(true).mEtTextEdit.getPaint();
//                                        paint.setColor(0xFFFF0000);
//                                        paint.setTextSize(sp2px(BookActivity4.this, 24));
                                if (false) {
                                    drawText(canvas,
                                            drawPoint.getDrawText().getStr(drawPoint.getDrawText().getUseHtml()),
                                            drawPoint.getDrawText().getUseHtml() ? DrawPath.POINTS_TEXT_TYPE_RICH : DrawPath.POINTS_TEXT_TYPE_NONE,
                                            drawPoint.getDrawText().getX(),
                                            drawPoint.getDrawText().getY(),
                                            paint,
                                            path.isBold, path.isItalics, path.isUnderline, path.styleType,
                                            path.pointsTextColor,
                                            path.pointsTextSize// * canvas.getScaleFactor())
                                    );
                                } else {
                                    if (path.pointsTextType == DrawPath.POINTS_TEXT_TYPE_RICH) {
                                        path.pointsText = drawPoint.getDrawText().getStr(drawPoint.getDrawText().getUseHtml());
                                    } else {
                                        path.pointsText = drawPoint.getDrawText().getStr(drawPoint.getDrawText().getUseHtml());
                                    }
                                    path.tempHidden = false; //show again
                                    if (canvas != null) {
                                        canvas.versionBackup();
                                    }
                                    canvas.invalidate();
                                }
                            } else {
                                drawText(canvas, "hello", DrawPath.POINTS_TEXT_TYPE_RICH, 100, 100, null,
                                        false, false, false, 0,
                                        0xFFFF0000, 18 * 5);
                            }
                        }
                        if (false) {
                            g_rootView.findViewById(R.id.top_toolkit_item1).performClick();
                        } else {
                            if (true) {
                                if (false) {
                                    g_rootView.findViewById(R.id.top_toolkit_item2).performClick();
                                }
                            } else {
                                dtViewBottom.setVisibility(View.VISIBLE);
                                getDtView(true).setVisibility(View.GONE);
                                llASR.setVisibility(View.GONE);
                                rl_ai.setVisibility(View.GONE);
                                g_rootView.findViewById(R.id.left_toolkit1).setVisibility(View.GONE);
                                g_rootView.findViewById(R.id.left_toolkit2).setVisibility(View.VISIBLE);
                                g_rootView.findViewById(R.id.left_toolkit4).setVisibility(View.GONE);
                            }
                            //skip, keep in text toolkit
                        }
                    }
                });
    }

    private void init003(View rootView) {
        try {
            _pageIdx = getBook().getLastPageIndex(); //FIXME:added
            canvas.pageIdx = _pageIdx; //FIXME:added
            BookPage page = getBook().getPage(getPageIdx());
            BitmapVector result = getBookIO().loadBitmapOrNull(page);
            Bitmap initBmp = null;
            if (result != null && result.strVecJson != null && result.strVecJson.length() > 0) {
                initBmp = null;
                if (canvas != null) {
                    canvas.loadVecJson(result.strVecJson);
                }
            } else if (result != null) {
                initBmp = result.bitmap;
            }
            Bitmap bgBmp = getBookIO().loadBgOrNull(getBook());
            try {
                String metaTxt = getBookIO().loadMetaPng(page.getFile());
                if (metaTxt != null && metaTxt.length() > 0) {
                    JSONObject item = new JSONObject(metaTxt);
                    curPattern = item.optString(BookActivity4Config.USE_SKETCH_CONFIG_PATTERN);
                    backText = curPattern;
                }
            } catch (Throwable eee) {
                eee.printStackTrace();
                if (INIT_EMPTY_BACK_TEXT_WHEN_ADD_PAGE) {
                    curPattern = FileMeta.NONE;
                    backText = curPattern;
                }
            }
            init(canvas, initBmp, bgBmp, initialPageIdx, getActivity());
            setBackText(canvas, backText);
        } catch (Throwable eee) {
            eee.printStackTrace();
        }
        canvas.setClipToOutline(true);
        setOnUpdateListener(canvas, new CanvasBoox.OnUpdateBmpListener() {
            @Override
            public void onUpdateBmp(Bitmap bmp) {
                notifyBitmapUpdate(bmp);
            }
        });
        setOnUndoStateListener(canvas, new CanvasBoox.OnUndoStateListener() {
            @Override
            public void onUndoState(Boolean undo, Boolean redo) {
                notifyUndoStateChanged(undo, redo);
            }
        });

        isPenEraserBrush = 1;
        selectPenOrEraser(rootView, 1);
        setPenEraserBrush(canvas,1);

        TextView tvMeetingSummary = rootView.findViewById(R.id.tvMeetingSummary);
        tvMeetingSummary.setText(getMeetingSummary());
        rootView.findViewById(R.id.tvEditSummary).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog dialog = new BookActivity4MeetingSummaryDialog(getActivity(), getMeetingSummary())
                        .create();
                if (dialog != null) {
                    dialog.show();
                }
            }
        });
        String meetingSpeaker = getMeetingSpeaker();
        if (meetingSpeaker != null) {
            com.google.android.material.textfield.TextInputEditText editNum =
                    (com.google.android.material.textfield.TextInputEditText)g_rootView.findViewById(R.id.textStateSpeakerNum);
            if (editNum != null) {
                editNum.setText(meetingSpeaker);
            }
        }
        rootView.findViewById(R.id.startDiarization).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (true) {
                    startDiarization();
                } else {
                    AlertDialog dialog =
                            new BookActivity4MeetingDiarizationDialog(getActivity(),
                                    "" + numSpeakers, "" + threshold).create();
                    if (dialog != null) {
                        dialog.show();
                    }
                }
            }
        });
        rootView.findViewById(R.id.startDiarization).postDelayed(new Runnable() {
            @Override
            public void run() {
                loadDiarizationFile();
            }
        }, 2000);
        TextView tvMeetingDate = rootView.findViewById(R.id.tvMeetingDate);
        Long meetingDate = getMeetingDate();
        if (meetingDate != null) {
            if (meetingDate > 0) {
                Date newDate = new Date(meetingDate);
                SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());//Locale.ENGLISH);
                String dateStr_ = sdf.format(newDate);
                if (dateStr_ != null) {
                    tvMeetingDate.setText(dateStr_);
                }
            }
        }
        rootView.findViewById(R.id.llMeetingDate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (DATE_READONLY) {
                    return;
                } else {
                    /*Dialog dialog = */
                    new BookActivity4MeetingDateDialog(getActivity(), getMeetingDate())
                            .create();
//                if (dialog != null) {
//                    dialog.show();
//                }
                }
            }
        });

        TextView tvMeetingTime = rootView.findViewById(R.id.tvMeetingTime);
        Integer meetingHour = getMeetingHour();
        Integer meetingMinute = getMeetingMinute();
        if (meetingHour != null && meetingMinute != null) {
            String dateStr_ = String.format("%02d:%02d", meetingHour, meetingMinute);
            tvMeetingTime.setText(dateStr_);
        } else {
            tvMeetingTime.setText("");
        }
        rootView.findViewById(R.id.llMeetingTime).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (DATE_READONLY) {
                    return;
                } else {
                    /*Dialog dialog = */
                    new BookActivity4MeetingTimeDialog(getActivity(),
                            getMeetingHour(), getMeetingMinute())
                            .create();
//                if (dialog != null) {
//                    dialog.show();
//                }
                }
            }
        });

        TextView tvMeetingDuration = rootView.findViewById(R.id.tvMeetingDuration);
        try {
            String duration = getMeetingDuration();
            if (duration == null || duration.length() == 0) {
                duration = getMeetingDurationTemp();
                if (duration != null && duration.length() > 0) {
                    editMeetingDuration(duration);
                }
            }
            if (duration != null && duration.length() > 0) {
                int durationVal = -1;
                try {
                    durationVal = Integer.parseInt(duration);
                } catch (Throwable eee) {
                    eee.printStackTrace();
                }
                if (durationVal > 1) {
                    tvMeetingDuration.setText("" + duration + " minutes");
                } else {
                    tvMeetingDuration.setText("" + duration + " minute");
                }
            }
        } catch (Throwable eee) {
            eee.printStackTrace();
        }
        rootView.findViewById(R.id.llMeetingDuration).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (DATE_READONLY) {
                    return;
                } else {
                    AlertDialog dialog = new BookActivity4MeetingDurationDialog(getActivity(), getMeetingDuration())
                            .create();
                    if (dialog != null) {
                        dialog.show();
                    }
                }
            }
        });


        RadioButton rbASR1 = (RadioButton) g_rootView.findViewById(R.id.rbASR1);
        RadioButton rbASR2 = (RadioButton) g_rootView.findViewById(R.id.rbASR2);
        RadioButton rbASR3 = (RadioButton) g_rootView.findViewById(R.id.rbASR3);
        RadioGroup rgASR = (RadioGroup) g_rootView.findViewById(R.id.rgASR);
        if (TYPE_NO_CHOOSE) {
            rgASR.setVisibility(View.GONE);
        } else {
            int lastASRType = getTypeASRTest();
            if (lastASRType == TYPE_USE_SHERPA) {
                rbASR2.setChecked(true);
            } else if (lastASRType == TYPE_USE_SHERPA_KROKO) {
                rbASR3.setChecked(true);
            } else {
                rbASR1.setChecked(true);
            }
            //don't use rbASR1.setOnCheckedChangeListener();
            RadioGroup.OnCheckedChangeListener onCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                    if (checkedId == R.id.rbASR2) {// rbASR2.isChecked()) {
                        type = TYPE_USE_SHERPA;
                    } else if (checkedId == R.id.rbASR3) { //rbASR3.isChecked()) {
                        type = TYPE_USE_SHERPA_KROKO;
                    } else {
                        type = TYPE_USE_VOSK;
                    }
                    setTypeASRTest();
                }
            };
            rgASR.setOnCheckedChangeListener(onCheckedChangeListener);
        }
    }

    public void editMeetingDiarization(String number, String threshold_) {
        boolean parseSuccess = false;
        try {
            this.numSpeakers = Integer.parseInt(number);
            this.threshold = Float.parseFloat(threshold_);
            parseSuccess = true;
            startDiarization();
        } catch (Throwable eee) {
            eee.printStackTrace();
            final TextView tvDiarizationLog = g_rootView.findViewById(R.id.tvDiarizationLog);
            if (parseSuccess) {
                if (tvDiarizationLog != null) {
                    tvDiarizationLog.setText("Diarization failed");
                }
            } else {
                if (tvDiarizationLog != null) {
                    tvDiarizationLog.setText("Diarization failed, parameters error");
                }
            }
        }
    }


    public void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    String progress = "";
    boolean done = false;
    boolean fileIsOk = false;
    float[] samples = null;
    String status;
    boolean started = false;
    int numSpeakers = 0;
    float threshold = 0.5f;
    private void startDiarization() {
        try {
            if (started) {
                return;
            }
            final TextView tvDiarizationLog = g_rootView.findViewById(R.id.tvDiarizationLog);
            if (tvDiarizationLog != null) {
                tvDiarizationLog.setText("Loading...");
            }
            com.google.android.material.textfield.TextInputEditText editNum =
                    (com.google.android.material.textfield.TextInputEditText)g_rootView.findViewById(R.id.textStateSpeakerNum);
            try {
                hideKeyboard(editNum);
                //numSpeakers = 0;
                if (editNum != null) {
                    String editNumStr = editNum.getText().toString();
                    numSpeakers = Integer.parseInt(editNumStr);
                } else {
//                    if (tvDiarizationLog != null) {
//                        tvDiarizationLog.setText("Failed, parse error.");
//                    }
                }
            } catch (Throwable eee) {
                numSpeakers = 0;
//                if (tvDiarizationLog != null) {
//                    tvDiarizationLog.setText("Failed, parse error.");
//                }
            }
            if (editNum != null) {
                editNum.setText("" + numSpeakers);
                editMeetingSpeaker("" + numSpeakers);
            }
            SpeakerDiarizationObject.INSTANCE.initSpeakerDiarization(getContext().getAssets());

            Context a_context = getContext().getApplicationContext();
            File pcmFile = null;
            File wavFile = null;
            if (false) {
                new File(getContext().getExternalFilesDir(null), "audio_record.pcm");
                new File(getContext().getExternalFilesDir(null), "audio_record.wav");
            } else {
                String dirPath = this.dirUrlPath;
                pcmFile = new File(dirPath, "audio_record.pcm");
                wavFile = new File(dirPath, "audio_record.wav");
            }
            String filename = wavFile.getAbsolutePath();
            Uri o = Uri.fromFile(new File(filename));
            if (!filename.isEmpty()) {
                com.k2fsa.sherpa.onnx.speaker.diarization.screens.WaveData data
                        = ReadWaveFileKt.readUri(a_context, o);
                Log.i(TAG, "sample rate: " + data.getSampleRate());
                Log.i(TAG, "numSamples: " +
                        (data.getSamples() != null ? data.getSamples().length : 0));
                if (data.getMsg() != null) {
                    Log.i(TAG, "failed to read " + filename);
                    status = data.getMsg();
                    //                Toast.makeText(getContext(),
                    //                        status,
                    //                        Toast.LENGTH_LONG).show();
                } else if (data.getSampleRate() != SpeakerDiarizationObject.INSTANCE.getSd().sampleRate()) {
                    status = "Expected sample rate: " + SpeakerDiarizationObject.INSTANCE.getSd().sampleRate() +
                            ". Given wave file with sample rate: " + data.getSampleRate();
                    //                Toast.makeText(getContext(),
                    //                        status,
                    //                        Toast.LENGTH_LONG).show();
                } else {
                    samples = data.getSamples();
                    //                Toast.makeText(getContext(),
                    //                        "load " + filename + " success!",
                    //                        Toast.LENGTH_LONG).show();
                }
            }

            Log.i(TAG, "started");
            Log.i(TAG, "num samples: " + (samples != null ? samples.length : 0));
            started = true;
            progress = "";
            if (tvDiarizationLog != null) {
                tvDiarizationLog.setText("Diarization started, Please wait");
            }

            OfflineSpeakerDiarizationConfig config = SpeakerDiarizationObject.INSTANCE.getSd().getConfig();
            config.getClustering().setNumClusters(numSpeakers);
            config.getClustering().setThreshold(threshold);

            SpeakerDiarizationObject.INSTANCE.getSd().setConfig(config);

            final CardView startDiarization = g_rootView.findViewById(R.id.startDiarization);
            (new Thread() {
                @Override
                public void run() {
                    done = false;
                    status = "Started! Please wait";
                    OfflineSpeakerDiarizationSegment[] segments =
                            SpeakerDiarizationObject.INSTANCE.getSd().processWithCallback(
                                    samples, myCallback, 0
                            );
                    done = true;
                    started = false;
                    status = "";
                    Log.i(TAG, "segments.length == " + segments.length);
                    for (OfflineSpeakerDiarizationSegment s : segments) {
                        String start = String.format("%.2f", s.getStart());
                        String end = String.format("%.2f", s.getEnd());
                        String speaker = String.format("speaker_%02d", s.getSpeaker());
                        status += "" + start + " -- " + end + " " + speaker + "\n";
                        Log.i(TAG, "" + start + " -- " + end + " " + speaker);
                    }
                    saveDiarizationFile(segments);
                    if (tvDiarizationLog != null) {
                        tvDiarizationLog.post(new Runnable() {
                            @Override
                            public void run() {
                                tvDiarizationLog.setText("Diarization done");
                                loadDiarizationFile();
                                g_rootView.findViewById(R.id.rlTranscript).performClick();
                            }
                        });
                    }
                    //Log.i(TAG, status);
                    if (false) {
                        startDiarization.post(new Runnable() {
                            @Override
                            public void run() {
                                //Toast.makeText(getContext(), status, Toast.LENGTH_LONG).show();
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        if (dialogInterface != null) {
                                            dialogInterface.dismiss();
                                        }
                                    }
                                });
                                builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                    @Override
                                    public void onCancel(DialogInterface dialogInterface) {
                                        if (dialogInterface != null) {
                                            dialogInterface.dismiss();
                                        }
                                    }
                                });
                                builder.setTitle("Speaker Diarization");
                                builder.setMessage(status);
                                builder.setCancelable(true);
                                final AlertDialog dialog = builder.create();
                                //        dialog.setContentView(R.layout.dialog_loadpages); //don't use this
                                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                                    @Override
                                    public void onShow(DialogInterface dialogInterface) {
                                        BookActivity4Utils.runFullScreen(getActivity());
                                    }
                                });
                                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialogInterface) {

                                    }
                                });
                                dialog.show();
                            }
                        });
                    }
                }
            }).start();
        } catch (Throwable eee) {
            eee.printStackTrace();
            started = false;
            final TextView tvDiarizationLog = g_rootView.findViewById(R.id.tvDiarizationLog);
            if (tvDiarizationLog != null) {
                tvDiarizationLog.setText("Diarization failed");
            }
        }
    }
    //must be static class
    public class MyCallback implements Function3<Integer, Integer, Long, Integer> {
        //JNI DETECTED ERROR IN APPLICATION: JNI GetObjectClass called with pending
        // exception java.lang.NoSuchMethodError:
        // no non-static method "Lcom/k2fsa/sherpa/onnx/MainActivity$MyCallback;.invoke(IIJ)Ljava/lang/Integer;"
        //must keep 2 invoke() functions here, I don't know why
        //TODO:don't remove this function!!!!!
        public Integer invoke(int numProcessedChunks, int numTotalChunks, long arg) {
            return invoke_(numProcessedChunks, numTotalChunks, arg);
        }

        @Override
        public Integer invoke(Integer numProcessedChunks, Integer numTotalChunks, Long arg) {
            return invoke_(numProcessedChunks, numTotalChunks, arg);
        }

        private Integer invoke_(int numProcessedChunks, int numTotalChunks, long arg) {
            double percent = 100.0 * numProcessedChunks / numTotalChunks;
            String progress = String.format("%.2f%%", percent);
            Log.i(TAG, progress);
            final TextView tvDiarizationLog = g_rootView.findViewById(R.id.tvDiarizationLog);
            if (tvDiarizationLog != null) {
                tvDiarizationLog.post(new Runnable() {
                    @Override
                    public void run() {
                        tvDiarizationLog.setText("progress:" + progress);
                    }
                });
            }
            return 0;
        }
    }
    public MyCallback myCallback = new MyCallback();
    private void saveDiarizationFile(OfflineSpeakerDiarizationSegment[] segments) {
        //this.dirUrl==file:///storage/emulated/0/txkjnote2/SKETCH_fa5d355e-ba40-40ff-b751-a39b1170661d
        //this.dirUrlPath==/storage/emulated/0/txkjnote2/SKETCH_fa5d355e-ba40-40ff-b751-a39b1170661d
        JSONObject content = new JSONObject();
        try {
            JSONArray arrSegments = new JSONArray();
            if (segments != null) {
                for (OfflineSpeakerDiarizationSegment seg : segments) {
                    JSONObject item = new JSONObject();
                    item.put("start", seg.getStart());
                    item.put("end", seg.getEnd());
                    item.put("speaker", seg.getSpeaker());
                    arrSegments.put(item);
                }
            }
            content.put("segments", arrSegments);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        String diaMeta = content.toString();
        if (this.dirUrlPath != null) {
            try {
                try {
                    String filePath = new File(this.dirUrlPath, BookActivity4Config.USE_DIARIZATION_RESULT_CONFIG).getAbsolutePath();
                    String fullFilePath = filePath;
                    Log.e(TAG, "saveDiarizationFile : " + fullFilePath);
                    if (diaMeta != null && diaMeta.length() > 0) {
                        OutputStream it = null;
                        try {
                            it = new FileOutputStream(fullFilePath);
                            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(it, StandardCharsets.UTF_8);
                            BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
                            bufferedWriter.write(diaMeta);
                            bufferedWriter.flush();
                        } catch (Throwable e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                if (it != null) {
                                    it.close();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (Throwable eee) {
                    eee.printStackTrace();
                }
            } catch (Throwable ee) {
                ee.printStackTrace();
            }
        }
    }
    private void loadDiarizationFile() {
        try {
            //this.dirUrl==file:///storage/emulated/0/txkjnote2/SKETCH_fa5d355e-ba40-40ff-b751-a39b1170661d
            //this.dirUrlPath==/storage/emulated/0/txkjnote2/SKETCH_fa5d355e-ba40-40ff-b751-a39b1170661d
            List<OfflineSpeakerDiarizationSegment> segmentList = new ArrayList<>();
            String content = null;
            if (this.dirUrlPath != null) {
                InputStream fis = null;
                InputStreamReader isr = null;
                BufferedReader reader = null;
                try {
                    String filePath = new File(this.dirUrlPath, BookActivity4Config.USE_DIARIZATION_RESULT_CONFIG).getAbsolutePath();
                    String fullFilePath = filePath;
                    Log.e(TAG, "loadDiarizationFile : " + fullFilePath);

                    fis = new FileInputStream(fullFilePath);
                    isr = new InputStreamReader(fis, "UTF-8");
                    reader = new BufferedReader(isr);
                    StringBuffer recentFilesBuffer = new StringBuffer();
                    while (true) {
                        String line = reader.readLine();
                        if (line != null) {
                            recentFilesBuffer.append(line);
                            recentFilesBuffer.append("\n");
                        } else {
                            break;
                        }
                    }
                    content = recentFilesBuffer.toString();
                } catch (IOException eee) {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (isr != null) {
                        try {
                            isr.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (fis != null) {
                        try {
                            fis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            if (content != null) {
                try {
                    JSONObject contentObj = new JSONObject(content);
                    JSONArray arrSegments = contentObj.getJSONArray("segments");
                    if (arrSegments != null) {
                        for (int i = 0; i < arrSegments.length(); ++i) {
                            JSONObject item = arrSegments.getJSONObject(i);
                            double start = item.optDouble("start");
                            double end = item.optDouble("end");
                            int speaker = item.optInt("speaker");

                            OfflineSpeakerDiarizationSegment seg =
                                    new OfflineSpeakerDiarizationSegment((float) start, (float) end, speaker);
                            segmentList.add(seg);
                        }
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
            if (adapter != null) {
                adapter.setDiarization(segmentList);
                adapter.notifyDataSetChanged();
            }
        } catch (Throwable eee) {
            eee.printStackTrace();
        }
    }

    public void setTypeASRTest() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
//        String savingTest = preferences.getString(BookActivity4Config.CONFIG_SAVING_TEST, null);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(BookActivity4Config.CONFIG_TYPE_ASR_TEST, type);
        editor.apply();
    }

    public int getTypeASRTest() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        int result = preferences.getInt(BookActivity4Config.CONFIG_TYPE_ASR_TEST, TYPE_USE_VOSK);
        return result;
    }

    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

//    @SuppressLint("GestureBackNavigation")
//    @Override
//    public void onBackPressed() {
//        if (SAVING_ASYNC) {
//            if (task == null) {
//                task = new SavingTask(true);
//                task.executeOnExecutor(newFixedThreadPool);
//            }
//        } else {
//            if (false) {
//                ensureSave();
//            } else {
//                savePageInMain(getPageIdx(), pageBmp);
//            }
//            super.onBackPressed();
//        }
//    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.book, menu);
//        return true;
//    }

    int isPenEraserBrush = 1; //0:Eraser;1:Pen;2:Brush
    //    boolean isEraser = false;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.pen) {
            onPen(g_rootView);
        } else if (item.getItemId() == R.id.eraser) {
            onEraser(g_rootView);
        } else if (item.getItemId() == R.id.undo) {
            onUndo();
        } else if (item.getItemId() == R.id.redo) {
            onRedo();
        } else if (item.getItemId() == R.id.grid) {
            if (SAVING_ASYNC) {
                try {
                    if (!SAVING_ASYNC_MULTI) {
                        if (task == null) {
                            task = new SavingTask(false, false);
                            task.executeOnExecutor(newFixedThreadPool);
                        }
                    } else {
                        task = new SavingTask(false, false);
                        task.executeOnExecutor(newFixedThreadPool);
                    }
                } catch (Throwable eee) {
                    eee.printStackTrace();
                }
            } else {
                gotoGridPage();
            }
        } else if (item.getItemId() == R.id.firstPage) {
                gotoFirstPage();
        } else if (item.getItemId() == R.id.prevPage) {
                gotoPrevPage();
        } else if (item.getItemId() == R.id.nextPage) {
                gotoNextPage();
        } else if (item.getItemId() == R.id.lastPage) {
                gotoLastPage();
        } else if (item.getItemId() == R.id.addPage) {
                addNewPageAndGo(true);
        } else if (item.getItemId() == R.id.share) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                share();
            }
        }
        return true;
    }
    private void onPenColor() {
        if (false) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (dialogInterface != null) {
                        dialogInterface.dismiss();
                    }
                }
            });
            builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    if (dialogInterface != null) {
                        dialogInterface.dismiss();
                    }
                }
            });
            builder.setView(R.layout.dialog_selectcolor);
            builder.setCancelable(true);
            final AlertDialog dialog = builder.create();
            //        dialog.setContentView(R.layout.dialog_loadpages); //don't use this
            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialogInterface) {
                    BookActivity4Utils.runFullScreen(getActivity());
                }
            });
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {

                }
            });
            dialog.show();
        } else if (false) {
            MaterialColorDialog dialog = new MaterialColorDialog(getActivity());
            dialog.show();
        } else {
            SimpleColorDialog dialog = new SimpleColorDialog(getActivity());
            dialog.show();
        }
    }
    private void onSelectPaint() {
        PaintSelectDialog dialog = new PaintSelectDialog(getActivity());
        dialog.show();
    }

    private void onPen2(View rootView) {
        if (isPenEraserBrush != 1) {
            isPenEraserBrush = 1;
            selectPenOrEraser(rootView, 1);
            setPenEraserBrush(canvas, 1);
        }
        if (rootView.findViewById(R.id.bookTab).getVisibility() == View.VISIBLE) { //debug mode
            onLineThicknessButton(rootView);
        }
    }
    private void onEraser2(View rootView) {
        if (isPenEraserBrush != 0) {
            isPenEraserBrush = 0;
            selectPenOrEraser(rootView, 0);
            setPenEraserBrush(canvas,0);
        }
    }

    private void onPen(View rootView) {
//        if (!isEraser) {
//            isEraser = true;
//            selectPenOrEraser(false);
//            canvas.penOrEraser(false);
//        } else {
//            isEraser = false;
//            selectPenOrEraser(true);
//            canvas.penOrEraser(true);
//        }
        if (isPenEraserBrush != 1) {
            isPenEraserBrush = 1;
            //selectPenOrEraser(true);
            selectPenOrEraser(rootView, 1);
            //canvas.penOrEraser(true);
            setPenEraserBrush(canvas, 1);
        }
    }
    private void onBrush(View rootView) {
        if (isPenEraserBrush != 2) {
            isPenEraserBrush = 2;
            //selectPenOrEraser(false);
            selectPenOrEraser(rootView,2);
            //canvas.penOrEraser(false);
            setPenEraserBrush(canvas, 2);
        }
    }
    private void onEraser(View rootView) {
//        if (isEraser) {
//            isEraser = false;
//            selectPenOrEraser(true);
//            canvas.penOrEraser(true);
//        } else {
//            isEraser = true;
//            selectPenOrEraser(false);
//            canvas.penOrEraser(false);
//        }
        if (isPenEraserBrush != 0) {
            isPenEraserBrush = 0;
            //selectPenOrEraser(false);
            selectPenOrEraser(rootView, 0);
//            canvas.penOrEraser(false);
            setPenEraserBrush(canvas,0);
        }
    }
    //    private void selectPenOrEraser(boolean selectPen) {
//        if (selectPen) {
//            findViewById(R.id.buttonPen).setEnabled(false);
//            findViewById(R.id.buttonEraser).setEnabled(true);
//            ((ImageButton)findViewById(R.id.buttonPen)).setImageResource(R.drawable.ic_baseline_edit_24);
//            ((ImageButton)findViewById(R.id.buttonEraser)).setImageResource(R.mipmap.eraser_button_selected);
//        } else {
//            findViewById(R.id.buttonPen).setEnabled(true);
//            findViewById(R.id.buttonEraser).setEnabled(false);
//            ((ImageButton)findViewById(R.id.buttonPen)).setImageResource(R.drawable.ic_baseline_edit_24_selected);
//            ((ImageButton)findViewById(R.id.buttonEraser)).setImageResource(R.mipmap.eraser_button);
//        }
//    }
    private void selectPenOrEraser(View rootView, int v) {
        if (v == 1) {
            rootView.findViewById(R.id.buttonPen).setEnabled(false);
            rootView.findViewById(R.id.buttonEraser).setEnabled(true);
            rootView.findViewById(R.id.buttonBrush).setEnabled(true);
            ((ImageButton) rootView.findViewById(R.id.buttonPen)).setImageResource(R.drawable.ic_baseline_edit_24);
            ((ImageButton) rootView.findViewById(R.id.buttonEraser)).setImageResource(R.mipmap.eraser_button_selected);
            ((ImageButton) rootView.findViewById(R.id.buttonBrush)).setImageResource(R.drawable.ic_baseline_brush_24_selected);
        } else if (v == 2) {
            rootView.findViewById(R.id.buttonPen).setEnabled(true);
            rootView.findViewById(R.id.buttonEraser).setEnabled(true);
            rootView.findViewById(R.id.buttonBrush).setEnabled(false);
            ((ImageButton) rootView.findViewById(R.id.buttonPen)).setImageResource(R.drawable.ic_baseline_edit_24_selected);
            ((ImageButton) rootView.findViewById(R.id.buttonEraser)).setImageResource(R.mipmap.eraser_button_selected);
            ((ImageButton) rootView.findViewById(R.id.buttonBrush)).setImageResource(R.drawable.ic_baseline_brush_24);
        } else if (v == 0){
            rootView.findViewById(R.id.buttonPen).setEnabled(true);
            rootView.findViewById(R.id.buttonEraser).setEnabled(false);
            rootView.findViewById(R.id.buttonBrush).setEnabled(true);
            ((ImageButton) rootView.findViewById(R.id.buttonPen)).setImageResource(R.drawable.ic_baseline_edit_24_selected);
            ((ImageButton) rootView.findViewById(R.id.buttonEraser)).setImageResource(R.mipmap.eraser_button);
            ((ImageButton) rootView.findViewById(R.id.buttonBrush)).setImageResource(R.drawable.ic_baseline_brush_24_selected);
        }

        if (v == 1) {
            ((CardView) rootView.findViewById(R.id.buttonPen2Wrapper)).setCardBackgroundColor(0xFFCCCCCC);
            ((CardView) rootView.findViewById(R.id.buttonEraser2Wrapper)).setCardBackgroundColor(0xFFFFFFFF);
        } else {
            ((CardView) rootView.findViewById(R.id.buttonPen2Wrapper)).setCardBackgroundColor(0xFFFFFFFF);
            ((CardView) rootView.findViewById(R.id.buttonEraser2Wrapper)).setCardBackgroundColor(0xFFCCCCCC);
        }
    }

    private void onUndo() {
        if (!g_isUndoActive) {
            return;
        }
        undoCount = undoCount + 1;
        if (isCanRestorePages()) {
            createWaitingProgressDialog();
            g_rootView.findViewById(R.id.buttonUndo).postDelayed(new Runnable() {
                @Override
                public void run() {
                    undoAndRestorePages();
                    canvas.undo(true);
                    cancelWaitingProgressDialog();
                }
            }, 0);//100);
        } else {
            canvas.undo(false);
        }
    }
    private void onRedo() {
        if (!g_isRedoActive) {
            return;
        }
        redoCount = redoCount + 1;
        canvas.redo();
    }
    private void onUpdatePidxPnum() {
        if (textViewPageInfo != null) {
            textViewPageInfo.post(new Runnable() {
                @Override
                public void run() {
                    textViewPageInfo.setText("" + (getPageIdx() + 1) + "/" + pageNum);
                };
            });
        }
    }


    private void handlePageIdxArg(Bundle intent) {
        //record in book, don't use this
        if (false) {
            int argPageIdx = intent.getInt(BookActivity4Utils.PAGE_IDX, -1);
            if (argPageIdx != -1) {
                this.initialPageIdx = argPageIdx;
            }
        }
    }



    public void setPenColor(int color) {
//        ImageButton buttonPenColor = (ImageButton) this.findViewById(R.id.buttonPenColor);
//
//        @SuppressLint("UseCompatLoadingForDrawables")
//        Drawable it = this.getDrawable(R.drawable.ic_baseline_color_lens_24);
//        DrawableCompat.setTintList(it, ColorStateList.valueOf(color));
//        buttonPenColor.setImageDrawable(it);
        CardView cvColorSel = g_rootView.findViewById(R.id.cvColorSel);
        cvColorSel.setCardBackgroundColor(0xff000000 | color);

        setColor(canvas, 0xff000000 | color);
    }

    public void onPaintSelect(String backText) {
        this.backText = backText;
        setBackText(canvas, backText); //FIXME: added
        addNewPageAndGo_old();
    }


    //-----------------------
    //search R.id.full_screen

    public static void runFullScreen(final Activity a) {
        try {
            a.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            a.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

            if (true) { //FIXME:???
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    a.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                    a.getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER;
                    a.getWindow().setAttributes(a.getWindow().getAttributes());
                }
            }

            KeyboardsMod.hideNavigation(a);
            //KeyboardsMod.hideNavigationOnCreate(a);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void runFullScreenCutOut(final Activity a) {
        try {

            setNavBarTintColor(a);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {


                a.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                a.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

                a.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                a.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

                a.getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
                a.getWindow().setAttributes(a.getWindow().getAttributes());


            }


            KeyboardsMod.hideNavigation(a);


        } catch (Exception e) {
//            LOG.e(e);
            e.printStackTrace();
        }
    }

    public static void setNavBarTintColor(Activity a) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //FIXME:
            int color = 0xFFFCFCFC;
            a.getWindow().setNavigationBarColor(color);//TintUtil.color);
            setStatusBarLightMode(a, true);
        }
    }

    @TargetApi(23)
    public static boolean setStatusBarLightMode(Activity activity, boolean isFontColorDark) {
//        Window window = activity.getWindow();
//        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//        window.setStatusBarColor(ContextCompat.getColor(activity, android.R.color.transparent));
        if (isFontColorDark) {
            //Status bar is Translucent
            //View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            //Status bar not Translucent
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        }
        return true;
    }

    public static void runNormalScreen(final Activity a) {
        try {
            a.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            a.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

            final View decorView = a.getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

            setNavBarTintColor(a);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                a.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                a.getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT;
                a.getWindow().setAttributes(a.getWindow().getAttributes());
            }


        } catch (Exception e) {
//            LOG.e(e);
            e.printStackTrace();
        }
    }


    //search R.id.full_screen
    //-----------------------












    public class SavingTask extends AsyncTask<Void, Void, Void> {
        private boolean mIsBack = true;
        private boolean mDoNothing = true;
        private long mTime = 0;
        public SavingTask(boolean isBack, boolean doNothing) {
            if (isBack || doNothing) {
                //TODO:
                //https://github.com/antwankakki/FabricView/wiki
                if (mOnUpdateBmpListener != null) {
                    Bitmap fullResult = getCanvasBitmap(canvas);
                    mOnUpdateBmpListener.onUpdateBmp(fullResult);
                }
            }
            this.mIsBack = isBack;
            this.mDoNothing = doNothing;
            if (this.mDoNothing) {
                //skip
            } else {
                createWaitingProgressDialog();
            }
            if (SAVING_ASYNC_MULTI) {
                if (D) {
                    Log.e(TAG, "SAVING_ASYNC_MULTI SavingTask");
                    mTime = System.currentTimeMillis();
                }
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            boolean locked = false;
            try {
                if (SAVING_ASYNC_MULTI) {
                    locked = saveLock.tryLock(SAVING_ASYNC_MULTI_TIMEOUT, TimeUnit.SECONDS);
                    if (locked) {
                        savePageInMain(getPageIdx(), pageBmp, getVecJson(canvas));
                    }
                } else {
                    savePageInMain(getPageIdx(), pageBmp, getVecJson(canvas));
                }
            } catch (Throwable eee) {
                eee.printStackTrace();
            } finally {
                if (locked) {
                    saveLock.unlock();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            if (SAVING_ASYNC_MULTI) {
                if (D) {
                    //about 2164ms
                    Log.e(TAG, "SAVING_ASYNC_MULTI onPostExecute " + (System.currentTimeMillis() - mTime));
                }
            }
            task = null;
            if (this.mDoNothing) {
                //skip
            } else {
                if (false) {
                    cancelWaitingProgressDialog();
                } else {
                    //move to onStop()
                }
                if (this.mIsBack) {
                    BookActivity4Utils.finish(getActivity(), true);
                } else {
                    gotoGridPage();
                }
            }
        }
    }

    private SavingTask task = null;
    private ExecutorService newFixedThreadPool;
    protected void createWaitingProgressDialog() {
        BookActivity4Utils.createWaitingProgressDialog(getActivity());
    }
    protected void cancelWaitingProgressDialog() {
        BookActivity4Utils.cancelWaitingProgressDialog(getActivity());
    }


    private float mCurrentInkLineThickness = 4.5f;
    public float getInkLineThickness() {
        return mCurrentInkLineThickness;
    }
    public void setInkLineThickness(float val) {
        mCurrentInkLineThickness=val;
        setSize(canvas, val);
    }
    private void onLineThicknessButton(View rootView)  {
        float val = getInkLineThickness();
        ImageButton btn = (ImageButton) rootView.findViewById(R.id.buttonPen2);
        LineWidthDialog.show(getActivity(), btn, val,
                new LineWidthDialog.WidthChangedListener() {
                    @Override
                    public void onWidthChanged(float value) {
                        setInkLineThickness(value);
                    }
                });
    }

//--------------------------------
    //FIXME:not good
    private void setPenEraserBrush(DrawCanvas canvas, int index) {
        if (index == 0) {
            canvas.setTool(DrawCanvas.TOOLS.paint); //eraser
        } else if (index == 1) {
            canvas.setTool(DrawCanvas.TOOLS.paint); //pen 1
        } else if (index == 2) {
            canvas.setTool(DrawCanvas.TOOLS.paint); //pen 2
        }
        canvas.penEraserBrush = index;
    }
    CanvasBoox.OnUpdateBmpListener mOnUpdateBmpListener;
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (false) {
            //TODO:
            //https://github.com/antwankakki/FabricView/wiki
            if (mOnUpdateBmpListener != null && canvas != null) {
                Bitmap fullResult = getCanvasBitmap(canvas);
                mOnUpdateBmpListener.onUpdateBmp(fullResult);
            }
        }
    }
    private void setOnUpdateListener(DrawCanvas canvas, CanvasBoox.OnUpdateBmpListener listener) {
        mOnUpdateBmpListener = listener;
    }
    private void setOnUndoStateListener(DrawCanvas canvas, CanvasBoox.OnUndoStateListener listener) {
        //TODO:
    }
    private void onPageIdx(DrawCanvas canvas, int idx, CanvasBoox.OnLoadBitmapListener bitmapLoader, boolean forceReload) {
        //TODO:
        if (canvas != null) {
            canvas.onPageIdx(idx, bitmapLoader, forceReload);
        }
    }
    private void setBackText(DrawCanvas canvas, String backText) {
        //TODO:
        if (backText != null && canvas != null) {
            if (backText.equals(FileMeta.NONE)) {
                setBackgroundMode(canvas, FileMeta.NONE);//FabricView.BACKGROUND_STYLE_BLANK);
            } else if (backText.equals(FileMeta.LINED)) {
                setBackgroundMode(canvas, FileMeta.LINED);//FabricView.BACKGROUND_STYLE_NOTEBOOK_PAPER);
            } else if (backText.equals(FileMeta.LINED_LONG_DASH)) {
                setBackgroundMode(canvas, FileMeta.LINED_LONG_DASH);//FabricView.BACKGROUND_STYLE_NOTEBOOK_PAPER);
            } else if (backText.equals(FileMeta.LINED_SHORT_DASH)) {
                setBackgroundMode(canvas, FileMeta.LINED_SHORT_DASH);//FabricView.BACKGROUND_STYLE_NOTEBOOK_PAPER);
            } else if (backText.equals(FileMeta.DOTTED)) {
                setBackgroundMode(canvas, FileMeta.DOTTED);//FabricView.BACKGROUND_STYLE_DOT_PAPER);
            } else if (backText.equals(FileMeta.GRAPH)) {
                setBackgroundMode(canvas, FileMeta.GRAPH);//FabricView.BACKGROUND_STYLE_GRAPH_PAPER);
            }
        }
    }
    private void init(DrawCanvas canvas, Bitmap initialBmp, Bitmap background, int initialPageIdx, Activity act) {
        //TODO:
//        if (canvas != null && background != null) {
//            canvas.drawImage(0, 0, background.getWidth(), background.getHeight(), background);
//        }
        canvas.initialBmp = initialBmp;
        canvas.pageIdx = initialPageIdx;
//        if (canvas != null && initialBmp != null) {
//            canvas.drawImage(0, 0, initialBmp.getWidth(), initialBmp.getHeight(), initialBmp);
//        }
    }
    private void drawText(DrawCanvas canvas, String text, int textType, float x, float y, Paint p_, boolean isBold,
                          boolean isItalics,
                          boolean isUnderline,
                          int styleType, int pointsTextColor, float pointsTextSize) {
        if (canvas != null) {
            canvas.drawText(text, textType, x, y, p_, isBold,
                isItalics,
                isUnderline,
                styleType, pointsTextColor, pointsTextSize);
        }
    }
    private DrawPath drawImage(DrawCanvas canvas, int x, int y, int width, int height, Bitmap pic, boolean needMap) {
        if (canvas != null) {
            return canvas.drawImage(x, y, width, height, pic, needMap);
        }
        return null;
    }
    private Bitmap getCanvasBitmap(DrawCanvas canvas) {
        return canvas != null ? canvas.toBitmap(false) : null;
    }
    private void setColor(DrawCanvas canvas, int color) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("strokeColor", color); //"strokeColor" or "fillColor"
        editor.apply();
    }
    public void setSize(DrawCanvas canvas, float size) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("strokeSize", Float.toString(size)); //"strokeColor" or "fillColor"
        editor.apply();
    }
    private void setBackgroundMode(DrawCanvas canvas, String mode) {
        canvas.setBackgroundMode(mode);
    }
    public void updateInfoBar() {

    }

    boolean isLoadingPageGrid = false;
    private void showPopupMenu(View rootView, View view) {
        //see LineWidthDialog
        if (CopyCutMenuDialog.isOpen == false) {
            CopyCutMenuDialog.isOpen = true;
            CopyCutMenuDialog.show(getActivity(), view, (getPageIdx() + 1), pageNum, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (view != null) {
                        if (view.getId() == R.id.popButtonGrid) {
                            if (false) {
                                notifyForceSave(false);
                                if (SAVING_ASYNC) {
                                    try {
                                        if (!SAVING_ASYNC_MULTI) {
                                            if (task == null) {
                                                task = new SavingTask(false, false);
                                                task.executeOnExecutor(newFixedThreadPool);
                                            }
                                        } else {
                                            task = new SavingTask(false, false);
                                            task.executeOnExecutor(newFixedThreadPool);
                                        }
                                    } catch (Throwable eee) {
                                        eee.printStackTrace();
                                    }
                                } else {
                                    gotoGridPage();
                                }
                            } else {
                                if (!isLoadingPageGrid) {
                                    isLoadingPageGrid = true;
                                    createWaitingProgressDialog();
                                    view.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            isLoadingPageGrid = false;
                                            beforePageGrid();
                                            AlertDialog dialog = new BookActivity4PageGridDialog(getActivity(),
                                                    BookActivity4Fragment.this.dirUrl,
                                                    BookActivity4Fragment.this.dirUrlPath)
                                                    .create();
                                            dialog.show();
                                            cancelWaitingProgressDialog();
                                        }
                                    }, 0);//100);
                                }
                            }
                        } else if (view.getId() == R.id.popButtonPrevPage) {
                            notifyForceSave(false);
                            gotoPrevPage();
                        } else if (view.getId() == R.id.popButtonNextPage) {
                            notifyForceSave(false);
                            gotoNextPage();
                        } else if (view.getId() == R.id.popButtonRemovePage) {
                            notifyForceSave(false);
                            removeCurrentPageAndGo();
                        } else if (view.getId() == R.id.popButtonAddPage) {
                            notifyForceSave(false);
                            //addNewPageAndGo(true);
                            addNewPageAndGo(false);
                        } else if (view.getId() == R.id.popButtonPan) {
                            if (canvas != null) {
                                canvas.setTool(DrawCanvas.TOOLS.pan);
                            }
                        } else if (view.getId() == R.id.popButtonShare) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                notifyForceSave(true);
                                //isDrawBG == true, because I want to draw lined bg
                                share();
                            } else {
                                AlertDialog dialog = new MaterialAlertDialogBuilder(getActivity(), BookActivity4Utils.getCenteredTitleThemeOverlay())
                                        .setTitle("Error")
                                        .setMessage("Sharing failed. Requires Android 8.0 or above.")
                                        .setCancelable(true)
                                        .setPositiveButton("OK", null)
                                        .show();
                            }
                        } else if (view.getId() == R.id.popTextViewInsertImage) {
                            if (false) {
                                loadFileDemo();
                            } else {
                                if (canvas != null) {
                                    canvas.disableCenter = true;
                                }
                                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                                intent.addCategory(Intent.CATEGORY_OPENABLE);
                                intent.setType("image/*");
                                intent.putExtra(Intent.EXTRA_TITLE, "input.png");
                                intent = Intent.createChooser(intent, "Load image file");
                                activityResultLauncherLoad.launch(intent);
                            }
                        } else if (view.getId() == R.id.popButtonShortcut) {
                            AlertDialog dialog = new BookActivity4TipsDialog(getActivity()).create();
                            dialog.show();
                        } else if (view.getId() == R.id.popTextViewCopy) {
                            if (canvas != null) {
                                copyPaths.clear();
                                LinkedList<DrawPath> selectedPaths = canvas.getSelectionTool().getSelectedPaths();
                                for (DrawPath drawPath : selectedPaths) {
                                    copyPaths.add(drawPath.clone());
                                }
                                canvas.getSelectionTool().exitSelect();
                                canvas.invalidate();
                            }
                        } else if (view.getId() == R.id.popTextViewPaste) {
                            if (canvas != null) {
                                canvas.paths.addAll(copyPaths);
                                canvas.versions.add(canvas.cloneDrawPathList(copyPaths));
                                canvas.version_index += 1;
                                canvas.onVersionChanged();
                                clearRestorePages();
                                copyPaths.clear();
                                canvas.invalidate();
                            }
                        } else if (view.getId() == R.id.popTextViewCut) {
                            if (canvas != null) {
                                LinkedList<DrawPath> selectedPaths = canvas.getSelectionTool().getSelectedPaths();
                                copyPaths.clear();
                                for (DrawPath drawPath : selectedPaths) {
                                    copyPaths.add(drawPath.clone());
                                    drawPath.clear();
                                }
                                canvas.getSelectionTool().exitSelect();
                                canvas.invalidate();
                            }
                        } else if (view.getId() == R.id.popTextViewRenameFile) {
                            AlertDialog dialog = new BookActivity4RenameDialog(getActivity(), _bookDir.getDisplayName())
                                    .create();
                            if (dialog != null) {
                                dialog.show();
                            }
                        } else if (view.getId() == R.id.popTextViewPageBackground) {
                            String backgroundMode = null;//-1;
                            if (canvas != null) {
                                backgroundMode = canvas.getBackgroundMode();
                            }
                            AlertDialog dialog = new BookActivity4BackgroundDialog(getActivity(), backgroundMode)
                                    .create();
                            if (dialog != null) {
                                dialog.show();
                            }
                        } else if (view.getId() == R.id.popTextViewTranscriptLanguage) {
                            AlertDialog dialog = new BookActivity4TranscriptionLanguage(getActivity())
                                    .create();
                            if (dialog != null) {
                                dialog.show();
                            }
                        }
                    }
                }
            }, this.copyPaths != null && !this.copyPaths.isEmpty());
        }
    }

    public void setBookBackText(String backText_) {
        if (backText_ != null) {
            setBackText(canvas, backText_);
        }
        if (BookIO.USE_META_TXT) {
            if (false) {
                getBookIO().saveMeta(backText_, BookActivity4Fragment.this.dirUrlPath,
                        String.format("%04d", BookActivity4Fragment.this.pageNum - 1) + ".meta");
            } else {
                try {
                    List<FastFile> pages = this.getBook().getPages();
                    FastFile file = pages.get(this._pageIdx);
                    getBookIO().saveMeta(backText_, BookActivity4Fragment.this.dirUrlPath,
                            new File(file.getFilePath()).getName().replace(".png", ".meta"));
                } catch (Throwable eee) {
                    eee.printStackTrace();
                }
            }
        }
    }

    public void renameBook(String newName) {
        boolean isFailed = false;
        if (_bookDir == null || _bookDir.getName() == null ||!_bookDir.getName().startsWith(BookActivity4Config.USE_SKETCH_PREFIX)) {
            isFailed = true;
        }
        String folder = _bookDir.getFilePath();
        if (folder == null ||
                !new File(folder, BookActivity4Config.USE_SKETCH_CONFIG).exists() ||
                !new File(folder, BookActivity4Config.USE_SKETCH_CONFIG).canWrite()
        ) {
            isFailed = true;
        }
        try {
            File file_2 = new File(folder, BookActivity4Config.USE_SKETCH_CONFIG);
            String str = FastFile.loadMetaText(file_2);
            JSONObject item = new JSONObject();
            try {
                item = new JSONObject(str);
            } catch (Throwable eee) {
                eee.printStackTrace();
            }
            item.put(BookActivity4Config.USE_SKETCH_CONFIG_DISPNAME, newName);
            FastFile.saveMetaText(file_2, item.toString(), getActivity());
        } catch (JSONException e) {
            e.printStackTrace();
            isFailed = true;
        }
        if (isFailed) {
            new MaterialAlertDialogBuilder(getActivity(), BookActivity4Utils.getCenteredTitleThemeOverlay())
                    .setTitle("Error")
//                    .setMessage("Rename failed : " + _bookDir.getFilePath() + ",\n" +
//                            "please check the path starts with '" + BookActivity4Config.USE_SKETCH_PREFIX + "' prefix, " +
//                            "and make sure " + BookActivity4Config.USE_SKETCH_CONFIG + " file exists.")
                    .setMessage("Renaming failed")
                    .setPositiveButton("OK", null)
                    .show();
        } else {
            onCreateAct(g_rootView);
            try {
                ((TextView) g_rootView.findViewById(R.id.newTitle)).setText(_bookDir.getDisplayName());
            } catch (Throwable eee) {
                eee.printStackTrace();
            }
        }
    }

    public void editMeetingDurationTemp(String newDuration) {
        boolean isFailed = false;
        if (_bookDir == null || _bookDir.getName() == null ||!_bookDir.getName().startsWith(BookActivity4Config.USE_SKETCH_PREFIX)) {
            isFailed = true;
        }
        String folder = _bookDir.getFilePath();
        if (folder == null ||
                !new File(folder, BookActivity4Config.USE_SKETCH_CONFIG).exists() ||
                !new File(folder, BookActivity4Config.USE_SKETCH_CONFIG).canWrite()
        ) {
            isFailed = true;
        }
        try {
            File file_2 = new File(folder, BookActivity4Config.USE_SKETCH_CONFIG);
            String str = FastFile.loadMetaText(file_2);
            JSONObject item = new JSONObject(str);
            item.put(BookActivity4Config.USE_SKETCH_CONFIG_MEETING_DURATION_TEMP, newDuration);
            FastFile.saveMetaText(file_2, item.toString(), getActivity());
        } catch (JSONException e) {
            e.printStackTrace();
            isFailed = true;
        }
        if (isFailed) {
            //failed
        } else {
            //success
        }
    }


    public void editMeetingDuration(String newDuration) {
        boolean isFailed = false;
        if (_bookDir == null || _bookDir.getName() == null ||!_bookDir.getName().startsWith(BookActivity4Config.USE_SKETCH_PREFIX)) {
            isFailed = true;
        }
        String folder = _bookDir.getFilePath();
        if (folder == null ||
                !new File(folder, BookActivity4Config.USE_SKETCH_CONFIG).exists() ||
                !new File(folder, BookActivity4Config.USE_SKETCH_CONFIG).canWrite()
        ) {
            isFailed = true;
        }
        try {
            File file_2 = new File(folder, BookActivity4Config.USE_SKETCH_CONFIG);
            String str = FastFile.loadMetaText(file_2);
            JSONObject item = new JSONObject(str);
            item.put(BookActivity4Config.USE_SKETCH_CONFIG_MEETING_DURATION, newDuration);
            FastFile.saveMetaText(file_2, item.toString(), getActivity());
        } catch (JSONException e) {
            e.printStackTrace();
            isFailed = true;
        }
        if (isFailed) {
            new MaterialAlertDialogBuilder(getActivity(), BookActivity4Utils.getCenteredTitleThemeOverlay())
                    .setTitle("Error")
                    .setMessage("Edit meeting duration failed")
                    .setPositiveButton("OK", null)
                    .show();
        } else {
            onCreateAct(g_rootView);
            try {
                String duration = getMeetingDuration();
                if (duration != null && duration.length() > 0) {
                    int durationVal = -1;
                    try {
                        durationVal = Integer.parseInt(duration);
                    } catch (Throwable eee) {
                        eee.printStackTrace();
                    }
                    if (durationVal > 1) {
                        ((TextView) g_rootView.findViewById(R.id.tvMeetingDuration)).setText("" + duration + " minutes");
                    } else {
                        ((TextView) g_rootView.findViewById(R.id.tvMeetingDuration)).setText("" + duration + " minute");
                    }
                } else {
                    ((TextView) g_rootView.findViewById(R.id.tvMeetingDuration)).setText("");
                }
            } catch (Throwable eee) {
                eee.printStackTrace();
            }
            updateRecordButtonStatus();
        }
    }

    public void editMeetingSummary(String newSummary) {
        boolean isFailed = false;
        if (_bookDir == null || _bookDir.getName() == null ||!_bookDir.getName().startsWith(BookActivity4Config.USE_SKETCH_PREFIX)) {
            isFailed = true;
        }
        String folder = _bookDir.getFilePath();
        if (folder == null ||
                !new File(folder, BookActivity4Config.USE_SKETCH_CONFIG).exists() ||
                !new File(folder, BookActivity4Config.USE_SKETCH_CONFIG).canWrite()
        ) {
            isFailed = true;
        }
        try {
            File file_2 = new File(folder, BookActivity4Config.USE_SKETCH_CONFIG);
            String str = FastFile.loadMetaText(file_2);
            JSONObject item = new JSONObject(str);
            item.put(BookActivity4Config.USE_SKETCH_CONFIG_MEETING_SUMMARY, newSummary);
            FastFile.saveMetaText(file_2, item.toString(), getActivity());
        } catch (JSONException e) {
            e.printStackTrace();
            isFailed = true;
        }
        if (isFailed) {
            new MaterialAlertDialogBuilder(getActivity(), BookActivity4Utils.getCenteredTitleThemeOverlay())
                    .setTitle("Error")
                    .setMessage("Edit meeting summary failed")
                    .setPositiveButton("OK", null)
                    .show();
        } else {
            onCreateAct(g_rootView);
            try {
                ((TextView) g_rootView.findViewById(R.id.tvMeetingSummary)).setText(getMeetingSummary());
            } catch (Throwable eee) {
                eee.printStackTrace();
            }
        }
    }

    public void editMeetingDate(String dateStr, Long dateVal) {
        boolean isFailed = false;
        if (_bookDir == null || _bookDir.getName() == null ||!_bookDir.getName().startsWith(BookActivity4Config.USE_SKETCH_PREFIX)) {
            isFailed = true;
        }
        String folder = _bookDir.getFilePath();
        if (folder == null ||
                !new File(folder, BookActivity4Config.USE_SKETCH_CONFIG).exists() ||
                !new File(folder, BookActivity4Config.USE_SKETCH_CONFIG).canWrite()
        ) {
            isFailed = true;
        }
        try {
            File file_2 = new File(folder, BookActivity4Config.USE_SKETCH_CONFIG);
            String str = FastFile.loadMetaText(file_2);
            JSONObject item = new JSONObject(str);
            item.put(BookActivity4Config.USE_SKETCH_CONFIG_MEETING_DATE, dateVal);
            FastFile.saveMetaText(file_2, item.toString(), getActivity());
        } catch (JSONException e) {
            e.printStackTrace();
            isFailed = true;
        }
        if (isFailed) {
            new MaterialAlertDialogBuilder(getActivity(), BookActivity4Utils.getCenteredTitleThemeOverlay())
                    .setTitle("Error")
                    .setMessage("Edit meeting date failed")
                    .setPositiveButton("OK", null)
                    .show();
        } else {
            onCreateAct(g_rootView);
            try {
                long newDateVal = getMeetingDate();
                if (newDateVal > 0) {
                    Date newDate = new Date(newDateVal);
                    SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());//Locale.ENGLISH);
                    String dateStr_ = sdf.format(newDate);
                    ((TextView) g_rootView.findViewById(R.id.tvMeetingDate)).setText(dateStr_);
                } else {
                    ((TextView) g_rootView.findViewById(R.id.tvMeetingDate)).setText("");
                }
            } catch (Throwable eee) {
                eee.printStackTrace();
            }
            updateRecordButtonStatus();
        }
    }

    public void editMeetingTime(int hour, int minute) {
        boolean isFailed = false;
        if (_bookDir == null || _bookDir.getName() == null ||!_bookDir.getName().startsWith(BookActivity4Config.USE_SKETCH_PREFIX)) {
            isFailed = true;
        }
        String folder = _bookDir.getFilePath();
        if (folder == null ||
                !new File(folder, BookActivity4Config.USE_SKETCH_CONFIG).exists() ||
                !new File(folder, BookActivity4Config.USE_SKETCH_CONFIG).canWrite()
        ) {
            isFailed = true;
        }
        try {
            File file_2 = new File(folder, BookActivity4Config.USE_SKETCH_CONFIG);
            String str = FastFile.loadMetaText(file_2);
            JSONObject item = new JSONObject(str);
            item.put(BookActivity4Config.USE_SKETCH_CONFIG_MEETING_HOUR, hour);
            item.put(BookActivity4Config.USE_SKETCH_CONFIG_MEETING_MINUTE, minute);
            FastFile.saveMetaText(file_2, item.toString(), getActivity());
        } catch (JSONException e) {
            e.printStackTrace();
            isFailed = true;
        }
        if (isFailed) {
            new MaterialAlertDialogBuilder(getActivity(), BookActivity4Utils.getCenteredTitleThemeOverlay())
                    .setTitle("Error")
                    .setMessage("Edit meeting time failed")
                    .setPositiveButton("OK", null)
                    .show();
        } else {
            onCreateAct(g_rootView);
            try {
                Integer newDateHour = getMeetingHour();
                Integer newDateMinute = getMeetingMinute();
                String dateStr_ = "";
                if (newDateHour != null && newDateMinute != null) {
                    dateStr_ = String.format("%02d:%02d", newDateHour, newDateMinute);
                    ((TextView) g_rootView.findViewById(R.id.tvMeetingTime)).setText(dateStr_);
                } else {
                    ((TextView) g_rootView.findViewById(R.id.tvMeetingTime)).setText(dateStr_);
                }
            } catch (Throwable eee) {
                eee.printStackTrace();
            }
            updateRecordButtonStatus();
        }
    }

    public String getMeetingSummary() {
        String newSummary = "";
        boolean isFailed = false;
        if (_bookDir == null || _bookDir.getName() == null ||!_bookDir.getName().startsWith(BookActivity4Config.USE_SKETCH_PREFIX)) {
            isFailed = true;
        }
        String folder = _bookDir.getFilePath();
        if (folder == null ||
                !new File(folder, BookActivity4Config.USE_SKETCH_CONFIG).exists() ||
                !new File(folder, BookActivity4Config.USE_SKETCH_CONFIG).canWrite()
        ) {
            isFailed = true;
        }
        try {
            File file_2 = new File(folder, BookActivity4Config.USE_SKETCH_CONFIG);
            String str = FastFile.loadMetaText(file_2);
            JSONObject item = new JSONObject(str);
            newSummary = item.optString(BookActivity4Config.USE_SKETCH_CONFIG_MEETING_SUMMARY, "");
        } catch (JSONException e) {
            e.printStackTrace();
            isFailed = true;
        }
        return newSummary;
    }

    public String getMeetingDuration() {
        String newSummary = "";
        boolean isFailed = false;
        if (_bookDir == null || _bookDir.getName() == null ||!_bookDir.getName().startsWith(BookActivity4Config.USE_SKETCH_PREFIX)) {
            isFailed = true;
        }
        String folder = _bookDir.getFilePath();
        if (folder == null ||
                !new File(folder, BookActivity4Config.USE_SKETCH_CONFIG).exists() ||
                !new File(folder, BookActivity4Config.USE_SKETCH_CONFIG).canWrite()
        ) {
            isFailed = true;
        }
        try {
            File file_2 = new File(folder, BookActivity4Config.USE_SKETCH_CONFIG);
            String str = FastFile.loadMetaText(file_2);
            JSONObject item = new JSONObject(str);
            newSummary = item.optString(BookActivity4Config.USE_SKETCH_CONFIG_MEETING_DURATION, "");
        } catch (JSONException e) {
            e.printStackTrace();
            isFailed = true;
        }
        return newSummary;
    }

    public String getMeetingDurationTemp() {
        String newSummary = "";
        boolean isFailed = false;
        if (_bookDir == null || _bookDir.getName() == null ||!_bookDir.getName().startsWith(BookActivity4Config.USE_SKETCH_PREFIX)) {
            isFailed = true;
        }
        String folder = _bookDir.getFilePath();
        if (folder == null ||
                !new File(folder, BookActivity4Config.USE_SKETCH_CONFIG).exists() ||
                !new File(folder, BookActivity4Config.USE_SKETCH_CONFIG).canWrite()
        ) {
            isFailed = true;
        }
        try {
            File file_2 = new File(folder, BookActivity4Config.USE_SKETCH_CONFIG);
            String str = FastFile.loadMetaText(file_2);
            JSONObject item = new JSONObject(str);
            newSummary = item.optString(BookActivity4Config.USE_SKETCH_CONFIG_MEETING_DURATION_TEMP, "");
        } catch (JSONException e) {
            e.printStackTrace();
            isFailed = true;
        }
        return newSummary;
    }

    public Long getMeetingDate() {
        long newDate = -1;
        boolean isFailed = false;
        if (_bookDir == null || _bookDir.getName() == null ||!_bookDir.getName().startsWith(BookActivity4Config.USE_SKETCH_PREFIX)) {
            isFailed = true;
        }
        String folder = _bookDir.getFilePath();
        if (folder == null ||
                !new File(folder, BookActivity4Config.USE_SKETCH_CONFIG).exists() ||
                !new File(folder, BookActivity4Config.USE_SKETCH_CONFIG).canWrite()
        ) {
            isFailed = true;
        }
        try {
            File file_2 = new File(folder, BookActivity4Config.USE_SKETCH_CONFIG);
            String str = FastFile.loadMetaText(file_2);
            JSONObject item = new JSONObject(str);
            newDate = item.optLong(BookActivity4Config.USE_SKETCH_CONFIG_MEETING_DATE, -1L);
        } catch (JSONException e) {
            e.printStackTrace();
            isFailed = true;
        }
        return newDate > 0 ? newDate : null;
    }

    public Integer getMeetingHour() {
        int newDate = 0;
        boolean isFailed = false;
        if (_bookDir == null || _bookDir.getName() == null ||!_bookDir.getName().startsWith(BookActivity4Config.USE_SKETCH_PREFIX)) {
            isFailed = true;
        }
        String folder = _bookDir.getFilePath();
        if (folder == null ||
                !new File(folder, BookActivity4Config.USE_SKETCH_CONFIG).exists() ||
                !new File(folder, BookActivity4Config.USE_SKETCH_CONFIG).canWrite()
        ) {
            isFailed = true;
        }
        try {
            File file_2 = new File(folder, BookActivity4Config.USE_SKETCH_CONFIG);
            String str = FastFile.loadMetaText(file_2);
            JSONObject item = new JSONObject(str);
            newDate = item.optInt(BookActivity4Config.USE_SKETCH_CONFIG_MEETING_HOUR, -1);
        } catch (JSONException e) {
            e.printStackTrace();
            isFailed = true;
        }
        return (newDate >= 0 && newDate <= 23) ? newDate : null;
    }

    public Integer getMeetingMinute() {
        int newDate = 0;
        boolean isFailed = false;
        if (_bookDir == null || _bookDir.getName() == null ||!_bookDir.getName().startsWith(BookActivity4Config.USE_SKETCH_PREFIX)) {
            isFailed = true;
        }
        String folder = _bookDir.getFilePath();
        if (folder == null ||
                !new File(folder, BookActivity4Config.USE_SKETCH_CONFIG).exists() ||
                !new File(folder, BookActivity4Config.USE_SKETCH_CONFIG).canWrite()
        ) {
            isFailed = true;
        }
        try {
            File file_2 = new File(folder, BookActivity4Config.USE_SKETCH_CONFIG);
            String str = FastFile.loadMetaText(file_2);
            JSONObject item = new JSONObject(str);
            newDate = item.optInt(BookActivity4Config.USE_SKETCH_CONFIG_MEETING_MINUTE, -1);
        } catch (JSONException e) {
            e.printStackTrace();
            isFailed = true;
        }
        return (newDate >= 0 && newDate < 59) ? newDate : null;
    }

    //isDrawBG is false, unless I want to share
    private void notifyForceSave(boolean isDrawBG) {
        //FIXME:this.isDirty should be always true
        //FIXME:I need to remove isDirty var
        if (true) {
            if (mOnUpdateBmpListener != null) { //FIXME:force save
                mOnUpdateBmpListener.onUpdateBmp(canvas.toBitmap(isDrawBG));
            }
        } else {
            isDirty = true; //FIXME: force save
        }
    }

    public String getBookNameNG(String url) {
        if (url == null) {
            return "";
        }
        try {
            return new File(url).getName();
        } catch (Throwable eee) {
            eee.printStackTrace();
        }
        if (url.contains("/")) {
            return url.substring(url.lastIndexOf("/") + 1);
        }
        return url;
    }
    public boolean isBold = false;
    public void setBold(boolean bold) {
        this.isBold = bold;
        setBoldItalicsStyle();
    }
    public void setBoldItalicsStyle() {
        if (this.getDtView(false) != null && this.getDtView(false).mEtTextEdit != null) {
            int style = Typeface.NORMAL;
            if (this.isBold) {
                style |= Typeface.BOLD;
            }
            if (this.isItalics) {
                style |= Typeface.ITALIC;
            }
            Typeface family = Typeface.DEFAULT;
            if (this.styleType == STYLE_TYPE_NONE) {

            } else if (this.styleType == STYLE_TYPE_HAND) {

            } else if (this.styleType == STYLE_TYPE_SERIF) {
                family = Typeface.SERIF;
            } else if (this.styleType == STYLE_TYPE_SANS) {
                family = Typeface.SANS_SERIF;
            }
            Typeface font = Typeface.create(family, style);
            this.getDtView(false).mEtTextEdit.setTypeface(font);
        }
    }
    public boolean isItalics = false;
    public void setItalics(boolean italics) {
        this.isItalics = italics;
        setBoldItalicsStyle();
    }
    public boolean isUnderline = false;
    public void setUnderline(boolean isUnderline) {
        this.isUnderline = isUnderline;
    }
    public final static int ALIGN_TYPE_NONE = 0;
    public final static int ALIGN_TYPE_LEFT = 1;
    public final static int ALIGN_TYPE_CENTER = 2;
    public final static int ALIGN_TYPE_RIGHT = 3;
    public final static int ALIGN_TYPE_JUSTIFY = 4;
    public int alignType = ALIGN_TYPE_NONE;
    public void setAlignType(int alignType) {
        this.alignType = alignType;
        if (this.getDtView(false) != null && this.getDtView(false).mEtTextEdit != null) {
            if (alignType == ALIGN_TYPE_NONE ||
                    alignType == ALIGN_TYPE_LEFT ||
                    alignType == ALIGN_TYPE_JUSTIFY) {
                this.getDtView(false).mEtTextEdit.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            } else if (alignType == ALIGN_TYPE_CENTER) {
                this.getDtView(false).mEtTextEdit.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            } else if (alignType == ALIGN_TYPE_RIGHT) {
                this.getDtView(false).mEtTextEdit.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
            }
        }
    }
    public final static int LIST_TYPE_NONE = 0;
    public final static int LIST_TYPE_BULLET = 1;
    public final static int LIST_TYPE_NUMBER = 2;
    public int listType = LIST_TYPE_NONE;
    public void setListType(int listType) {
        this.listType = listType;
    }
    public final static int STYLE_TYPE_NONE = 0;
    public final static int STYLE_TYPE_HAND = 1;
    public final static int STYLE_TYPE_SERIF = 2;
    public final static int STYLE_TYPE_SANS = 3;
    public int styleType = STYLE_TYPE_NONE;
    public void setStyleType(int styleType) {
        this.styleType = styleType;
        setBoldItalicsStyle();
    }
    public final static int SIZE_TYPE_NONE = 0;
    public final static int SIZE_TYPE_TITLE = 1;
    public final static int SIZE_TYPE_H1= 2;
    public final static int SIZE_TYPE_H2 = 3;
    public final static int SIZE_TYPE_H3 = 4;
    public int sizeType = SIZE_TYPE_NONE;
    private float editTextSize = 19;//28;
    public void setSizeType(int sizeType) {
        float scale = 1.0f * canvas.getScaleFactor();
        this.sizeType = sizeType;
        if (this.getDtView(false) != null && this.getDtView(false).mEtTextEdit != null) {
            if (sizeType == SIZE_TYPE_NONE) {
                //this.dtView.mEtTextEdit.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 32);
                float textSize = 19 * scale;
                this.getDtView(false).mEtTextEdit.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);//28);
                this.editTextSize = 19;//28;
            } else if (sizeType == SIZE_TYPE_TITLE) {
                //this.dtView.mEtTextEdit.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 48);
                float textSize = 36 * scale;
                this.getDtView(false).mEtTextEdit.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);//39);//48);
                this.editTextSize = 36;//39;//48;
            } else if (sizeType == SIZE_TYPE_H1) {
                //this.dtView.mEtTextEdit.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 42);
                float textSize = 32 * scale;
                this.getDtView(false).mEtTextEdit.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);//33);//42);
                this.editTextSize = 32;//33;//42;
            } else if (sizeType == SIZE_TYPE_H2) {
                //this.dtView.mEtTextEdit.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 38);
                float textSize = 28 * scale;
                this.getDtView(false).mEtTextEdit.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);//29);//38);
                this.editTextSize = 28;//29;//38;
            } else if (sizeType == SIZE_TYPE_H3) {
                //this.dtView.mEtTextEdit.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 36);
                float textSize = 24 * scale;
                this.getDtView(false).mEtTextEdit.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);//25);//34);
                this.editTextSize = 24;//25;//34;
            }
        }
    }
    private int editTextColor = 0xFF000000;
    public void setEditTextColor(int editTextColor) {
        this.editTextColor = editTextColor;
        if (this.getDtView(false) != null && this.getDtView(false).mEtTextEdit != null) {
            this.getDtView(false).mEtTextEdit.setTextColor(editTextColor);
        }
    }

    private final static String FOCUS_PREF_NAME = "FocusModePrefs";
    private final static String FOCUS_PREF_ITEM_NAME = "focusModeDemo1";
    private void setFocusModeDemo(boolean used){
        SharedPreferences.Editor prefs = this.getActivity().getSharedPreferences(FOCUS_PREF_NAME, Activity.MODE_PRIVATE).edit();
        prefs.putBoolean(FOCUS_PREF_ITEM_NAME, used);
        prefs.apply();
    }
    private boolean getFocusModeDemo(){
        SharedPreferences prefs = this.getActivity().getSharedPreferences(FOCUS_PREF_NAME, Activity.MODE_PRIVATE);
        return prefs.getBoolean(FOCUS_PREF_ITEM_NAME, false);
    }

    public void toggleFocusMode() { //focus mode, fullscreen
        if (g_rootView.findViewById(R.id.llTab).getVisibility() == View.VISIBLE) {
            //enter fullscreen
            g_rootView.findViewById(R.id.llTab).setVisibility(View.GONE);
            g_rootView.findViewById(R.id.llTopBar).setVisibility(View.GONE);
            //findViewById(R.id.llFullscreen).setVisibility(View.VISIBLE);
            g_rootView.findViewById(R.id.llFullscreen2).setVisibility(View.VISIBLE);
            g_rootView.findViewById(R.id.llFullscreen2_demo).setVisibility(View.GONE);//View.VISIBLE);
            g_rootView.findViewById(R.id.left_toolkit_global).setVisibility(View.GONE);
        } else {
            //exit fullscreen
            g_rootView.findViewById(R.id.llTab).setVisibility(View.VISIBLE);
            g_rootView.findViewById(R.id.llTopBar).setVisibility(View.VISIBLE);
            //findViewById(R.id.llFullscreen).setVisibility(View.GONE);
            g_rootView.findViewById(R.id.llFullscreen2).setVisibility(View.GONE);
            g_rootView.findViewById(R.id.llFullscreen2_demo).setVisibility(View.GONE);
            g_rootView.findViewById(R.id.left_toolkit_global).setVisibility(View.VISIBLE);
        }
    }

    public void nextPage() {
        gotoNextPage();
    }
    public void previousPage() {
        gotoPrevPage();
    }
    public void flipUp() {
        if (false) {
            if (currentTabIdTopBar == iconsTopBar[0]) { //paint tool
                g_rootView.findViewById(R.id.bottomDialog2).setVisibility(View.GONE);
                if (g_rootView.findViewById(R.id.bottomDialog1).getVisibility() == View.VISIBLE) {
                    g_rootView.findViewById(R.id.bottomDialog1).setVisibility(View.GONE);
                } else {
                    initBottom12();
                    g_rootView.findViewById(R.id.bottomDialog1).setVisibility(View.VISIBLE);
                }
            } else if (currentTabIdTopBar == iconsTopBar[1]) { //text tool
                if (currentTabIdSubmenu2 == R.id.left_toolkit_item21) {
                    initBottom12();
                    g_rootView.findViewById(R.id.llLeftPanel1).setVisibility(View.VISIBLE);
                    g_rootView.findViewById(R.id.llLeftPanel2).setVisibility(View.GONE);
                } else if (currentTabIdSubmenu2 == R.id.left_toolkit_item22) {
                    initBottom12();
                    g_rootView.findViewById(R.id.llLeftPanel1).setVisibility(View.GONE);
                    g_rootView.findViewById(R.id.llLeftPanel2).setVisibility(View.VISIBLE);
                }
                g_rootView.findViewById(R.id.bottomDialog1).setVisibility(View.GONE);
                if (g_rootView.findViewById(R.id.bottomDialog2).getVisibility() == View.VISIBLE) {
                    g_rootView.findViewById(R.id.bottomDialog2).setVisibility(View.GONE);
                } else {
                    initBottom12();
                    g_rootView.findViewById(R.id.bottomDialog2).setVisibility(View.VISIBLE);
                }
            }
        } else {
            //open navigation panel
            if (!isLoadingPageGrid) {
                isLoadingPageGrid = true;
                createWaitingProgressDialog();
                g_rootView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isLoadingPageGrid = false;
                        beforePageGrid();
                        AlertDialog dialog = new BookActivity4PageGridDialog(getActivity(),
                                BookActivity4Fragment.this.dirUrl,
                                BookActivity4Fragment.this.dirUrlPath)
                                .create();
                        dialog.show();
                        cancelWaitingProgressDialog();
                    }
                }, 0);//100);
            }
        }
    }

    public void openPage(int pageIdx) {
        this.ensureSave();
        this._pageIdx = pageIdx;
        onPageIdxChange(false);
    }

    //FIXME: not good
    public void deletePages(List<Page> pages) {
        BookPage page_old = _book.getPage(_pageIdx);

        List<FastFile> fastFiles = new ArrayList<>();
        for (Page page : pages) {
            //page.getName() is new String(index + 1)
            List<FastFile> pageFiles = this.getBook().getPages();
            for (int i = 0; i < pageFiles.size(); ++i) {
                FastFile pageFile = pageFiles.get(i);
                if (("" + (i + 1)).equals(page.getName())) {
                    fastFiles.add(pageFile);
                }
            }
        }
        int gotoPage = _pageIdx;
        //----------------------------
        //backup pageIdx and pages
        if (getBook() != null) {
            //Note, need deep copy
            if (pages_old == null) {
                _pageIdx_old = _pageIdx;
                pages_old = new ArrayList<FastFile>(getBook().getPages());
                pages_NameMap_old = new HashMap<>(getBook().getPagetNameMap());
            }
        }
        //----------------------------
        int gotoPage_2 = -1;
        for (int i = gotoPage; i >= 0; --i) {
            boolean isDeleted = false;
            for (Page page : pages) {
                if (("" + (i + 1)).equals(page.getName())) {
                    isDeleted = true;
                    break;
                }
            }
            if (isDeleted) {
                continue;
            } else {
                gotoPage_2 = i;
                break;
            }
        }
        if (gotoPage_2 >= 0) {
            gotoPage = gotoPage_2;
        } else {
            int gotoPage_3 = -1;
            List<FastFile> pageFiles = this.getBook().getPages();
            for (int i = gotoPage + 1; i < pageFiles.size(); ++i) {
                boolean isDeleted = false;
                for (Page page : pages) {
                    if (("" + (i + 1)).equals(page.getName())) {
                        isDeleted = true;
                        break;
                    }
                }
                if (isDeleted) {
                    continue;
                } else {
                    gotoPage_3 = i;
                    break;
                }
            }
            if (gotoPage_3 >= 0) {
                gotoPage = gotoPage_2;
            } else {
                gotoPage = 0;
            }
        }
        for (FastFile fastFile : fastFiles) {
            System.out.println("deletePages : " + fastFile.getName());
            this.getBook().removePage(fastFile, _bookIO);
        }


        getBookIO().savePageOrder(page_old, _book, this._pageIdx, getActivity());
        //--------------
        this._book = null; //if _book == null, it will be reloaded from files
        set_book(getBook()); //FIXME:???reload
        //--------------
        //gotoFirstPage();
        this.ensureSave();
        this._pageIdx = gotoPage;
        onPageIdxChange(true);
        if (!BookActivity4PageGridDialog.NO_REOPEN_DIALOG) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    AlertDialog dialog = new BookActivity4PageGridDialog(getActivity(),
                            BookActivity4Fragment.this.dirUrl,
                            BookActivity4Fragment.this.dirUrlPath)
                            .create();
                    dialog.show();
                }
            });
        }
        if (canvas != null) {
            canvas.onVersionChanged();
        }
    }

    //see public final ArrayList<CopyOnWriteArrayList<DrawPath>> versions = new ArrayList<>();
    private Integer _pageIdx_old;
    private List<FastFile> pages_old;
    private Map<Integer, String> pages_NameMap_old;
    public final static boolean REMOVE_PAGE_FILE = false; //don't remove page file
    public boolean isCanRestorePages() {
        return pages_old != null && pages_NameMap_old != null && _pageIdx_old != null;
    }
    public void clearRestorePages() {
        _pageIdx_old = null;
        pages_old = null;
        pages_NameMap_old = null;
    }
    public void undoAndRestorePages() {
        if (pages_old != null && pages_NameMap_old != null && _pageIdx_old != null) {
            this.getBook().restorePages(pages_old, pages_NameMap_old);
            int gotoPage = _pageIdx_old;
            clearRestorePages();
//--------------
//need to save _book to the files to reload
            BookPage page_old = _book.getPage(_pageIdx);
            getBookIO().savePageOrder(page_old, _book, this._pageIdx, getActivity());
//--------------
            //reload
            this._book = null; //if _book == null, it will be reloaded from files
            set_book(getBook()); //FIXME:???reload
//--------------
//--------------
//gotoFirstPage();
            this.ensureSave();
            this._pageIdx = gotoPage;
            onPageIdxChange(true);
        }
    }

    //FIXME: not good
    public void copyPages(List<Page> pages) {
        BookPage page_old = _book.getPage(_pageIdx);

        List<FastFile> fastFiles = new ArrayList<>();
        for (Page page : pages) {
            //page.getName() is new String(index + 1)
            List<FastFile> pageFiles = this.getBook().getPages();
            for (int i = 0; i < pageFiles.size(); ++i) {
                FastFile pageFile = pageFiles.get(i);
                if (("" + (i + 1)).equals(page.getName())) {
                    fastFiles.add(pageFile);
                }
            }
        }
        int gotoPage = _pageIdx;
        for (FastFile fastFile : fastFiles) {
            System.out.println("copyPages : " + fastFile.getName());
            this.getBook().copyPage(fastFile, _bookIO);
        }


        getBookIO().savePageOrder(page_old, _book, this._pageIdx, getActivity());
        //--------------
        this._book = null; //if _book == null, it will be reloaded from files
        set_book(getBook()); //FIXME:???reload
        //--------------
        //gotoFirstPage();
        this.ensureSave();
        this._pageIdx = gotoPage;
        onPageIdxChange(true);
        if (!BookActivity4PageGridDialog.NO_REOPEN_DIALOG) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    AlertDialog dialog = new BookActivity4PageGridDialog(getActivity(),
                            BookActivity4Fragment.this.dirUrl,
                            BookActivity4Fragment.this.dirUrlPath)
                            .create();
                    dialog.show();
                }
            });
        }
    }

    //FIXME: not good
    public void reorderPages(List<Page> pages) {
        BookPage page_old = _book.getPage(_pageIdx);

        List<FastFile> fastFiles = new ArrayList<>();
        for (Page page : pages) {
            //page.getName() is new String(index + 1)
            List<FastFile> pageFiles = this.getBook().getPages();
            for (int i = 0; i < pageFiles.size(); ++i) {
                FastFile pageFile = pageFiles.get(i);
                if (("" + (i + 1)).equals(page.getName())) {
                    fastFiles.add(pageFile);
                }
            }
        }

        int gotoPage = _pageIdx;

        {
            System.out.println("reorderPages : " + fastFiles.size());
            this.getBook().reorderPage(fastFiles, _bookIO);
        }


        getBookIO().savePageOrder(page_old, _book, this._pageIdx, getActivity());
        //--------------
        this._book = null; //if _book == null, it will be reloaded from files
        set_book(getBook()); //FIXME:???reload
        //--------------
        //gotoFirstPage();
        this.ensureSave();
        this._pageIdx = gotoPage;
        onPageIdxChange(true);
        if (!BookActivity4PageGridDialog.NO_REOPEN_DIALOG) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    AlertDialog dialog = new BookActivity4PageGridDialog(getActivity(),
                            BookActivity4Fragment.this.dirUrl,
                            BookActivity4Fragment.this.dirUrlPath)
                            .create();
                    dialog.show();
                }
            });
        }
    }

    public void showNewBrushDialog() {
        AlertDialog dialog = new MaterialAlertDialogBuilder(getActivity(), BookActivity4Utils.getCenteredTitleThemeOverlay())
                //.setTitle(title)
                .setView(R.layout.activity_book4_brush_new)
                .setCancelable(false)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .create();
        dialog.show();
    }

    private void loadFileDemo() {
        Drawable drawable = ContextCompat.getDrawable(getActivity(), R.mipmap.ic_launcher);
        //https://blog.csdn.net/jaycee110905/article/details/38817871
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ?
                Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), config);
        Canvas canvas_ = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas_);
        //myFabricView.drawImage(200, 200, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), bitmap);
        drawImage(canvas, 200, 400, bitmap.getWidth(), bitmap.getHeight(), bitmap, false);
    }
    private final ActivityResultLauncher<Intent> activityResultLauncherLoad = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            (result) -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    System.out.println(Objects.requireNonNull(uri).getPath());
                    try {
                        InputStream inputStream = getActivity().getContentResolver().openInputStream(uri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        int x = canvas.getWidth() / 2;
                        int y = canvas.getHeight() / 2;
                        //Point p = new Point(x, y, 1.0f);
                        Point p = canvas.mapPoint(x, y, 1.0f);
                        DrawPath pathImage = drawImage(canvas, (int)p.x, (int)p.y, bitmap.getWidth(), bitmap.getHeight(), bitmap, false);
                        if (inputStream != null) {
                            inputStream.close();
                        }
                        if (pathImage != null) {
                            this.canvas.setTool(DrawCanvas.TOOLS.select);
                            this.canvas.getSelectionTool().mode = SelectionTool.TOUCH_MODES.none;

                            this.canvas.getSelectionTool().getSelectedPaths().clear();
                            this.canvas.getSelectionTool().getSelectedPaths().add(pathImage);

                            this.canvas.getSelectionTool().currentPath.clear();
                            float w = 100;
                            float h = 100;
                            if (pathImage.pointsBitmap != null) {
                                w = pathImage.pointsBitmap.getWidth();
                                h = pathImage.pointsBitmap.getHeight();
                            }
                            Point boundsTop = new Point(pathImage.pointsTextX - w / 2, pathImage.pointsTextY - h / 2);
                            Point boundsBottom = new Point(boundsTop.x + w, boundsTop.y + h);
                            this.canvas.getSelectionTool().currentPath.addPoint(boundsTop);
                            this.canvas.getSelectionTool().currentPath.addPoint(new Point(boundsBottom.x, boundsTop.y));
                            this.canvas.getSelectionTool().currentPath.addPoint(boundsBottom);
                            this.canvas.getSelectionTool().currentPath.addPoint(new Point(boundsTop.x, boundsBottom.y));
                            this.canvas.getSelectionTool().currentPath.appearance =
                                    this.canvas.getSelectionTool().APPEARANCE_SELECTED;

                            if (this.canvas != null) {
                                this.canvas.versionBackup();
                            }

                            this.canvas.invalidate();
                        }
                    } catch (Exception e) {
                        Toast.makeText(getActivity().getApplicationContext(), "An error was encountered while loading.", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
                if (canvas != null) {
                    canvas.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            canvas.disableCenter = false;
                            runFullScreen(getActivity());
                        }
                    }, 200); //FIXME:????
                }
            }
    );

//    DrawCanvas.TOOLS lastTool = DrawCanvas.TOOLS.none;
//    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) { //keyboard F1, need enable keyboard
            boolean isTextMode = false;
            if (g_rootView != null &&
                    g_rootView.findViewById(R.id.left_toolkit2) != null &&
                    g_rootView.findViewById(R.id.left_toolkit2).getVisibility() == View.VISIBLE) {
                isTextMode = true;
            }
            if (!isTextMode && this.canvas != null) {
//                lastTool = this.canvas.tool;
                this.canvas.setTool(DrawCanvas.TOOLS.pan);
            }
            return true;
        }
        return false;
    }

    private long rowId = -1;
    public void tv_result_setText(String str, String subStr, boolean isEnd, boolean isAppend, float[] timestamps, float processTime) {
        Log.e(TAG, "tv_result_setText : " + str);

        if (g_rootView.findViewById(R.id.pauseRecordOn).getVisibility() == View.VISIBLE) {
            //pause, no output
            return;
        }

        if (str != null && str.equals("")) {
            rowId = -1;
            //return;
        }
        try {
            SDRecordingsDatabase mDatabase = this.adapter.getDB(); //new SDRecordingsDatabase(getActivity(), _bookDir.getFilePath());
            if (mDatabase != null) {
                if (rowId > -1) {
                    mDatabase.updateItemContent(rowId, str, isAppend);
                } else {
                    String recordingName = "rtasr-" + System.currentTimeMillis();

                    if (timestamps != null) {
                        recordingName = "sherpa-";
//                        for (int i = 0; i < timestamps.length; ++i) {
//                            recordingName += timestamps[i] + ",";
//                        }
                        recordingName += ("" + processTime);
                    }

                    if (str != null &&
                            (str.startsWith(".") || str.startsWith("?") || str.startsWith(","))) {
                        rowId = mDatabase.addRecording(
                                recordingName,
                                "",
                                0,
                                "", "",
                                "text", str.substring(1));
                        mDatabase.updateItemContent(rowId - 1, str.substring(0, 1), true);
                    } else {
                        rowId = mDatabase.addRecording(
                                recordingName,
                                "",
                                0,
                                "", "",
                                "text", str);
                    }
                }
                if (isEnd) {
                    rowId = -1;
                }
            }
            //mDatabase.close();
            if (adapter != null) {
                adapter.notifyDataSetChanged();
                ListView viewListViewBook = (ListView) g_rootView.findViewById(R.id.viewListViewBook);
                viewListViewBook.setSelection(adapter.getCount() - 1);
            }
        } catch (Throwable eee) {
            eee.printStackTrace();
        }
//        AppCompatImageView btnPanel = (AppCompatImageView) findViewById(R.id.btnPanel);
//        AnimationDrawable anim = (AnimationDrawable) btnPanel.getDrawable();
//        if (isRecording) { //FIXME: use var not good
//            anim.start();
//            findViewById(R.id.startRecord).setVisibility(View.GONE);
//            findViewById(R.id.stopRecord).setVisibility(View.VISIBLE);
//        } else {
//            anim.stop();
//            findViewById(R.id.startRecord).setVisibility(View.VISIBLE);
//            findViewById(R.id.stopRecord).setVisibility(View.GONE);
//        }
//        Toast.makeText(BookActivity4.this, "total : " + adapter.getCount(), Toast.LENGTH_LONG).show();
    }
    private boolean isTranscriptRecording = false;
    public void btn_audio_start_setEnabled(boolean enable) {
        Log.e(TAG, "btn_audio_start_setEnabled : " + enable);
        AppCompatImageView btnPanel = (AppCompatImageView) g_rootView.findViewById(R.id.btnPanel);
        AnimationDrawable anim = (AnimationDrawable) btnPanel.getDrawable();
        if (!enable) {
            isTranscriptRecording = true;
            anim.start();
            g_rootView.findViewById(R.id.startRecord).setVisibility(View.GONE);
            g_rootView.findViewById(R.id.stopRecord).setVisibility(View.VISIBLE);
            g_rootView.findViewById(R.id.pauseRecordOff).setVisibility(View.VISIBLE);
            g_rootView.findViewById(R.id.pauseRecordOn).setVisibility(View.GONE);

            rlAIIcon.setVisibility(View.GONE);
            btnPanel2.setVisibility(View.VISIBLE);
            AnimationDrawable anim2 = (AnimationDrawable) btnPanel2.getDrawable();
            anim2.start();
        } else {
            isTranscriptRecording = false;
            anim.stop();
            anim.selectDrawable(0);
            g_rootView.findViewById(R.id.startRecord).setVisibility(View.VISIBLE);
            g_rootView.findViewById(R.id.stopRecord).setVisibility(View.GONE);
            g_rootView.findViewById(R.id.pauseRecordOff).setVisibility(View.VISIBLE);
            g_rootView.findViewById(R.id.pauseRecordOn).setVisibility(View.GONE);

            rlAIIcon.setVisibility(View.VISIBLE);
            btnPanel2.setVisibility(View.GONE);
            AnimationDrawable anim2 = (AnimationDrawable) btnPanel2.getDrawable();
            anim2.stop();
            anim2.selectDrawable(0);
        }
    }

    public void updateAppear(int id) {
        int stroke = 0xFF000000;
        int strokeSize = 1;
        if (id == R.id.left_toolkit_item1) {
            stroke = left_toolkit_item1_color;
            strokeSize = left_toolkit_item1_size;
        } else if (id == R.id.left_toolkit_item2) {
            stroke = left_toolkit_item2_color;
            strokeSize = left_toolkit_item2_size;
        } else if (id == R.id.left_toolkit_item3) {
            stroke = left_toolkit_item3_color;
            strokeSize = left_toolkit_item3_size;
        } else if (id == R.id.left_toolkit_item4) {
            stroke = left_toolkit_item4_color;
            strokeSize = left_toolkit_item4_size;
        } else if (id == R.id.left_toolkit_item5) {
            stroke = left_toolkit_item5_color;
            strokeSize = left_toolkit_item5_size;
        } else if (id == R.id.left_toolkit_item6) {
            stroke = left_toolkit_item6_color;
            strokeSize = left_toolkit_item6_size;
        }
        if (true) {
            //see loadFromSettings
            setColor(canvas, stroke);
            setSize(canvas, strokeSize);
        } else {
            //don't use this
            canvas.getPaintTool().getAppearance().stroke = left_toolkit_item6_color;
            canvas.getPaintTool().getAppearance().strokeSize = left_toolkit_item6_size;
        }
    }

    public String getVecJson(DrawCanvas canvas) {
        if (canvas != null) {
            return canvas.getVecJson();
        }
        return "";
    }

    private final static boolean USE_PRUSH_PRESET_SINGLE_FOLDER = true;
    private void saveBrushPreset() {
        try {
            if (USE_PRUSH_PRESET_SINGLE_FOLDER) {
                boolean isFailed = false;
                if (_bookDir == null || _bookDir.getName() == null ||!_bookDir.getName().startsWith(BookActivity4Config.USE_SKETCH_PREFIX)) {
                    isFailed = true;
                }
                String folder = _bookDir.getFilePath();
                if (folder == null ||
                        !new File(folder, BookActivity4Config.USE_SKETCH_CONFIG).exists() ||
                        !new File(folder, BookActivity4Config.USE_SKETCH_CONFIG).canWrite()
                ) {
                    isFailed = true;
                }
                try {
                    File file_2 = new File(folder, BookActivity4Config.USE_SKETCH_CONFIG);
                    String str = FastFile.loadMetaText(file_2);
                    JSONObject item = new JSONObject(str);
                    item.put("left_toolkit_item1_color", left_toolkit_item1_color);
                    item.put("left_toolkit_item2_color", left_toolkit_item2_color);
                    item.put("left_toolkit_item3_color", left_toolkit_item3_color);
                    item.put("left_toolkit_item4_color", left_toolkit_item4_color);
                    item.put("left_toolkit_item5_color", left_toolkit_item5_color);
                    item.put("left_toolkit_item6_color", left_toolkit_item6_color);

                    item.put("left_toolkit_item1_size", left_toolkit_item1_size);
                    item.put("left_toolkit_item2_size", left_toolkit_item2_size);
                    item.put("left_toolkit_item3_size", left_toolkit_item3_size);
                    item.put("left_toolkit_item4_size", left_toolkit_item4_size);
                    item.put("left_toolkit_item5_size", left_toolkit_item5_size);
                    item.put("left_toolkit_item6_size", left_toolkit_item6_size);
                    FastFile.saveMetaText(file_2, item.toString(), getActivity());
                } catch (JSONException e) {
                    e.printStackTrace();
                    isFailed = true;
                }
            } else {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("left_toolkit_item1_color", left_toolkit_item1_color);
                editor.putInt("left_toolkit_item2_color", left_toolkit_item2_color);
                editor.putInt("left_toolkit_item3_color", left_toolkit_item3_color);
                editor.putInt("left_toolkit_item4_color", left_toolkit_item4_color);
                editor.putInt("left_toolkit_item5_color", left_toolkit_item5_color);
                editor.putInt("left_toolkit_item6_color", left_toolkit_item6_color);

                editor.putInt("left_toolkit_item1_size", left_toolkit_item1_size);
                editor.putInt("left_toolkit_item2_size", left_toolkit_item2_size);
                editor.putInt("left_toolkit_item3_size", left_toolkit_item3_size);
                editor.putInt("left_toolkit_item4_size", left_toolkit_item4_size);
                editor.putInt("left_toolkit_item5_size", left_toolkit_item5_size);
                editor.putInt("left_toolkit_item6_size", left_toolkit_item6_size);

                editor.apply();
            }
        } catch (Throwable eee) {
            eee.printStackTrace();
        }
    }
    private void loadBrushPreset() {
        if (USE_PRUSH_PRESET_SINGLE_FOLDER) {
            //String newSummary = "";
            boolean isFailed = false;
            if (_bookDir == null || _bookDir.getName() == null ||!_bookDir.getName().startsWith(BookActivity4Config.USE_SKETCH_PREFIX)) {
                isFailed = true;
            }
            String folder = _bookDir.getFilePath();
            if (folder == null ||
                    !new File(folder, BookActivity4Config.USE_SKETCH_CONFIG).exists() ||
                    !new File(folder, BookActivity4Config.USE_SKETCH_CONFIG).canWrite()
            ) {
                isFailed = true;
            }
            try {
                File file_2 = new File(folder, BookActivity4Config.USE_SKETCH_CONFIG);
                String str = FastFile.loadMetaText(file_2);
                JSONObject item = new JSONObject(str);
                try {
                    this.left_toolkit_item1_color = item.optInt("left_toolkit_item1_color", Color.BLACK);
                } catch (Throwable eee) {
                    eee.printStackTrace();
                    this.left_toolkit_item1_color = Color.BLACK;
                }
                try {
                    this.left_toolkit_item2_color = item.optInt("left_toolkit_item2_color", Color.BLACK);
                } catch (Throwable eee) {
                    eee.printStackTrace();
                    this.left_toolkit_item2_color = Color.BLACK;
                }
                try {
                    this.left_toolkit_item3_color = item.optInt("left_toolkit_item3_color", Color.BLACK);
                } catch (Throwable eee) {
                    eee.printStackTrace();
                    this.left_toolkit_item3_color = Color.BLACK;
                }

                try {
                    this.left_toolkit_item4_color = item.optInt("left_toolkit_item4_color", Color.BLACK);
                } catch (Throwable eee) {
                    eee.printStackTrace();
                    this.left_toolkit_item4_color = Color.BLACK;
                }
                try {
                    this.left_toolkit_item5_color = item.optInt("left_toolkit_item5_color", Color.BLACK);
                } catch (Throwable eee) {
                    eee.printStackTrace();
                    this.left_toolkit_item5_color = Color.BLACK;
                }
                try {
                    this.left_toolkit_item6_color = item.optInt("left_toolkit_item6_color", Color.BLACK);
                } catch (Throwable eee) {
                    eee.printStackTrace();
                    this.left_toolkit_item6_color = Color.BLACK;
                }


                try {
                    this.left_toolkit_item1_size = item.optInt("left_toolkit_item1_size", 1);
                } catch (Throwable eee) {
                    eee.printStackTrace();
                    this.left_toolkit_item1_size = 1;
                }
                try {
                    this.left_toolkit_item2_size = item.optInt("left_toolkit_item2_size", 1);
                } catch (Throwable eee) {
                    eee.printStackTrace();
                    this.left_toolkit_item2_size = 1;
                }
                ////highlight
                try {
                    this.left_toolkit_item3_size = item.optInt("left_toolkit_item3_size", 5);//1);
                } catch (Throwable eee) {
                    eee.printStackTrace();
                    this.left_toolkit_item3_size = 5;
                }


                try {
                    this.left_toolkit_item4_size = item.optInt("left_toolkit_item4_size", 1);
                } catch (Throwable eee) {
                    eee.printStackTrace();
                    this.left_toolkit_item4_size = 1;
                }
                try {
                    this.left_toolkit_item5_size = item.optInt("left_toolkit_item5_size", 1);
                } catch (Throwable eee) {
                    eee.printStackTrace();
                    this.left_toolkit_item5_size = 1;
                }
                try {
                    this.left_toolkit_item6_size = item.optInt("left_toolkit_item6_size", 1);
                } catch (Throwable eee) {
                    eee.printStackTrace();
                    this.left_toolkit_item6_size = 1;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                isFailed = true;
            }
        } else {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            try {
                this.left_toolkit_item1_color = preferences.getInt("left_toolkit_item1_color", Color.BLACK);
            } catch (Throwable eee) {
                eee.printStackTrace();
                this.left_toolkit_item1_color = Color.BLACK;
            }
            try {
                this.left_toolkit_item2_color = preferences.getInt("left_toolkit_item2_color", Color.BLACK);
            } catch (Throwable eee) {
                eee.printStackTrace();
                this.left_toolkit_item2_color = Color.BLACK;
            }
            try {
                this.left_toolkit_item3_color = preferences.getInt("left_toolkit_item3_color", Color.BLACK);
            } catch (Throwable eee) {
                eee.printStackTrace();
                this.left_toolkit_item3_color = Color.BLACK;
            }

            try {
                this.left_toolkit_item4_color = preferences.getInt("left_toolkit_item4_color", Color.BLACK);
            } catch (Throwable eee) {
                eee.printStackTrace();
                this.left_toolkit_item4_color = Color.BLACK;
            }
            try {
                this.left_toolkit_item5_color = preferences.getInt("left_toolkit_item5_color", Color.BLACK);
            } catch (Throwable eee) {
                eee.printStackTrace();
                this.left_toolkit_item5_color = Color.BLACK;
            }
            try {
                this.left_toolkit_item6_color = preferences.getInt("left_toolkit_item6_color", Color.BLACK);
            } catch (Throwable eee) {
                eee.printStackTrace();
                this.left_toolkit_item6_color = Color.BLACK;
            }


            try {
                this.left_toolkit_item1_size = preferences.getInt("left_toolkit_item1_size", 1);
            } catch (Throwable eee) {
                eee.printStackTrace();
                this.left_toolkit_item1_size = 1;
            }
            try {
                this.left_toolkit_item2_size = preferences.getInt("left_toolkit_item2_size", 1);
            } catch (Throwable eee) {
                eee.printStackTrace();
                this.left_toolkit_item2_size = 1;
            }
            //highlight
            try {
                this.left_toolkit_item3_size = preferences.getInt("left_toolkit_item3_size", 5);//1);
            } catch (Throwable eee) {
                eee.printStackTrace();
                this.left_toolkit_item3_size = 5;//1;
            }


            try {
                this.left_toolkit_item4_size = preferences.getInt("left_toolkit_item4_size", 1);
            } catch (Throwable eee) {
                eee.printStackTrace();
                this.left_toolkit_item4_size = 1;
            }
            try {
                this.left_toolkit_item5_size = preferences.getInt("left_toolkit_item5_size", 1);
            } catch (Throwable eee) {
                eee.printStackTrace();
                this.left_toolkit_item5_size = 1;
            }
            try {
                this.left_toolkit_item6_size = preferences.getInt("left_toolkit_item6_size", 1);
            } catch (Throwable eee) {
                eee.printStackTrace();
                this.left_toolkit_item6_size = 1;
            }
        }
    }
//    LinkedList<DrawPath> copyPaths = new LinkedList<>();
    CopyOnWriteArrayList<DrawPath> copyPaths = new CopyOnWriteArrayList<>();

    private void beforePageGrid() {
        notifyForceSave(false);
        this.ensureSave();
        onPageIdxChange(true);
    }

    public void onSelectChange(boolean forceExit) {
        if (g_rootView != null) {
            Slider sliderVerticalShape = (Slider) g_rootView.findViewById(R.id.sliderVerticalShape);
            if (sliderVerticalShape != null) {
                boolean found = false;
                LinkedList<DrawPath> selectedPaths = canvas.getSelectionTool().getSelectedPaths();
                DrawPath drawPathFound = null;
                if (selectedPaths != null) {
                    for (DrawPath drawPath : selectedPaths) {
                        if (drawPath != null &&
                                drawPath.pointsType == DrawPath.POINTS_TYPE_STROKE &&
                                drawPath.appearance != null &&
                                drawPath.appearance.penType == DrawAppearance.PEN_TYPE_6) {
                            drawPathFound = drawPath;
                            found = true;
                        }
                    }
                }
                if (forceExit) {
                    sliderVerticalShape.setVisibility(View.GONE);
                } else if (found && drawPathFound != null) {
                    sliderVerticalShape.setVisibility(View.VISIBLE);
                    if (ENABLE_NO_DETECT_SHAPE_PEN) {
                        sliderVerticalShape.setValue(drawPathFound.shapeSide);
                    } else {
                        switch (drawPathFound.shapeType) {
                            case DrawPath.SHAPE_TYPE_UNKNOWN:
                                sliderVerticalShape.setValue(0);
                                break;
                            case DrawPath.SHAPE_TYPE_LINE:
                                sliderVerticalShape.setValue(1);
                                break;
                            case DrawPath.SHAPE_TYPE_RECTANGLE:
                                sliderVerticalShape.setValue(2);
                                break;
                            case DrawPath.SHAPE_TYPE_CIRCLE:
                                sliderVerticalShape.setValue(3);
                                break;
                            case DrawPath.SHAPE_TYPE_TRIANGLE:
                                sliderVerticalShape.setValue(4);
                                break;
                            case DrawPath.SHAPE_TYPE_POLYGON:
                                sliderVerticalShape.setValue(5);
                                break;
                        }
                    }

                    LinkedList<DrawPath> toolPath = null;
                    if (canvas != null && canvas.getSelectionTool() != null) {
                        toolPath = canvas.getSelectionTool().getToolPaths();
                    }
                    if (toolPath != null) {
                        for (DrawPath path : toolPath) {
                            if (path.points.size() >= 4) {
                                Point p1 = path.points.get(1);
                                Point p2 = path.points.get(2);

                                RelativeLayout.LayoutParams pp = (RelativeLayout.LayoutParams)
                                        sliderVerticalShape.getLayoutParams();
                                //pp.setMargins((int)p.x, (int)p.y, 0, 0);
                                Point screenPoint1 = canvas.mapPointScreen(p1.x, p1.y, 1.0f);
                                Point screenPoint2 = canvas.mapPointScreen(p2.x, p2.y, 1.0f);
                                int sliderHeight = getResources().getDimensionPixelSize(R.dimen.activity_book4_slider_height);
                                float dy = (Math.abs(screenPoint2.y - screenPoint1.y) -
                                        sliderHeight/*sliderVerticalShape.getMeasuredHeight()*/)
                                        / 2.0f;
                                pp.leftMargin = (int)screenPoint1.x + 50;
                                pp.topMargin = (int)screenPoint1.y + (int)dy;
                                sliderVerticalShape.setLayoutParams(pp);
                                break;
                            }
                        }
                    }
                } else {
                    sliderVerticalShape.setVisibility(View.GONE);
                }
            }
        }
    }

    private boolean isBackPressed = false;
    public void onBackPressed() {
        isBackPressed = true;
        //FIXME:exit and save
        if (SAVING_ASYNC) {
            try {
                if (!SAVING_ASYNC_MULTI) {
                    if (task == null) {
                        task = new SavingTask(true, false);
                        task.executeOnExecutor(newFixedThreadPool);
                    }
                } else {
                    task = new SavingTask(true, false);
                    task.executeOnExecutor(newFixedThreadPool);
                }
            } catch (Throwable eee) {
                eee.printStackTrace();
            }
        } else {
            if (false) {
                ensureSave();
            } else {
                savePageInMain(getPageIdx(), pageBmp, getVecJson(canvas));
            }
            BookActivity4Utils.finish(getActivity(), true);
        }
    }

    public void scale(double delta,
                      float translationX,
                      float translationY,
                      float deltaX,
                      float deltaY) {
        if (canvas != null) {
            canvas.getPanTool().scaleFactor *= delta;
            canvas.getPanTool().updatePanOffset();
            canvas.getPanTool().offset.set(
                    canvas.getPanTool().offset.x + deltaX / canvas.getPanTool().scaleFactor /*canvas.getPanTool().scaleFactor*/,
                    canvas.getPanTool().offset.y + deltaY / canvas.getPanTool().scaleFactor /*canvas.getPanTool().scaleFactor*/);
            canvas.invalidate();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        refreshRunnable2 = null;
        BookActivity4Utils.canPushFragment = true;
    }

    private boolean g_isUndoActive = false;
    private boolean g_isRedoActive = false;
    public void onVersionChanged(boolean isUndoActive, boolean isRedoActive, int numUndo, int numRedo) {
        if (isUndoActive || isCanRestorePages()) {
            //((ImageView)g_rootView.findViewById(R.id.ivTitleUndo)).setImageAlpha(255);
            g_rootView.findViewById(R.id.llTitleUndo).setVisibility(View.VISIBLE);
            g_rootView.findViewById(R.id.llTitleUndo2).setVisibility(View.INVISIBLE);
            ((TextView)g_rootView.findViewById(R.id.tvTitleUndoNum)).setText("" + numUndo);
            g_isUndoActive = true;
        } else {
            //((ImageView)g_rootView.findViewById(R.id.ivTitleUndo)).setImageAlpha(125);
            g_rootView.findViewById(R.id.llTitleUndo).setVisibility(View.INVISIBLE);
            g_rootView.findViewById(R.id.llTitleUndo2).setVisibility(View.VISIBLE);
            ((TextView)g_rootView.findViewById(R.id.tvTitleUndoNum)).setText("");
            g_isUndoActive = false;
        }

        if (isRedoActive) {
            //((ImageView)g_rootView.findViewById(R.id.ivTitleRedo)).setImageAlpha(255);
            g_rootView.findViewById(R.id.llTitleRedo).setVisibility(View.VISIBLE);
            g_rootView.findViewById(R.id.llTitleRedo2).setVisibility(View.INVISIBLE);
            ((TextView)g_rootView.findViewById(R.id.tvTitleRedoNum)).setText("" + numRedo);
            g_isRedoActive = true;
        } else {
            //((ImageView)g_rootView.findViewById(R.id.ivTitleRedo)).setImageAlpha(125);
            g_rootView.findViewById(R.id.llTitleRedo).setVisibility(View.INVISIBLE);
            g_rootView.findViewById(R.id.llTitleRedo2).setVisibility(View.VISIBLE);
            ((TextView)g_rootView.findViewById(R.id.tvTitleRedoNum)).setText("");
            g_isRedoActive = false;
        }
    }

    public final static boolean DATE_READONLY = true;
    private boolean enableRecordButton = false;
    private void updateRecordButtonStatus() {
        String duration = getMeetingDuration();
        Integer durationVal = null;
        try {
            durationVal = Integer.parseInt(duration);
        } catch (Throwable eee) {
            eee.printStackTrace();
        }
        Long date = getMeetingDate();
        Integer hour = getMeetingHour();
        Integer minute = getMeetingMinute();
        Date startTime = null;
        if (date != null && hour != null && minute != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date(date));
            cal.set(Calendar.HOUR_OF_DAY, hour);
            cal.set(Calendar.MINUTE, minute);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            startTime = cal.getTime();
        }
        if (durationVal != null && startTime != null) {
            //===============
            //Comment below, If the user modifies the system time, he still can't record again
//            if (durationVal > 0) {
//                Date endTime = addMinute(startTime, durationVal);
//                if (endTime != null) {
//                    Date now = new Date();
//                    if (now.after(startTime) && now.before(endTime)) {
//                        enableRecordButton = true;
//                    } else {
//                        enableRecordButton = false;
//                    }
//                } else {
//                    enableRecordButton = false;
//                }
//            } else {
//                enableRecordButton = false;
//            }
            //===============
            enableRecordButton = false;
        } else {
            enableRecordButton = true;
        }

        if (date == null) {
            g_rootView.findViewById(R.id.llMeetingDate).setVisibility(View.GONE);
        } else {
            g_rootView.findViewById(R.id.llMeetingDate).setVisibility(View.VISIBLE);
        }
        if (hour == null || minute == null) {
            g_rootView.findViewById(R.id.llMeetingTime).setVisibility(View.GONE);
        } else {
            g_rootView.findViewById(R.id.llMeetingTime).setVisibility(View.VISIBLE);
        }
        if (duration == null) {
            g_rootView.findViewById(R.id.llMeetingDuration).setVisibility(View.GONE);
        } else {
            g_rootView.findViewById(R.id.llMeetingDuration).setVisibility(View.VISIBLE);
        }

        AppCompatImageView ivStartRecord = (AppCompatImageView) g_rootView.findViewById(R.id.ivStartRecord);
        TextView tvStartRecord = (TextView) g_rootView.findViewById(R.id.tvStartRecord);
        LinearLayout llStartRecordOuter = (LinearLayout) g_rootView.findViewById(R.id.llStartRecordOuter);
        CardView startRecord = (CardView) g_rootView.findViewById(R.id.startRecord);

        AppCompatImageView ivPauseRecordOff = (AppCompatImageView) g_rootView.findViewById(R.id.ivPauseRecordOff);
        TextView tvPauseRecordOff = (TextView) g_rootView.findViewById(R.id.tvPauseRecordOff);
        LinearLayout llPauseRecordOffOuter = (LinearLayout) g_rootView.findViewById(R.id.llPauseRecordOffOuter);
        CardView pauseRecordOff = (CardView) g_rootView.findViewById(R.id.pauseRecordOff);

        AppCompatImageView ivPauseRecordOn = (AppCompatImageView) g_rootView.findViewById(R.id.ivPauseRecordOn);
        TextView tvPauseRecordOn = (TextView) g_rootView.findViewById(R.id.tvPauseRecordOn);

        LinearLayout llDiarization = (LinearLayout) g_rootView.findViewById(R.id.llDiarization);
        LinearLayout llDiarizationSetting = (LinearLayout) g_rootView.findViewById(R.id.llDiarizationSetting);

        RadioButton rbASR1 = (RadioButton) g_rootView.findViewById(R.id.rbASR1);
        RadioButton rbASR2 = (RadioButton) g_rootView.findViewById(R.id.rbASR2);
        RadioButton rbASR3 = (RadioButton) g_rootView.findViewById(R.id.rbASR3);

        final int LTGRAY = 0xFF898786; //Color.LTGRAY
        if (enableRecordButton) {
            ivStartRecord.setColorFilter(null);
            tvStartRecord.setTextColor(Color.BLACK);
            llStartRecordOuter.setBackgroundResource(R.drawable.border_background);
            startRecord.setCardBackgroundColor(0xFFF6F3F2);

            ivPauseRecordOff.setColorFilter(null);
            tvPauseRecordOff.setTextColor(Color.BLACK);
            llPauseRecordOffOuter.setBackgroundResource(R.drawable.border_background);
            pauseRecordOff.setCardBackgroundColor(0xFFF6F3F2);

            ivPauseRecordOn.setColorFilter(null);
            tvPauseRecordOn.setTextColor(Color.WHITE);

            rbASR1.setEnabled(true);
            rbASR2.setEnabled(true);
            rbASR3.setEnabled(true);

            llDiarizationSetting.setVisibility(View.GONE);
            llDiarization.setVisibility(View.GONE);
        } else {
            ivStartRecord.setColorFilter(LTGRAY, PorterDuff.Mode.SRC_IN);
            tvStartRecord.setTextColor(LTGRAY);
            llStartRecordOuter.setBackgroundResource(R.drawable.border_background_disable);
            startRecord.setCardBackgroundColor(0xFFDCD8D8);
            startRecord.setForeground(null);
            //startRecord.setCardBackgroundColor(0xFF00FF00);

            ivPauseRecordOff.setColorFilter(LTGRAY, PorterDuff.Mode.SRC_IN);
            tvPauseRecordOff.setTextColor(LTGRAY);
            llPauseRecordOffOuter.setBackgroundResource(R.drawable.border_background_disable);
            pauseRecordOff.setCardBackgroundColor(0xFFDCD8D8);
            pauseRecordOff.setForeground(null);
            //pauseRecordOff.setCardBackgroundColor(0xFF00FF00);

            ivPauseRecordOn.setColorFilter(LTGRAY, PorterDuff.Mode.SRC_IN);
            tvPauseRecordOn.setTextColor(LTGRAY);

            rbASR1.setEnabled(false);
            rbASR2.setEnabled(false);
            rbASR3.setEnabled(false);

            llDiarizationSetting.setVisibility(View.VISIBLE);
            llDiarization.setVisibility(View.VISIBLE);
        }
    }

    //https://github.com/avesha/android.fba.toolkit/blob/master/engine/src/main/java/ru/profi1c/engine/util/DateHelper.java
    private static Date beginOfDay(Date dt) {
        //package cn.hutool.core.date;
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public static Date addDays(Date date, int count) {
        Calendar cal = getEmptyCalendar();
        cal.setTime(date);
        cal.add(Calendar.DATE, count);
        return cal.getTime();
    }

    public static Calendar getEmptyCalendar() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(0);
        return cal;
    }

    public static Date addMinute(Date date, int count) {
        Calendar cal = getEmptyCalendar();
        cal.setTime(date);
        cal.add(Calendar.MINUTE, count);
        return cal.getTime();
    }





    public void editMeetingSpeaker(String newSpeaker) {
        boolean isFailed = false;
        if (_bookDir == null || _bookDir.getName() == null ||!_bookDir.getName().startsWith(BookActivity4Config.USE_SKETCH_PREFIX)) {
            isFailed = true;
        }
        String folder = _bookDir.getFilePath();
        if (folder == null ||
                !new File(folder, BookActivity4Config.USE_SKETCH_CONFIG).exists() ||
                !new File(folder, BookActivity4Config.USE_SKETCH_CONFIG).canWrite()
        ) {
            isFailed = true;
        }
        try {
            File file_2 = new File(folder, BookActivity4Config.USE_SKETCH_CONFIG);
            String str = FastFile.loadMetaText(file_2);
            JSONObject item = new JSONObject(str);
            item.put(BookActivity4Config.USE_SKETCH_CONFIG_MEETING_SPEAKER, newSpeaker);
            FastFile.saveMetaText(file_2, item.toString(), getActivity());
        } catch (JSONException e) {
            e.printStackTrace();
            isFailed = true;
        }
//        if (isFailed) {
//            new MaterialAlertDialogBuilder(getActivity(), BookActivity4Utils.getCenteredTitleThemeOverlay())
//                    .setTitle("Error")
//                    .setMessage("Edit meeting speaker number failed")
//                    .setPositiveButton("OK", null)
//                    .show();
//        } else {
//            onCreateAct(g_rootView);
//            try {
//                ((TextView) g_rootView.findViewById(R.id.tvMeetingSummary)).setText(getMeetingSummary());
//            } catch (Throwable eee) {
//                eee.printStackTrace();
//            }
//        }
    }


    public String getMeetingSpeaker() {
        String newSummary = "";
        boolean isFailed = false;
        if (_bookDir == null || _bookDir.getName() == null ||!_bookDir.getName().startsWith(BookActivity4Config.USE_SKETCH_PREFIX)) {
            isFailed = true;
        }
        String folder = _bookDir.getFilePath();
        if (folder == null ||
                !new File(folder, BookActivity4Config.USE_SKETCH_CONFIG).exists() ||
                !new File(folder, BookActivity4Config.USE_SKETCH_CONFIG).canWrite()
        ) {
            isFailed = true;
        }
        try {
            File file_2 = new File(folder, BookActivity4Config.USE_SKETCH_CONFIG);
            String str = FastFile.loadMetaText(file_2);
            JSONObject item = new JSONObject(str);
            newSummary = item.optString(BookActivity4Config.USE_SKETCH_CONFIG_MEETING_SPEAKER, "");
        } catch (JSONException e) {
            e.printStackTrace();
            isFailed = true;
        }
        return newSummary;
    }

    private final static String TRANSCRIPT_PREF_NAME = "TranscriptPrefs";
    private final static String TRANSCRIPT_PREF_ITEM_NAME = "TranscriptLang";
    public void setTranscriptLang(String lang){
        SharedPreferences.Editor prefs = this.getActivity().getSharedPreferences(TRANSCRIPT_PREF_NAME, Activity.MODE_PRIVATE).edit();
        prefs.putString(TRANSCRIPT_PREF_ITEM_NAME, lang);
        prefs.apply();
    }
    public String getTranscriptLang(){
        SharedPreferences prefs = this.getActivity().getSharedPreferences(TRANSCRIPT_PREF_NAME, Activity.MODE_PRIVATE);
        return prefs.getString(TRANSCRIPT_PREF_ITEM_NAME, "en");
    }
}
