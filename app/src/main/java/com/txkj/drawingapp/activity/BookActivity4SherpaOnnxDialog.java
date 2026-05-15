package com.txkj.drawingapp.activity;

import android.Manifest;
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

import androidx.core.app.ActivityCompat;

import com.hjq.permissions.OnPermission;
import com.hjq.permissions.XXPermissions;
import com.k2fsa.sherpa.onnx.FeatureConfig;
import com.k2fsa.sherpa.onnx.FeatureConfigKt;
import com.k2fsa.sherpa.onnx.HomophoneReplacerConfig;
import com.k2fsa.sherpa.onnx.MainActivity;
import com.k2fsa.sherpa.onnx.OnlineCtcFstDecoderConfig;
import com.k2fsa.sherpa.onnx.OnlineLMConfig;
import com.k2fsa.sherpa.onnx.OnlineModelConfig;
import com.k2fsa.sherpa.onnx.OnlineRecognizer;
import com.k2fsa.sherpa.onnx.OnlineRecognizerConfig;
import com.k2fsa.sherpa.onnx.OnlineRecognizerKt;
import com.k2fsa.sherpa.onnx.OnlineStream;
import com.txkj.drawingapp.R;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Ref;
import kotlin.text.StringsKt;

public class BookActivity4SherpaOnnxDialog {
    private static final String TAG = "sherpa-onnx";

    private void textView_setText(String text) {

    }


    @NotNull
    private final String[] permissions = new String[]{"android.permission.RECORD_AUDIO"};
    private OnlineRecognizer recognizer;
    @Nullable
    private AudioRecord audioRecord;
//    private Button recordButton;
//    private TextView textView;
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

    private int mModelType; //21; // 10 // 0;
    private Activity mAct;
    public BookActivity4SherpaOnnxDialog(Activity act, int modelType) {
        this.mAct = act;
        this.mModelType = modelType;
//        String[] var1 = new String[]{"android.permission.RECORD_AUDIO"};
//        this.permissions = var1;
        this.audioSource = 1;
        this.sampleRateInHz = 16000;
        this.channelConfig = 16;
        this.audioFormat = 2;
        this.lastText = "";
        onCreate();
    }

//    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
////        Intrinsics.checkNotNullParameter(permissions, "permissions");
////        Intrinsics.checkNotNullParameter(grantResults, "grantResults");
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        boolean permissionToRecordAccepted = requestCode == 200 ? grantResults[0] == 0 : false;
//        if (!permissionToRecordAccepted) {
//            Log.e(TAG, "Audio record is disallowed");
//            this.finish();
//        }
//
//        Log.i(TAG, "Audio record is permitted");
//    }

    protected void onCreate() {
        if (false) {
//        super.onCreate(savedInstanceState);
//        this.setContentView(R.layout.activity_main_sherpa_onnx);
            ActivityCompat.requestPermissions((Activity) mAct, this.permissions, 200);

            Log.i(TAG, "Start to initialize model");
            this.initModel();
            Log.i(TAG, "Finished initializing model");
//        View var10001 = this.findViewById(R.id.record_button);
//        Intrinsics.checkNotNullExpressionValue(var10001, "findViewById(R.id.record_button)");
//        this.recordButton = (Button)var10001;
//        Button var10000 = this.recordButton;
//        if (var10000 == null) {
//            Intrinsics.throwUninitializedPropertyAccessException("recordButton");
//            var10000 = null;
//        }
//
//        var10000.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                onclick();
//            }
//        });


//        var10001 = this.findViewById(R.id.my_text);
//        Intrinsics.checkNotNullExpressionValue(var10001, "findViewById(R.id.my_text)");
//        this.textView = (TextView)var10001;
//        TextView var2 = this.textView;
//        if (var2 == null) {
//            Intrinsics.throwUninitializedPropertyAccessException("textView");
//            var2 = null;
//        }
//
//        var2.setMovementMethod((MovementMethod)(new ScrollingMovementMethod()));
        } else {
            XXPermissions.with(mAct).permission(Manifest.permission.RECORD_AUDIO).request(new OnPermission() {
                @Override
                public void hasPermission(List<String> granted, boolean all) {
                    Log.d(TAG,"Permission success:"+all);
                    for(int i=0;i<granted.size();i++){
                        Log.d(TAG,"Granted："+granted.get(i));
                    }
                    if(all) {
                        Log.i(TAG, "Start to initialize model");
                        initModel();
                        Log.i(TAG, "Finished initializing model");
                        onclick_Start();
                    } else {
                        BookActivity4Utils.btn_audio_start_setEnabled(mAct, true);
                    }
                }

                @Override
                public void noPermission(List<String> denied, boolean quick) {
                    if(quick){
                        Log.e(TAG,"onDenied:no permission, require manual");
                        XXPermissions.startPermissionActivity(mAct, denied);
                    }else{
                        Log.e(TAG,"onDenied:permission failed");
                    }
                    BookActivity4Utils.btn_audio_start_setEnabled(mAct, true);
                }
            });
        }
    }

    private final void onclick() {
        if (!this.isRecording) {
            onclick_Start();
        } else {
            onclick_Stop();
        }
    }

    private void onclick_Start() {
        boolean ret = this.initMicrophone();
        if (!ret) {
            Log.e(TAG, "Failed to initialize microphone");
            return;
        }

        StringBuilder var10001 = (new StringBuilder()).append("state: ");
        AudioRecord var10002 = this.audioRecord;
        Log.i(TAG, var10001.append(var10002 != null ? var10002.getState() : null).toString());

        AudioRecord var10000 = this.audioRecord;
        Intrinsics.checkNotNull(var10000);
        var10000.startRecording();

//            Button var2 = this.recordButton;
//            if (var2 == null) {
//                Intrinsics.throwUninitializedPropertyAccessException("recordButton");
//                var2 = null;
//            }
//            var2.setText("stop");


        this.isRecording = true;


//            TextView var3 = this.textView;
//            if (var3 == null) {
//                Intrinsics.throwUninitializedPropertyAccessException("textView");
//                var3 = null;
//            }
//            var3.setText((CharSequence)"");
        textView_setText("");
        if (false) BookActivity4Utils.tv_result_setText(mAct, "", "", true, false, null, -1);

        this.lastText = "";
        this.idx = 0;
        this.recordingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                processSamples();
            }
        });
        recordingThread.start();
        Log.i(TAG, "Started recording");
        BookActivity4Utils.btn_audio_start_setEnabled(mAct, false);
    }

    public void onclick_Stop() {
        this.isRecording = false;
        AudioRecord var4 = this.audioRecord;
        Intrinsics.checkNotNull(var4);
        var4.stop();
        var4 = this.audioRecord;
        Intrinsics.checkNotNull(var4);
        var4.release();
        this.audioRecord = null;

//            Button var6 = this.recordButton;
//            if (var6 == null) {
//                Intrinsics.throwUninitializedPropertyAccessException("recordButton");
//                var6 = null;
//            }
//            var6.setText("start");

        Log.i(TAG, "Stopped recording");

        BookActivity4Utils.btn_audio_start_setEnabled(mAct, true);
    }


    private float processTime = 0;
    private long processBytes = 0;
    private final void processSamples() {
        Log.i(TAG, "processing samples");
        OnlineRecognizer var10000 = this.recognizer;
        if (var10000 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("recognizer");
            var10000 = null;
        }

        OnlineStream stream = OnlineRecognizer.createStream$default(var10000, (String)null, 1, (Object)null);
        double interval = 0.1;
        int bufferSize = (int)(interval * (double)this.sampleRateInHz);
        short[] buffer = new short[bufferSize];

        File pcmFile = null;
        File wavFile = null;
        if (false) {
            pcmFile = new File(mAct.getExternalFilesDir(null), "audio_record.pcm");
            wavFile = new File(mAct.getExternalFilesDir(null), "audio_record.wav");
        } else {
            String dirPath = BookActivity4Utils.getDirPath(mAct);
            pcmFile = new File(dirPath, "audio_record.pcm");
            wavFile = new File(dirPath, "audio_record.wav");
        }
        Log.e(TAG, "pcmFile == " + pcmFile.getAbsolutePath());
        try (FileOutputStream fos = new FileOutputStream(pcmFile)) {
            while (this.isRecording) {
                AudioRecord var16 = this.audioRecord;
                Integer ret = var16 != null ? var16.read(buffer, 0, buffer.length) : null;
                if (ret != null && ret > 0) {
                    {
                        byte[] bytes = new byte[buffer.length * 2];
                        ByteBuffer.wrap(bytes)
                                .order(ByteOrder.LITTLE_ENDIAN)//.BIG_ENDIAN)   // 可改 LITTLE_ENDIAN
                                .asShortBuffer()
                                .put(buffer);
                        fos.write(bytes, 0, ret * 2); // 将 PCM 数据写入文件


                        processBytes += ret * 2;
                        //575488 bytes ==  575488/(16kHz*2) = 35.968/2 = 17.984sec
                        //13sec 416000==16000*2*13==13sec
                        processTime = (float)processBytes / (float)(this.sampleRateInHz * this.channelConfig / 8);
                    }

                    int isEndpoint_i = 0;
                    int var9 = ret;

                    float[] tailPaddings;
                    for (tailPaddings = new float[var9]; isEndpoint_i < var9; ++isEndpoint_i) {
                        tailPaddings[isEndpoint_i] = (float) buffer[isEndpoint_i] / 32768.0F;
                    }

                    stream.acceptWaveform(tailPaddings, this.sampleRateInHz);

                    while (true) {
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

                                if (!StringsKt.isBlank((CharSequence) var17.getConfig().getModelConfig().getParaformer().getEncoder())) {
                                    tailPaddings = new float[(int) (0.8 * (double) this.sampleRateInHz)];
                                    stream.acceptWaveform(tailPaddings, this.sampleRateInHz);

                                    while (true) {
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
                            if (!StringsKt.isBlank((CharSequence) text)) {
                                textToDisplay.element = StringsKt.isBlank((CharSequence) this.lastText) ? this.idx + ": " + text : this.lastText + '\n' + this.idx + ": " + text;
                            }

                            boolean isEndPointValue = false;
                            if (isEndpoint_) {
                                var17 = this.recognizer;
                                if (var17 == null) {
                                    Intrinsics.throwUninitializedPropertyAccessException("recognizer");
                                    var17 = null;
                                }

                                var17.reset(stream);
                                if (!StringsKt.isBlank((CharSequence) text)) {
                                    this.lastText = this.lastText + '\n' + this.idx + ": " + text;
                                    textToDisplay.element = this.lastText;
                                    ++this.idx;
                                    isEndPointValue = true;
                                }
                            }
                            final boolean isEndPointValue_ = isEndPointValue;
                            final float[] timestamps_ = timestamps;
                            final String text_ = text;
                            this.mAct.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
//                                TextView var10000 = textView;
//                                if (var10000 == null) {
//                                    Intrinsics.throwUninitializedPropertyAccessException("textView");
//                                    var10000 = null;
//                                }
//                                var10000.setText((CharSequence)textToDisplay.element);
                                    textView_setText(((CharSequence) textToDisplay.element).toString());
                                    if (text_ != null && text_.length() > 0) {
                                        //see BookReaderItemsAdapter.java
                                        BookActivity4Utils.tv_result_setText(mAct, text_, text_, isEndPointValue_, false, timestamps_, processTime);
                                    }
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
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            PcmToWavUtil pcmToWavUtil = new PcmToWavUtil(this.sampleRateInHz, this.channelConfig, this.audioFormat);
            pcmToWavUtil.pcmToWav(pcmFile.getAbsolutePath(), wavFile.getAbsolutePath());
        }
        stream.release();
    }

    private final boolean initMicrophone() {
        if (false) {
            if (ActivityCompat.checkSelfPermission(mAct, "android.permission.RECORD_AUDIO") != 0) {
                ActivityCompat.requestPermissions(mAct, this.permissions, 200);
                return false;
            } else {
                int numBytes = AudioRecord.getMinBufferSize(this.sampleRateInHz, this.channelConfig, this.audioFormat);
                Log.i(TAG, "buffer size in milliseconds: " + (float) numBytes * 1000.0F / (float) this.sampleRateInHz);
                this.audioRecord = new AudioRecord(this.audioSource, this.sampleRateInHz, this.channelConfig, this.audioFormat, numBytes * 2);
                return true;
            }
        } else {
            if (ActivityCompat.checkSelfPermission(mAct, "android.permission.RECORD_AUDIO") != 0) {
                return false;
            } else {
                int numBytes = AudioRecord.getMinBufferSize(this.sampleRateInHz, this.channelConfig, this.audioFormat);
                Log.i(TAG, "buffer size in milliseconds: " + (float) numBytes * 1000.0F / (float) this.sampleRateInHz);
                this.audioRecord = new AudioRecord(this.audioSource, this.sampleRateInHz, this.channelConfig, this.audioFormat, numBytes * 2);
                return true;
            }
        }
    }

    private final void initModel() {
        int type = mModelType; //21; // 10 // 0;
        String ruleFsts = null;
        String var6 = null;
        boolean useHr = false;
        new HomophoneReplacerConfig((String)null, "lexicon.txt", "replace.fst", 1, (DefaultConstructorMarker)null);
        Log.i(TAG, "Select model type " + type);
        FeatureConfig var10002 = FeatureConfigKt.getFeatureConfig(this.sampleRateInHz, 80);
        OnlineModelConfig var10003 = OnlineRecognizerKt.getModelConfig(type);
        Intrinsics.checkNotNull(var10003);
        OnlineRecognizerConfig config = new OnlineRecognizerConfig(var10002, var10003, (OnlineLMConfig)null, (OnlineCtcFstDecoderConfig)null, (HomophoneReplacerConfig)null, OnlineRecognizerKt.getEndpointConfig(), true, (String)null, 0, (String)null, 0.0F, (String)null, (String)null, 0.0F, 16284, (DefaultConstructorMarker)null);
        this.recognizer = new OnlineRecognizer(mAct.getApplication().getAssets(), config);
    }
}
