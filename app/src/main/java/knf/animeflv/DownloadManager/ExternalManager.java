package knf.animeflv.DownloadManager;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;

import knf.animeflv.DManager;
import knf.animeflv.Directorio.DB.DirectoryHelper;
import knf.animeflv.DownloadService.DownloadListManager;
import knf.animeflv.DownloadService.DownloadObject;
import knf.animeflv.DownloadService.DownloaderService;
import knf.animeflv.DownloadService.SQLiteHelperDownloads;
import knf.animeflv.Parser;

public class ExternalManager {
    Activity context;
    SharedPreferences sharedPreferences;
    String EXTERNA = "1";
    Parser parser = new Parser();

    public ExternalManager(Activity context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("data", Context.MODE_PRIVATE);
    }

    public static boolean isDownloading(Activity activity, String eid) {
        return new SQLiteHelperDownloads(activity).downloadExist(eid);
    }

    public static int getDownloadState(Activity activity, String eid) {
        return new SQLiteHelperDownloads(activity).getState(eid);
    }

    public void startDownload(String eid, String url) {
        sharedPreferences.edit().putString(eid + "dtype", EXTERNA).apply();
        long downloadID = System.currentTimeMillis();
        sharedPreferences.edit().putLong(eid + "_downloadID", downloadID).commit();
        Intent intent = new Intent(context, DownloaderService.class);
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        bundle.putString("eid", eid);
        bundle.putLong("downloadID", downloadID);
        bundle.putBoolean("constructor", false);
        intent.putExtras(bundle);
        DownloadListManager.add(context, eid + "_" + downloadID);
        new SQLiteHelperDownloads(context)
                .addElement(new DownloadObject(downloadID, url, eid))
                .updateState(eid, DownloadManager.STATUS_PENDING)
                .close();
        ContextCompat.startForegroundService(context, intent);
    }

    public void startDownload(String eid, String url, CookieConstructor constructor) {
        sharedPreferences.edit().putString(eid + "dtype", EXTERNA).apply();
        long downloadID = System.currentTimeMillis();
        sharedPreferences.edit().putLong(eid + "_downloadID", downloadID).commit();
        Intent intent = new Intent(context, DownloaderService.class);
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        bundle.putString("eid", eid);
        bundle.putLong("downloadID", downloadID);
        bundle.putBoolean("constructor", true);
        bundle.putString("cookie", constructor.getCookie());
        bundle.putString("referer", constructor.getReferer());
        bundle.putString("useragent", constructor.getUseAgent());
        intent.putExtras(bundle);
        DownloadListManager.add(context, eid + "_" + downloadID);
        new SQLiteHelperDownloads(context)
                .addElement(new DownloadObject(downloadID, url, eid))
                .updateState(eid, DownloadManager.STATUS_PENDING)
                .close();
        ContextCompat.startForegroundService(context, intent);
    }

    public void cancelDownload(String eid) {
        String aid = eid.replace("E", "").substring(0, eid.lastIndexOf("_"));
        String numero = eid.replace("E", "").substring(eid.lastIndexOf("_") + 1);
        String titulo = DirectoryHelper.get(context).getTitle(aid);
        sharedPreferences.edit().putString(eid + "dtype", "2").apply();
        DManager.getManager().cancel(Integer.parseInt(sharedPreferences.getString(eid, "0")));
        String descargados = sharedPreferences.getString("eids_descarga", "");
        sharedPreferences.edit().putString("eids_descarga", descargados.replace(eid + ":::", "")).apply();
        String tits = sharedPreferences.getString("titulos_descarga", "");
        String epID = sharedPreferences.getString("epIDS_descarga", "");
        sharedPreferences.edit().putString("titulos_descarga", tits.replace(titulo + ":::", "")).apply();
        sharedPreferences.edit().putString("epIDS_descarga", epID.replace(aid + "_" + numero + ":::", "")).apply();
        new SQLiteHelperDownloads(context).updateState(eid, DownloaderService.CANCELED).delete(eid).close();
        DownloadListManager.delete(context, eid + "_" + sharedPreferences.getLong(eid + "_downloadID", -1));
    }

    public int getProgress(String eid) {
        return new SQLiteHelperDownloads(context).getProgress(eid);
    }
}
