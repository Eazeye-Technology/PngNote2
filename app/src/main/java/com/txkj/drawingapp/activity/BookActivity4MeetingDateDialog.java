package com.txkj.drawingapp.activity;

import android.app.Dialog;

import androidx.fragment.app.FragmentActivity;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.txkj.notemobile2.BookListActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BookActivity4MeetingDateDialog {
    private FragmentActivity mAct;
    private Long mStartDate;
    public BookActivity4MeetingDateDialog(FragmentActivity context, Long startDate) {
        this.mAct = context;
        this.mStartDate = startDate;
    }

    //https://www.cnblogs.com/stars-one/p/17841549.html
    public void create() {
        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
        if (mStartDate != null) {
            builder.setSelection(mStartDate);
        }
        builder.setTitleText("Meeting Date");
        MaterialDatePicker<Long> materialDatePicker = builder.build();
        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
            @Override
            public void onPositiveButtonClick(Long selection) {
                String dateStr = null;
                if (selection != null) {
                    Date date = new Date(selection);
                    SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);
                    dateStr = sdf.format(date);
                }
                if (dateStr != null) {
                    BookActivity4Utils.editMeetingDate(mAct, dateStr, selection);
                }
                //todo
            }
        });
        materialDatePicker.show(this.mAct.getSupportFragmentManager(), "DATE_PICKER_TAG");
        //return materialDatePicker.getDialog();
    }
}
