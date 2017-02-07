package knf.animeflv.DownloadManager;

import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.thin.downloadmanager.ThinDownloadManager;

import java.io.File;

import knf.animeflv.DManager;
import knf.animeflv.Parser;

/**
 * Created by Jordy on 04/03/2016.
 */
public class InternalManager {
    Context context;
    SharedPreferences sharedPreferences;
    String INTERNA = "0";
    Parser parser = new Parser();

    public InternalManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("data", Context.MODE_PRIVATE);
    }

    public static int getDownloadSate(Context context, String eid) {
        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Query q = new DownloadManager.Query();
        q.setFilterById(Long.parseLong(context.getSharedPreferences("data", Context.MODE_PRIVATE).getString(eid, "0")));
        Cursor cursor = manager.query(q);
        cursor.moveToFirst();
        try {
            return cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
        } catch (Exception e) {
            return -1;
        }
    }

    public static boolean isDownloading(Context context, String eid) {
        switch (getDownloadSate(context, eid)) {
            case DownloadManager.STATUS_RUNNING:
                return true;
            case DownloadManager.STATUS_SUCCESSFUL:
                return false;
            case DownloadManager.STATUS_PENDING:
                return true;
            case DownloadManager.STATUS_PAUSED:
                return true;
            default:
                return false;
        }
    }

    public void startDownload(String eid, String downUrl) {
        String aid = eid.replace("E", "").substring(0, eid.lastIndexOf("_"));
        String numero = eid.replace("E", "").substring(eid.lastIndexOf("_") + 1);
        String titulo = parser.getTitCached(aid);
        File Dstorage = new File(Environment.getExternalStorageDirectory() + "/Animeflv/download/" + aid);
        if (Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            if (!Dstorage.exists()) {
                if (!Dstorage.mkdirs())
                    Toast.makeText(context, "Error al crear carpeta", Toast.LENGTH_SHORT).show();
            }
        }
        sharedPreferences.edit().putString(eid + "dtype", INTERNA).apply();
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downUrl));
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setTitle(titulo);
        request.setDescription("Capítulo " + numero);
        request.setMimeType("video/mp4");
        request.setDestinationInExternalPublicDir("Animeflv/download/" + aid, aid + "_" + numero + ".mp4");
        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        long l = manager.enqueue(request);
        sharedPreferences.edit().putString(eid, Long.toString(l)).apply();
        String descargados = sharedPreferences.getString("eids_descarga", "");
        String epID = sharedPreferences.getString("epIDS_descarga", "");
        if (descargados.contains(eid)) {
            sharedPreferences.edit().putString("eids_descarga", descargados.replace(eid + ":::", "")).apply();
            sharedPreferences.edit().putString("epIDS_descarga", epID.replace(aid + "_" + numero + ":::", "")).apply();
        }
        descargados = sharedPreferences.getString("eids_descarga", "");
        sharedPreferences.edit().putString("eids_descarga", descargados + eid + ":::").apply();
        String tits = sharedPreferences.getString("titulos_descarga", "");
        epID = sharedPreferences.getString("epIDS_descarga", "");
        sharedPreferences.edit().putString("titulos_descarga", tits + aid + ":::").apply();
        sharedPreferences.edit().putString("epIDS_descarga", epID + aid + "_" + numero + ":::").apply();
    }

    public void startDownload(String eid, String downUrl, CookieConstructor constructor) {
        String aid = eid.replace("E", "").substring(0, eid.lastIndexOf("_"));
        String numero = eid.replace("E", "").substring(eid.lastIndexOf("_") + 1);
        String titulo = parser.getTitCached(aid);
        File Dstorage = new File(Environment.getExternalStorageDirectory() + "/Animeflv/download/" + aid);
        if (Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            if (!Dstorage.exists()) {
                if (!Dstorage.mkdirs())
                    Toast.makeText(context, "Error al crear carpeta", Toast.LENGTH_SHORT).show();
            }
        }
        sharedPreferences.edit().putString(eid + "dtype", INTERNA).apply();
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downUrl));
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setTitle(titulo);
        request.setDescription("Capítulo " + numero);
        request.setMimeType("video/mp4");
        request.addRequestHeader("cookie", constructor.getCookie());
        Log.e("Cookie", constructor.getCookie());
        if (constructor.getReferer() != null)
            request.addRequestHeader("Referer", constructor.getReferer());
        if (constructor.getReferer() != null)
            request.addRequestHeader("User-Agent", constructor.getUseAgent());
        request.addRequestHeader("Accept", "text/html, application/xhtml+xml, *" + "/" + "*");
        request.addRequestHeader("Accept-Language", "en-US,en;q=0.7,he;q=0.3");
        request.setDestinationInExternalPublicDir("Animeflv/download/" + aid, aid + "_" + numero + ".mp4");
        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        long l = manager.enqueue(request);
        sharedPreferences.edit().putString(eid, Long.toString(l)).apply();
        String descargados = sharedPreferences.getString("eids_descarga", "");
        String epID = sharedPreferences.getString("epIDS_descarga", "");
        if (descargados.contains(eid)) {
            sharedPreferences.edit().putString("eids_descarga", descargados.replace(eid + ":::", "")).apply();
            sharedPreferences.edit().putString("epIDS_descarga", epID.replace(aid + "_" + numero + ":::", "")).apply();
        }
        descargados = sharedPreferences.getString("eids_descarga", "");
        sharedPreferences.edit().putString("eids_descarga", descargados + eid + ":::").apply();
        String tits = sharedPreferences.getString("titulos_descarga", "");
        epID = sharedPreferences.getString("epIDS_descarga", "");
        sharedPreferences.edit().putString("titulos_descarga", tits + aid + ":::").apply();
        sharedPreferences.edit().putString("epIDS_descarga", epID + aid + "_" + numero + ":::").apply();
    }

    public void cancelDownload(String eid) {
        String aid = eid.replace("E", "").substring(0, eid.lastIndexOf("_"));
        String numero = eid.replace("E", "").substring(eid.lastIndexOf("_") + 1);
        String titulo = parser.getTitCached(aid);
        sharedPreferences.edit().putString(eid + "dtype", "2").apply();
        long l = Long.parseLong(sharedPreferences.getString(eid, "0"));
        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        manager.remove(l);
        ThinDownloadManager downloadManager = DManager.getManager();
        downloadManager.cancel((int) l);
        String descargados = sharedPreferences.getString("eids_descarga", "");
        sharedPreferences.edit().putString("eids_descarga", descargados.replace(eid + ":::", "")).apply();
        String tits = sharedPreferences.getString("titulos_descarga", "");
        String epID = sharedPreferences.getString("epIDS_descarga", "");
        sharedPreferences.edit().putString("titulos_descarga", tits.replace(titulo + ":::", "")).apply();
        sharedPreferences.edit().putString("epIDS_descarga", epID.replace(aid + "_" + numero + ":::", "")).apply();
    }

    public int getProgress(String eid) {
        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Query q = new DownloadManager.Query();
        q.setFilterById(Long.parseLong(sharedPreferences.getString(eid, "0")));
        Cursor cursor = manager.query(q);
        cursor.moveToFirst();
        int bytes_downloaded = cursor.getInt(cursor
                .getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
        int bytes_total = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
        return (int) ((bytes_downloaded * 100L) / bytes_total);
    }

    public DownloadState getState(String eid) {
        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Query q = new DownloadManager.Query();
        q.setFilterById(Long.parseLong(sharedPreferences.getString(eid, "0")));
        Cursor cursor = manager.query(q);
        cursor.moveToFirst();
        DownloadState state;
        switch (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
            case 2:
                state = DownloadState.DOWNLOADING;
                break;
            case 8:
                state = DownloadState.SUCCESS;
                break;
            case 16:
                state = DownloadState.ERROR;
                break;
            case 4:
                state = DownloadState.PAUSED;
                break;
            case 1:
                state = DownloadState.INLIST;
                break;
            default:
                state = DownloadState.NULL;
                break;
        }
        return state;
    }

    public String getStateString(String eid) {
        switch (getDownloadSate(context, eid)) {
            case 2:
                return "DESCARGANDO";
            case 8:
                return "COMPLETADO";
            case 16:
                return "ERROR";
            case 4:
                return "PAUSA";
            case 1:
                return "EN LISTA";
            default:
                return "SIN ESTADO";
        }
    }

    public boolean isDownloading(String eid) {
        return getState(eid) == DownloadState.DOWNLOADING;
    }

    public boolean isSuccess(String eid) {
        return getState(eid) == DownloadState.SUCCESS;
    }

    public boolean isError(String eid) {
        return getState(eid) == DownloadState.ERROR;
    }

    public boolean isCanceled(String eid) {
        return getState(eid) == DownloadState.CANCELED;
    }

    public String getDownloadTitle(String eid) {
        return parser.getTitCached(eid.substring(0, eid.lastIndexOf("_")));
    }
}
