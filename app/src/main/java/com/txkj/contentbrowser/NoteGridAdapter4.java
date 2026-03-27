package com.txkj.contentbrowser;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.foobnix.android.utils.Dips;
import com.foobnix.android.utils.LOG;
import com.foobnix.android.utils.TxtUtils;
import com.foobnix.dao2.FileMeta;
import com.foobnix.pdf.info.IMG;
import com.txkj.drawingapp.R;

import java.util.ArrayList;
import java.util.List;

public class NoteGridAdapter4 extends RecyclerView.Adapter<NoteGridAdapter4.GridViewHolder> {
    private final static int SINGLE_GRID_DP_WIDTH = 120;
    public final static int SINGLE_GRID_PX_WIDTH_PORT = 200;
    public final static int SINGLE_GRID_PX_HEIGHT_PORT = 300;
    public final static int SINGLE_GRID_PX_WIDTH_LAND = 300;
    public final static int SINGLE_GRID_PX_HEIGHT_LAND = 200;

    public int gridHeight = 0;
    AdapterView.OnItemClickListener mListener = null;
    public void setOnItemClickListener2(AdapterView.OnItemClickListener listener) {
        this.mListener = listener;
    }

    public void clearItems() {
        if (dataList != null) {
            dataList.clear();
        }
    }
    public List<FileMeta> getItemsList() {
        if (dataList == null) {
            dataList = new ArrayList<>();
        }
        return dataList;
    }

    private Context mContext;
    //private GridViewHolder gridholder;
    private List<FileMeta> dataList;
    DisplayMetrics DM = new DisplayMetrics();

    private AdapterView.OnItemLongClickListener mLongClick;
    public void setOnItemLongClickListener(AdapterView.OnItemLongClickListener longClick) {
        this.mLongClick = longClick;
    }

    private AdapterView.OnItemClickListener mItemClick;
    public void setOnItemClickListener(AdapterView.OnItemClickListener itemCLick) {
        this.mItemClick = itemCLick;
    }

    public NoteGridAdapter4(Context context, List<FileMeta> results) {
        this.mContext = context;
        this.dataList = results;

        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(DM);
    }

    @NonNull
    @Override
    public GridViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View root_view = LayoutInflater.from(mContext)
                .inflate(R.layout.pagegridviewitem_note_grid4, parent, false);
        return new GridViewHolder(root_view);
    }

    @Override
    public void onBindViewHolder(@NonNull GridViewHolder gridholder, int position_) {
        gridholder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (mItemClick != null) {
                        mItemClick.onItemClick(null, view, gridholder.getBindingAdapterPosition(), 0L);
                    }
                } catch (Throwable eee) {
                    eee.printStackTrace();
                }
            }
        });
        gridholder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                try {
                    if (mLongClick != null) {
                        mLongClick.onItemLongClick(null, view, gridholder.getBindingAdapterPosition(), 0L);
                        return true;
                    }
                } catch (Throwable eee) {
                    eee.printStackTrace();
                }
                return false;
            }
        });
        if (dataList != null) {
            int position = gridholder.getBindingAdapterPosition();
            gridholder.getAbsoluteAdapterPosition();
            FileMeta fileMeta = dataList.get(position);
            if (fileMeta != null) {
                bindFileMetaView(gridholder, position);
                boolean needRefresh = TxtUtils.isEmpty(fileMeta.getPathTxt());
                int imageSize = 0;
                if (true) {
                    imageSize = IMG.getImageSize();
                } else {
                    imageSize = Dips.dpToPx(gridholder.ivCoverImage.getMeasuredWidth());
                }
                if (false) {
//                    gridholder.ivCoverImage.setImageResource(R.drawable.glyphicons_144_database_search);
//                    bindFileMetaView(gridholder, position);
                } else {
                    IMG.getCoverPageWithEffect(gridholder.ivCoverImage, fileMeta.getPath(), imageSize, new IMG.ResourceReady() {
                        @Override
                        public void onResourceReady(Bitmap bitmap) {
                            try {
                                bindFileMetaView(gridholder, position);
                            } catch (Exception e) {
                                LOG.e(e);
                            }
//                            try {
//                                if (dataList != null && position < dataList.size() && needRefresh) {
//                                    FileMeta it = AppDB.get().load(fileMeta.getPath());
//                                    if (it != null) {
//                                        dataList.set(position, it);
//                                        bindFileMetaView(gridholder, position);
//                                    }
//                                }
//                            } catch (Exception e) {
//                                LOG.e(e);
//                            }
                        }
                    });
                }
            }
        }
        // 设置固定高度
//        if (gridHeight != 0) {
//            ViewGroup.LayoutParams params = convertView.getLayoutParams();
//            if (params != null) {
//                params.height = gridHeight; //100; // 假设你想要的固定高度是100dp
//                convertView.setLayoutParams(params);
//            }
//        }
//        convertView.findViewById(R.id.llGridTop).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //Toast.makeText(context, "note grid click", Toast.LENGTH_LONG).show();
//                if (mListener != null) {
//                    mListener.onItemClick(null, view, position, (long)position);
//                }
//            }
//        });
//        convertView.findViewById(R.id.llGridTop).requestFocus();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
/*
getItem()
return position;
 */
    @Override
    public int getItemCount() {
        if (dataList != null) {
            return dataList.size();
        } else {
            return 0;
        }
    }

    //@see com.foobnix.ui2.adapter.FileMetaAdapter
//    @Override
//    public View getView(final int position, View convertView, ViewGroup parent) {
//
//    }

    private FileMeta bindFileMetaView(final GridViewHolder holder, final int position) {
        if (dataList == null || position >= dataList.size()) {
            return new FileMeta();
        }
        final FileMeta fileMeta = (FileMeta)dataList.get(position);
        if (fileMeta == null) {
            return new FileMeta();
        }
        String path = fileMeta.getPathTxt() != null ? fileMeta.getPathTxt() : "";
        if (path.endsWith(".xopp")) {
            path = path.substring(0, path.length() - ".xopp".length());
        }
        String name = fileMeta.getTitle();
        //holder.tfBookName.setText(path);
        holder.tfBookName.setText(name != null ? name : "");
//      holder.ivCoverImage.setImageBitmap(page.getThumbnail());
//      holder.ivCoverImageBack.setImageBitmap(page.getBgThumbnail());
        if (true) { //if (AppState.get().isCropBookCovers) {
            holder.ivCoverImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            holder.ivCoverImage.setScaleType(ImageView.ScaleType.FIT_CENTER); //
        }
        if (fileMeta.checkShow) {
            holder.checkBox.setVisibility(View.VISIBLE);
        } else {
            holder.checkBox.setVisibility(View.GONE);
        }
        if (fileMeta.getCheckSelect()) {
            holder.checkBox.setChecked(true);
        } else {
            holder.checkBox.setChecked(false);
        }
        return fileMeta;
    }

    public class GridViewHolder extends RecyclerView.ViewHolder {
        private TextView tfBookName;
        private ImageView ivCoverImage;
        private ImageView ivCoverImageBack;
        private LinearLayout llGridTop;
        private CheckBox checkBox;

        public GridViewHolder(@NonNull View itemView) {
            super(itemView);
            tfBookName = (TextView) itemView.findViewById(R.id.bookgrid_name_library);
            ivCoverImage = (ImageView) itemView.findViewById(R.id.browserItemIcon_library);
//            ivCoverImageBack = (ImageView) itemView.findViewById(R.id.bookgrid_pic_backgroud);
            llGridTop = (LinearLayout) itemView.findViewById(R.id.llGridTop);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkbox);
            if (true) {
                ViewGroup.LayoutParams params2 = new AbsListView.LayoutParams(
                        AbsListView.LayoutParams.MATCH_PARENT,
                        AbsListView.LayoutParams.MATCH_PARENT);
                if (DM.heightPixels > DM.widthPixels) {
                    //port
                    //params2.height = (int)(DM.heightPixels / 2.8);//3.5);
                    params2.height = SINGLE_GRID_PX_HEIGHT_PORT;
                    params2.width = SINGLE_GRID_PX_WIDTH_PORT;
                } else {
                    //land
                    //params2.height = (int)(DM.widthPixels / 2.0 / 1.6);//2.5);
                    params2.height = SINGLE_GRID_PX_HEIGHT_LAND;
                    params2.width = SINGLE_GRID_PX_WIDTH_LAND;
                }
                itemView.setLayoutParams(params2);
            }
        }
    }
}
