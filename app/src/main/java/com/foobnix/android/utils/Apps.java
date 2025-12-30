package com.foobnix.android.utils;

import android.app.Activity;
import android.os.Build;

public class Apps {
    public static boolean isDestroyedActivity(Activity a) {
        if (a == null || a.isFinishing()) {
            return true;
        }
        if (Build.VERSION.SDK_INT >= 17 && a.isDestroyed()) {
            return true;
        }
        return false;
    }

}
