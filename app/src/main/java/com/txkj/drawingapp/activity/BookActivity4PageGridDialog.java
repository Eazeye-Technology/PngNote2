package com.txkj.drawingapp.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.txkj.drawingapp.R;
import com.txkj.notemobile2.Book;
import com.txkj.notemobile2.BookListActivity;
import com.txkj.notemobile2.book.BookIO;
import com.txkj.notemobile2.book.FastFile;
import com.txkj.notemobile2.ui.CanvasBoox;
import com.txkj.notemobile2.ui.Page;
import com.txkj.notemobile2.ui.PageGridAdapter;
import com.txkj.notemobile2.ui.PageGridData;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BookActivity4PageGridDialog {
    public BookActivity4PageGridDialog(Activity ctx, Uri dirUrl_, String dirUrlPath_) {
        onCreateAct(ctx, dirUrl_, dirUrlPath_);
    }

    public AlertDialog create() {
        AlertDialog dialog = new MaterialAlertDialogBuilder(mContext, BookListActivity.getCenteredTitleThemeOverlay())
                //.setTitle(title)
                .setView(R.layout.activity_book4_dialog_jump)
                .setCancelable(true)
                .setNegativeButton("Cancel", null)
                .create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                gridview = (GridView) ((AlertDialog)dialog).findViewById(R.id.bookgridview);
                gridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
                //gridview.setBackgroundColor(Color.WHITE);
                bookGridAdapter = new BookPageGridAdapter(mContext, getPageList());
                gridview.setAdapter(bookGridAdapter);
                gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        int pageIdx = position;
                        openPage(dialog, pageIdx);
                    }
                });
                requestLoadPages();
            }
        });
        return dialog;
    }

    public void openPage(DialogInterface dialog, int pageIdx) {
        try {
            if (dialog != null) {
                dialog.dismiss();
            }
        } catch (Throwable eee) {
            eee.printStackTrace();
        }
        if (mContext instanceof BookActivity4) {
            ((BookActivity4) mContext).openPage(pageIdx);
        }
    }

    private void requestLoadPages() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int idx = 0; idx < getBook().getPages().size(); ++idx) {
                    FastFile bmpFile = getBook().getPages().get(idx);
                    Bitmap bitmap = getBookIO().loadPageThumbnail(bmpFile);
                    if (BookIO.USE_META_TXT) {
                        try {
                            String pattern = null;
                            String metaTxt = getBookIO().loadMetaPng(bmpFile);
                            JSONObject item = new JSONObject(metaTxt);
                            if (item != null) {
                                pattern = item.optString("pattern");
                            }
                            if (pattern != null) {
                                Bitmap emptyBmp = Bitmap.createBitmap(bitmap.getWidth(),
                                        bitmap.getHeight(), Bitmap.Config.ARGB_8888);
                                if (BookIO.USE_META_TXT) {
                                    emptyBmp.eraseColor(0x00000000);
                                } else {
                                    emptyBmp.eraseColor(0x00000000);
                                }
                                CanvasBoox.initBackText(pattern, emptyBmp, BookIO.loadPageThumbnail_size);
                                Canvas canvas = new Canvas(emptyBmp);
                                Paint paint = new Paint();
                                canvas.drawBitmap(bitmap, 0, 0, paint);
                                bitmap = emptyBmp;
                            }
                        } catch (Throwable eee) {
                            eee.printStackTrace();
                        }
                    }
                    final int idx_ = idx;
                    final Bitmap bitmap_ = bitmap;
                    mContext.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            List<Page> result = new ArrayList<Page>();
                            for (int idx2 = 0; idx2 < getPageList().size(); ++idx2) {
                                Page page = getPageList().get(idx2);
                                if (idx_ == idx2) {
                                    result.add(page.copy(null, bitmap_, null));
                                } else {
                                    result.add(page);
                                }
                            }
                            _pageList.clear();
                            _pageList.addAll(result);
                            bookGridAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        }).start();
    }

    private GridView gridview;
    private BookPageGridAdapter bookGridAdapter;
    private void onCreateAct(Activity ctx, Uri dirUrl_, String dirUrlPath_) {
        this.mContext = ctx;
        this.dirUrl = dirUrl_;
        this.dirUrlPath = dirUrlPath_;
        this._bookDir = bookDir_init();
        this._bookIO = bookIO_init();
        this._bgImage = bgImage_init();
        this._pageList = pageList_init();
    }

    private Activity mContext;
    private Uri dirUrl;
    private String dirUrlPath;
    private FastFile _bookDir;
    private FastFile bookDir_init() {
        return FastFile.fromTreeUri(this.mContext, this.dirUrl, this.dirUrlPath);
    }
    private FastFile getBookDir() {
        return this._bookDir;
    }
    private BookIO _bookIO;
    private BookIO getBookIO() {
        return this._bookIO;
    }
    private Book _book;
    private BookIO bookIO_init() {
        return new BookIO(mContext.getContentResolver());
    }
    private Book getBook() {
        if (this._book != null) {
            return this._book;
        } else {
            this._book = getBookIO().loadBook(getBookDir());
            return this._book;
        }
    }

    private List<Page> getPageList() {
        return this._pageList;
    }
    private List<Page> _pageList;// = pageList_init();
    private List<Page> pageList_init() {
        List<FastFile> pages = this.getBook().getPages();
        List<Page> result = new ArrayList<Page>(pages != null ? pages.size() : 10);
        int idxNext = 0;
        Iterator<FastFile> var8 = pages.iterator();
        while (var8.hasNext()) { //FIXME:
            var8.next();
            int idx = idxNext++;
            if (idx < 0) {
                throw new IndexOutOfBoundsException(); //FIXME:
            }
            Page page = new Page(String.valueOf(idx + 1), PageGridData.getBlankBitmap(), this.getBgImage(), String.valueOf(idx + 1));
            result.add(page);
        }
        return result;
    }

    private Bitmap getBgImage() {
        return this._bgImage;
    }
    private Bitmap _bgImage;// = bgImage_init();
    private Bitmap bgImage_init() {
        return this.getBookIO().loadBgForGrid(this.getBook().getBookDir());
    }
}
