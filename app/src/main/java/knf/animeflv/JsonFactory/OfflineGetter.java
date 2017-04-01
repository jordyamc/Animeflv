package knf.animeflv.JsonFactory;

import android.os.AsyncTask;
import android.os.Environment;

import org.json.JSONObject;

import java.io.File;

import knf.animeflv.JsonFactory.JsonTypes.ANIME;
import knf.animeflv.Utils.ExecutorManager;
import knf.animeflv.Utils.FileUtil;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class OfflineGetter {
    public static final File inicio = new File(Environment.getExternalStorageDirectory() + "/Animeflv/cache/inicio.txt");
    public static final File directorio = new File(Environment.getExternalStorageDirectory() + "/Animeflv/cache/directorio.txt");
    public static final File animecache = new File(Environment.getExternalStorageDirectory() + "/Animeflv/cache");
    public static final File emision = new File(Environment.getExternalStorageDirectory() + "/Animeflv/cache/emision.txt");

    public static String getInicio() {
        checkCacheDir();
        if (inicio.exists()) {
            String json = FileUtil.getStringFromFile(inicio);
            if (FileUtil.isJSONValid(json)) {
                return json;
            } else {
                inicio.delete();
            }
        }
        return "null";
    }

    public static String getDirectorio() {
        checkCacheDir();
        if (directorio.exists()) {
            String json = FileUtil.getStringFromFile(directorio);
            if (FileUtil.isJSONValid(json)) {
                return json;
            } else {
                directorio.delete();
            }
        }
        return "null";
    }

    public static String getAnime(ANIME anime) {
        checkCacheDir();
        File a = new File(animecache, anime.aid + ".txt");
        if (a.exists()) {
            return FileUtil.getStringFromFile(a);
        } else {
            return "null";
        }
    }

    public static String getEmision() {
        checkCacheDir();
        if (emision.exists()) {
            String json = FileUtil.getStringFromFile(emision);
            if (FileUtil.isJSONValid(json)) {
                return json;
            } else {
                emision.delete();
            }
        }
        return "null";
    }

    private static void checkCacheDir() {
        if (animecache.toString().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            if (!animecache.exists()) {
                animecache.mkdirs();
            }
        }
    }

    public static void backupJson(final JSONObject data, final File file) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                FileUtil.writeToFile(data.toString(), file);
                return null;
            }
        }.executeOnExecutor(ExecutorManager.getExecutor());
    }

    public static void backupJsonSync(final JSONObject data, final File file) {
        FileUtil.writeToFile(data.toString(), file);
    }
}
