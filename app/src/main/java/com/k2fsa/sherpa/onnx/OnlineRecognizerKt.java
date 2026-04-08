package com.k2fsa.sherpa.onnx;

import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
        mv = {1, 7, 0},
        k = 2,
        xi = 48,
        d1 = {"\u0000\u001a\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u001a\u0010\u0010\u0000\u001a\u0004\u0018\u00010\u00012\u0006\u0010\u0002\u001a\u00020\u0003\u001a\u000e\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0002\u001a\u00020\u0003\u001a\u0006\u0010\u0006\u001a\u00020\u0007¨\u0006\b"},
        d2 = {"getModelConfig", "Lcom/k2fsa/sherpa/onnx/OnlineModelConfig;", "type", "", "getOnlineLMConfig", "Lcom/k2fsa/sherpa/onnx/OnlineLMConfig;", "getEndpointConfig", "Lcom/k2fsa/sherpa/onnx/EndpointConfig;", "Sources of SherpaOnnx.app.main"}
)
public final class OnlineRecognizerKt {
    @Nullable
    public static final OnlineModelConfig getModelConfig(int type) {
        String modelDir;
        switch (type) {
            case 0:
                modelDir = "sherpa-onnx-streaming-zipformer-bilingual-zh-en-2023-02-20";
                return new OnlineModelConfig(new OnlineTransducerModelConfig(modelDir + "/encoder-epoch-99-avg-1.onnx", modelDir + "/decoder-epoch-99-avg-1.onnx", modelDir + "/joiner-epoch-99-avg-1.onnx"), (OnlineParaformerModelConfig)null, (OnlineZipformer2CtcModelConfig)null, (OnlineNeMoCtcModelConfig)null, (OnlineToneCtcModelConfig)null, modelDir + "/tokens.txt", 0, false, (String)null, "zipformer", (String)null, (String)null, 3550, (DefaultConstructorMarker)null);
            case 1:
                modelDir = "sherpa-onnx-lstm-zh-2023-02-20";
                return new OnlineModelConfig(new OnlineTransducerModelConfig(modelDir + "/encoder-epoch-11-avg-1.onnx", modelDir + "/decoder-epoch-11-avg-1.onnx", modelDir + "/joiner-epoch-11-avg-1.onnx"), (OnlineParaformerModelConfig)null, (OnlineZipformer2CtcModelConfig)null, (OnlineNeMoCtcModelConfig)null, (OnlineToneCtcModelConfig)null, modelDir + "/tokens.txt", 0, false, (String)null, "lstm", (String)null, (String)null, 3550, (DefaultConstructorMarker)null);
            case 2:
                modelDir = "sherpa-onnx-lstm-en-2023-02-17";
                return new OnlineModelConfig(new OnlineTransducerModelConfig(modelDir + "/encoder-epoch-99-avg-1.onnx", modelDir + "/decoder-epoch-99-avg-1.onnx", modelDir + "/joiner-epoch-99-avg-1.onnx"), (OnlineParaformerModelConfig)null, (OnlineZipformer2CtcModelConfig)null, (OnlineNeMoCtcModelConfig)null, (OnlineToneCtcModelConfig)null, modelDir + "/tokens.txt", 0, false, (String)null, "lstm", (String)null, (String)null, 3550, (DefaultConstructorMarker)null);
            case 3:
                modelDir = "icefall-asr-zipformer-streaming-wenetspeech-20230615";
                return new OnlineModelConfig(new OnlineTransducerModelConfig(modelDir + "/exp/encoder-epoch-12-avg-4-chunk-16-left-128.int8.onnx", modelDir + "/exp/decoder-epoch-12-avg-4-chunk-16-left-128.onnx", modelDir + "/exp/joiner-epoch-12-avg-4-chunk-16-left-128.onnx"), (OnlineParaformerModelConfig)null, (OnlineZipformer2CtcModelConfig)null, (OnlineNeMoCtcModelConfig)null, (OnlineToneCtcModelConfig)null, modelDir + "/data/lang_char/tokens.txt", 0, false, (String)null, "zipformer2", (String)null, (String)null, 3550, (DefaultConstructorMarker)null);
            case 4:
                modelDir = "icefall-asr-zipformer-streaming-wenetspeech-20230615";
                return new OnlineModelConfig(new OnlineTransducerModelConfig(modelDir + "/exp/encoder-epoch-12-avg-4-chunk-16-left-128.onnx", modelDir + "/exp/decoder-epoch-12-avg-4-chunk-16-left-128.onnx", modelDir + "/exp/joiner-epoch-12-avg-4-chunk-16-left-128.onnx"), (OnlineParaformerModelConfig)null, (OnlineZipformer2CtcModelConfig)null, (OnlineNeMoCtcModelConfig)null, (OnlineToneCtcModelConfig)null, modelDir + "/data/lang_char/tokens.txt", 0, false, (String)null, "zipformer2", (String)null, (String)null, 3550, (DefaultConstructorMarker)null);
            case 5:
                modelDir = "sherpa-onnx-streaming-paraformer-bilingual-zh-en";
                return new OnlineModelConfig((OnlineTransducerModelConfig)null, new OnlineParaformerModelConfig(modelDir + "/encoder.int8.onnx", modelDir + "/decoder.int8.onnx"), (OnlineZipformer2CtcModelConfig)null, (OnlineNeMoCtcModelConfig)null, (OnlineToneCtcModelConfig)null, modelDir + "/tokens.txt", 0, false, (String)null, "paraformer", (String)null, (String)null, 3549, (DefaultConstructorMarker)null);
            case 6:
                modelDir = "sherpa-onnx-streaming-zipformer-en-2023-06-26";
                return new OnlineModelConfig(new OnlineTransducerModelConfig(modelDir + "/encoder-epoch-99-avg-1-chunk-16-left-128.int8.onnx", modelDir + "/decoder-epoch-99-avg-1-chunk-16-left-128.onnx", modelDir + "/joiner-epoch-99-avg-1-chunk-16-left-128.onnx"), (OnlineParaformerModelConfig)null, (OnlineZipformer2CtcModelConfig)null, (OnlineNeMoCtcModelConfig)null, (OnlineToneCtcModelConfig)null, modelDir + "/tokens.txt", 0, false, (String)null, "zipformer2", (String)null, (String)null, 3550, (DefaultConstructorMarker)null);
            case 7:
                modelDir = "sherpa-onnx-streaming-zipformer-fr-2023-04-14";
                return new OnlineModelConfig(new OnlineTransducerModelConfig(modelDir + "/encoder-epoch-29-avg-9-with-averaged-model.int8.onnx", modelDir + "/decoder-epoch-29-avg-9-with-averaged-model.onnx", modelDir + "/joiner-epoch-29-avg-9-with-averaged-model.onnx"), (OnlineParaformerModelConfig)null, (OnlineZipformer2CtcModelConfig)null, (OnlineNeMoCtcModelConfig)null, (OnlineToneCtcModelConfig)null, modelDir + "/tokens.txt", 0, false, (String)null, "zipformer", (String)null, (String)null, 3550, (DefaultConstructorMarker)null);
            case 8:
                modelDir = "sherpa-onnx-streaming-zipformer-bilingual-zh-en-2023-02-20";
                return new OnlineModelConfig(new OnlineTransducerModelConfig(modelDir + "/encoder-epoch-99-avg-1.int8.onnx", modelDir + "/decoder-epoch-99-avg-1.onnx", modelDir + "/joiner-epoch-99-avg-1.int8.onnx"), (OnlineParaformerModelConfig)null, (OnlineZipformer2CtcModelConfig)null, (OnlineNeMoCtcModelConfig)null, (OnlineToneCtcModelConfig)null, modelDir + "/tokens.txt", 0, false, (String)null, "zipformer", (String)null, (String)null, 3550, (DefaultConstructorMarker)null);
            case 9:
                modelDir = "sherpa-onnx-streaming-zipformer-zh-14M-2023-02-23";
                return new OnlineModelConfig(new OnlineTransducerModelConfig(modelDir + "/encoder-epoch-99-avg-1.int8.onnx", modelDir + "/decoder-epoch-99-avg-1.onnx", modelDir + "/joiner-epoch-99-avg-1.int8.onnx"), (OnlineParaformerModelConfig)null, (OnlineZipformer2CtcModelConfig)null, (OnlineNeMoCtcModelConfig)null, (OnlineToneCtcModelConfig)null, modelDir + "/tokens.txt", 0, false, (String)null, "zipformer", (String)null, (String)null, 3550, (DefaultConstructorMarker)null);
            case 10:
                modelDir = "sherpa-onnx-streaming-zipformer-en-20M-2023-02-17";
                return new OnlineModelConfig(new OnlineTransducerModelConfig(modelDir + "/encoder-epoch-99-avg-1.int8.onnx", modelDir + "/decoder-epoch-99-avg-1.onnx", modelDir + "/joiner-epoch-99-avg-1.int8.onnx"), (OnlineParaformerModelConfig)null, (OnlineZipformer2CtcModelConfig)null, (OnlineNeMoCtcModelConfig)null, (OnlineToneCtcModelConfig)null, modelDir + "/tokens.txt", 0, false, (String)null, "zipformer", (String)null, (String)null, 3550, (DefaultConstructorMarker)null);
            case 11:
                modelDir = "sherpa-onnx-nemo-streaming-fast-conformer-ctc-en-80ms";
                return new OnlineModelConfig((OnlineTransducerModelConfig)null, (OnlineParaformerModelConfig)null, (OnlineZipformer2CtcModelConfig)null, new OnlineNeMoCtcModelConfig(modelDir + "/model.onnx"), (OnlineToneCtcModelConfig)null, modelDir + "/tokens.txt", 0, false, (String)null, (String)null, (String)null, (String)null, 4055, (DefaultConstructorMarker)null);
            case 12:
                modelDir = "sherpa-onnx-nemo-streaming-fast-conformer-ctc-en-480ms";
                return new OnlineModelConfig((OnlineTransducerModelConfig)null, (OnlineParaformerModelConfig)null, (OnlineZipformer2CtcModelConfig)null, new OnlineNeMoCtcModelConfig(modelDir + "/model.onnx"), (OnlineToneCtcModelConfig)null, modelDir + "/tokens.txt", 0, false, (String)null, (String)null, (String)null, (String)null, 4055, (DefaultConstructorMarker)null);
            case 13:
                modelDir = "sherpa-onnx-nemo-streaming-fast-conformer-ctc-en-1040ms";
                return new OnlineModelConfig((OnlineTransducerModelConfig)null, (OnlineParaformerModelConfig)null, (OnlineZipformer2CtcModelConfig)null, new OnlineNeMoCtcModelConfig(modelDir + "/model.onnx"), (OnlineToneCtcModelConfig)null, modelDir + "/tokens.txt", 0, false, (String)null, (String)null, (String)null, (String)null, 4055, (DefaultConstructorMarker)null);
            case 14:
                modelDir = "sherpa-onnx-streaming-zipformer-korean-2024-06-16";
                return new OnlineModelConfig(new OnlineTransducerModelConfig(modelDir + "/encoder-epoch-99-avg-1.int8.onnx", modelDir + "/decoder-epoch-99-avg-1.onnx", modelDir + "/joiner-epoch-99-avg-1.int8.onnx"), (OnlineParaformerModelConfig)null, (OnlineZipformer2CtcModelConfig)null, (OnlineNeMoCtcModelConfig)null, (OnlineToneCtcModelConfig)null, modelDir + "/tokens.txt", 0, false, (String)null, "zipformer", (String)null, (String)null, 3550, (DefaultConstructorMarker)null);
            case 15:
                modelDir = "sherpa-onnx-streaming-zipformer-small-ctc-zh-int8-2025-04-01";
                return new OnlineModelConfig((OnlineTransducerModelConfig)null, (OnlineParaformerModelConfig)null, new OnlineZipformer2CtcModelConfig(modelDir + "/model.int8.onnx"), (OnlineNeMoCtcModelConfig)null, (OnlineToneCtcModelConfig)null, modelDir + "/tokens.txt", 0, false, (String)null, (String)null, (String)null, (String)null, 4059, (DefaultConstructorMarker)null);
            case 16:
                modelDir = "sherpa-onnx-streaming-zipformer-small-ctc-zh-2025-04-01";
                return new OnlineModelConfig((OnlineTransducerModelConfig)null, (OnlineParaformerModelConfig)null, new OnlineZipformer2CtcModelConfig(modelDir + "/model.onnx"), (OnlineNeMoCtcModelConfig)null, (OnlineToneCtcModelConfig)null, modelDir + "/tokens.txt", 0, false, (String)null, (String)null, (String)null, (String)null, 4059, (DefaultConstructorMarker)null);
            case 17:
                modelDir = "sherpa-onnx-streaming-zipformer-ctc-zh-int8-2025-06-30";
                return new OnlineModelConfig((OnlineTransducerModelConfig)null, (OnlineParaformerModelConfig)null, new OnlineZipformer2CtcModelConfig(modelDir + "/model.int8.onnx"), (OnlineNeMoCtcModelConfig)null, (OnlineToneCtcModelConfig)null, modelDir + "/tokens.txt", 0, false, (String)null, (String)null, (String)null, (String)null, 4059, (DefaultConstructorMarker)null);
            case 18:
                modelDir = "sherpa-onnx-streaming-zipformer-ctc-zh-2025-06-30";
                return new OnlineModelConfig((OnlineTransducerModelConfig)null, (OnlineParaformerModelConfig)null, new OnlineZipformer2CtcModelConfig(modelDir + "/model.onnx"), (OnlineNeMoCtcModelConfig)null, (OnlineToneCtcModelConfig)null, modelDir + "/tokens.txt", 0, false, (String)null, "zipformer2", (String)null, (String)null, 3547, (DefaultConstructorMarker)null);
            case 19:
                modelDir = "sherpa-onnx-streaming-zipformer-ctc-zh-fp16-2025-06-30";
                return new OnlineModelConfig((OnlineTransducerModelConfig)null, (OnlineParaformerModelConfig)null, new OnlineZipformer2CtcModelConfig(modelDir + "/model.fp16.onnx"), (OnlineNeMoCtcModelConfig)null, (OnlineToneCtcModelConfig)null, modelDir + "/tokens.txt", 0, false, (String)null, "zipformer2", (String)null, (String)null, 3547, (DefaultConstructorMarker)null);
            case 20:
                modelDir = "sherpa-onnx-streaming-zipformer-zh-int8-2025-06-30";
                return new OnlineModelConfig(new OnlineTransducerModelConfig(modelDir + "/encoder.int8.onnx", modelDir + "/decoder.onnx", modelDir + "/joiner.int8.onnx"), (OnlineParaformerModelConfig)null, (OnlineZipformer2CtcModelConfig)null, (OnlineNeMoCtcModelConfig)null, (OnlineToneCtcModelConfig)null, modelDir + "/tokens.txt", 0, false, (String)null, "zipformer2", (String)null, (String)null, 3550, (DefaultConstructorMarker)null);
            case 21:
                modelDir = "sherpa-onnx-streaming-zipformer-en-kroko-2025-08-06";
                return new OnlineModelConfig(new OnlineTransducerModelConfig(modelDir + "/encoder.onnx", modelDir + "/decoder.onnx", modelDir + "/joiner.onnx"), (OnlineParaformerModelConfig)null, (OnlineZipformer2CtcModelConfig)null, (OnlineNeMoCtcModelConfig)null, (OnlineToneCtcModelConfig)null, modelDir + "/tokens.txt", 0, false, (String)null, "zipformer2", (String)null, (String)null, 3550, (DefaultConstructorMarker)null);
            case 22:
                modelDir = "sherpa-onnx-streaming-zipformer-es-kroko-2025-08-06";
                return new OnlineModelConfig(new OnlineTransducerModelConfig(modelDir + "/encoder.onnx", modelDir + "/decoder.onnx", modelDir + "/joiner.onnx"), (OnlineParaformerModelConfig)null, (OnlineZipformer2CtcModelConfig)null, (OnlineNeMoCtcModelConfig)null, (OnlineToneCtcModelConfig)null, modelDir + "/tokens.txt", 0, false, (String)null, "zipformer2", (String)null, (String)null, 3550, (DefaultConstructorMarker)null);
            case 23:
                modelDir = "sherpa-onnx-streaming-zipformer-fr-kroko-2025-08-06";
                return new OnlineModelConfig(new OnlineTransducerModelConfig(modelDir + "/encoder.onnx", modelDir + "/decoder.onnx", modelDir + "/joiner.onnx"), (OnlineParaformerModelConfig)null, (OnlineZipformer2CtcModelConfig)null, (OnlineNeMoCtcModelConfig)null, (OnlineToneCtcModelConfig)null, modelDir + "/tokens.txt", 0, false, (String)null, "zipformer2", (String)null, (String)null, 3550, (DefaultConstructorMarker)null);
            case 24:
                modelDir = "sherpa-onnx-streaming-zipformer-de-kroko-2025-08-06";
                return new OnlineModelConfig(new OnlineTransducerModelConfig(modelDir + "/encoder.onnx", modelDir + "/decoder.onnx", modelDir + "/joiner.onnx"), (OnlineParaformerModelConfig)null, (OnlineZipformer2CtcModelConfig)null, (OnlineNeMoCtcModelConfig)null, (OnlineToneCtcModelConfig)null, modelDir + "/tokens.txt", 0, false, (String)null, "zipformer2", (String)null, (String)null, 3550, (DefaultConstructorMarker)null);
            case 25:
                modelDir = "sherpa-onnx-streaming-zipformer-small-ru-vosk-int8-2025-08-16";
                return new OnlineModelConfig(new OnlineTransducerModelConfig(modelDir + "/encoder.int8.onnx", modelDir + "/decoder.onnx", modelDir + "/joiner.int8.onnx"), (OnlineParaformerModelConfig)null, (OnlineZipformer2CtcModelConfig)null, (OnlineNeMoCtcModelConfig)null, (OnlineToneCtcModelConfig)null, modelDir + "/tokens.txt", 0, false, (String)null, "zipformer2", (String)null, (String)null, 3550, (DefaultConstructorMarker)null);
            case 26:
                modelDir = "sherpa-onnx-streaming-zipformer-small-ru-vosk-2025-08-16";
                return new OnlineModelConfig(new OnlineTransducerModelConfig(modelDir + "/encoder.onnx", modelDir + "/decoder.onnx", modelDir + "/joiner.onnx"), (OnlineParaformerModelConfig)null, (OnlineZipformer2CtcModelConfig)null, (OnlineNeMoCtcModelConfig)null, (OnlineToneCtcModelConfig)null, modelDir + "/tokens.txt", 0, false, (String)null, "zipformer2", (String)null, (String)null, 3550, (DefaultConstructorMarker)null);
            case 27:
                modelDir = "sherpa-onnx-streaming-t-one-russian-2025-09-08";
                return new OnlineModelConfig((OnlineTransducerModelConfig)null, (OnlineParaformerModelConfig)null, (OnlineZipformer2CtcModelConfig)null, (OnlineNeMoCtcModelConfig)null, new OnlineToneCtcModelConfig(modelDir + "/model.onnx"), modelDir + "/tokens.txt", 0, false, (String)null, (String)null, (String)null, (String)null, 4047, (DefaultConstructorMarker)null);
            case 28:
                modelDir = "sherpa-onnx-nemotron-speech-streaming-en-0.6b-int8-2026-01-14";
                return new OnlineModelConfig(new OnlineTransducerModelConfig(modelDir + "/encoder.int8.onnx", modelDir + "/decoder.int8.onnx", modelDir + "/joiner.int8.onnx"), (OnlineParaformerModelConfig)null, (OnlineZipformer2CtcModelConfig)null, (OnlineNeMoCtcModelConfig)null, (OnlineToneCtcModelConfig)null, modelDir + "/tokens.txt", 0, false, (String)null, (String)null, (String)null, (String)null, 4062, (DefaultConstructorMarker)null);
            case 29:
                modelDir = "sherpa-onnx-streaming-zipformer-bn-vosk-2026-02-09";
                return new OnlineModelConfig(new OnlineTransducerModelConfig(modelDir + "/encoder.onnx", modelDir + "/decoder.onnx", modelDir + "/joiner.onnx"), (OnlineParaformerModelConfig)null, (OnlineZipformer2CtcModelConfig)null, (OnlineNeMoCtcModelConfig)null, (OnlineToneCtcModelConfig)null, modelDir + "/tokens.txt", 0, false, (String)null, "zipformer2", (String)null, (String)null, 3550, (DefaultConstructorMarker)null);
            case 1000:
                modelDir = "sherpa-onnx-rk3588-streaming-zipformer-bilingual-zh-en-2023-02-20";
                OnlineTransducerModelConfig var35 = new OnlineTransducerModelConfig(modelDir + "/encoder.rknn", modelDir + "/decoder.rknn", modelDir + "/joiner.rknn");
                String var36 = modelDir + "/tokens.txt";
                return new OnlineModelConfig(var35, (OnlineParaformerModelConfig)null, (OnlineZipformer2CtcModelConfig)null, (OnlineNeMoCtcModelConfig)null, (OnlineToneCtcModelConfig)null, var36, 0, false, "rknn", "zipformer", (String)null, (String)null, 3294, (DefaultConstructorMarker)null);
            case 1001:
                modelDir = "sherpa-onnx-rk3588-streaming-zipformer-small-bilingual-zh-en-2023-02-16";
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
