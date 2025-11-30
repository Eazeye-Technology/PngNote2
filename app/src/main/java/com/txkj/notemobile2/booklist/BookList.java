package com.txkj.notemobile2.booklist;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.txkj.notemobile2.book.FastFile;

public class BookList {
    private FastFile dir;
    private ContentResolver resolver;
    private static final String LAST_ROOT_DIR_KEY = "last_root_url";

    public BookList(FastFile dir, ContentResolver resolver) {
        this.dir = dir;
        this.resolver = resolver;
    }

    public FastFile getDir() {
        return this.dir;
    }
    public ContentResolver getResolver() {
        return this.resolver;
    }

    public static String lastUriStr(Context ctx) {
        return sharedPreferences(ctx).getString(LAST_ROOT_DIR_KEY, null);
    }

    public static boolean writeLastUriStr(Context ctx, String path) {
        return sharedPreferences(ctx).edit().putString(LAST_ROOT_DIR_KEY, path).commit();
    }

    public static boolean resetLastUriStr(Context ctx) {
        return sharedPreferences(ctx).edit().putString(LAST_ROOT_DIR_KEY, null).commit();
    }

    private static SharedPreferences sharedPreferences(Context ctx) {
        return ctx.getSharedPreferences("KAKIOKU", 0); //FIXME:
    }

    public static void showMessage(Context ctx, String msg) {
        Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
    }
}
