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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.sys.speech.db.SDRecordingsDatabase;
import com.sys.speech.util.JsonParser;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.txkj.drawingapp.R;

public class ListenDialog extends Dialog implements Dialog.OnCancelListener {
	private static final boolean D = true;
	private static final String TAG = "ListenDialog";	
	
	private static final boolean USE_BUFFER = true;
	
	public static final String LANG_CHINESE = "mandarin";
	public static final String LANG_CHINESE_GD = "cantonese";
	public static final String LANG_ENGLISH = "en_us";
	
	private Activity mAct;
	private TextView mTextViewContent;
	private String mMeetingId, mAgendaId;
	private BaseAdapter mAdapter;
	
	public ListenDialog(Activity context, String meetingId, String agendaId, BaseAdapter adapter) {
		super(context);
		this.mAct = context;
		
		mMeetingId = meetingId;
		mAgendaId = agendaId;
		mAdapter = adapter;

		mIat = SpeechRecognizer.createRecognizer(context, mInitListener);

		mIatDialog = new RecognizerDialog(this.getContext(), mInitListener);
		
		mToast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
		
		if (mIat == null) {
			showTip("mIat == null");
		}
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
		this.setTitle("Recognition");
        
        
        this.setContentView(R.layout.speech__dialog_listen);
    
        mTextViewContent = (TextView) this.findViewById(R.id.textViewContent);

        mTextViewContent.setText("Please click the button above to start recognition...");
        
        Button buttonClose = (Button) this.findViewById(R.id.buttonClose);
        buttonClose.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				ListenDialog.this.cancel();
			}
        });
        
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        this.setOnCancelListener(this);
        
        Button btnListen1 = (Button) this.findViewById(R.id.btnListen1);
        Button btnListen2 = (Button) this.findViewById(R.id.btnListen2);
        Button btnListen3 = (Button) this.findViewById(R.id.btnListen3);
        btnListen1.setOnClickListener(new android.view.View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				ListenDialog.this.lag = ListenDialog.LANG_CHINESE;
				readVoice();
			}
        });
        btnListen2.setOnClickListener(new android.view.View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				ListenDialog.this.lag = ListenDialog.LANG_CHINESE_GD;
				readVoice();
			}
        });
        btnListen3.setOnClickListener(new android.view.View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				ListenDialog.this.lag = ListenDialog.LANG_ENGLISH;
				readVoice();
			}
        });
	}

	@Override
	public void onCancel(DialogInterface arg0) {
		this.dismiss();
		SDRecordingsDatabase mDatabase = new SDRecordingsDatabase(this.getContext(), null);
		mDatabase.addRecording("",
				"", 0, this.mMeetingId, this.mAgendaId, "text", 
				this.mResultText != null ? this.mResultText.trim() : "");
		mDatabase.close();
		if (mAdapter != null) {
			mAdapter.notifyDataSetChanged();
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

			//addDictation(mItem, mResultText, ListenDialog.this.lag);
			if (isLast) {

            }
		}

		@Override
		public void onVolumeChanged(int volume, byte[] data) {
			showTip("Speaking, volume : " + volume);
			Log.d(TAG, "return audio data : "+data.length);
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
				showTip("Init failed, error: " + code);
			}
		}
	};
	
	private String mResultTextOld = "";
	private String mResultText = "";
	private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();
	private SpeechRecognizer mIat;
	private int ret = 0;
	private RecognizerDialog mIatDialog;
	
	private void readVoice() {
		mResultTextOld = mResultText + "\n";
		//mResultText = "";
		mIatResults.clear();

		setParam();
		boolean isShowDialog = true;
		if (isShowDialog) {
			mIatDialog.setListener(mRecognizerDialogListener);
			mIatDialog.show();
			showTip("Please start speaking");
		} else {
			ret = mIat.startListening(mRecognizerListener);
			if (ret != ErrorCode.SUCCESS) {
				showTip("Listen failed, error code: " + ret);
			} else {
				showTip("Please start speaking");
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
        if (true) {
            //https://developer.aliyun.com/article/454733
            mIat.setParameter(SpeechConstant.PARAMS, null);
            mIat.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
            mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");
            mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
            mIat.setParameter(SpeechConstant.ACCENT, "zh_cn");
            mIat.setParameter(SpeechConstant.VAD_BOS, "4000");
            mIat.setParameter(SpeechConstant.VAD_EOS, "1000");
            mIat.setParameter(SpeechConstant.ASR_PTT, "1");
        } else if (false) {
            //https://developer.aliyun.com/article/454733
            mIat.setParameter(SpeechConstant.PARAMS, null);
            mIat.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
            mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");
            mIat.setParameter(SpeechConstant.LANGUAGE, "en_us");
            mIat.setParameter(SpeechConstant.ACCENT, "mandarin");  //https://console.xfyun.cn/services/iat
            mIat.setParameter(SpeechConstant.VAD_BOS, "4000");
            mIat.setParameter(SpeechConstant.VAD_EOS, "1000");
            mIat.setParameter(SpeechConstant.ASR_PTT, "1");
        } else {
            mIat.setParameter(SpeechConstant.PARAMS, null);

            mIat.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
            mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");


            if (lag.equals("en_us")) {
                mIat.setParameter(SpeechConstant.LANGUAGE, "en_us");
                mIat.setParameter(SpeechConstant.ACCENT, null);

                if (mTranslateEnable) {
                    mIat.setParameter(SpeechConstant.ORI_LANG, "en");
                    mIat.setParameter(SpeechConstant.TRANS_LANG, "cn");
                }
            } else {
                mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
                mIat.setParameter(SpeechConstant.ACCENT, lag);

                if (mTranslateEnable) {
                    mIat.setParameter(SpeechConstant.ORI_LANG, "cn");
                    mIat.setParameter(SpeechConstant.TRANS_LANG, "en");
                }
            }

            mIat.setParameter(SpeechConstant.VAD_BOS, iat_vadbos_preference);

            mIat.setParameter(SpeechConstant.VAD_EOS, iat_vadeos_preference);

            mIat.setParameter(SpeechConstant.ASR_PTT, iat_punc_preference);

            //		mIat.setParameter(SpeechConstant.AUDIO_FORMAT,"wav");
            //		mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory()+"/msc/iat.wav");
        }
    }
	
	private RecognizerDialogListener mRecognizerDialogListener = new RecognizerDialogListener() {
		public void onResult(RecognizerResult results, boolean isLast) {
			printResult(results);
		}

		public void onError(SpeechError error) {
			showTip(error.getPlainDescription(true));
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

		mResultText = mResultTextOld + resultBuffer.toString();
		showTip(mResultText);
		this.mTextViewContent.setText("" + mResultText);
	}
}
