package knf.animeflv.Utils;

import android.content.Context;
import android.os.Environment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;

import knf.animeflv.Parser;
import knf.animeflv.TaskType;

/**
 * Created by Jordy on 01/04/2016.
 */
public class UrlUtils {
    private static Context context;

    public static void init(Context c) {
        context = c;
    }

    public static String getTitCached(String aid) {
        String ret = "null";
        String file_loc = Environment.getExternalStorageDirectory() + "/Animeflv/cache/directorio.txt";
        File file = new File(file_loc);
        if (file.exists()) {
            try {
                JSONObject jsonObj = new JSONObject(FileUtil.getStringFromFile(file_loc));
                JSONArray jsonArray = jsonObj.getJSONArray("lista");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject nombreJ = jsonArray.getJSONObject(i);
                    String n = nombreJ.getString("a");
                    if (n.trim().equals(aid)) {
                        return FileUtil.corregirTit(nombreJ.getString("b"));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return FileUtil.corregirTit(ret);
    }

    public static String getAidCached(String url) {
        String ret = "0";
        String file_loc = Environment.getExternalStorageDirectory() + "/Animeflv/cache/directorio.txt";
        File file = new File(file_loc);
        if (file.exists()) {
            try {
                JSONObject jsonObj = new JSONObject(FileUtil.getStringFromFile(file_loc));
                JSONArray jsonArray = jsonObj.getJSONArray("lista");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject nombreJ = jsonArray.getJSONObject(i);
                    String corto = nombreJ.getString("d");
                    if (corto.trim().equals(url.trim())) {
                        return FileUtil.corregirTit(nombreJ.getString("a"));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    public static String getUrlCapCached(String eid) {
        String[] data = eid.replace("E", "").split("_");
        String aid = data[0];
        String numero = data[1];
        String ret = "null";
        String file_loc = Environment.getExternalStorageDirectory() + "/Animeflv/cache/directorio.txt";
        File file = new File(file_loc);
        if (file.exists()) {
            try {
                JSONObject jsonObj = new JSONObject(FileUtil.getStringFromFile(file_loc));
                JSONArray jsonArray = jsonObj.getJSONArray("lista");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject nombreJ = jsonArray.getJSONObject(i);
                    String n = nombreJ.getString("a");
                    if (n.trim().equals(aid)) {
                        Parser parser = new Parser();
                        return parser.getInicioUrl(TaskType.NORMAL, context) + "?certificate=" + parser.getCertificateSHA1Fingerprint(context) + "&url=http://animeflv.net/ver/" + nombreJ.getString("d") + "-" + numero + ".html";
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    public static String getUrlAnimeCached(String aid) {
        String ret = "null";
        String file_loc = Environment.getExternalStorageDirectory() + "/Animeflv/cache/directorio.txt";
        File file = new File(file_loc);
        if (file.exists()) {
            try {
                JSONObject jsonObj = new JSONObject(FileUtil.getStringFromFile(file_loc));
                JSONArray jsonArray = jsonObj.getJSONArray("lista");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject nombreJ = jsonArray.getJSONObject(i);
                    String n = nombreJ.getString("a");
                    if (n.trim().equals(aid)) {
                        Parser parser = new Parser();
                        return parser.getInicioUrl(TaskType.NORMAL, context) + "?certificate=" + parser.getCertificateSHA1Fingerprint(context) + "&url=http://animeflv.net/" + nombreJ.getString("c").toLowerCase() + "/" + nombreJ.getString("d") + ".html";
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ret;
    }
}
