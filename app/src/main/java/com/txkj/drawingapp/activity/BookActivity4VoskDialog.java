package com.txkj.drawingapp.activity;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.hjq.permissions.OnPermission;
import com.hjq.permissions.XXPermissions;

import org.json.JSONException;
import org.json.JSONObject;
import org.vosk.LibVosk;
import org.vosk.LogLevel;
import org.vosk.Model;
import org.vosk.Recognizer;
import org.vosk.android.RecognitionListener;
import org.vosk.android.SpeechService;
import org.vosk.android.SpeechStreamService;
import org.vosk.android.StorageService;

import java.io.IOException;
import java.util.List;

public class BookActivity4VoskDialog implements RecognitionListener {
    private static final String TAG = "Vosk";
    private static final boolean DEBUG = false;

    static private final int STATE_START = 0;
    static private final int STATE_READY = 1;
    static private final int STATE_DONE = 2;
    static private final int STATE_FILE = 3;
    static private final int STATE_MIC = 4;

    /* Used to handle permission request */
    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;

    private Model model;
    private SpeechService speechService;
    private SpeechStreamService speechStreamService;
//    private TextView resultView;


    private Activity mAct;
    public BookActivity4VoskDialog(Activity act) {
        this.mAct = act;
        onCreate();
    }

    private void onCreate() {
        LibVosk.setLogLevel(LogLevel.INFO);

        // Check if user has given permission to record audio, init the model after permission is granted
        if (false) {
            int permissionCheck = ContextCompat.checkSelfPermission(mAct, Manifest.permission.RECORD_AUDIO);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(mAct, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSIONS_REQUEST_RECORD_AUDIO);
            } else {
                initModel();
            }
        } else {
            XXPermissions.with(mAct).permission(Manifest.permission.RECORD_AUDIO).request(new OnPermission() {
                @Override
                public void hasPermission(List<String> granted, boolean all) {
                    Log.d(TAG,"Permission success:"+all);
                    for(int i=0;i<granted.size();i++){
                        Log.d(TAG,"Granted："+granted.get(i));
                    }
                    if(all){
                        initModel();
                    } else {
                        BookActivity4Utils.btn_audio_start_setEnabled(mAct, true);
                    }
                }

                @Override
                public void noPermission(List<String> denied, boolean quick) {
                    if(quick){
                        Log.e(TAG,"onDenied:no permission, require manual");
                        XXPermissions.startPermissionActivity(mAct, denied);
                    }else{
                        Log.e(TAG,"onDenied:permission failed");
                    }
                    BookActivity4Utils.btn_audio_start_setEnabled(mAct, true);
                }
            });
        }
    }


    private void initModel() {
        StorageService.unpack(mAct, "vendor/assets/model-en-us", "model",
                (model) -> {
                    this.model = model;
                    setUiState(STATE_READY);
                    BookActivity4Utils.btn_audio_start_setEnabled(mAct, false);
                    recognizeMicrophone2();
                },
                (exception) -> {
                    exception.printStackTrace();
                    setErrorState("Failed to unpack the model" + exception.getMessage());
                    BookActivity4Utils.btn_audio_start_setEnabled(mAct, true);
                });
    }


//    @Override
//    public void onRequestPermissionsResult(int requestCode,
//                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//        if (requestCode == PERMISSIONS_REQUEST_RECORD_AUDIO) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // Recognizer initialization is a time-consuming and it involves IO,
//                // so we execute it in async task
//                initModel();
//            } else {
//                finish();
//            }
//        }
//    }

    //@Override
    public void onDestroy() {
        //super.onDestroy();

        if (speechService != null) {
            speechService.stop();
            speechService.shutdown();
        }

        if (speechStreamService != null) {
            speechStreamService.stop();
        }
    }

    //you can delete assets/model-en-us/uuid file to test failed situation
    public void onClick_audio() {
//        recognizeMicrophone2(); //don't do this, move to callback
        //do nothing here
    }

    public void onClick_stop() {
        onDestroy();
        BookActivity4Utils.btn_audio_start_setEnabled(mAct, true);
    }

    @Override
    public void onResult(String hypothesis) {
//        resultView.append(hypothesis + "\n");
        if (DEBUG) {
            BookActivity4Utils.tv_result_setText(mAct, "onResult == " + hypothesis, hypothesis, true, false);
        } else {
            String result = null;
            try {
                JSONObject resultJson = new JSONObject(hypothesis);
                result = resultJson.optString("text");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (result != null && !result.isEmpty()) {
                BookActivity4Utils.tv_result_setText(mAct, result, result, true, false);
            }
        }
    }

    @Override
    public void onFinalResult(String hypothesis) {
//        resultView.append(hypothesis + "\n");
        if (DEBUG) {
            BookActivity4Utils.tv_result_setText(mAct, "onFinalResult == " + hypothesis, hypothesis, true, false);
        }
        setUiState(STATE_DONE);
        if (speechStreamService != null) {
            speechStreamService = null;
        }
    }

    @Override
    public void onPartialResult(String hypothesis) {
//        resultView.append(hypothesis + "\n");
        if (DEBUG) {
            BookActivity4Utils.tv_result_setText(mAct, "onPartialResult == " + hypothesis, hypothesis, true, false);
        } else {
            String result = null;
            try {
                JSONObject resultJson = new JSONObject(hypothesis);
                result = resultJson.optString("partial");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (result != null && !result.isEmpty()) {
                BookActivity4Utils.tv_result_setText(mAct, result, result, false, false);
            }
        }
    }

    @Override
    public void onError(Exception e) {
        setErrorState(e.getMessage());
    }

    @Override
    public void onTimeout() {
        setUiState(STATE_DONE);
    }

    private void setUiState(int state) {
//        switch (state) {
//            case STATE_START:
//                resultView.setText(R.string.preparing);
//                resultView.setMovementMethod(new ScrollingMovementMethod());
//                findViewById(R.id.recognize_file).setEnabled(false);
//                findViewById(R.id.recognize_mic).setEnabled(false);
//                findViewById(R.id.pause).setEnabled((false));
//                break;
//            case STATE_READY:
//                resultView.setText(R.string.ready);
//                ((Button) findViewById(R.id.recognize_mic)).setText(R.string.recognize_microphone);
//                findViewById(R.id.recognize_file).setEnabled(true);
//                findViewById(R.id.recognize_mic).setEnabled(true);
//                findViewById(R.id.pause).setEnabled((false));
//                break;
//            case STATE_DONE:
//                ((Button) findViewById(R.id.recognize_file)).setText(R.string.recognize_file);
//                ((Button) findViewById(R.id.recognize_mic)).setText(R.string.recognize_microphone);
//                findViewById(R.id.recognize_file).setEnabled(true);
//                findViewById(R.id.recognize_mic).setEnabled(true);
//                findViewById(R.id.pause).setEnabled((false));
//                ((ToggleButton) findViewById(R.id.pause)).setChecked(false);
//                break;
//            case STATE_FILE:
//                ((Button) findViewById(R.id.recognize_file)).setText(R.string.stop_file);
//                resultView.setText(getString(R.string.starting));
//                findViewById(R.id.recognize_mic).setEnabled(false);
//                findViewById(R.id.recognize_file).setEnabled(true);
//                findViewById(R.id.pause).setEnabled((false));
//                break;
//            case STATE_MIC:
//                ((Button) findViewById(R.id.recognize_mic)).setText(R.string.stop_microphone);
//                resultView.setText(getString(R.string.say_something));
//                findViewById(R.id.recognize_file).setEnabled(false);
//                findViewById(R.id.recognize_mic).setEnabled(true);
//                findViewById(R.id.pause).setEnabled((true));
//                break;
//            default:
//                throw new IllegalStateException("Unexpected value: " + state);
//        }
    }

    private void setErrorState(String message) {
//        resultView.setText(message);


//        ((Button) findViewById(R.id.recognize_mic)).setText(R.string.recognize_microphone);
//        findViewById(R.id.recognize_file).setEnabled(false);
//        findViewById(R.id.recognize_mic).setEnabled(false);
    }

//    private void recognizeFile() {
//        if (speechStreamService != null) {
//            setUiState(STATE_DONE);
//            speechStreamService.stop();
//            speechStreamService = null;
//        } else {
//            setUiState(STATE_FILE);
//            try {
//                Recognizer rec = new Recognizer(model, 16000.f, "[\"one zero zero zero one\", " +
//                        "\"oh zero one two three four five six seven eight nine\", \"[unk]\"]");
//
//                InputStream ais = mAct.getAssets().open(
//                        "10001-90210-01803.wav");
//                if (ais.skip(44) != 44) throw new IOException("File too short");
//
//                speechStreamService = new SpeechStreamService(rec, ais, 16000);
//                speechStreamService.start(this);
//            } catch (IOException e) {
//                setErrorState(e.getMessage());
//            }
//        }
//    }

    private void recognizeMicrophone() {
        if (speechService != null) {
            setUiState(STATE_DONE);
            speechService.stop();
            speechService = null;
        } else {
            recognizeMicrophone2();
        }
    }

    private void recognizeMicrophone2() {
        setUiState(STATE_MIC);
        try {
            Recognizer rec = new Recognizer(model, 16000.0f);
            speechService = new SpeechService(rec, 16000.0f);
            speechService.startListening(this);
        } catch (IOException e) {
            setErrorState(e.getMessage());
        }
    }


    private void pause(boolean checked) {
        if (speechService != null) {
            speechService.setPause(checked);
        }
    }

}
