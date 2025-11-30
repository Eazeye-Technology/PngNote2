package com.txkj.notemobile2;

import com.txkj.drawingapp.activity.BookActivity2;
import com.txkj.drawingapp.activity.BookActivity3;
import com.txkj.drawingapp.activity.BookActivity4;

public class Config {
    public final static boolean USE_ACTIONBAR = false;
    public final static int USE_NEW_UI = 3;

    public static Class<?> getCls() {
        Class<?> cls = null;
        if (Config.USE_NEW_UI == 3) {
            cls = BookActivity4.class;
        } else if (Config.USE_NEW_UI == 2) {
            cls = BookActivity3.class;
        } else if (Config.USE_NEW_UI == 1) {
            cls = BookActivity2.class;
        } else {
            cls = BookActivity.class;
        }
        return cls;
    }
}
