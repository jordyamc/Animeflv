package knf.animeflv.DownloadManager;

import android.content.Context;
import android.preference.PreferenceManager;

import knf.animeflv.Utils.NetworkUtils;
import xdroid.toaster.Toaster;

/**
 * Created by Jordy on 04/03/2016.
 */
public class ManageDownload {
    public static ExternalManager external(Context context) {
        return new ExternalManager(context);
    }

    public static InternalManager internal(Context context) {
        return new InternalManager(context);
    }

    public static DownloadType getType(Context context, String eid) {
        DownloadType state;
        int type = Integer.parseInt(context.getSharedPreferences("data", Context.MODE_PRIVATE).getString(eid + "dtype", "2"));
        switch (type) {
            case 0:
                state = DownloadType.INTERNAL;
                break;
            case 1:
                state = DownloadType.EXTERNAL;
                break;
            case 2:
                state = DownloadType.NULL;
                break;
            default:
                state = DownloadType.NULL;
                break;
        }
        return state;
    }

    public static void cancel(Context context, String eid) {
        DownloadType type = getType(context, eid);
        if (type == DownloadType.INTERNAL) {
            new InternalManager(context).cancelDownload(eid);
        }
        if (type == DownloadType.EXTERNAL) {
            new ExternalManager(context).cancelDownload(eid);
        }
    }

    public static int getProgress(Context context, String eid) {
        DownloadType type = getType(context, eid);
        int prog = 0;
        if (type == DownloadType.INTERNAL) {
            prog = new InternalManager(context).getProgress(eid);
        }
        if (type == DownloadType.EXTERNAL) {
            prog = new ExternalManager(context).getProgress(eid);
        }
        return prog;
    }

    private static void DescargarSD(Context context, String eid, String downUrl) {
        if (NetworkUtils.isNetworkAvailable()) {
            ManageDownload.external(context).startDownload(eid, downUrl);
        } else {
            Toaster.toast("No hay conexion a internet");
        }
    }

    private static void DescargarSD(Context context, String eid, String downUrl, CookieConstructor constructor) {
        if (NetworkUtils.isNetworkAvailable()) {
            ManageDownload.external(context).startDownload(eid, downUrl, constructor);
        } else {
            Toaster.toast("No hay conexion a internet");
        }
    }

    private static void DescargarInbyURL(Context context, String eid, String downUrl) {
        if (NetworkUtils.isNetworkAvailable()) {
            ManageDownload.internal(context).startDownload(eid, downUrl);
        } else {
            Toaster.toast("No hay conexion a internet");
        }
    }

    private static void DescargarInbyURL(Context context, String eid, String downUrl, CookieConstructor constructor) {
        if (NetworkUtils.isNetworkAvailable()) {
            ManageDownload.internal(context).startDownload(eid, downUrl, constructor);
        } else {
            Toaster.toast("No hay conexion a internet");
        }
    }

    public static void chooseDownDir(Context context, String eid, String url) {
        Boolean inSD = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("sd_down", false);
        if (inSD) {
            DescargarSD(context, eid, url);
        } else {
            DescargarInbyURL(context, eid, url);
        }
    }

    public static void chooseDownDir(Context context, String eid, String url, CookieConstructor constructor) {
        Boolean inSD = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("sd_down", false);
        if (inSD) {
            DescargarSD(context, eid, url, constructor);
        } else {
            DescargarInbyURL(context, eid, url, constructor);
        }
    }
}
