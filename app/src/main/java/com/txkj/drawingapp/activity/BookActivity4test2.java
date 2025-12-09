package com.txkj.drawingapp.activity;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.txkj.drawingapp.R;

//https://zhuanlan.zhihu.com/p/691513009
public class BookActivity4test2 extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_bottom2);

        NestedScrollView arBottomSheet = this.findViewById(R.id.ar_bottom_sheet);

        View ar_recording_check_view = findViewById(R.id.ar_recording_check_view);
        BottomSheetBehavior<NestedScrollView> behavior = BottomSheetBehavior.from(arBottomSheet);
        ar_recording_check_view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (behavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
                return false;
            }
        });
    }
}
