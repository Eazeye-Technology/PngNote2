package com.txkj.drawingapp.activity;

import android.app.Activity;
import android.util.Log;
import android.widget.TextView;

import com.example.sparkchaindemo.utils.AudioRecorderManager;
import com.hjq.permissions.OnPermission;
import com.hjq.permissions.XXPermissions;
import com.iflytek.sparkchain.core.LogLvl;
import com.iflytek.sparkchain.core.SparkChain;
import com.iflytek.sparkchain.core.SparkChainConfig;
import com.iflytek.sparkchain.core.rtasr.RTASR;
import com.iflytek.sparkchain.core.rtasr.RTASRCallbacks;
import com.txkj.drawingapp.R;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

//https://console.xfyun.cn/services/rta
public class BookActivity4RTASRDialog implements AudioRecorderManager.AudioDataCallback{
    public final static boolean NO_AUTO_WRAP = true;
    private static final String TAG = "AEELog";
    private String RTASRAPIKEY = "";
//    private Spinner sp_language;
//    TextView tv_result,tv_transResult,tv_audioPath;
//    private Button btn_audio_start,btn_file_start;

    private final static String PREFIX01 = "";
    private final static String PREFIX02 = "";

    private RTASR mRTASR;
    boolean isrun = false;
    String asrFinalResult = "" + PREFIX01 + "\n";
    String transFinalResult = "" + PREFIX02 + "\n";
    String audioPath = "";
    private String startMode = "NONE";
    private ASRMode language = ASRMode.CN;
    private List<String> languageList = new ArrayList<String>();
    private AudioRecorderManager audioRecorderManager;
    private AtomicBoolean isWrite = new AtomicBoolean(false);

    private void showInfo(String text) {

    }
    public void toend_tv_transResult() {}
    public void toend_tv_result() {}
    public void tv_audioPath_setText(String str) {}
    long lastUpdate = 0;
    public final static boolean SHOW_DEBUG_TIME = false;
    public void tv_result_setText(String str, String subStr, boolean isEnd) {
        if (!NO_AUTO_WRAP && System.currentTimeMillis() - lastUpdate > 1000L * 2L) { //5 second auto wrap
            //idle, send new line
            String oldStr = asrFinalResult;
            asrFinalResult = "";
            if (false) {
                Log.e(TAG, "5sec: str == " + str + ", asrFinalResult == " + asrFinalResult + ", subStr == " + subStr);
                //keep prefix . or prefix ?
                BookActivity4Utils.tv_result_setText(mAct, asrFinalResult, asrFinalResult, true, false, null, -1);
                BookActivity4Utils.tv_result_setText(mAct, subStr, subStr, false, false, null, -1);
                asrFinalResult = "" + subStr;
            } else {
                Log.e(TAG, "5sec: str == " + str + ", asrFinalResult == " + asrFinalResult + ", subStr == " + subStr);
                //remove prefix . or prefix ?
                if (oldStr.startsWith(".") || oldStr.startsWith("?") || oldStr.startsWith(",")) {
                    BookActivity4Utils.tv_result_setText(mAct, oldStr.substring(0, 1), oldStr.substring(0, 1), true, true, null, -1);
                    BookActivity4Utils.tv_result_setText(mAct, "", "", true, false, null, -1);
                    asrFinalResult = "" + oldStr.substring(1);
                    BookActivity4Utils.tv_result_setText(mAct, asrFinalResult, asrFinalResult, false, false, null, -1);
                } else {
//                    BookActivity4Utils.tv_result_setText(mAct, oldStr, oldStr, true);
//                    //BookActivity4Utils.tv_result_setText(mAct, asrFinalResult, asrFinalResult, true);
//                    asrFinalResult = "";
                    BookActivity4Utils.tv_result_setText(mAct, asrFinalResult, asrFinalResult, true, false, null, -1);
                    BookActivity4Utils.tv_result_setText(mAct, subStr, subStr, false, false, null, -1);
                    asrFinalResult = "" + subStr;
                }
            }
        } else {
            BookActivity4Utils.tv_result_setText(mAct, str, subStr, isEnd, false, null, -1);
        }
        lastUpdate = System.currentTimeMillis();
    }
    public void tv_transResult_setText(String str) {}
    public void btn_audio_start_setText(String str) {}
    public void btn_audio_start_setEnabled(boolean enable) {
        BookActivity4Utils.btn_audio_start_setEnabled(mAct, enable);
    }
    public void btn_file_start_setEnabled(boolean enable) {}
    private Activity mAct;
    public BookActivity4RTASRDialog(Activity act) {
        this.mAct = act;
        onCreate();
    }
    public void setLanguage(boolean isCN) {
        if (isCN) {
            language = ASRMode.CN;
        } else {
            language = ASRMode.EN;
        }
    }
    //don't use this
    public void onClick_file() {
        if (false) {
            tv_result_setText("" + PREFIX01 + "\n", "" + PREFIX01 + "\n", true);
        } else {
            tv_result_setText("", "", true);
        }
        tv_transResult_setText("" + PREFIX02 + "\n");
        asrFinalResult = "" + PREFIX01 + "\n";
        transFinalResult = "" + PREFIX02 + "\n";
        new Thread(new Runnable() {
            @Override
            public void run() {
                runRtasr_file(language);
            }
        }).start();
    }
    public void onClick_audio() {
        setLanguage(false); //choose CN

        if (false) {
            tv_result_setText("" + PREFIX01 + "\n", "" + PREFIX01 + "\n", true);
        } else {
            tv_result_setText("", "", true);
        }
        tv_transResult_setText("" + PREFIX02 + "\n");
        asrFinalResult = "" + PREFIX01 + "\n";
        transFinalResult = "" + PREFIX02 + "\n";
        new Thread(new Runnable() {
            @Override
            public void run() {
                getPermission();
            }
        }).start();
    }
    public void onClick_stop() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mRTASR != null && isrun){
                    if ("FILE".equals(startMode)) {
                        mRTASR.stop();
                    } else {
                        if (audioRecorderManager != null) {
                            audioRecorderManager.stopRecord();
                            audioRecorderManager = null;
                        }
                        mRTASR.stop();
                    }
                    startMode = "NONE";
                    isrun = false;
                    mAct.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            btn_audio_start_setText("Microphone recognition");
                            btn_audio_start_setEnabled(true);
                            btn_file_start_setEnabled(true);
                        }
                    });
                } else {
                    mAct.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            btn_audio_start_setText("Microphone recognition");
                            btn_audio_start_setEnabled(true);
                            btn_file_start_setEnabled(true);
                        }
                    });
                }
            }
        }).start();
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

    private void onCreate() {
        languageList.add("Chinese");
        languageList.add("English");
        init();
    }

    protected void init() {
        SDKInit();
        RTASRAPIKEY = mAct.getResources().getString(R.string.RTASRAPIKEY);
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

            mAct.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(status == 1){
                        String asrText = asrFinalResult + data;
                        tv_result_setText(asrText, data, false);
                        toend_tv_result();
                    } else if (status == 2) {
                        asrFinalResult = asrFinalResult + data;
                        //FIXME:added
                        String asrText = asrFinalResult;
                        tv_result_setText(asrText, data, false);
                        toend_tv_result();
                    } else if(status == 3) {
                        tv_result_setText(asrFinalResult, "", false);
                        toend_tv_result();
                        if (isrun){
                            if ("AUDIO".equals(startMode)){
                                if (mRTASR!=null){
                                    if (audioRecorderManager != null) {
                                        audioRecorderManager.stopRecord();
                                        audioRecorderManager = null;
                                    }
                                    mRTASR.stop();
                                }
                            } else {
                                if (mRTASR != null) {
                                    mRTASR.stop();
                                }
                            }
                            startMode = "NONE";
                            mAct.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    btn_audio_start_setText("Microphone recognition");
                                    btn_audio_start_setEnabled(true);
                                    btn_file_start_setEnabled(true);
                                }
                            });
                            isrun = false;
                        }
                    } else if (status == 0){
                        if (transStatus == 2) {
                            transFinalResult = transFinalResult + dst;
                            tv_transResult_setText(transFinalResult);
                            toend_tv_transResult();
                        } else {
                            String transText = transFinalResult + dst;
                            tv_transResult_setText(transText);
                            toend_tv_transResult();
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
                mAct.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        btn_audio_start_setText("Microphone recognition");
                        btn_audio_start_setEnabled(true);
                        btn_file_start_setEnabled(true);
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
        asrFinalResult = "" + PREFIX01 + "\n";
        transFinalResult = "" + PREFIX02 + "\n";
        mAct.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (false) {
                    tv_result_setText(asrFinalResult, asrFinalResult, true);
                } else {
                    tv_result_setText(asrFinalResult, "", true);
                }
                tv_transResult_setText(transFinalResult);
                tv_audioPath_setText("Audio file path:" + audioPath);
                btn_audio_start_setEnabled(false);
                btn_file_start_setEnabled(false);
            }
        });
        startMode = "FILE";
        isrun = true;
        int ret = mRTASR.start(count+"");
        Log.d(TAG, "mRTASR.start ret:" + ret+"-count:"+count);
        if (ret != 0){
            mAct.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    isrun = false;
                    tv_audioPath_setText("Transcription error, code:" + ret);
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
            if (isrun)
                mRTASR.stop();
            Thread.sleep(10);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getPermission(){
        XXPermissions.with(mAct).permission("android.permission.RECORD_AUDIO").request(new OnPermission() {
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
                    XXPermissions.startPermissionActivity(mAct, denied);
                }else{
                    Log.e(TAG,"onDenied: grant failed");
                }
            }
        });
    }


    private void runRtasr_Audio(ASRMode mode){
        try {
            if (isrun)
                return;
            count++;
            isrun = true;
            if (mRTASR == null) {
                mRTASR = new RTASR(RTASRAPIKEY);
                mRTASR.registerCallbacks(mRtAsrCallbacks);
            }

            mRTASR.transType("normal");
            mRTASR.transStrategy(2);
            mRTASR.lang("en");
            //FIXME: don't set targetLange, otherwise RTASR error code = 10110
            //see https://www.bookstack.cn/read/xfyun-rest_api/f1aca998ccd8f33a.md
            //see also https://www.xfyun.cn/doc/asr/rtasr/API.html
            //invalid authorization|illegal signa
            //need enable .logLevel(LogLvl.VERBOSE.getValue());
            //
            asrFinalResult = "" + PREFIX01 + "\n";
            transFinalResult = "" + PREFIX02 + "\n";
            mAct.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (false) {
                        tv_result_setText(asrFinalResult, asrFinalResult, true);
                    } else {
                        tv_result_setText(asrFinalResult, "", true);
                    }
                    tv_transResult_setText(transFinalResult);
                    tv_audioPath_setText("Audio file path:" + audioPath);
                    btn_audio_start_setText("Recording\n");
                    btn_audio_start_setEnabled(false);
                    btn_file_start_setEnabled(false);
                }
            });
            startMode = "AUDIO";
            int ret = mRTASR.start(count + "");
            Log.d(TAG, "mRTASR.start ret:" + ret + "-count:" + count);
            if (ret != 0) {
                mAct.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        isrun = false;
                        tv_audioPath_setText("Transcription error, code:" + ret);
                    }
                });
            } else {
                isWrite.set(true);
                if (audioRecorderManager == null) {
                    audioRecorderManager = AudioRecorderManager.getInstance();
                }
                audioRecorderManager.startRecord();
                audioRecorderManager.registerCallBack(this);
            }
        } catch (Throwable eee) {
            eee.printStackTrace();
        }
    }


    public void toend(TextView tv){
        int scrollAmount = tv.getLayout().getLineTop(tv.getLineCount()) - tv.getHeight();
        if (scrollAmount > 0) {
            tv.scrollTo(0, scrollAmount+10);
        }
    }

    private boolean isAuth = false;
    private boolean SDKInit(){
        Log.d(TAG,"initSDK");
        SparkChainConfig sparkChainConfig = SparkChainConfig.builder();
        sparkChainConfig.appID(mAct.getResources().getString(R.string.appid))
                .apiKey(mAct.getResources().getString(R.string.apikey))
                .apiSecret(mAct.getResources().getString(R.string.apiSecret))
//                .uid("")
//                .logPath("/sdcard/iflytek/AEELog.txt")
                //.logLevel(LogLvl.VERBOSE.getValue());
                .logLevel(LogLvl.ERROR.getValue());

        int ret = SparkChain.getInst().init(mAct.getApplicationContext(),sparkChainConfig);
        String result;
        if (ret == 0) {
            result = "SDK init successful";
            isAuth = true;
        } else {
            result = "SDK init failed:" + ret;
            isAuth = false;
        }
        Log.d(TAG, result);
        showInfo(result);
        return isAuth;
    }
}
