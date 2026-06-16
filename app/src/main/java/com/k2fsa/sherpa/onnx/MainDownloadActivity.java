// MainActivity.java
package com.k2fsa.sherpa.onnx;

import android.app.Activity;
import android.content.Context;
import android.media.AudioRecord;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.MovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.documentfile.provider.DocumentFile;

import com.example.sherpaasr.data.ModelRepository;
import com.k2fsa.sherpa.onnx.speaker.diarization.SpeakerDiarizationObject;
import com.k2fsa.sherpa.onnx.speaker.diarization.screens.ReadWaveFileKt;
import com.txkj.drawingapp.R;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Ref;
import kotlin.text.StringsKt;

//(x) from sherpa-onnx-v1.12.9-android.tar.bz2
//from sherpa-onnx-1.12.33.zip
//from sherpa-onnx-v1.12.33-android.tar.bz2
//(x) from sherpa-onnx-v1.12.33-android-static-link-onnxruntime.tar.bz2
//from sherpa-onnx-1.12.33-arm64-v8a-asr-en-small_zipformer_20M_2023_02_17.apk
//from sherpa-onnx-1.12.33-arm64-v8a-asr-en-zipformer_kroko_asr.apk
//https://github.com/k2-fsa/sherpa-onnx/releases/tag/v1.12.33
//https://github.com/k2-fsa/sherpa-onnx/releases/tag/v1.12.9
//
//https://huggingface.co/csukuangfj2/sherpa-onnx-apk/resolve/main/asr/1.12.33/sherpa-onnx-1.12.33-arm64-v8a-asr-en-zipformer_kroko_asr.apk
// I found that the model with the word kroko_asr supports punctuation marks, but the recognition speed will slow down.
// I may also add this model to the engine's options
//from https://k2-fsa.github.io/sherpa/onnx/android/apk.html
//
//see initModel(), type = 21 or 10
public final class MainDownloadActivity extends AppCompatActivity {
    private final static String TAG = "MainActivity";

    @NotNull
    private final String[] permissions;
    private OnlineRecognizer recognizer;
    @Nullable
    private AudioRecord audioRecord;
    private Button recordButton;
    private TextView textView;
    @Nullable
    private Thread recordingThread;
    private final int audioSource;
    private final int sampleRateInHz;
    private final int channelConfig;
    private final int audioFormat;
    private int idx;
    @NotNull
    private String lastText;
    private volatile boolean isRecording;

    public MainDownloadActivity() {
        String[] var1 = new String[]{"android.permission.RECORD_AUDIO"};
        this.permissions = var1;
        this.audioSource = 1;
        this.sampleRateInHz = 16000;
        this.channelConfig = 16;
        this.audioFormat = 2;
        this.lastText = "";
    }

    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
//        Intrinsics.checkNotNullParameter(permissions, "permissions");
//        Intrinsics.checkNotNullParameter(grantResults, "grantResults");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean permissionToRecordAccepted = requestCode == 200 ? grantResults[0] == 0 : false;
        if (!permissionToRecordAccepted) {
            Log.e("sherpa-onnx", "Audio record is disallowed");
            this.finish();
        }

        Log.i("sherpa-onnx", "Audio record is permitted");
    }

    String progress = "";
    boolean done = false;
    boolean fileIsOk = false;
    float[] samples = null;
    String status;
    boolean started = false;
    int numSpeakers = 0;
    float threshold = 0.5f;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main_download_test);
        ActivityCompat.requestPermissions((Activity)this, this.permissions, 200);

        View var10001 = this.findViewById(R.id.record_button);
        Intrinsics.checkNotNullExpressionValue(var10001, "findViewById(R.id.record_button)");
        this.recordButton = (Button)var10001;
        Button var10000 = this.recordButton;
        if (var10000 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("recordButton");
            var10000 = null;
        }

        var10000.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onclick();
            }
        });
        var10001 = this.findViewById(R.id.my_text);
        Intrinsics.checkNotNullExpressionValue(var10001, "findViewById(R.id.my_text)");
        this.textView = (TextView)var10001;
        TextView var2 = this.textView;
        if (var2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("textView");
            var2 = null;
        }

        var2.setMovementMethod((MovementMethod)(new ScrollingMovementMethod()));


        Context a_context = MainDownloadActivity.this.getApplicationContext();
        ActivityResultLauncher<String[]> launcher = this.registerForActivityResult(new ActivityResultContracts.OpenDocument(),
                new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri o) {
                DocumentFile documentFile = DocumentFile.fromSingleUri(a_context, o);
                String filename = "";
                if (documentFile != null) {
                    filename = documentFile.getName();
                }
                if (!filename.isEmpty()) {
                    com.k2fsa.sherpa.onnx.speaker.diarization.screens.WaveData data
                            = ReadWaveFileKt.readUri(a_context, o);
                    Log.i(TAG, "sample rate: " + data.getSampleRate());
                    Log.i(TAG, "numSamples: " +
                            (data.getSamples() != null ? data.getSamples().length : 0));
                    if (data.getMsg() != null) {
                        Log.i(TAG, "failed to read $filename");
                        status = data.getMsg();
                    } else if (data.getSampleRate() != SpeakerDiarizationObject.INSTANCE.getSd().sampleRate()) {
                        status = "Expected sample rate: ${SpeakerDiarizationObject.sd.sampleRate()}. Given wave file with sample rate: ${data.sampleRate}";
                    } else {
                        samples = data.getSamples();
                    }
                }
            }
        });
        Button select_file = (Button)findViewById(R.id.select_file);
        Button start_diarization = (Button)findViewById(R.id.start_diarization);
        select_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (false) {
                    SpeakerDiarizationObject.INSTANCE.initSpeakerDiarization(MainDownloadActivity.this.getAssets());
                    launcher.launch(new String[] {"audio/*"});
                } else {
                    loadWavFile();
                }
            }
        });
        start_diarization.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "started");
                Log.i(TAG, "num samples: " + (samples != null ? samples.length : 0));
                started = true;
                progress = "";

                OfflineSpeakerDiarizationConfig config = SpeakerDiarizationObject.INSTANCE.getSd().getConfig();
                config.getClustering().setNumClusters(numSpeakers);
                config.getClustering().setThreshold(threshold);

                SpeakerDiarizationObject.INSTANCE.getSd().setConfig(config);

                (new Thread() {
                    @Override
                    public void run() {
                        done = false;
                        status = "Started! Please wait";
                        OfflineSpeakerDiarizationSegment[] segments =
                                SpeakerDiarizationObject.INSTANCE.getSd().processWithCallback(
                                        samples, myCallback, 0
                                );
                        done = true;
                        started = false;
                        status = "";
                        Log.i(TAG, "segments.length == " + segments.length);
                        for (OfflineSpeakerDiarizationSegment s : segments) {
                            String start = String.format("%.2f", s.getStart());
                            String end = String.format("%.2f", s.getEnd());
                            String speaker = String.format("speaker_%02d", s.getSpeaker());
                            status += "" + start + " -- " + end + " " + speaker + "\n";
                            Log.i(TAG, "" + start + " -- " + end + " " + speaker);
                        }
                        Log.i(TAG, status);
                    }
                }).start();
            }
        });
    }

    //must be static class
    public class MyCallback implements Function3<Integer, Integer, Long, Integer> {
        //JNI DETECTED ERROR IN APPLICATION: JNI GetObjectClass called with pending
        // exception java.lang.NoSuchMethodError:
        // no non-static method "Lcom/k2fsa/sherpa/onnx/MainActivity$MyCallback;.invoke(IIJ)Ljava/lang/Integer;"
        //must keep 2 invoke() functions here, I don't know why
        //TODO:don't remove this function!!!!!
        public Integer invoke(int numProcessedChunks, int numTotalChunks, long arg) {
            double percent = 100.0 * numProcessedChunks / numTotalChunks;
            String progress = String.format("%.2f%%", percent);
            Log.i(TAG, progress);
            return 0;
        }

        @Override
        public Integer invoke(Integer numProcessedChunks, Integer numTotalChunks, Long arg) {
            double percent = 100.0 * numProcessedChunks / numTotalChunks;
            String progress = String.format("%.2f%%", percent);
            Log.i(TAG, progress);
            return 0;
        }
    }
    public MyCallback myCallback = new MyCallback();

    private final void onclick() {
        if (!this.isRecording) {
            Log.i("sherpa-onnx", "Start to initialize model");
            if (false) {
                ModelRepository repository = new ModelRepository(this, true);
                String modelFileName = repository.getModels().get(0).getFileName();

                File parent = null;
                if (ModelRepository.USE_EXTERNAL_FILES) {
                    parent = getExternalFilesDir(null);
                } else {
                    parent = getFilesDir();
                }
                File modelArchive = new File(parent,
                        ModelRepository.MODELS_FOLDER_NAME + File.separator + modelFileName);
                File modelDir = new File(parent,
                        ModelRepository.MODELS_FOLDER_NAME + File.separator + modelFileName.replace(".tar.bz2", ""));
                if (!modelDir.exists()) {
                    try {
                        extractTarBz2(modelArchive, modelDir.getParentFile());
                    } catch (IOException e) {
                        return;
                    }
                }
            }
            this.initModel();
            Log.i("sherpa-onnx", "Finished initializing model");


            boolean ret = this.initMicrophone();
            if (!ret) {
                Log.e("sherpa-onnx", "Failed to initialize microphone");
                return;
            }

            StringBuilder var10001 = (new StringBuilder()).append("state: ");
            AudioRecord var10002 = this.audioRecord;
            Log.i("sherpa-onnx", var10001.append(var10002 != null ? var10002.getState() : null).toString());
            AudioRecord var10000 = this.audioRecord;
            Intrinsics.checkNotNull(var10000);
            var10000.startRecording();
            Button var2 = this.recordButton;
            if (var2 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("recordButton");
                var2 = null;
            }

            var2.setText("stop");
            this.isRecording = true;
            TextView var3 = this.textView;
            if (var3 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("textView");
                var3 = null;
            }

            var3.setText((CharSequence)"");
            this.lastText = "";
            this.idx = 0;
            this.recordingThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    MainDownloadActivity.this.processSamples();
                }
            });
            recordingThread.start();
            Log.i("sherpa-onnx", "Started recording");
        } else {
            this.isRecording = false;
            AudioRecord var4 = this.audioRecord;
            Intrinsics.checkNotNull(var4);
            var4.stop();
            var4 = this.audioRecord;
            Intrinsics.checkNotNull(var4);
            var4.release();
            this.audioRecord = null;
            Button var6 = this.recordButton;
            if (var6 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("recordButton");
                var6 = null;
            }

            var6.setText("start");
            Log.i("sherpa-onnx", "Stopped recording");
        }

    }

    private void loadWavFile() {
        SpeakerDiarizationObject.INSTANCE.initSpeakerDiarization(this.getAssets());

        Context a_context = MainDownloadActivity.this.getApplicationContext();
        File pcmFile = new File(MainDownloadActivity.this.getExternalFilesDir(null), "audio_record.pcm");
        File wavFile = new File(MainDownloadActivity.this.getExternalFilesDir(null), "audio_record.wav");
        String filename = wavFile.getAbsolutePath();
        Uri o = Uri.fromFile(new File(filename));
        if (!filename.isEmpty()) {
            com.k2fsa.sherpa.onnx.speaker.diarization.screens.WaveData data
                    = ReadWaveFileKt.readUri(a_context, o);
            Log.i(TAG, "sample rate: " + data.getSampleRate());
            Log.i(TAG, "numSamples: " +
                    (data.getSamples() != null ? data.getSamples().length : 0));
            if (data.getMsg() != null) {
                Log.i(TAG, "failed to read " + filename);
                status = data.getMsg();
                Toast.makeText(MainDownloadActivity.this,
                        status,
                        Toast.LENGTH_LONG).show();
            } else if (data.getSampleRate() != SpeakerDiarizationObject.INSTANCE.getSd().sampleRate()) {
                status = "Expected sample rate: " + SpeakerDiarizationObject.INSTANCE.getSd().sampleRate() +
                        ". Given wave file with sample rate: " + data.getSampleRate();
                Toast.makeText(MainDownloadActivity.this,
                        status,
                        Toast.LENGTH_LONG).show();
            } else {
                samples = data.getSamples();
                Toast.makeText(MainDownloadActivity.this,
                        "load " + filename + " success!",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private final void processSamples() {
        Log.i("sherpa-onnx", "processing samples");
        OnlineRecognizer var10000 = this.recognizer;
        if (var10000 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("recognizer");
            var10000 = null;
        }

        OnlineStream stream = OnlineRecognizer.createStream$default(var10000, (String)null, 1, (Object)null);
        double interval = 0.1;
        int bufferSize = (int)(interval * (double)this.sampleRateInHz);
        short[] buffer = new short[bufferSize];

        while(this.isRecording) {
            AudioRecord var16 = this.audioRecord;
            Integer ret = var16 != null ? var16.read(buffer, 0, buffer.length) : null;
            if (ret != null && ret > 0) {
                int isEndpoint_i = 0;
                int var9 = ret;

                float[] tailPaddings;
                for(tailPaddings = new float[var9]; isEndpoint_i < var9; ++isEndpoint_i) {
                    tailPaddings[isEndpoint_i] = (float)buffer[isEndpoint_i] / 32768.0F;
                }

                stream.acceptWaveform(tailPaddings, this.sampleRateInHz);

                while(true) {
                    OnlineRecognizer var17 = this.recognizer;
                    if (var17 == null) {
                        Intrinsics.throwUninitializedPropertyAccessException("recognizer");
                        var17 = null;
                    }

                    if (!var17.isReady(stream)) {
                        var17 = this.recognizer;
                        if (var17 == null) {
                            Intrinsics.throwUninitializedPropertyAccessException("recognizer");
                            var17 = null;
                        }

                        boolean isEndpoint_ = var17.isEndpoint(stream);
                        var17 = this.recognizer;
                        if (var17 == null) {
                            Intrinsics.throwUninitializedPropertyAccessException("recognizer");
                            var17 = null;
                        }

                        String text = var17.getResult(stream).getText();
                        float[] timestamps = var17.getResult(stream).getTimestamps();
                        if (isEndpoint_) {
                            var17 = this.recognizer;
                            if (var17 == null) {
                                Intrinsics.throwUninitializedPropertyAccessException("recognizer");
                                var17 = null;
                            }

                            if (!StringsKt.isBlank((CharSequence)var17.getConfig().getModelConfig().getParaformer().getEncoder())) {
                                tailPaddings = new float[(int)(0.8 * (double)this.sampleRateInHz)];
                                stream.acceptWaveform(tailPaddings, this.sampleRateInHz);

                                while(true) {
                                    var17 = this.recognizer;
                                    if (var17 == null) {
                                        Intrinsics.throwUninitializedPropertyAccessException("recognizer");
                                        var17 = null;
                                    }

                                    if (!var17.isReady(stream)) {
                                        var17 = this.recognizer;
                                        if (var17 == null) {
                                            Intrinsics.throwUninitializedPropertyAccessException("recognizer");
                                            var17 = null;
                                        }

                                        text = var17.getResult(stream).getText();
                                        timestamps = var17.getResult(stream).getTimestamps();
                                        break;
                                    }

                                    var17 = this.recognizer;
                                    if (var17 == null) {
                                        Intrinsics.throwUninitializedPropertyAccessException("recognizer");
                                        var17 = null;
                                    }

                                    var17.decode(stream);
                                }
                            }
                        }

                        Ref.ObjectRef textToDisplay = new Ref.ObjectRef();
                        textToDisplay.element = this.lastText;
                        if (!StringsKt.isBlank((CharSequence)text)) {
                            textToDisplay.element = StringsKt.isBlank((CharSequence)this.lastText) ? this.idx + ": " + text : this.lastText + '\n' + this.idx + ": " + text;
                        }

                        if (isEndpoint_) {
                            var17 = this.recognizer;
                            if (var17 == null) {
                                Intrinsics.throwUninitializedPropertyAccessException("recognizer");
                                var17 = null;
                            }

                            var17.reset(stream);
                            if (!StringsKt.isBlank((CharSequence)text)) {
                                this.lastText = this.lastText + '\n' + this.idx + ": " + text;
                                textToDisplay.element = this.lastText;
                                ++this.idx;
                            }
                        }

                        this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                               TextView var10000 = textView;
                                if (var10000 == null) {
                                    Intrinsics.throwUninitializedPropertyAccessException("textView");
                                    var10000 = null;
                                }
                                var10000.setText((CharSequence)textToDisplay.element);
                            }
                        });
                        break;
                    }

                    var17 = this.recognizer;
                    if (var17 == null) {
                        Intrinsics.throwUninitializedPropertyAccessException("recognizer");
                        var17 = null;
                    }

                    var17.decode(stream);
                }
            }
        }

        stream.release();
    }

    private final boolean initMicrophone() {
        if (ActivityCompat.checkSelfPermission((Context)this, "android.permission.RECORD_AUDIO") != 0) {
            ActivityCompat.requestPermissions((Activity)this, this.permissions, 200);
            return false;
        } else {
            int numBytes = AudioRecord.getMinBufferSize(this.sampleRateInHz, this.channelConfig, this.audioFormat);
            Log.i("sherpa-onnx", "buffer size in milliseconds: " + (float)numBytes * 1000.0F / (float)this.sampleRateInHz);
            this.audioRecord = new AudioRecord(this.audioSource, this.sampleRateInHz, this.channelConfig, this.audioFormat, numBytes * 2);
            return true;
        }
    }

    private final void initModel() {
        int folderType = 1; //1:from downloaded file; 0:from assets
        int type = 21; // 10 // 0;
        String ruleFsts = null;
        String var6 = null;
        boolean useHr = false;
        new HomophoneReplacerConfig((String)null, "lexicon.txt", "replace.fst", 1, (DefaultConstructorMarker)null);
        Log.i("sherpa-onnx", "Select model type " + type);
        FeatureConfig var10002 = FeatureConfigKt.getFeatureConfig(this.sampleRateInHz, 80);
        OnlineModelConfig var10003 = OnlineRecognizerKt.getModelConfig(type, folderType, this);
        Intrinsics.checkNotNull(var10003);
        OnlineRecognizerConfig config = new OnlineRecognizerConfig(var10002, var10003, (OnlineLMConfig)null, (OnlineCtcFstDecoderConfig)null, (HomophoneReplacerConfig)null, OnlineRecognizerKt.getEndpointConfig(), true, (String)null, 0, (String)null, 0.0F, (String)null, (String)null, 0.0F, 16284, (DefaultConstructorMarker)null);
        //TODO: be carful here: if load downloaded file, assetManager MUST BE set to null
        this.recognizer = new OnlineRecognizer(folderType == 1 ? null : this.getApplication().getAssets(), config);
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

