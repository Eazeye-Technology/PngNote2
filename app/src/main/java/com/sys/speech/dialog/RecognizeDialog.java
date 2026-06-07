package com.sys.speech.dialog;

import java.util.HashMap;
import java.util.LinkedHashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.sys.speech.db.DictationsDatabase;
import com.sys.speech.pojo.RecordingItem;
import com.sys.speech.util.FucUtil;
import com.sys.speech.util.JsonParser;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.txkj.drawingapp.R;

public class RecognizeDialog extends Dialog implements Dialog.OnCancelListener {
	private static final boolean D = true;
	private static final String TAG = "RecognizeDialog";	
	
	private static final boolean USE_BUFFER = true;
	
	public static final String LANG_CHINESE = "mandarin";
	public static final String LANG_CHINESE_GD = "cantonese";
	public static final String LANG_ENGLISH = "en_us";
	
	private Activity mAct;
	private RecordingItem mItem;
	private TextView mTextViewContent;
	
	private DictationsDatabase mDictationDatabase;
	
	public RecognizeDialog(Activity context, final RecordingItem item, String lag_) {
		super(context);
		this.mAct = context;
		this.mItem = item;
		this.lag = lag_;
		
		mDictationDatabase = new DictationsDatabase(context);

		mIat = SpeechRecognizer.createRecognizer(context, mInitListener);
		mToast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
		
		if (mIat == null) {
			showTip("mIat == null");
		}
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        //window.requestFeature(Window.FEATURE_NO_TITLE);
		this.setTitle("Recognition result");
        
        
        this.setContentView(R.layout.speech__dialog_recognize);
    
        mTextViewContent = (TextView) this.findViewById(R.id.textViewContent);

        mTextViewContent.setText("Recognition, please wait...");
        
        Button buttonClose = (Button) this.findViewById(R.id.buttonClose);
        buttonClose.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				//RecognizeDialog.this.dismiss();
				RecognizeDialog.this.cancel();
			}
        });
        
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        this.setOnCancelListener(this);
        
        readVoice(mItem.getFilePath());
	}

	@Override
	public void onCancel(DialogInterface arg0) {
		this.dismiss();
		if (mDictationDatabase != null) {
			mDictationDatabase.close();
			mDictationDatabase = null;
		}
	}
	

	private Toast mToast;
	private void showTip(final String str) {
		mToast.setText(str);
		mToast.show();
	}
	private RecognizerListener mRecognizerListener = new RecognizerListener() {

		@Override
		public void onBeginOfSpeech() {
			showTip("Start speaking");
		}

		@Override
		public void onError(SpeechError error) {
		    showTip(error.getPlainDescription(true));
		}

		@Override
		public void onEndOfSpeech() {
			showTip("Speaking stopped");
		}

		@Override
		public void onResult(RecognizerResult results, boolean isLast) {
			Log.d(TAG, results.getResultString());
			printResult(results);

			addDictation(mItem, mResultText, RecognizeDialog.this.lag);
			if (isLast) {

            }
		}

		@Override
		public void onVolumeChanged(int volume, byte[] data) {
			showTip("Speaking, volume: " + volume);
			Log.d(TAG, "Return audio data: "+data.length);
		}

		@Override
		public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
			//	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
			//		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
			//		Log.d(TAG, "session id =" + sid);
			//	}
		}
	};
	
	
	

	private InitListener mInitListener = new InitListener() {

		@Override
		public void onInit(int code) {
			Log.d(TAG, "SpeechRecognizer init() code = " + code);
			if (code != ErrorCode.SUCCESS) {
				showTip("Init failed, error code:" + code);
			}
		}
	};
	
	private String mResultText;
	private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();
	private SpeechRecognizer mIat;
	private int ret = 0;
	
	private void readVoice(String path) {
		mResultText = "";
		mIatResults.clear();
		setParam();
		if (USE_BUFFER) {
			mIat.setParameter(SpeechConstant.AUDIO_SOURCE, "-1");
			// mIat.setParameter(SpeechConstant.AUDIO_SOURCE, "-2");
			// mIat.setParameter(SpeechConstant.ASR_SOURCE_PATH, "sdcard/XXX/XXX.pcm");
			ret = mIat.startListening(mRecognizerListener);
			if (ret != ErrorCode.SUCCESS) {
				showTip("Recognition failed, error code:" + ret);
			} else {
				//byte[] audioData = FucUtil.readAudioFile(this.mAct, "iattest.wav", true);
				//byte[] audioData = FucUtil.readAudioFile(this.mAct, "/mnt/sdcard/01.wav", false);
				byte[] audioData = FucUtil.readAudioFile(this.mAct, path, false);
				if (null != audioData) {
					Log.e(TAG, "=============>length : " + audioData.length);
					showTip("Start audio stream recognition");
					mIat.writeAudio(audioData, 0, audioData.length);
					mIat.stopListening();
				} else {
					mIat.cancel();
					showTip("Read audio stream failed");
				}
			}
		} else {
			mIat.setParameter(SpeechConstant.AUDIO_SOURCE, "-2");
			mIat.setParameter(SpeechConstant.ASR_SOURCE_PATH, path);
			ret = mIat.startListening(mRecognizerListener);
			if (ret != ErrorCode.SUCCESS) {
				showTip("Recognition failed, error code:" + ret);
			} else {
				mIat.stopListening();
			}
		}
	}
	
	private boolean mTranslateEnable = false;
	private String lag = "mandarin"; //mandarin, cantonese, en_us</item>
	private String iat_vadbos_preference = "10000";//default:"4000"/sms:"5000"/other:"4000"
	private String iat_vadeos_preference = "10000";//default:"1000"/sms:"1800"/other:"700"
	private String iat_punc_preference = "1";
	private String mEngineType = SpeechConstant.TYPE_CLOUD;

	public void setParam() {
		mIat.setParameter(SpeechConstant.PARAMS, null);

		mIat.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
		mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");

		
		if (lag.equals("en_us")) {
			mIat.setParameter(SpeechConstant.LANGUAGE, "en_us");
			mIat.setParameter(SpeechConstant.ACCENT, null);
			
			if( mTranslateEnable ){
				mIat.setParameter( SpeechConstant.ORI_LANG, "en" );
				mIat.setParameter( SpeechConstant.TRANS_LANG, "cn" );
			}
		} else {
			mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
			mIat.setParameter(SpeechConstant.ACCENT, lag);
			
			if( mTranslateEnable ){
				mIat.setParameter( SpeechConstant.ORI_LANG, "cn" );
				mIat.setParameter( SpeechConstant.TRANS_LANG, "en" );
			}
		}

		mIat.setParameter(SpeechConstant.VAD_BOS, iat_vadbos_preference);
		
		mIat.setParameter(SpeechConstant.VAD_EOS, iat_vadeos_preference);
		
		mIat.setParameter(SpeechConstant.ASR_PTT, iat_punc_preference);
		
		mIat.setParameter(SpeechConstant.AUDIO_FORMAT,"wav");
		mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory()+"/msc/iat.wav");
	}
	
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

		mResultText = resultBuffer.toString();
		showTip(mResultText);
		this.mTextViewContent.setText("" + mResultText);
	}
	
	public void addDictation(RecordingItem item, String content, String lang) {
		if (mDictationDatabase != null) {
			mDictationDatabase.addItem(item.getId(), content, lang);
		}
	}
}
