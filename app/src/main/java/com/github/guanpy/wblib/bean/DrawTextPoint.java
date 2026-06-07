package com.github.guanpy.wblib.bean;

import com.txkj.drawingapp.activity.BookActivity4Utils;

public class DrawTextPoint {
    private float mTextSize;
    public float getTextSize() {
        return this.mTextSize;
    }
    public void setTextSize(float textSize) {
        this.mTextSize = textSize;
    }

    private long mId;
    private float mX;
    private float mY;
    private boolean mUseHtml = false;
    private String mStr;
    private boolean mIsUnderline;
    private boolean mIsItalics;
    private boolean mIsBold;

    private int mColor;

    private int mStatus;

    private boolean mIsVisible;

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        this.mId = id;
    }

    public float getX() {
        return mX;
    }

    public void setX(float x) {
        this.mX = x;
    }

    public float getY() {
        return mY;
    }

    public void setY(float y) {
        this.mY = y;
    }

    public String getStr(boolean isHtml) {
        return mStr;
    }
    public boolean getUseHtml() {
        return mUseHtml;
    }

    public void setStr(String str, String htmlString) {
        if (BookActivity4Utils.USE_HTML_EDIT) {
            //mark
        }
        if (mUseHtml) {
            this.mStr = htmlString;
        } else {
            this.mStr = str;
        }
    }

    public void setStr(String str, boolean isHtml) {
        this.mStr = str;
        this.mUseHtml = isHtml;
    }

    public boolean getIsUnderline() {
        return mIsUnderline;
    }

    public void setIsUnderline(boolean isUnderline) {
        this.mIsUnderline = isUnderline;
    }

    public boolean getIsItalics() {
        return mIsItalics;
    }

    public void setIsItalics(boolean isItalics) {
        this.mIsItalics = isItalics;
    }

    public boolean getIsBold() {
        return mIsBold;
    }

    public void setIsBold(boolean isBold) {
        this.mIsBold = isBold;
    }

    public int getColor() {
        return mColor;
    }

    public void setColor(int color) {
        this.mColor = color;
    }

    public int getStatus() {
        return mStatus;
    }

    public void setStatus(int status) {
        this.mStatus = status;
    }

    public boolean getIsVisible() {
        return mIsVisible;
    }

    public void setIsVisible(boolean isVisible) {
        this.mIsVisible = isVisible;
    }
}
