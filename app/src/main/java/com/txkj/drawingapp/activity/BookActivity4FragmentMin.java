package com.txkj.drawingapp.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.txkj.drawingapp.R;

public class BookActivity4FragmentMin extends Fragment {
    private final static String TAG = "min";

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        long t0 = System.currentTimeMillis();
        View rootView = inflater.inflate(R.layout.activity_book4_aaa, container, false);
        long t1 = System.currentTimeMillis();
        Log.e(TAG, "oncreateview, t1== " + (t1 - t0));
        return rootView;
    }
}
