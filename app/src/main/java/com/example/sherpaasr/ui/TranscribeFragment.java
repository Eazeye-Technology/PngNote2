package com.example.sherpaasr.ui;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.sherpaasr.data.ModelItem;
import com.example.sherpaasr.service.TranscriptionService;
import com.google.android.material.button.MaterialButton;
import com.txkj.drawingapp.R;

public class TranscribeFragment extends Fragment {

    private TextView tvTranscription;
    private TextView tvSelectedModel;
    private MaterialButton btnRecord;
    private ModelItem selectedModel;

    private boolean isRecording = false;
    private TranscriptionService transcriptionService;
    private boolean serviceBound = false;

    private final ServiceConnection connection = new ServiceConnection() {
        @Override public void onServiceConnected(ComponentName name, IBinder service) {
            TranscriptionService.LocalBinder binder = (TranscriptionService.LocalBinder) service;
            transcriptionService = binder.getService();
            serviceBound = true;
            transcriptionService.setCallback(result -> {
                if (tvTranscription != null) {
                    tvTranscription.post(() -> tvTranscription.append(result + " "));
                }
            });
        }
        @Override public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }
    };

    private final ActivityResultLauncher<String> permissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(), granted -> {
                if (granted) startRecordingInternal();
                else Toast.makeText(requireContext(), R.string.permission_required, Toast.LENGTH_SHORT).show();
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_transcribe, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvTranscription = view.findViewById(R.id.tvTranscription);
        tvSelectedModel = view.findViewById(R.id.tvSelectedModel);
        btnRecord = view.findViewById(R.id.btnRecord);
        btnRecord.setOnClickListener(v -> toggleRecording());
    }

    public void setSelectedModel(ModelItem model) {
        this.selectedModel = model;
        if (tvSelectedModel != null) {
            tvSelectedModel.setText("Current model: " + (model != null ? model.getName() : "无"));
        }
    }

    private void toggleRecording() {
        if (isRecording) {
            stopRecording();
        } else {
            if (selectedModel == null) {
                Toast.makeText(requireContext(), "Please Download and choose a model", Toast.LENGTH_SHORT).show();
                return;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    permissionLauncher.launch(Manifest.permission.RECORD_AUDIO);
                    return;
                }
            }
            startRecordingInternal();
        }
    }

    private void startRecordingInternal() {
        tvTranscription.setText("");
        Intent intent = new Intent(requireContext(), TranscriptionService.class);
        intent.putExtra(TranscriptionService.EXTRA_MODEL_PATH, selectedModel.getFileName());
        requireContext().startService(intent);
        requireContext().bindService(intent, connection, Context.BIND_AUTO_CREATE);
        isRecording = true;
        btnRecord.setText(R.string.finish_recording);
    }

    private void stopRecording() {
        if (serviceBound) {
            transcriptionService.stopTranscription();
            requireContext().unbindService(connection);
            serviceBound = false;
        }
        Intent intent = new Intent(requireContext(), TranscriptionService.class);
        requireContext().stopService(intent);
        isRecording = false;
        btnRecord.setText(R.string.start_recording);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (isRecording) stopRecording();
    }
}
