package com.txkj.contentbrowser;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.txkj.drawingapp.R;

//https://developer.aliyun.com/article/862974
//https://github.com/han1202012/001_RecyclerView
public class ReAdapter extends RecyclerView.Adapter<ReAdapter.ViewHolder> {
    private Context mContext;
    public ReAdapter(Context context) {
        this.mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View root_view = LayoutInflater.from(mContext)
                .inflate(R.layout.item_recyclerview, parent, false);
        return new ViewHolder(root_view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.text.setText("" + position);
    }
    @Override
    public int getItemCount() {
        return 100;
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView text;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.text);
        }
    }
}
