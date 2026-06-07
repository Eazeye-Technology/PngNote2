package com.example.sparkchaindemo.utils;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.util.Log;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;

public class AudioTrackManager {
    private AudioTrack mAudioTrack;
    private DataInputStream mDis;
    private Thread mRecordThread;
    private boolean isStart = false;
    private volatile static AudioTrackManager mInstance;

    private static final int mStreamType = AudioManager.STREAM_MUSIC;
    public static final int mSampleRateIn16KHz=16000 ;
    public static final int mSampleRateIn24KHz=24000 ;
    private static final int mChannelConfig= AudioFormat.CHANNEL_CONFIGURATION_MONO;
    private static final int mAudioFormat=AudioFormat.ENCODING_PCM_16BIT;
    private int mMinBufferSize;
    private static int mMode = AudioTrack.MODE_STREAM;
    private int mSampleRate = 16000;

    public enum sampleRateType {
        SAMPLE_RATE_16k,
        SAMPLE_RATE_24k
    }


    public AudioTrackManager() {
        initData();
    }

    private void initData(){
        Log.d("AEE","AudioTrackManager:sampleRate="+mSampleRate);
        mMinBufferSize = AudioTrack.getMinBufferSize(mSampleRate,mChannelConfig, mAudioFormat);
        mAudioTrack = new AudioTrack(mStreamType, mSampleRate,mChannelConfig,
                mAudioFormat,mMinBufferSize,mMode);
    }

    public void setSampleRate(sampleRateType sampleRate){
        switch(sampleRate){
            case SAMPLE_RATE_16k:
                mSampleRate = mSampleRateIn16KHz;
                break;
            case SAMPLE_RATE_24k:
                mSampleRate = mSampleRateIn24KHz;
                break;
        }
    }

    public static AudioTrackManager getInstance() {
        if (mInstance == null) {
            synchronized (AudioTrackManager.class) {
                if (mInstance == null) {
                    mInstance = new AudioTrackManager();
                }
            }
        }
        return mInstance;
    }

    private void destroyThread() {
        try {
            isStart = false;
            if (null != mRecordThread && Thread.State.RUNNABLE == mRecordThread.getState()) {
                try {
                    Thread.sleep(500);
                    mRecordThread.interrupt();
                } catch (Exception e) {
                    mRecordThread = null;
                }
            }
            mRecordThread = null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mRecordThread = null;
        }
    }

    private void startThread() {
        destroyThread();
        isStart = true;
        if (mRecordThread == null) {
            mRecordThread = new Thread(playRunnable);
            mRecordThread.start();
        }
    }

    Runnable playRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
                byte[] tempBuffer = new byte[mMinBufferSize];
                int readCount = 0;
                while (mDis.available() > 0) {
                    readCount= mDis.read(tempBuffer);
                    if (readCount == AudioTrack.ERROR_INVALID_OPERATION || readCount == AudioTrack.ERROR_BAD_VALUE) {
                        continue;
                    }
                    if (readCount != 0 && readCount != -1) {
                        if(mAudioTrack.getState() == mAudioTrack.STATE_UNINITIALIZED){
                            initData();
                        }
                        mAudioTrack.play();
                        mAudioTrack.write(tempBuffer, 0, readCount);
                    }
                }
              stopPlay();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    };

    private void setPath(String path) throws Exception {
        File file = new File(path);
        mDis = new DataInputStream(new FileInputStream(file));
    }

    public void startPlay(String path) {
        try {
            setPath(path);
            startThread();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopPlay() {
        try {
            destroyThread();
            if (mAudioTrack != null) {
                if (mAudioTrack.getState() == AudioRecord.STATE_INITIALIZED) {
                    mAudioTrack.stop();
                }
                if (mAudioTrack != null) {
                    mAudioTrack.release();
                }
            }
            if (mDis != null) {
                mDis.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getPlayState(){
        if (mAudioTrack!= null){
           return mAudioTrack.getPlayState();
        }
        return AudioTrack.PLAYSTATE_STOPPED;
    }

}