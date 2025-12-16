package com.txkj.drawingapp.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
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
import androidx.preference.PreferenceManager;

import com.agsw.FabricView.FabricView;
import com.foobnix.android.utils.KeyboardsMod;
import com.github.guanpy.wblib.bean.DrawPoint;
import com.github.guanpy.wblib.widget.DrawTextView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.slider.Slider;
import com.sys.speech.activity.DictResultActivity;
import com.sys.speech.activity.RecordingFragment;
import com.sys.speech.dialog.PlayerDialog;
import com.sys.speech.dialog.RecognizeDialog;
import com.sys.speech.pojo.RecordingItem;
import com.txkj.drawingapp.R;
import com.txkj.notemobile2.Book;
import com.txkj.notemobile2.BookActivity;
import com.txkj.notemobile2.BookListActivity;
import com.txkj.notemobile2.Config;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import io.github.pastthepixels.freepaint.Graphics.DrawAppearance;
import io.github.pastthepixels.freepaint.Graphics.DrawCanvas;
import io.github.pastthepixels.freepaint.Graphics.Point;
import io.github.pastthepixels.freepaint.MainActivity;
import io.github.pastthepixels.freepaint.Tools.EraserTool;
import io.material.catalog.windowpreferences.WindowPreferencesManager;

//FIXME:this.isDirty should be always true
//FIXME:I need to remove isDirty var
//isDirty = true; //FIXME: force save
//TODO:notifyForceSave, when view onSizeChanged or other events, need call it
public class BookActivity4 extends AppCompatActivity {
    private final static boolean ENABLE_BOTTOM_SHEET = false;
    
    //TODO:check .setCancelable(false)
    //TODO:android:background="#00000000"
    private final static boolean USE_OLD_PEN_SETTING_PANEL = false;

    private final static boolean D = true;
    private final static String TAG = "BookActivity4";

    private boolean isRecording = false; //FIXME:
    private BookReaderItemsAdapter adapter;
    private Slider slider;
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

    public final static String EXTRA_DIRURLPATH = "EXTRA_DIRURLPATH";
    public final static String EXTRA_BACKTEXT = "EXTRA_BACKTEXT";
    public final static String PAGE_IDX = "PAGE_IDX";

    public String curPattern;
    private String backText; //背景图案的种类文本

    private Uri dirUrl;
    private String dirUrlPath;
    private int initialPageIdx;
    private FastFile _bookDir;// = bookDir_init();
    FastFile bookDir_init() {
        //FIXME:check this.dirUrlPath null
        return FastFile.fromTreeUri(this, this.dirUrl, this.dirUrlPath);
    }

    private BookIO _bookIO;// = bookIO_init();
    private BookIO bookIO_init() {
        return new BookIO(this.getContentResolver());
    }

    private Book _book;
    private int _pageIdx;// = pageIdx_init();
    private int pageIdx_init() {
        return initialPageIdx;
    }

    private void onCreateAct() {
        _bookDir = bookDir_init();
        _bookIO = bookIO_init();
        _pageIdx = pageIdx_init();
        try {
            ((TextView) findViewById(R.id.newTitle)).setText(_bookDir.getDisplayName());
        } catch (Throwable eee) {
            eee.printStackTrace();
        }
    }
    private void onPageIdxChange() {
        int idx = getPageIdx();
        if (idx < 0) {
            idx = this._pageIdx = 0;
        } else if (idx >= getBook().getPages().size()) {
            idx = this._pageIdx = getBook().getPages().size() - 1;
        }
        if (idx >= 0 && idx < getBook().getPages().size()) { //FIXME: null??
            onPageIdx(canvas, idx, new CanvasBoox.OnLoadBitmapListener() {
                @Override
                public Bitmap onLoadBitmap(int idx) {
                    BookIO bookIO = getBookIO();
                    BookPage page = getBook().getPage(idx);
                    pageBmp = bookIO.loadBitmapOrNull(page);
                    try {
                        String metaTxt = bookIO.loadMetaPng(page.getFile());
                        JSONObject item = new JSONObject(metaTxt);
                        if (item != null) {
                            curPattern = item.optString("pattern");
                            backText = curPattern; //FIXME:added
                        }
                    } catch (Throwable eee) {
                        eee.printStackTrace();
                    }
                    isDirty = false;
                    return pageBmp;
                }
            });
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
    private static final ReentrantLock bitmapLock = new ReentrantLock();

    private FastFile getBookDir() {
        return this._bookDir;
    }

    private BookIO getBookIO() {
        return this._bookIO;
    }

    private void set_book(Book newbook) {
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
            Book it = this.getBookIO().loadBook(this.getBookDir());
            this.set_book(it);
            return it;
        }
    }

    private void showMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    private int getPageIdx() {
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

    private void savePage(final int pageIdx, Bitmap pageBmp) {
        this.getBookIO().saveBitmap(this.getBook().getPage(pageIdx), pageBmp);
        new Thread(new Runnable() {
            @Override
            public void run() {
                set_book(getBook().assignNonEmpty(pageIdx));
            }
        }).start();
    }

    private void savePageInMain(int pageIdx, Bitmap pageBmp) {
        this.getBookIO().saveBitmap(this.getBook().getPage(pageIdx), pageBmp);
        this.set_book(this.getBook().assignNonEmpty(pageIdx));
    }

    private void lazySave() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(SAVE_INTERVAL_MILL);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (isDirty && getCurrentMills() - lastWritten
                                >= SAVE_INTERVAL_MILL) {
                    isDirty = false;
                    Lock bitmapLock = BookActivity.getBitmapLock();
                    bitmapLock.lock();
                    Bitmap tempBmp = null;
                    try {
                        tempBmp = pageBmp.copy(pageBmp.getConfig(), false);
                    } catch (Throwable eee) {
                        eee.printStackTrace();
                    } finally {
                        bitmapLock.unlock();
                    }
                    savePage(getPageIdx(), tempBmp);
                }
            }
        }).start();
    }

    private void ensureSave() {
        try {
            if (this.isDirty) {
                this.isDirty = false;
                this.savePageInMain(this.getPageIdx(), this.pageBmp);
            }
        } catch (Throwable eee) {
            eee.printStackTrace();
            try {
                Toast.makeText(this, "Save failed", Toast.LENGTH_SHORT).show();
            } catch (Throwable eee2) {
                eee2.printStackTrace();
            }
        }
    }

    protected void onStop() {
        if (true) {
            this.ensureSave();
        } else {
            this.savePageInMain(this.getPageIdx(), this.pageBmp);
        }
        super.onStop();
        runNormalScreen(this);
    }

    //FIXME: remove RequiresApi
    //@RequiresApi(26)
    private void share() {
        this.ensureSave();
        if (this.pageBmp != null) {
            FileOutputStream it = null;
            File path = null;
            try {
                path = File.createTempFile("share", ".png", this.getCacheDir());
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
                Uri u = FileProvider.getUriForFile(this,
                        this.getApplicationContext().getPackageName() + ".provider",
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
        this.startActivity(intent);
    }

    private void addNewPageAndGo(boolean needAsk) {
        if (needAsk) {
            PaintSelectDialog dialog = new PaintSelectDialog(this);
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
            //FIXME:重复上一张,why????
            if (this.pageBmp != null) {
                this.savePageInMain(this.pageNum - 1, this.pageBmp);
            }
        } else {
            //FIXME:空白页？？？
            if (this.emptyBmp != null) {
                this.pageBmp = this.emptyBmp; //FIXME:???
            }
            if (this.pageBmp != null) {
                this.savePageInMain(this.pageNum - 1, this.pageBmp);
                if (BookIO.USE_META_TXT) {
                    getBookIO().saveMeta(backText, this.dirUrlPath, String.format("%04d", this.pageNum - 1) + ".meta");
                }
            }
        }
        this._pageIdx = this.pageNum - 1;
        onPageIdxChange();
    }

    //删除页面
    private void removeCurrentPageAndGo() {
        if (this.pageNum <= 1) {
            BookPage page = this.getBook().getPage(this._pageIdx);
            addNewPageAndGo(false);
            this.getBook().removePage(page.getFile(), _bookIO);
        } else if (this._pageIdx <= 0) { //0页的话直接清空就可以了
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
            //FIXME:空白页？？？
            if (this.emptyBmp != null) {
                this.pageBmp = this.emptyBmp; //FIXME:???
            }
            if (this.pageBmp != null) {
                this.savePageInMain(this.pageNum - 1, this.pageBmp);
            }
            onPageIdxChange();
        } else {
            BookPage page = this.getBook().getPage(this._pageIdx);
            gotoPrevPage();
            this.getBook().removePage(page.getFile(), _bookIO);
        }

        //--------------
        this._book = null;
        set_book(getBook()); //FIXME:???重新加载
        //--------------

        onPageIdxChange();
    }

    private void gotoFirstPage() {
        this.ensureSave();
        this._pageIdx = 0;
        onPageIdxChange();
    }

    private void gotoLastPage() {
        this.ensureSave();
        this._pageIdx = this.pageNum - 1;
        onPageIdxChange();
    }

    private void gotoPrevPage() {
        this.ensureSave();
        int var1 = this.getPageIdx();
        if (var1 >= 1) {
            this._pageIdx = var1 - 1;
            onPageIdxChange();
        }
    }

    private void gotoNextPage() {
        this.ensureSave();
        int var1 = this.getPageIdx();
        this._pageIdx = var1 + 1;
        onPageIdxChange();
    }

    private void gotoGridPage() {
        Intent intent = new Intent(this, PageGridActivity.class);
        intent.setData(this.dirUrl);
        intent.putExtra(PageGridActivity.EXTRA_DIR_URL_PATH, this.dirUrlPath);
        this.startActivity(intent);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            this.handlePageIdxArg(intent);
        }
    }
    //--------------------------
    private void onClickBottomButton1(int id, boolean isToggle, boolean isClear, boolean isClick) {
        if (id == R.id.btnBold) {
            if (isToggle) {
                setBold(!isBold);
            } else if (isClear) {
                setBold(false);
            }
        } else if (id == R.id.btnItalics) {
            if (isToggle) {
                setItalics(!isItalics);
            } else if (isClear) {
                setItalics(false);
            }
        } else if (id == R.id.btnUnderline) {
            if (isToggle) {
                setUnderline(!isUnderline);
            } else {
                setUnderline(false);
            }
        } else if (id == R.id.btnFormatClear) {
            setBold(false);
            setItalics(false);
            setUnderline(false);
        } else if (id == R.id.btnAlignLeft) {
            if (isToggle) {
                if (alignType != ALIGN_TYPE_LEFT) {
                    setAlignType(ALIGN_TYPE_LEFT);
                } else {
                    setAlignType(ALIGN_TYPE_NONE);
                }
            } else {
                setAlignType(ALIGN_TYPE_NONE);
            }
        } else if (id == R.id.btnAlignCenter) {
            if (isToggle) {
                if (alignType != ALIGN_TYPE_CENTER) {
                    setAlignType(ALIGN_TYPE_CENTER);
                } else {
                    setAlignType(ALIGN_TYPE_NONE);
                }
            } else {
                setAlignType(ALIGN_TYPE_NONE);
            }
        } else if (id == R.id.btnAlignRight) {
            if (isToggle) {
                if (alignType != ALIGN_TYPE_RIGHT) {
                    setAlignType(ALIGN_TYPE_RIGHT);
                } else {
                    setAlignType(ALIGN_TYPE_NONE);
                }
            } else {
                setAlignType(ALIGN_TYPE_NONE);
            }
        } else if (id == R.id.btnAlignJustify) {
            if (isToggle) {
                if (alignType != ALIGN_TYPE_JUSTIFY) {
                    setAlignType(ALIGN_TYPE_JUSTIFY);
                } else {
                    setAlignType(ALIGN_TYPE_NONE);
                }
            } else {
                setAlignType(ALIGN_TYPE_NONE);
            }
        } else if (id == R.id.btnBullet) {
            if (isToggle) {
                if (listType != LIST_TYPE_BULLET) {
                    setListType(LIST_TYPE_BULLET);
                } else {
                    setListType(LIST_TYPE_NONE);
                }
            } else {
                setListType(LIST_TYPE_NONE);
            }
        } else if (id == R.id.btnNumber) {
            if (isToggle) {
                if (listType != LIST_TYPE_NUMBER) {
                    setListType(LIST_TYPE_NUMBER);
                } else {
                    setListType(LIST_TYPE_NONE);
                }
            } else {
                setListType(LIST_TYPE_NONE);
            }
        } else if (id == R.id.btnStyleHand) {
            if (isToggle) {
                if (styleType != STYLE_TYPE_HAND) {
                    setStyleType(STYLE_TYPE_HAND);
                } else {
                    setStyleType(STYLE_TYPE_NONE);
                }
            } else {
                setStyleType(STYLE_TYPE_NONE);
            }
        } else if (id == R.id.btnSerif) {
            if (isToggle) {
                if (styleType != STYLE_TYPE_SERIF) {
                    setStyleType(STYLE_TYPE_SERIF);
                } else {
                    setStyleType(STYLE_TYPE_NONE);
                }
            } else {
                setStyleType(STYLE_TYPE_NONE);
            }
        } else if (id == R.id.btnSans) {
            if (isToggle) {
                if (styleType != STYLE_TYPE_SANS) {
                    setStyleType(STYLE_TYPE_SANS);
                } else {
                    setStyleType(STYLE_TYPE_NONE);
                }
            } else {
                setStyleType(STYLE_TYPE_NONE);
            }
        } else if (id == R.id.btnTitle) {
            if (isToggle) {
                if (sizeType != SIZE_TYPE_TITLE) {
                    setSizeType(SIZE_TYPE_TITLE);
                } else {
                    setSizeType(SIZE_TYPE_NONE);
                }
            } else {
                setSizeType(SIZE_TYPE_NONE);
            }
        } else if (id == R.id.btnH1) {
            if (isToggle) {
                if (sizeType != SIZE_TYPE_H1) {
                    setSizeType(SIZE_TYPE_H1);
                } else {
                    setSizeType(SIZE_TYPE_NONE);
                }
            } else {
                setSizeType(SIZE_TYPE_NONE);
            }
        } else if (id == R.id.btnH2) {
            if (isToggle) {
                if (sizeType != SIZE_TYPE_H2) {
                    setSizeType(SIZE_TYPE_H2);
                } else {
                    setSizeType(SIZE_TYPE_NONE);
                }
            } else {
                setSizeType(SIZE_TYPE_NONE);
            }
        } else if (id == R.id.btnH3) {
            if (isToggle) {
                if (sizeType != SIZE_TYPE_H3) {
                    setSizeType(SIZE_TYPE_H3);
                } else {
                    setSizeType(SIZE_TYPE_NONE);
                }
            } else {
                setSizeType(SIZE_TYPE_NONE);
            }
        }
        CardView viewCard_bold = (CardView) findViewById(R.id.btnBold);
        ImageView icon_bold = (ImageView) viewCard_bold.findViewWithTag("icon");
        CardView viewCard_italics = (CardView) findViewById(R.id.btnItalics);
        ImageView icon_italics = (ImageView) viewCard_italics.findViewWithTag("icon");
        CardView viewCard_underline = (CardView) findViewById(R.id.btnUnderline);
        ImageView icon_underline = (ImageView) viewCard_underline.findViewWithTag("icon");
        if (isBold) {
            viewCard_bold.setCardBackgroundColor(0xFF1C1B1B); //黑色背景选中
            icon_bold.setImageResource(R.drawable.ic_my_para_bold_011_w);
        } else {
            viewCard_bold.setCardBackgroundColor(0x00585858);
            icon_bold.setImageResource(R.drawable.ic_my_para_bold_011);
        }
        if (isItalics) {
            viewCard_italics.setCardBackgroundColor(0xFF1C1B1B); //黑色背景选中
            icon_italics.setImageResource(R.drawable.ic_my_para_italics_012_w);
        } else {
            viewCard_italics.setCardBackgroundColor(0x00585858);
            icon_italics.setImageResource(R.drawable.ic_my_para_italics_012);
        }
        if (isUnderline) {
            viewCard_underline.setCardBackgroundColor(0xFF1C1B1B); //黑色背景选中
            icon_underline.setImageResource(R.drawable.ic_my_para_underline_013_w);
        } else {
            viewCard_underline.setCardBackgroundColor(0x00585858);
            icon_underline.setImageResource(R.drawable.ic_my_para_underline_013);
        }
        CardView viewCard_alignLeft = (CardView) findViewById(R.id.btnAlignLeft);
        ImageView icon_alignLeft = (ImageView) viewCard_alignLeft.findViewWithTag("icon");
        CardView viewCard_alignCenter = (CardView) findViewById(R.id.btnAlignCenter);
        ImageView icon_alignCenter = (ImageView) viewCard_alignCenter.findViewWithTag("icon");
        CardView viewCard_alignRight = (CardView) findViewById(R.id.btnAlignRight);
        ImageView icon_alignRight = (ImageView) viewCard_alignRight.findViewWithTag("icon");
        CardView viewCard_justify = (CardView) findViewById(R.id.btnAlignJustify);
        ImageView icon_justify = (ImageView) viewCard_justify.findViewWithTag("icon");
        if (alignType == ALIGN_TYPE_LEFT) {
            viewCard_alignLeft.setCardBackgroundColor(0xFF1C1B1B); //黑色背景选中
            icon_alignLeft.setImageResource(R.drawable.ic_my_para_left_015_w);
        } else {
            viewCard_alignLeft.setCardBackgroundColor(0x00585858);
            icon_alignLeft.setImageResource(R.drawable.ic_my_para_left_015);
        }
        if (alignType == ALIGN_TYPE_CENTER) {
            viewCard_alignCenter.setCardBackgroundColor(0xFF1C1B1B); //黑色背景选中
            icon_alignCenter.setImageResource(R.drawable.ic_my_para_center_017_w);
        } else {
            viewCard_alignCenter.setCardBackgroundColor(0x00585858);
            icon_alignCenter.setImageResource(R.drawable.ic_my_para_center_017);
        }
        if (alignType == ALIGN_TYPE_RIGHT) {
            viewCard_alignRight.setCardBackgroundColor(0xFF1C1B1B); //黑色背景选中
            icon_alignRight.setImageResource(R.drawable.ic_my_para_right_016_w);
        } else {
            viewCard_alignRight.setCardBackgroundColor(0x00585858);
            icon_alignRight.setImageResource(R.drawable.ic_my_para_right_016);
        }
        if (alignType == ALIGN_TYPE_JUSTIFY) {
            viewCard_justify.setCardBackgroundColor(0xFF1C1B1B); //黑色背景选中
            icon_justify.setImageResource(R.drawable.ic_my_para_justify_018_w);
        } else {
            viewCard_justify.setCardBackgroundColor(0x00585858);
            icon_justify.setImageResource(R.drawable.ic_my_para_justify_018);
        }
        CardView viewCard_bullet = (CardView) findViewById(R.id.btnBullet);
        ImageView icon_bullet = (ImageView) viewCard_bullet.findViewWithTag("icon");
        CardView viewCard_number = (CardView) findViewById(R.id.btnNumber);
        ImageView icon_number = (ImageView) viewCard_number.findViewWithTag("icon");
        if (listType == LIST_TYPE_BULLET) {
            viewCard_bullet.setCardBackgroundColor(0xFF1C1B1B); //黑色背景选中
            icon_bullet.setImageResource(R.drawable.ic_my_para_bullet_019_w);
        } else {
            viewCard_bullet.setCardBackgroundColor(0x00585858);
            icon_bullet.setImageResource(R.drawable.ic_my_para_bullet_019);
        }
        if (listType == LIST_TYPE_NUMBER) {
            viewCard_number.setCardBackgroundColor(0xFF1C1B1B); //黑色背景选中
            icon_number.setImageResource(R.drawable.ic_my_para_number_020_w);
        } else {
            viewCard_number.setCardBackgroundColor(0x00585858);
            icon_number.setImageResource(R.drawable.ic_my_para_number_020);
        }
        CardView viewCard_hand = (CardView) findViewById(R.id.btnStyleHand);
        ImageView icon_hand = (ImageView) viewCard_hand.findViewWithTag("icon");
        CardView viewCard_serif = (CardView) findViewById(R.id.btnSerif);
        ImageView icon_serif = (ImageView) viewCard_serif.findViewWithTag("icon");
        CardView viewCard_sans = (CardView) findViewById(R.id.btnSans);
        ImageView icon_sans = (ImageView) viewCard_sans.findViewWithTag("icon");
        if (styleType == STYLE_TYPE_HAND) {
            viewCard_hand.setCardBackgroundColor(0xFF1C1B1B); //黑色背景选中
            icon_hand.setImageResource(R.drawable.ic_my_style_hand_005_w);
        } else {
            viewCard_hand.setCardBackgroundColor(0x00585858);
            icon_hand.setImageResource(R.drawable.ic_my_style_hand_005);
        }
        if (styleType == STYLE_TYPE_SERIF) {
            viewCard_serif.setCardBackgroundColor(0xFF1C1B1B); //黑色背景选中
            icon_serif.setImageResource(R.drawable.ic_my_style_serif_006_w);
        } else {
            viewCard_serif.setCardBackgroundColor(0x00585858);
            icon_serif.setImageResource(R.drawable.ic_my_style_serif_006);
        }
        if (styleType == STYLE_TYPE_SANS) {
            viewCard_sans.setCardBackgroundColor(0xFF1C1B1B); //黑色背景选中
            icon_sans.setImageResource(R.drawable.ic_my_style_sans_007_w);
        } else {
            viewCard_sans.setCardBackgroundColor(0x00585858);
            icon_sans.setImageResource(R.drawable.ic_my_style_sans_007);
        }
        CardView viewCard_title = (CardView) findViewById(R.id.btnTitle);
        ImageView icon_title = (ImageView) viewCard_title.findViewWithTag("icon");
        CardView viewCard_h1 = (CardView) findViewById(R.id.btnH1);
        ImageView icon_h1 = (ImageView) viewCard_h1.findViewWithTag("icon");
        CardView viewCard_h2 = (CardView) findViewById(R.id.btnH2);
        ImageView icon_h2 = (ImageView) viewCard_h2.findViewWithTag("icon");
        CardView viewCard_h3 = (CardView) findViewById(R.id.btnH3);
        ImageView icon_h3 = (ImageView) viewCard_h3.findViewWithTag("icon");
        if (sizeType == SIZE_TYPE_TITLE) {
            viewCard_title.setCardBackgroundColor(0xFF1C1B1B); //黑色背景选中
            icon_title.setImageResource(R.drawable.ic_my_style_title_001_w);
        } else {
            viewCard_title.setCardBackgroundColor(0x00585858);
            icon_title.setImageResource(R.drawable.ic_my_style_title_001);
        }
        if (sizeType == SIZE_TYPE_H1) {
            viewCard_h1.setCardBackgroundColor(0xFF1C1B1B); //黑色背景选中
            icon_h1.setImageResource(R.drawable.ic_my_style_h1_002_w);
        } else {
            viewCard_h1.setCardBackgroundColor(0x00585858);
            icon_h1.setImageResource(R.drawable.ic_my_style_h1_002);
        }
        if (sizeType == SIZE_TYPE_H2) {
            viewCard_h2.setCardBackgroundColor(0xFF1C1B1B); //黑色背景选中
            icon_h2.setImageResource(R.drawable.ic_my_style_h2_003_w);
        } else {
            viewCard_h2.setCardBackgroundColor(0x00585858);
            icon_h2.setImageResource(R.drawable.ic_my_style_h2_003);
        }
        if (sizeType == SIZE_TYPE_H3) {
            viewCard_h3.setCardBackgroundColor(0xFF1C1B1B); //黑色背景选中
            icon_h3.setImageResource(R.drawable.ic_my_style_h3_004_w);
        } else {
            viewCard_h3.setCardBackgroundColor(0x00585858);
            icon_h3.setImageResource(R.drawable.ic_my_style_h3_004);
        }
    }
    //--------------------------
    private final static int iconsBottomMenu1[] = {
            R.id.colorBtn001,
            R.id.colorBtn002,
            R.id.colorBtn003,
            R.id.colorBtn004,
            R.id.colorBtn005,
            R.id.colorBtn006,
            R.id.colorBtn007,
            R.id.colorBtn008,
            R.id.colorBtn009,
    };
    private final static int iconsBottomMenu2[] = {
            R.id.colorBtn101,
            R.id.colorBtn102,
            R.id.colorBtn103,
            R.id.colorBtn104,
            R.id.colorBtn105,
            R.id.colorBtn106,
            R.id.colorBtn107,
            R.id.colorBtn108,
            R.id.colorBtn109,
    };
    private void onClickBottomMenu1(int id, boolean isClick) {
        View view = findViewById(id);
        for (int i = 0; i < iconsBottomMenu1.length; ++i) {
            View viewIcon = this.findViewById(iconsBottomMenu1[i]);
            if (viewIcon != null) {
                ((CardView) viewIcon).setCardBackgroundColor(0xFFF1EDEC); //白色背景
                //((CardView) viewIcon).setCardElevation(0.0f);
//                if (viewIcon.findViewWithTag("binding_1") != null) {
//                    ((AppCompatImageView) viewIcon.findViewWithTag("binding_1")).setBackgroundResource(
//                            getActiveIconIdSubmenu1(iconsSubmenu1[i], false));
//                }
                if (viewIcon.findViewWithTag("binding_1") != null) {
                    ((TextView) viewIcon.findViewWithTag("binding_1")).setTextColor(0xFF000000); //黑色字
                }
            }
        }
        if (view != null) {
            ((CardView) view).setCardBackgroundColor(0xFF1C1B1B); //黑色背景选中
            //((CardView) view).setCardElevation(5.0f);

            if (view.findViewWithTag("binding_1") != null) {
                ((TextView) view.findViewWithTag("binding_1")).setTextColor(0xFFFFFFFF); //白色字
            }
        }
        int color = Color.BLACK;
        //https://www.cnblogs.com/huojiaoqingchun0123/p/6753642.html
        if (id == R.id.colorBtn001) {
            color = 0xFF000000; //Black //0xFF000000;
        } else if (id == R.id.colorBtn002) {
            color = 0xFF696969; //DimGray //0xFF0000FF;
        } else if (id == R.id.colorBtn003) {
            color = 0xFF808080; //Gray //0xFFFF0000;
        } else if (id == R.id.colorBtn004) {
            color = 0xFFA9A9A9; //DarkGray //0xFF00FF00;
        } else if (id == R.id.colorBtn005) {
            color = 0xFFC0C0C0; //Silver //0xFFFFA500;
        } else if (id == R.id.colorBtn006) {
            color = 0xFFD3D3D3; //LightGray //0xFFFFFF00;
        } else if (id == R.id.colorBtn007) {
            color = 0xFFDCDCDC; //Gainsboro //0xFF800080;
        } else if (id == R.id.colorBtn008) {
            color = 0xFFF5F5F5; //WhiteSmoke//0xFFA52A2A;
        } else if (id == R.id.colorBtn009) {
            color = 0xFFFFFFFF; //White//0xFF808080;
        }
        setPenColor(color);
    }
    private void onClickBottomMenu2(int id, boolean isClick) {
        View view = findViewById(id);
        for (int i = 0; i < iconsBottomMenu2.length; ++i) {
            View viewIcon = this.findViewById(iconsBottomMenu2[i]);
            if (viewIcon != null) {
                ((CardView) viewIcon).setCardBackgroundColor(0xFFF1EDEC); //白色背景
                //((CardView) viewIcon).setCardElevation(0.0f);
//                if (viewIcon.findViewWithTag("binding_1") != null) {
//                    ((AppCompatImageView) viewIcon.findViewWithTag("binding_1")).setBackgroundResource(
//                            getActiveIconIdSubmenu1(iconsSubmenu1[i], false));
//                }
                if (viewIcon.findViewWithTag("binding_1") != null) {
                    ((TextView) viewIcon.findViewWithTag("binding_1")).setTextColor(0xFF000000); //黑色字
                }
            }
        }
        if (view != null) {
            ((CardView) view).setCardBackgroundColor(0xFF1C1B1B); //黑色背景选中
            //((CardView) view).setCardElevation(5.0f);

            if (view.findViewWithTag("binding_1") != null) {
                ((TextView) view.findViewWithTag("binding_1")).setTextColor(0xFFFFFFFF); //白色字
            }
        }
        int color = Color.BLACK;
        if (id == R.id.colorBtn101) {
            color = 0xFF000000; //Black //0xFF000000;
        } else if (id == R.id.colorBtn102) {
            color = 0xFF696969; //DimGray //0xFF0000FF;
        } else if (id == R.id.colorBtn103) {
            color = 0xFF808080; //Gray //0xFFFF0000;
        } else if (id == R.id.colorBtn104) {
            color = 0xFFA9A9A9; //DarkGray //0xFF00FF00;
        } else if (id == R.id.colorBtn105) {
            color = 0xFFC0C0C0; //Silver //0xFFFFA500;
        } else if (id == R.id.colorBtn106) {
            color = 0xFFD3D3D3; //LightGray //0xFFFFFF00;
        } else if (id == R.id.colorBtn107) {
            color = 0xFFDCDCDC; //Gainsboro //0xFF800080;
        } else if (id == R.id.colorBtn108) {
            color = 0xFFF5F5F5; //WhiteSmoke//0xFFA52A2A;
        } else if (id == R.id.colorBtn109) {
            color = 0xFFFFFFFF; //White//0xFF808080;
        }
        //setPenColor(color);
        this.setEditTextColor(color);
    }
    //--------------------------
    private final static int iconsSubmenu1[] = {
            R.id.left_toolkit_item1,
            R.id.left_toolkit_item2,
            R.id.left_toolkit_item3,
            R.id.left_toolkit_item4,
            R.id.left_toolkit_item5,
            R.id.left_toolkit_item6,
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
    public void onClickSubmenu1(int id, boolean isClick) {
        boolean isShowBottom = false;
        if (this.currentTabIdSubmenu1 == id) {
            isShowBottom = true;
        }
        this.currentTabIdSubmenu1 = id;
        View view = findViewById(id);
        for (int i = 0; i < iconsSubmenu1.length; ++i) {
            View viewIcon = this.findViewById(iconsSubmenu1[i]);
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
                canvas.setPenType(DrawAppearance.PEN_TYPE_3);
            } else if (id == R.id.left_toolkit_item4) {
                canvas.setPenType(DrawAppearance.PEN_TYPE_4);
            } else if (id == R.id.left_toolkit_item5) {
                canvas.setPenType(DrawAppearance.PEN_TYPE_5);
            } else if (id == R.id.left_toolkit_item6) {
                canvas.setPenType(DrawAppearance.PEN_TYPE_6);
            }
//            if (bottomSheetDialog1 != null) {
//                bottomSheetDialog1.show();
//            }

            this.findViewById(R.id.bottomDialog2).setVisibility(View.GONE);
            if (isShowBottom) {
                //!USE_OLD_PEN_SETTING_PANEL) { //
                if (false) { //id == R.id.left_toolkit_item6) {
                    AlertDialog dialog = new MaterialAlertDialogBuilder(BookActivity4.this, BookListActivity.getCenteredTitleThemeOverlay())
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
                    if (this.findViewById(R.id.bottomDialog1).getVisibility() == View.VISIBLE) {
                        this.findViewById(R.id.bottomDialog1).setVisibility(View.GONE);
                    } else {
                        this.findViewById(R.id.bottomDialog1).setVisibility(View.VISIBLE);
                    }
                }
            } else {
                this.findViewById(R.id.bottomDialog1).setVisibility(View.GONE);
            }
            //settingsBottomSheet.show(getSupportFragmentManager(), MainActivity.SettingsBottomSheet.TAG);
        }
    }
    public void onLongClickSubmenu1(int id, boolean isClick) {
        AlertDialog dialog = new BookActivity4BrushEditDialog(this, id).create();
        dialog.show();
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
    public void onClickSubmenu2(int id, boolean isClick) {
        boolean isShowBottom = false;
        if (this.currentTabIdSubmenu2 == id) {
            isShowBottom = true;
        }
        this.currentTabIdSubmenu2 = id;
        View view = findViewById(id);
        for (int i = 0; i < iconsSubmenu2.length; ++i) {
            View viewIcon = this.findViewById(iconsSubmenu2[i]);
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
                this.findViewById(R.id.llLeftPanel1).setVisibility(View.VISIBLE);
                this.findViewById(R.id.llLeftPanel2).setVisibility(View.GONE);
            } else if (id == R.id.left_toolkit_item22) {
                this.findViewById(R.id.llLeftPanel1).setVisibility(View.GONE);
                this.findViewById(R.id.llLeftPanel2).setVisibility(View.VISIBLE);
            }
            this.findViewById(R.id.bottomDialog1).setVisibility(View.GONE);
            if (isShowBottom) {
                if (this.findViewById(R.id.bottomDialog2).getVisibility() == View.VISIBLE) {
                    this.findViewById(R.id.bottomDialog2).setVisibility(View.GONE);
                } else {
                    this.findViewById(R.id.bottomDialog2).setVisibility(View.VISIBLE);
                }
            } else {
                this.findViewById(R.id.bottomDialog2).setVisibility(View.GONE);
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
    public void onClickSubmenu4(int id, boolean isClick) {
        this.currentTabIdSubmenu4 = id;
        View view = findViewById(id);
        for (int i = 0; i < iconsSubmenu4.length; ++i) {
            View viewIcon = this.findViewById(iconsSubmenu4[i]);
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
                //移动
                canvas.setTool(DrawCanvas.TOOLS.select);
                canvas.setEraserMode(false);
                canvas.setScaleMode(false);
            } else if (id == R.id.left_toolkit_item42) {
                //多选
                canvas.setTool(DrawCanvas.TOOLS.select);
                canvas.setEraserMode(false);
                canvas.setScaleMode(false);
            } else if (id == R.id.left_toolkit_item43) {
                //放大
                canvas.setTool(DrawCanvas.TOOLS.select);
                canvas.setEraserMode(false);
                canvas.setScaleMode(true);
            } else if (id == R.id.left_toolkit_item44) {
                //拖动
                canvas.setTool(DrawCanvas.TOOLS.pan);
            } else if (id == R.id.left_toolkit_item45) {
                //删除
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
    public void onClickTopBar(int id, boolean isClick) {
        this.currentTabIdTopBar = id;
        View view = findViewById(id);
        for (int i = 0; i < iconsTopBar.length; ++i) {
            View viewIcon = this.findViewById(iconsTopBar[i]);
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
            dtView.setVisibility(View.GONE);
            llASR.setVisibility(View.GONE);
            rl_ai.setVisibility(View.GONE);
            findViewById(R.id.left_toolkit1).setVisibility(View.VISIBLE);
            findViewById(R.id.left_toolkit2).setVisibility(View.GONE);
            findViewById(R.id.left_toolkit4).setVisibility(View.GONE);
            canvas.setTool(DrawCanvas.TOOLS.paint);
        } else if (id == R.id.top_toolkit_item2) {
            //typing
            dtViewBottom.setVisibility(View.VISIBLE);
            dtView.setVisibility(View.GONE);
            llASR.setVisibility(View.GONE);
            rl_ai.setVisibility(View.GONE);
            findViewById(R.id.left_toolkit1).setVisibility(View.GONE);
            findViewById(R.id.left_toolkit2).setVisibility(View.VISIBLE);
            findViewById(R.id.left_toolkit4).setVisibility(View.GONE);
        } else if (id == R.id.top_toolkit_item3) {
            //AI note talking
            dtViewBottom.setVisibility(View.GONE);
            dtView.setVisibility(View.GONE);
            llASR.setVisibility(View.GONE);
            rl_ai.setVisibility(View.VISIBLE);
            findViewById(R.id.left_toolkit1).setVisibility(View.GONE);
            findViewById(R.id.left_toolkit2).setVisibility(View.GONE);
            findViewById(R.id.left_toolkit4).setVisibility(View.GONE);
        } else if (id == R.id.top_toolkit_item4) {
            //Selection
            dtViewBottom.setVisibility(View.GONE);
            dtView.setVisibility(View.GONE);
            llASR.setVisibility(View.GONE);
            rl_ai.setVisibility(View.GONE);
            findViewById(R.id.left_toolkit1).setVisibility(View.GONE);
            findViewById(R.id.left_toolkit2).setVisibility(View.GONE);
            findViewById(R.id.left_toolkit4).setVisibility(View.VISIBLE);
            if (currentTabIdSubmenu4 == R.id.left_toolkit_item44) {
                canvas.setTool(DrawCanvas.TOOLS.pan); //拖动背景
            } else {
                canvas.setTool(DrawCanvas.TOOLS.select);
            }
        }
        this.findViewById(R.id.bottomDialog1).setVisibility(View.GONE);
        this.findViewById(R.id.bottomDialog2).setVisibility(View.GONE);
        if (dtView != null) {
            dtView.hideSoftInput();
        }
    }
    //--------------------------

    private TextView textViewPageInfo;
    DrawCanvas canvas;
    View dtViewBottom;
    DrawTextView dtView;
    LinearLayout llASR;
    RelativeLayout rl_ai;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        newFixedThreadPool = Executors.newFixedThreadPool(6);
        Intent intent = this.getIntent();
        boolean isInitBackText = false;
        if (intent != null) {
            this.dirUrl = intent.getData();
            this.dirUrlPath = intent.getStringExtra(EXTRA_DIRURLPATH);
            if (false) {
                this.backText = intent.getStringExtra(EXTRA_BACKTEXT);
            } else {
                //TODO: read config
            }
            this.handlePageIdxArg(intent);
            isInitBackText = true;
        }

        setContentView(R.layout.activity_book4);

        setPenColor(0xFF000000); //FIXME:初始化画笔

        if (this.dirUrlPath != null) {
            if (false) {
                ((TextView) findViewById(R.id.newTitle)).setText(getBookNameNG(this.dirUrlPath));
            }
        }

        if (false) { //for debugging
            findViewById(R.id.bookTab).setVisibility(View.VISIBLE);
        }
        findViewById(R.id.btnTitleBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.buttonBack).performClick();
            }
        });
        findViewById(R.id.btnMore).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(view);
            }
        });
        findViewById(R.id.btnTitleUndo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.buttonUndo).performClick();
            }
        });
        findViewById(R.id.btnTitleRedo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.buttonRedo).performClick();
            }
        });
        for (int id : iconsSubmenu1) {
            this.findViewById(id).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickSubmenu1(id, true);
                }
            });
            this.findViewById(id).setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    onLongClickSubmenu1(id, true);
                    return true;
                }
            });
        }
        for (int id : iconsSubmenu2) {
            this.findViewById(id).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickSubmenu2(id, true);
                }
            });
        }
        for (int id : iconsSubmenu4) {
            this.findViewById(id).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickSubmenu4(id, true);
                }
            });
        }
        for (int id : iconsTopBar) {
            this.findViewById(id).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickTopBar(id, true);
                }
            });
        }
        for (int id : iconsBottomMenu1) {
            this.findViewById(id).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickBottomMenu1(id, true);
                }
            });
        }
        for (int id : iconsBottomMenu2) {
            this.findViewById(id).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickBottomMenu2(id, true);
                }
            });
        }
        slider = (Slider) findViewById(R.id.slider);
        slider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                setSize(canvas, value);
            }
        });
        findViewById(R.id.btnPanel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (false) {
                    AppCompatImageView btnPanel = (AppCompatImageView) findViewById(R.id.btnPanel);
                    AnimationDrawable anim = (AnimationDrawable) btnPanel.getDrawable();
                    anim.start();
                }

                LinearLayout llPanel = (LinearLayout) findViewById(R.id.llPanel);
                if (llPanel != null) {
                    if (llPanel.getVisibility() == View.VISIBLE) {
                        llPanel.setVisibility(View.GONE);
                    } else {
                        llPanel.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        findViewById(R.id.closeTrans).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout llPanel = (LinearLayout) findViewById(R.id.llPanel);
                if (llPanel != null) {
                    llPanel.setVisibility(View.GONE);
                }
            }
        });
        findViewById(R.id.rlInfo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.llInfo).setVisibility(View.VISIBLE);
                findViewById(R.id.llTranscript).setVisibility(View.GONE);
                findViewById(R.id.bottomLineInfo).setVisibility(View.VISIBLE);
                findViewById(R.id.bottomLineTranscript).setVisibility(View.INVISIBLE);
            }
        });
        findViewById(R.id.rlTranscript).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.llInfo).setVisibility(View.GONE);
                findViewById(R.id.llTranscript).setVisibility(View.VISIBLE);
                findViewById(R.id.bottomLineInfo).setVisibility(View.INVISIBLE);
                findViewById(R.id.bottomLineTranscript).setVisibility(View.VISIBLE);
            }
        });
        windowPreferencesManager = new WindowPreferencesManager(this);
        bottomSheetDialog1 = new BottomSheetDialog(this);
        bottomSheetDialog1.setContentView(R.layout.cat_bottomsheet_content);
        bottomSheetDialog1.setDismissWithAnimation(true);
        windowPreferencesManager.applyEdgeToEdgePreference(bottomSheetDialog1.getWindow());
        findViewById(R.id.btnEnterFullscreen).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.llTab).setVisibility(View.GONE);
                findViewById(R.id.llTopBar).setVisibility(View.GONE);
                //findViewById(R.id.llFullscreen).setVisibility(View.VISIBLE);
                findViewById(R.id.llFullscreen2).setVisibility(View.VISIBLE);
                findViewById(R.id.llFullscreen2_demo).setVisibility(View.VISIBLE);
                findViewById(R.id.left_toolkit_global).setVisibility(View.GONE);
            }
        });
        //not used this exit button
        findViewById(R.id.btnExitFullscreen).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.llTab).setVisibility(View.VISIBLE);
                findViewById(R.id.llTopBar).setVisibility(View.VISIBLE);
                //findViewById(R.id.llFullscreen).setVisibility(View.GONE);
                findViewById(R.id.llFullscreen2).setVisibility(View.GONE);
                findViewById(R.id.llFullscreen2_demo).setVisibility(View.GONE);
                findViewById(R.id.left_toolkit_global).setVisibility(View.VISIBLE);
            }
        });
        findViewById(R.id.btnFullscreenExit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.llTab).setVisibility(View.VISIBLE);
                findViewById(R.id.llTopBar).setVisibility(View.VISIBLE);
                //findViewById(R.id.llFullscreen).setVisibility(View.GONE);
                findViewById(R.id.llFullscreen2).setVisibility(View.GONE);
                findViewById(R.id.llFullscreen2_demo).setVisibility(View.GONE);
                findViewById(R.id.left_toolkit_global).setVisibility(View.VISIBLE);
            }
        });
        findViewById(R.id.llFullscreen2_demo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.llFullscreen2_demo).setVisibility(View.GONE);

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
            NestedScrollView arBottomSheet = this.findViewById(R.id.ar_bottom_sheet);
            View ar_recording_check_view = findViewById(R.id.ar_recording_check_view);
            BottomSheetBehavior<NestedScrollView> behavior = BottomSheetBehavior.from(arBottomSheet);
            ar_recording_check_view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (behavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        //textViewPageInfo
                        TextView textViewPageInfoBottom = (TextView) findViewById(R.id.textViewPageInfoBottom);
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
                        findViewById(R.id.rl_bottom_sheet).requestLayout();
                        behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                        findViewById(R.id.rl_bottom_sheet).setVisibility(View.VISIBLE);
                    }
                }, 500);
            }
            findViewById(R.id.addPageButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    behavior.setState(BottomSheetBehavior.STATE_HIDDEN);//BottomSheetBehavior.STATE_COLLAPSED);
                    addNewPageAndGo(false);
                }
            });
            findViewById(R.id.cardShowPageNumInfo).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    behavior.setState(BottomSheetBehavior.STATE_HIDDEN);//BottomSheetBehavior.STATE_COLLAPSED);
                    AlertDialog dialog = new BookActivity4PageGridDialog(BookActivity4.this,
                            BookActivity4.this.dirUrl,
                            BookActivity4.this.dirUrlPath)
                            .create();
                    dialog.show();
                }
            });
        }
        {
            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    RecordingFragment fragment = (RecordingFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_recording);
                    if (fragment != null && fragment.button1 != null) {
                        fragment.button1.performClick();
                        isRecording = !isRecording;
                    }
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                    AppCompatImageView btnPanel = (AppCompatImageView) findViewById(R.id.btnPanel);
                    AnimationDrawable anim = (AnimationDrawable) btnPanel.getDrawable();
                    if (isRecording) { //FIXME: use var not good
                        anim.start();
                        findViewById(R.id.startRecord).setVisibility(View.GONE);
                        findViewById(R.id.stopRecord).setVisibility(View.VISIBLE);
                    } else {
                        anim.stop();
                        findViewById(R.id.startRecord).setVisibility(View.VISIBLE);
                        findViewById(R.id.stopRecord).setVisibility(View.GONE);
                    }
                    Toast.makeText(BookActivity4.this, "total : " + adapter.getCount(), Toast.LENGTH_LONG).show();
                }
            };
            findViewById(R.id.startRecord).setOnClickListener(onClickListener);
            findViewById(R.id.stopRecord).setOnClickListener(onClickListener);
            ListView viewListViewBook = (ListView) this.findViewById(R.id.viewListViewBook);
            String meetingId = "";
            String agendaId = "";
            adapter = new BookReaderItemsAdapter(this, meetingId, agendaId);
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
                            PlayerDialog playerDialog = new PlayerDialog(BookActivity4.this, item);
                            playerDialog.show();
                        }

                    }
                }
            });
            viewListViewBook.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> av, View v, int position, long id) {
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(BookActivity4.this);
                    final RecordingItem item = (RecordingItem)adapter.getItem(position);
                    if (item.getRecType() != null
                            && item.getRecType().equals("text")) {
                        builder.setTitle("Sync recognition result")
                                .setItems(new String[] {
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
                                .setItems(new String[] {
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
                                                    startActivity(new Intent(BookActivity4.this, DictResultActivity.class).putExtra(DictResultActivity.EXTRA_RECORDING_ID, item.getId()));
                                                    break;

                                                case 1: {
                                                    //Toast.makeText(MainActivity.this, item.getFilePath(), Toast.LENGTH_SHORT).show();
                                                    RecognizeDialog recogizeDialog = new RecognizeDialog(BookActivity4.this, item, RecognizeDialog.LANG_CHINESE);
                                                    recogizeDialog.show();
                                                }
                                                break;

                                                case 2: {
                                                    //Toast.makeText(MainActivity.this, item.getFilePath(), Toast.LENGTH_SHORT).show();
                                                    RecognizeDialog recogizeDialog = new RecognizeDialog(BookActivity4.this, item, RecognizeDialog.LANG_CHINESE_GD);
                                                    recogizeDialog.show();
                                                }
                                                break;

                                                case 3: {
                                                    //Toast.makeText(MainActivity.this, item.getFilePath(), Toast.LENGTH_SHORT).show();
                                                    RecognizeDialog recogizeDialog = new RecognizeDialog(BookActivity4.this, item, RecognizeDialog.LANG_ENGLISH);
                                                    recogizeDialog.show();
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


        findViewById(R.id.btnFullscreenUndo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.btnTitleUndo).performClick();
            }
        });
        findViewById(R.id.btnFullscreenRedo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.btnTitleRedo).performClick();
            }
        });
        findViewById(R.id.btnFullscreenBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.btnTitleBack).performClick();
            }
        });
        findViewById(R.id.btnBold).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickBottomButton1(R.id.btnBold, true, false, true);
            }
        });
        findViewById(R.id.btnItalics).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickBottomButton1(R.id.btnItalics, true, false, true);
            }
        });
        findViewById(R.id.btnUnderline).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickBottomButton1(R.id.btnUnderline, true, false, true);
            }
        });
        findViewById(R.id.btnFormatClear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickBottomButton1(R.id.btnFormatClear, false, true, true);
            }
        });
        findViewById(R.id.btnAlignLeft).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickBottomButton1(R.id.btnAlignLeft, true, false, true);
            }
        });
        findViewById(R.id.btnAlignCenter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickBottomButton1(R.id.btnAlignCenter, true, false, true);
            }
        });
        findViewById(R.id.btnAlignRight).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickBottomButton1(R.id.btnAlignRight, true, false, true);
            }
        });
        findViewById(R.id.btnAlignJustify).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickBottomButton1(R.id.btnAlignJustify, true, false, true);
            }
        });
        findViewById(R.id.btnBullet).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickBottomButton1(R.id.btnBullet, true, false, true);
            }
        });
        findViewById(R.id.btnNumber).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickBottomButton1(R.id.btnNumber, true, false, false);
            }
        });
        findViewById(R.id.btnStyleHand).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickBottomButton1(R.id.btnStyleHand, true, false, false);
            }
        });
        findViewById(R.id.btnSerif).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickBottomButton1(R.id.btnSerif, true, false, true);
            }
        });
        findViewById(R.id.btnSans).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickBottomButton1(R.id.btnSans, true, false, true);
            }
        });
        findViewById(R.id.btnTitle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickBottomButton1(R.id.btnTitle, true, false, true);
            }
        });
        findViewById(R.id.btnH1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickBottomButton1(R.id.btnH1, true, false, false);
            }
        });
        findViewById(R.id.btnH2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickBottomButton1(R.id.btnH2, true, false, true);
            }
        });
        findViewById(R.id.btnH3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickBottomButton1(R.id.btnH3, true, false, true);
            }
        });


        ActionBar topAppBar = getSupportActionBar();
        if (topAppBar != null) {
            topAppBar.setIcon(R.mipmap.ic_launcher);
            topAppBar.setHomeButtonEnabled(true);
            topAppBar.setDisplayHomeAsUpEnabled(true);
            if (!Config.USE_ACTIONBAR) {
                topAppBar.hide();
            }
        }
        findViewById(R.id.buttonBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //FIXME:退出立即保存
                if (SAVING_ASYNC) {
                    if (task == null) {
                        task = new SavingTask(true);
                        task.executeOnExecutor(newFixedThreadPool);
                    }
                } else {
                    if (false) {
                        ensureSave();
                    } else {
                        savePageInMain(getPageIdx(), pageBmp);
                    }
                    finish();
                }
            }
        });
        findViewById(R.id.buttonPen2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPen2();
            }
        });
        findViewById(R.id.buttonEraser2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onEraser2();
            }
        });

        findViewById(R.id.buttonPenColor).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPenColor();
            }
        });
        findViewById(R.id.buttonPaint).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSelectPaint();
            }
        });
        findViewById(R.id.buttonPen).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPen();
            }
        });
        findViewById(R.id.buttonBrush).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBrush();
            }
        });
        findViewById(R.id.buttonEraser).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onEraser();
            }
        });

//        selectPenOrEraser(true);

        findViewById(R.id.buttonUndo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onUndo();
            }
        });
        findViewById(R.id.buttonRedo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRedo();
            }
        });
        findViewById(R.id.buttonGrid).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SAVING_ASYNC) {
                    if (task == null) {
                        task = new SavingTask(false);
                        task.executeOnExecutor(newFixedThreadPool);
                    }
                } else {
                    gotoGridPage();
                }
            }
        });
        findViewById(R.id.buttonFirstPage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoFirstPage();
            }
        });
        findViewById(R.id.buttonPrevPage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoPrevPage();
            }
        });
        findViewById(R.id.buttonNextPage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoNextPage();
            }
        });
        findViewById(R.id.buttonLastPage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoLastPage();
            }
        });
        findViewById(R.id.buttonAddPage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewPageAndGo(true);
            }
        });
        findViewById(R.id.buttonRemovePage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeCurrentPageAndGo();
            }
        });
        findViewById(R.id.buttonShare).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    share();
                }
            }
        });
        textViewPageInfo = (TextView) findViewById(R.id.textViewPageInfo);
        onCreateAct();
        if (BookIO.USE_META_TXT) {
            if (isInitBackText && backText != null) {
                //如果是创建的才会走这里
                getBookIO().saveMeta(backText, this.dirUrlPath, "0000.meta");
            }
        }









        //FIXME:throw new RuntimeException("not implemented");
        canvas = (DrawCanvas) findViewById(R.id.canvas);
        canvas.setPenType(DrawAppearance.PEN_TYPE_1);
        dtView = (DrawTextView) findViewById(R.id.dtView);
        dtView.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (dtView != null) {
                    dtView.hideSoftInput();
                }
            }
        }, 100);
        llASR = (LinearLayout) findViewById(R.id.llASR);
        rl_ai = (RelativeLayout) findViewById(R.id.rl_ai);
        dtViewBottom = (View) findViewById(R.id.dtViewBottom);
        dtViewBottom.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int ea = event.getAction();
                switch (ea) {
                    case MotionEvent.ACTION_DOWN:
                        // 获取触摸事件触摸位置的原始X坐标
                        int lastX = (int) event.getX();
                        int lastY = (int) event.getY();
                        dtViewBottom.setVisibility(View.GONE);
                        dtView.setVisibility(View.VISIBLE);
                        dtView.init2(lastX, lastY, "", BookActivity4.this.editTextColor, new DrawTextView.CallBackListener() {
                            @Override
                            public void onUpdate(DrawPoint drawPoint) {

                            }

                            @Override
                            public void onSave(DrawPoint drawPoint) {
                                if (drawPoint != null && drawPoint.getDrawText() != null) {
                                    if (true) {
                                        Paint paint = new Paint();
//                                        paint.setColor(0xFFFF0000);
//                                        paint.setTextSize(sp2px(BookActivity4.this, 24));
                                        drawText(canvas,
                                                drawPoint.getDrawText().getStr(),
                                                (int) drawPoint.getDrawText().getX(),
                                                (int) drawPoint.getDrawText().getY(),
                                                paint,
                                                isBold, isItalics, isUnderline, styleType, editTextColor
                                        );
                                    } else {
                                        Paint paint = new Paint();
                                        paint.setColor(0xFFFF0000);
                                        paint.setTextSize(18 * 5);
                                        drawText(canvas, "hello", 100, 100, paint, false, false, false, 0, 0xFF000000);
                                    }
                                }
                                if (false) {
                                    findViewById(R.id.top_toolkit_item1).performClick(); //返回绘画模式
                                } else {
                                    //保留在编辑模式
                                    //FIXME:调用点击
                                    if (true) {
                                        findViewById(R.id.top_toolkit_item2).performClick();
                                    } else {
                                        dtViewBottom.setVisibility(View.VISIBLE);
                                        dtView.setVisibility(View.GONE);
                                        llASR.setVisibility(View.GONE);
                                        rl_ai.setVisibility(View.GONE);
                                        findViewById(R.id.left_toolkit1).setVisibility(View.GONE);
                                        findViewById(R.id.left_toolkit2).setVisibility(View.VISIBLE);
                                        findViewById(R.id.left_toolkit4).setVisibility(View.GONE);
                                    }
                                    //skip, keep in text toolkit
                                }
                            }
                        });
                        break;

                    case MotionEvent.ACTION_MOVE:
                        break;

                    case MotionEvent.ACTION_UP:
                        break;
                }
                return true; //阻止冒泡，阻止绘画
            }
        });
        try {
            BookPage page = getBook().getPage(getPageIdx());
            Bitmap initBmp = getBookIO().loadBitmapOrNull(page);
            Bitmap bgBmp = getBookIO().loadBgOrNull(getBook());
            try {
                String metaTxt = getBookIO().loadMetaPng(page.getFile());
                JSONObject item = new JSONObject(metaTxt);
                if (item != null) {
                    curPattern = item.optString("pattern");
                    backText = curPattern;
                }
            } catch (Throwable eee) {
                eee.printStackTrace();
            }
            init(canvas, initBmp, bgBmp, initialPageIdx, this);
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

        //恢复画笔选择初始状态
        isPenEraserBrush = 1;
        selectPenOrEraser(1);
        setPenEraserBrush(canvas,1);

        onClickTopBar(iconsTopBar[0], false); //init

        runFullScreen(this);
    }

    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    @SuppressLint("GestureBackNavigation")
    @Override
    public void onBackPressed() {
        //FIXME:
        //FIXME:退出立即保存
        if (SAVING_ASYNC) {
            if (task == null) {
                task = new SavingTask(true);
                task.executeOnExecutor(newFixedThreadPool);
            }
        } else {
            if (false) {
                ensureSave();
            } else {
                savePageInMain(getPageIdx(), pageBmp);
            }
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.book, menu);
        return true;
    }

    int isPenEraserBrush = 1; //0:Eraser;1:Pen;2:Brush //初始状态是pen
    //    boolean isEraser = false;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.pen) {
            onPen();
        } else if (item.getItemId() == R.id.eraser) {
            onEraser();
        } else if (item.getItemId() == R.id.undo) {
            onUndo();
        } else if (item.getItemId() == R.id.redo) {
            onRedo();
        } else if (item.getItemId() == R.id.grid) {
            if (SAVING_ASYNC) {
                if (task == null) {
                    task = new SavingTask(false);
                    task.executeOnExecutor(newFixedThreadPool);
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
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
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

                }
            });
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {

                }
            });
            dialog.show();
        } else if (false) {
            MaterialColorDialog dialog = new MaterialColorDialog(this);
            dialog.show();
        } else {
            SimpleColorDialog dialog = new SimpleColorDialog(this);
            dialog.show();
        }
    }
    private void onSelectPaint() {
        PaintSelectDialog dialog = new PaintSelectDialog(this);
        dialog.show();
    }

    private void onPen2() {
        if (isPenEraserBrush != 1) {
            isPenEraserBrush = 1;
            selectPenOrEraser(1);
            setPenEraserBrush(canvas, 1);
        }
        if (findViewById(R.id.bookTab).getVisibility() == View.VISIBLE) { //debug mode
            onLineThicknessButton();
        }
    }
    private void onEraser2() {
        if (isPenEraserBrush != 0) {
            isPenEraserBrush = 0;
            selectPenOrEraser(0);
            setPenEraserBrush(canvas,0);
        }
    }

    private void onPen() {
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
            selectPenOrEraser(1);
            //canvas.penOrEraser(true);
            setPenEraserBrush(canvas, 1);
        }
    }
    private void onBrush() {
        if (isPenEraserBrush != 2) {
            isPenEraserBrush = 2;
            //selectPenOrEraser(false);
            selectPenOrEraser(2);
            //canvas.penOrEraser(false);
            setPenEraserBrush(canvas, 2);
        }
    }
    private void onEraser() {
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
            selectPenOrEraser(0);
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
    private void selectPenOrEraser(int v) {
        if (v == 1) {
            findViewById(R.id.buttonPen).setEnabled(false);
            findViewById(R.id.buttonEraser).setEnabled(true);
            findViewById(R.id.buttonBrush).setEnabled(true);
            ((ImageButton) findViewById(R.id.buttonPen)).setImageResource(R.drawable.ic_baseline_edit_24);
            ((ImageButton) findViewById(R.id.buttonEraser)).setImageResource(R.mipmap.eraser_button_selected);
            ((ImageButton)findViewById(R.id.buttonBrush)).setImageResource(R.drawable.ic_baseline_brush_24_selected);
        } else if (v == 2) {
            findViewById(R.id.buttonPen).setEnabled(true);
            findViewById(R.id.buttonEraser).setEnabled(true);
            findViewById(R.id.buttonBrush).setEnabled(false);
            ((ImageButton) findViewById(R.id.buttonPen)).setImageResource(R.drawable.ic_baseline_edit_24_selected);
            ((ImageButton) findViewById(R.id.buttonEraser)).setImageResource(R.mipmap.eraser_button_selected);
            ((ImageButton)findViewById(R.id.buttonBrush)).setImageResource(R.drawable.ic_baseline_brush_24);
        } else if (v == 0){
            findViewById(R.id.buttonPen).setEnabled(true);
            findViewById(R.id.buttonEraser).setEnabled(false);
            findViewById(R.id.buttonBrush).setEnabled(true);
            ((ImageButton)findViewById(R.id.buttonPen)).setImageResource(R.drawable.ic_baseline_edit_24_selected);
            ((ImageButton)findViewById(R.id.buttonEraser)).setImageResource(R.mipmap.eraser_button);
            ((ImageButton)findViewById(R.id.buttonBrush)).setImageResource(R.drawable.ic_baseline_brush_24_selected);
        }

        if (v == 1) {
            ((CardView)findViewById(R.id.buttonPen2Wrapper)).setCardBackgroundColor(0xFFCCCCCC);
            ((CardView)findViewById(R.id.buttonEraser2Wrapper)).setCardBackgroundColor(0xFFFFFFFF);
        } else {
            ((CardView)findViewById(R.id.buttonPen2Wrapper)).setCardBackgroundColor(0xFFFFFFFF);
            ((CardView)findViewById(R.id.buttonEraser2Wrapper)).setCardBackgroundColor(0xFFCCCCCC);
        }
    }

    private void onUndo() {
        undoCount = undoCount + 1;
        canvas.undo();
    }
    private void onRedo() {
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


    private void handlePageIdxArg(Intent intent) {
        int argPageIdx = intent.getIntExtra(PAGE_IDX, -1);
        if (argPageIdx != -1) {
            this.initialPageIdx = argPageIdx;
        }
    }

    public static ReentrantLock getBitmapLock() {
        return bitmapLock;
    }

    public void setPenColor(int color) {
//        ImageButton buttonPenColor = (ImageButton) this.findViewById(R.id.buttonPenColor);
//
//        @SuppressLint("UseCompatLoadingForDrawables")
//        Drawable it = this.getDrawable(R.drawable.ic_baseline_color_lens_24);
//        DrawableCompat.setTintList(it, ColorStateList.valueOf(color));
//        buttonPenColor.setImageDrawable(it);
        CardView cvColorSel = this.findViewById(R.id.cvColorSel);
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

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                a.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                a.getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER;
                a.getWindow().setAttributes(a.getWindow().getAttributes());
            }

            KeyboardsMod.hideNavigation(a);


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
        }
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
        public SavingTask(boolean isBack) {
            if (isBack) {
                //TODO:
                //https://github.com/antwankakki/FabricView/wiki
                if (mOnUpdateBmpListener != null) {
                    Bitmap fullResult = getCanvasBitmap(canvas);
                    mOnUpdateBmpListener.onUpdateBmp(fullResult);
                }
            }
            this.mIsBack = isBack;
            createWaitingProgressDialog();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                savePageInMain(getPageIdx(), pageBmp);
            } catch (Throwable eee) {
                eee.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            cancelWaitingProgressDialog();
            task = null;
            if (this.mIsBack) {
                finish();
            } else {
                gotoGridPage();
            }
        }
    }

    private SavingTask task = null;
    private ExecutorService newFixedThreadPool;
    private ProgressDialog mProgressDialog = null; // 对话框对象
    protected void createWaitingProgressDialog() {
        if (mProgressDialog == null || !mProgressDialog.isShowing()) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setTitle("");
            mProgressDialog.setMessage("Saving, please wait...");
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }
    }
    protected void cancelWaitingProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }


    private float mCurrentInkLineThickness = 4.5f;
    public float getInkLineThickness() {
        return mCurrentInkLineThickness;
    }
    public void setInkLineThickness(float val) {
        mCurrentInkLineThickness=val;
        setSize(canvas, val);
    }
    private void onLineThicknessButton()  {
        float val = getInkLineThickness();
        ImageButton btn = (ImageButton)findViewById(R.id.buttonPen2);
        LineWidthDialog.show(this, btn, val,
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
            canvas.setTool(DrawCanvas.TOOLS.paint); //橡皮擦
        } else if (index == 1) {
            canvas.setTool(DrawCanvas.TOOLS.paint); //画笔1
        } else if (index == 2) {
            canvas.setTool(DrawCanvas.TOOLS.paint); //画笔2
        }
        canvas.penEraserBrush = index;
    }
    CanvasBoox.OnUpdateBmpListener mOnUpdateBmpListener;
    @Override
    protected void onDestroy() {
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
    private void onPageIdx(DrawCanvas canvas, int idx, CanvasBoox.OnLoadBitmapListener bitmapLoader) {
        //TODO:
        if (canvas != null) {
            canvas.onPageIdx(idx, bitmapLoader);
        }
    }
    private void setBackText(DrawCanvas canvas, String backText) {
        //TODO:
        if (backText != null && canvas != null) {
            if (backText.equals(FileMeta.NONE)) {
                setBackgroundMode(canvas, FabricView.BACKGROUND_STYLE_BLANK);
            } else if (backText.equals(FileMeta.LINED)) {
                setBackgroundMode(canvas, FabricView.BACKGROUND_STYLE_NOTEBOOK_PAPER);
            } else if (backText.equals(FileMeta.DOTTED)) {
                setBackgroundMode(canvas, FabricView.BACKGROUND_STYLE_DOT_PAPER);
            } else if (backText.equals(FileMeta.GRAPH)) {
                setBackgroundMode(canvas, FabricView.BACKGROUND_STYLE_GRAPH_PAPER);
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
    private void drawText(DrawCanvas canvas, String text, int x, int y, Paint p, boolean isBold,
                          boolean isItalics,
                          boolean isUnderline,
                          int styleType, int pointsTextColor) {
        if (canvas != null) {
            canvas.drawText(text, x, y, p, isBold,
                isItalics,
                isUnderline,
                styleType, pointsTextColor);
        }
    }
    private void drawImage(DrawCanvas canvas, int x, int y, int width, int height, Bitmap pic, boolean needMap) {
        if (canvas != null) {
            canvas.drawImage(x, y, width, height, pic, needMap);
        }
    }
    private Bitmap getCanvasBitmap(DrawCanvas canvas) {
        return canvas != null ? canvas.toBitmap(false) : null;
    }
    private void setColor(DrawCanvas canvas, int color) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("strokeColor", color); //"strokeColor" or "fillColor"
        editor.apply();
    }
    private void setSize(DrawCanvas canvas, float size) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("strokeSize", Float.toString(size)); //"strokeColor" or "fillColor"
        editor.apply();
    }
    private void setBackgroundMode(DrawCanvas canvas, int mode) {
        canvas.setBackgroundMode(mode);
    }
    public void updateInfoBar() {

    }

    private void showPopupMenu(View view) {
        //see LineWidthDialog
        CopyCutMenuDialog.show(this, view, (getPageIdx() + 1), pageNum, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view != null) {
                    if (view.getId() == R.id.popButtonGrid) {
                        if (false) {
                            notifyForceSave(false);
                            if (SAVING_ASYNC) {
                                if (task == null) {
                                    task = new SavingTask(false);
                                    task.executeOnExecutor(newFixedThreadPool);
                                }
                            } else {
                                gotoGridPage();
                            }
                        } else {
                            AlertDialog dialog = new BookActivity4PageGridDialog(BookActivity4.this,
                                    BookActivity4.this.dirUrl,
                                    BookActivity4.this.dirUrlPath)
                                    .create();
                            dialog.show();
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
                            AlertDialog dialog = new MaterialAlertDialogBuilder(BookActivity4.this, BookListActivity.getCenteredTitleThemeOverlay())
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
                    } else if (view.getId() == R.id.popTextViewRenameFile) {
                        AlertDialog dialog = new MaterialAlertDialogBuilder(BookActivity4.this, BookListActivity.getCenteredTitleThemeOverlay())
                                //.setTitle(title)
                                .setView(R.layout.activity_booklist_dialog1)
                                .setCancelable(true)
                                .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        TextView input = ((AlertDialog) dialog).findViewById(R.id.textState);
                                        //Toast.makeText(BookListActivity.this, input.getText(), Toast.LENGTH_LONG).show();
                                        boolean isFailed = false;
                                        if (_bookDir == null || _bookDir.getName() == null ||!_bookDir.getName().startsWith(FastFile.USE_SKETCH_PREFIX)) {
                                            isFailed = true;
                                        }
                                        String folder = _bookDir.getFilePath();
                                        if (folder == null ||
                                                !new File(folder, FastFile.USE_SKETCH_CONFIG).exists() ||
                                                !new File(folder, FastFile.USE_SKETCH_CONFIG).canWrite()
                                            ) {
                                            isFailed = true;
                                        }
                                        try {
                                            File file_2 = new File(folder, FastFile.USE_SKETCH_CONFIG);
                                            String str = FastFile.loadMetaText(file_2);
                                            JSONObject item = new JSONObject(str);
                                            item.put(FastFile.USE_SKETCH_CONFIG_DISPNAME, input.getText().toString());
                                            FastFile.saveMetaText(file_2, item.toString());
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            isFailed = true;
                                        }
                                        if (isFailed) {
                                            new MaterialAlertDialogBuilder(BookActivity4.this, BookListActivity.getCenteredTitleThemeOverlay())
                                                    .setTitle("Error")
                                                    .setMessage("Rename failed : " + _bookDir.getFilePath() + ",\n" +
                                                            "please check the path starts with '" + FastFile.USE_SKETCH_PREFIX + "' prefix, " +
                                                            "and make sure " + FastFile.USE_SKETCH_CONFIG + " file exists.")
                                                    .setPositiveButton("OK", null)
                                                    .show();
                                        } else {
                                            onCreateAct();
                                            try {
                                                ((TextView) findViewById(R.id.newTitle)).setText(_bookDir.getDisplayName());
                                            } catch (Throwable eee) {
                                                eee.printStackTrace();
                                            }
                                        }
                                    }
                                })
                                .setNegativeButton("Cancel", null)
                                .create();
                        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                            @Override
                            public void onShow(DialogInterface dialog) {
//                                TextView tvDialogTitle = ((AlertDialog) dialog).findViewById(R.id.tvDialogTitle);
//                                tvDialogTitle.setText("Rename note");
                                TextView input = ((AlertDialog) dialog).findViewById(R.id.textState);
                                try {
                                    input.setText(_bookDir.getDisplayName());
                                } catch (Throwable eee) {
                                    eee.printStackTrace();
                                }
                            }
                        });
                        dialog.show();
                    } else if (view.getId() == R.id.popTextViewPageBackground) {
                        AlertDialog dialog = new MaterialAlertDialogBuilder(BookActivity4.this, BookListActivity.getCenteredTitleThemeOverlay())
                                //.setTitle(title)
                                .setView(R.layout.activity_booklist_dialog2)
                                .setCancelable(false)
                                .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        RadioButton radioLine = ((AlertDialog) dialog).findViewById(R.id.radio1);
                                        RadioButton radioBlank = ((AlertDialog) dialog).findViewById(R.id.radio2);
                                        RadioButton radioGrid = ((AlertDialog) dialog).findViewById(R.id.radio3);
                                        RadioButton radioDotted = ((AlertDialog) dialog).findViewById(R.id.radio4);
                                        RadioButton radioInfinite = ((AlertDialog) dialog).findViewById(R.id.radio5);
                                        String backText = null;
                                        if (radioLine.isChecked()) {
                                            backText = FileMeta.LINED;
                                        } else if (radioBlank.isChecked()) {
                                            backText = FileMeta.NONE;
                                        } else if (radioGrid.isChecked()) {
                                            backText = FileMeta.GRAPH;
                                        } else if (radioDotted.isChecked()) {
                                            backText = FileMeta.DOTTED;
                                        } else if (radioInfinite.isChecked()) {
                                            backText = FileMeta.NONE;
                                        }
                                        if (backText != null) {
                                            setBackText(canvas, backText);
                                        }
                                        if (BookIO.USE_META_TXT) {
                                            getBookIO().saveMeta(backText, BookActivity4.this.dirUrlPath, String.format("%04d", BookActivity4.this.pageNum - 1) + ".meta");
                                        }
                                    }
                                })
                                .setNegativeButton("Cancel", null)
                                .create();
                        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                            @Override
                            public void onShow(DialogInterface dialogInterface) {
                                AlertDialog dialog = (AlertDialog) dialogInterface;
                                int[] ll_ids = {
                                        R.id.llradio1,
                                        R.id.llradio2,
                                        R.id.llradio3,
                                        R.id.llradio4,
                                        R.id.llradio5,
                                };
                                int[] ids = {
                                        R.id.radio1, //Note book
                                        R.id.radio2, //Blank
                                        R.id.radio3, //Grid
                                        R.id.radio4, //Dotted
                                        R.id.radio5, //Infinite canvas
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
                                if (canvas != null) {
                                    if (canvas.getBackgroundMode() == FabricView.BACKGROUND_STYLE_GRAPH_PAPER) {
                                        RadioButton input = (RadioButton)dialog.findViewById(R.id.radio3);
                                        input.setChecked(true);
                                    } else if (canvas.getBackgroundMode() == FabricView.BACKGROUND_STYLE_NOTEBOOK_PAPER) {
                                        RadioButton input = (RadioButton)dialog.findViewById(R.id.radio1);
                                        input.setChecked(true);
                                    } else if (canvas.getBackgroundMode() == FabricView.BACKGROUND_STYLE_DOT_PAPER) {
                                        RadioButton input = (RadioButton)dialog.findViewById(R.id.radio4);
                                        input.setChecked(true);
                                    } else if (canvas.getBackgroundMode() == FabricView.BACKGROUND_STYLE_BLANK) {
                                        RadioButton input = (RadioButton)dialog.findViewById(R.id.radio2);
                                        input.setChecked(true);
                                    }
                                }
                            }
                        });
                        dialog.show();
                    }
                }
            }
        });
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
    private boolean isBold = false;
    public void setBold(boolean bold) {
        this.isBold = bold;
        setBoldItalicsStyle();
    }
    public void setBoldItalicsStyle() {
        if (this.dtView != null && this.dtView.mEtTextEdit != null) {
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
            this.dtView.mEtTextEdit.setTypeface(font);
        }
    }
    private boolean isItalics = false;
    public void setItalics(boolean italics) {
        this.isItalics = italics;
        setBoldItalicsStyle();
    }
    private boolean isUnderline = false;
    public void setUnderline(boolean isUnderline) {
        this.isUnderline = isUnderline;
    }
    public final static int ALIGN_TYPE_NONE = 0;
    public final static int ALIGN_TYPE_LEFT = 1;
    public final static int ALIGN_TYPE_CENTER = 2;
    public final static int ALIGN_TYPE_RIGHT = 3;
    public final static int ALIGN_TYPE_JUSTIFY = 4;
    private int alignType = ALIGN_TYPE_NONE;
    public void setAlignType(int alignType) {
        this.alignType = alignType;
        if (this.dtView != null && this.dtView.mEtTextEdit != null) {
            if (alignType == ALIGN_TYPE_NONE ||
                    alignType == ALIGN_TYPE_LEFT ||
                    alignType == ALIGN_TYPE_JUSTIFY) {
                this.dtView.mEtTextEdit.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            } else if (alignType == ALIGN_TYPE_CENTER) {
                this.dtView.mEtTextEdit.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            } else if (alignType == ALIGN_TYPE_RIGHT) {
                this.dtView.mEtTextEdit.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
            }
        }
    }
    public final static int LIST_TYPE_NONE = 0;
    public final static int LIST_TYPE_BULLET = 1;
    public final static int LIST_TYPE_NUMBER = 2;
    private int listType = LIST_TYPE_NONE;
    public void setListType(int listType) {
        this.listType = listType;
    }
    public final static int STYLE_TYPE_NONE = 0;
    public final static int STYLE_TYPE_HAND = 1;
    public final static int STYLE_TYPE_SERIF = 2;
    public final static int STYLE_TYPE_SANS = 3;
    private int styleType = STYLE_TYPE_NONE;
    public void setStyleType(int styleType) {
        this.styleType = styleType;
        setBoldItalicsStyle();
    }
    private final static int SIZE_TYPE_NONE = 0;
    private final static int SIZE_TYPE_TITLE = 1;
    private final static int SIZE_TYPE_H1= 2;
    private final static int SIZE_TYPE_H2 = 3;
    private final static int SIZE_TYPE_H3 = 4;
    private int sizeType = SIZE_TYPE_NONE;
    public void setSizeType(int sizeType) {
        this.sizeType = sizeType;
        if (this.dtView != null && this.dtView.mEtTextEdit != null) {
            if (sizeType == SIZE_TYPE_NONE) {
                this.dtView.mEtTextEdit.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 32);
            } else if (sizeType == SIZE_TYPE_TITLE) {
                this.dtView.mEtTextEdit.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 48);
            } else if (sizeType == SIZE_TYPE_H1) {
                this.dtView.mEtTextEdit.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 42);
            } else if (sizeType == SIZE_TYPE_H2) {
                this.dtView.mEtTextEdit.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 38);
            } else if (sizeType == SIZE_TYPE_H3) {
                this.dtView.mEtTextEdit.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 36);
            }
        }
    }
    private int editTextColor = 0xFF000000;
    public void setEditTextColor(int editTextColor) {
        this.editTextColor = editTextColor;
        if (this.dtView != null && this.dtView.mEtTextEdit != null) {
            this.dtView.mEtTextEdit.setTextColor(editTextColor);
        }
    }

    public void openPage(int pageIdx) {
        this.ensureSave();
        this._pageIdx = pageIdx;
        onPageIdxChange();
    }

    public void showNewBrushDialog() {
        AlertDialog dialog = new MaterialAlertDialogBuilder(BookActivity4.this, BookListActivity.getCenteredTitleThemeOverlay())
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
        Drawable drawable = ContextCompat.getDrawable(BookActivity4.this, R.mipmap.ic_launcher);
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
                        InputStream inputStream = BookActivity4.this.getContentResolver().openInputStream(uri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        int x = canvas.getWidth() / 2;
                        int y = canvas.getHeight() / 2;
                        //Point p = new Point(x, y, 1.0f);
                        Point p = canvas.mapPoint(x, y, 1.0f);
                        drawImage(canvas, (int)p.x, (int)p.y, bitmap.getWidth(), bitmap.getHeight(), bitmap, false);
                        if (inputStream != null) {
                            inputStream.close();
                        }
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "An error was encountered while loading.", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
                if (canvas != null) {
                    canvas.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            canvas.disableCenter = false;
                            runFullScreen(BookActivity4.this);
                        }
                    }, 200); //FIXME:????
                }
            }
    );

    DrawCanvas.TOOLS lastTool = DrawCanvas.TOOLS.none;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_SPACE) {
//            if (this.canvas != null) {
//                lastTool = this.canvas.tool;
//                this.canvas.setTool(DrawCanvas.TOOLS.pan);
//            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_SPACE) {
            if (this.canvas != null) {
//                this.canvas.setTool(lastTool);
                this.canvas.setTool(DrawCanvas.TOOLS.pan);
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    //FIXME:TODO:
    //@SuppressLint("GestureBackNavigation")
    //    @Override
    //    public void onBackPressed() {
}
