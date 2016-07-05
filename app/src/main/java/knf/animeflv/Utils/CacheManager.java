package knf.animeflv.Utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Callback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import knf.animeflv.Parser;
import knf.animeflv.PicassoCache;
import knf.animeflv.R;
import knf.animeflv.TaskType;

public class CacheManager {
    public static final File miniCache = new File(Environment.getExternalStorageDirectory() + "/Animeflv/cache/mini");
    public static final File portadaCache = new File(Environment.getExternalStorageDirectory() + "/Animeflv/cache/portada");
    public static final File miniCacheNoMedia = new File(miniCache,".nomedia");
    public static final File portadaCacheNoMedia = new File(portadaCache,".nomedia");
    private Parser parser = new Parser();

    public void portada(final Activity context, String aid, final ImageView imageView) {
        checkCacheDirs();
        final File localFile = new File(portadaCache, aid + ".jpg");
        boolean isHD = false;
        if (localFile.exists()) {
            PicassoCache.getPicassoInstance(context).load(localFile).into(imageView);
            isHD = isHD(localFile);
            if (isHD) Log.d("Local", "is HD!!!");
        }
        if (NetworkUtils.isNetworkAvailable() && !isHD) {
            PicassoCache.getPicassoInstance(context).load(parser.getBaseUrl(TaskType.NORMAL, context) + "imagen.php?certificate=" + parser.getCertificateSHA1Fingerprint(context) + "&hd=http://cdn.animeflv.net/img/portada/thumb_80/" + aid + ".jpg").noFade().noPlaceholder().error(R.drawable.ic_block_r).into(imageView, new Callback() {
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
    }

    public void mini(final Activity context, String aid, final ImageView imageView) {
        checkCacheDirs();
        final File localFile = new File(miniCache, aid + ".jpg");
        imageView.setImageResource(android.R.color.transparent);
        if (localFile.exists()) {
            PicassoCache.getPicassoInstance(context).load(localFile).into(imageView);
        } else {
            PicassoCache.getPicassoInstance(context).load(parser.getBaseUrl(TaskType.NORMAL, context) + "imagen.php?certificate=" + parser.getCertificateSHA1Fingerprint(context) + "&thumb=http://cdn.animeflv.net/img/portada/thumb_80/" + aid + ".jpg").error(R.drawable.ic_block_r).into(imageView, new Callback() {
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
    }

    private void checkCacheDirs() {
        if (!miniCache.exists()) miniCache.mkdirs();
        if (!portadaCache.exists()) portadaCache.mkdirs();
        if (!miniCacheNoMedia.exists())try {miniCacheNoMedia.createNewFile();}catch (Exception e){e.printStackTrace();}
        if (!portadaCacheNoMedia.exists())try {portadaCacheNoMedia.createNewFile();}catch (Exception e){e.printStackTrace();}
    }

    private boolean isHD(File file) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getAbsolutePath(), options);
        if (Integer.parseInt(file.getName().replace(".jpg", "")) < 2418) {
            return true;
        } else {
            return options.outWidth > 100;
        }
    }

    public static void invalidateCache(final File file, final boolean withDirectory, final boolean exclude,final OnInvalidateCache inter) {
        new AsyncTask<String,String,String>(){
            @Override
            protected String doInBackground(String... strings) {
                for (File f : file.listFiles()) {
                    delete(f,withDirectory, exclude);
                }
                inter.onFinish();
                return null;
            }
        }.executeOnExecutor(ExecutorManager.getExecutor());
    }

    public static List<String> getExceptionList(){
        List<String> list=new ArrayList<>();
        list.add(".sounds");
        list.add("data.save");
        list.add("inicio.txt");
        list.add("directorio.txt");
        return list;
    }

    private static void delete(File file, boolean withDirectory, boolean exclude){
        if (file.isFile()){
            if (exclude){
                if (!getExceptionList().contains(file.getName())){
                    file.delete();
                }
            }else {
                file.delete();
            }
        }else {
            if (withDirectory){
                if (file.isDirectory()){
                    for (File f:file.listFiles()){
                        delete(f,true, exclude);
                    }
                }
            }
        }
    }

    public static long getSize(File file) {
        long size = 0;
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
        return size;
    }

    public static long getSize(File file, boolean directory) {
        long size = 0;
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
                onFinishCount.counted(formatSize(getSize(file,false)));
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

    private void saveBitmap(final Bitmap bitmap, final File file) {
        new AsyncTask<String,String,String>(){
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
    }

    public interface OnInvalidateCache {
        void onFinish();
    }

    public interface OnFinishCount {
        void counted(String formated);
    }
}
