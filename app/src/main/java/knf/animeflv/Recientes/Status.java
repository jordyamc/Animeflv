package knf.animeflv.Recientes;

import android.os.Environment;

import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import knf.animeflv.Utils.FileUtil;

public class Status {
    private static String server_version = "0.0";
    private static String cache = "0";
    private static String late = "00:00";
    private static Status status = new Status();

    public static Status reload() {
        String file_loc = Environment.getExternalStorageDirectory() + "/Animeflv/cache/inicio.txt";
        File file = new File(file_loc);
        if (file.exists()) {
            try {
                JSONObject object = new JSONObject(FileUtil.getStringFromFile(file_loc));
                server_version = object.getString("version");
                cache = object.getString("cache");
                late = object.getString("last");
            } catch (Exception e) {

            }
        }
        return status;
    }

    public static String getVersion() {
        return server_version;
    }

    public static String getCacheStatusString() {
        String s = "OK";
        if (getCacheStatusInt() == 1) {
            s = "CACHE";
        }
        return s;
    }

    public static int getCacheStatusInt() {
        return Integer.parseInt(cache);
    }

    public static String getLateRefresh() {
        return UTCtoLocal(late);
    }

    private static String UTCtoLocal(String utc) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mmaa", Locale.ENGLISH);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date myDate = simpleDateFormat.parse(utc);
            simpleDateFormat.setTimeZone(TimeZone.getDefault());
            return simpleDateFormat.format(myDate);
        } catch (Exception e) {
            e.printStackTrace();
            return utc;
        }
    }

}
