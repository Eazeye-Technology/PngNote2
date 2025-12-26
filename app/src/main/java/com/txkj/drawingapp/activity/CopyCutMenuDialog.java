package com.txkj.drawingapp.activity;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.txkj.drawingapp.R;

public class CopyCutMenuDialog {
    private static final int POPUP_OFFSET = 0;//30;

    private static View.OnClickListener mListener = null;
    private static PopupWindow popup;
    public static void show(Context context, View anchor, int pageIndex, int pageNum, final View.OnClickListener listener) {
        //View layout = View.inflate(context, R.layout.activity_main_menu1, null);
        View layout = View.inflate(context, R.layout.activity_book4_popup_menu, null);
        //View wv = layout.findViewById(R.id.wheel);

        mListener = listener;
        final int[] ids = {
                R.id.popButtonGrid,
                R.id.popButtonPrevPage,
                R.id.popButtonNextPage,
                R.id.popButtonRemovePage,
                R.id.popButtonAddPage,

                R.id.popTextViewInsertImage,
                R.id.popTextViewRenameFile,
                R.id.popTextViewPageBackground,
                R.id.popButtonShare,
                R.id.popButtonPan,
                R.id.popButtonShortcut,

                R.id.popTextViewCopy,
                R.id.popTextViewPaste,
                R.id.popTextViewCut,
        };
        for (int id : ids) {
            View popButton = layout.findViewById(id); //R.id.popButtonGrid
            popButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) {
                        mListener.onClick(view);
                    }
                    try {
                        if (popup != null) {
                            popup.dismiss();
                        }
                    } catch (Throwable eee) {
                        eee.printStackTrace();
                    }
                }
            });
        }
        TextView popTextViewPageInfo = (TextView) layout.findViewById(R.id.popTextViewPageInfo);
        if (popTextViewPageInfo != null) {
            //textViewPageInfo.setText("" + (getPageIdx() + 1) + "/" + pageNum);
            popTextViewPageInfo.setText("" + pageIndex + "/" + pageNum);
        }

        //  make a popup window
        popup = new PopupWindow(layout,
                300/*ViewGroup.LayoutParams.WRAP_CONTENT*/, ViewGroup.LayoutParams.WRAP_CONTENT);
        popup.setFocusable(true);

        //  now show the popup
        popup.showAsDropDown(anchor, POPUP_OFFSET, POPUP_OFFSET);
    }
//    public interface WidthChangedListener {
//        void onWidthChanged(float value);
//    }
}
