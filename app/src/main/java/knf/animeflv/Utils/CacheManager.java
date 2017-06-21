package knf.animeflv.Utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.UiThread;
import android.util.Log;
import android.widget.ImageView;

import com.github.siyamed.shapeimageview.CircularImageView;
import com.squareup.picasso.Callback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import knf.animeflv.JsonFactory.MALGetter;
import knf.animeflv.Parser;
import knf.animeflv.PicassoCache;
import knf.animeflv.R;
import knf.animeflv.TaskType;
import xdroid.toaster.Toaster;

public class CacheManager {
    public static final File miniCache = new File(Environment.getExternalStorageDirectory() + "/Animeflv/cache/mini");
    public static final File portadaCache = new File(Environment.getExternalStorageDirectory() + "/Animeflv/cache/portada");
    public static final File hallImgs = new File(Environment.getExternalStorageDirectory() + "/Animeflv/cache/hall");
    public static final File miniCacheNoMedia = new File(miniCache, ".nomedia");
    public static final File portadaCacheNoMedia = new File(portadaCache, ".nomedia");
    public static final File hallCacheNoMedia = new File(hallImgs, ".nomedia");
    private Parser parser = new Parser();

    public static void invalidateCache(final File file, final boolean withDirectory, final boolean exclude, final OnInvalidateCache inter) {
        new AsyncTask<String, String, String>() {
            @Override
            protected String doInBackground(String... strings) {
                try {
                    for (File f : file.listFiles()) {
                        delete(f, withDirectory, exclude);
                    }
                    inter.onFinish();
                } catch (Exception e) {
                    Toaster.toast("Error al vaciar cache");
                    inter.onFinish();
                }
                return null;
            }
        }.executeOnExecutor(ExecutorManager.getExecutor());
    }

    public static void invalidateCacheSync(final File file, final boolean withDirectory, final boolean exclude) {
        for (File f : file.listFiles()) {
            delete(f, withDirectory, exclude);
        }
    }

    public static List<String> getExceptionList() {
        List<String> list = new ArrayList<>();
        list.add(".sounds");
        list.add("data.save");
        list.add("inicio.txt");
        list.add("directorio.txt");
        return list;
    }

    private static void delete(File file, boolean withDirectory, boolean exclude) {
        if (file.isFile()) {
            if (exclude) {
                if (!getExceptionList().contains(file.getName())) {
                    file.delete();
                }
            } else {
                file.delete();
            }
        } else {
            if (withDirectory) {
                if (file.isDirectory()) {
                    for (File f : file.listFiles()) {
                        delete(f, true, exclude);
                    }
                }
            }
        }
    }

    public static long getSize(File file) {
        long size = 0;
        try {
            if (file.isDirectory()) {
                if (!getExceptionList().contains(file.getName())) {
                    for (File f : file.listFiles()) {
                        size = size + getSize(f);
                    }
                }
            } else {
                if (!getExceptionList().contains(file.getName())) {
                    size = size + file.length();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    public static long getSize(File file, boolean directory) {
        long size = 0;
        try {
            if (file.isDirectory()) {
                if (!getExceptionList().contains(file.getName())) {
                    for (File f : file.listFiles()) {
                        if (!f.isDirectory()) {
                            size = size + getSize(f);
                        }
                    }
                }
            } else {
                if (!getExceptionList().contains(file.getName())) {
                    size = size + file.length();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    public static String formatSize(long v) {
        if (v < 1024) return v + " B";
        int z = (63 - Long.numberOfLeadingZeros(v)) / 10;
        return String.format(Locale.US, "%.1f %sB", (double) v / (1L << (z * 10)), " KMGTPE".charAt(z));
    }

    public static long getcachesize(Activity activity) {
        long size = 0;
        File[] files = activity.getCacheDir().listFiles();
        File[] mediaStorage = new File(Environment.getExternalStorageDirectory() + "/Animeflv/cache").listFiles();
        for (File f : files) {
            size = size + getSize(f);
        }
        if (mediaStorage != null) {
            for (File f1 : mediaStorage) {
                size = size + getSize(f1);
            }
        }
        return size;
    }

    public static void asyncGetFormatedCacheSize(final Activity activity, final OnFinishCount onFinishCount) {
        new AsyncTask<String, String, String>() {
            @Override
            protected String doInBackground(String... params) {
                onFinishCount.counted(formatSize(getcachesize(activity)));
                return null;
            }
        }.executeOnExecutor(ExecutorManager.getExecutor());
    }

    public static void asyncGetFormatedFileSize(final File file, final OnFinishCount onFinishCount) {
        new AsyncTask<String, String, String>() {
            @Override
            protected String doInBackground(String... params) {
                onFinishCount.counted(formatSize(getSize(file)));
                return null;
            }
        }.executeOnExecutor(ExecutorManager.getExecutor());
    }

    public static void asyncGetFormatedCacheSize(final File file, final OnFinishCount onFinishCount) {
        new AsyncTask<String, String, String>() {
            @Override
            protected String doInBackground(String... params) {
                onFinishCount.counted(formatSize(getSize(file, false)));
                return null;
            }
        }.executeOnExecutor(ExecutorManager.getExecutor());
    }

    public static void asyncGetFormatedFileNumber(final File file, final boolean withDirectory, final OnFinishCount onFinishCount) {
        new AsyncTask<String, String, String>() {
            @Override
            protected String doInBackground(String... params) {
                onFinishCount.counted(formatSize(getFileNumber(file, withDirectory)));
                return null;
            }
        }.executeOnExecutor(ExecutorManager.getExecutor());
    }

    public static int getFileNumber(File file, boolean withDirectory) {
        int number = 0;
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                if (withDirectory) {
                    number = number + getFileNumber(f, true);
                }
            }
        } else {
            number = 1;
        }
        return number;
    }

    public void invalidatePortada(String aid) {
        new File(portadaCache, aid + ".jpg").delete();
    }

    @UiThread
    public void portada(final Activity context, final String aid, final ImageView imageView) {
        checkCacheDirs();
        final File localFile = new File(portadaCache, aid + ".jpg");
        boolean isHD = false;
        if (localFile.exists()) {
            PicassoCache.getPicassoInstance(context).load(localFile).into(imageView);
            isHD = isHD(localFile);
            if (isHD) Log.e("Local", "is HD!!!");
        }
        if (NetworkUtils.isNetworkAvailable() && !isHD) {
            final String title = new Parser().getTitCached(aid).trim();
            MALGetter.getAnimeSearch(title, new MALGetter.SearchInterface() {
                @Override
                public void onFinishSearch(String r, boolean success) {
                    try {
                        String result = MALGetter.parseImageHtml(r, title);
                        if (success && !result.startsWith("_error")) {
                            Log.e("Image Getter", result);
                            if (localFile.exists())
                                PicassoCache.getPicassoInstance(context).invalidate(localFile);
                            PicassoCache.getPicassoInstance(context).load(result).noFade().noPlaceholder().into(imageView, new Callback() {
                                @Override
                                public void onSuccess() {
                                    if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("save_portada", true))
                                        saveBitmap(((BitmapDrawable) imageView.getDrawable()).getBitmap(), localFile);
                                }

                                @Override
                                public void onError() {

                                }
                            });
                        } else {
                            Log.e("Portada", "Searching in page: " + "http://animeflv.net/uploads/animes/covers/" + aid + ".jpg");
                            PicassoCache.getPicassoInstance(context).load("http://animeflv.net/uploads/animes/covers/" + aid + ".jpg").noFade().noPlaceholder().into(imageView, new Callback() {
                                @Override
                                public void onSuccess() {
                                    if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("save_portada", true))
                                        saveBitmap(((BitmapDrawable) imageView.getDrawable()).getBitmap(), localFile);
                                }

                                @Override
                                public void onError() {

                                }
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public void mini(final Activity context, final String aid, final ImageView imageView) {
        checkCacheDirs();
        final File localFile = new File(miniCache, aid + ".jpg");
        imageView.setImageResource(android.R.color.transparent);
        try {
            if (localFile.exists()) {
                PicassoCache.getPicassoInstance(context).load(localFile).into(imageView);
            } else {
                PicassoCache.getPicassoInstance(context).load(parser.getBaseUrl(TaskType.NORMAL, context) + "imagen.php?certificate=" + Parser.getCertificateSHA1Fingerprint(context) + "&thumb=http://cdn.animeflv.net/img/portada/thumb_80/" + aid + ".jpg").into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("save_mini", true))
                            saveBitmap(((BitmapDrawable) imageView.getDrawable()).getBitmap(), localFile);
                    }

                    @Override
                    public void onError() {
                        PicassoCache.getPicassoInstance(context).load("http://animeflv.net/uploads/animes/covers/" + aid + ".jpg").error(R.drawable.ic_block_r).into(imageView, new Callback() {
                            @Override
                            public void onSuccess() {
                                if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("save_mini", true))
                                    saveBitmap(((BitmapDrawable) imageView.getDrawable()).getBitmap(), localFile);
                            }

                            @Override
                            public void onError() {

                            }
                        });
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void hallMini(final Activity activity, String id, String link, final CircularImageView imageView) {
        checkCacheDirs();
        final File localFile = new File(hallImgs, id + "_mini.png");
        if (localFile.exists()) {
            PicassoCache.getPicassoInstance(activity).load(localFile).into(imageView);
        }
        if (NetworkUtils.isNetworkAvailable()) {
            PicassoCache.getPicassoInstance(activity).load(link).noPlaceholder().noFade().error(R.drawable.def).into(imageView, new Callback() {
                @Override
                public void onSuccess() {
                    saveBitmap(((BitmapDrawable) imageView.getDrawable()).getBitmap(), localFile);
                }

                @Override
                public void onError() {

                }
            });
        }
    }

    public void hallLarge(final Activity activity, String id, String link, final CircularImageView imageView) {
        checkCacheDirs();
        final File localFile = new File(hallImgs, id + "_large.png");
        if (localFile.exists()) {
            PicassoCache.getPicassoInstance(activity).load(localFile).into(imageView);
        } else {
            if (NetworkUtils.isNetworkAvailable())
                PicassoCache.getPicassoInstance(activity).load(link).error(R.drawable.ic_block_r).into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        saveBitmap(((BitmapDrawable) imageView.getDrawable()).getBitmap(), localFile);
                    }

                    @Override
                    public void onError() {

                    }
                });
        }
    }

    private void checkCacheDirs() {
        if (!miniCache.exists()) miniCache.mkdirs();
        if (!portadaCache.exists()) portadaCache.mkdirs();
        if (!hallImgs.exists()) hallImgs.mkdirs();
        if (!miniCacheNoMedia.exists()) try {
            miniCacheNoMedia.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!portadaCacheNoMedia.exists()) try {
            portadaCacheNoMedia.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!hallCacheNoMedia.exists()) try {
            hallCacheNoMedia.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isHD(File file) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getAbsolutePath(), options);
        Log.d("Cache Image", "" + options.outHeight + " - " + options.outWidth);
        return options.outWidth > 100;
    }

    private void saveBitmap(final Bitmap bitmap, final File file) {
        try {
            new AsyncTask<String, String, String>() {
                @Override
                protected String doInBackground(String... strings) {
                    if (file.exists()) file.delete();
                    FileOutputStream out = null;
                    try {
                        out = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            if (out != null) {
                                out.close();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    return null;
                }
            }.executeOnExecutor(ExecutorManager.getExecutor());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface OnInvalidateCache {
        void onFinish();
    }

    public interface OnFinishCount {
        void counted(String formated);
    }
}
