package com.txkj.drawingapp.activity;

import android.view.View;

import androidx.fragment.app.FragmentActivity;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BookActivity4MeetingTimeDialog {
    private FragmentActivity mAct;
    private Integer mStartHour, mStartMinute;
    public BookActivity4MeetingTimeDialog(FragmentActivity context, Integer startHour, Integer startMinute) {
        this.mAct = context;
        this.mStartHour = startHour;
        this.mStartMinute = startMinute;
    }

    //https://www.cnblogs.com/stars-one/p/17841549.html
    public void create() {
        MaterialTimePicker.Builder builder = new MaterialTimePicker.Builder();
        builder.setTimeFormat(TimeFormat.CLOCK_24H);
        if (mStartHour != null) {
            builder.setHour(mStartHour);
        }
        if (mStartMinute != null) {
            builder.setMinute(mStartMinute);
        }
        builder.setTitleText("Meeting Time");
        MaterialTimePicker materialTimePicker = builder.build();
        materialTimePicker.addOnPositiveButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int hour = materialTimePicker.getHour();
                int minute = materialTimePicker.getMinute();
                BookActivity4Utils.editMeetingTime(mAct, hour, minute);
            }
        });
        materialTimePicker.show(this.mAct.getSupportFragmentManager(), "DATE_PICKER_TAG");
        //return materialDatePicker.getDialog();
    }
}
