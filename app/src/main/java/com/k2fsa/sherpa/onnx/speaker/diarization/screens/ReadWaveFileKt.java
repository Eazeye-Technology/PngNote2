// ReadWaveFileKt.java
package com.k2fsa.sherpa.onnx.speaker.diarization.screens;

import android.content.Context;
import android.media.MediaCodec;
import android.media.MediaCrypto;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.net.Uri;
import android.view.Surface;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import kotlin.Metadata;
import kotlin.collections.ArraysKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;

public final class ReadWaveFileKt {
    @NotNull
    public static final WaveData readUri(@NotNull Context context, @NotNull Uri uri) {
        Intrinsics.checkNotNullParameter(context, "context");
        Intrinsics.checkNotNullParameter(uri, "uri");
        MediaExtractor extractor = new MediaExtractor();
        try {
            extractor.setDataSource(context, uri, (Map)null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        List<float[]> samplesList = new ArrayList<float[]>();
        int i = 0;

        for (int var5 = extractor.getTrackCount(); i < var5; ++i) {
            MediaFormat var10000 = extractor.getTrackFormat(i);
            Intrinsics.checkNotNullExpressionValue(var10000, "getTrackFormat(...)");
            MediaFormat format = var10000;
            String mime = format.getString(MediaFormat.KEY_MIME);
            if (mime != null ? mime.startsWith("audio/") : false) {
                extractor.selectTrack(i);
                int encoding = -1;

                try {
                    encoding = format.getInteger("pcm-encoding");
                } catch (Exception var22) {
                }

                if (encoding != 2) {
                    return new WaveData((Integer)null, (float[])null, "We support only 16-bit encoded wave files", 3, (DefaultConstructorMarker)null);
                }

                int sampleRate = format.getInteger("sample-rate");
                MediaCodec var32 = null;
                try {
                    var32 = MediaCodec.createDecoderByType(mime);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                Intrinsics.checkNotNullExpressionValue(var32, "createDecoderByType(...)");
                MediaCodec decoder = var32;
                decoder.configure(format, (Surface)null, (MediaCrypto)null, 0);
                decoder.start();
                ByteBuffer[] var33 = decoder.getInputBuffers();
                Intrinsics.checkNotNullExpressionValue(var33, "getInputBuffers(...)");
                ByteBuffer[] inputBuffers = var33;
                var33 = decoder.getOutputBuffers();
                Intrinsics.checkNotNullExpressionValue(var33, "getOutputBuffers(...)");
                ByteBuffer[] outputBuffers = var33;
                MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
                boolean eof = false;
                int outputBufferIndex = -1;

                while(true) {
                    if (!eof) {
                        int inputBufferIndex = decoder.dequeueInputBuffer(10000L);
                        if (inputBufferIndex > 0) {
                            int size = extractor.readSampleData(inputBuffers[inputBufferIndex], 0);
                            if (size < 0) {
                                decoder.queueInputBuffer(inputBufferIndex, 0, 0, 0L, 4);
                                eof = true;
                            } else {
                                decoder.queueInputBuffer(inputBufferIndex, 0, size, extractor.getSampleTime(), 0);
                                extractor.advance();
                            }
                        }
                    }

                    if (outputBufferIndex >= 0) {
                        outputBuffers[outputBufferIndex].position(0);
                    }

                    outputBufferIndex = decoder.dequeueOutputBuffer(info, 10000L);
                    if (outputBufferIndex >= 0) {
                        if (info.flags != 0) {
                            decoder.stop();
                            decoder.release();
                            int k = 0;

                            for (float[] s : samplesList) {
                                k += s.length;
                            }

                            if (k == 0) {
                                return new WaveData((Integer)null, (float[])null, "Failed to read selected file", 3, (DefaultConstructorMarker)null);
                            }

                            float[] ans = new float[k];
                            k = 0;

                            for (float[] s : samplesList) {
                                //ArraysKt.copyInto$default(s, ans, k, 0, 0, 12, (Object)null);
                                System.arraycopy(s, 0, ans, k, s.length);
                                k += s.length;
                            }

                            return new WaveData(sampleRate, ans, (String)null, 4, (DefaultConstructorMarker)null);
                        }

                        ByteBuffer buffer = outputBuffers[outputBufferIndex];
                        byte[] chunk = new byte[info.size];
                        buffer.get(chunk);
                        buffer.clear();
                        int numSamples = info.size / 2;
                        float[] samples = new float[numSamples];

                        for(int k = 0; k < numSamples; ++k) {
                            float s = (float)chunk[2 * k] + (float)chunk[2 * k + 1] * 256.0F;
                            samples[k] = s / 32768.0F;
                        }

                        samplesList.add(samples);
                        decoder.releaseOutputBuffer(outputBufferIndex, false);
                    } else if (outputBufferIndex == -3) {
                        var33 = decoder.getOutputBuffers();
                        Intrinsics.checkNotNullExpressionValue(var33, "getOutputBuffers(...)");
                        outputBuffers = var33;
                    }
                }
            }
        }

        extractor.release();
        return new WaveData((Integer)null, (float[])null, "not an audio file", 3, (DefaultConstructorMarker)null);
    }
}
