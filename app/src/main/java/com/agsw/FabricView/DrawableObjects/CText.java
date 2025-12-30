package com.agsw.FabricView.DrawableObjects;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Created by antwan on 10/3/2015.
 * This class represents a piece of text written on the canvas.
 */
public class CText extends CDrawable {
    private static final int MARGIN = 0;//20;
    private String mText;

    /**
     * Constructor.
     * Make sure that the paint has a set text size by calling paint.setTextSize().
     * @param s The string to write.
     * @param x The horizontal position to put the text.
     * @param y The vertical position to put the text.
     * @param p The paint to use for the writing.
     */
    public CText(String s, int x, int y, Paint p) {
        setPaint(p); //Must be before setText()
        setText(s);
        setYcoords(y);
        setXcoords(x);
        calculateTextSizes();
    }

    private final static boolean NEW_CALC = true;
    private final static boolean DEBUG_RECT = false;
    private final static boolean MULTILINE = true;
    private void calculateTextSizes() {
        Paint p = getPaint();
        Paint.FontMetrics metric = p.getFontMetrics();
        int textHeight = (int) Math.ceil(metric.descent - metric.ascent);
        int y = (int)(textHeight - metric.descent);

        if (!NEW_CALC) {
            Rect bounds = new Rect();
            getPaint().getTextBounds(getText(), 0, getText().length(), bounds);
            // setYcoords(getYcoords() + y);
            setHeight(bounds.height() + (MARGIN * 2));
            setWidth(bounds.width() + (MARGIN * 2));
        } else {
            if (!MULTILINE) {
                setWidth((int)getFontlength(p, getText()));
                setHeight((int) getFontHeight(p));
            } else {
                String[] lines = getText().split(SPLIT_REGXP);
                float tX2 = 0;
                float tY2 = 0;
                for (String line : lines) {
                    tX2 = Math.max((int)getFontlength(p, line), tX2);
                    tY2 += getFontHeight(p);
                }
                setWidth((int)tX2);
                setHeight((int)tY2);
            }
        }
    }

    /**
     * Setter for the text to write.
     * @param t The new text to write.
     */
    public void setText(String t) {
        mText = t;
        calculateTextSizes();
    }

    /**
     * @return The text to write.
     */
    public String getText() {
        return mText;
    }

    @Override
    public void setPaint(Paint p) {
        super.setPaint(p);
        if(getText() != null) {
            calculateTextSizes();
        }
    }

    private final static String SPLIT_REGXP = "\r\n|\n|\r";
    @Override
    public void draw(Canvas canvas) {
        Paint p = getPaint();
        Paint.FontMetrics metric = p.getFontMetrics();
        int textHeight = (int) Math.ceil(metric.descent - metric.ascent);
        int y = (int)(textHeight - metric.descent);

        Matrix matrix = new Matrix();
        for (CTransform t:
                getTransforms()) {
            t.applyTransform(matrix);
        }

        Bitmap canvasBitmap = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas temp = new Canvas(canvasBitmap);
        temp.save();
        temp.concat(matrix);
        if (!NEW_CALC) {
            temp.drawText(getText(), (float) (getXcoords() + MARGIN), (float) (getYcoords() + y + MARGIN), p);
        } else {
            float tW = getFontlength(p, getText());
            float tH = getFontHeight(p);// + getFontLeading(p);
            float tX = getXcoords(); //(getXcoords() - getFontlength(p, getText())) / 2;
            //float tY = getYcoords() - getFontHeight(p) + getFontLeading(p) * 2 + getFontHeight(p) - getFontLeading(p) * 2; //(getYcoords() - getFontHeight(p)) / 2 + getFontLeading(p);
            float tY = getYcoords() + getFontLeading(p); //(getYcoords() - getFontHeight(p)) / 2 + getFontLeading(p);
            if (DEBUG_RECT) {
                float tX_ = getXcoords();
                //float tY_ = getYcoords() - getFontHeight(p) + getFontLeading(p) + getFontHeight(p) - getFontLeading(p) * 2;
                float tY_ = getYcoords();// - getFontLeading(p) + getFontLeading(p);
                p.setColor(Color.BLUE);
                temp.drawRect(new Rect((int) tX_, (int) tY_, (int) (tX_ + tW), (int) (tY_ + tH)), p);
                p.setColor(Color.RED);
            }
            if (!MULTILINE) {
                temp.drawText(getText(), tX, tY, p);
            } else {
                String[] lines = getText().split(SPLIT_REGXP);
                float tY2 = tY;
                for (String line : lines) {
                    temp.drawText(line, tX, tY2, p);
                    tY2 += getFontHeight(p);
                }
            }
        }
        temp.restore();

        canvas.drawBitmap(canvasBitmap, 0, 0, getPaint());
    }

    //package com.immomo.momo.android.util;
    //public class PhotoUtils {
    //andli0626/Android_App_MoMo
    public static float getFontlength(Paint paint, String str) {
        return paint.measureText(str);
    }

    //package com.immomo.momo.android.util;
    //public class PhotoUtils {
    //andli0626/Android_App_MoMo
    public static float getFontHeight(Paint paint) {
        Paint.FontMetrics fm = paint.getFontMetrics();
        return fm.descent - fm.ascent;
    }

    //package com.immomo.momo.android.util;
    //public class PhotoUtils {
    //andli0626/Android_App_MoMo
    public static float getFontLeading(Paint paint) {
        Paint.FontMetrics fm = paint.getFontMetrics();
        return fm.leading - fm.ascent;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) {
            return true;
        }
        if(!super.equals(obj)) {
            return false;
        }

        if (!(obj instanceof CPath)) {
            return false;
        }
        CText other = (CText) obj;
        if(other.mText == null && this.mText == null) {
            return true;
        }
        return other.mText.equals(this.mText);
    }

}
