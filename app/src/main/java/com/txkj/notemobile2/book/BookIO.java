package com.txkj.notemobile2.book;

import android.app.Activity;
import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.util.Base64;
import android.util.Log;
import android.view.EinkPWInterface;
import android.view.animation.AccelerateInterpolator;

import androidx.activity.ActivityViewModelLazyKt;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.BaseExtractor;
import com.foobnix.dao2.FileMeta;
import com.txkj.contentbrowser.NoteFragment4;
import com.txkj.drawingapp.activity.BookActivity4Config;
import com.txkj.drawingapp.activity.BookActivity4Fragment;
import com.txkj.drawingapp.db.NoteItem;
import com.txkj.drawingapp.db.SDNotesDatabase;
import com.txkj.notemobile2.Book;
import com.txkj.notemobile2.ui.CanvasBoox;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.librera.LinkedJSONObject;

import io.github.pastthepixels.freepaint.Graphics.BitmapVector;
import io.github.pastthepixels.freepaint.Graphics.DualScreenCanvas;
import io.github.pastthepixels.freepaint.Graphics.Point;

public class BookIO {
    private final static boolean D = true;
    private final static String TAG = "BookIO";

    public final static boolean USE_META_TXT = true;
    public final static boolean USE_0000_PNG = false;
    public final static boolean USE_CONTENT_RESOLVER = false;
    public static String rootPath = new File(Environment.getExternalStorageDirectory(),
            "txkjnote2" //"pngnote"
        ).toString(); //FIXME:

    private ContentResolver resolver;
    private Pattern pageNamePat;

    public BookIO(ContentResolver resolver) {
        if (USE_CONTENT_RESOLVER) {
            this.resolver = resolver;
        }
        this.pageNamePat = Pattern.compile("([0-9][0-9][0-9][0-9])\\.png");
    }

    private BitmapVector loadBitmap(FastFile file, BookActivity4Fragment fragment) {
        BitmapVector result = new BitmapVector();
        if (USE_CONTENT_RESOLVER) {
            ParcelFileDescriptor it = null;
            try {
                it = this.resolver.openFileDescriptor(file.getUri(), "r");
                result.bitmap = BitmapFactory.decodeFileDescriptor(it.getFileDescriptor());
                result.strVecJson = "";
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
        } else {
            result.bitmap = BitmapFactory.decodeFile(file.getFilePath());
            {
                try {
                    InputStream fis = new FileInputStream(new File(file.getFilePath().replace(".png", ".vecj")));
                    InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
                    BufferedReader reader = new BufferedReader(isr);
                    StringBuffer vecjBuffer = new StringBuffer();
                    while (true) {
                        String line = reader.readLine();
                        if (line != null) {
                            vecjBuffer.append(line);
                            vecjBuffer.append("\n");
                        } else {
                            break;
                        }
                    }
                    result.strVecJson = vecjBuffer.toString();
                    reader.close();
                    isr.close();
                    fis.close();
                } catch (Throwable eee) {
                    eee.printStackTrace();
                }
            }
            if (fragment != null && fragment.getCanvas() instanceof DualScreenCanvas) {
                {
                    String path = file.getFilePath().replace(".png", ".ds.tch");//".ds.png");
                    if (fragment.getCanvas() != null) {
                        EinkPWInterface einkPWlnterface = fragment.getCanvas().einkPWInterface;
                        if (einkPWlnterface != null) {
                            //public abstract void clearContent(Rect rect, boolean post, boolean clearTouch)
//                            einkPWlnterface.clearContent(null, true, false);

                            einkPWlnterface.setLoadFilePath(path, true);
                            //einkPWlnterface.saveBitmap(true, null);
                            //einkPWlnterface.setLoadFilePath(null, true);
                        }
                    }
                }
            }
        }
        return result; //FIXME: check null
    }

    public final static int loadThumbnail_size = 3;
    private Bitmap loadThumbnail(FastFile bookDir, String displayName) {
        FastFile it = bookDir.findFile(displayName);
        Bitmap result = null;
        if (it != null) {
            result = this.loadBitmapThumbnail(it, loadThumbnail_size); //3
        }
        return result;
    }
    public final static int loadThumbnailParent_size = 3;
    private Bitmap loadThumbnailParent(FastFile bookDir, String displayName) {
        FastFile it = bookDir.findParentFile(displayName);
        Bitmap result = null;
        if (it != null) {
            result = this.loadBitmapThumbnail(it, loadThumbnailParent_size);//3);
        }
        return result;
    }

    private String loadMeta(FastFile bookDir, String displayName) {
        FastFile it = bookDir.findFile(displayName);
        String result = null;
        if (it != null) {
            result = this.loadMetaText(it);
        }
        return result;
    }
    private String loadMetaParent(FastFile bookDir, String displayName) {
        FastFile it = bookDir.findParentFile(displayName);
        String result = null;
        if (it != null) {
            result = this.loadMetaText(it);
        }
        return result;
    }

//    private String loadMetaPng(FastFile bookDir, String displayName) {
//        FastFile it = bookDir.findFile(displayName);
//        String result = null;
//        if (it != null) {
//            result = this.loadMetaText(it);
//        }
//        return result;
//    }


    public Bitmap loadThumbnail(FastFile bookDir) {
        return this.loadThumbnail(bookDir, "0000.png");
    }

    public Bitmap loadThumbnailParent(FastFile bookDir) {
        return this.loadThumbnailParent(bookDir, "0000.png");
    }

    public String loadMetaParent(FastFile bookDir) {
        return this.loadMetaParent(bookDir, "0000.meta");
    }
    public String loadMeta(FastFile bookDir) {
        return this.loadMeta(bookDir, "0000.meta");
    }
    public String loadMetaPng(FastFile bookDir) {
        if (bookDir.getName() != null) {
            return this.loadMetaParent(bookDir, bookDir.getName().replace(".png", ".meta"));
        } else {
            return "";
        }
    }

    public Bitmap loadBgThumbnail(FastFile bookDir) {
        return this.loadThumbnail(bookDir, "background.png");
    }

    public final static int loadPageThumbnail_size = 4;
    public Bitmap loadPageThumbnail(FastFile file) {
        return this.loadBitmapThumbnail(file, loadPageThumbnail_size); //4
    }

    public Bitmap loadBgForGrid(FastFile bookDir) {
        FastFile it = bookDir.findFile("background.png");
        Bitmap result = null;
        if (it != null) {
            result = this.loadBitmapThumbnail(it, 4);
        }
        return result;
    }

    private String loadMetaText(FastFile file) {
        InputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader reader = null;
        try {
            fis = new FileInputStream(file.getFilePath());
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
            return recentFilesBuffer.toString();
        } catch (IOException eee) {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(isr != null) {
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
        return "";
    }

    private Bitmap loadBitmapThumbnail(FastFile file, int sampleSize) {
        Bitmap result = null;
        if (BookIO.USE_CONTENT_RESOLVER) {
            ParcelFileDescriptor it = null;
            try {
                it = this.resolver.openFileDescriptor(file.getUri(), "r");
                BitmapFactory.Options option = new BitmapFactory.Options();
                option.inSampleSize = sampleSize;
                result = BitmapFactory.decodeFileDescriptor(it.getFileDescriptor(), null, option);
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
        } else {
            BitmapFactory.Options option = new BitmapFactory.Options();
            option.inSampleSize = sampleSize;
            String filePath = file.getFilePath();
            String dsFilePath = filePath.replace(".png", ".ds.png");
            if (new File(dsFilePath).exists()) {
                result = BitmapFactory.decodeFile(dsFilePath, option);
            } else {
                result = BitmapFactory.decodeFile(filePath, option);
            }
        }
        return result;
    }

    private boolean isEmpty(FastFile file) {
        return file.isEmpty();
    }

    public boolean isPageEmpty(BookPage page) {
        return this.isEmpty(page.getFile());
    }

    public BitmapVector loadBitmap(BookPage page, BookActivity4Fragment fragment) {
        return this.loadBitmap(page.getFile(), fragment);
    }

    public BitmapVector loadBitmapOrNull(BookPage page, BookActivity4Fragment fragment) {
        if (false) {
            //FIXME: if page file size==0, isPageEmpty return true, may make bug
            return this.isPageEmpty(page) ? null : this.loadBitmap(page, fragment);
        } else {
            return this.loadBitmap(page, fragment);
        }
    }

    public Bitmap loadBgOrNull(Book book, BookActivity4Fragment fragment) {
        FastFile it = book.getBgImage();
        Bitmap result = null;
        if (it != null) {
            result = this.loadBitmap(it, fragment).bitmap; //bg vecj not used
        }
        return result;
    }

    public void saveMeta(String pattern, String dirUrlPath, String displayName, boolean isSaveBgPng, Point documentSize) {
        OutputStream it = null;
        try {
            if (D) {
                Log.e(TAG, "saving meta " + pattern + " to " + dirUrlPath + ", " + displayName);
            }
            String oldContent = FastFile.loadMetaText(new File(dirUrlPath, displayName));
            it = new FileOutputStream(new File(dirUrlPath, displayName));
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(it, StandardCharsets.UTF_8);
            BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
            JSONObject item = new JSONObject();
            if (oldContent != null && oldContent.length() > 0){
                item = new JSONObject(oldContent);
            }
            item.put(BookActivity4Config.USE_SKETCH_CONFIG_PATTERN, pattern);
            bufferedWriter.write(item.toString());
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
        if (isSaveBgPng && documentSize != null && documentSize.x > 0 && documentSize.y > 0) {
            //FIXME: added: bg.png
            Bitmap emptyBmp = null;
            try {
                if (pattern != null) {
                    emptyBmp = Bitmap.createBitmap((int)documentSize.x,
                            (int)documentSize.y, Bitmap.Config.ARGB_8888);
                    if (BookIO.USE_META_TXT) {
                        emptyBmp.eraseColor(0xFFFFFFFF); //run here
                    } else {
                        emptyBmp.eraseColor(0x00000000);
                    }
                    DualScreenCanvas.initBackText(pattern, emptyBmp, 1);
                }
            } catch (Throwable eee) {
                eee.printStackTrace();
            }
            if (emptyBmp != null) {
                File parent = new File(dirUrlPath, displayName).getParentFile();
                File bgFile = new File(parent, "bg.png");
                try (FileOutputStream outputStream = new FileOutputStream(bgFile)) {
                    emptyBmp.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void savePageOrder(BookPage page, Book book, int lastPageIndex, Activity act) {
        boolean isFailed = false;
        String folder = null;
        if (page.getFile().getFilePath() != null) {
            folder = new File(page.getFile().getFilePath()).getParent();
        }
        if (folder == null ||
                !new File(folder, BookActivity4Config.USE_SKETCH_CONFIG).exists() ||
                !new File(folder, BookActivity4Config.USE_SKETCH_CONFIG).canWrite()
        ) {
            isFailed = true;
        }
        if (!isFailed) {
            try {
                File file_2 = new File(folder, BookActivity4Config.USE_SKETCH_CONFIG);
                String str = FastFile.loadMetaText(file_2);
                JSONObject item = new JSONObject(str);
                JSONObject pageOrderObj = new JSONObject();
                for (Integer key : book.getPagetNameMap().keySet()) {
                    pageOrderObj.put(Integer.toString(key), book.getPagetNameMap().get(key));
                }
                item.put(BookActivity4Config.USE_SKETCH_CONFIG_PAGEORDER, pageOrderObj);
                item.put(BookActivity4Config.USE_SKETCH_CONFIG_LASTPAGEINDEX, lastPageIndex);
                FastFile.saveMetaText(file_2, item.toString(), act);
            } catch (JSONException e) {
                e.printStackTrace();
                isFailed = true;
            }
        }
    }

    public void saveBitmap(BookPage page, Bitmap bitmap, String vecJson, Book book, Activity act, int lastPageIndex, BookActivity4Fragment fragment) {
        if (bitmap == null) {
            Log.e(TAG, "saveBitmap, bitmap == null");
        }
        if (USE_CONTENT_RESOLVER) {
            OutputStream it = null;
            try {
                it = this.resolver.openOutputStream(page.getFile().getUri(), "w"); //"wt"
                if (bitmap != null) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 80, it);
                }
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
        } else {
            OutputStream it = null;
            try {
                if (D) {
                    Log.e(TAG, "saving " + page.getFile().getFilePath());
                }
                it = new FileOutputStream(page.getFile().getFilePath());
                if (bitmap != null) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 80, it);
                }
                if (BookIO.USE_META_TXT) {
                    //FIXME: not save .meta here
                    //saveMeta();
                }
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

            OutputStream it2 = null;
            try {
                if (D) {
                    Log.e(TAG, "saving " + page.getFile().getFilePath().replace(".png", ".vecj"));
                }
                it2 = new FileOutputStream(page.getFile().getFilePath().replace(".png", ".vecj"));
                if (vecJson != null) {
                    it2.write(vecJson.getBytes(StandardCharsets.UTF_8));
                }
            } catch (Throwable e) {
                e.printStackTrace();
            } finally {
                try {
                    if (it2 != null) {
                        it2.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        {
            savePageOrder(page, book, lastPageIndex, act);
        }


        try {
            File file = new File(page.getFile().getFilePath());
            File folder = file.getParentFile();
            String name = folder.getName();
            SimpleFileMeta itemFound = null;
            itemFound = new SimpleFileMeta();
            itemFound.setCreateTime("" + new Date().getTime());
            itemFound.setPath(name); //FIXME:may be not name
            itemFound.setName(name);
            itemFound.setDispName(FastFile.getDisplayMetaName(folder));
            itemFound.setUpdateTime("" + new Date().getTime());

            //https://blog.csdn.net/ocean__yang/article/details/113740043
            if (!USE_0000_PNG) {
                if (USE_META_TXT) {
                    try {
                        String pattern = null;
                        String metaTxt = loadMetaPng(page.getFile());
                        if (metaTxt != null && !metaTxt.isEmpty()) {
                            JSONObject item = new JSONObject(metaTxt);
                            pattern = item.optString(BookActivity4Config.USE_SKETCH_CONFIG_PATTERN);
                        }
                        if (pattern != null && bitmap != null) {
                            Bitmap emptyBmp = Bitmap.createBitmap(bitmap.getWidth(),
                                    bitmap.getHeight(), Bitmap.Config.ARGB_8888);
                            if (BookIO.USE_META_TXT) {
                                emptyBmp.eraseColor(0xFFFFFFFF); //run here
                            } else {
                                emptyBmp.eraseColor(0x00000000);
                            }
                            CanvasBoox.initBackText(pattern, emptyBmp, 1);
                            Canvas canvas = new Canvas(emptyBmp);
                            Paint paint = new Paint();
                            canvas.drawBitmap(bitmap, 0, 0, paint);
                            bitmap = emptyBmp;
                        }
                    } catch (Throwable eee) {
                        eee.printStackTrace();
                    }
                }
                if (bitmap != null) {
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                    byte[] imageByte = outputStream.toByteArray();
                    itemFound.setPreview(Base64.encodeToString(imageByte, Base64.DEFAULT));
                }
                {
                    //String path = "你的手与保存路径";
                    if (fragment.getCanvas() != null) {
                        EinkPWInterface einkPWlnterface = fragment.getCanvas().einkPWInterface;
                        if (einkPWlnterface != null) {
                            //einkPWlnterface.setLoadFilePath(path, true);
                            final String mypath_ = einkPWlnterface.getPWBitmapFilePath();
                            einkPWlnterface.saveBitmap(true, new EinkPWInterface.PWSaveBitmapListener() {
                                @Override
                                public void saveDone(String s) {
                                    if (mypath_ != null) {
                                        File parent = new File(mypath_).getParentFile();
                                        File coverFile = new File(parent, "cover.png");
                                        copyFile(page, mypath_, coverFile.getAbsolutePath());
                                    }
                                }
                            });
                            //einkPWlnterface.setLoadFilePath(null, true);
                        }
                    }
                }
            } else {
                Bitmap thumbnailBitmap = loadThumbnailParent(page.getFile());
                if (USE_META_TXT) {
                    try {
                        String pattern = null; //NOTE not run here
                        String metaTxt = loadMetaParent(page.getFile());
                        JSONObject item = new JSONObject(metaTxt);
                        if (item != null) {
                            pattern = item.optString(BookActivity4Config.USE_SKETCH_CONFIG_PATTERN);
                        }
                        if (pattern != null) {//NOTE not run here
                            Bitmap emptyBmp = Bitmap.createBitmap(thumbnailBitmap.getWidth(),
                                    thumbnailBitmap.getHeight(), Bitmap.Config.ARGB_8888);
                            //NOTE not run here
                            emptyBmp.eraseColor(0x00000000);
                            CanvasBoox.initBackText(pattern, emptyBmp, BookIO.loadThumbnailParent_size);
                            Canvas canvas = new Canvas(emptyBmp);
                            Paint paint = new Paint();//NOTE not run here
//                            paint.setColor(0xFFFFFFFF);
//                            paint.setStyle(Paint.Style.FILL);
//                            canvas.drawRect(new RectF(0, 0,
//                                    thumbnailBitmap.getWidth(), thumbnailBitmap.getHeight()), paint);
                            canvas.drawBitmap(thumbnailBitmap, 0, 0, paint);
                            thumbnailBitmap = emptyBmp;
                        }
                    } catch (Throwable eee) {
                        eee.printStackTrace();
                    }
                }
                if (thumbnailBitmap != null) {
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    thumbnailBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                    byte[] imageByte = outputStream.toByteArray();
                    itemFound.setPreview(Base64.encodeToString(imageByte, Base64.DEFAULT));
                    thumbnailBitmap.recycle();
                }
            }
            saveRecent_new(act, itemFound);
        } catch (Throwable eee) {
            eee.printStackTrace();
        }
    }

    public void copyFile(BookPage page, String source, String target) {
        if (true) {
            if (new File(source).exists()) {
                try (FileInputStream fis = new FileInputStream(source);
                     FileOutputStream fos = new FileOutputStream(target)) {
                    byte[] buffer = new byte[8192];
                    int length;
                    while ((length = fis.read(buffer)) > 0) {
                        fos.write(buffer, 0, length);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e(TAG, "copyFile not exists: " + source);
            }
        } else {
            //FIXME: don't use this method, too slow, use bg.png
            Bitmap bitmap = BitmapFactory.decodeFile(source);
            if (USE_META_TXT) {
                if (bitmap != null) {
                    try {
                        String pattern = null;
                        String metaTxt = loadMetaPng(page.getFile());
                        if (metaTxt != null && !metaTxt.isEmpty()) {
                            JSONObject item = new JSONObject(metaTxt);
                            pattern = item.optString(BookActivity4Config.USE_SKETCH_CONFIG_PATTERN);
                        }
                        if (pattern != null) {
                            Bitmap emptyBmp = Bitmap.createBitmap(bitmap.getWidth(),
                                    bitmap.getHeight(), Bitmap.Config.ARGB_8888);
                            if (BookIO.USE_META_TXT) {
                                emptyBmp.eraseColor(0xFFFFFFFF); //run here
                            } else {
                                emptyBmp.eraseColor(0x00000000);
                            }
                            CanvasBoox.initBackText(pattern, emptyBmp, 1);
                            Canvas canvas = new Canvas(emptyBmp);
                            Paint paint = new Paint();
                            canvas.drawBitmap(bitmap, 0, 0, paint);
                            bitmap = emptyBmp;
                        }
                    } catch (Throwable eee) {
                        eee.printStackTrace();
                    }
                }
            }
            if (bitmap != null) {
//                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
//                byte[] imageByte = outputStream.toByteArray();
//                itemFound.setPreview(Base64.encodeToString(imageByte, Base64.DEFAULT));
                try (FileOutputStream outputStream = new FileOutputStream(target)) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static Object lockSave = new Object();

    private void saveRecent_new(Activity act, SimpleFileMeta itemFound) {
        synchronized (lockSave) {
            try {
                String dirPath = new File(Environment.getExternalStorageDirectory(), NoteFragment4.APPNAME_NEW).toString();
                SDNotesDatabase mDatabase = new SDNotesDatabase(act, dirPath);
                NoteItem itemNoteFound = null;
                List<NoteItem> items = mDatabase.getAllItems();
                for (NoteItem itemNote : items) {
                    if (itemNote != null &&
                            itemNote.getNoteFilePath() != null &&
                            itemFound.getPath() != null &&
                            itemNote.getNoteFilePath().equals(itemFound.getPath())) {
                        itemNoteFound = itemNote;
                        break;
                    }
                }


                JSONObject obj = new JSONObject();
                obj.put("preview", itemFound.getPreview());
                obj.put("name", itemFound.getName());
                obj.put("path", itemFound.getPath());
                obj.put("createTime", itemFound.getCreateTime());
                obj.put("updateTime", itemFound.getUpdateTime());
                obj.put("dispName", itemFound.getDispName());
                String content = obj.toString();
                NoteItem item = new NoteItem();
                item.setNoteContent(content);
                item.setNoteName(itemFound.getName());
                item.setNoteFilePath(itemFound.getPath());
                item.setCreateTime(itemFound.getCreateTime());
                item.setUpdateTime(itemFound.getUpdateTime());
                item.setNoteName(itemFound.getDispName());
                if (itemNoteFound != null) {
                    mDatabase.updateItem(itemNoteFound.getId(), item);
                } else {
                    mDatabase.addNote(item);
                }
            } catch (Throwable eee) {
                eee.printStackTrace();
            }
        }
    }

    private void saveRecent_remove(Activity act, String filePath) {
        try {
            String dirPath = new File(Environment.getExternalStorageDirectory(), NoteFragment4.APPNAME_NEW).toString();
            SDNotesDatabase mDatabase = new SDNotesDatabase(act, dirPath);
            NoteItem itemNoteFound = null;
            List<NoteItem> items = mDatabase.getAllItems();
            for (NoteItem itemNote : items) {
                if (itemNote != null &&
                        filePath != null &&
                        itemNote.getNoteFilePath() != null &&
                        itemNote.getNoteFilePath().equals(filePath)) {
                    itemNoteFound = itemNote;
                    break;
                }
            }
            if (itemNoteFound != null) {
                mDatabase.removeItemWithId(itemNoteFound.getId());
            }
        } catch (Throwable eee) {
            eee.printStackTrace();
        }
    }

    public void removeSaveRecent(String filePath, Activity act) {
        try {
            saveRecent_remove(act, filePath);
        } catch (Throwable eee) {
            eee.printStackTrace();
        }
    }

    public Book loadBook(FastFile bookDir, Activity act) {
//        val pageMap = bookDir.listFiles()
//                .filter {file ->
//                pageNamePat.matches(file.name)
//        }.map {file ->
//                val res = pageNamePat.find(file.name)!!
//                val pageIdx = res.groupValues[1].toInt()
//            Pair(pageIdx, file)
//        }.toMap()

        Map<Integer, FastFile> pageMap = new HashMap<Integer, FastFile>();
        Map<Integer, String> pageOrderObj = new HashMap<>();
        int lastPageIndex = 0;
        if (BookActivity4Config.USE_UUID_PAGE_NAME) {
            boolean isFailed = false;
            String folder = bookDir.getFilePath();
            if (folder == null ||
                    !new File(folder, BookActivity4Config.USE_SKETCH_CONFIG).exists() ||
                    !new File(folder, BookActivity4Config.USE_SKETCH_CONFIG).canWrite()
            ) {
                isFailed = true;
            }
            if (!isFailed) {
                try {
                    File file_2 = new File(folder, BookActivity4Config.USE_SKETCH_CONFIG);
                    String str = FastFile.loadMetaText(file_2);
                    JSONObject item = new JSONObject(str);
                    //normally hit here
                    lastPageIndex = item.optInt(BookActivity4Config.USE_SKETCH_CONFIG_LASTPAGEINDEX, 0);
                    JSONObject pageOrder = item.optJSONObject(BookActivity4Config.USE_SKETCH_CONFIG_PAGEORDER);
                    if (pageOrder != null) {
                        Iterator<String> it = pageOrder.keys();
                        while (it.hasNext()) {
                            String key = it.next();
                            String value = pageOrder.optString(key);
                            if (key != null && value != null) {
                                try {
                                    int keyV = Integer.parseInt(key);
                                    pageOrderObj.put(keyV, value);
                                    File pngFile = new File(bookDir.getFilePath(), value + ".png");
                                    pageMap.put(keyV, FastFile.fromFile(pngFile.getAbsolutePath()));
                                } catch (Throwable eee) {
                                    eee.printStackTrace();
                                }
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    isFailed = true;
                }
            }
        }
        if (pageOrderObj.isEmpty()) {
            List<FastFile> files = bookDir.listFiles();
            if (files != null) {
                for (FastFile file : files) {
                    try {
                        Matcher res = pageNamePat.matcher(file.getName());
                        if (res.matches()) {
                            //FIXME:java.lang.IllegalStateException: No successful match so far
                            int pageIdx = Integer.parseInt(res.group(1));
                            if (BookActivity4Config.USE_UUID_PAGE_NAME) {
                                if (res.group(1) != null && res.group(1).endsWith(".png")) {
                                    pageOrderObj.put(pageIdx, res.group(1).substring(0, res.group(1).length() - ".png".length()));
                                }
                            }
                            pageMap.put(pageIdx, file);
                        }
                    } catch (Throwable eee) {
                        eee.printStackTrace();
                    }
                }
            }
            if (BookActivity4Config.USE_UUID_PAGE_NAME) {
                boolean isFailed = false;
                String folder = bookDir.getFilePath();
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
                    JSONObject pageOrderObj_ = new JSONObject();
                    for (Integer key : pageOrderObj.keySet()) {
                        pageOrderObj_.put(Integer.toString(key), pageOrderObj.get(key));
                    }
                    lastPageIndex = item.optInt(BookActivity4Config.USE_SKETCH_CONFIG_LASTPAGEINDEX, 0);
                    item.put(BookActivity4Config.USE_SKETCH_CONFIG_PAGEORDER, pageOrderObj_);
                    FastFile.saveMetaText(file_2, item.toString(), act);
                } catch (JSONException e) {
                    e.printStackTrace();
                    isFailed = true;
                }
            }
        }

        //val lastPageIdx = if(pageMap.isEmpty()) 0 else pageMap.maxOf { it.key }
        int lastPageIdx = 0;
        if (!pageMap.isEmpty()) {
            int tempKey = 0;
            for (Integer curKey : pageMap.keySet()) {
                if (curKey != null && curKey > tempKey) {
                    tempKey = curKey;
                }
            }
            lastPageIdx = tempKey;
        }

        List<Integer> pagesTemp = new ArrayList<Integer>();
        for (int i = 0; i <= lastPageIdx; ++i) {
            pagesTemp.add(i);
        }
        List<FastFile> pages = new ArrayList<FastFile>(pagesTemp.size());
        for (Integer it : pagesTemp) {
            FastFile itemOld = pageMap.get(it);
            FastFile item = itemOld != null ? itemOld : BookPage.createEmptyFile(bookDir, it, null);
            pages.add(item);
        }
        FastFile bgFile = bookDir.findFile("background.png");
        return new Book(bookDir, pages, bgFile, lastPageIndex);
    }

    public Book loadBookParentNoCreate(FastFile bookDir, Book book) {
        Map<Integer, FastFile> pageMap = new HashMap<Integer, FastFile>();
        Map<Integer, String> pageOrderObj = new HashMap<>();
        int lastPageIndex = 0;
        if (BookActivity4Config.USE_UUID_PAGE_NAME) {
            boolean isFailed = false;
            String folder = bookDir.getFilePath();
            if (folder == null ||
                    !new File(folder, BookActivity4Config.USE_SKETCH_CONFIG).exists() ||
                    !new File(folder, BookActivity4Config.USE_SKETCH_CONFIG).canWrite()
            ) {
                isFailed = true;
            }
            if (!isFailed) {
                try {
                    File file_2 = new File(folder, BookActivity4Config.USE_SKETCH_CONFIG);
                    String str = FastFile.loadMetaText(file_2);
                    JSONObject item = new JSONObject(str);
                    lastPageIndex = item.optInt(BookActivity4Config.USE_SKETCH_CONFIG_LASTPAGEINDEX, 0);
                    JSONObject pageOrder = item.optJSONObject(BookActivity4Config.USE_SKETCH_CONFIG_PAGEORDER);
                    if (pageOrder != null) {
                        Iterator<String> it = pageOrder.keys();
                        while (it.hasNext()) {
                            String key = it.next();
                            String value = pageOrder.optString(key);
                            if (key != null && value != null) {
                                try {
                                    int keyV = Integer.parseInt(key);
                                    pageOrderObj.put(keyV, value);
                                } catch (Throwable eee) {
                                    eee.printStackTrace();
                                }
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    isFailed = true;
                }
            }
        }
        if (!pageOrderObj.isEmpty()) {
            return book; //FIXME: no need to change
        } else {
            List<FastFile> files = bookDir.listFilesParent();
            if (files != null) {
                for (FastFile file : files) {
                    try {
                        Matcher res = pageNamePat.matcher(file.getName());
                        if (res.matches()) {
                            //FIXME:java.lang.IllegalStateException: No successful match so far
                            int pageIdx = Integer.parseInt(res.group(1));
                            pageMap.put(pageIdx, file);
                        }
                    } catch (Throwable eee) {
                        eee.printStackTrace();
                    }
                }
            }
        }

        //val lastPageIdx = if(pageMap.isEmpty()) 0 else pageMap.maxOf { it.key }
        int lastPageIdx = 0;
        if (!pageMap.isEmpty()) {
            int tempKey = 0;
            for (Integer curKey : pageMap.keySet()) {
                if (curKey != null && curKey > tempKey) {
                    tempKey = curKey;
                }
            }
            lastPageIdx = tempKey;
        }

        List<Integer> pagesTemp = new ArrayList<Integer>();
        for (int i = 0; i <= lastPageIdx; ++i) {
            pagesTemp.add(i);
        }
        List<FastFile> pages = new ArrayList<FastFile>(pagesTemp.size());
        for (Integer it : pagesTemp) {
            FastFile itemOld = pageMap.get(it);
            if (itemOld == null) {
                continue;
            }
            pages.add(itemOld);
        }
        for (int i = 0; i < pages.size(); ++i) {
            FastFile item = pages.get(i);
            if (item != null && item.getName() != null) {
                try {
                    Matcher res = pageNamePat.matcher(item.getName());
                    if (res.matches()) {
                        //FIXME:java.lang.IllegalStateException: No successful match so far
                        int pageIdx = Integer.parseInt(res.group(1));
                        if (pageIdx != i) {
                            String newName = BookPage.newPageName(i, book);
                            String oldFilePath = item.getFilePath();
                            String newFilePath = new File(
                                    new File(oldFilePath).getParent(), newName)
                                    .getAbsolutePath();
                            FastFile.copyFile(oldFilePath, newFilePath);
                            boolean r = new File(oldFilePath).delete();

                            if (BookIO.USE_META_TXT) {
                                String oldFilePath2 = oldFilePath.replace(".png", ".meta");
                                String newFilePath2 = newFilePath.replace(".png", ".meta");
                                if (!oldFilePath2.equals(oldFilePath)) {
                                    FastFile.copyFile(oldFilePath2, newFilePath2);
                                    boolean r2 = new File(oldFilePath2).delete();
                                }

                                String oldFilePath2_vecj = oldFilePath.replace(".png", ".vecj");
                                String newFilePath2_vecj = newFilePath.replace(".png", ".vecj");
                                if (!oldFilePath2_vecj.equals(oldFilePath)) {
                                    FastFile.copyFile(oldFilePath2_vecj, newFilePath2_vecj);
                                    boolean r2 = new File(oldFilePath2_vecj).delete();
                                }
                            }

                        }
                   }
                } catch (Throwable eee) {
                    eee.printStackTrace();
                }
            }
        }
        FastFile bgFile = bookDir.findFile("background.png");
        return new Book(bookDir, pages, bgFile, lastPageIndex);
    }

















    private static final String KEY_RECENT_FILES = "recentFiles";
    //private final static String APPNAME = "txkjnote";
    public void saveRecent_not_used(String value) {
        String key = "flutter." + KEY_RECENT_FILES;
        try {
            //String rootPath = new File(Environment.getExternalStorageDirectory(), APPNAME).toString();
            boolean kkk = new File(rootPath).mkdirs();
            if (true) {
                FileWriter fw = new FileWriter(new File(rootPath, key + ".txt"), false);
                fw.write(value);
                fw.flush();
                fw.close();
            }
        } catch (Throwable eee) {
            eee.printStackTrace();
        }
    }

    public void removeOrClearRecent_not_used() {
        String key = "flutter." + KEY_RECENT_FILES;
        try {
            //String rootPath = new File(Environment.getExternalStorageDirectory(), APPNAME).toString();
            boolean kkk = new File(rootPath).mkdirs();
            if (true) {
                FileWriter fw = new FileWriter(new File(rootPath, key + ".txt"), false);
                fw.write("");
                fw.flush();
                fw.close();
            }
        } catch (Throwable eee) {
            eee.printStackTrace();
        }
    }

    public List<SimpleFileMeta> loadRecent_not_used() {
        String recentFiles = "";
        if (true) {
            try {
                //String rootPath = new File(Environment.getExternalStorageDirectory(), APPNAME).toString();
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
        if (false) {
            Log.e(TAG, "recentFiles: " + recentFiles);
        }

        List<SimpleFileMeta> recentNoteList2 = new ArrayList<SimpleFileMeta>();
        try {
            JSONArray jsonArray = new JSONArray(recentFiles);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);
                if (item != null) {
                    String preview = item.optString("preview");
                    String name = item.optString("name");
                    String path = item.optString("path");
                    String createTime = item.optString("createTime");
                    String updateTime = item.optString("updateTime");
                    String dispName = item.optString("dispName");

                    SimpleFileMeta meta = new SimpleFileMeta();
                    meta.setPreview(preview);
                    meta.setName(name);
                    meta.setPath(path);
                    meta.setCreateTime(createTime);
                    meta.setUpdateTime(updateTime);
                    meta.setDispName(dispName);
                    recentNoteList2.add(meta);
                }
            }
        } catch (Throwable eee) {
            eee.printStackTrace();
        }
        return recentNoteList2;
    }

    public static boolean deleteFolder(String sPath) {
        try {
            boolean flag = false;
            File file = new File(sPath);
            if (!file.exists()) {
                return flag;
            } else {
                if (file.isFile()) {
                    return deleteFile(sPath);
                } else {
                    return deleteDirectory(sPath);
                }
            }
        } catch (Throwable eee) {
            eee.printStackTrace();
        }
        return true;
    }

    public static boolean deleteDirectory(String sPath) {
        if (!sPath.endsWith(File.separator)) {
            sPath = sPath + File.separator;
        }
        File dirFile = new File(sPath);
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        boolean flag = true;
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag) break;
            }
            else {
                flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag) break;
            }
        }
        if (!flag) return false;
        if (dirFile.delete()) {
	        return true;
	    } else {
	        return false;
	    }
    }

    public static boolean deleteFile(String sPath) {
        boolean flag = false;
        File file = new File(sPath);
        if (file.isFile() && file.exists()) {
            file.delete();
            flag = true;
        }
        return flag;
    }

    public List<SimpleFileMeta> loadRecent_new(Activity act) {
        List<SimpleFileMeta> recentNoteList2 = new ArrayList<>();
        String dirPath = new File(Environment.getExternalStorageDirectory(), NoteFragment4.APPNAME_NEW).toString();
        boolean kkk2 = new File(dirPath).mkdirs();
        if (new File(dirPath, SDNotesDatabase.DATABASE_NAME).exists()) {
            SDNotesDatabase mDatabase = new SDNotesDatabase(act, dirPath);
            List<NoteItem> items = mDatabase.getAllItems();
            for (NoteItem itemNote : items) {
                try {
                    LinkedJSONObject item = new LinkedJSONObject(itemNote.getNoteContent());
                    if (item != null) {
                        String preview = item.optString("preview");
                        String name = item.optString("name");
                        String path = item.optString("path");
                        String createTime = item.optString("createTime");
                        String updateTime = item.optString("updateTime");
                        String dispName = item.optString("dispName");

                        SimpleFileMeta meta = new SimpleFileMeta();
                        meta.setPreview(preview);
                        meta.setName(name);
                        meta.setPath(path);
                        meta.setCreateTime(createTime);
                        meta.setUpdateTime(updateTime);
                        meta.setDispName(dispName);

                        recentNoteList2.add(meta);
                    }
                } catch (Throwable eee) {
                    eee.printStackTrace();
                }
            }
        } else {
            if (NoteFragment4.LOAD_OLD_DATA) {
                String recentFiles = "";
                try {
                    String rootPath = null;
                    rootPath = new File(Environment.getExternalStorageDirectory(), NoteFragment4.APPNAME_NEW).toString();
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
                //List<FileMeta> recentNoteList2 = new ArrayList<>();
                List<NoteItem> noteItems = new ArrayList<>();
                try {
                    org.librera.JSONArray jsonArray = new org.librera.JSONArray(recentFiles);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        LinkedJSONObject item = jsonArray.getJSONObject(i);
                        if (item != null) {
                            String preview = item.optString("preview");
                            String name = item.optString("name");
                            String path = item.optString("path");
                            String createTime = item.optString("createTime");
                            String updateTime = item.optString("updateTime");
                            String dispName = item.optString("dispName");

                            NoteItem noteItem = new NoteItem();
                            noteItem.setCreateTime(createTime);
                            noteItem.setNoteFilePath(path);
                            noteItem.setUpdateTime(updateTime);
                            noteItem.setNoteName(dispName);
                            noteItem.setNoteContent(item.toString());
                            noteItems.add(noteItem);

                            SimpleFileMeta meta = new SimpleFileMeta();
                            meta.setPreview(preview);
                            meta.setName(name);
                            meta.setPath(path);
                            meta.setCreateTime(createTime);
                            meta.setUpdateTime(updateTime);
                            meta.setDispName(dispName);

                            recentNoteList2.add(meta);
                        }
                    }
                } catch (Throwable eee) {
                    eee.printStackTrace();
                }


                //reimport
                try {
                    SDNotesDatabase mDatabase = new SDNotesDatabase(act, dirPath);
                    for (NoteItem item : noteItems) {
                        mDatabase.addNote(item);
                    }
                } catch (Throwable eee) {
                    eee.printStackTrace();
                }
            }
        }
        return recentNoteList2;
    }
}
