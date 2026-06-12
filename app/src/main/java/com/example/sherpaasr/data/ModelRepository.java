package com.example.sherpaasr.data;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ModelRepository {
    public final static String MODELS_FOLDER_NAME = "models";
    public final static boolean USE_EXTERNAL_FILES = true;

    private static final String PREFS_NAME = "model_prefs";
    private static final String KEY_PREFIX_DOWNLOADED = "downloaded_";

    private final Context context;
    private final SharedPreferences prefs;
    private final List<ModelItem> models;

    public ModelRepository(Context context) {
        this.context = context.getApplicationContext();
        this.prefs = this.context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.models = new ArrayList<>();
        initModels();
    }

    private void initModels() {
        //https://github.com/k2-fsa/sherpa-onnx/releases/tag/asr-models
//        models.add(new ModelItem(
//                "English Streaming Zipformer (small)",
//                "https://github.com/k2-fsa/sherpa-onnx/releases/download/asr-models/sherpa-onnx-streaming-zipformer-en-2023-06-21.tar.bz2",
//                "sherpa-onnx-streaming-zipformer-en-2023-06-21.tar.bz2"
//        ));
//        models.add(new ModelItem(
//                "English Streaming Zipformer (medium)",
//                "https://github.com/k2-fsa/sherpa-onnx/releases/download/asr-models/sherpa-onnx-streaming-zipformer-en-2023-06-26.tar.bz2",
//                "sherpa-onnx-streaming-zipformer-en-2023-06-26.tar.bz2"
//        ));
        models.add(new ModelItem(
                "Zipformer English kroko", //French
                "https://github.com/k2-fsa/sherpa-onnx/releases/download/asr-models/sherpa-onnx-streaming-zipformer-en-kroko-2025-08-06.tar.bz2",
                "sherpa-onnx-streaming-zipformer-en-kroko-2025-08-06.tar.bz2"
        ));
        models.add(new ModelItem(
                "Zipformer German kroko",
                "https://github.com/k2-fsa/sherpa-onnx/releases/download/asr-models/sherpa-onnx-streaming-zipformer-de-kroko-2025-08-06.tar.bz2",
                "sherpa-onnx-streaming-zipformer-de-kroko-2025-08-06.tar.bz2"
        ));


        for (ModelItem m : models) {
            if (prefs.getBoolean(KEY_PREFIX_DOWNLOADED + m.getFileName(), false)) {
                if (getModelFile(m).exists()) {
                    m.setStatus(ModelItem.Status.DOWNLOADED);
                } else {
                    prefs.edit().putBoolean(KEY_PREFIX_DOWNLOADED + m.getFileName(), false).apply();
                }
            }
        }
    }

    public List<ModelItem> getModels() {
        return models;
    }

    public File getModelDir() {
        File parent = ModelRepository.USE_EXTERNAL_FILES ? context.getExternalFilesDir(null) : context.getFilesDir();
        File dir = new File(parent, ModelRepository.MODELS_FOLDER_NAME);
        if (!dir.exists()) dir.mkdirs();
        return dir;
    }

    public File getModelFile(ModelItem item) {
        return new File(getModelDir(), item.getFileName());
    }

    public void markDownloaded(ModelItem item, boolean downloaded) {
        prefs.edit().putBoolean(KEY_PREFIX_DOWNLOADED + item.getFileName(), downloaded).apply();
    }

    public void deleteModel(ModelItem item) {
        File f = getModelFile(item);
        if (f.exists()) f.delete();
        markDownloaded(item, false);
        item.setStatus(ModelItem.Status.NOT_DOWNLOADED);
        item.setProgress(0);
        item.setPaused(false);
    }
}
