package knf.animeflv.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.provider.DocumentFile;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import knf.animeflv.Utils.Files.FileSearchResponse;

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
                    if (DocumentFile.fromFile(fileCur).canWrite()) {
                        sSDpath = fileCur.getAbsolutePath();
                    } else {
                        if (RootFileHaveAccess()) {
                            sSDpath = fileCur.getAbsolutePath();
                        } else {
                            sSDpath = "_noWrite_" + sPathCur;
                        }
                    }
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
                        if (DocumentFile.fromFile(fileCur).canWrite()) {
                            sSDpath = fileCur.getAbsolutePath();
                        } else {
                            if (RootFileHaveAccess()) {
                                sSDpath = fileCur.getAbsolutePath();
                            } else {
                                sSDpath = "_noWrite_" + sPathCur;
                            }
                        }
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
                        if (DocumentFile.fromFile(fileCur).canWrite()) {
                            sSDpath = fileCur.getAbsolutePath();
                        } else {
                            if (RootFileHaveAccess()) {
                                sSDpath = fileCur.getAbsolutePath();
                            } else {
                                sSDpath = "_noWrite_" + sPathCur;
                            }
                        }
                        break;
                    }
                }
            }
        }
        return sSDpath;
    }

    public static FileSearchResponse searchforSD() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return getResponseAccess();
        } else {
            return getResponseNormal();
        }
    }

    private static FileSearchResponse getResponseNormal() {
        List<String> sdNames = new ArrayList<>();
        List<String> exclude = Arrays.asList("expand", "media_rw", "obb", "runtime", "secure", "shared",
                "user", "self", "sdcard", "emulated", "acct", "cache", "config", "d", "data", "dev", "etc",
                "firmware", "fsg", "oem", "persist", "proc", "root", "sbin", "sys", "system", "vendor", "asec", "shell");
        String intName = Environment.getExternalStorageDirectory().getName();
        File mnt = new File("/mnt");
        if (mnt.exists()) {
            for (File dir : mnt.listFiles()) {
                if (dir.isDirectory()) {
                    if (dir.canWrite()) {
                        if (!dir.getName().equals(intName) && !exclude.contains(dir.getName())) {
                            if (!sdNames.contains(dir.getName())) {
                                sdNames.add(dir.getName());
                            }
                        }
                    } else {
                        if (!dir.getName().equals(intName) && !exclude.contains(dir.getName())) {
                            if (!sdNames.contains(dir.getName())) {
                                if (!DocumentFile.fromFile(dir).canWrite()) {
                                    sdNames.add("_noWrite_" + dir.getName());
                                } else {
                                    sdNames.add(dir.getName());
                                }
                            }
                        }
                    }
                }
            }
        }
        File storage = new File("/storage");
        if (storage.exists()) {
            for (File dir : storage.listFiles()) {
                if (dir.isDirectory()) {
                    if (dir.canWrite()) {
                        if (!dir.getName().equals(intName) && !exclude.contains(dir.getName())) {
                            if (!sdNames.contains(dir.getName())) {
                                sdNames.add(dir.getName());
                            }
                        }
                    } else {
                        if (!dir.getName().equals(intName) && !exclude.contains(dir.getName())) {
                            if (!sdNames.contains(dir.getName())) {
                                if (!DocumentFile.fromFile(dir).canWrite()) {
                                    sdNames.add("_noWrite_" + dir.getName());
                                } else {
                                    sdNames.add(dir.getName());
                                }
                            }
                        }
                    }
                }
            }
        }
        return new FileSearchResponse(sdNames);
    }

    private static FileSearchResponse getResponseAccess() {
        List<String> sdNames = new ArrayList<>();
        List<String> exclude = Arrays.asList("expand", "media_rw", "obb", "runtime", "secure", "shared",
                "user", "self", "sdcard", "emulated", "acct", "cache", "config", "d", "data", "dev", "etc",
                "firmware", "fsg", "oem", "persist", "proc", "root", "sbin", "sys", "system", "vendor", "asec", "shell");
        String intName = Environment.getExternalStorageDirectory().getName();
        File mnt = new File("/mnt");
        if (mnt.exists()) {
            for (File dir : mnt.listFiles()) {
                if (dir.isDirectory()) {
                    if (dir.canWrite()) {
                        if (!dir.getName().equals(intName) && !exclude.contains(dir.getName())) {
                            if (!sdNames.contains(dir.getName())) {
                                sdNames.add(dir.getName());
                            }
                        }
                    } else {
                        if (!dir.getName().equals(intName) && !exclude.contains(dir.getName())) {
                            if (!sdNames.contains(dir.getName())) {
                                if (!DocumentFile.fromFile(dir).canWrite()) {
                                    if (RootFileHaveAccess()) {
                                        sdNames.add(dir.getName());
                                    } else {
                                        sdNames.add("_noWrite_" + dir.getName());
                                    }
                                } else {
                                    sdNames.add(dir.getName());
                                }
                            }
                        }
                    }
                }
            }
        }
        File storage = new File("/storage");
        if (storage.exists()) {
            for (File dir : storage.listFiles()) {
                if (dir.isDirectory()) {
                    if (dir.canWrite()) {
                        if (!dir.getName().equals(intName) && !exclude.contains(dir.getName())) {
                            if (!sdNames.contains(dir.getName())) {
                                sdNames.add(dir.getName());
                            }
                        }
                    } else {
                        if (!dir.getName().equals(intName) && !exclude.contains(dir.getName())) {
                            if (!sdNames.contains(dir.getName())) {
                                if (!DocumentFile.fromFile(dir).canWrite()) {
                                    if (RootFileHaveAccess()) {
                                        sdNames.add(dir.getName());
                                    } else {
                                        sdNames.add("_noWrite_" + dir.getName());
                                    }
                                } else {
                                    sdNames.add(dir.getName());
                                }
                            }
                        }
                    }
                }
            }
        }
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
        try {
            return internal.delete() || external.delete() || getFileFromAccess(eid).delete();
        } catch (NullPointerException e) {
            return internal.delete() || external.delete();
        }
    }

    public static DocumentFile getFileFromAccess(String eid) {
        String[] data = eid.replace("E", "").split("_");
        Uri treeUri;
        try {
            treeUri = Uri.parse(PreferenceManager.getDefaultSharedPreferences(context).getString(Keys.Extra.EXTERNAL_SD_ACCESS_URI, null));
        } catch (Exception e) {
            return DocumentFile.fromFile(new File(Environment.getExternalStorageDirectory(), "adahsjkdhuaiohaudusws.txt"));
        }
        if (treeUri != null) {
            DocumentFile sdFile = DocumentFile.fromTreeUri(context, treeUri);
            return findFileFromAccess(findFileFromAccess(findFileFromAccess(findFileFromAccess(sdFile, "Animeflv"), "download"), data[0]), eid.replace("E", "") + ".mp4");
        } else {
            return DocumentFile.fromFile(new File(Environment.getExternalStorageDirectory(), "adahsjkdhuaiohaudusws.txt"));
        }
    }

    public static File getFileNormal(String eid) {
        return new File(getSDPath() + "/Animeflv/download/" + eid.split("_")[0], eid.replace("E", "") + ".mp4");
    }

    @Nullable
    public static DocumentFile getDownloadFromAccess(File file) {
        String name = file.getName();
        Uri treeUri;
        try {
            treeUri = Uri.parse(PreferenceManager.getDefaultSharedPreferences(context).getString(Keys.Extra.EXTERNAL_SD_ACCESS_URI, null));
        } catch (Exception e) {
            return null;
        }
        if (treeUri != null) {
            DocumentFile sdFile = DocumentFile.fromTreeUri(context, treeUri);
            DocumentFile animeFile = findFileFromAccess(findFileFromAccess(findFileFromAccess(findFileFromAccess(sdFile, "Animeflv"), "download"), name.replace(".mp4", "").split("_")[0]), name);
            if (animeFile == null) {
                findFileFromAccess(findFileFromAccess(findFileFromAccess(sdFile, "Animeflv"), "download"), name.replace(".mp4", "").split("_")[0]).createFile("video/mp4", name.replace(".mp4", ""));
            } else {
                animeFile.delete();
                findFileFromAccess(findFileFromAccess(findFileFromAccess(sdFile, "Animeflv"), "download"), name.replace(".mp4", "").split("_")[0]).createFile("video/mp4", name.replace(".mp4", ""));
            }
            return findFileFromAccess(findFileFromAccess(findFileFromAccess(findFileFromAccess(sdFile, "Animeflv"), "download"), name.replace(".mp4", "").split("_")[0]), name);
        } else {
            return null;
        }
    }

    @Nullable
    public static DocumentFile getDownloadDirFromAccess() {
        Uri treeUri;
        try {
            treeUri = Uri.parse(PreferenceManager.getDefaultSharedPreferences(context).getString(Keys.Extra.EXTERNAL_SD_ACCESS_URI, null));
        } catch (Exception e) {
            return null;
        }
        if (treeUri != null) {
            DocumentFile sdFile = DocumentFile.fromTreeUri(context, treeUri);
            return findFileFromAccess(findFileFromAccess(sdFile, "Animeflv"), "download");
        } else {
            return null;
        }
    }

    @Nullable
    public static ParcelFileDescriptor getFileDescriptorFromAccess(Activity activity, File file) {
        DocumentFile sdFile = getDownloadFromAccess(file);
        if (sdFile != null) {
            try {
                return activity.getContentResolver().openFileDescriptor(sdFile.getUri(), "rw");
            } catch (FileNotFoundException e) {
                return null;
            }

        } else {
            return null;
        }
    }

    @Nullable
    public static OutputStream getOutputStreamFromAccess(Activity activity, File file) {
        DocumentFile sdFile = getDownloadFromAccess(file);
        if (sdFile != null) {
            try {
                return activity.getContentResolver().openOutputStream(sdFile.getUri(), "rw");
            } catch (FileNotFoundException e) {
                return null;
            }

        } else {
            return null;
        }
    }

    public static boolean RootFileHaveAccess() {
        Uri treeUri;
        try {
            treeUri = Uri.parse(PreferenceManager.getDefaultSharedPreferences(context).getString(Keys.Extra.EXTERNAL_SD_ACCESS_URI, null));
        } catch (Exception e) {
            return false;
        }
        if (treeUri != null) {
            DocumentFile sdFile = DocumentFile.fromTreeUri(context, treeUri);
            if (sdFile != null) {
                return sdFile.canWrite();
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Nullable
    public static OutputStream getOutputStreamFromAccess(String eid) {
        String[] data = eid.replace("E", "").split("_");
        Uri treeUri = Uri.parse(PreferenceManager.getDefaultSharedPreferences(context).getString(Keys.Extra.EXTERNAL_SD_ACCESS_URI, null));
        if (treeUri != null) {
            DocumentFile sdFile = DocumentFile.fromTreeUri(context, treeUri);
            DocumentFile animeFile = findFileFromAccess(findFileFromAccess(findFileFromAccess(findFileFromAccess(sdFile, "Animeflv"), "download"), data[0]), eid.replace("E", "") + ".mp4");
            if (animeFile == null) {
                animeFile = findFileFromAccess(findFileFromAccess(findFileFromAccess(sdFile, "Animeflv"), "download"), data[0]).createFile("video/mp4", eid.replace("E", ""));
            }
            try {
                return context.getContentResolver().openOutputStream(animeFile.getUri());
            } catch (Exception e) {
                animeFile.delete();
                e.printStackTrace();
                return null;
            }
        } else {
            return null;
        }
    }

    public static OutputStream getOutputStream(String eid) throws FileNotFoundException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return getOutputStreamFromAccess(eid);
        } else {
            return (OutputStream) new FileOutputStream(getFileNormal(eid));
        }
    }

    public static DocumentFile findFileFromAccess(@NonNull DocumentFile parent, String name) {
        DocumentFile file = parent.findFile(name);
        if (file == null) {
            if (!name.endsWith(".mp4")) file = parent.createDirectory(name);
        }
        try {
            if (!file.exists() && !name.endsWith(".mp4")) parent.createDirectory(name);
        } catch (NullPointerException e) {
            Log.d("File Not Exist", name);
        }
        return file;
    }

    public static boolean isInSeen(String eid) {
        return context.getSharedPreferences("data", Context.MODE_PRIVATE).getBoolean("visto" + eid.replace("E", ""), false);
    }

    public static void setSeenState(String eid, boolean seen) {
        context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putBoolean("visto" + eid.replace("E", ""), seen).apply();
        if (seen) {
            String vistos = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("vistos", "");
            if (!vistos.contains(eid)) {
                vistos = vistos + eid + ":::";
                context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("vistos", vistos).apply();
            } else {
                if (vistos.contains(eid + ":::")) {
                    vistos = vistos.replace(eid + ":::", "");
                    context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("vistos", vistos).apply();
                }
            }
        }
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
        array = array.replace("[\"", "")
                .replace("\"]", "")
                .replace("\",\"", ":::")
                .replace("\\/", "/")
                .replace("â\u0098\u0086", "\u2606")
                .replace("&#039;", "\'")
                .replace("&iacute;", "í")
                .replace("&deg;", "°")
                .replace("&amp;", "&")
                .replace("&Delta;", "\u0394")
                .replace("&acirc;", "\u00C2")
                .replace("&egrave;", "\u00E8")
                .replace("&middot;", "\u00B7")
                .replace("&#333;", "\u014D")
                .replace("&#9834;", "\u266A")
                .replace("&aacute;", "á")
                .replace("&oacute;", "ó")
                .replace("&quot;", "\"")
                .replace("&uuml;", "\u00FC")
                .replace("&szlig;", "\u00DF")
                .replace("&radic;", "\u221A")
                .replace("&dagger;", "\u2020")
                .replace("&hearts;", "\u2665")
                .replace("&infin;", "\u221E")
                .replace("♪", "\u266A")
                .replace("â\u0099ª","\u266A")
                .replace("&Psi;","\u03A8");
        return array;
    }

    public static String convertStreamToString(InputStream is) throws Exception {
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
