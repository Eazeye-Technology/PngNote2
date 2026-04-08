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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BookActivity4PageGridDialog {
    public final static boolean HIDE_BUTTONS = true;

    public final static boolean NO_REOPEN_DIALOG = true;
    //private final static int WIN_WIDTH = 633;
    //private final static int WIN_HEIGHT = 750;//600;//646;
    private AlertDialog mDialog;
    public BookActivity4PageGridDialog(Activity ctx, Uri dirUrl_, String dirUrlPath_) {
        onCreateAct(ctx, dirUrl_, dirUrlPath_);
    }

    public AlertDialog create() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.activity_book4_dialog_jump, null);
        LinearLayout llTopJump = (LinearLayout) view.findViewById(R.id.llTopJump);
        llTopJump.setVisibility(View.GONE);
        mDialog = new MaterialAlertDialogBuilder(mContext, BookActivity4Utils.getCenteredTitleThemeOverlay())
                //.setTitle(title)
                //.setView(R.layout.activity_book4_dialog_jump)
                .setView(view)
                .setCancelable(true)
                //.setNegativeButton("Close", null)
                .create();
        mDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                BookActivity4Utils.runFullScreen(mContext);

                AlertDialog dialog = (AlertDialog)dialogInterface;
                LinearLayout llTopJump = (LinearLayout) dialog.findViewById(R.id.llTopJump);
                onshow(mDialog);
                llTopJump.setVisibility(View.VISIBLE);
            }
        });
        updateLayout(mDialog);
        return mDialog;
    }
    private void onshow(DialogInterface dialogInterface) {
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
        btnReorder = (RelativeLayout) dialog.findViewById(R.id.btnReorder);
        btnReorderText = (TextView) dialog.findViewById(R.id.btnReorderText);
        btnSelect = (RelativeLayout) dialog.findViewById(R.id.btnSelect);
        btnSelectText = (TextView) dialog.findViewById(R.id.btnSelectText);
        btnDuplicate = (RelativeLayout) dialog.findViewById(R.id.btnDuplicate);
        btnDuplicateText = (TextView) dialog.findViewById(R.id.btnDuplicateText);
        btnDelete = (RelativeLayout) dialog.findViewById(R.id.btnDelete);
        btnDeleteText = (TextView) dialog.findViewById(R.id.btnDeleteText);
        btnDeleteTextIcon = (ImageView) dialog.findViewById(R.id.btnDeleteTextIcon);
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
                    List<Page> pages = new ArrayList<>();
                    for (Page page : _pageList) {
                        if (page != null && page.checked) {
                            pages.add(page);
                        }
                    }
                    copyPages(dialog, pages);
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
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bookGridAdapter.checkMode == BookPageGridAdapter.CHECK_MODE_CHECK) {
                    if (true) {
                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                deleteCurPage();
                            }
                        };
                        AlertDialog dialogDel = new BookActivity4DeleteDialog(mContext, runnable).create();
                        dialogDel.show();
                    } else {
                        deleteCurPage();
                    }
                }
            }
        });
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
        if (HIDE_BUTTONS) {
            btnReorder.setVisibility(View.GONE);
            btnDuplicate.setVisibility(View.GONE);
        }
        updateButtons();
        updateLayout(dialog);
        requestLoadPages();
    }
    private void deleteCurPage() {
        if (bookGridAdapter != null &&
                bookGridAdapter.checkMode == BookPageGridAdapter.CHECK_MODE_CHECK) {
            List<Page> pages = new ArrayList<>();
            for (Page page : _pageList) {
                if (page != null && page.checked) {
                    pages.add(page);
                }
            }
            deletePages(mDialog, pages);
            bookGridAdapter.checkMode = BookPageGridAdapter.CHECK_MODE_NONE;
            bookGridAdapter.notifyDataSetChanged();
            updateButtons();
            if (!BookActivity4PageGridDialog.NO_REOPEN_DIALOG) {
                if (mDialog != null && mDialog.isShowing()) {
                    mDialog.dismiss();
                }
            } else {
                _book = null;
                onCreateAct(mContext, dirUrl, dirUrlPath);
                requestLoadPages();
            }
        }
    }
    private void updateLayout(AlertDialog dialog) {
        //WIN_HEIGHT
//        dialog.getWindow().setLayout(
//                WIN_WIDTH, //ViewGroup.LayoutParams.WRAP_CONTENT,
//                WIN_HEIGHT //ViewGroup.LayoutParams.MATCH_PARENT
//                /*ViewGroup.LayoutParams.WRAP_CONTENT*/
//        );
        try {
            Window window = dialog.getWindow();
            if (window != null) {
//                window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, 800);
                int WIN_WIDTH = mContext.getResources().getDimensionPixelSize(R.dimen.activity_dialog_upgrade_min_width_page_grid);
                int WIN_HEIGHT = mContext.getResources().getDimensionPixelSize(R.dimen.activity_dialog_upgrade_min_height_page_grid);
                window.setLayout(
                        WIN_WIDTH,
                        WIN_HEIGHT
                  );
            }
        } catch (Throwable eee) {
            eee.printStackTrace();
        }
    }
    RelativeLayout btnReorder;
    TextView btnReorderText;
    RelativeLayout btnSelect;
    TextView btnSelectText;
    RelativeLayout btnDuplicate;
    TextView btnDuplicateText;
    RelativeLayout btnDelete;
    TextView btnDeleteText;
    ImageView btnDeleteTextIcon;
    public void updateButtons() {
        if (btnReorder != null && btnSelect != null &&
                btnDuplicate != null && btnDelete != null &&
                bookGridAdapter != null) {
            if (bookGridAdapter.checkMode == BookPageGridAdapter.CHECK_MODE_CHECK) {
                btnDuplicateText.setTextColor(0xFF000000);
                btnDuplicate.setBackgroundResource(R.drawable.button_outline);

                btnDeleteText.setTextColor(0xFF000000);
                btnDeleteTextIcon.setBackgroundTintList(ColorStateList.valueOf(0xFF000000));
                btnDelete.setBackgroundResource(R.drawable.button_outline);

                btnSelectText.setTextColor(0xFFFFFFFF);
                btnSelect.setBackgroundResource(R.drawable.button_outline_black);

                btnReorderText.setTextColor(0xFF000000);
                btnReorder.setBackgroundResource(R.drawable.button_outline);
            } else if (bookGridAdapter.checkMode == BookPageGridAdapter.CHECK_MODE_MOVE) {
                btnDuplicateText.setTextColor(0xFF908E8E);
                btnDuplicate.setBackgroundResource(R.drawable.button_outline_gray);

                btnDeleteText.setTextColor(0xFF908E8E);
                btnDeleteTextIcon.setBackgroundTintList(ColorStateList.valueOf(0xFF908E8E));
                btnDelete.setBackgroundResource(R.drawable.button_outline_gray);

                btnSelectText.setTextColor(0xFF000000);
                btnSelect.setBackgroundResource(R.drawable.button_outline);

                btnReorderText.setTextColor(0xFFFFFFFF);
                btnReorder.setBackgroundResource(R.drawable.button_outline_black);
            } else if (bookGridAdapter.checkMode == BookPageGridAdapter.CHECK_MODE_NONE) {
                btnDuplicateText.setTextColor(0xFF908E8E);
                btnDuplicate.setBackgroundResource(R.drawable.button_outline_gray);

                btnDeleteText.setTextColor(0xFF908E8E);
                btnDeleteTextIcon.setBackgroundTintList(ColorStateList.valueOf(0xFF908E8E));
                btnDelete.setBackgroundResource(R.drawable.button_outline_gray);

                btnSelectText.setTextColor(0xFF000000);
                btnSelect.setBackgroundResource(R.drawable.button_outline);

                btnReorderText.setTextColor(0xFF000000);
                btnReorder.setBackgroundResource(R.drawable.button_outline);
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

    public void copyPages(DialogInterface dialog, List<Page> pages) {
        BookActivity4Utils.copyPages(mContext, pages);
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
                                pattern = item.optString(BookActivity4Config.USE_SKETCH_CONFIG_PATTERN);
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
            this._book = getBookIO().loadBook(getBookDir(), mContext);
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
