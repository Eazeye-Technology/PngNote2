package com.txkj.drawingapp.activity;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.txkj.drawingapp.R;
import com.txkj.notemobile2.BookListActivity;

public class CopyCutMenuDialog {
    private static final int POPUP_OFFSET_X = 0;//30;
    private static final int POPUP_OFFSET_Y = 0;//30;

    public static boolean isOpen = false;
    private static View.OnClickListener mListener = null;
    private static PopupWindow popup;
    public static void show(Context context, View anchor, int pageIndex, int pageNum, final View.OnClickListener listener, boolean isEnablePaste) {
        //View layout = View.inflate(context, R.layout.activity_main_menu1, null);
        View layout = View.inflate(context, R.layout.activity_book4_popup_menu, null);
        //View wv = layout.findViewById(R.id.wheel);

        if (BookListActivity.USE_DS) {
            layout.findViewById(R.id.popTextViewCopy).setVisibility(View.GONE);
            layout.findViewById(R.id.popTextViewPaste).setVisibility(View.GONE);
            layout.findViewById(R.id.popTextViewCut).setVisibility(View.GONE);
            layout.findViewById(R.id.popTextViewInsertImage).setVisibility(View.GONE);
            layout.findViewById(R.id.popTextViewTranscriptLanguage).setVisibility(View.GONE);
            layout.findViewById(R.id.popButtonShare).setVisibility(View.GONE);
            layout.findViewById(R.id.popButtonShortcut).setVisibility(View.GONE);

            layout.findViewById(R.id.popLine1).setVisibility(View.GONE);
            layout.findViewById(R.id.popLine2).setVisibility(View.GONE);
            layout.findViewById(R.id.popLine3).setVisibility(View.GONE);
            layout.findViewById(R.id.popLine4).setVisibility(View.GONE);
            //layout.findViewById(R.id.popLine5).setVisibility(View.GONE);
            layout.findViewById(R.id.popLine6).setVisibility(View.GONE);
            layout.findViewById(R.id.popLine7).setVisibility(View.GONE);
        }

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
                R.id.popTextViewTranscriptLanguage,
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

        //20260410: paste gray color
        ImageView ivMenuPaste = (ImageView) layout.findViewById(R.id.ivMenuPaste);
        TextView tvMenuPaste = (TextView) layout.findViewById(R.id.tvMenuPaste);
        //this.copyPaths.isEmpty()
        if (isEnablePaste) {
            ivMenuPaste.clearColorFilter();
            tvMenuPaste.setTextColor(Color.BLACK);
        } else {
            ivMenuPaste.setColorFilter(Color.LTGRAY, PorterDuff.Mode.SRC_IN);
            tvMenuPaste.setTextColor(Color.LTGRAY);
        }

        int menuWidth = context.getResources().getDimensionPixelSize(R.dimen.activity_book4_popup_menu_width);
        int offsetX = context.getResources().getDimensionPixelSize(R.dimen.activity_book4_popup_menu_offsetx);
        int offsetY = context.getResources().getDimensionPixelSize(R.dimen.activity_book4_popup_menu_offsety);
        //  make a popup window
        popup = new PopupWindow(layout,
                menuWidth/*300 ViewGroup.LayoutParams.WRAP_CONTENT*/, ViewGroup.LayoutParams.WRAP_CONTENT);
        popup.setFocusable(true);
        popup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                isOpen = false;
            }
        });

                //  now show the popup
        popup.showAsDropDown(anchor, offsetX, offsetY, Gravity.RIGHT);
    }
//    public interface WidthChangedListener {
//        void onWidthChanged(float value);
//    }
}
