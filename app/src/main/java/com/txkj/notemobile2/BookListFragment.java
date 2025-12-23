package com.txkj.notemobile2;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.foobnix.pdf.info.Android6Mod;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.txkj.drawingapp.R;
import com.txkj.drawingapp.activity.BookActivity4Utils;
import com.txkj.notemobile2.book.BookIO;
import com.txkj.notemobile2.book.FastFile;
import com.txkj.notemobile2.book.SimpleFileMeta;
import com.txkj.notemobile2.booklist.BookList;
import com.txkj.notemobile2.colorpicker.FileMeta;
import com.txkj.notemobile2.colorpicker.NewNoteDialog;
import com.txkj.notemobile2.ui.CanvasBoox;
import com.txkj.notemobile2.ui.Page;
import com.txkj.notemobile2.ui.PageGridAdapter;
import com.txkj.notemobile2.ui.PageGridData;
import com.txkj.notemobile2.ui.PageListAdapter;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BookListFragment extends Fragment {
    private final static boolean USE_CREATING_PROGRESS = false;
    private final static boolean D = true;
    private final static String TAG = "BookListFragment";

    private Uri _url;
    private String _urlPath;

    //if (BookIO.USE_CONTENT_RESOLVER)
//    private ActivityResultLauncher<Uri> getRootDirUrl;
    private Bitmap blankBitmap;
    private List<FastFile> files;
    private List<Page> pageList;
    private Pair<Double, Double> _bookSizeDP;
    private BookIO _bookIO;

    public BookListFragment() {

    }

    public void onActCreate() {
        if (BookIO.USE_CONTENT_RESOLVER) {
//        this.getRootDirUrl = this.registerForActivityResult(new ActivityResultContracts.OpenDocumentTree(),
//                new ActivityResultCallback<Uri>() {
//                    @Override
//                    public void onActivityResult(Uri o) {
//                        BookListActivity.this.getRootDirUrl(o);
//                    }
//                });
        }
        this.blankBitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        this.blankBitmap.eraseColor(PageGridData.THUMB_BACK_COLOR);
        this.files = new ArrayList<FastFile>(); //CollectionsKt.emptyList();
        this.pageList = new ArrayList<Page>();//CollectionsKt.emptyList();
        try {
            DisplayMetrics metrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
            this._bookSizeDP = PageGridActivity.displayMetricsTo4GridSize(metrics);
        } catch (Throwable eee) {
            eee.printStackTrace();
        }
        this._bookIO = new BookIO(getActivity().getContentResolver());
    }

    private Uri getLastUri() {
        String it = BookList.lastUriStr(getActivity());
        Uri result = null;
        if (it != null) {
            result = Uri.parse(it);
        }
        return result;
    }

    private boolean writeLastUri(Uri uri) {
        return BookList.writeLastUriStr(getActivity(), uri.toString());
    }

    private void showMessage(String msg) {
        BookList.showMessage(getActivity(), msg);
    }

    private void openRootDir(Uri url, String urlPath) {
        this._url = url;
        this._urlPath = urlPath;
        this.reloadBookList(url, urlPath);
    }

    public Bitmap getBlankBitmap() {
        return this.blankBitmap;
    }

    private boolean firstNew = true;
    private void updateFiles(List<FastFile> newFiles) {
        this.files.clear();
        this.files.addAll(newFiles);
        List<Page> tempList = new ArrayList<Page>(newFiles.size() > 0 ? newFiles.size() : 10);
        for (FastFile file : newFiles) {
            tempList.add(new Page(file.getDisplayName(), this.blankBitmap, this.blankBitmap, file.getName()));
        }
        List<SimpleFileMeta> datas = null;
        try {
            datas = getBookIO().loadRecent();
        } catch (Throwable eee) {
            eee.printStackTrace();
        }
        if (datas == null) {
            datas = new ArrayList<SimpleFileMeta>();
        }
        Map<String, Long> updateTimeMap = new HashMap<String, Long>();
        for (SimpleFileMeta meta : datas) {
            if (meta != null && meta.getName() != null &&
                    meta.getUpdateTime() != null &&
                    meta.getUpdateTime().length() > 0) {
                try {
                    updateTimeMap.put(meta.getName(), Long.parseLong(meta.getUpdateTime()));
                } catch (Throwable eee) {
                    eee.printStackTrace();
                }
            }
        }
        //根据更新时间倒序
        Collections.sort(tempList, new Comparator<Page>() {
            @Override
            public int compare(Page o1, Page o2) {
                if (o1 == null) {
                    return -(-1);
                } else if (o2 == null) {
                    return -(1);
                }
                long updateTime1 = 0L;
                long updateTime2 = 0L;
                if (o1.getTitle() != null) {
                    Long updateTime1Val = updateTimeMap.get(o1.getTitle());
                    updateTime1 = updateTime1Val != null ? updateTime1Val : 0L;
                }
                if (o2.getTitle() != null) {
                    Long updateTime2Val = updateTimeMap.get(o2.getTitle());
                    updateTime2 = updateTime2Val != null ? updateTime2Val : 0L;
                }
                if (updateTime1 < updateTime2) {
                    return -(-1);
                } else if (updateTime1 > updateTime2) {
                    return -(1);
                }
                return 0;
            }
        });
        this.pageList.clear();
        this.pageList.addAll(tempList);
        if (D) {
            for (int i = 0; i < this.pageList.size(); ++i) {
                Page o1 = this.pageList.get(i);
                Log.e(TAG, "<<< " + i + ", o1.getTitle() : " + o1.getTitle() + ", " + updateTimeMap.get(o1.getTitle()));
            }
        }
        bookGridAdapter.notifyDataSetChanged();
        bookListAdapter.notifyDataSetChanged();
        this.requestLoadPages();

        if (BookActivity4Utils.APP_OPEN != null && BookActivity4Utils.APP_OPEN.equals("NEW")) {
            //findViewById(R.id.buttonNewBook).performClick();
            if (firstNew && BookActivity4Utils.getStateStarted(getActivity()) == 0) {
                firstNew = false;
                onNewBook();
            }
        } else if (BookActivity4Utils.APP_FILE != null && !BookActivity4Utils.APP_FILE.equals("")) {
            for (int i = 0; i < files.size(); ++i) {
                FastFile f = files.get(i);
                if (f != null && f.getName() != null && f.getName().equals(BookActivity4Utils.APP_FILE)) {
                    int pageIdx = i;
                    BookActivity4Utils.openBookUI(getActivity(),
                            files.get(pageIdx).getUri(),
                            files.get(pageIdx).getFilePath(),
                            null,
                            null,
                            false, true);
                    break;
                }
            }
        }
    }

    private void requestLoadPages() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int idx = 0; idx < files.size(); ++idx) {
                    FastFile oneBookDir = files.get(idx);
                    //FIXME: thumbnailBitmap可能会抛异常导致null
                    try {
                        Bitmap thumbnailBitmap = getBookIO().loadThumbnail(oneBookDir);
                        if (BookIO.USE_META_TXT) {
                            try {
                                String pattern = null;
                                String metaTxt = getBookIO().loadMeta(oneBookDir);
                                JSONObject item = new JSONObject(metaTxt);
                                if (item != null) {
                                    pattern = item.optString("pattern");
                                }
                                if (pattern != null) {
                                    Bitmap emptyBmp = Bitmap.createBitmap(thumbnailBitmap.getWidth(),
                                            thumbnailBitmap.getHeight(), Bitmap.Config.ARGB_8888);
                                    if (BookIO.USE_META_TXT) {
                                        emptyBmp.eraseColor(0x00000000);
                                    } else {
                                        emptyBmp.eraseColor(0x00000000);
                                    }
                                    CanvasBoox.initBackText(pattern, emptyBmp, BookIO.loadThumbnail_size);
                                    Canvas canvas = new Canvas(emptyBmp);
                                    Paint paint = new Paint();
                                    canvas.drawBitmap(thumbnailBitmap, 0, 0, paint);
                                    thumbnailBitmap = emptyBmp;
                                }
                            } catch (Throwable eee) {
                                eee.printStackTrace();
                            }
                        }
                        final Bitmap thumbnail = thumbnailBitmap != null ? thumbnailBitmap : blankBitmap;
                        final Bitmap bg = getBookIO().loadBgThumbnail(oneBookDir);
                        final int idx_ = idx;
                        final String name_ = oneBookDir.getName();
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                List<Page> result = new ArrayList<Page>();
                                for (int idx2 = 0; idx2 < pageList.size(); ++idx2) {
                                    Page page = pageList.get(idx2);
                                    if (page != null && page.getTitle() != null &&
                                            page.getTitle().equals(name_)) {
                                        result.add(page.copy(null, thumbnail, bg));
                                    } else {
                                        result.add(page);
                                    }
                                }
                                pageList.clear();
                                pageList.addAll(result);
                                bookGridAdapter.notifyDataSetChanged();
                                bookListAdapter.notifyDataSetChanged();
                            }
                        });
                    } catch (Throwable eee) {
                        eee.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void reloadBookList(Uri url, String urlPath) {
        this.updateFiles(this.listFiles(url, urlPath));
    }

    private List<FastFile> listFiles(Uri url, String urlPath) {
        FastFile rootDir = FastFile.fromTreeUri(getActivity(), url, urlPath);
        if (!rootDir.isDirectory()) {
            //throw new RuntimeException("Not directory");
            if (D) {
                Log.e(TAG, "Not directory or not exits or no permission : " + urlPath);
            }
            return new ArrayList<FastFile>();
        } else {
            List<FastFile> result = new ArrayList<FastFile>();
            for (FastFile obj : rootDir.listFiles()) {
                if (obj.isDirectory()) {
                    result.add(obj);
                }
            }
            Collections.sort(result, new Comparator<FastFile>() {
                @Override
                public int compare(FastFile item0, FastFile item1) {
                    String name0 = item0.getDisplayName();
                    String name1 = item1.getDisplayName();
                    if (name0 == null) name0 = "";
                    if (name1 == null) name1 = "";
                    return -name0.compareTo(name1);
                }
            });
            return result;
        }
    }

    private Pair getBookSizeDP() {
        return this._bookSizeDP;
    }

    private BookIO getBookIO() {
        return this._bookIO;
    }

    @Override
    public void onResume() {
        super.onResume();
//        onRestart();
    }

    private void onRestart() {
        if (!this.files.isEmpty()) {
            this.reloadBookList(this._url, this._urlPath);
        }
    }

    //    protected void onRestart() {
//        super.onRestart();
//        if (!this.files.isEmpty()) {
//            this.reloadBookList(this._url, this._urlPath);
//        }
//    }

    private GridView gridview;
    private PageGridAdapter bookGridAdapter;
    private ListView listview;
    private PageListAdapter bookListAdapter;
    boolean isIntentNew = false;
    boolean isIntentOpen = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_booklist, container, false);
        onActCreate();

//        ActionBar topAppBar = getActivity().getActionBar();
//        if (topAppBar != null) {
//            topAppBar.setTitle("Note List");
//            topAppBar.setIcon(R.mipmap.ic_launcher);
//            if (!BookActivity4CallConfig.USE_ACTIONBAR) {
//                topAppBar.hide();
//            }
//        }
        view.findViewById(R.id.buttonNewBook).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onNewBook();
            }
        });
        view.findViewById(R.id.buttonSettings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSettings();
            }
        });

        ImageButton imageButtonBack = (ImageButton) view.findViewById(R.id.imageButtonBack);
        imageButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        gridview = (GridView) view.findViewById(R.id.bookgridview);
        gridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
        gridview.setBackgroundColor(Color.WHITE);
        bookGridAdapter = new PageGridAdapter(getActivity(), pageList);
        gridview.setAdapter(bookGridAdapter);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //TODO: see below
//                //int bookType = bookInfoList.get(position).getBookType();
//                int pageIdx = position;
//                Intent intent = new Intent(BookListActivity.this, BookActivity.class);
//                intent.setData(files.get(pageIdx).getUri());
//                intent.putExtra(BookActivity.EXTRA_DIRURLPATH, files.get(pageIdx).getFilePath());
//                startActivity(intent);
            }
        });
        listview = (ListView) view.findViewById(R.id.booklistview);
        //listview.setSelector(new ColorDrawable(Color.TRANSPARENT));
        listview.setBackgroundColor(Color.WHITE);
        bookListAdapter = new PageListAdapter(getActivity(), pageList) {
            @Override
            public void onItemDelete(int position_, final Page page) {
                super.onItemDelete(position_, page);
                showOkDialog(getActivity(),
                        "Delete this note \"" + page.getTitle() + "\" ? ",
                        "OK",
                        new Runnable() {
                            @Override
                            public void run() {
                                //FIXME:
                                int position = -1;
                                Page p = pageList.get(position_);
                                for (int k = 0; k < files.size(); ++k) {
                                    FastFile f = files.get(k);
                                    //FIXME: this is a bad idea
                                    if (f != null && f.getName().equals(p.getName())) {
                                        position = k;
                                        break;
                                    }
                                }
                                if (position >= 0) {
                                    FastFile f = files.get(position);
                                    if (f != null) {
                                        BookIO.deleteFolder(f.getFilePath());
                                    }
//                                  if (bookListAdapter != null) {
//                                      bookListAdapter.notifyDataSetChanged();
//                                  }
                                    getBookIO().removeSaveRecent(f.getName());
                                    reloadBookList(_url, _urlPath);
                                }
                            }
                        }, null);
            }
        };
        listview.setAdapter(bookListAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position_, long id) {
                //int bookType = bookInfoList.get(position).getBookType();
                //FIXME:
                int position = -1;
                Page p = pageList.get(position_);
                for (int k = 0; k < files.size(); ++k) {
                    FastFile f = files.get(k);
                    if (f != null && f.getName().equals(p.getName())) {
                        position = k;
                        break;
                    }
                }
                if (position >= 0) {
                    int pageIdx = position;

                    Uri data = files.get(pageIdx).getUri();
                    String filePath = files.get(pageIdx).getFilePath();
                    //FIXME:可能乱序了
                    if (false) {
                        Toast.makeText(getActivity(),
                                "filePath == " + filePath,
                                Toast.LENGTH_SHORT).show();
                    }
                    BookActivity4Utils.openBookUI(getActivity(),
                            data, filePath, null, null,
                            false, false);
                }
            }
        });

        if (true) {
            if (BookActivity4Utils.APP_OPEN != null && BookActivity4Utils.APP_OPEN.equals("NEW")) {
                isIntentNew = true;
            } else if (BookActivity4Utils.APP_FILE != null && !BookActivity4Utils.APP_FILE.equals("")) {
                isIntentOpen = true;
            }
            if (isIntentNew || isIntentOpen) {
                view.findViewById(R.id.llTop).setVisibility(View.GONE);
                view.findViewById(R.id.tvLoading).setVisibility(View.GONE);
                view.findViewById(R.id.progressLoading1).setVisibility(View.VISIBLE);
            }
        }

        if (BookIO.USE_CONTENT_RESOLVER) {
            boolean openGood = false;
            try {
                Uri it = this.getLastUri();
                if (it != null) {
                    this.openRootDir(it, null);
                    openGood = true;
                }
            } catch (Exception eee) {
                this.showMessage("Can't open dir. Please re-open.");
            }
            if (!openGood) {
                if (BookIO.USE_CONTENT_RESOLVER) {
//            this.getRootDirUrl.launch(null);
                }
            }
        } else {
            this.openRootDir(null, BookIO.rootPath);
        }

        onRestart();

        return view;
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getActivity().getMenuInflater().inflate(R.menu.booklist, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == R.id.newBook) {
//            onNewBook();
//        } else if (item.getItemId() == R.id.settings) {
//            onSettings();
//        }
//        return true;
//    }

    private void onNewBook() {
        NewBookPopup(new OnNewBookListener() {
            @Override
            public void onNewBook(String bookName) {
                addNewBook(bookName, "");
            }
        }, new OnPopDismissListener() {
            @Override
            public void onDismiss() {

            }
        });
    }
    private void onSettings() {
        if (BookIO.USE_CONTENT_RESOLVER) {
//            getRootDirUrl.launch(null);
        }
    }

    private void addNewBook(String newBookName, String backText) {
        if (USE_CREATING_PROGRESS) {
            createWaitingProgressDialog();
        }
        FastFile rootDir = null;
        if (BookIO.USE_CONTENT_RESOLVER && this._url != null) {
            rootDir = FastFile.fromTreeUri(getActivity(), this._url, this._urlPath);
        } else if (!BookIO.USE_CONTENT_RESOLVER && this._urlPath != null) {
            rootDir = FastFile.fromTreeUri(getActivity(), this._url, this._urlPath);
        }
        if (rootDir != null) {
            try {
                if (FastFile.USE_SKETCH) {
                    String newBookName2 = FastFile.USE_SKETCH_PREFIX + UUID.randomUUID().toString();
                    rootDir.createDirectory(newBookName2);
                    File folder = new File(rootDir.getFilePath(), newBookName2);
                    File file_2 = new File(folder, FastFile.USE_SKETCH_CONFIG);
                    JSONObject item = new JSONObject();
                    item.put(FastFile.USE_SKETCH_CONFIG_DISPNAME, newBookName);
                    FastFile.saveMetaText(file_2, item.toString());
                } else {
                    rootDir.createDirectory(newBookName);
                }
                this.openRootDir(this._url, this._urlPath);
                this.bookListAdapter.notifyDataSetChanged();
                this.listview.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        boolean found = false;
                        for (int i = 0; i < files.size(); ++i) {
                            FastFile f = files.get(i);
                            //FIXME: this is a bad idea
                            if (f != null && f.getDisplayName() != null && f.getDisplayName().equals(newBookName)) {
                                int pageIdx = i;

                                Uri data = files.get(pageIdx).getUri();
                                String filePath = files.get(pageIdx).getFilePath();
                                BookActivity4Utils.openBookUI(getActivity(),
                                        data, filePath, null, backText,
                                        false, isIntentNew);
                                found = true;
                                break;
                            }
                        }
                        cancelWaitingProgressDialog();
                        if (!found) {
                            showMessage("Can't create book directory (" + newBookName + ").");
                        }
                    }
                }, 0/*500*/);
            } catch (Exception var8) {
                cancelWaitingProgressDialog();
                this.showMessage("Can't create book directory (" + newBookName + ").");
            }
            return;
        }

        if (false) {
            throw new RuntimeException("Can't open dir"); //FIXME:
        } else {
            Toast.makeText(getActivity(), "Can't open dir", Toast.LENGTH_SHORT).show();
        }
        cancelWaitingProgressDialog();
    }

    private ProgressDialog mProgressDialog = null; // 对话框对象
    protected void createWaitingProgressDialog() {
        if (mProgressDialog == null || !mProgressDialog.isShowing()) {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setTitle("");
            mProgressDialog.setMessage("Creating note, please wait...");
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

    //FIXME:
    private void getRootDirUrl(Uri uri) {
        if (BookIO.USE_CONTENT_RESOLVER) {
            if (uri != null) {
                getActivity().getContentResolver().takePersistableUriPermission(uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION |
                                Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                this.writeLastUri(uri);
                this.openRootDir(uri, null);
            }
        } else {
            throw new IllegalArgumentException("not implemented");
        }
    }


    EditText g_textState = null;
    private void NewBookPopup(OnNewBookListener onNewBook, OnPopDismissListener onDismiss) {
        if (false) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setPositiveButton("CREATE", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (onDismiss != null) {
                        onDismiss.onDismiss();
                    }
                    String textState = "";
                    if (g_textState != null) {
                        textState = g_textState.getText().toString();
                    }
                    if (textState != "") {
                        if (onNewBook != null) {
                            onNewBook.onNewBook(textState);
                        }
                    }
                }
            });
            builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (onDismiss != null) {
                        onDismiss.onDismiss();
                    }
                    if (dialogInterface != null) {
                        dialogInterface.dismiss();
                    }
                }
            });
            builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    if (onDismiss != null) {
                        onDismiss.onDismiss();
                    }
                    if (dialogInterface != null) {
                        dialogInterface.dismiss();
                    }
                }
            });
            builder.setView(R.layout.dialog_loadpages);
            builder.setCancelable(true);
            final AlertDialog dialog = builder.create();
//        dialog.setContentView(R.layout.dialog_loadpages); //don't use this
            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialogInterface) {
                    g_textState = dialog.findViewById(R.id.textState);
                }
            });
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    g_textState = null;
                }
            });
            dialog.show();
        } else if (false) {
            AlertDialog dialog = new MaterialAlertDialogBuilder(getActivity(), BookActivity4Utils.getCenteredTitleThemeOverlay())
                    //.setTitle(title)
                    .setView(R.layout.activity_booklist_dialog1)
                    .setCancelable(false)
                    .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            TextView input = ((AlertDialog) dialog).findViewById(R.id.textState);
                            //Toast.makeText(BookListActivity.this, input.getText(), Toast.LENGTH_LONG).show();
                            showNewNote2Dialog(input.getText().toString());
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .create();
            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    TextView tvDialogTitle = ((AlertDialog) dialog).findViewById(R.id.tvDialogTitle);
                    tvDialogTitle.setText("New note");
                    TextView input = ((AlertDialog) dialog).findViewById(R.id.textState);
                    String defaultNoteName = generateNoteName();
                    input.setText(defaultNoteName);
                }
            });
            dialog.show();
        } else if (false) {
            String defaultNoteName = generateNoteName();
            NewNoteDialog dialog = new NewNoteDialog(getActivity(), defaultNoteName);
            dialog.show();
        } else {
            String defaultNoteName = generateNoteName();
            String backText = FileMeta.NONE;
            onNewBook(defaultNoteName, backText);
        }
    }

    public void showNewNote2Dialog(final String newNoteName) {
        AlertDialog dialog = new MaterialAlertDialogBuilder(getActivity(), BookActivity4Utils.getCenteredTitleThemeOverlay())
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
                            onNewBook(newNoteName, backText);
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                int[] ids = {
                        R.id.radio1,
                        R.id.radio2,
                        R.id.radio3,
                        R.id.radio4,
                        R.id.radio5,
                };
                for (int id : ids) {
                    RadioButton input = ((AlertDialog) dialog).findViewById(id);
                    input.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            for (int id2 : ids) {
                                if (id2 != id) {
                                    RadioButton input = ((AlertDialog) dialog).findViewById(id2);
                                    input.setChecked(false);
                                }
                            }
                        }
                    });
                }
            }
        });
        dialog.show();
    }

    public String generateNoteName() {
        String noteName = "Note 1";
        List<String> bookTitleList = new ArrayList<>();
        if (files != null) {
            for (FastFile book : files) {
                if (book != null && book.getDisplayName() != null) {
                    bookTitleList.add(book.getDisplayName());
                }
            }
        }
        int index = 1;
        while (bookTitleList.contains(noteName)) {
            index++;
            noteName = "Note " + index;
        }
        return noteName;
    }

    public interface OnNewBookListener {
        void onNewBook(String bookName);
    }

    public interface OnPopDismissListener {
        void onDismiss();
    }




    public static AlertDialog showOkDialog(final Activity c,
                                           final String message,
                                           final String actionString,
                                           final Runnable action,
                                           Runnable onDismiss) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setMessage(message);
        builder.setCancelable(true);
        builder.setPositiveButton(actionString, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int id) {
                if (action != null) {
                    action.run();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int id) {
                if (onDismiss != null) {
                    onDismiss.run();
                }
            }
        });
        AlertDialog create = builder.create();
        create.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (onDismiss != null) {
                    onDismiss.run();
                }
                //Keyboards.hideNavigation(c);
            }
        });
        create.show();
        return create;
    }

    public void onNewBook(String textState, String backText) {
        if (textState != null && textState.length() > 0) {
            addNewBook(textState, backText);
        }
    }

    public boolean checkText(String textState) {
        if (textState == null) {
            return false;
        }
        if (pageList != null) {
            for (Page page : pageList) {
                if (page != null && page.getTitle() != null &&
                        page.getTitle().equalsIgnoreCase(textState)) {
                    return false; //重复名称不允许
                }
            }
        }
        return true;
    }
}
