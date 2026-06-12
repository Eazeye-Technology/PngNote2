package com.example.sherpaasr.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.sherpaasr.data.ModelRepository;
import com.example.sherpaasr.ui.MainActivity;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TranscriptionService extends Service {

    public static final String EXTRA_MODEL_PATH = "model_path";
    private static final String CHANNEL_ID = "sherpa_asr_channel";
    private static final int NOTIF_ID = 1;

    private final IBinder binder = new LocalBinder();
    private ExecutorService executor;
    private AudioRecord audioRecord;
    private volatile boolean isRecording = false;
    private ResultCallback callback;

    // TODO: Sherpa-ONNX
    // private OnlineRecognizer recognizer;

    public interface ResultCallback {
        void onResult(String text);
    }

    public class LocalBinder extends Binder {
        public TranscriptionService getService() {
            return TranscriptionService.this;
        }
    }

    public void setCallback(ResultCallback callback) {
        this.callback = callback;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String modelFileName = intent != null ? intent.getStringExtra(EXTRA_MODEL_PATH) : null;
        startForegroundService();
        executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> startTranscription(modelFileName));
        return START_NOT_STICKY;
    }

    private void startForegroundService() {
        createNotificationChannel();
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("SherpaASR")
                .setContentText("Transcription...")
                .setSmallIcon(android.R.drawable.ic_btn_speak_now)
                .setContentIntent(pendingIntent)
                .build();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(NOTIF_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE);
        } else {
            startForeground(NOTIF_ID, notification);
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "ASR Service", NotificationManager.IMPORTANCE_LOW);
            getSystemService(NotificationManager.class).createNotificationChannel(channel);
        }
    }

    @SuppressLint("MissingPermission")
    private void startTranscription(String modelFileName) {
        File parent = ModelRepository.USE_EXTERNAL_FILES ? getExternalFilesDir(null) : getFilesDir();
        File modelArchive = new File(parent, ModelRepository.MODELS_FOLDER_NAME + File.separator + modelFileName);
        File modelDir = new File(parent, ModelRepository.MODELS_FOLDER_NAME + File.separator + modelFileName.replace(".tar.bz2", ""));
        if (!modelDir.exists()) {
            try {
                extractTarBz2(modelArchive, modelDir.getParentFile());
            } catch (IOException e) {
                if (callback != null) callback.onResult("[Model extract failed]");
                stopSelf();
                return;
            }
        }

        // TODO: 使用 modelDir 中的模型文件初始化 Sherpa-ONNX OnlineRecognizer
        // 示例伪代码：
        // OnlineRecognizerConfig config = new OnlineRecognizerConfig();
        // config.setModelDir(modelDir.getAbsolutePath());
        // recognizer = new OnlineRecognizer(config);

        int sampleRate = 16000;
        int bufferSize = AudioRecord.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize);
        if (audioRecord.getState() != AudioRecord.STATE_INITIALIZED) {
            if (callback != null) callback.onResult("[Record init failed]");
            stopSelf();
            return;
        }
        audioRecord.startRecording();
        isRecording = true;
        short[] buffer = new short[bufferSize / 2];

        while (isRecording) {
            int read = audioRecord.read(buffer, 0, buffer.length);
            if (read > 0) {
                // TODO: Put buffer into Sherpa-ONNX recognition
                // String text = recognizer.acceptWaveform(buffer);
                // if (text != null && !text.isEmpty() && callback != null) callback.onResult(text);

                // TODO: emulate output
                if (callback != null) callback.onResult(".");
            }
        }
        audioRecord.stop();
        audioRecord.release();
        // if (recognizer != null) recognizer.release();
    }

    public void stopTranscription() {
        isRecording = false;
        if (executor != null) executor.shutdownNow();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopTranscription();
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
