package com.example.sherpaasr.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sherpaasr.data.ModelItem;
import com.example.sherpaasr.data.ModelRepository;
import com.example.sherpaasr.utils.DownloadManager;
import com.txkj.drawingapp.R;

import java.io.File;

public class ModelsFragment extends Fragment {

    public interface ModelSelectListener {
        void onModelSelected(ModelItem item);
    }

    private ModelRepository repository;
    private DownloadManager downloadManager;
    private ModelsAdapter adapter;
    private ModelSelectListener selectListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof ModelSelectListener) {
            selectListener = (ModelSelectListener) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_models, container, false);
        return inflate;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        repository = new ModelRepository(requireContext());
        downloadManager = new DownloadManager();

        RecyclerView rv = view.findViewById(R.id.recyclerModels);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new ModelsAdapter(repository.getModels(), new ModelsAdapter.ActionListener() {
            @Override public void onDownload(ModelItem item) { startDownload(item); }
            @Override public void onPause(ModelItem item) { downloadManager.pause(item); adapter.notifyDataSetChanged(); }
            @Override public void onResume(ModelItem item) { downloadManager.resume(item, repository.getModelFile(item), cb); adapter.notifyDataSetChanged(); }
            @Override public void onDelete(ModelItem item) {
                downloadManager.cancel(item);
                repository.deleteModel(item);
                adapter.notifyDataSetChanged();
                Toast.makeText(requireContext(), "Deleted", Toast.LENGTH_SHORT).show();
            }
            @Override public void onSelect(ModelItem item) {
                if (selectListener != null) selectListener.onModelSelected(item);
                Toast.makeText(requireContext(), "Select: " + item.getName(), Toast.LENGTH_SHORT).show();
            }
        });
        rv.setAdapter(adapter);
    }

    private void startDownload(ModelItem item) {
        item.setStatus(ModelItem.Status.DOWNLOADING);
        item.setPaused(false);
        adapter.notifyDataSetChanged();
        File dest = repository.getModelFile(item);
        downloadManager.download(item, dest, cb);
    }

    private final DownloadManager.Callback cb = new DownloadManager.Callback() {
        @Override public void onProgress(ModelItem item, int percent) {
            int idx = repository.getModels().indexOf(item);
            if (idx >= 0) adapter.notifyItemChanged(idx);
        }
        @Override public void onComplete(ModelItem item, boolean success, String message) {
            if (success) repository.markDownloaded(item, true);
            else item.setStatus(ModelItem.Status.NOT_DOWNLOADED);
            adapter.notifyDataSetChanged();
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
        }
    };
}
