package com.example.sparkchaindemo.ai.rtasr;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sparkchaindemo.utils.AudioRecorderManager;
import com.hjq.permissions.OnPermission;
import com.hjq.permissions.XXPermissions;
import com.iflytek.sparkchain.core.rtasr.RTASR;
import com.iflytek.sparkchain.core.rtasr.RTASRCallbacks;
import com.txkj.drawingapp.R;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class RTASRActivity extends AppCompatActivity implements View.OnClickListener, AudioRecorderManager.AudioDataCallback{
    private static final String TAG = "AEELog";
    private String RTASRAPIKEY = "";
    private Spinner sp_language;
    TextView tv_result,tv_transResult,tv_audioPath;
    private Button btn_audio_start,btn_file_start;
    private RTASR mRTASR;
    boolean isrun = false;
    String asrFinalResult = "Recognition result:\n";
    String transFinalResult = "Translation result:\n";
    String audioPath = "";
    private String startMode = "NONE";
    private ASRMode language = ASRMode.CN;
    private List<String> languageList = new ArrayList<String>();
    private AudioRecorderManager audioRecorderManager;
    private AtomicBoolean isWrite = new AtomicBoolean(false);

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.ai_rtasr_file_btn) {
            tv_result.setText("Recognition result:\n");
            tv_transResult.setText("Translation result:\n");
            asrFinalResult = "Recognition result:\n";
            transFinalResult = "Translation result:\n";
            new Thread(new Runnable() {
                @Override
                public void run() {
                    runRtasr_file(language);
                }
            }).start();
        } else if (view.getId() == R.id.ai_rtasr_audio_btn) {
            tv_result.setText("Recognition result:\n");
            tv_transResult.setText("Translation result:\n");
            asrFinalResult = "Recognition result:\n";
            transFinalResult = "Translation result:\n";

            new Thread(new Runnable() {
                @Override
                public void run() {
                    getPermission();
                }
            }).start();
        } else if (view.getId() == R.id.ai_rtasr_btn_stop) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if(mRTASR!=null&&isrun){
                        if("FILE".equals(startMode)){
                            mRTASR.stop();
                        }else{
                            if (audioRecorderManager != null) {
                                audioRecorderManager.stopRecord();
                                audioRecorderManager = null;
                            }
                            mRTASR.stop();
                        }
                        startMode = "NONE";
                        isrun = false;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                btn_audio_start.setText("Microphone recognition");
                                btn_audio_start.setEnabled(true);
                                btn_file_start.setEnabled(true);
                            }
                        });
                    }
                }
            }).start();
        }
    }

    @Override
    public void onAudioData(byte[] data, int size) {
        if (isWrite.get()) {
            int ret = mRTASR.write(data);
            if (ret != 0) {
                isWrite.set(false);
            }
        }
    }

    @Override
    public void onAudioVolume(double db, int volume) {

    }

    private enum ASRMode{
        CN,
        EN
    }

    @SuppressLint("MissingInflatedId")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ai_rtasr);
        languageList.add("Chinese");
        languageList.add("English");
        tv_result = findViewById(R.id.ai_rtasr_asrResult);
        tv_result.setMovementMethod(new ScrollingMovementMethod());
        tv_transResult = findViewById(R.id.ai_rtasr_translateResult);
        tv_transResult.setMovementMethod(new ScrollingMovementMethod());
        tv_audioPath = findViewById(R.id.ai_rtasr_testAudioPath);
        sp_language = findViewById(R.id.ai_rtasr_language);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, languageList);
        sp_language.setAdapter(adapter);
        sp_language.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                String selectedItem = adapterView.getItemAtPosition(position).toString();
                Log.d(TAG,"language:"+selectedItem);
                if("Chinese".equals(selectedItem)){
                    language = ASRMode.CN;
                }else{
                    language = ASRMode.EN;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        btn_file_start = findViewById(R.id.ai_rtasr_file_btn);
        btn_audio_start = findViewById(R.id.ai_rtasr_audio_btn);
        btn_file_start.setOnClickListener(this);
        btn_audio_start.setOnClickListener(this);
        findViewById(R.id.ai_rtasr_btn_stop).setOnClickListener(this);
        init();
    }

    protected void init() {
        RTASRAPIKEY = getResources().getString(R.string.RTASRAPIKEY);
        mRTASR = new RTASR(RTASRAPIKEY);
        mRTASR.registerCallbacks(mRtAsrCallbacks);
    }


    RTASRCallbacks mRtAsrCallbacks = new RTASRCallbacks() {
        @Override
        public void onResult(RTASR.RtAsrResult result, Object usrTag) {
            String data      = result.getData();
            String rawResult = result.getRawResult();
            int status       = result.getStatus();
            String sid       = result.getSid();
            String src       = result.getTransResult().getSrc();
            String dst       = result.getTransResult().getDst();
            int transStatus  = result.getTransResult().getStatus();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(status == 1){
                        String asrText = asrFinalResult + data;
                        tv_result.setText(asrText);
                        toend(tv_result);
                    }else if(status == 2){
                        asrFinalResult = asrFinalResult + data;
                        //FIXME:added
                        String asrText = asrFinalResult;
                        tv_result.setText(asrText);
                        toend(tv_result);
                    }else if(status == 3){
                        tv_result.setText(asrFinalResult);
                        toend(tv_result);
                        if(isrun){
                            if("AUDIO".equals(startMode)){
                                if(mRTASR!=null){
                                    if (audioRecorderManager != null) {
                                        audioRecorderManager.stopRecord();
                                        audioRecorderManager = null;
                                    }
                                    mRTASR.stop();
                                }
                            }else{
                                if(mRTASR!=null) {
                                    mRTASR.stop();
                                }
                            }
                            startMode = "NONE";
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    btn_audio_start.setText("Microphone Recognition");
                                    btn_audio_start.setEnabled(true);
                                    btn_file_start.setEnabled(true);
                                }
                            });
                            isrun = false;
                        }
                    }else if(status == 0){
                        if(transStatus == 2){
                            transFinalResult = transFinalResult + dst;
                            tv_transResult.setText(transFinalResult);
                            toend(tv_transResult);
                        }else{
                            String transText = transFinalResult + dst;
                            tv_transResult.setText(transText);
                            toend(tv_transResult);
                        }
                    }
                }
            });
        }

        @Override
        public void onError(RTASR.RtAsrError error, Object usrTag) {
            int code   = error.getCode();
            String msg = error.getErrMsg();
            String sid = error.getSid();
            if (isrun) {
                if ("AUDIO".equals(startMode)) {
                    if (mRTASR != null) {
                        if (audioRecorderManager != null) {
                            audioRecorderManager.stopRecord();
                            audioRecorderManager = null;
                        }
                        mRTASR.stop();
                    }
                }else{
                    if(mRTASR!=null) {
                        mRTASR.stop();
                    }
                }
                startMode = "NONE";
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        btn_audio_start.setText("Microphone Recognition");
                        btn_audio_start.setEnabled(true);
                        btn_file_start.setEnabled(true);
                    }
                });
                isrun = false;
            }
        }

        @Override
        public void onBeginOfSpeech() {

        }

        @Override
        public void onEndOfSpeech() {

        }
    };

    int count = 0;
    private void runRtasr_file(ASRMode mode) {
        if(isrun)
            return;
        count ++;

        if(mRTASR == null){
            mRTASR = new RTASR(RTASRAPIKEY);
            mRTASR.registerCallbacks(mRtAsrCallbacks);
        }

        mRTASR.transType("normal");
        mRTASR.transStrategy(2);
        if(mode == ASRMode.CN){
            mRTASR.lang("cn");
            mRTASR.targetLang("en");
            audioPath = "/sdcard/iflytek/asr/cn_test.pcm";
        }else{
            mRTASR.lang("en");
            mRTASR.targetLang("cn");
            audioPath = "/sdcard/iflytek/asr/en_test.pcm";
        }


        asrFinalResult = "Recognition result:\n";
        transFinalResult = "Translation result:\n";
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv_result.setText(asrFinalResult);
                tv_transResult.setText(transFinalResult);
                tv_audioPath.setText("Audio file path:" + audioPath);
                btn_audio_start.setEnabled(false);
                btn_file_start.setEnabled(false);
            }
        });
        startMode = "FILE";
        isrun = true;
        int ret = mRTASR.start(count+"");
        Log.d(TAG, "mRTASR.start ret:" + ret+"-count:"+count);
        if(ret != 0){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    isrun = false;
                    tv_audioPath.setText("Transcription error, code:"+ret);

                }
            });
        }
        try{
            FileInputStream fs = new FileInputStream(audioPath);
            byte[] buffer = new byte[320];
            int len = 0;
            while (-1 != (len = fs.read(buffer))) {
                if(!isrun){
                    Log.d(TAG, "mRTASR sop!!!!!!!!");
                    break;
                }
                if(len>0){
                    mRTASR.write(buffer.clone());
                    Thread.sleep(10);
                }
            }
            fs.close();
            Thread.sleep(10);
            if(isrun)
                mRTASR.stop();
            Thread.sleep(10);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                    runRtasr_Audio(language);
                }
            }

            @Override
            public void noPermission(List<String> denied, boolean quick) {
                if(quick){
                    Log.e(TAG,"onDenied: please manually grant");
                    XXPermissions.startPermissionActivity(RTASRActivity.this,denied);
                }else{
                    Log.e(TAG,"onDenied: grant failed");
                }
            }
        });
    }


    private void runRtasr_Audio(ASRMode mode){
        if(isrun)
            return;
        count ++;
        isrun = true;
        if(mRTASR == null){
            mRTASR = new RTASR(RTASRAPIKEY);
            mRTASR.registerCallbacks(mRtAsrCallbacks);
        }

        mRTASR.transType("normal");
        mRTASR.transStrategy(2);
        if(mode == ASRMode.CN){
            mRTASR.lang("cn");
            mRTASR.targetLang("en");
        }else{
            mRTASR.lang("en");
            mRTASR.targetLang("cn");
        }


        asrFinalResult = "Recognition result:\n";
        transFinalResult = "Translation result\n";
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv_result.setText(asrFinalResult);
                tv_transResult.setText(transFinalResult);
                tv_audioPath.setText("Audio file path:" + audioPath);
                btn_audio_start.setText("Recording\n");
                btn_audio_start.setEnabled(false);
                btn_file_start.setEnabled(false);
            }
        });
        startMode = "AUDIO";
        int ret = mRTASR.start(count+"");
        Log.d(TAG, "mRTASR.start ret:" + ret+"-count:"+count);
        if(ret != 0){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    isrun = false;
                    tv_audioPath.setText("Transcription error, code:"+ret);

                }
            });
        }else{
            isWrite.set(true);
            if (audioRecorderManager == null) {
                audioRecorderManager = AudioRecorderManager.getInstance();
            }
            audioRecorderManager.startRecord();
            audioRecorderManager.registerCallBack(this);
        }
    }

    public void toend(TextView tv){
        int scrollAmount = tv.getLayout().getLineTop(tv.getLineCount()) - tv.getHeight();
        if (scrollAmount > 0) {
            tv.scrollTo(0, scrollAmount+10);
        }
    }

}
