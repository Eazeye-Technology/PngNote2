package com.txkj.drawingapp.activity;

import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * WAV file writer utility.
 * Wraps raw PCM audio data into standard WAV format.
 */
public class WavFileWriter {

    // Standard WAV PCM parameters
    private static final int SAMPLE_RATE = 44100;
    private static final int BITS_PER_SAMPLE = 16;
    private static final int NUM_CHANNELS = 1;
    private static final int BYTE_RATE = SAMPLE_RATE * NUM_CHANNELS * BITS_PER_SAMPLE / 8;
    private static final int BLOCK_ALIGN = NUM_CHANNELS * BITS_PER_SAMPLE / 8;

    /**
     * Generate an auto-named filename, format: audio_20260622_151516.wav
     */
    public static String generateFileName() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        return "audio_" + sdf.format(new Date()) + ".wav";
    }

    /**
     * Write PCM audio data to an OutputStream in WAV format.
     *
     * @param out       Target output stream
     * @param pcmData   Raw PCM byte data (16-bit signed little-endian, mono, 44100Hz)
     * @throws IOException Write exception
     */
    public static void writeWav(OutputStream out, byte[] pcmData) throws IOException {
        int dataSize = pcmData.length;

        // WAV file header (44 bytes)
        ByteBuffer header = ByteBuffer.allocate(44);
        header.order(ByteOrder.LITTLE_ENDIAN);

        // RIFF chunk
        header.put(new byte[]{'R', 'I', 'F', 'F'});
        header.putInt(36 + dataSize);               // ChunkSize
        header.put(new byte[]{'W', 'A', 'V', 'E'});

        // fmt sub-chunk
        header.put(new byte[]{'f', 'm', 't', ' '});
        header.putInt(16);                           // Subchunk1Size (PCM)
        header.putShort((short) 1);                  // AudioFormat = PCM
        header.putShort((short) NUM_CHANNELS);
        header.putInt(SAMPLE_RATE);
        header.putInt(BYTE_RATE);
        header.putShort((short) BLOCK_ALIGN);
        header.putShort((short) BITS_PER_SAMPLE);

        // data sub-chunk
        header.put(new byte[]{'d', 'a', 't', 'a'});
        header.putInt(dataSize);

        out.write(header.array());
        out.write(pcmData);
        out.flush();
    }

    /**
     * Generate sample audio data (1-second 440Hz sine wave, 16-bit mono 44100Hz).
     * In real projects, replace this with actual audio data source.
     */
    public static byte[] generateSampleAudio() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        double duration = 1.0; // 1 second
        int totalSamples = (int) (SAMPLE_RATE * duration);
        double frequency = 440.0; // A4 pitch

        ByteBuffer buffer = ByteBuffer.allocate(2);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        for (int i = 0; i < totalSamples; i++) {
            double t = i / (double) SAMPLE_RATE;
            double amplitude = 0.5; // Half amplitude to avoid clipping
            short sample = (short) (amplitude * Short.MAX_VALUE * Math.sin(2.0 * Math.PI * frequency * t));
            buffer.clear();
            buffer.putShort(sample);
            baos.write(buffer.array(), 0, 2);
        }

        return baos.toByteArray();
    }
}

