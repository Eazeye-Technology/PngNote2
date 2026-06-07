package com.example.sparkchaindemo.ai.asr;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sparkchaindemo.adapter.SpinnerAdapter;
import com.example.sparkchaindemo.utils.AudioRecorderManager;
import com.hjq.permissions.OnPermission;
import com.hjq.permissions.XXPermissions;
import com.iflytek.sparkchain.core.asr.ASR;
import com.iflytek.sparkchain.core.asr.AsrCallbacks;
import com.iflytek.sparkchain.core.asr.Segment;
import com.iflytek.sparkchain.core.asr.Transcription;
import com.iflytek.sparkchain.core.asr.Vad;
import com.txkj.drawingapp.R;

import java.io.FileInputStream;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class asrActivity extends AppCompatActivity implements View.OnClickListener, AudioRecorderManager.AudioDataCallback {

    private static final String TAG = "AEELog";
    private Spinner sp_language;
    private TextView tv_result;
    private Button btn_audio_start,btn_file_start;
    private String language = "zh_cn";
    private ASR mAsr = null;
    private boolean isrun = false;
    private boolean isdws = false;
    private String startMode = "NONE";
    private String cacheInfo = "";
    private AudioRecorderManager audioRecorderManager;
    private AtomicBoolean isWrite = new AtomicBoolean(false);
    AsrCallbacks mAsrCallbacks = new AsrCallbacks() {
        @Override
        public void onResult(ASR.ASRResult asrResult, Object o) {
            Log.e(TAG,"result:"+asrResult.getStatus());
            int begin     = asrResult.getBegin();
            int end       = asrResult.getEnd();
            int status    = asrResult.getStatus();
            String result = asrResult.getBestMatchText();
            String sid    = asrResult.getSid();

            List<Vad> vads = asrResult.getVads();
            List<Transcription> transcriptions = asrResult.getTranscriptions();
            int vad_begin = -1;
            int vad_end = -1;
            String word = null;
            for(Vad vad:vads){
                vad_begin = vad.getBegin();
                vad_end = vad.getEnd();
                Log.d(TAG,"vad={begin:"+vad_begin+",end:"+vad_end+"}");
            }
            for(Transcription transcription : transcriptions){
                List<Segment> segments = transcription.getSegments();
                for(Segment segment:segments){
                    word = segment.getText();
//                    Log.d(TAG,"word={word:"+word+"}");
                }
            }
            String info = "result={begin:"+begin+",end:"+end+",status:"+status+",result:"+result+",sid:"+sid+"}";
            Log.d(TAG,info);
            if(status == 0){
                if(isdws){
                    cacheInfo = tv_result.getText().toString();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv_result.setText(cacheInfo+" result:"+result);
                        }
                    });
                }
            }else if(status == 2){
                if(isdws){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv_result.setText(cacheInfo+" result:"+result+"\n");
                        }
                    });
                }else{
                    showInfo(result+"\n");
                }
                stopAsr();
            }else{
                if(isdws){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv_result.setText(cacheInfo+" result:"+result);
                        }
                    });
                }
            }
            toend();
            /*********************************************************************/
        }

        @Override
        public void onError(ASR.ASRError asrError, Object o) {
            int code = asrError.getCode();
            String msg = asrError.getErrMsg();
            String sid = asrError.getSid();
            String info = "error={code:"+code+",msg:"+msg+",sid:"+sid+"}";
            Log.d(TAG,info);
            showInfo("error, code:"+code+",msg:"+msg+",sid:"+sid+"\n");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    btn_audio_start.setText("Microphone recognition");
                    btn_audio_start.setEnabled(true);
                    btn_file_start.setEnabled(true);
                }
            });
            isrun = false;
        }

        @Override
        public void onBeginOfSpeech() {

        }

        @Override
        public void onEndOfSpeech() {

        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ai_asr);
        initView();
//        initASR();
    }

    private void initASR(){
        if(mAsr == null){
            mAsr = new ASR();
            mAsr.registerCallbacks(mAsrCallbacks);
        }
    }
    int count = 0;
    private void runAsr_file(){
        if(isrun){
            showInfo("Recognition, don't reopen\n");
            return;
        }
        if(mAsr == null){
            initASR();
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btn_audio_start.setText("Recording\n");
                btn_audio_start.setEnabled(false);
                btn_file_start.setEnabled(false);
            }
        });
        isdws = false;
        mAsr.language(language);
        mAsr.domain("iat");
        mAsr.accent("mandarin");
        mAsr.vinfo(true);
        if("zh_cn".equals(language)){
            mAsr.dwa("wpgs");
            isdws = true;
        }

        count++;
        int ret = mAsr.start(count+"");
        //AudioAttributes attr = new AudioAttributes();
        //attr.setSampleRate(16000);
        //attr.setEncoding("raw");
        //attr.setChannels(1);
        //attr.setBitdepth(16);
        //int ret = asr.start(attr,count+"");

        if(ret == 0){
            isrun = true;
            write();
        }else{
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    btn_audio_start.setText("Microphone recognition");
                    btn_audio_start.setEnabled(true);
                    btn_file_start.setEnabled(true);
                }
            });
            isrun = false;
            showInfo("Recognition failed, code:"+ret+"\n");
        }
    }

    private void runAsr_Audio(){
        if(isrun){
            showInfo("Recognition, don't reopen\n");
            return;
        }

        if(mAsr == null){
            initASR();
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showInfo("Recording, please press stop to get final result\n");
                btn_audio_start.setText("Recording\n");
                btn_audio_start.setEnabled(false);
                btn_file_start.setEnabled(false);
            }
        });
        isdws = false;
        mAsr.language(language);
        mAsr.domain("iat");
        mAsr.accent("mandarin");
        mAsr.vinfo(true);
        if("zh_cn".equals(language)){
            mAsr.dwa("wpgs");
            isdws = true;
        }

        count++;
        int ret = mAsr.start(count+"");
        if(ret != 0){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    btn_audio_start.setText("Microphone recognition");
                    btn_audio_start.setEnabled(true);
                    btn_file_start.setEnabled(true);
                }
            });
            isrun = false;
            showInfo("Recognition failed, code:"+ret+"\n");
        }else{
            isrun = true;
            isWrite.set(true);
            if (audioRecorderManager == null) {
                audioRecorderManager = AudioRecorderManager.getInstance();
            }
            audioRecorderManager.startRecord();
            audioRecorderManager.registerCallBack(this);
        }
    }

    private String getAudioPath(){
        String path = "";
        showInfo("Choose language:"+language+"\n");
        switch(language){
            case "zh_cn":
                path = "/sdcard/iflytek/asr/cn.pcm";
                break;
            case "en_us":
                path = "/sdcard/iflytek/asr/en.pcm";
                break;
        }
        return path;
    }

    private void write(){
        String filePath = getAudioPath();
        showInfo("Recognition file path:"+filePath+"\n");
        showInfo("Recognition, please wait...\n");
        try{
            FileInputStream fs = new FileInputStream(filePath);
            byte[] buffer = new byte[1280];
            int len = 0;
            while (-1 != (len = fs.read(buffer))) {
                if(!isrun){
                    break;
                }
                if(len>0){
                    mAsr.write(buffer.clone());
                    Thread.sleep(40);
                }
            }
            fs.close();
            Thread.sleep(10);
            mAsr.stop(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void stopAsr() {
        if (isrun) {
            if ("AUDIO".equals(startMode)) {
                if (mAsr != null) {
                    if (audioRecorderManager != null) {
                        audioRecorderManager.stopRecord();
                        audioRecorderManager = null;
                    }
                    mAsr.stop(false);
                    showInfo("\nStop recording.\n");
                }
            }else{
                if(mAsr!=null) {
                    mAsr.stop(true);
                    showInfo("\nRecognition stopped\n");
                }
            }
            startMode = "NONE";
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    btn_audio_start.setText("Microphone recognition");
                    btn_audio_start.setEnabled(true);
                    btn_file_start.setEnabled(true);
                }
            });
            isrun = false;
        }else{
            showInfo("\nRecognition stopped.\n");
        }
    }

    private void initView(){
        btn_file_start = findViewById(R.id.ai_asr_file_start_btn);
        btn_audio_start = findViewById(R.id.ai_asr_audio_start_btn);
        btn_file_start.setOnClickListener(this);
        btn_audio_start.setOnClickListener(this);
        findViewById(R.id.ai_asr_stop_btn).setOnClickListener(this);
        sp_language = findViewById(R.id.ai_asr_language);
        tv_result = findViewById(R.id.ai_asr_result);
        tv_result.setMovementMethod(new ScrollingMovementMethod());
        SpinnerAdapter languageSpinner = new SpinnerAdapter(this, asrParams.getLanguage());
        sp_language.setAdapter(languageSpinner);
        sp_language.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                language = asrParams.getLanguage().get(position).value;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    private void getPermission(){
        XXPermissions.with(this).permission("android.permission.RECORD_AUDIO").request(new OnPermission() {
            @Override
            public void hasPermission(List<String> granted, boolean all) {
                Log.d(TAG,"Grant permissions:"+all);
                for(int i=0;i<granted.size();i++){
                    Log.d(TAG,"Grant permissions:"+granted.get(i));
                }
                if(all){
                    runAsr_Audio();
                }
            }

            @Override
            public void noPermission(List<String> denied, boolean quick) {
                if(quick){
                    Log.e(TAG,"onDenied: please manually grant");
                    XXPermissions.startPermissionActivity(asrActivity.this,denied);
                }else{
                    Log.e(TAG,"onDenied: grant failed");
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.ai_asr_file_start_btn) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    startMode = "FILE";
                    runAsr_file();
                }
            }).start();
        } else if (view.getId() == R.id.ai_asr_audio_start_btn) {
            startMode = "AUDIO";
            getPermission();
        } else if (view.getId() == R.id.ai_asr_stop_btn) {
                stopAsr();
        }
    }

    private void showInfo(String text){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv_result.append(text);
            }
        });
    }



    public void toend(){
        int scrollAmount = tv_result.getLayout().getLineTop(tv_result.getLineCount()) - tv_result.getHeight();
        if (scrollAmount > 0) {
            tv_result.scrollTo(0, scrollAmount+10);
        }
    }

    @Override
    public void onAudioData(byte[] data, int size) {
        if (isWrite.get()) {
            int ret = mAsr.write(data);
            if (ret != 0) {
                isWrite.set(false);
            }
        }
    }

    @Override
    public void onAudioVolume(double db, int volume) {

    }
}
