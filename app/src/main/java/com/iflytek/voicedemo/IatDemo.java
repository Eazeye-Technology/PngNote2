package com.iflytek.voicedemo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.LexiconListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.iflytek.speech.setting.IatSettings;
import com.iflytek.speech.util.FucUtil;
import com.iflytek.speech.util.JsonParser;
import com.txkj.drawingapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class IatDemo extends Activity implements OnClickListener {
    private static String TAG = IatDemo.class.getSimpleName();
    private SpeechRecognizer mIat;
    private RecognizerDialog mIatDialog;
    private HashMap<String, String> mIatResults = new LinkedHashMap<>();

    private EditText mResultText;
    private EditText showContacts;
    private TextView languageText;
    private Toast mToast;
    private SharedPreferences mSharedPreferences;
    private String mEngineType = SpeechConstant.TYPE_CLOUD;

    private String[] languageEntries;
    private String[] languageValues;
    private String language = "zh_cn";
    private int selectedNum = 0;

    private String resultType = "json";

    private StringBuffer buffer = new StringBuffer();


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.iatdemo);

        languageEntries = getResources().getStringArray(R.array.iat_language_entries);
        languageValues = getResources().getStringArray(R.array.iat_language_value);
        initLayout();
        mIat = SpeechRecognizer.createRecognizer(IatDemo.this, mInitListener);

        mIatDialog = new RecognizerDialog(IatDemo.this, mInitListener);

        mSharedPreferences = getSharedPreferences(IatSettings.PREFER_NAME,
                Activity.MODE_PRIVATE);
        mResultText = ((EditText) findViewById(R.id.iat_text));
        showContacts = (EditText) findViewById(R.id.iat_contacts);
    }


    private void initLayout() {
        findViewById(R.id.iat_recognize).setOnClickListener(IatDemo.this);
        findViewById(R.id.iat_recognize_stream).setOnClickListener(IatDemo.this);
        findViewById(R.id.iat_upload_userwords).setOnClickListener(IatDemo.this);
        findViewById(R.id.iat_stop).setOnClickListener(IatDemo.this);
        findViewById(R.id.iat_cancel).setOnClickListener(IatDemo.this);
        findViewById(R.id.image_iat_set).setOnClickListener(IatDemo.this);
        findViewById(R.id.languageText).setOnClickListener(IatDemo.this);
    }

    int ret = 0;

    @Override
    public void onClick(View view) {
        if (null == mIat) {
            this.showTip("Create object failed, make sure libmsc.so put correctly, and call createUtility to init");
            return;
        }

        if (view.getId() == R.id.image_iat_set) {
            Intent intents = new Intent(IatDemo.this, IatSettings.class);
            startActivity(intents);
        } else if (view.getId() == R.id.iat_recognize) {
            buffer.setLength(0);
            mResultText.setText(null);
            mIatResults.clear();
            setParam();
            boolean isShowDialog = mSharedPreferences.getBoolean(
                    getString(R.string.pref_key_iat_show), true);
            if (isShowDialog) {
                mIatDialog.setListener(mRecognizerDialogListener);
                mIatDialog.show();
                showTip(getString(R.string.text_begin));
            } else {
                ret = mIat.startListening(mRecognizerListener);
                if (ret != ErrorCode.SUCCESS) {
                    showTip("Listen error, code:" + ret + ",please visit https://www.xfyun.cn/document/error-code");
                } else {
                    showTip(getString(R.string.text_begin));
                }
            }

        } else if (view.getId() == R.id.iat_recognize_stream) {
            executeStream();
        } else if (view.getId() == R.id.languageText) {
            setLanguage(view);
        } else if (view.getId() == R.id.iat_stop) {
            mIat.stopListening();
            showTip("Stop listening");
        } else if (view.getId() == R.id.iat_cancel) {
            mIat.cancel();
            showTip("Cancel listening");
        } else if (view.getId() == R.id.iat_upload_userwords) {
            String contents = FucUtil.readFile(IatDemo.this, "userwords", "utf-8");
            showContacts.setText(contents);
            mIat.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
            mIat.setParameter(SpeechConstant.TEXT_ENCODING, "utf-8");
            ret = mIat.updateLexicon("userword", contents, mLexiconListener);
            if (ret != ErrorCode.SUCCESS)
                showTip("Upload hot words failed, error:" + ret + ", please visit https://www.xfyun.cn/document/error-code");
        } else {

        }
    }

    private InitListener mInitListener = new InitListener() {

        @Override
        public void onInit(int code) {
            Log.d(TAG, "SpeechRecognizer init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                showTip("Init failed, error code:" + code + ", please visit https://www.xfyun.cn/document/error-code");
            }
        }
    };

    private void setLanguage(View v) {
        new AlertDialog.Builder(v.getContext()).setTitle("Language")
                .setSingleChoiceItems(languageEntries,
                        0,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                language = languageValues[which];
                                ((TextView) findViewById(R.id.languageText)).setText("Your choice: " + languageEntries[which]);
                                selectedNum = which;
                                dialog.dismiss();
                            }
                        }).show();
        mIat.setParameter(SpeechConstant.LANGUAGE, language);
    }

    private LexiconListener mLexiconListener = new LexiconListener() {

        @Override
        public void onLexiconUpdated(String lexiconId, SpeechError error) {
            if (error != null) {
                showTip(error.toString());
            } else {
                showTip(getString(R.string.text_upload_success));
            }
        }
    };

    private RecognizerListener mRecognizerListener = new RecognizerListener() {

        @Override
        public void onBeginOfSpeech() {
            showTip("Start speaking");
        }

        @Override
        public void onError(SpeechError error) {
            Log.d(TAG, "onError " + error.getPlainDescription(true));
            showTip(error.getPlainDescription(true));
        }

        @Override
        public void onEndOfSpeech() {
            showTip("Speaking stopped");
        }

        @Override
        public void onResult(RecognizerResult results, boolean isLast) {
            Log.d(TAG, results.getResultString());
            if (isLast) {
                Log.d(TAG, "onResult end");
            }
            if (resultType.equals("json")) {
                printResult(results);
                return;
            }
            if (resultType.equals("plain")) {
                buffer.append(results.getResultString());
                mResultText.setText(buffer.toString());
                mResultText.setSelection(mResultText.length());
            }
        }

        @Override
        public void onVolumeChanged(int volume, byte[] data) {
            showTip("Currently speaking, volume = " + volume + " return audio data = " + data.length);
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            //	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            //		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            //		Log.d(TAG, "session id =" + sid);
            //	}
        }
    };

    private void printResult(RecognizerResult results) {
        String text = JsonParser.parseIatResult(results.getResultString());
        String sn = null;
        try {
            JSONObject resultJson = new JSONObject(results.getResultString());
            sn = resultJson.optString("sn");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mIatResults.put(sn, text);

        StringBuffer resultBuffer = new StringBuffer();
        for (String key : mIatResults.keySet()) {
            resultBuffer.append(mIatResults.get(key));
        }
        mResultText.setText(resultBuffer.toString());
        mResultText.setSelection(mResultText.length());
    }

    private RecognizerDialogListener mRecognizerDialogListener = new RecognizerDialogListener() {
        public void onResult(RecognizerResult results, boolean isLast) {
            printResult(results);
        }

        public void onError(SpeechError error) {
            showTip(error.getPlainDescription(true));
        }

    };


    private void showTip(final String str) {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT);
        mToast.show();
    }

    public void setParam() {
        mIat.setParameter(SpeechConstant.PARAMS, null);
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
        mIat.setParameter(SpeechConstant.RESULT_TYPE, resultType);

        if (language.equals("zh_cn")) {
            String lag = mSharedPreferences.getString("iat_language_preference",
                    "mandarin");
            Log.e(TAG, "language = " + language);
            mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
            mIat.setParameter(SpeechConstant.ACCENT, lag);
        } else {
            mIat.setParameter(SpeechConstant.LANGUAGE, language);
        }
        Log.e(TAG, "last language:" + mIat.getParameter(SpeechConstant.LANGUAGE));

        //mIat.setParameter("view_tips_plain","false");

        mIat.setParameter(SpeechConstant.VAD_BOS, mSharedPreferences.getString("iat_vadbos_preference", "4000"));

        mIat.setParameter(SpeechConstant.VAD_EOS, mSharedPreferences.getString("iat_vadeos_preference", "1000"));

        mIat.setParameter(SpeechConstant.ASR_PTT, mSharedPreferences.getString("iat_punc_preference", "1"));

        mIat.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH,
                getExternalFilesDir("msc").getAbsolutePath() + "/iat.wav");
    }

    private void executeStream() {
        buffer.setLength(0);
        mResultText.setText(null);
        mIatResults.clear();
        setParam();
        mIat.setParameter(SpeechConstant.AUDIO_SOURCE, "-1");
        // mIat.setParameter(SpeechConstant.AUDIO_SOURCE, "-2");
        // mIat.setParameter(SpeechConstant.ASR_SOURCE_PATH, "sdcard/XXX/XXX.pcm");
        ret = mIat.startListening(mRecognizerListener);
        if (ret != ErrorCode.SUCCESS) {
            showTip("Recognition failed, error code:" + ret + ",please visit https://www.xfyun.cn/document/error-code");
            return;
        }
        try {
            InputStream open = getAssets().open("iattest.wav");
            byte[] buff = new byte[1280];
            while (open.available() > 0) {
                int read = open.read(buff);
                mIat.writeAudio(buff, 0, read);
            }
            mIat.stopListening();
        } catch (IOException e) {
            mIat.cancel();
            showTip("Read audio stream faild");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mIat != null) {
            mIat.cancel();
            mIat.destroy();
        }
    }
}
