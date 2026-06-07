package com.txkj.drawingapp.activity;

import android.content.Context;
import android.icu.math.BigDecimal;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.k2fsa.sherpa.onnx.OfflineSpeakerDiarizationSegment;
import com.sys.speech.db.SDRecordingsDatabase;
import com.sys.speech.pojo.RecordingItem;
import com.txkj.drawingapp.R;

import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BookReaderItemsAdapter extends BaseAdapter implements SDRecordingsDatabase.OnDatabaseChangedListener {
    private LayoutInflater mInflater;

    List<OfflineSpeakerDiarizationSegment> m_segmentList = null;
    public void setDiarization(List<OfflineSpeakerDiarizationSegment> segmentList) {
        m_segmentList = segmentList;
    }

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

    public SDRecordingsDatabase getDB() {
        return this.mDatabase;
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

            holder.titleTime = (TextView) convertView.findViewById(R.id.titleTime);
            holder.titleSpeaker = (Button) convertView.findViewById(R.id.titleSpeaker);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        RecordingItem item = (RecordingItem)getItem(position);
        if (item != null) {
            if (item.getRecType() != null && item.getRecType().equals("text")) {
                //normally run here
                String content = item.getRecContent() != null ? item.getRecContent().trim() : "";
                final boolean USE_ONE_LINE = false;

                //content = ">>>content<<<";
                if (m_segmentList != null && m_segmentList.size() > 0 &&
                        item.getName() != null && item.getName().startsWith("sherpa-")) {

                    String timeStrSimple = item.getName().substring("sherpa-".length());
                    String speakerName = "?";
                    float timeSimple = -1;
                    try {
                        timeSimple = Float.parseFloat(timeStrSimple);
                        if (m_segmentList != null) {
                            for (OfflineSpeakerDiarizationSegment seg : m_segmentList) {
                                if (seg != null) {
                                    if (timeSimple >= 0) {
                                        if (timeSimple >= seg.getStart() &&
                                            timeSimple < seg.getEnd()) {
                                            //speakerName = "Speaker_" + seg.getSpeaker();
                                            speakerName = "Speaker " + (seg.getSpeaker() + 1);
                                        }
                                    }
                                }
                            }
                            if (speakerName.equals("?")) {
                                final float deltaTime = 1.0f; //1 second
                                for (int i = 0; i < m_segmentList.size(); ++i) {
                                    OfflineSpeakerDiarizationSegment seg = m_segmentList.get(i);
                                    if (seg != null) {
//                                        if (timeSimple >= 0) {
//                                            if (timeSimple < seg.getStart() &&
//                                                    timeSimple > seg.getStart() - 3) {
//                                                //speakerName = "Speaker_" + seg.getSpeaker();
//                                                speakerName = "Speaker " + (seg.getSpeaker() + 1);
//                                            }
//                                        }
                                        if (timeSimple - deltaTime >= 0) {
                                            if (timeSimple - deltaTime >= seg.getStart() &&
                                                    timeSimple - deltaTime < seg.getEnd()) {
                                                //speakerName = "Speaker_" + seg.getSpeaker();
                                                speakerName = "Speaker " + (seg.getSpeaker() + 1);
                                            }
                                        }
                                    }
                                }
                            }
                            if (speakerName.equals("?")) {
                                final float deltaTime = 1.5f; //1.5 second
                                for (int i = 0; i < m_segmentList.size(); ++i) {
                                    OfflineSpeakerDiarizationSegment seg = m_segmentList.get(i);
                                    if (seg != null) {
//                                        if (timeSimple >= 0) {
//                                            if (timeSimple < seg.getStart() &&
//                                                    timeSimple > seg.getStart() - 3) {
//                                                //speakerName = "Speaker_" + seg.getSpeaker();
//                                                speakerName = "Speaker " + (seg.getSpeaker() + 1);
//                                            }
//                                        }
                                        if (timeSimple - deltaTime >= 0) {
                                            if (timeSimple - deltaTime >= seg.getStart() &&
                                                    timeSimple - deltaTime < seg.getEnd()) {
                                                //speakerName = "Speaker_" + seg.getSpeaker();
                                                speakerName = "Speaker " + (seg.getSpeaker() + 1);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } catch (Throwable eee) {}

                    String timeStr = "";
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        BigDecimal seconds = new BigDecimal(timeStrSimple/*"123.456"*/);
                        int minutes = seconds.divide(new BigDecimal("60"), 0).intValue();
                        BigDecimal remainingSeconds = seconds.remainder(new BigDecimal("60"));
                        BigDecimal fractionalSeconds = seconds.subtract(BigDecimal.valueOf(minutes * 60))
                                .subtract(BigDecimal.valueOf(remainingSeconds.intValue()));
                        int milliseconds = fractionalSeconds.movePointRight(3).intValue();

                        timeStr = String.format("%02d:%02d.%01d", minutes, remainingSeconds.intValue(), (int)(milliseconds / 100));
                    } else {
                        double seconds = 123.456;
                        int minutes = (int) (seconds / 60);
                        int remainingSeconds = (int) (seconds % 60);
                        double fractionalSeconds = seconds - Math.floor(seconds);
                        int milliseconds = (int) (fractionalSeconds * 1000);

                        timeStr = String.format("%02d:%02d.%01d", minutes, remainingSeconds, (int)(milliseconds / 100));
                    }

                    if (USE_ONE_LINE) {
                        content = "[" + speakerName + "]" + timeStr
                                + "\n" + content;
                        holder.title.setText(content);
                        holder.titleTime.setVisibility(View.GONE);
                        holder.titleSpeaker.setVisibility(View.GONE);
                    } else {
                        holder.title.setText(content);
                        holder.titleTime.setText(timeStr);
                        holder.titleTime.setVisibility(View.VISIBLE);
                        holder.titleSpeaker.setText(speakerName);
                        holder.titleSpeaker.setVisibility(View.VISIBLE);
                    }
                } else {
                    holder.title.setText(content);
                    holder.titleTime.setVisibility(View.GONE);
                    holder.titleSpeaker.setVisibility(View.GONE);
                }
            } else {
                String name = item.getName() != null ? item.getName().trim() : "";
                //name = ">>>name<<<";
                holder.title.setText(name);
            }
            holder.date.setText(getTime(item.getTime()));
            //lengthView.setText(getLengthString(item.getLength()));
            if (true) {
                if (holder.title.getText().toString().length() > 0) {
                    holder.llTop.setVisibility(View.VISIBLE);
                } else {
                    holder.llTop.setVisibility(View.GONE);
                }
            } else {
                holder.llTop.setVisibility(View.VISIBLE);
            }
        } else {
            holder.title.setText("");
            holder.llTop.setVisibility(View.GONE);
        }
        return convertView;
    }

    public static String getTime(long milliSeconds) {
        Date date = new Date(milliSeconds);
        return mDateAddedFormatter.format(date);
    }

    private static final class ViewHolder {
        Button titleSpeaker;
        TextView titleTime;
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
