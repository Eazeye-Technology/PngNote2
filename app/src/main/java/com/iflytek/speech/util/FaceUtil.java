package com.iflytek.speech.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

public class FaceUtil {
    public final static int REQUEST_PICTURE_CHOOSE = 1;
    public final static int REQUEST_CAMERA_IMAGE = 2;
    public final static int REQUEST_CROP_IMAGE = 3;


    public static Uri parseUri(Context context, File file) {
        Uri imageUri = Uri.fromFile(file);
        if (Build.VERSION.SDK_INT >= 24) {
            imageUri = FileProvider.getUriForFile(
                    context,
                    context.getPackageName() + ".fileprovider",
                    file
            );
        }
        return imageUri;
    }

    public static void cropPicture(Activity activity, Uri uri) {
        Intent innerIntent = new Intent("com.android.camera.action.CROP");
        innerIntent.setDataAndType(uri, "image/*");
        innerIntent.putExtra("crop", "true");
        innerIntent.putExtra("aspectX", 1);
        innerIntent.putExtra("aspectY", 1);
        innerIntent.putExtra("outputX", 320);
        innerIntent.putExtra("outputY", 320);
        innerIntent.putExtra("return-data", false);
        innerIntent.putExtra("scale", true);
        innerIntent.putExtra("scaleUpIfNeeded", true);
        File imageFile = new File(getImagePath(activity.getApplicationContext()));
        imageFile.delete();
        Uri cropUri = parseUri(activity, imageFile);
        innerIntent.putExtra(MediaStore.EXTRA_OUTPUT, cropUri);
        innerIntent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());

        List<ResolveInfo> resolveInfos = activity.getPackageManager()
                .queryIntentActivities(innerIntent, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo resolveInfo : resolveInfos) {
            activity.grantUriPermission(activity.getPackageName(), uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            activity.grantUriPermission(resolveInfo.activityInfo.packageName, cropUri,
                    (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION));
        }
        innerIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        innerIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        activity.startActivityForResult(innerIntent, REQUEST_CROP_IMAGE);
    }

    public static String getImagePath(Context context) {
        String path = context.getExternalFilesDir("msc").getAbsolutePath();
        if (!path.endsWith("/")) {
            path += "/";
        }
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        path += "ifd.jpg";
        return path;
    }

    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    public static Bitmap rotateImage(int angle, Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizedBitmap;
    }

    static public void drawFaceRect(Canvas canvas, FaceRect face, int width, int height, boolean frontCamera, boolean DrawOriRect) {
        if (canvas == null) {
            return;
        }

        Paint paint = new Paint();
        paint.setColor(Color.rgb(255, 203, 15));
        int len = (face.bound.bottom - face.bound.top) / 8;
        if (len / 8 >= 2) paint.setStrokeWidth(len / 8);
        else paint.setStrokeWidth(2);

        Rect rect = face.bound;

        if (frontCamera) {
            int top = rect.top;
            rect.top = width - rect.bottom;
            rect.bottom = width - top;
        }

        if (DrawOriRect) {
            paint.setStyle(Style.STROKE);
            canvas.drawRect(rect, paint);
        } else {
            int drawl = rect.left - len;
            int drawr = rect.right + len;
            int drawu = rect.top - len;
            int drawd = rect.bottom + len;

            canvas.drawLine(drawl, drawd, drawl, drawd - len, paint);
            canvas.drawLine(drawl, drawd, drawl + len, drawd, paint);
            canvas.drawLine(drawr, drawd, drawr, drawd - len, paint);
            canvas.drawLine(drawr, drawd, drawr - len, drawd, paint);
            canvas.drawLine(drawl, drawu, drawl, drawu + len, paint);
            canvas.drawLine(drawl, drawu, drawl + len, drawu, paint);
            canvas.drawLine(drawr, drawu, drawr, drawu + len, paint);
            canvas.drawLine(drawr, drawu, drawr - len, drawu, paint);
        }

        if (face.point != null) {
            for (Point p : face.point) {
                if (frontCamera) {
                    p.y = width - p.y;
                }
                canvas.drawPoint(p.x, p.y, paint);
            }
        }
    }

    static public Rect RotateDeg90(Rect r, int width, int height) {
        int left = r.left;
        r.left = height - r.bottom;
        r.bottom = r.right;
        r.right = height - r.top;
        r.top = left;
        return r;
    }

    static public Point RotateDeg90(Point p, int width, int height) {
        int x = p.x;
        p.x = height - p.y;
        p.y = x;
        return p;
    }

    public static int getNumCores() {
        class CpuFilter implements FileFilter {
            @Override
            public boolean accept(File pathname) {
                if (Pattern.matches("cpu[0-9]", pathname.getName())) {
                    return true;
                }
                return false;
            }
        }
        try {
            File dir = new File("/sys/devices/system/cpu/");
            File[] files = dir.listFiles(new CpuFilter());
            return files.length;
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
    }

    public static void saveBitmapToFile(Context context, Bitmap bmp) {
        String file_path = getImagePath(context);
        File file = new File(file_path);
        FileOutputStream fOut;
        try {
            fOut = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
            fOut.flush();
            fOut.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
