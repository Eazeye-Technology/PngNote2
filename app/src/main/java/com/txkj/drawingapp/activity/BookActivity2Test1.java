package com.txkj.drawingapp.activity;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.txkj.drawingapp.R;

public class BookActivity2Test1 extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book2_test);
        ActionBar topAppBar = getSupportActionBar();
        if (topAppBar != null) {
            topAppBar.setIcon(R.mipmap.ic_launcher_drawingapp);
            topAppBar.setHomeButtonEnabled(true);
            topAppBar.setDisplayHomeAsUpEnabled(true);
            topAppBar.hide();
        }
    }
}
