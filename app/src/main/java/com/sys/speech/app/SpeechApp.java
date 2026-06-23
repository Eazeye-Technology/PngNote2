package com.sys.speech.app;

import android.app.Application;
import android.content.Context;
import android.os.Environment;

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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;

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
        if (getAcraEnableFile(APPNAME_NEW2)) {
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

    public final static String APPNAME_NEW2 = "txkjnote2";
    private final static String PREFNAME = "acrapref.json";
    public boolean getAcraEnableFile(String appName) {
        String prefName = PREFNAME;
        String recentFiles = "";
        try {
            String rootPath = null;
            rootPath = new File(Environment.getExternalStorageDirectory(), appName).toString();
            boolean kkk = new File(rootPath).mkdirs();
            if (new File(rootPath, "" + prefName).exists()) {
                InputStream fis = new FileInputStream(new File(rootPath, "" + prefName));
                InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
                BufferedReader reader = new BufferedReader(isr);
                StringBuffer recentFilesBuffer = new StringBuffer();
                while (true) {
                    String line = reader.readLine();
                    if (line != null) {
                        recentFilesBuffer.append(line);
                        recentFilesBuffer.append("\n");
                    } else {
                        break;
                    }
                }
                recentFiles = recentFilesBuffer.toString();
                reader.close();
                isr.close();
                fis.close();
            }
        } catch (Throwable eee) {
            eee.printStackTrace();
        }
        //Log.e(TAG, "recentFiles: " + recentFiles);
        JSONObject item = new JSONObject();
        try {
            item = new JSONObject(recentFiles);
            return item.optBoolean("acraEnable", false);
        } catch (Throwable eee) {
            eee.printStackTrace();
        }
        return false;
    }

    public void setAcraEnableFile(String appName, boolean acraEnable) {
        String prefName = PREFNAME;
        String recentFiles = "";
        try {
            String rootPath = null;
            rootPath = new File(Environment.getExternalStorageDirectory(), appName).toString();
            boolean kkk = new File(rootPath).mkdirs();
            if (new File(rootPath, "" + prefName).exists()) {
                InputStream fis = new FileInputStream(new File(rootPath, "" + prefName));
                InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
                BufferedReader reader = new BufferedReader(isr);
                StringBuffer recentFilesBuffer = new StringBuffer();
                while (true) {
                    String line = reader.readLine();
                    if (line != null) {
                        recentFilesBuffer.append(line);
                        recentFilesBuffer.append("\n");
                    } else {
                        break;
                    }
                }
                recentFiles = recentFilesBuffer.toString();
                reader.close();
                isr.close();
                fis.close();
            }
        } catch (Throwable eee) {
            eee.printStackTrace();
        }
        //Log.e(TAG, "recentFiles: " + recentFiles);
        JSONObject item = new JSONObject();
        try {
            item = new JSONObject(recentFiles);
            item.put("acraEnable", acraEnable);
        } catch (Throwable eee) {
            eee.printStackTrace();
            if (item != null) {
                try {
                    item.put("acraEnable", acraEnable);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        try {
            String rootPath = null;
            rootPath = new File(Environment.getExternalStorageDirectory(), appName).toString();
            FileOutputStream fout = new FileOutputStream(new File(rootPath, "" + prefName));
            OutputStreamWriter osw = new OutputStreamWriter(fout, "UTF-8");
            BufferedWriter writer = new BufferedWriter(osw);
            writer.write(item.toString());
            writer.flush();
            writer.close();
            osw.close();
            fout.close();
        } catch (Throwable eee) {
            eee.printStackTrace();
        }
    }
}
