package com.txkj.drawingapp.activity;

import android.inputmethodservice.InputMethodService;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.txkj.drawingapp.R;

public class MyInputMethodService extends InputMethodService {
    @Override
    public View onCreateInputView() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View inputView = inflater.inflate(R.layout.input_view, null);
        setupTopMenu(inputView);
        return inputView; //super.onCreateInputView();
    }
    private void setupTopMenu(View inputView) {
        Button topMenuButton = inputView.findViewById(R.id.top_menu_button);
        topMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MyInputMethodService.this, "hello", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
