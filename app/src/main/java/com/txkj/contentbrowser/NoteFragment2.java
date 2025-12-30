package com.txkj.contentbrowser;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.BaseExtractor;
import com.foobnix.android.utils.Apps;
import com.foobnix.android.utils.KeyboardsMod;
import com.foobnix.android.utils.LOG;
import com.foobnix.dao2.FileMeta;
import com.foobnix.pdf.info.AppsConfig;
import com.foobnix.pdf.info.view.EditTextHelper;
import com.txkj.drawingapp.R;
import com.txkj.drawingapp.activity.BookActivity4Config;
import com.txkj.drawingapp.activity.BookActivity4Utils;
import com.txkj.notemobile2.book.FastFile;

import org.json.JSONException;
import org.json.JSONObject;
import org.librera.JSONArray;
import org.librera.LinkedJSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import gm.com.dosya.utils.FileTransactions;

public class NoteFragment2 extends Fragment {
//    public final static boolean USE_NEW_NOTE = true;
//    public final static String APPNAME = "txkjnote";
    public final static String APPNAME_NEW = "txkjnote2";

    private final static String STR_NO_ITEMS = "Nothing here yet";//"No items.";
    private final static String STR_LOADING = "Loading...";

    AutoCompleteTextView searchEditText;
    TextView tvPageInfo;
    private TextView tvEmpty1;
    private View progressLoading1, loadingContent1;
    private LinearLayout llEmpty1;

    private final static boolean USE_EXTERNAL_FILE = true;

    private static final String SHARED_PREFERENCES_NAME = "FlutterSharedPreferences";

    //这个值没用了
    private static final String SHARE_PACKAGE_NAME = "com.txkj.notemobile";//"online.xournal.mobile";
    //PreferencesKeys.kRecentFiles
    /*
class PreferencesKeys {
  static const String kRecentFiles = 'recentFiles';
}
     */
    public static final String KEY_RECENT_FILES = "recentFiles";

    private final static String TAG = "HomeFragment";
    private final static boolean TEST_GRID = false;
    private final static int SINGLE_GRID_DP_WIDTH = 120;
    //这个宽度参考pagegridviewitem_library的最大宽度，例如封面的dp宽度（可以稍微设置大一点）








    private boolean isCheckMode = false;
    private List<FileMeta> recentNoteList;
    private GridView recentNoteView;
    NoteGridAdapter2 recentNoteAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note2, container, false);
//        view.findViewById(R.id.button_second).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                try {
//                    Intent intent = new Intent();
////                    intent.setAction(android.content.Intent.ACTION_VIEW);
//                    intent.setClassName("online.xournal.mobile",
//                            "online.xournal.mobile.MainActivity");
//                    startActivity(intent);
//                } catch (ActivityNotFoundException e) {
//                    e.printStackTrace();
//                }
//            }
//        });

        ImageButton imageButtonBack = (ImageButton) view.findViewById(R.id.imageButtonBack);
        ImageButton buttonListNewBook = (ImageButton) view.findViewById(R.id.buttonListNewBook);
        ImageButton buttonMore = (ImageButton) view.findViewById(R.id.buttonMore);
        imageButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() != null) {
                    getActivity().finish();
                }
            }
        });
        buttonListNewBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onNewBook();
            }
        });
        buttonMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenuNoteFragment2(view);
            }
        });

        searchEditText = (AutoCompleteTextView) view.findViewById(R.id.filterLine_Library);
        tvPageInfo = (TextView) view.findViewById(R.id.tvPageInfo);

//        if (AppState.get().appTheme == AppState.THEME_DARK_OLED || (AppState.get().appTheme == AppState.THEME_DARK && TintUtil.color == Color.BLACK)) {
//            searchEditText.setBackgroundResource(R.drawable.bg_search_edit_night);
//        }

        searchEditText.addTextChangedListener(filterTextWatcher);
        searchEditText.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        EditTextHelper.enableKeyboardSearch(searchEditText, new Runnable() {
            @Override
            public void run() {
                KeyboardsMod.close(searchEditText);
                KeyboardsMod.hideNavigation(getActivity());
            }
        });

        ImageButton buttonNewBookHide = (ImageButton) view.findViewById(R.id.buttonNewBookHide);
        buttonNewBookHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        //笔记历史记录加载

        recentNoteList = new ArrayList<FileMeta>();
        recentNoteView = (GridView) view.findViewById(R.id.notegridview_note);
        recentNoteView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        DisplayMetrics DM = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(DM);
        if (DM.heightPixels > DM.widthPixels) {
            recentNoteView.setNumColumns(3);
        } else {
            recentNoteView.setNumColumns(4);
        }
        //recentNoteView.setBackgroundColor(Color.WHITE);
        recentNoteAdapter = new NoteGridAdapter2(this.getContext(), recentNoteList);
        recentNoteView.setAdapter(recentNoteAdapter);
//        recentNoteView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                if (recentNoteAdapter != null) {
//                    Rect r = new Rect();
//                    recentNoteView.getGlobalVisibleRect(r);
//                    recentNoteAdapter.gridHeight = (int)(r.width() / recentNoteView.getNumColumns() / 210.0 * 297.0);
//                    recentNoteAdapter.notifyDataSetChanged();
//                }
//                try {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                        recentNoteView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                    } else {
//                        recentNoteView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
//                    }
//                } catch (Throwable e) {
//                    e.printStackTrace();
//                }
//            }
//        });
        tvEmpty1 = view.findViewById(R.id.tvEmpty1);
        tvEmpty1.setText(STR_LOADING);
        progressLoading1 = view.findViewById(R.id.progressLoading1);
        loadingContent1 = view.findViewById(R.id.loadingContent1);
        progressLoading1.setVisibility(View.VISIBLE);
        loadingContent1.setVisibility(View.GONE);
        llEmpty1 = (LinearLayout) view.findViewById(R.id.llEmpty1);
        recentNoteView.setEmptyView(llEmpty1);
        recentNoteView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                for (FileMeta meta : recentNoteList) {
                    if (meta != null) {
                        meta.checkShow = true;
                        meta.checkSelect = false;
                    }
                }
                if (!isCheckMode) {
                    isCheckMode = true;
                }
                recentNoteAdapter.notifyDataSetChanged();
                return false;
            }
        });
        //recentNoteAdapter.
        recentNoteView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (isCheckMode) {
                    FileMeta meta = recentNoteList.get(position);
                    if (meta != null) {
                        meta.checkSelect = !meta.checkSelect;
                    }
                    recentNoteAdapter.notifyDataSetChanged();
                } else {
                    FileMeta meta = recentNoteList.get(position);
                    if (meta != null) {
                        String APP_FILE = meta.getPathTxt();
                        String rootPath = new File(Environment.getExternalStorageDirectory(), APPNAME_NEW).toString();
                        File dirPath = new File(rootPath, APP_FILE);
                        if (APP_FILE != null) {
                            BookActivity4Utils.openBookUI(getActivity(),
                                    Uri.fromFile(dirPath), dirPath.getAbsolutePath(), null, null,
                                    false, false);
                        }
                    }
                }
            }
        });

        if (BookActivity4Utils.APP_OPEN != null && BookActivity4Utils.APP_OPEN.equals("NEW")) {
            isIntentNew = true;
            onNewBook();
        } else if (BookActivity4Utils.APP_FILE != null && !BookActivity4Utils.APP_FILE.equals("")) {
            isIntentOpen = true;
            String APP_FILE = BookActivity4Utils.APP_FILE;
            String rootPath = new File(Environment.getExternalStorageDirectory(), APPNAME_NEW).toString();
            File dirPath = new File(rootPath, APP_FILE);
            if (APP_FILE != null) {
                BookActivity4Utils.openBookUI(getActivity(),
                        Uri.fromFile(dirPath), dirPath.getAbsolutePath(), null, null,
                        false, true);
            }
        }
        if (isIntentNew || isIntentOpen) {
//            view.findViewById(R.id.llTop).setVisibility(View.GONE);
//            view.findViewById(R.id.tvLoading).setVisibility(View.GONE);
//            view.findViewById(R.id.progressLoading1).setVisibility(View.VISIBLE);
            progressLoading1.setVisibility(View.GONE);
        } else {
            if (true) { //FIXME:小心，注释此处无法阻止监听器执行populate
                onGridList();
                if (true) {
                    //don't use onResume
                    populate();
                }
            }
        }



        return view;
    }
    private boolean isIntentNew = false;
    private boolean isIntentOpen = false;

    public String generateNoteName() {
        String noteName = "Note 1";
        List<FileMeta> files = prepareData("");
        List<String> bookTitleList = new ArrayList<>();
        if (files != null) {
            for (FileMeta book : files) {
                //String path == book.getPathTxt();
                //String displayName == book.getTitle();
                if (book != null && book.getTitle() != null) {
                    bookTitleList.add(book.getTitle());
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



    private ProgressDialog mProgressDialog = null; // 对话框对象
    protected void createWaitingProgressDialog() {
//        if (isIntentNew || isIntentOpen) {
//            return;
//        }
        if (mProgressDialog == null || !mProgressDialog.isShowing()) {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setTitle("");
            mProgressDialog.setMessage("Creating, please wait...");
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
    File folder = null;
    private void onNewBook() {
        createWaitingProgressDialog();
        final Runnable target = () -> {
            if (getActivity() == null) {
                return;
            }
            try {
                folder = null;
                String newNoteName = generateNoteName();
                String rootPath = new File(Environment.getExternalStorageDirectory(), APPNAME_NEW).toString();
                String newBookFolderName = BookActivity4Config.USE_SKETCH_PREFIX + UUID.randomUUID().toString();
                folder = new File(rootPath, newBookFolderName);
                try {
                    boolean result = folder.mkdirs();
                } catch (Throwable eee) {
                    eee.printStackTrace();
                }
                File file_2 = new File(folder, BookActivity4Config.USE_SKETCH_CONFIG);
                JSONObject item = new JSONObject();
                try {
                    item.put(BookActivity4Config.USE_SKETCH_CONFIG_DISPNAME, newNoteName);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                FastFile.saveMetaText(file_2, item.toString());
            } finally {

            }
            if (isDetached() || Apps.isDestroyedActivity(getActivity())) {
                return;
            }
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (folder != null) {
                            File dirPath = folder;
                            String backText = com.txkj.notemobile2.colorpicker.FileMeta.NONE;
                            String filePath = dirPath.getAbsolutePath();
                            Uri data = Uri.fromFile(dirPath);
                            BookActivity4Utils.openBookUI(getActivity(),
                                    data, filePath, null, backText,
                                    false, isIntentNew);
                        }
                    } finally {
                        cancelWaitingProgressDialog();
                    }
                }
            });
        };
        AppsConfig.executorService.submit(target);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public void onGridList() {
        LOG.d("onGridList");
//        onGridList(AppState.get().recentMode, null, recentAdapter, null);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        handler = new Handler();
    }
    volatile boolean inProgress = false;
    Handler handler;
    private void prepareDataInBackgroundSync() {
        prepareDataInBackground();
    }
    public void populate() {
        if (isCheckMode) {
            LOG.d("isCheckMode == true, stop update");
            return;
        }

        if (inProgress) {
            LOG.d("IN_PROGRESS");
            return;
        }

        final Runnable target = () -> {

            if (getActivity() == null) {
                return;
            }


            try {
                inProgress = true;
                prepareDataInBackgroundSync();
            } finally {
                inProgress = false;

            }
            if (isDetached() || Apps.isDestroyedActivity(getActivity())) {
                return;
            }

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (isAdded()) {
                        try {
                            populateDataInUI();
                        } catch (Exception e) {
                            LOG.e(e);
                        }
                    }

                }
            });
        };
        AppsConfig.executorService.submit(target);
    }

    //@Override
    public void prepareDataInBackground() {
        String txt = searchEditText.getText().toString().trim();
        List<FileMeta> recentNoteList2 = prepareData(txt);
        recentNoteList.clear();
        for (int i = 0; i < recentNoteList2.size(); ++i) {
            recentNoteList.add(recentNoteList2.get(i));
        }
    }

    public List<FileMeta> prepareData(String txt) {
        String recentFiles = "";
        try {
            String rootPath = null;
            rootPath = new File(Environment.getExternalStorageDirectory(), APPNAME_NEW).toString();
            boolean kkk = new File(rootPath).mkdirs();
            if (new File(rootPath, "flutter." + KEY_RECENT_FILES + ".txt").exists()) {
                InputStream fis = new FileInputStream(new File(rootPath, "flutter." + KEY_RECENT_FILES + ".txt"));
                InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
                BufferedReader reader = new BufferedReader(isr);
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
                recentFiles = recentFilesBuffer.toString();
                reader.close();
                isr.close();
                fis.close();
            }
        } catch (Throwable eee) {
            eee.printStackTrace();
        }
        //Log.e(TAG, "recentFiles: " + recentFiles);
        List<FileMeta> recentNoteList2 = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(recentFiles);
            for (int i = 0; i < jsonArray.length(); i++) {
                LinkedJSONObject item = jsonArray.getJSONObject(i);
                if (item != null) {
                    String preview = item.optString("preview");
                    String name = item.optString("name");
                    String path = item.optString("path");
                    String createTime = item.optString("createTime");
                    String updateTime = item.optString("updateTime");
                    String dispName = item.optString("dispName");

                    FileMeta fileMeta = new FileMeta();
                    fileMeta.setPathTxt(path);
                    fileMeta.setTitle((dispName != null && dispName.length() > 0) ? dispName : name);
                    if (updateTime != null) {
                        String updateTimeStr = null;
                        if (updateTime != null && updateTime.length() > 0) {
                            try {
                                Date date = new Date(Long.parseLong(updateTime));
                                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                updateTimeStr = format.format(date);
                            } catch (Throwable eeee) {
                                eeee.printStackTrace();
                            }
                        }
                        fileMeta.setDateTxt(updateTimeStr);
                    }
                    //preview字段加上base64头部才能显示出来封面
                    fileMeta.setPath(preview != null ? BaseExtractor.BASE64_PREFIX + preview : null);

                    //搜索过滤
                    if (txt != null && txt.length() > 0) {
                        if (name.toLowerCase().contains(txt.toLowerCase())) {
                            recentNoteList2.add(fileMeta);
                        }
                    } else {
                        recentNoteList2.add(fileMeta);
                    }
                }
            }
        } catch (Throwable eee) {
            eee.printStackTrace();
        }
        return recentNoteList2;
    }

    //@Override
    public void populateDataInUI() {
        tvEmpty1.setText(STR_NO_ITEMS);
        progressLoading1.setVisibility(View.GONE);
        loadingContent1.setVisibility(View.VISIBLE);

        recentNoteAdapter.notifyDataSetChanged();
        if (tvPageInfo != null) {
            tvPageInfo.setText("Total item(s) : " + recentNoteAdapter.getCount() + "");
        }
    }

    private final TextWatcher filterTextWatcher = new TextWatcher() {
        @Override
        public void afterTextChanged(final Editable s) {
            //AppState.get().searchQuery = s.toString();
        }

        @Override
        public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {
        }

        @Override
        public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
            populate();
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        //notifyFragment();
//        if (BookActivity4Utils.isTopNoteFragment2(getActivity())) {
//            populate(); //FIXME:是否重复执行？
//        }
    }

    public void refresh() {
        populate();
    }

    public void setSearch(String text) {
        if (text != null && searchEditText != null) {
            searchEditText.setText(text);
        }
    }

    MenuItem deleteMenu;
    MenuItem cancelmenu;
    public void showPopupMenuNoteFragment2(View view) {
        PopupMenu popupMenu = new PopupMenu(getActivity(), view);
        popupMenu.getMenuInflater().inflate(R.menu.popup_menu_note2, popupMenu.getMenu());
        Menu menu = popupMenu.getMenu();
        deleteMenu = menu.findItem(R.id.delete);
        deleteMenu.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
                if (isCheckMode) {
                    List<String> arrPaths = new ArrayList<>();
                    for (FileMeta meta : recentNoteList) {
                        if (meta != null && meta.checkShow && meta.checkSelect) {
                            String path = meta.getPathTxt();
                            //Toast.makeText(getActivity(), "path : " + path, Toast.LENGTH_LONG).show();
                            String rootPath = new File(Environment.getExternalStorageDirectory(), APPNAME_NEW).toString();
                            FileTransactions.DeleteRecursive(new File(rootPath, path));
                            arrPaths.add(path);
                        }
                    }
                    removeRecent(arrPaths);
                }
                for (FileMeta meta : recentNoteList) {
                    if (meta != null) {
                        meta.checkShow = false;
                        meta.checkSelect = false;
                    }
                }
                if (isCheckMode) {
                    isCheckMode = false;
                }
                recentNoteAdapter.notifyDataSetChanged();
                populate();
                return true;
            }
        });
        cancelmenu = menu.findItem(R.id.cancelmenu);
        cancelmenu.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
                for (FileMeta meta : recentNoteList) {
                    if (meta != null) {
                        meta.checkShow = false;
                        meta.checkSelect = false;
                    }
                }
                if (isCheckMode) {
                    isCheckMode = false;
                }
                recentNoteAdapter.notifyDataSetChanged();
                return true;
            }
        });
        popupMenu.show();
    }

    public void removeRecent(List<String> arrPaths) {
        String recentFiles = "";
        if (USE_EXTERNAL_FILE) {
            try {
                String rootPath = null;
                rootPath = new File(Environment.getExternalStorageDirectory(), APPNAME_NEW).toString();
                boolean kkk = new File(rootPath).mkdirs();
                if (new File(rootPath, "flutter." + KEY_RECENT_FILES + ".txt").exists()) {
                    InputStream fis = new FileInputStream(new File(rootPath, "flutter." + KEY_RECENT_FILES + ".txt"));
                    InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
                    BufferedReader reader = new BufferedReader(isr);
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
                    recentFiles = recentFilesBuffer.toString();
                    reader.close();
                    isr.close();
                    fis.close();
                }
            } catch (Throwable eee) {
                eee.printStackTrace();
            }
        }
        //Log.e(TAG, "recentFiles: " + recentFiles);
        JSONArray jsonArray2 = new JSONArray();
        try {
            JSONArray jsonArray = new JSONArray(recentFiles);
            for (int i = 0; i < jsonArray.length(); i++) {
                LinkedJSONObject item = jsonArray.getJSONObject(i);
                if (item != null) {
                    String preview = item.optString("preview");
                    String name = item.optString("name");
                    String path = item.optString("path");
                    String createTime = item.optString("createTime");
                    String updateTime = item.optString("updateTime");
                    String dispName = item.optString("dispName");
                    if (path != null && arrPaths.contains(path)) {
                        //skip, removed
                    } else {
                        //written
                        jsonArray2.put(item);
                    }
                }
            }
        } catch (Throwable eee) {
            eee.printStackTrace();
        }
        try {
            String rootPath = null;
            rootPath = new File(Environment.getExternalStorageDirectory(), APPNAME_NEW).toString();
            FileOutputStream fout = new FileOutputStream(new File(rootPath, "flutter." + KEY_RECENT_FILES + ".txt"));
            OutputStreamWriter osw = new OutputStreamWriter(fout, "UTF-8");
            BufferedWriter writer = new BufferedWriter(osw);
            writer.write(jsonArray2.toString());
            writer.flush();
            writer.close();
            osw.close();
            fout.close();
        } catch (Throwable eee) {
            eee.printStackTrace();
        }
    }
}