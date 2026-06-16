package com.example.sherpaasr.utils;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import com.example.sherpaasr.data.ModelItem;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DownloadManager {
    //
    //StorageService.unpack(mAct, "vendor/assets/model-en-us", "model",...)
    // vosk-android-0.3.75-sources.jar!\
    // org\vosk\android\StorageService.java
    //File externalFilesDir = context.getExternalFilesDir(null);
    //

    public interface Callback {
        void onProgress(ModelItem item, int percent);
        void onComplete(ModelItem item, boolean success, String message);
    }

    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final ConcurrentHashMap<String, DownloadTask> tasks = new ConcurrentHashMap<>();

    public void download(ModelItem item, File destFile, Callback callback) {
        if (tasks.containsKey(item.getFileName())) return;
        DownloadTask task = new DownloadTask(item, destFile, callback);
        tasks.put(item.getFileName(), task);
        executor.execute(task);
    }

    public void pause(ModelItem item) {
        DownloadTask task = tasks.get(item.getFileName());
        if (task != null) task.pause();
    }

    public void resume(ModelItem item, File destFile, Callback callback) {
        DownloadTask task = tasks.get(item.getFileName());
        if (task != null) task.resume();
        else download(item, destFile, callback);
    }

    public void cancel(ModelItem item) {
        DownloadTask task = tasks.remove(item.getFileName());
        if (task != null) task.cancel();
    }
    public void cancelAll() {
        try {
            List<String> names = new ArrayList<>();
            for (String name : tasks.keySet()) {
                names.add(name);
            }
            for (String name : names) {
                DownloadTask task = tasks.remove(name);
                if (task != null) {
                    task.cancel();
                }
            }
        } catch (Throwable eee) {
            eee.printStackTrace();
        }
    }


    private class DownloadTask implements Runnable {
        private final ModelItem item;
        private final File destFile;
        private final Callback callback;
        private volatile boolean paused = false;
        private volatile boolean cancelled = false;

        DownloadTask(ModelItem item, File destFile, Callback callback) {
            this.item = item;
            this.destFile = destFile;
            this.callback = callback;
        }

        void pause() { paused = true; item.setPaused(true); }
        void resume() { paused = false; item.setPaused(false); synchronized(this){ notify(); } }
        void cancel() { cancelled = true; }

        @Override
        public void run() {
            HttpURLConnection conn = null;
            try {
                long existing = destFile.exists() ? destFile.length() : 0;
                URL url = new URL(item.getUrl());
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Range", "bytes=" + existing + "-");
                conn.setConnectTimeout(15000);
                conn.setReadTimeout(15000);
                int response = conn.getResponseCode();
                boolean supportsRange = (response == HttpURLConnection.HTTP_PARTIAL);
                long total = 0;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    total = conn.getContentLengthLong() + existing;
                }
                if (!supportsRange) {
                    existing = 0;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        total = conn.getContentLengthLong();
                    }
                }

                InputStream in = conn.getInputStream();
                FileOutputStream out = new FileOutputStream(destFile, supportsRange);
                byte[] buffer = new byte[8192];
                long downloaded = existing;
                int read;
                while (!cancelled && (read = in.read(buffer)) != -1) {
                    if (paused) {
                        synchronized (this) {
                            while (paused && !cancelled) wait();
                        }
                    }
                    out.write(buffer, 0, read);
                    downloaded += read;
                    int percent = total > 0 ? (int) (downloaded * 100 / total) : 0;
                    item.setProgress(percent);
                    mainHandler.post(() -> callback.onProgress(item, percent));
                }
                out.close();
                in.close();

                item.setStatusExtract(true);
                mainHandler.post(() -> callback.onProgress(item, 100));
                String modelFileName = destFile.getName();

                File modelArchive = destFile; //File(getFilesDir(), "models/" + modelFileName)
                File modelDir = //"models/"
                        new File(modelArchive.getParent(), "" + modelFileName.replace(".tar.bz2", ""));
                if (!modelDir.exists()) {
                    try {
                        extractTarBz2(modelArchive, modelDir.getParentFile());
                        modelArchive.delete();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                item.setStatusExtract(false);
                mainHandler.post(() -> callback.onProgress(item, 100));

                tasks.remove(item.getFileName());
                boolean ok = !cancelled;
                if (ok) item.setStatus(ModelItem.Status.DOWNLOADED);
                mainHandler.post(() -> callback.onComplete(item, ok, ok ? "Download done" : "Download cancelled"));
            } catch (Exception e) {
                tasks.remove(item.getFileName());
                mainHandler.post(() -> callback.onComplete(item, false, e.getMessage()));
            } finally {
                if (conn != null) conn.disconnect();
            }
        }
    }

    private void extractTarBz2(File archive, File destDir) throws IOException {
        try (TarArchiveInputStream tarIn = new TarArchiveInputStream(
                new BZip2CompressorInputStream(new BufferedInputStream(new FileInputStream(archive))))) {
            TarArchiveEntry entry;
            while ((entry = tarIn.getNextTarEntry()) != null) {
                File outFile = new File(destDir, entry.getName());
                if (entry.isDirectory()) {
                    outFile.mkdirs();
                } else {
                    outFile.getParentFile().mkdirs();
                    try (FileOutputStream fos = new FileOutputStream(outFile)) {
                        byte[] buf = new byte[8192];
                        int n;
                        while ((n = tarIn.read(buf)) != -1) fos.write(buf, 0, n);
                    }
                }
            }
        }
    }
}
