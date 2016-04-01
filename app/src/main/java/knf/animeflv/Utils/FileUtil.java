package knf.animeflv.Utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.preference.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import knf.animeflv.Utils.Files.FileSearchResponse;

/**
 * Created by Jordy on 22/03/2016.
 */
public class FileUtil {
    private static FileUtil util = new FileUtil();
    private static Context context;

    public static void init(Context cont) {
        if (util == null) {
            util = new FileUtil();
        }
        context = cont;
    }

    public static String getSDPath() {
        String sSDpath = null;
        File fileCur = null;
        for (String sPathCur : Arrays.asList("MicroSD", "external_SD", "sdcard1", "ext_card", "external_sd", "ext_sd", "external", "extSdCard", "externalSdCard", PreferenceManager.getDefaultSharedPreferences(context).getString("SDPath", "null"))) {
            fileCur = new File("/mnt/", sPathCur);
            if (fileCur.isDirectory()) {
                if (fileCur.canWrite()) {
                    sSDpath = fileCur.getAbsolutePath();
                    break;
                } else {
                    sSDpath = "_noWrite_" + sPathCur;
                    break;
                }
            }
            if (sSDpath == null) {
                fileCur = new File("/storage/", sPathCur);
                if (fileCur.isDirectory()) {
                    if (fileCur.canWrite()) {
                        sSDpath = fileCur.getAbsolutePath();
                        break;
                    } else {
                        sSDpath = "_noWrite_" + sPathCur;
                        break;
                    }
                }
            }
            if (sSDpath == null) {
                fileCur = new File("/storage/emulated", sPathCur);
                if (fileCur.isDirectory()) {
                    if (fileCur.canWrite()) {
                        sSDpath = fileCur.getAbsolutePath();
                        break;
                    } else {
                        sSDpath = "_noWrite_" + sPathCur;
                        break;
                    }
                }
            }
        }
        return sSDpath;
    }

    public static FileSearchResponse searchforSD() {
        List<String> sdNames = new ArrayList<>();
        List<String> exclude = Arrays.asList("expand", "media_rw", "obb", "runtime", "secure", "shared", "user", "self", "sdcard", "emulated");
        String intName = Environment.getExternalStorageDirectory().getName();
        File mnt = new File("/mnt");
        if (mnt.exists()) {
            for (File dir : mnt.listFiles()) {
                if (dir.isDirectory() && dir.canWrite()) {
                    if (!dir.getName().equals(intName) && !exclude.contains(dir.getName())) {
                        if (!sdNames.contains(dir.getName())) {
                            sdNames.add(dir.getName());
                        }
                    }
                }
            }
        }
        File storage = new File("/storage");
        if (storage.exists()) {
            for (File dir : storage.listFiles()) {
                if (dir.isDirectory() && dir.canWrite()) {
                    if (!dir.getName().equals(intName) && !exclude.contains(dir.getName())) {
                        if (!sdNames.contains(dir.getName())) {
                            sdNames.add(dir.getName());
                        }
                    }
                }
            }
        }
        /*File emulated=new File("/storage/emulated");
        if (emulated.exists()) {
            for (File dir : emulated.listFiles()) {
                if (dir.isDirectory() && dir.canWrite()) {
                    if (!dir.getAbsolutePath().equals(Environment.getExternalStorageState())) {
                        if (!sdNames.contains(dir.getName())) {
                            sdNames.add(dir.getName());
                        }
                    }
                }
            }
        }*/
        return new FileSearchResponse(sdNames);
    }

    public static boolean ExistAnime(String eid) {
        String[] data = eid.replace("E", "").split("_");
        File internal = new File(Environment.getExternalStorageDirectory() + "/Animeflv/download/" + data[0], eid.replace("E", "") + ".mp4");
        File external = new File(getSDPath() + "/Animeflv/download/" + data[0], eid.replace("E", "") + ".mp4");
        return internal.exists() || external.exists();
    }

    public static boolean DeleteAnime(String eid) {
        String[] data = eid.replace("E", "").split("_");
        File internal = new File(Environment.getExternalStorageDirectory() + "/Animeflv/download/" + data[0], eid.replace("E", "") + ".mp4");
        File external = new File(getSDPath() + "/Animeflv/download/" + data[0], eid.replace("E", "") + ".mp4");
        return internal.delete() || external.delete();
    }

    public static boolean isMXinstalled() {
        List<ApplicationInfo> packages;
        PackageManager pm;
        pm = context.getPackageManager();
        packages = pm.getInstalledApplications(0);
        String pack = "null";
        for (ApplicationInfo packageInfo : packages) {
            if (packageInfo.packageName.equals("com.mxtech.videoplayer.pro")) {
                pack = "com.mxtech.videoplayer.pro";
                break;
            }
            if (packageInfo.packageName.equals("com.mxtech.videoplayer.ad")) {
                pack = "com.mxtech.videoplayer.ad";
                break;
            }
        }
        return !pack.equals("null");
    }

    public static boolean isJSONValid(String test) {
        try {
            new JSONObject(test);
        } catch (JSONException ex) {
            try {
                new JSONArray(test);
            } catch (JSONException ex1) {
                ex1.printStackTrace();
                return false;
            }
        }
        return true;
    }

    public static void writeToFile(String body, File file) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(body.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getStringFromFile(String filePath) {
        String ret = "";
        try {
            File fl = new File(filePath);
            FileInputStream fin = new FileInputStream(fl);
            ret = convertStreamToString(fin);
            fin.close();
        } catch (IOException e) {
        } catch (Exception e) {
        }
        return ret;
    }

    public static String getStringFromFile(File fl) {
        String ret = "";
        try {
            FileInputStream fin = new FileInputStream(fl);
            ret = convertStreamToString(fin);
            fin.close();
        } catch (IOException e) {
        } catch (Exception e) {
        }
        return ret;
    }

    public static boolean isNumber(String number) {
        try {
            Integer.parseInt(number);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static String corregirTit(String tit) {
        String array = tit;
        array = array.replace("[\"", "");
        array = array.replace("\"]", "");
        array = array.replace("\",\"", ":::");
        array = array.replace("\\/", "/");
        array = array.replace("â\u0098\u0086", "\u2606");
        array = array.replace("&#039;", "\'");
        array = array.replace("&iacute;", "í");
        array = array.replace("&deg;", "°");
        array = array.replace("&amp;", "&");
        array = array.replace("&acirc;", "\u00C2");
        array = array.replace("&egrave;", "\u00E8");
        array = array.replace("&middot;", "\u00B7");
        array = array.replace("&#333;", "\u014D");
        array = array.replace("&#9834;", "\u266A");
        array = array.replace("&aacute;", "á");
        array = array.replace("&oacute;", "ó");
        array = array.replace("&quot;", "\"");
        array = array.replace("&uuml;", "\u00FC");
        array = array.replace("&szlig;", "\u00DF");
        array = array.replace("&radic;", "\u221A");
        array = array.replace("&dagger;", "\u2020");
        array = array.replace("&hearts;", "\u2665");
        return array;
    }

    private static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        reader.close();
        return sb.toString();
    }

    public static boolean existDir() {
        return new File(Environment.getExternalStorageDirectory() + "/Animeflv/cache/directorio.txt").exists();
    }
}
