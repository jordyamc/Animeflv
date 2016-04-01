package knf.animeflv.DownloadManager;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.io.File;

import knf.animeflv.Application;
import knf.animeflv.DManager;
import knf.animeflv.Downloader;
import knf.animeflv.DownloaderCookie;
import knf.animeflv.Parser;
import knf.animeflv.Utils.FileUtil;

/**
 * Created by Jordy on 04/03/2016.
 */
public class ExternalManager {
    Context context;
    SharedPreferences sharedPreferences;
    String EXTERNA = "1";
    Parser parser = new Parser();

    public ExternalManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("data", Context.MODE_PRIVATE);
    }

    public void startDownload(String eid, String url) {
        Application application = (Application) context.getApplicationContext();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("Download");
        mTracker.send(new HitBuilders.EventBuilder("Download", "Download Ext " + eid).build());
        String aid = eid.replace("E", "").substring(0, eid.lastIndexOf("_"));
        String numero = eid.replace("E", "").substring(eid.lastIndexOf("_") + 1);
        String titulo = parser.getTitCached(aid);
        File f = new File(FileUtil.getSDPath() + "/Animeflv/download/" + aid, aid + "_" + numero + ".mp4");
        sharedPreferences.edit().putString(eid + "dtype", EXTERNA).apply();
        new Downloader(context, eid, aid, titulo, numero, f).execute(url);
    }

    public void startDownload(String eid, String url, CookieConstructor constructor) {
        Application application = (Application) context.getApplicationContext();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("Download");
        mTracker.send(new HitBuilders.EventBuilder("Download", "Download Ext Zippy" + eid).build());
        String aid = eid.replace("E", "").substring(0, eid.lastIndexOf("_"));
        String numero = eid.replace("E", "").substring(eid.lastIndexOf("_") + 1);
        String titulo = parser.getTitCached(aid);
        File f = new File(FileUtil.getSDPath() + "/Animeflv/download/" + aid, aid + "_" + numero + ".mp4");
        sharedPreferences.edit().putString(eid + "dtype", EXTERNA).apply();
        new DownloaderCookie(context, eid, aid, titulo, numero, f, constructor).execute(url);
    }

    public void cancelDownload(String eid) {
        Application application = (Application) context.getApplicationContext();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("Download");
        mTracker.send(new HitBuilders.EventBuilder("Download", "Cancel Ext " + eid).build());
        String aid = eid.replace("E", "").substring(0, eid.lastIndexOf("_"));
        String numero = eid.replace("E", "").substring(eid.lastIndexOf("_") + 1);
        String titulo = parser.getTitCached(aid);
        sharedPreferences.edit().putString(eid + "dtype", "2").apply();
        DManager.getManager().cancel(Integer.parseInt(sharedPreferences.getString(eid, "0")));
        String descargados = sharedPreferences.getString("eids_descarga", "");
        sharedPreferences.edit().putString("eids_descarga", descargados.replace(eid + ":::", "")).apply();
        String tits = sharedPreferences.getString("titulos_descarga", "");
        String epID = sharedPreferences.getString("epIDS_descarga", "");
        sharedPreferences.edit().putString("titulos_descarga", tits.replace(titulo + ":::", "")).apply();
        sharedPreferences.edit().putString("epIDS_descarga", epID.replace(aid + "_" + numero + ":::", "")).apply();
    }

    public int getProgress(String eid) {
        return Integer.parseInt(sharedPreferences.getString(eid + "prog", "0"));
    }

    public DownloadState getState(String eid) {
        DownloadState state;
        switch (sharedPreferences.getInt(eid + "status", 6)) {
            case 0:
                state = DownloadState.DOWNLOADING;
                break;
            case 1:
                state = DownloadState.SUCCESS;
                break;
            case 2:
                state = DownloadState.ERROR;
                break;
            case 3:
                state = DownloadState.CANCELED;
                break;
            case 4:
                state = DownloadState.PAUSED;
                break;
            case 5:
                state = DownloadState.INLIST;
                break;
            case 6:
                state = DownloadState.NULL;
                break;
            default:
                state = DownloadState.NULL;
                break;
        }
        return state;
    }

    public String getStateString(String eid) {
        String state;
        switch (sharedPreferences.getInt(eid + "status", 6)) {
            case 0:
                state = "DESCARGANDO";
                break;
            case 1:
                state = "COMPLETADO";
                break;
            case 2:
                state = "ERROR";
                break;
            case 3:
                state = "CANCELADO";
                break;
            case 4:
                state = "PAUSA";
                break;
            case 5:
                state = "EN LISTA";
                break;
            case 6:
                state = "SIN ESTADO";
                break;
            default:
                state = "SIN ESTADO";
                break;
        }
        return state;
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

    public String getError(String eid) {
        return sharedPreferences.getString(eid + "errmessage", "Sin Errores");
    }
}
