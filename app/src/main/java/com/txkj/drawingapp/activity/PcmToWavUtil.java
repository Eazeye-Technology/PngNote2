package com.txkj.drawingapp.activity;

import android.media.AudioFormat;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

//https://juejin.cn/post/7067475026882068494?from=search-suggest
public class PcmToWavUtil {
    private int mSampleRate;
    private int mChannelConfig;
    private int mAudioFormat;

    public PcmToWavUtil(int sampleRate, int channelConfig, int audioFormat) {
        this.mSampleRate = sampleRate;
        this.mChannelConfig = channelConfig;
        this.mAudioFormat = audioFormat;
    }

    public void pcmToWav(String pcmPath, String wavPath) {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            fis = new FileInputStream(pcmPath);
            fos = new FileOutputStream(wavPath);

            // 1. 写入 WAV 头
            byte[] header = generateWavHeader(fis.available());
            fos.write(header);

            // 2. 写入 PCM 音频数据
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) != -1) {
                fos.write(buffer, 0, length);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try { if (fis != null) fis.close(); } catch (IOException e) { e.printStackTrace(); }
            try { if (fos != null) fos.close(); } catch (IOException e) { e.printStackTrace(); }
        }
    }

    private byte[] generateWavHeader(long totalAudioLen) {
        // 计算各字段
        long totalDataLen = totalAudioLen + 36; // 音频数据总长度 + 头部长度（不含 RIFF 头部自身）
        long byteRate = mSampleRate * getChannelCount() * getBitsPerSample() / 8; // 字节率

        byte[] header = new byte[44];
        header[0] = 'R';  // RIFF
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff);
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        header[8] = 'W';  // WAVE
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        header[12] = 'f'; // 'fmt ' chunk
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';
        header[16] = 16;  // Sub-chunk size
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        header[20] = 1;   // Audio format, PCM = 1 (little-endian)
        header[21] = 0;
        header[22] = (byte) getChannelCount(); // Number of channels: 1 for mono, 2 for stereo
        header[23] = 0;
        header[24] = (byte) (mSampleRate & 0xff); // Sample rate (Hz)
        header[25] = (byte) ((mSampleRate >> 8) & 0xff);
        header[26] = (byte) ((mSampleRate >> 16) & 0xff);
        header[27] = (byte) ((mSampleRate >> 24) & 0xff);
        header[28] = (byte) (byteRate & 0xff); // Byte rate (Hz)
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        header[32] = (byte) (getChannelCount() * getBitsPerSample() / 8); // Block align
        header[33] = 0;
        header[34] = (byte) getBitsPerSample(); // Bits per sample (16 or 8)
        header[35] = 0;
        header[36] = 'd'; // data chunk
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (totalAudioLen & 0xff);
        header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
        header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
        header[43] = (byte) ((totalAudioLen >> 24) & 0xff);
        return header;
    }

    private int getChannelCount() {
        return mChannelConfig == AudioFormat.CHANNEL_IN_STEREO ? 2 : 1;
    }

    private int getBitsPerSample() {
        return mAudioFormat == AudioFormat.ENCODING_PCM_16BIT ? 16 : 8;
    }
}
/*
// 1. 配置参数
private static final int SAMPLE_RATE = 44100; // 采样率，推荐44100Hz[reference:3][reference:4]
private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO; // 音频通道：单声道[reference:5][reference:6]
private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT; // 音频数据格式，推荐16bit[reference:7][reference:8]

// 根据配置计算最小缓冲区大小
int bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT);
// 2. 创建 AudioRecord 对象
AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT, bufferSize);
audioRecord.startRecording();

// 3. 在后台线程循环读取数据
isRecording = true;
byte[] buffer = new byte[bufferSize];
File pcmFile = new File(getExternalFilesDir(null), "audio_record.pcm");
try (FileOutputStream fos = new FileOutputStream(pcmFile)) {
    while (isRecording) {
        int readSize = audioRecord.read(buffer, 0, buffer.length);
        if (readSize > 0) {
            fos.write(buffer, 0, readSize); // 将 PCM 数据写入文件
        }
    }
} catch (IOException e) {
    e.printStackTrace();
}

// 4. 停止录音并释放资源
audioRecord.stop();
audioRecord.release();
audioRecord = null;
 */