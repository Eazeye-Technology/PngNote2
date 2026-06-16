package com.sys.speech.app;

import android.app.Application;
import android.content.Context;

//import com.iflytek.cloud.SpeechUtility;
import com.txkj.drawingapp.R;

import org.acra.ACRA;
import org.acra.BuildConfig;
import org.acra.config.CoreConfigurationBuilder;
import org.acra.config.DialogConfiguration;
import org.acra.config.DialogConfigurationBuilder;
import org.acra.config.HttpSenderConfiguration;
import org.acra.config.HttpSenderConfigurationBuilder;
import org.acra.config.ToastConfigurationBuilder;
import org.acra.data.StringFormat;
import org.acra.sender.HttpSender;

public class SpeechApp extends Application {
	@Override
	public void onCreate() {
//		SpeechUtility.createUtility(SpeechApp.this, "appid=" + getString(R.string.appid));
		super.onCreate();
	}

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        // The following line triggers the initialization of ACRA
//        CoreConfigurationBuilder builder = new CoreConfigurationBuilder(this);
//        builder.setBuildConfigClass(BuildConfig.class).setReportFormat(StringFormat.JSON);
//        builder.getPluginConfigurationBuilder(HttpSenderConfigurationBuilder.class)
//                .setUri("http://192.168.1.118:8080/AcraServiceDemo/CrashApiAction")
//                .setHttpMethod(HttpSender.Method.POST)
//                .setEnabled(true);
//        ACRA.init(this, builder);

        //need android:usesCleartextTraffic="true"
        String URL = "http://43.106.83.57:3456/report";
        ACRA.init(this, new CoreConfigurationBuilder()
                //core configuration:
                .withBuildConfigClass(BuildConfig.class)
                .withReportFormat(StringFormat.JSON)
                .withPluginConfigurations(
                        //each plugin you chose above can be configured with its builder like this:
//                        new ToastConfigurationBuilder()
//                                .withText(getString(R.string.acra_toast_text))
//                                .build()
                        new HttpSenderConfigurationBuilder()
                                .withUri(URL)
                                .withHttpMethod(HttpSender.Method.POST)
                                .withEnabled(true)
                                .build(),
                        new DialogConfigurationBuilder()
                                .withText("It looks like the application has crashed. Tap OK to send a report.")// to " + URL + " .")
                                .build()
                )
        );
    }
}
