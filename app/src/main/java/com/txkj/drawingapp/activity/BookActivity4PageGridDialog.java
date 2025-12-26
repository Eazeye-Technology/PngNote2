package com.txkj.drawingapp.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BlendMode;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.txkj.drawingapp.R;
import com.txkj.notemobile2.Book;
import com.txkj.notemobile2.book.BookIO;
import com.txkj.notemobile2.book.BookPage;
import com.txkj.notemobile2.book.FastFile;
import com.txkj.notemobile2.ui.CanvasBoox;
import com.txkj.notemobile2.ui.Page;
import com.txkj.notemobile2.ui.PageGridData;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BookActivity4PageGridDialog {
    public final static boolean NO_REOPEN_DIALOG = true;
    private final static int WIN_HEIGHT = 800;

    public BookActivity4PageGridDialog(Activity ctx, Uri dirUrl_, String dirUrlPath_) {
        onCreateAct(ctx, dirUrl_, dirUrlPath_);
    }

    public AlertDialog create() {
        AlertDialog dialog = new MaterialAlertDialogBuilder(mContext, BookActivity4Utils.getCenteredTitleThemeOverlay())
                //.setTitle(title)
                .setView(R.layout.activity_book4_dialog_jump)
                .setCancelable(true)
                .setNegativeButton("Close", null)
                .create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                AlertDialog dialog = (AlertDialog)dialogInterface;
                gridview = (BookActivity4DragGridView) dialog.findViewById(R.id.bookgridview);
                gridview.setOnDrop(new Runnable() {
                    @Override
                    public void run() {
                        reorderPages(dialogInterface, getPageList());
                        if (!BookActivity4PageGridDialog.NO_REOPEN_DIALOG) {
                            if (dialog != null && dialog.isShowing()) {
                                dialog.dismiss();
                            }
                        } else {
                            _book = null;
                            onCreateAct(mContext, dirUrl, dirUrlPath);
                            requestLoadPages();
                        }
                    }
                });
                gridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
                //gridview.setBackgroundColor(Color.WHITE);
                bookGridAdapter = new BookPageGridAdapter(mContext, getPageList());
                bookGridAdapter.pageIndex = BookActivity4Utils.getPageIndex(mContext);
                gridview.setAdapter(bookGridAdapter);
                gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (bookGridAdapter != null &&
                                bookGridAdapter.checkMode != BookPageGridAdapter.CHECK_MODE_NONE) {
                            if (bookGridAdapter.checkMode == BookPageGridAdapter.CHECK_MODE_CHECK) {
                                Page page = _pageList.get(position);
                                if (page != null) {
                                    page.checked = !page.checked;
                                    bookGridAdapter.notifyDataSetChanged();
                                }
                            }
                        } else {
                            int pageIdx = position;
                            openPage(dialog, pageIdx);
                        }
                    }
                });
                btnReorder = (Button) dialog.findViewById(R.id.btnReorder);
                btnSelect = (Button) dialog.findViewById(R.id.btnSelect);
                btnDuplicate = (Button) dialog.findViewById(R.id.btnDuplicate);
                btnDelete = (Button) dialog.findViewById(R.id.btnDelete);
                btnReorder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (bookGridAdapter != null) {
                            if (bookGridAdapter.checkMode != BookPageGridAdapter.CHECK_MODE_MOVE) {
                                bookGridAdapter.checkMode = BookPageGridAdapter.CHECK_MODE_MOVE;
                            } else {
                                bookGridAdapter.checkMode = BookPageGridAdapter.CHECK_MODE_NONE;
                            }
                            bookGridAdapter.notifyDataSetChanged();
                            updateButtons();
                        }
                    }
                });
                btnSelect.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (bookGridAdapter != null) {
                            if (bookGridAdapter.checkMode != BookPageGridAdapter.CHECK_MODE_CHECK) {
                                bookGridAdapter.checkMode = BookPageGridAdapter.CHECK_MODE_CHECK;
                                if (_pageList != null) {
                                    for (Page page : _pageList) {
                                        if (page != null) {
                                            page.checked = false;
                                        }
                                    }
                                }
                            } else {
                                bookGridAdapter.checkMode = BookPageGridAdapter.CHECK_MODE_NONE;
                            }
                            bookGridAdapter.notifyDataSetChanged();
                            updateButtons();
                        }
                    }
                });
                btnDuplicate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (bookGridAdapter != null &&
                                bookGridAdapter.checkMode == BookPageGridAdapter.CHECK_MODE_CHECK) {

                        }
                    }
                });
                btnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (bookGridAdapter != null &&
                                bookGridAdapter.checkMode == BookPageGridAdapter.CHECK_MODE_CHECK) {
                            List<Page> pages = new ArrayList<>();
                            for (Page page : _pageList) {
                                if (page != null && page.checked) {
                                    pages.add(page);
                                }
                            }
                            deletePages(dialog, pages);
                            bookGridAdapter.checkMode = BookPageGridAdapter.CHECK_MODE_NONE;
                            bookGridAdapter.notifyDataSetChanged();
                            updateButtons();
                            if (!BookActivity4PageGridDialog.NO_REOPEN_DIALOG) {
                                if (dialog != null && dialog.isShowing()) {
                                    dialog.dismiss();
                                }
                            } else {
                                _book = null;
                                onCreateAct(mContext, dirUrl, dirUrlPath);
                                requestLoadPages();
                            }
                        }
                    }
                });
                updateButtons();
                updateLayout(dialog);
                requestLoadPages();
            }
        });
        //WIN_HEIGHT
//        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,
//                ViewGroup.LayoutParams.MATCH_PARENT/*ViewGroup.LayoutParams.WRAP_CONTENT*/);
        updateLayout(dialog);
        return dialog;
    }
    private void updateLayout(AlertDialog dialog) {
        try {
            Window window = dialog.getWindow();
            if (window != null) {
                window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, 800);
            }
        } catch (Throwable eee) {
            eee.printStackTrace();
        }
    }
    Button btnReorder;
    Button btnSelect;
    Button btnDuplicate;
    Button btnDelete;
    public void updateButtons() {
        if (btnReorder != null && btnSelect != null &&
                btnDuplicate != null && btnDelete != null &&
                bookGridAdapter != null) {
            if (bookGridAdapter.checkMode == BookPageGridAdapter.CHECK_MODE_CHECK) {
                btnDuplicate.setTextColor(0xFF000000);
                btnDelete.setTextColor(0xFF000000);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    btnDelete.setCompoundDrawableTintList(ColorStateList.valueOf(0xFF000000));
                    btnDelete.setCompoundDrawableTintMode(PorterDuff.Mode.SRC_IN);
                }

                btnSelect.setTextColor(0xFFFFFFFF);
                btnSelect.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    btnSelect.setBackgroundTintBlendMode(BlendMode.SRC_IN);
                }

                btnReorder.setTextColor(0xFF000000);
                btnReorder.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    btnReorder.setBackgroundTintBlendMode(BlendMode.SRC_IN);
                }
            } else if (bookGridAdapter.checkMode == BookPageGridAdapter.CHECK_MODE_MOVE) {
                btnDuplicate.setTextColor(0xFFCCCCCC);
                btnDelete.setTextColor(0xFFCCCCCC);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    btnDelete.setCompoundDrawableTintList(ColorStateList.valueOf(0xFFCCCCCC));
                    btnDelete.setCompoundDrawableTintMode(PorterDuff.Mode.SRC_IN);
                }

                btnSelect.setTextColor(0xFF000000);
                btnSelect.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    btnSelect.setBackgroundTintBlendMode(BlendMode.SRC_IN);
                }

                btnReorder.setTextColor(0xFFFFFFFF);
                btnReorder.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    btnReorder.setBackgroundTintBlendMode(BlendMode.SRC_IN);
                }
            } else if (bookGridAdapter.checkMode == BookPageGridAdapter.CHECK_MODE_NONE) {
                btnDuplicate.setTextColor(0xFFCCCCCC);
                btnDelete.setTextColor(0xFFCCCCCC);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    btnDelete.setCompoundDrawableTintList(ColorStateList.valueOf(0xFFCCCCCC));
                    btnDelete.setCompoundDrawableTintMode(PorterDuff.Mode.SRC_IN);
                }

                btnSelect.setTextColor(0xFF000000);
                btnSelect.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    btnSelect.setBackgroundTintBlendMode(BlendMode.SRC_IN);
                }

                btnReorder.setTextColor(0xFF000000);
                btnReorder.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    btnReorder.setBackgroundTintBlendMode(BlendMode.SRC_IN);
                }
            }
        }
    }

    public void openPage(DialogInterface dialog, int pageIdx) {
        try {
            if (dialog != null) {
                dialog.dismiss();
            }
        } catch (Throwable eee) {
            eee.printStackTrace();
        }
        BookActivity4Utils.openPage(mContext, pageIdx);
    }

    public void deletePages(DialogInterface dialog, List<Page> pages) {
        BookActivity4Utils.deletePages(mContext, pages);
    }

    public void reorderPages(DialogInterface dialog, List<Page> pages) {
        BookActivity4Utils.reorderPages(mContext, pages);
    }

    private void requestLoadPages() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int size = getBook().getPages().size();
                for (int idx = 0; idx < size; ++idx) {
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
                            //FIXME:added
                            if (NO_REOPEN_DIALOG) {
                                bookGridAdapter = new BookPageGridAdapter(mContext, getPageList());
                                bookGridAdapter.pageIndex = BookActivity4Utils.getPageIndex(mContext);
                                gridview.setAdapter(bookGridAdapter);
                            }
                            bookGridAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        }).start();
    }

    private BookActivity4DragGridView gridview;
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
    //private int _pageIndex;
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
