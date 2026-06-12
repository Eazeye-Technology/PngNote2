package com.example.sherpaasr.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sherpaasr.data.ModelItem;
import com.google.android.material.button.MaterialButton;
import com.txkj.drawingapp.R;

import java.util.List;

public class ModelsAdapter extends RecyclerView.Adapter<ModelsAdapter.VH> {

    public interface ActionListener {
        void onDownload(ModelItem item);
        void onPause(ModelItem item);
        void onResume(ModelItem item);
        void onDelete(ModelItem item);
        void onSelect(ModelItem item);
    }

    private final List<ModelItem> items;
    private final ActionListener listener;

    public ModelsAdapter(List<ModelItem> items, ActionListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_model, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        ModelItem item = items.get(position);
        h.tvName.setText(item.getName());
        h.tvStatus.setText(statusText(item));
        h.progress.setVisibility(item.getStatus() == ModelItem.Status.DOWNLOADING ? View.VISIBLE : View.GONE);
        h.progress.setProgress(item.getProgress());

        switch (item.getStatus()) {
            case NOT_DOWNLOADED:
                h.btnAction.setText(R.string.download);
                h.btnAction.setOnClickListener(v -> listener.onDownload(item));
                break;
            case DOWNLOADING:
                if (item.getStatusExtract()) {
                    h.tvStatus.setText("Extracting, please wait...");
                    h.btnAction.setText(item.isPaused() ? R.string.resume : R.string.pause);
                    h.btnAction.setOnClickListener(v -> {
                        //skip
                    });
                } else {
                    h.btnAction.setText(item.isPaused() ? R.string.resume : R.string.pause);
                    h.btnAction.setOnClickListener(v -> {
                        if (item.isPaused()) listener.onResume(item);
                        else listener.onPause(item);
                    });
                }
                break;
            case DOWNLOADED:
                h.btnAction.setText("Use");
                h.btnAction.setOnClickListener(v -> listener.onSelect(item));
                break;
        }
        h.btnDelete.setOnClickListener(v -> listener.onDelete(item));
    }

    @Override
    public int getItemCount() { return items.size(); }

    private String statusText(ModelItem item) {
        switch (item.getStatus()) {
            case DOWNLOADED: return "Downloaded";
            case DOWNLOADING: return item.isPaused() ? "Paused " + item.getProgress() + "%" : "Downloading " + item.getProgress() + "%";
            default: return "Not downloaded";
        }
    }

    static class VH extends RecyclerView.ViewHolder {
        final TextView tvName, tvStatus;
        final ProgressBar progress;
        final MaterialButton btnAction, btnDelete;
        VH(View v) {
            super(v);
            tvName = v.findViewById(R.id.tvModelName);
            tvStatus = v.findViewById(R.id.tvModelStatus);
            progress = v.findViewById(R.id.progressDownload);
            btnAction = v.findViewById(R.id.btnAction);
            btnDelete = v.findViewById(R.id.btnDelete);
        }
    }
}
