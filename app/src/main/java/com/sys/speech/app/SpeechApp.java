package com.sys.speech.app;

import android.app.Application;

//import com.iflytek.cloud.SpeechUtility;
import com.txkj.drawingapp.R;

public class SpeechApp extends Application {
	@Override
	public void onCreate() {
//		SpeechUtility.createUtility(SpeechApp.this, "appid=" + getString(R.string.appid));
		super.onCreate();
	}
}
