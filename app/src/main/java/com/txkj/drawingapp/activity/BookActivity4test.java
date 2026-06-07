package com.txkj.drawingapp.activity;

import android.os.Bundle;
import android.widget.CheckBox;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.txkj.drawingapp.R;

//https://zhuanlan.zhihu.com/p/691513009
public class BookActivity4test extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_bottom);
        NestedScrollView arBottomSheet = this.findViewById(R.id.ar_bottom_sheet);
        CheckBox arRecordingCheck = findViewById(R.id.ar_recording_check);

        BottomSheetBehavior<NestedScrollView> behavior = BottomSheetBehavior.from(arBottomSheet);

        arRecordingCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!buttonView.isPressed()) return;
            int se = BottomSheetBehavior.STATE_COLLAPSED;
            //int se = BottomSheetBehavior.STATE_HIDDEN;
            if (isChecked) se = BottomSheetBehavior.STATE_EXPANDED;
            behavior.setState(se);
        });
    }
}
