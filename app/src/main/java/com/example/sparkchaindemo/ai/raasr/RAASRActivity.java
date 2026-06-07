package com.example.sparkchaindemo.ai.raasr;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sparkchaindemo.llm.online_llm.image_understanding.GetFilePathFromUri;
import com.example.sparkchaindemo.utils.FileUtils;
import com.iflytek.sparkchain.core.raasr.RAASR;
import com.iflytek.sparkchain.core.raasr.RAASRCallbacks;
import com.txkj.drawingapp.R;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
public class RAASRActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "AEELog";
    private TextView tv_result;
    private RAASR mRAASR;
    private String RAASRAPIKEY = "";
    private static final int AUDIO_FILE_SELECT_CODE = 1024;
    private String orderId = null;

    private long resultGenTime = 0;
    private String requestId;
    private String resultTypes = "transfer";
    private TextView tv_audioPathInfo;
    private String audioPath = "/sdcard/iflytek/asr/cn_test.pcm";
    private Button btn_stop,btn_upload;

    RAASRCallbacks mRAASRCallbacks = new RAASRCallbacks() {
        @Override
        public void onResult(RAASR.RaAsrResult raAsrResult, Object usrTag) {
            int status                                 = raAsrResult.getStatus();
            String orderResult                         = raAsrResult.getOrderResult();
            RAASR.RaAsrTransResult[] raAsrTransResults = raAsrResult.getTransResult();
            orderId                                    = raAsrResult.getOrderId();
            long originalDuration                      = raAsrResult.getOriginalDuration();
            long realDuration                          = raAsrResult.getRealDuration();
            int taskEstimateTime                       = raAsrResult.getTaskEstimateTime();
            String usrContext                          = (String)usrTag;
            resultGenTime = System.currentTimeMillis()+taskEstimateTime;

            String info = "{status:"+status+",orderId:"+orderId+",originalDuration:"
                    +originalDuration+",realDuration:"+realDuration+",taskEstimateTime:"+taskEstimateTime+",usrContext:"+usrContext+"}\n";
            Log.d(TAG,info);
            switch(usrContext){
                case "UPLOAD":
                    Log.d(TAG,"UPLOAD");
                    showInfo("Audio uploaded successful! orderId:"+orderId+"\n");
                    setStopButton(btn_stop,false);
                    seleteResult();
                    break;
                case "SELETE":
                    Log.d(TAG,"SELETE");
                    FileUtils.longLog(TAG,orderResult+"\n");
                    if("transfer".equals(resultTypes)){
                        if(!TextUtils.isEmpty(orderResult)) {
                            showInfo("Transcription result:" + analysisResult(orderResult) + "\n");
                            setStopButton(btn_upload,true);
                        }else {
                            showInfo("Don't query transcription result, retry...\n");
                            seleteResult();
                        }
                    }else if("translate".equals(resultTypes)){
                        String transResult = "";
                        for (int i = 0; i < raAsrTransResults.length; i++) {
                            transResult = transResult + raAsrTransResults[i].getDst();
                        }
                        showInfo("Translate result:"+transResult+"\n");
                    }
                    break;
            }
            toend();
        }

        @Override
        public void onError(RAASR.RaAsrError raAsrError, Object o) {
            String errMsg  = raAsrError.getErrMsg();
            int errCode    = raAsrError.getCode();
            String orderId = raAsrError.getOrderId();
            int failType   = raAsrError.getFailType();
            String info = "{errMsg:"+errMsg+",errCode:"+errCode+",orderId:"+orderId+",failType:"+failType+"}\n";
            Log.d(TAG,info);
        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ai_raasr);
        initView();
        initRAASR();
    }

    private void initView(){
        btn_upload = findViewById(R.id.ai_raasr_start);
        findViewById(R.id.ai_raasr_getResult).setOnClickListener(this);
        btn_stop = findViewById(R.id.ai_raasr_stop);
        findViewById(R.id.ai_raasr_audiopath).setOnClickListener(this);
        tv_result = findViewById(R.id.ai_raasr_notification);
        tv_result.setMovementMethod(new ScrollingMovementMethod());
        tv_audioPathInfo = findViewById(R.id.ai_raasr_pathinfo);
        btn_stop.setOnClickListener(this);
        btn_upload.setOnClickListener(this);
        setStopButton(btn_stop,false);
    }
    int count = 0;
    private void runRaasr(){
        String resultType = "transfer";
        resultTypes = resultType;
        if(mRAASR == null){
            initRAASR();
        }
        setStopButton(btn_upload,false);
        orderId = null;
        mRAASR.transLanguage("en");
        mRAASR.language("cn");
        mRAASR.roleType(0);
        Log.d(TAG,"Current audio file path:"+audioPath);

        count ++;
        requestId = "The "+count+" times query";

        int ret = mRAASR.uploadAsync(audioPath,requestId,"UPLOAD");
        setStopButton(btn_stop,true);
        Log.d(TAG,"RAASR start:"+ret);
        if(ret !=0 ){
            showInfo("Transcription failed code:"+ret+"\n");
        }else{
            showInfo("Uploading audio, please wait...\n");
        }
    }

    private void seleteResult(){
        if(mRAASR !=null && orderId!=null){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    long genRemainTime = resultGenTime-System.currentTimeMillis();
                    long needTime = 10;
                    if(genRemainTime > 0){
                        needTime = genRemainTime/1000 + 1;
                    }
                    String catche = tv_result.getText().toString();
                    showInfo("OrderId:"+orderId+" result, estimated to require "+needTime+" seconds, please wait...\n");
                    while(needTime>0){
                        try {
                            Thread.sleep(1000);
                            needTime --;
                            String info = catche + " Query orderId:"+orderId+" result, estimated to require "+ needTime +" seconds, please wait...\n";
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tv_result.setText(info);
                                    toend();
                                }
                            });
                        } catch (InterruptedException e) {
//                          throw new RuntimeException(e);
                        }
                    }
                    resultGenTime = 0;
                    String resultType = "transfer";
                    mRAASR.resultType(resultType);
                    int ret = mRAASR.getResultOnceAsync(orderId,"SELETE");
                    if(ret != 0){
                        showInfo("Transcription failed, error code:"+ret+"\n");
                    }
                }
            }).start();
        }else{
            showInfo("Can't get orderId or raasr instance! please click start testing or wait result to retry!\n");
        }
    }

    private void initRAASR(){
        RAASRAPIKEY = getResources().getString(R.string.RAASRAPIKEY);
        mRAASR = new RAASR(RAASRAPIKEY);
        mRAASR.registerCallbacks(mRAASRCallbacks);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.ai_raasr_start) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    runRaasr();
                }
            }).start();
        } else if (view.getId() == R.id.ai_raasr_stop) {
            int ret = mRAASR.stop();
            showInfo("Stop upload, ret:" + ret + "\n");
            setStopButton(btn_stop, false);
            setStopButton(btn_upload, true);
        } else if (view.getId() == R.id.ai_raasr_audiopath) {
            Log.d(TAG,"audioPath");
            showFileChooser();
        }
    }

    private void showFileChooser() {
        Log.d(TAG,"showFileChooser");
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, AUDIO_FILE_SELECT_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case AUDIO_FILE_SELECT_CODE:
                if (data != null) {
                    Uri uri = data.getData();
                    String path = GetFilePathFromUri.getFileAbsolutePath(this, uri);
                    audioPath = path;
                }
                tv_audioPathInfo.setText("Current audio file path:"+audioPath);
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void showInfo(String text){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv_result.append(text);
            }
        });
    }

    private void setStopButton(Button btn,boolean enable){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btn.setEnabled(enable);
            }
        });
    }


    private List<String> extractChineseCharacters(String jsonString) {
        List<String> chineseCharacters = new ArrayList<>();
        try {
            JSONObject object = new JSONObject(jsonString);
            JSONArray latticeArray = object.getJSONArray("lattice");
            for (int i = 0; i < latticeArray.length(); i++) {
                JSONObject jsonObject = latticeArray.getJSONObject(i);
                String json1Best = jsonObject.getString("json_1best");
                JSONObject stObject = new JSONObject(json1Best).getJSONObject("st");
                JSONArray rtArray = stObject.getJSONArray("rt");
                for (int j = 0; j < rtArray.length(); j++) {
                    JSONArray wsArray = rtArray.getJSONObject(j).getJSONArray("ws");
                    for (int k = 0; k < wsArray.length(); k++) {
                        JSONArray cwArray = wsArray.getJSONObject(k).getJSONArray("cw");
                        for (int l = 0; l < cwArray.length(); l++) {
                            JSONObject cwObject = cwArray.getJSONObject(l);
                            String word = cwObject.getString("w");
                            chineseCharacters.add(word);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG,"extractChineseCharacters:"+e.toString());
        }
        return chineseCharacters;
    }

    private String analysisResult(String orderResult){
        List<String> resultList = extractChineseCharacters(orderResult);
        StringBuilder sb = new StringBuilder();
        for (String str : resultList) {
            sb.append(str);
        }
        String result = sb.toString();
        Log.d(TAG,"analysisResult:"+result);
        return result;
    }

    public void toend(){
        int scrollAmount = tv_result.getLayout().getLineTop(tv_result.getLineCount()) - tv_result.getHeight();
        if (scrollAmount > 0) {
            tv_result.scrollTo(0, scrollAmount+10);
        }
    }
}
