// MainActivity.java
package com.k2fsa.sherpa.onnx;

import android.app.Activity;
import android.content.Context;
import android.media.AudioRecord;
import android.os.Bundle;
import android.text.method.MovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.txkj.drawingapp.R;

import kotlin.Metadata;
import kotlin.Unit;
import kotlin.concurrent.ThreadsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Ref;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

//(x) from sherpa-onnx-v1.12.9-android.tar.bz2
//from sherpa-onnx-1.12.33.zip
//from sherpa-onnx-v1.12.33-android.tar.bz2
//(x) from sherpa-onnx-v1.12.33-android-static-link-onnxruntime.tar.bz2
//from sherpa-onnx-1.12.33-arm64-v8a-asr-en-small_zipformer_20M_2023_02_17.apk
//from sherpa-onnx-1.12.33-arm64-v8a-asr-en-zipformer_kroko_asr.apk
//https://github.com/k2-fsa/sherpa-onnx/releases/tag/v1.12.33
//https://github.com/k2-fsa/sherpa-onnx/releases/tag/v1.12.9
//
//https://huggingface.co/csukuangfj2/sherpa-onnx-apk/resolve/main/asr/1.12.33/sherpa-onnx-1.12.33-arm64-v8a-asr-en-zipformer_kroko_asr.apk，
// I found that the model with the word kroko_asr supports punctuation marks, but the recognition speed will slow down.
// I may also add this model to the engine's options
//from https://k2-fsa.github.io/sherpa/onnx/android/apk.html
//
//see initModel(), type = 21 or 10
public final class MainActivity extends AppCompatActivity {
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

    public MainActivity() {
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

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main_sherpa_onnx);
        ActivityCompat.requestPermissions((Activity)this, this.permissions, 200);
        Log.i("sherpa-onnx", "Start to initialize model");
        this.initModel();
        Log.i("sherpa-onnx", "Finished initializing model");
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
    }

    private final void onclick() {
        if (!this.isRecording) {
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
                    MainActivity.this.processSamples();
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
        int type = 21; // 10 // 0;
        String ruleFsts = null;
        String var6 = null;
        boolean useHr = false;
        new HomophoneReplacerConfig((String)null, "lexicon.txt", "replace.fst", 1, (DefaultConstructorMarker)null);
        Log.i("sherpa-onnx", "Select model type " + type);
        FeatureConfig var10002 = FeatureConfigKt.getFeatureConfig(this.sampleRateInHz, 80);
        OnlineModelConfig var10003 = OnlineRecognizerKt.getModelConfig(type);
        Intrinsics.checkNotNull(var10003);
        OnlineRecognizerConfig config = new OnlineRecognizerConfig(var10002, var10003, (OnlineLMConfig)null, (OnlineCtcFstDecoderConfig)null, (HomophoneReplacerConfig)null, OnlineRecognizerKt.getEndpointConfig(), true, (String)null, 0, (String)null, 0.0F, (String)null, (String)null, 0.0F, 16284, (DefaultConstructorMarker)null);
        this.recognizer = new OnlineRecognizer(this.getApplication().getAssets(), config);
    }
}

