package com.txkj.drawingapp.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;

import com.agsw.FabricView.FabricView;
import com.github.guanpy.wblib.bean.DrawPoint;
import com.github.guanpy.wblib.widget.DrawTextView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.txkj.drawingapp.R;
import com.txkj.notemobile2.Book;
import com.txkj.notemobile2.BookActivity;
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

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import io.material.catalog.windowpreferences.WindowPreferencesManager;

public class BookActivity3 extends AppCompatActivity {
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
            if (id == R.id.left_toolkit_item1) {
                findViewById(R.id.buttonPen2).performClick();
            } else if (id == R.id.left_toolkit_item2) {
                findViewById(R.id.buttonEraser2).performClick();
            } else if (id == R.id.left_toolkit_item3) {

            } else if (id == R.id.left_toolkit_item4) {

            } else if (id == R.id.left_toolkit_item5) {

            } else if (id == R.id.left_toolkit_item6) {

            }
//            if (bottomSheetDialog1 != null) {
//                bottomSheetDialog1.show();
//            }

            this.findViewById(R.id.bottomDialog2).setVisibility(View.GONE);
            if (isShowBottom) {
                if (this.findViewById(R.id.bottomDialog1).getVisibility() == View.VISIBLE) {
                    this.findViewById(R.id.bottomDialog1).setVisibility(View.GONE);
                } else {
                    this.findViewById(R.id.bottomDialog1).setVisibility(View.VISIBLE);
                }
            } else {
                this.findViewById(R.id.bottomDialog1).setVisibility(View.GONE);
            }
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

            } else if (id == R.id.left_toolkit_item22) {

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

            } else if (id == R.id.left_toolkit_item42) {

            } else if (id == R.id.left_toolkit_item43) {

            } else if (id == R.id.left_toolkit_item44) {

            } else if (id == R.id.left_toolkit_item45) {

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
            canvas.setInteractionMode(FabricView.DRAW_MODE);
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
            canvas.setInteractionMode(FabricView.SELECT_MODE);
        }
        this.findViewById(R.id.bottomDialog1).setVisibility(View.GONE);
    }
    //--------------------------

    private TextView textViewPageInfo;
    FabricView canvas;
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
            this.backText = intent.getStringExtra(EXTRA_BACKTEXT);
            this.handlePageIdxArg(intent);
            isInitBackText = true;
        }

        setContentView(R.layout.activity_book3);

        if (this.dirUrlPath != null) {
            ((TextView) findViewById(R.id.newTitle)).setText(getBookName(this.dirUrlPath));
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
        findViewById(R.id.btnPanel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                findViewById(R.id.llFullscreen).setVisibility(View.VISIBLE);
            }
        });
        findViewById(R.id.btnExitFullscreen).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.llTab).setVisibility(View.VISIBLE);
                findViewById(R.id.llTopBar).setVisibility(View.VISIBLE);
                findViewById(R.id.llFullscreen).setVisibility(View.GONE);
            }
        });
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
        canvas = (FabricView) findViewById(R.id.canvas);
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
                        dtView.init2(lastX, lastY, "", 0xFF000000, new DrawTextView.CallBackListener() {
                            @Override
                            public void onUpdate(DrawPoint drawPoint) {

                            }

                            @Override
                            public void onSave(DrawPoint drawPoint) {
                                if (drawPoint != null && drawPoint.getDrawText() != null) {
                                    if (true) {
                                        Paint paint = new Paint();
                                        paint.setColor(0xFFFF0000);
                                        paint.setTextSize(sp2px(BookActivity3.this, 24));
                                        canvas.drawText(
                                                drawPoint.getDrawText().getStr(),
                                                (int) drawPoint.getDrawText().getX(),
                                                (int) drawPoint.getDrawText().getY(),
                                                paint
                                        );
                                    } else {
                                        Paint paint = new Paint();
                                        paint.setColor(0xFFFF0000);
                                        paint.setTextSize(18 * 5);
                                        canvas.drawText("hello", 100, 100, paint);
                                    }
                                }
                                findViewById(R.id.top_toolkit_item1).performClick(); //返回绘画模式
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

        canvas.setColor(0xff000000 | color);
    }

    public void onPaintSelect(String backText) {
        this.backText = backText;
        addNewPageAndGo_old();
    }












    public class SavingTask extends AsyncTask<Void, Void, Void> {
        private boolean mIsBack = true;
        public SavingTask(boolean isBack) {
            if (isBack) {
                //TODO:
                //https://github.com/antwankakki/FabricView/wiki
                if (mOnUpdateBmpListener != null) {
                    Bitmap fullResult = canvas.getCanvasBitmap();
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
        canvas.setSize(val);
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

    //FIXME:not good
    private void setPenEraserBrush(FabricView canvas, int index) {
        if (index == 0) {
            canvas.setInteractionMode(FabricView.DRAW_MODE); //橡皮擦
        } else if (index == 1) {
            canvas.setInteractionMode(FabricView.DRAW_MODE); //画笔1
        } else if (index == 2) {
            canvas.setInteractionMode(FabricView.DRAW_MODE); //画笔2
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
                Bitmap fullResult = canvas.getCanvasBitmap();
                mOnUpdateBmpListener.onUpdateBmp(fullResult);
            }
        }
    }
    private void setOnUpdateListener(FabricView canvas, CanvasBoox.OnUpdateBmpListener listener) {
        mOnUpdateBmpListener = listener;
    }
    private void setOnUndoStateListener(FabricView canvas, CanvasBoox.OnUndoStateListener listener) {
        //TODO:
    }
    private void onPageIdx(FabricView canvas, int idx, CanvasBoox.OnLoadBitmapListener bitmapLoader) {
        //TODO:
    }
    private void setBackText(FabricView canvas, String backText) {
        //TODO:
        if (backText != null && canvas != null) {
            if (backText.equals(FileMeta.NONE)) {
                canvas.setBackgroundMode(FabricView.BACKGROUND_STYLE_BLANK);
            } else if (backText.equals(FileMeta.LINED)) {
                canvas.setBackgroundMode(FabricView.BACKGROUND_STYLE_NOTEBOOK_PAPER);
            } else if (backText.equals(FileMeta.DOTTED)) {
                canvas.setBackgroundMode(FabricView.BACKGROUND_STYLE_DOT_PAPER);
            } else if (backText.equals(FileMeta.GRAPH)) {
                canvas.setBackgroundMode(FabricView.BACKGROUND_STYLE_GRAPH_PAPER);
            }
        }
    }
    private void init(FabricView canvas, Bitmap initialBmp, Bitmap background, int initialPageIdx, Activity act) {
        //TODO:
//        if (canvas != null && background != null) {
//            canvas.drawImage(0, 0, background.getWidth(), background.getHeight(), background);
//        }
        canvas.initialBmp = initialBmp;
//        if (canvas != null && initialBmp != null) {
//            canvas.drawImage(0, 0, initialBmp.getWidth(), initialBmp.getHeight(), initialBmp);
//        }
    }

    public String getBookName(String url) {
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

    //FIXME:TODO:
    //@SuppressLint("GestureBackNavigation")
    //    @Override
    //    public void onBackPressed() {
}
