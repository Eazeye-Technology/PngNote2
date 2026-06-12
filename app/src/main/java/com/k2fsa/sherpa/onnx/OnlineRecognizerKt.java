package com.k2fsa.sherpa.onnx;

import android.content.Context;

import com.example.sherpaasr.data.ModelRepository;

import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public final class OnlineRecognizerKt {
    @NotNull
    public static final String getFolder(@NotNull String modelDir, int folderType, @NotNull Context context) {
//        Intrinsics.checkNotNullParameter(modelDir, "modelDir");
//        Intrinsics.checkNotNullParameter(context, "context");
        if (folderType == 0) {
            return "" + modelDir;
        } else if (ModelRepository.USE_EXTERNAL_FILES) {
            StringBuilder var10000 = new StringBuilder();
            File var10001 = context.getExternalFilesDir((String)null);
            return var10000.append(var10001 != null ? var10001.getAbsolutePath() : null).append(File.separator).append(ModelRepository.MODELS_FOLDER_NAME).append(File.separator).append(modelDir).toString();
        } else {
            StringBuilder var10000 = new StringBuilder();
            File var10001 = context.getFilesDir();
            return var10000.append(var10001 != null ? var10001.getAbsolutePath() : null).append(File.separator).append(ModelRepository.MODELS_FOLDER_NAME).append(File.separator).append(modelDir).toString();
        }
    }

    //if folderType == 1, loaded from downloaded files, if folderType == 0, loaded from assets files
    @Nullable
    public static final OnlineModelConfig getModelConfig(int type, int folderType, @NotNull Context context) {
        String modelDir;
        switch (type) {
            case 0:
                modelDir = getFolder("sherpa-onnx-streaming-zipformer-bilingual-zh-en-2023-02-20", folderType, context);
                return new OnlineModelConfig(new OnlineTransducerModelConfig(modelDir + "/encoder-epoch-99-avg-1.onnx", modelDir + "/decoder-epoch-99-avg-1.onnx", modelDir + "/joiner-epoch-99-avg-1.onnx"), (OnlineParaformerModelConfig)null, (OnlineZipformer2CtcModelConfig)null, (OnlineNeMoCtcModelConfig)null, (OnlineToneCtcModelConfig)null, modelDir + "/tokens.txt", 0, false, (String)null, "zipformer", (String)null, (String)null, 3550, (DefaultConstructorMarker)null);
            case 1:
                modelDir = getFolder("sherpa-onnx-lstm-zh-2023-02-20", folderType, context);
                return new OnlineModelConfig(new OnlineTransducerModelConfig(modelDir + "/encoder-epoch-11-avg-1.onnx", modelDir + "/decoder-epoch-11-avg-1.onnx", modelDir + "/joiner-epoch-11-avg-1.onnx"), (OnlineParaformerModelConfig)null, (OnlineZipformer2CtcModelConfig)null, (OnlineNeMoCtcModelConfig)null, (OnlineToneCtcModelConfig)null, modelDir + "/tokens.txt", 0, false, (String)null, "lstm", (String)null, (String)null, 3550, (DefaultConstructorMarker)null);
            case 2:
                modelDir = getFolder("sherpa-onnx-lstm-en-2023-02-17", folderType, context);
                return new OnlineModelConfig(new OnlineTransducerModelConfig(modelDir + "/encoder-epoch-99-avg-1.onnx", modelDir + "/decoder-epoch-99-avg-1.onnx", modelDir + "/joiner-epoch-99-avg-1.onnx"), (OnlineParaformerModelConfig)null, (OnlineZipformer2CtcModelConfig)null, (OnlineNeMoCtcModelConfig)null, (OnlineToneCtcModelConfig)null, modelDir + "/tokens.txt", 0, false, (String)null, "lstm", (String)null, (String)null, 3550, (DefaultConstructorMarker)null);
            case 3:
                modelDir = getFolder("icefall-asr-zipformer-streaming-wenetspeech-20230615", folderType, context);
                return new OnlineModelConfig(new OnlineTransducerModelConfig(modelDir + "/exp/encoder-epoch-12-avg-4-chunk-16-left-128.int8.onnx", modelDir + "/exp/decoder-epoch-12-avg-4-chunk-16-left-128.onnx", modelDir + "/exp/joiner-epoch-12-avg-4-chunk-16-left-128.onnx"), (OnlineParaformerModelConfig)null, (OnlineZipformer2CtcModelConfig)null, (OnlineNeMoCtcModelConfig)null, (OnlineToneCtcModelConfig)null, modelDir + "/data/lang_char/tokens.txt", 0, false, (String)null, "zipformer2", (String)null, (String)null, 3550, (DefaultConstructorMarker)null);
            case 4:
                modelDir = getFolder("icefall-asr-zipformer-streaming-wenetspeech-20230615", folderType, context);
                return new OnlineModelConfig(new OnlineTransducerModelConfig(modelDir + "/exp/encoder-epoch-12-avg-4-chunk-16-left-128.onnx", modelDir + "/exp/decoder-epoch-12-avg-4-chunk-16-left-128.onnx", modelDir + "/exp/joiner-epoch-12-avg-4-chunk-16-left-128.onnx"), (OnlineParaformerModelConfig)null, (OnlineZipformer2CtcModelConfig)null, (OnlineNeMoCtcModelConfig)null, (OnlineToneCtcModelConfig)null, modelDir + "/data/lang_char/tokens.txt", 0, false, (String)null, "zipformer2", (String)null, (String)null, 3550, (DefaultConstructorMarker)null);
            case 5:
                modelDir = getFolder("sherpa-onnx-streaming-paraformer-bilingual-zh-en", folderType, context);
                return new OnlineModelConfig((OnlineTransducerModelConfig)null, new OnlineParaformerModelConfig(modelDir + "/encoder.int8.onnx", modelDir + "/decoder.int8.onnx"), (OnlineZipformer2CtcModelConfig)null, (OnlineNeMoCtcModelConfig)null, (OnlineToneCtcModelConfig)null, modelDir + "/tokens.txt", 0, false, (String)null, "paraformer", (String)null, (String)null, 3549, (DefaultConstructorMarker)null);
            case 6:
                modelDir = getFolder("sherpa-onnx-streaming-zipformer-en-2023-06-26", folderType, context);
                return new OnlineModelConfig(new OnlineTransducerModelConfig(modelDir + "/encoder-epoch-99-avg-1-chunk-16-left-128.int8.onnx", modelDir + "/decoder-epoch-99-avg-1-chunk-16-left-128.onnx", modelDir + "/joiner-epoch-99-avg-1-chunk-16-left-128.onnx"), (OnlineParaformerModelConfig)null, (OnlineZipformer2CtcModelConfig)null, (OnlineNeMoCtcModelConfig)null, (OnlineToneCtcModelConfig)null, modelDir + "/tokens.txt", 0, false, (String)null, "zipformer2", (String)null, (String)null, 3550, (DefaultConstructorMarker)null);
            case 7:
                modelDir = getFolder("sherpa-onnx-streaming-zipformer-fr-2023-04-14", folderType, context);
                return new OnlineModelConfig(new OnlineTransducerModelConfig(modelDir + "/encoder-epoch-29-avg-9-with-averaged-model.int8.onnx", modelDir + "/decoder-epoch-29-avg-9-with-averaged-model.onnx", modelDir + "/joiner-epoch-29-avg-9-with-averaged-model.onnx"), (OnlineParaformerModelConfig)null, (OnlineZipformer2CtcModelConfig)null, (OnlineNeMoCtcModelConfig)null, (OnlineToneCtcModelConfig)null, modelDir + "/tokens.txt", 0, false, (String)null, "zipformer", (String)null, (String)null, 3550, (DefaultConstructorMarker)null);
            case 8:
                modelDir = getFolder("sherpa-onnx-streaming-zipformer-bilingual-zh-en-2023-02-20", folderType, context);
                return new OnlineModelConfig(new OnlineTransducerModelConfig(modelDir + "/encoder-epoch-99-avg-1.int8.onnx", modelDir + "/decoder-epoch-99-avg-1.onnx", modelDir + "/joiner-epoch-99-avg-1.int8.onnx"), (OnlineParaformerModelConfig)null, (OnlineZipformer2CtcModelConfig)null, (OnlineNeMoCtcModelConfig)null, (OnlineToneCtcModelConfig)null, modelDir + "/tokens.txt", 0, false, (String)null, "zipformer", (String)null, (String)null, 3550, (DefaultConstructorMarker)null);
            case 9:
                modelDir = getFolder("sherpa-onnx-streaming-zipformer-zh-14M-2023-02-23", folderType, context);
                return new OnlineModelConfig(new OnlineTransducerModelConfig(modelDir + "/encoder-epoch-99-avg-1.int8.onnx", modelDir + "/decoder-epoch-99-avg-1.onnx", modelDir + "/joiner-epoch-99-avg-1.int8.onnx"), (OnlineParaformerModelConfig)null, (OnlineZipformer2CtcModelConfig)null, (OnlineNeMoCtcModelConfig)null, (OnlineToneCtcModelConfig)null, modelDir + "/tokens.txt", 0, false, (String)null, "zipformer", (String)null, (String)null, 3550, (DefaultConstructorMarker)null);
            case 10:
                modelDir = getFolder("vendor/assets/sherpa-onnx-streaming-zipformer-en-20M-2023-02-17", folderType, context);
                return new OnlineModelConfig(new OnlineTransducerModelConfig(modelDir + "/encoder-epoch-99-avg-1.int8.onnx", modelDir + "/decoder-epoch-99-avg-1.onnx", modelDir + "/joiner-epoch-99-avg-1.int8.onnx"), (OnlineParaformerModelConfig)null, (OnlineZipformer2CtcModelConfig)null, (OnlineNeMoCtcModelConfig)null, (OnlineToneCtcModelConfig)null, modelDir + "/tokens.txt", 0, false, (String)null, "zipformer", (String)null, (String)null, 3550, (DefaultConstructorMarker)null);
            case 11:
                modelDir = getFolder("sherpa-onnx-nemo-streaming-fast-conformer-ctc-en-80ms", folderType, context);
                return new OnlineModelConfig((OnlineTransducerModelConfig)null, (OnlineParaformerModelConfig)null, (OnlineZipformer2CtcModelConfig)null, new OnlineNeMoCtcModelConfig(modelDir + "/model.onnx"), (OnlineToneCtcModelConfig)null, modelDir + "/tokens.txt", 0, false, (String)null, (String)null, (String)null, (String)null, 4055, (DefaultConstructorMarker)null);
            case 12:
                modelDir = getFolder("sherpa-onnx-nemo-streaming-fast-conformer-ctc-en-480ms", folderType, context);
                return new OnlineModelConfig((OnlineTransducerModelConfig)null, (OnlineParaformerModelConfig)null, (OnlineZipformer2CtcModelConfig)null, new OnlineNeMoCtcModelConfig(modelDir + "/model.onnx"), (OnlineToneCtcModelConfig)null, modelDir + "/tokens.txt", 0, false, (String)null, (String)null, (String)null, (String)null, 4055, (DefaultConstructorMarker)null);
            case 13:
                modelDir = getFolder("sherpa-onnx-nemo-streaming-fast-conformer-ctc-en-1040ms", folderType, context);
                return new OnlineModelConfig((OnlineTransducerModelConfig)null, (OnlineParaformerModelConfig)null, (OnlineZipformer2CtcModelConfig)null, new OnlineNeMoCtcModelConfig(modelDir + "/model.onnx"), (OnlineToneCtcModelConfig)null, modelDir + "/tokens.txt", 0, false, (String)null, (String)null, (String)null, (String)null, 4055, (DefaultConstructorMarker)null);
            case 14:
                modelDir = getFolder("sherpa-onnx-streaming-zipformer-korean-2024-06-16", folderType, context);
                return new OnlineModelConfig(new OnlineTransducerModelConfig(modelDir + "/encoder-epoch-99-avg-1.int8.onnx", modelDir + "/decoder-epoch-99-avg-1.onnx", modelDir + "/joiner-epoch-99-avg-1.int8.onnx"), (OnlineParaformerModelConfig)null, (OnlineZipformer2CtcModelConfig)null, (OnlineNeMoCtcModelConfig)null, (OnlineToneCtcModelConfig)null, modelDir + "/tokens.txt", 0, false, (String)null, "zipformer", (String)null, (String)null, 3550, (DefaultConstructorMarker)null);
            case 15:
                modelDir = getFolder("sherpa-onnx-streaming-zipformer-small-ctc-zh-int8-2025-04-01", folderType, context);
                return new OnlineModelConfig((OnlineTransducerModelConfig)null, (OnlineParaformerModelConfig)null, new OnlineZipformer2CtcModelConfig(modelDir + "/model.int8.onnx"), (OnlineNeMoCtcModelConfig)null, (OnlineToneCtcModelConfig)null, modelDir + "/tokens.txt", 0, false, (String)null, (String)null, (String)null, (String)null, 4059, (DefaultConstructorMarker)null);
            case 16:
                modelDir = getFolder("sherpa-onnx-streaming-zipformer-small-ctc-zh-2025-04-01", folderType, context);
                return new OnlineModelConfig((OnlineTransducerModelConfig)null, (OnlineParaformerModelConfig)null, new OnlineZipformer2CtcModelConfig(modelDir + "/model.onnx"), (OnlineNeMoCtcModelConfig)null, (OnlineToneCtcModelConfig)null, modelDir + "/tokens.txt", 0, false, (String)null, (String)null, (String)null, (String)null, 4059, (DefaultConstructorMarker)null);
            case 17:
                modelDir = getFolder("sherpa-onnx-streaming-zipformer-ctc-zh-int8-2025-06-30", folderType, context);
                return new OnlineModelConfig((OnlineTransducerModelConfig)null, (OnlineParaformerModelConfig)null, new OnlineZipformer2CtcModelConfig(modelDir + "/model.int8.onnx"), (OnlineNeMoCtcModelConfig)null, (OnlineToneCtcModelConfig)null, modelDir + "/tokens.txt", 0, false, (String)null, (String)null, (String)null, (String)null, 4059, (DefaultConstructorMarker)null);
            case 18:
                modelDir = getFolder("sherpa-onnx-streaming-zipformer-ctc-zh-2025-06-30", folderType, context);
                return new OnlineModelConfig((OnlineTransducerModelConfig)null, (OnlineParaformerModelConfig)null, new OnlineZipformer2CtcModelConfig(modelDir + "/model.onnx"), (OnlineNeMoCtcModelConfig)null, (OnlineToneCtcModelConfig)null, modelDir + "/tokens.txt", 0, false, (String)null, "zipformer2", (String)null, (String)null, 3547, (DefaultConstructorMarker)null);
            case 19:
                modelDir = getFolder("sherpa-onnx-streaming-zipformer-ctc-zh-fp16-2025-06-30", folderType, context);
                return new OnlineModelConfig((OnlineTransducerModelConfig)null, (OnlineParaformerModelConfig)null, new OnlineZipformer2CtcModelConfig(modelDir + "/model.fp16.onnx"), (OnlineNeMoCtcModelConfig)null, (OnlineToneCtcModelConfig)null, modelDir + "/tokens.txt", 0, false, (String)null, "zipformer2", (String)null, (String)null, 3547, (DefaultConstructorMarker)null);
            case 20:
                modelDir = getFolder("sherpa-onnx-streaming-zipformer-zh-int8-2025-06-30", folderType, context);
                return new OnlineModelConfig(new OnlineTransducerModelConfig(modelDir + "/encoder.int8.onnx", modelDir + "/decoder.onnx", modelDir + "/joiner.int8.onnx"), (OnlineParaformerModelConfig)null, (OnlineZipformer2CtcModelConfig)null, (OnlineNeMoCtcModelConfig)null, (OnlineToneCtcModelConfig)null, modelDir + "/tokens.txt", 0, false, (String)null, "zipformer2", (String)null, (String)null, 3550, (DefaultConstructorMarker)null);
            case 21:
                modelDir = getFolder("sherpa-onnx-streaming-zipformer-en-kroko-2025-08-06", folderType, context);
                return new OnlineModelConfig(new OnlineTransducerModelConfig(modelDir + "/encoder.onnx", modelDir + "/decoder.onnx", modelDir + "/joiner.onnx"), (OnlineParaformerModelConfig)null, (OnlineZipformer2CtcModelConfig)null, (OnlineNeMoCtcModelConfig)null, (OnlineToneCtcModelConfig)null, modelDir + "/tokens.txt", 0, false, (String)null, "zipformer2", (String)null, (String)null, 3550, (DefaultConstructorMarker)null);
            case 22:
                modelDir = getFolder("sherpa-onnx-streaming-zipformer-es-kroko-2025-08-06", folderType, context);
                return new OnlineModelConfig(new OnlineTransducerModelConfig(modelDir + "/encoder.onnx", modelDir + "/decoder.onnx", modelDir + "/joiner.onnx"), (OnlineParaformerModelConfig)null, (OnlineZipformer2CtcModelConfig)null, (OnlineNeMoCtcModelConfig)null, (OnlineToneCtcModelConfig)null, modelDir + "/tokens.txt", 0, false, (String)null, "zipformer2", (String)null, (String)null, 3550, (DefaultConstructorMarker)null);
            case 23:
                modelDir = getFolder("sherpa-onnx-streaming-zipformer-fr-kroko-2025-08-06", folderType, context);
                return new OnlineModelConfig(new OnlineTransducerModelConfig(modelDir + "/encoder.onnx", modelDir + "/decoder.onnx", modelDir + "/joiner.onnx"), (OnlineParaformerModelConfig)null, (OnlineZipformer2CtcModelConfig)null, (OnlineNeMoCtcModelConfig)null, (OnlineToneCtcModelConfig)null, modelDir + "/tokens.txt", 0, false, (String)null, "zipformer2", (String)null, (String)null, 3550, (DefaultConstructorMarker)null);
            case 24:
                modelDir = getFolder("sherpa-onnx-streaming-zipformer-de-kroko-2025-08-06", folderType, context);
                return new OnlineModelConfig(new OnlineTransducerModelConfig(modelDir + "/encoder.onnx", modelDir + "/decoder.onnx", modelDir + "/joiner.onnx"), (OnlineParaformerModelConfig)null, (OnlineZipformer2CtcModelConfig)null, (OnlineNeMoCtcModelConfig)null, (OnlineToneCtcModelConfig)null, modelDir + "/tokens.txt", 0, false, (String)null, "zipformer2", (String)null, (String)null, 3550, (DefaultConstructorMarker)null);
            case 25:
                modelDir = getFolder("sherpa-onnx-streaming-zipformer-small-ru-vosk-int8-2025-08-16", folderType, context);
                return new OnlineModelConfig(new OnlineTransducerModelConfig(modelDir + "/encoder.int8.onnx", modelDir + "/decoder.onnx", modelDir + "/joiner.int8.onnx"), (OnlineParaformerModelConfig)null, (OnlineZipformer2CtcModelConfig)null, (OnlineNeMoCtcModelConfig)null, (OnlineToneCtcModelConfig)null, modelDir + "/tokens.txt", 0, false, (String)null, "zipformer2", (String)null, (String)null, 3550, (DefaultConstructorMarker)null);
            case 26:
                modelDir = getFolder("sherpa-onnx-streaming-zipformer-small-ru-vosk-2025-08-16", folderType, context);
                return new OnlineModelConfig(new OnlineTransducerModelConfig(modelDir + "/encoder.onnx", modelDir + "/decoder.onnx", modelDir + "/joiner.onnx"), (OnlineParaformerModelConfig)null, (OnlineZipformer2CtcModelConfig)null, (OnlineNeMoCtcModelConfig)null, (OnlineToneCtcModelConfig)null, modelDir + "/tokens.txt", 0, false, (String)null, "zipformer2", (String)null, (String)null, 3550, (DefaultConstructorMarker)null);
            case 27:
                modelDir = getFolder("sherpa-onnx-streaming-t-one-russian-2025-09-08", folderType, context);
                return new OnlineModelConfig((OnlineTransducerModelConfig)null, (OnlineParaformerModelConfig)null, (OnlineZipformer2CtcModelConfig)null, (OnlineNeMoCtcModelConfig)null, new OnlineToneCtcModelConfig(modelDir + "/model.onnx"), modelDir + "/tokens.txt", 0, false, (String)null, (String)null, (String)null, (String)null, 4047, (DefaultConstructorMarker)null);
            case 28:
                modelDir = getFolder("sherpa-onnx-nemotron-speech-streaming-en-0.6b-int8-2026-01-14", folderType, context);
                return new OnlineModelConfig(new OnlineTransducerModelConfig(modelDir + "/encoder.int8.onnx", modelDir + "/decoder.int8.onnx", modelDir + "/joiner.int8.onnx"), (OnlineParaformerModelConfig)null, (OnlineZipformer2CtcModelConfig)null, (OnlineNeMoCtcModelConfig)null, (OnlineToneCtcModelConfig)null, modelDir + "/tokens.txt", 0, false, (String)null, (String)null, (String)null, (String)null, 4062, (DefaultConstructorMarker)null);
            case 29:
                modelDir = getFolder("sherpa-onnx-streaming-zipformer-bn-vosk-2026-02-09", folderType, context);
                return new OnlineModelConfig(new OnlineTransducerModelConfig(modelDir + "/encoder.onnx", modelDir + "/decoder.onnx", modelDir + "/joiner.onnx"), (OnlineParaformerModelConfig)null, (OnlineZipformer2CtcModelConfig)null, (OnlineNeMoCtcModelConfig)null, (OnlineToneCtcModelConfig)null, modelDir + "/tokens.txt", 0, false, (String)null, "zipformer2", (String)null, (String)null, 3550, (DefaultConstructorMarker)null);
            case 1000:
                modelDir = getFolder("sherpa-onnx-rk3588-streaming-zipformer-bilingual-zh-en-2023-02-20", folderType, context);
                OnlineTransducerModelConfig var35 = new OnlineTransducerModelConfig(modelDir + "/encoder.rknn", modelDir + "/decoder.rknn", modelDir + "/joiner.rknn");
                String var36 = modelDir + "/tokens.txt";
                return new OnlineModelConfig(var35, (OnlineParaformerModelConfig)null, (OnlineZipformer2CtcModelConfig)null, (OnlineNeMoCtcModelConfig)null, (OnlineToneCtcModelConfig)null, var36, 0, false, "rknn", "zipformer", (String)null, (String)null, 3294, (DefaultConstructorMarker)null);
            case 1001:
                modelDir = getFolder("sherpa-onnx-rk3588-streaming-zipformer-small-bilingual-zh-en-2023-02-16", folderType, context);
                OnlineTransducerModelConfig var2 = new OnlineTransducerModelConfig(modelDir + "/encoder.rknn", modelDir + "/decoder.rknn", modelDir + "/joiner.rknn");
                String var3 = modelDir + "/tokens.txt";
                return new OnlineModelConfig(var2, (OnlineParaformerModelConfig)null, (OnlineZipformer2CtcModelConfig)null, (OnlineNeMoCtcModelConfig)null, (OnlineToneCtcModelConfig)null, var3, 0, false, "rknn", "zipformer", (String)null, (String)null, 3294, (DefaultConstructorMarker)null);
            default:
                return null;
        }
    }

    @NotNull
    public static final OnlineLMConfig getOnlineLMConfig(int type) {
        if (type == 0) {
            String modelDir = "sherpa-onnx-streaming-zipformer-bilingual-zh-en-2023-02-20";
            return new OnlineLMConfig(modelDir + "/with-state-epoch-99-avg-1.int8.onnx", 0.5F);
        } else {
            return new OnlineLMConfig((String)null, 0.0F, 3, (DefaultConstructorMarker)null);
        }
    }

    @NotNull
    public static final EndpointConfig getEndpointConfig() {
        return new EndpointConfig(new EndpointRule(false, 2.4F, 0.0F), new EndpointRule(true, 1.4F, 0.0F), new EndpointRule(false, 0.0F, 20.0F));
    }
}
