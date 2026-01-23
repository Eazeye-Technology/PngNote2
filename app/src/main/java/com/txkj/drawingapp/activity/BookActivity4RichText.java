package com.txkj.drawingapp.activity;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.EditText;

import com.txkj.drawingapp.R;

public class BookActivity4RichText {
    //https://blog.csdn.net/weixin_52173250/article/details/152817299
    //https://blog.csdn.net/weixin_45147894/article/details/107145014
    //https://developer.android.com/develop/ui/views/text-and-emoji/spans?hl=zh-cn#kotlin
    /*
h1=32px
h2=24px
h3=18.72px
h4=16px
p=16px
h5=13.28px
h6=12px

h1: 大约是默认字体大小的两倍 (2em)。32px
h2: 大约是默认字体大小的 1.5 倍 (1.5em)。28px
h3: 大约是默认字体大小的 1.17 倍 (1.17em)。24px
h4: 与默认字体大小相同 (1em)。
h5: 略小于默认字体大小 (0.83em)。
h6: 更小 (0.67em)。
     */
    public static void initButtons(BookActivity4Fragment fragment) {
        //https://www.cnblogs.com/conglingkaishi/p/9502241.html
        //https://github.com/gzu-liyujiang/SpanTextBuilder
        //https://github.com/nalancer08/Android-Utils
        View.OnClickListener l_bold = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editText = fragment.getDtView(true).mEtTextEdit;
                Editable text = editText.getText();
                int start = editText.getSelectionStart();
                int end = editText.getSelectionEnd();
                StyleSpan[] old = text.getSpans(start, end, StyleSpan.class);
                if (old != null && old.length > 0) {
                    for (StyleSpan del : old) {
                        if (del.getStyle() == Typeface.BOLD) {
                            text.removeSpan(del);
                        }
                    }
                    //editText.setText(text);
                } else {
                    text.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    //editText.setText(text);
                }
                //Log.d("RichText", "onSetSelectedBold: " + Html.toHtml(text));
            }
        };
        fragment.g_rootView.findViewById(R.id.action_bold).setOnClickListener(l_bold);
        fragment.g_rootView.findViewById(R.id.action_bold2).setOnClickListener(l_bold);
        View.OnClickListener l_italic = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editText = fragment.getDtView(true).mEtTextEdit;
                Editable text = editText.getText();
                int start = editText.getSelectionStart();
                int end = editText.getSelectionEnd();
                StyleSpan[] old = text.getSpans(start, end, StyleSpan.class);
                if (old != null && old.length > 0) {
                    for (StyleSpan del : old) {
                        if (del.getStyle() == Typeface.ITALIC) {
                            text.removeSpan(del);
                        }
                    }
                    //editText.setText(text);
                } else {
                    text.setSpan(new StyleSpan(Typeface.ITALIC), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    //editText.setText(text);
                }
                //Log.d("RichText", "onSetSelectedBold: " + Html.toHtml(text));
            }
        };
        fragment.g_rootView.findViewById(R.id.action_italic).setOnClickListener(l_italic);
        fragment.g_rootView.findViewById(R.id.action_italic2).setOnClickListener(l_italic);
        View.OnClickListener l_underline = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editText = fragment.getDtView(true).mEtTextEdit;
                Editable text = editText.getText();
                int start = editText.getSelectionStart();
                int end = editText.getSelectionEnd();

                UnderlineSpan[] old = text.getSpans(start, end, UnderlineSpan.class);
                if (old != null && old.length > 0) {
                    for (UnderlineSpan del : old) {
                        text.removeSpan(del);
                    }
                    //editText.setText(text);
                } else {
                    UnderlineSpan underlineSpan = new UnderlineSpan();
                    text.setSpan(underlineSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    //editText.setText(text);
                }
                //Log.d("RichText", "onSetSelectedBold: " + Html.toHtml(text));
            }
        };
        fragment.g_rootView.findViewById(R.id.action_underline).setOnClickListener(l_underline);
        fragment.g_rootView.findViewById(R.id.action_underline2).setOnClickListener(l_underline);
        View.OnClickListener l_black = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editText = fragment.getDtView(true).mEtTextEdit;
                Editable text = editText.getText();
                int start = editText.getSelectionStart();
                int end = editText.getSelectionEnd();

                ForegroundColorSpan[] old = text.getSpans(start, end, ForegroundColorSpan.class);
                if (old != null && old.length > 0) {
                    for (ForegroundColorSpan del : old) {
                        text.removeSpan(del);
                    }
                    //editText.setText(text);
                } else {
                    ForegroundColorSpan underlineSpan = new ForegroundColorSpan(Color.BLACK);
                    text.setSpan(underlineSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    //editText.setText(text);
                }
                //Log.d("RichText", "onSetSelectedBold: " + Html.toHtml(text));
            }
        };
        fragment.g_rootView.findViewById(R.id.action_txt_color_black).setOnClickListener(l_black);
        fragment.g_rootView.findViewById(R.id.action_txt_color_black2).setOnClickListener(l_black);
        View.OnClickListener l_grey = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editText = fragment.getDtView(true).mEtTextEdit;
                Editable text = editText.getText();
                int start = editText.getSelectionStart();
                int end = editText.getSelectionEnd();

                ForegroundColorSpan[] old = text.getSpans(start, end, ForegroundColorSpan.class);
                if (old != null && old.length > 0) {
                    for (ForegroundColorSpan del : old) {
                        text.removeSpan(del);
                    }
                    //editText.setText(text);
                } else {
                    ForegroundColorSpan underlineSpan = new ForegroundColorSpan(0xffe1e1e1);
                    text.setSpan(underlineSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    //editText.setText(text);
                }
                //Log.d("RichText", "onSetSelectedBold: " + Html.toHtml(text));
            }
        };
        fragment.g_rootView.findViewById(R.id.action_txt_color_grey).setOnClickListener(l_grey);
        fragment.g_rootView.findViewById(R.id.action_txt_color_grey2).setOnClickListener(l_grey);
        View.OnClickListener l_white = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editText = fragment.getDtView(true).mEtTextEdit;
                Editable text = editText.getText();
                int start = editText.getSelectionStart();
                int end = editText.getSelectionEnd();

                ForegroundColorSpan[] old = text.getSpans(start, end, ForegroundColorSpan.class);
                if (old != null && old.length > 0) {
                    for (ForegroundColorSpan del : old) {
                        text.removeSpan(del);
                    }
                    //editText.setText(text);
                } else {
                    ForegroundColorSpan underlineSpan = new ForegroundColorSpan(Color.WHITE);
                    text.setSpan(underlineSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    //editText.setText(text);
                }
                //Log.d("RichText", "onSetSelectedBold: " + Html.toHtml(text));
            }
        };
        fragment.g_rootView.findViewById(R.id.action_txt_color_white).setOnClickListener(l_white);
        fragment.g_rootView.findViewById(R.id.action_txt_color_white2).setOnClickListener(l_white);
        View.OnClickListener l_h1 = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editText = fragment.getDtView(true).mEtTextEdit;
                Editable text = editText.getText();
                int start = editText.getSelectionStart();
                int end = editText.getSelectionEnd();

                //RelativeSizeSpan
                AbsoluteSizeSpan[] old = text.getSpans(start, end, AbsoluteSizeSpan.class);
                if (old != null && old.length > 0) {
                    for (AbsoluteSizeSpan del : old) {
                        text.removeSpan(del);
                    }
                    //editText.setText(text);
                } else {
                    AbsoluteSizeSpan underlineSpan = new AbsoluteSizeSpan(32);
                    text.setSpan(underlineSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    //editText.setText(text);
                }
                //Log.d("RichText", "onSetSelectedBold: " + Html.toHtml(text));
            }
        };
        fragment.g_rootView.findViewById(R.id.action_heading1_h1).setOnClickListener(l_h1);
        fragment.g_rootView.findViewById(R.id.action_heading1_h12).setOnClickListener(l_h1);
        View.OnClickListener l_h2 = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editText = fragment.getDtView(true).mEtTextEdit;
                Editable text = editText.getText();
                int start = editText.getSelectionStart();
                int end = editText.getSelectionEnd();

                AbsoluteSizeSpan[] old = text.getSpans(start, end, AbsoluteSizeSpan.class);
                if (old != null && old.length > 0) {
                    for (AbsoluteSizeSpan del : old) {
                        text.removeSpan(del);
                    }
                    //editText.setText(text);
                } else {
                    AbsoluteSizeSpan underlineSpan = new AbsoluteSizeSpan(28);
                    text.setSpan(underlineSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    //editText.setText(text);
                }
                //Log.d("RichText", "onSetSelectedBold: " + Html.toHtml(text));
            }
        };
        fragment.g_rootView.findViewById(R.id.action_heading2_h2).setOnClickListener(l_h2);
        fragment.g_rootView.findViewById(R.id.action_heading2_h22).setOnClickListener(l_h2);
        View.OnClickListener l_h3 = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editText = fragment.getDtView(true).mEtTextEdit;
                Editable text = editText.getText();
                int start = editText.getSelectionStart();
                int end = editText.getSelectionEnd();

                AbsoluteSizeSpan[] old = text.getSpans(start, end, AbsoluteSizeSpan.class);
                if (old != null && old.length > 0) {
                    for (AbsoluteSizeSpan del : old) {
                        text.removeSpan(del);
                    }
                    //editText.setText(text);
                } else {
                    AbsoluteSizeSpan underlineSpan = new AbsoluteSizeSpan(24);
                    text.setSpan(underlineSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    //editText.setText(text);
                }
                //Log.d("RichText", "onSetSelectedBold: " + Html.toHtml(text));
            }
        };
        fragment.g_rootView.findViewById(R.id.action_heading3_h3).setOnClickListener(l_h3);
        fragment.g_rootView.findViewById(R.id.action_heading3_h32).setOnClickListener(l_h3);
    }
}
