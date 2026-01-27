package com.github.guanpy.wblib.widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.os.Build;
import android.text.Html;
import android.text.InputType;
import android.text.Spannable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.guanpy.wblib.bean.DrawPoint;
import com.github.guanpy.wblib.bean.DrawTextPoint;
import com.txkj.drawingapp.R;

import io.github.pastthepixels.freepaint.Graphics.DrawPath;

public class DrawTextView extends RelativeLayout implements
        View.OnClickListener {
    /**
     * 显示状态
     */
    public static final int TEXT_VIEW = 1;
    /**
     * 编辑（文字编辑）状态
     */
    public static final int TEXT_EDIT = 2;
    /**
     * 详情（显示删除、编辑按钮）状态
     */
    public static final int TEXT_DETAIL = 3;
    /**
     * 被删除状态
     */
    public static final int TEXT_DELETE = 4;

    /** */
    private View mVOutside;
    /** */
    private RelativeLayout mRlContent;
    /** */
    private RelativeLayout mRlText;
    /** */
    public/*private*/ EditText mEtTextEdit;
    /** */
//    private TextView mTvTextEdit;
    /** */
//    private Button mBtTextDelete;
    /** */
//    private Button mBtTextEdit;

    private Context mContext;

    private CallBackListener mCallBackListener;

    private DrawPoint mDrawPoint;

    private int mWidth;
    /**
     * 特殊字符所需
     */
    private Spannable mSpannable;

    public DrawTextView(Context context) {
        super(context);
        init(context);
    }

    public DrawTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DrawTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        mDrawPoint = new DrawPoint(); //FIXME:
        mDrawPoint.setDrawText(new DrawTextPoint()); //FIXME:

//        mDrawPoint = DrawPoint.copyDrawPoint(drawPoint);
//        mCallBackListener = callBackListener;
        Display display = ((Activity) mContext).getWindowManager()
                .getDefaultDisplay();
        mWidth = display.getWidth();
        initUI();
        initEvent();
        //FIXME:not TEXT_VIEW,TEXT_DETAIL
        switchView(TEXT_EDIT/*mDrawPoint.getDrawText().getStatus()*/);
    }

    public void init2(float x, float y, String text, int useHtml, int textColor, float textSize, CallBackListener callBackListener) {
        this.mCallBackListener = callBackListener;
        mDrawPoint.getDrawText().setX(x);
        mDrawPoint.getDrawText().setY(y);
        mDrawPoint.getDrawText().setStr(text, useHtml == DrawPath.POINTS_TEXT_TYPE_RICH);
        mDrawPoint.getDrawText().setColor(textColor);
        mDrawPoint.getDrawText().setTextSize(textSize);
        //don't call initUI();
        if (null != mDrawPoint) {
            setText(mDrawPoint.getDrawText().getStr(mDrawPoint.getDrawText().getUseHtml()),
                    mDrawPoint.getDrawText().getUseHtml());
            //FIXME:
            //setText("");
        }
        setLayoutParams();
        //FIXME:not TEXT_VIEW,TEXT_DETAIL
        switchView(TEXT_EDIT/*mDrawPoint.getDrawText().getStatus()*/);
    }


    /**
     * 初始化界面控件 <br>
     * Created 2015-8-10 16:55:49
     *
     * @author : gpy
     */
    private void initUI() {
        LayoutInflater.from(mContext).inflate(R.layout.draw_text, this, true);
        mVOutside = (View) findViewById(R.id.v_outside);
        mRlContent = (RelativeLayout) findViewById(R.id.rl_content);
        mRlText = (RelativeLayout) findViewById(R.id.rl_text);
        mEtTextEdit = (EditText) findViewById(R.id.et_text_edit);
//        mEtTextEdit.setShowSoftInputOnFocus(false);
//        mEtTextEdit.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
//        mEtTextEdit.setRawInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        mEtTextEdit.setEnabled(true);
//        mTvTextEdit = (TextView) findViewById(R.id.tv_text_edit);
//        mBtTextDelete = (Button) findViewById(R.id.bt_text_delete);
//        mBtTextEdit = (Button) findViewById(R.id.bt_text_edit);
        if (null != mDrawPoint) {
            setText(mDrawPoint.getDrawText().getStr(mDrawPoint.getDrawText().getUseHtml()),
                    mDrawPoint.getDrawText().getUseHtml());
            //FIXME:
            //setText("");
        }
        setLayoutParams();
    }

    int mDeltaY = 0;
    public void moveUp(int deltaY) {
        this.mDeltaY = deltaY;
        setLayoutParams();
    }

    /**
     * 初始化监听 <br>
     * Created 2015-8-10 16:55:49
     *
     * @author : gpy
     */
    //@SuppressLint("ClickableViewAccessibility")
    private void initEvent() {
        mVOutside.setOnClickListener(this);
        mRlText.setOnClickListener(this);
        mEtTextEdit.setOnClickListener(this);
//        mBtTextDelete.setOnClickListener(this);
//        mBtTextEdit.setOnClickListener(this);
//        mTvTextEdit.setOnClickListener(this);
//        mTvTextEdit.setOnTouchListener(new OnTouchListener() {
//            int lastX, lastY;
//
//            @Override
//            public boolean onTouch(View view, MotionEvent event) {
//                if (true) { //if (mDrawPoint.getDrawText().getStatus() == TEXT_DETAIL&& OperationUtils.getInstance().DISABLE) {
//                    int ea = event.getAction();
//                    switch (ea) {
//                        case MotionEvent.ACTION_DOWN:
//                            // 获取触摸事件触摸位置的原始X坐标
//                            lastX = (int) event.getRawX();
//                            lastY = (int) event.getRawY();
//                            break;
//                        case MotionEvent.ACTION_MOVE:
//                            int dx = (int) event.getRawX() - lastX;
//                            int dy = (int) event.getRawY() - lastY;
//
//                            int left = mRlContent.getLeft() + dx;
//                            int top = mRlContent.getTop() + dy;
//                            int right = mRlContent.getRight() + dx;
//                            int bottom = mRlContent.getBottom() + dy;
//                            if (left < 0) {
//                                left = 0;
//                                right = left + mRlContent.getWidth();
//                            }
//                            if (right > getWidth()) {
//                                right = getWidth();
//                                left = right - mRlContent.getWidth();
//                            }
//                            if (top < 0) {
//                                top = 0;
//                                bottom = top + mRlContent.getHeight();
//                            }
//                            if (bottom > getHeight()) {
//                                bottom = getHeight();
//                                top = bottom - mRlContent.getHeight();
//                            }
////                            mDrawPoint.getDrawText().setX(left); //FIXME:
////                            mDrawPoint.getDrawText().setY(top);
//                            Log.e("移动", "-" + left + "," + top);
//                            mRlContent.layout(left, top, right, bottom);
//                            lastX = (int) event.getRawX();
//                            lastY = (int) event.getRawY();
//                            break;
//                        case MotionEvent.ACTION_UP:
//                            if (null != mCallBackListener) {
//                                mCallBackListener.onUpdate(mDrawPoint);
//                            }
//                            break;
//                    }
//                }
//
//                return false;
//            }
//        });
    }


    private void setText(String strText, boolean isHtml) {
        if (false) {
            if (!TextUtils.isEmpty(strText)) {
                mEtTextEdit.setText(strText);
//                mTvTextEdit.setText(strText);
            }
        } else {
            if (strText == null) strText = "";
            if (isHtml) { //if (DrawPath.POINTS_TEXT_TYPE_RICH)
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    mEtTextEdit.setText(Html.fromHtml(strText, Html.FROM_HTML_MODE_LEGACY));
                } else {
                    mEtTextEdit.setText(Html.fromHtml(strText));
                }
            } else {
                mEtTextEdit.setText(strText);
            }
//            mTvTextEdit.setText(strText);
        }
        if (isHtml) {
            //skip
        } else {
            if (false) {
                mEtTextEdit.setTextColor(0xFFFF0000/*mDrawPoint.getDrawText().getColor()*/);
                //            mTvTextEdit.setTextColor(0xFFFF0000/*mDrawPoint.getDrawText().getColor()*/);
            } else {
                mEtTextEdit.setTextColor(mDrawPoint.getDrawText().getColor());
                //            mTvTextEdit.setTextColor(mDrawPoint.getDrawText().getColor());
                mEtTextEdit.setTextSize(mDrawPoint.getDrawText().getTextSize());
                //            mTvTextEdit.setTextSize(mDrawPoint.getDrawText().getTextSize());
            }
            //        if (mDrawPoint.getDrawText().getIsUnderline()) {
            //            mTvTextEdit.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
            //            mEtTextEdit.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
            //        }
            //        if (mDrawPoint.getDrawText().getIsBold()) {
            //            mTvTextEdit.getPaint().setFakeBoldText(true);
            //            mEtTextEdit.getPaint().setFakeBoldText(true);
            //        }
        }
    }

    private void setLayoutParams() {
/*
        <RelativeLayout
            android:id="@+id/rl_text"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_marginLeft="15dip"
            android:layout_marginRight="15dip"
            android:background="@drawable/draw_text_border"
            android:gravity="center_vertical"
            android:paddingTop="25dip"
            android:paddingBottom="25dip"
            android:paddingLeft="30dip"
            android:paddingRight="30dip" >
 */
        LayoutParams layParamsTxt = new LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layParamsTxt.leftMargin = (int) (mDrawPoint.getDrawText().getX() ); //- dp2px(getContext(), 30) //FIXME:
        layParamsTxt.topMargin = (int) (mDrawPoint.getDrawText().getY() ) - this.mDeltaY; // - dp2px(getContext(), 25)
        mRlContent.setLayoutParams(layParamsTxt);
    }
    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f); // 0.5f用于四舍五入
    }

    public void switchView(int currentStatus) {
        switch (currentStatus) {
//            case TEXT_VIEW:
//                mVOutside.setVisibility(View.GONE);
//                mEtTextEdit.setVisibility(View.GONE);
//                mTvTextEdit.setVisibility(View.VISIBLE);
//                mRlText.setBackgroundResource(R.color.transparent);
//                mBtTextEdit.setVisibility(View.GONE);
//                mBtTextDelete.setVisibility(View.GONE);
//                break;
            case TEXT_EDIT:
                //mVOutside.setBackgroundResource(R.color.white);
                mVOutside.setBackgroundResource(R.color.transparent); //FIXME:
                mVOutside.setVisibility(View.VISIBLE);
                mEtTextEdit.setVisibility(View.VISIBLE);
//                mTvTextEdit.setVisibility(View.GONE);
                mRlText.setBackgroundResource(R.drawable.draw_text_border);
//                mBtTextEdit.setVisibility(View.GONE);
                mEtTextEdit.setSelection(mEtTextEdit.getText().length());
//                mBtTextDelete.setVisibility(View.GONE);
                //EventBus.getDefault().post(Events.WHITE_BOARD_TEXT_EDIT);
                //FIXME:
                showSoftKeyBoard(mEtTextEdit);
                break;
//            case TEXT_DETAIL:
//                mVOutside.setBackgroundResource(R.color.transparent);
//                mVOutside.setVisibility(View.VISIBLE);
//                mEtTextEdit.setVisibility(View.GONE);
//                mTvTextEdit.setVisibility(View.VISIBLE);
//                mRlText.setBackgroundResource(R.drawable.draw_text_border);
//                mBtTextEdit.setVisibility(View.VISIBLE);
//                mBtTextDelete.setVisibility(View.VISIBLE);
//                break;
//            case TEXT_DELETE:
//
//                break;
//            default:
//                break;
        }
        Log.d("gpy","文字宽："+mRlText.getHeight());
//        if (mDrawPoint.getDrawText().getStatus() != currentStatus) {
//            mDrawPoint.getDrawText().setStatus(currentStatus);
//            if (null != mCallBackListener && currentStatus != TEXT_EDIT) {
//                mCallBackListener.onUpdate(mDrawPoint);
//            }
//        }

    }

    /**
     * 文字编辑完成
     *
     * @param isSave 是否保存
     */
    public void afterEdit(boolean isSave) {
        Log.d("gpy", "要保存的文字：" + mEtTextEdit.getText().toString());
        if (isSave) {
//            mDrawPoint.getDrawText().setStr(mEtTextEdit.getText().toString());
            //FIXME: save
        }
        switchView(TEXT_VIEW);
        hideSoftInput();
    }

    @Override
    public void onClick(View v) {
        int vId = v.getId();
        if (vId == R.id.v_outside) {
            if (true) { //if (mDrawPoint.getDrawText().getStatus() == TEXT_DETAIL && OperationUtils.getInstance().DISABLE) {
                switchView(TEXT_VIEW);
            }
            hideSoftInput();
            if (null != mCallBackListener) {
                if (mDrawPoint != null && mDrawPoint.getDrawText() != null) {
                    if (mEtTextEdit != null) {
                        //https://stackoverflow.com/questions/5504433/how-to-remove-the-underline-from-the-edittext-field-in-android
                        //remove edittext underline
                        mEtTextEdit.clearComposingText();
                        String htmlString = null;//Html.TO_HTML_PARAGRAPH_LINES_INDIVIDUAL);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            htmlString = Html.toHtml(mEtTextEdit.getText(), Html.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE);
                        } else {
                            htmlString = Html.toHtml(mEtTextEdit.getText());
                        }
                        mDrawPoint.getDrawText().setStr(mEtTextEdit.getText().toString(), htmlString);
                    }
                }
                mCallBackListener.onSave(mDrawPoint);
            }
//        } else if (vId == R.id.tv_text_edit) {
//            if (true) { //if (OperationUtils.getInstance().DISABLE) {
//                switchView(TEXT_DETAIL);
//            }
//        } else if (vId == R.id.bt_text_delete) {
//            if (true) { //if (OperationUtils.getInstance().DISABLE) {
//                switchView(TEXT_DELETE);
//            }
//        } else if (vId == R.id.bt_text_edit) {
//            if (true) { //if (OperationUtils.getInstance().DISABLE) {
//                switchView(TEXT_EDIT);
//            }
        }
    }

    public interface CallBackListener {
        /**
         * 更新文字属性
         */
        void onUpdate(DrawPoint drawPoint);

        void onSave(DrawPoint drawPoint);
    }

    private void showSoftKeyBoard(final EditText et) {
        if (true) {
            et.requestFocus();
            et.post(new Runnable() {
                @Override
                public void run() {
                    mEtTextEdit.setEnabled(true);
                    mEtTextEdit.requestFocus();
                    // 弹出输入法
                    InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (true) {
                        imm.showSoftInput(et, 0);//InputMethodManager.RESULT_UNCHANGED_SHOWN);
                    } else {
                        imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);
                    }
                }
            });
        }
    }

    public void hideSoftInput() {
        if (this == null || mContext == null || mEtTextEdit == null) {
            return;
        }
        mEtTextEdit.setEnabled(false);
        // 隐藏输入法
        ((InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(mEtTextEdit.getWindowToken(), 0);
    }
}
