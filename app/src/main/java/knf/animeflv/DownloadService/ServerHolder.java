package knf.animeflv.DownloadService;

import android.app.Activity;
import android.content.Context;

import java.io.File;

import knf.animeflv.PlayBack.CastPlayBackManager;
import knf.animeflv.Utils.NetworkUtils;
import xdroid.toaster.Toaster;

/**
 * Created by Jordy on 22/08/2017.
 */

public class ServerHolder {
    private static final ServerHolder ourInstance = new ServerHolder();
    private WebServer server;

    private ServerHolder() {
    }

    public static ServerHolder getInstance() {
        return ourInstance;
    }

    public void startServer(Context context, File file, String eid) {
        try {
            if (server != null && server.isAlive())
                server.stop();
            server = new WebServer(context, file);
            server.start();
            CastPlayBackManager.get((Activity) context).play(getIpWport(context, 8880, true), eid);
        } catch (Exception e) {
            e.printStackTrace();
            Toaster.toast("Error al iniciar webserver");
        }
    }

    public void stopServer(Context context) {
        if (server != null)
            server.stop();
        CastPlayBackManager.get((Activity) context).stop();
    }

    private String getIpWport(Context context, int port, boolean formatted) {
        return (formatted ? "http://" : "") + NetworkUtils.getIPAddress(context) + ":" + port;
    }
}
