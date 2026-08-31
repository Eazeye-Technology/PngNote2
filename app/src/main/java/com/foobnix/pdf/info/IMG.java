package com.foobnix.pdf.info;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.foobnix.android.utils.Dips;
import com.foobnix.android.utils.LOG;
import com.sys.speech.app.SpeechApp;
import com.txkj.notemobile2.BookListActivity;

public class IMG {
    public static int coverBigSize = (int) (((Dips.screenWidthDP() / (Dips.screenWidthDP() / 120)) - 8) * (Dips.isXLargeScreen() ? 1.5f : 1));
    public static int coverSmallSize = 80;

    public static int getImageSize() {
        return Dips.dpToPx(Math.max(coverSmallSize, coverBigSize));
    }

    public static interface ResourceReady {
        void onResourceReady(Bitmap bitmap);
    }

    public static void getCoverPageWithEffect(ImageView img, String path, int width, ResourceReady run) {
        String url = path;//IMG.toUrl(path, ImageExtractor.COVER_PAGE, width);
        LOG.d("Bitmap-test-load", path);
        IMG.with(img.getContext())
                .clearOnStop() //FIXME:added
                .asBitmap()
                .load(url)
                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                //.diskCacheStrategy(DiskCacheStrategy.NONE)
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        LOG.d("Bitmap-test-2", "failed");

                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap bitmap, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        target.onResourceReady(bitmap, null);
                        LOG.d("Bitmap-test-2", bitmap, bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());

                        if (run != null) {
                            run.onResourceReady(null);
                        }
                        return true;
                    }
                })
                .into(img);
    }

    public static RequestManager with(Context a) {
        if (a instanceof BookListActivity) {
            return Glide.with((BookListActivity) a);
        } else {
            return null;
        }
    }

    public static void pauseRequests(Context a) {
        LOG.d("Glide-pause", a);
        //with(a).pauseRequests();
    }

    public static void resumeRequests(Context a) {
        LOG.d("Glide-resume", a);
        //with(a).resumeRequests();
    }

    public static void clear(ImageView image) {
        try {
            LOG.d("Glide-clear", image.getContext());
            Activity activity = ((Activity) image.getContext());
            if (Build.VERSION.SDK_INT < 17 || !activity.isDestroyed()) {
                with(image.getContext()).clear(image);
            }
        } catch (Exception e) {
            LOG.e(e);
        }
    }

    public static void clear(Context c, Target t) {
        LOG.d("Glide-clear", c);
        try {
            with(c).clear(t);
        } catch (Exception e) {
            LOG.e(e);
        }
    }

    public static void clearMemoryCache() {
        if (SpeechApp.context != null) {
            Glide.get(SpeechApp.context).clearMemory();
        }
    }

    public static void clearDiscCache() {
        new Thread("@T clearDiscCache") {
            @Override
            public void run() {
                try {
                    if (SpeechApp.context != null) {
                        Glide.get(SpeechApp.context).clearDiskCache();
                    }
                } catch (Exception e) {
                    LOG.e(e);
                }
            }
        }.start();
    }

//    public static void clearCache(String path) {
//        try {
//            String url = IMG.toUrl(path, ImageExtractor.COVER_PAGE, IMG.getImageSize());
//            //Glide.get(LibreraApp.context).clearMemory();
//            //Glide.get(LibreraApp.context).getRegistry()
//        } catch (Exception e) {
//            LOG.e(e);
//        }
//    }
}
