package com.dseink;

import android.view.EinkPWInterface;
import android.view.View;

public class EinkUtils {
    public static void forceEinkFullUpdateWithView(View view) {
        if (view == null) {
            return;
        }
        try {
            ReflectUtils.reflect(view).method("forceEinkFullUpdate");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static EinkPWInterface getEinkPWInterfaceWithView(View view) {
        try {
            Object obj = ReflectUtils.reflect(view).method("getPWInterFace").get();
            if (obj instanceof EinkPWInterface) {
                return (EinkPWInterface) obj;
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
