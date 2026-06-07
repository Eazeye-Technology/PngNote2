package com.example.sparkchaindemo.ai;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sparkchaindemo.ai.asr.asrActivity;
import com.example.sparkchaindemo.ai.raasr.RAASRActivity;
import com.example.sparkchaindemo.ai.rtasr.RTASRActivity;
import com.example.sparkchaindemo.utils.FileUtils;
import com.hjq.permissions.OnPermission;
import com.hjq.permissions.XXPermissions;
import com.iflytek.sparkchain.core.LogLvl;
import com.iflytek.sparkchain.core.SparkChain;
import com.iflytek.sparkchain.core.SparkChainConfig;
import com.txkj.drawingapp.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class AIMainActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "AEELog";
    private TextView tv_notification;

    private boolean isAuth = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ai_main);
        initView();
    }

    private void initView(){
        findViewById(R.id.ai_main_sdk_init).setOnClickListener(this);
        findViewById(R.id.ai_main_its).setOnClickListener(this);
        findViewById(R.id.ai_main_rtasr).setOnClickListener(this);
        findViewById(R.id.ai_main_tts).setOnClickListener(this);
        findViewById(R.id.ai_main_asr).setOnClickListener(this);
        findViewById(R.id.ai_main_raasr).setOnClickListener(this);
        findViewById(R.id.ai_main_imts).setOnClickListener(this);
        findViewById(R.id.ai_main_ist).setOnClickListener(this);


        tv_notification = findViewById(R.id.ai_main_notification);

    }

    private void showInfo(String text){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv_notification.setText(text);
            }
        });
    }

    private void getPermission(){
        XXPermissions.with(this).permission("android.permission.WRITE_EXTERNAL_STORAGE"
                , "android.permission.READ_EXTERNAL_STORAGE"
                , "android.permission.INTERNET"
                , "android.permission.MANAGE_EXTERNAL_STORAGE").request(new OnPermission() {
            @Override
            public void hasPermission(List<String> granted, boolean all) {
                Log.d(TAG,"Grant permissions:"+all);
                for(int i=0;i<granted.size();i++){
                    Log.d(TAG,"Grant permissions:"+granted.get(i));
                }
                if(all){
                    createWorkDir();
                    SDKInit();
                }
            }

            @Override
            public void noPermission(List<String> denied, boolean quick) {
                if(quick){
                    Log.e(TAG,"onDenied: please manually grant");
                    XXPermissions.startPermissionActivity(AIMainActivity.this,denied);
                }else{
                    Log.e(TAG,"onDenied: grant failed");
                }
            }
        });
    }


    private void SDKInit(){
        Log.d(TAG,"initSDK");
        SparkChainConfig sparkChainConfig = SparkChainConfig.builder();
        sparkChainConfig.appID(getResources().getString(R.string.appid))
                .apiKey(getResources().getString(R.string.apikey))
                .apiSecret(getResources().getString(R.string.apiSecret))
//                .uid("")
//                .logPath("/sdcard/iflytek/AEELog.txt")
                .logLevel(LogLvl.VERBOSE.getValue());

        int ret = SparkChain.getInst().init(getApplicationContext(),sparkChainConfig);
        String result;
        if(ret == 0){
            result = "SDK init successful";
            isAuth = true;
        }else{
            result = "SDK init failed:" + ret;
            isAuth = false;
        }
        Log.d(TAG,result);
        showInfo(result);
    }

    private void jump(Class jumpAct) {
        try {
            Intent intent = new Intent(this, jumpAct);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        SparkChain.getInst().unInit();

    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.ai_main_sdk_init) {
            getPermission();
        } else if (view.getId() == R.id.ai_main_its) {
            if (!isAuth) {
                showInfo("SDK is uninitialized, please initialize SDK");
                return;
            }
//                jump(ITSActivity.class);
        } else if (view.getId() == R.id.ai_main_rtasr) {
            if (!isAuth) {
                showInfo("SDK is uninitialized, please initialize SDK");
                return;
            }
            jump(RTASRActivity.class);
        } else if (view.getId() == R.id.ai_main_tts) {
            if (!isAuth) {
                showInfo("SDK is uninitialized, please initialize SDK");
                return;
            }
//                jump(TTSActivity.class);
        } else if (view.getId() == R.id.ai_main_asr) {
            if (!isAuth) {
                showInfo("SDK is uninitialized, please initialize SDK");
                return;
            }
                jump(asrActivity.class);
        } else if (view.getId() == R.id.ai_main_raasr) {
            if (!isAuth) {
                showInfo("SDK is uninitialized, please initialize SDK");
                return;
            }
            jump(RAASRActivity.class);
        } else if (view.getId() == R.id.ai_main_imts) {
            if (!isAuth) {
                showInfo("SDK is uninitialized, please initialize SDK");
                return;
            }
//                jump(IMTSActivity.class);
        } else if (view.getId() == R.id.ai_main_ist) {
                if(!isAuth){
                    showInfo("SDK is uninitialized, please initialize SDK");
                    return;
                }
//                jump(ISTActivity.class);
        }
    }

    private void createWorkDir()  {
        String path = "/sdcard/iflytek/asr";
        FileUtils.deleteDirectory(path);
        File folder = new File(path);
        boolean success = folder.mkdirs();
        if (success) {
            try {
                copyFilesFromAssets();
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),"Copy failed, please check sdcard permission",Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getApplicationContext(),"Copy failed, please check sdcard permission",Toast.LENGTH_LONG).show();
        }
    }

    private void copyFilesFromAssets() throws IOException {
        String[] fileNames = getAssets().list("");
        if (fileNames != null && fileNames.length > 0) {
            for (String fileName : fileNames) {
                if (fileName.endsWith(".pcm")) {
                    try {
                        InputStream inputStream = getAssets().open(fileName);
                        OutputStream outputStream = new FileOutputStream("/sdcard/iflytek/asr/" + fileName);
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, length);
                        }
                        inputStream.close();
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
