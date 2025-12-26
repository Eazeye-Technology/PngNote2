package com.txkj.drawingapp.activity;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.txkj.drawingapp.R;
import com.txkj.notemobile2.ui.Page;

import java.util.List;

public class BookPageGridAdapter extends BaseAdapter {
    public int pageIndex = -1;

    public int checkMode = 0;
    public final static int CHECK_MODE_NONE = 0;
    public final static int CHECK_MODE_CHECK = 1;
    public final static int CHECK_MODE_MOVE = 2;

    private Context context;
    private GridViewHolder gridholder;
    private List<Page> dataList;

    public BookPageGridAdapter(Context context, List<Page> results) {
        this.context = context;
        this.dataList = results;
    }

    @Override
    public int getCount() {
        if (dataList != null) {
            return dataList.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(this.context, R.layout.activity_book4_pagegridviewitem, null);
            gridholder = new GridViewHolder();
            gridholder.tfBookName = (TextView) convertView.findViewById(R.id.bookgrid_name);
            gridholder.cardViewName = (CardView) convertView.findViewById(R.id.cardViewName);
            gridholder.ivCoverImage = (ImageView) convertView.findViewById(R.id.bookgrid_pic);
            gridholder.ivCoverImageBack = (ImageView) convertView.findViewById(R.id.bookgrid_pic_backgroud);
            gridholder.rlCheckBox = (RelativeLayout) convertView.findViewById(R.id.rlCheckBox);
            gridholder.rlMove = (RelativeLayout) convertView.findViewById(R.id.rlMove);
            gridholder.cbSel = (CheckBox) convertView.findViewById(R.id.cbSel);
            gridholder.topView = (LinearLayout) convertView.findViewById(R.id.topView);
            convertView.setTag(gridholder);
        } else {
            gridholder = (GridViewHolder) convertView.getTag();
        }

        //hide时隐藏Text
        if (position != hidePosition) {
            gridholder.topView.setVisibility(View.VISIBLE);
        } else {
            gridholder.topView.setVisibility(View.INVISIBLE);
        }

        if (dataList != null) {
            Page page = dataList.get(position);
            if (page != null) {
                gridholder.tfBookName.setText(page.getTitle() != null ? page.getTitle() : "no title");

                if (position == pageIndex) {
                    gridholder.cardViewName.setCardBackgroundColor(0xFFE0E0E0);
                    gridholder.tfBookName.setTextColor(0xFF000000);
                } else {
                    gridholder.cardViewName.setCardBackgroundColor(0xFF000000);
                    gridholder.tfBookName.setTextColor(0xFFFFFFFF);
                }
                gridholder.ivCoverImage.setImageBitmap(page.getThumbnail());
                gridholder.ivCoverImageBack.setImageBitmap(page.getBgThumbnail());
                if (page.checked) {
                    gridholder.cbSel.setChecked(true);
                } else {
                    gridholder.cbSel.setChecked(false);
                }
            }
        }

        if (checkMode == CHECK_MODE_CHECK) {
            gridholder.rlCheckBox.setVisibility(View.VISIBLE);
            gridholder.rlMove.setVisibility(View.GONE);
        } else if (checkMode == CHECK_MODE_MOVE) {
            gridholder.rlCheckBox.setVisibility(View.GONE);
            gridholder.rlMove.setVisibility(View.VISIBLE);
        } else {
            gridholder.rlCheckBox.setVisibility(View.GONE);
            gridholder.rlMove.setVisibility(View.GONE);
        }

        return convertView;
    }

    private final static class GridViewHolder {
        private TextView tfBookName;
        private ImageView ivCoverImage;
        private ImageView ivCoverImageBack;
        private RelativeLayout rlCheckBox, rlMove;
        private CheckBox cbSel;
        private LinearLayout topView;
        private CardView cardViewName;
    }

    private int hidePosition = AdapterView.INVALID_POSITION;
    public void hideView(int pos) {
        hidePosition = pos;
        notifyDataSetChanged();
    }
    public void showHideView() {
        hidePosition = AdapterView.INVALID_POSITION;
        notifyDataSetChanged();
    }
    //更新拖动时的gridView
    public void swapView(int draggedPos, int destPos) {
        //从前向后拖动，其他item依次前移
        if(draggedPos < destPos) {
            dataList.add(destPos + 1, dataList.get(draggedPos));
            dataList.remove(draggedPos);
        }
        //从后向前拖动，其他item依次后移
        else if(draggedPos > destPos) {
            dataList.add(destPos, dataList.get(draggedPos));
            dataList.remove(draggedPos + 1);
        }
        hidePosition = destPos;
        notifyDataSetChanged();
    }
}
