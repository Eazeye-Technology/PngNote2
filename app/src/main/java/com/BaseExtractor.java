package com;

import android.graphics.Bitmap;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public abstract class BaseExtractor {
    public final static String BASE64_PREFIX = "data:image/png;base64,";
    public static InputStream decodeImageByBase64(String path, int width) {
        try {
            path = path.replace(BASE64_PREFIX, "");
            byte[] decode = Base64.decode(path, Base64.DEFAULT);
            return new ByteArrayInputStream(decode);
        } catch (Exception e) {
            return null;
        }
    }


    public static byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 95, stream);
        byte[] byteArray = stream.toByteArray();
        bitmap.recycle();
        bitmap = null;
        try {
            stream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return byteArray;
    }

    public static final int BUFFER_SIZE = 16 * 1024;

    public static byte[] getEntryAsByte(InputStream zipInputStream) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        byte[] bytesIn = new byte[BUFFER_SIZE];
        int read = 0;
        while ((read = zipInputStream.read(bytesIn)) != -1) {
            out.write(bytesIn, 0, read);
        }
        out.close();
        return out.toByteArray();
    }
}
