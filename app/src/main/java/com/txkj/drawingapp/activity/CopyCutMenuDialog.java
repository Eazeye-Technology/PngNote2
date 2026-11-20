package com.txkj.drawingapp.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.text.Html;
import android.text.Spanned;
import android.text.SpannedString;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dseink.DualScreenConstant;
import com.dseink.EinkUtils;
import com.foobnix.android.utils.TxtUtils;
import com.txkj.drawingapp.R;
import com.txkj.notemobile2.BookListActivity;

public class CopyCutMenuDialog {
    private static final int POPUP_OFFSET_X = 0;//30;
    private static final int POPUP_OFFSET_Y = 0;//30;

    public static boolean isOpen = false;
    private static View.OnClickListener mListener = null;
    private static PopupWindow popup;
    public static void show(Context context, View anchor, int pageIndex, int pageNum, final View.OnClickListener listener, boolean isEnablePaste, View rootView) {
        //View layout = View.inflate(context, R.layout.activity_main_menu1, null);
        View layout = View.inflate(context, R.layout.activity_book4_popup_menu, null);
        //View wv = layout.findViewById(R.id.wheel);

        if (true) {
            layout.findViewById(R.id.popLine8).setVisibility(View.VISIBLE);
            layout.findViewById(R.id.llVersion).setVisibility(View.VISIBLE);
            layout.findViewById(R.id.llVersion).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (context instanceof BookListActivity) {
                        ((BookListActivity) context).checkVersion(true);
                        ((BookListActivity) context).startCheckVersionAnimation(layout.findViewById(R.id.ivVersion));
                    }
                }
            });
            ((TextView) layout.findViewById(R.id.tvVersion)).setText("v" + getLocalVersion(context));

            final TextView tvDisplayTool = layout.findViewById(R.id.tvDisplayTool);
            tvDisplayTool.setText(EinkUtils.getRightScreen((Activity) context) ? "Right Screen" : "Left Screen");
            underlineTextView(tvDisplayTool);

            tvDisplayTool.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu menu = new PopupMenu(v.getContext(), v);
                    String[] screens = {
                            "Left Screen",
                            "Right Screen"
                    };
                    for (int i = 0; i < screens.length; i++) {
                        final int j = i;
                        menu.getMenu().add(screens[i]).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                if (popup != null && popup.isShowing()) {
                                    popup.dismiss();
                                }

                                boolean isRightScreen_ = screens[j] != null &&
                                        screens[j].equals("Right Screen");
                                EinkUtils.setRightScreen((Activity) context, isRightScreen_);
                                tvDisplayTool.setText(isRightScreen_ ?
                                        "Right Screen" :
                                        "Left Screen");
                                underlineTextView(tvDisplayTool);

                                //FIXME:
                                updateRightScreen(context, rootView);

                                return false;
                            }
                        });
                    }
                    menu.show();
                }
            });
        }

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
                R.id.popTextViewFocusMode,
                R.id.popTextViewToggleDualScreen,

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

    public static String getLocalVersion(Context mAct) {
        PackageInfo packageInfo = null;
        try {
            packageInfo = mAct.getApplicationContext().getPackageManager()
                    .getPackageInfo(mAct.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageInfo != null) {
            return packageInfo.versionName;
        } else {
            return "";
        }
    }

    //public static boolean isRightScreen = true;
    public static TextView underlineTextView(View view) {
        if (view == null) {
            return null;
        }
        TextView textView = (TextView) view;
        String text = textView.getText().toString();
        textView.setText(underline(text));
        if (true) {
            textView.setTextColor(0xFF000000);
        }
        return textView;
    }

    @TargetApi(24)
    public static Spanned underline(final String text) {
        try {
            return Html.fromHtml("<u>" + text + "</u>");
        } catch (Exception e) {
            try {
                return Html.fromHtml("<u>" + text + "</u>", Html.FROM_HTML_MODE_LEGACY);
            } catch (Exception e1) {
                return new SpannedString(text);
            }
        }
    }

    public static void updateRightScreen(Context context, View rootView) {
        if (context instanceof BookListActivity) {
            BookListActivity act = (BookListActivity) context;
            boolean isRightScreen = EinkUtils.getRightScreen(act);

            LinearLayout left_toolkit1 = rootView.findViewById(R.id.left_toolkit1);
            LinearLayout left_toolkit4 = rootView.findViewById(R.id.left_toolkit4);
            if (left_toolkit1 != null && left_toolkit4 != null) {
                {
                    RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams) left_toolkit1.getLayoutParams();
                    if (params1 != null) {
                        if (isRightScreen) {
                            params1.removeRule(RelativeLayout.ALIGN_PARENT_LEFT);
                            params1.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                            params1.leftMargin = 0;
                            params1.rightMargin = 10;
                        } else {
                            params1.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                            params1.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                            params1.leftMargin = 10;
                            params1.rightMargin = 0;
                        }
                    }
                    left_toolkit1.setLayoutParams(params1);
                }
                {
                    RelativeLayout.LayoutParams params4 = (RelativeLayout.LayoutParams) left_toolkit4.getLayoutParams();
                    if (params4 != null) {
                        if (isRightScreen) {
                            params4.removeRule(RelativeLayout.ALIGN_PARENT_LEFT);
                            params4.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                            params4.leftMargin = 0;
                            params4.rightMargin = 10;
                        } else {
                            params4.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                            params4.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                            params4.leftMargin = 10;
                            params4.rightMargin = 0;
                        }
                    }
                    left_toolkit4.setLayoutParams(params4);
                }
            }

            if (BookActivity4Fragment.ENABLE_SWAP_SCREEN && EinkUtils.getCurrentScreenPos(act, 0) ==
                    DualScreenConstant.EXTRA_LAUNCH_SCREEN_PANEL_BOTH) {
                LinearLayout container = rootView.findViewById(R.id.container);
                container.post(() -> {
                    View leftArea = container.findViewById(R.id.leftArea);
                    View rightArea = container.findViewById(R.id.rightArea);
                    container.removeAllViews();
                    if (isRightScreen) {
                        container.addView(leftArea);
                        container.addView(rightArea);
                    } else {
                        container.addView(rightArea);
                        container.addView(leftArea);
                    }
                });
            }
        }
    }
}
