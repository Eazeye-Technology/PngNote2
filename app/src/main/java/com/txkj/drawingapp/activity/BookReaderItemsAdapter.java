package com.txkj.drawingapp.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sys.speech.db.SDRecordingsDatabase;
import com.sys.speech.pojo.RecordingItem;
import com.txkj.drawingapp.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BookReaderItemsAdapter extends BaseAdapter implements SDRecordingsDatabase.OnDatabaseChangedListener {
    private LayoutInflater mInflater;

    private Context mContext;
    private SDRecordingsDatabase mDatabase;
    private static final SimpleDateFormat mDateAddedFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    private static final SimpleDateFormat mDateFormat = new SimpleDateFormat("mm:ss", Locale.getDefault());

    private String mMeetingId;
    private String mAgendaId;

    public BookReaderItemsAdapter(Context context, String meetingId, String agendaId, String dirPath) {
        this.mInflater = LayoutInflater.from(context);
        mContext = context;
        mDatabase = new SDRecordingsDatabase(context, dirPath);
        mDatabase.setOnDatabaseChangedListener(this);
        mMeetingId = meetingId;
        mAgendaId = agendaId;
    }

    @Override
    public int getCount() {
        if (mDatabase != null) {
            return mDatabase.getCount(this.mMeetingId, this.mAgendaId);
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return mDatabase.getItemAt(position, this.mMeetingId, this.mAgendaId);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.activity_book4_item_recording, null);
            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.date = (TextView) convertView.findViewById(R.id.date);
            holder.llTop = (LinearLayout) convertView.findViewById(R.id.llTop);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        RecordingItem item = (RecordingItem)getItem(position);
        if (item != null) {
            if (item.getRecType() != null && item.getRecType().equals("text")) {
                holder.title.setText(item.getRecContent() != null ? item.getRecContent().trim() : "");
            } else {
                holder.title.setText(item.getName() != null ? item.getName().trim() : "");
            }
            holder.date.setText(getTime(item.getTime()));
            //lengthView.setText(getLengthString(item.getLength()));
//            if (holder.title.getText().toString().length() > 0) {
//                holder.llTop.setVisibility(View.VISIBLE);
//            } else {
//                holder.llTop.setVisibility(View.GONE);
//            }
        } else {
            holder.title.setText("");
//            holder.llTop.setVisibility(View.GONE);
        }
        return convertView;
    }

    public static String getTime(long milliSeconds) {
        Date date = new Date(milliSeconds);
        return mDateAddedFormatter.format(date);
    }

    private static final class ViewHolder {
        TextView title;
        TextView date;
        LinearLayout llTop;
    }

    @Override
    public void onDatabaseEntryUpdated() {
        this.notifyDataSetChanged();
    }

    public void remove(RecordingItem item) {
        mDatabase.removeItemWithId(item.getId(), this.mMeetingId, this.mAgendaId);
    }
}
