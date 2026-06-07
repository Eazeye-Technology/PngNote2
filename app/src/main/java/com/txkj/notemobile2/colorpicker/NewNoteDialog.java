package com.txkj.notemobile2.colorpicker;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.txkj.drawingapp.activity.BookActivity4Utils;
import com.txkj.drawingapp.R;

import java.util.ArrayList;
import java.util.List;

public class NewNoteDialog extends Dialog {
    //FIXME:
    private final static int SINGLE_GRID_DP_WIDTH = 100;

    List<FileMeta> recentNoteList;
    private GridView recentNoteView;
    private NoteListAdapter recentNoteAdapter;
    EditText g_textState = null;
    Button btnCreate = null;

    public NewNoteDialog(@NonNull final Context context, String defaultNoteName) {
        super(context);

        this.setContentView(R.layout.dialog_loadpages2);
        this.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        g_textState = this.findViewById(R.id.textState);
        Button btnCancel = this.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewNoteDialog.this.dismiss();
            }
        });
        btnCreate = this.findViewById(R.id.btnCreate);
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textState = "";
                if (g_textState != null) {
                    textState = g_textState.getText().toString();
                }
                if (textState.length() > 0) {
//                    if (onNewBook != null) {
//                        onNewBook.onNewBook(textState);
//                    }
                    String backText = recentNoteAdapter.getSelectText();
                    if (BookActivity4Utils.checkText(context, textState)) {
                        NewNoteDialog.this.dismiss();
                        BookActivity4Utils.onNewBook(context, textState, backText);
                    } else {
                        Toast.makeText(context, "Duplicate note name, case insensitive", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "Please input new note name", Toast.LENGTH_SHORT).show();
                }
            }
        });

        recentNoteList = new ArrayList<FileMeta>();
        recentNoteList.add(new FileMeta(FileMeta.NONE, 0));
        recentNoteList.add(new FileMeta(FileMeta.LINED, R.drawable.paint_lined));
        recentNoteList.add(new FileMeta(FileMeta.GRAPH, R.drawable.paint_graph));
        recentNoteList.add(new FileMeta(FileMeta.DOTTED, R.drawable.paint_dotted));
//        for (int i = 0; i < 100; ++i) {
//            recentNoteList.add(new FileMeta("None" + i, 0));
//        }
        recentNoteView = (GridView) findViewById(R.id.notegridview_home);
        recentNoteView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        recentNoteView.setBackgroundColor(Color.WHITE);
        recentNoteAdapter = new NoteListAdapter(this.getContext(), recentNoteList);
        recentNoteView.setAdapter(recentNoteAdapter);
        int size = 3;
        if (recentNoteAdapter.getCount() > 0) {
            size = recentNoteAdapter.getCount();
            recentNoteView.setNumColumns(size);
        } else {
            recentNoteView.setNumColumns(3);
        }
        int gridviewWidth = size * Dips.dpToPx(SINGLE_GRID_DP_WIDTH);
        //https://blog.csdn.net/zhuwentao2150/article/details/70211610
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                gridviewWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
        recentNoteView.setLayoutParams(params);
        recentNoteView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                recentNoteAdapter.select(position);
            }
        });
        if (defaultNoteName != null && g_textState != null) {
            g_textState.setText(defaultNoteName);
            g_textState.selectAll();
        }
    }
}
