package knf.animeflv.WebServer;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.util.Locale;

import fi.iki.elonen.NanoHTTPD;
import knf.animeflv.Utils.FileUtil;

/**
 * Created by Jordy on 14/12/2016.
 */

public class ServerManager extends NanoHTTPD {

    private static ServerManager manager;
    private File file;
    private Context context;

    public ServerManager(int port) {
        super(port);
    }

    public ServerManager(String hostname, int port) {
        super(hostname, port);
    }

    public static ServerManager get() {
        if (manager == null)
            manager = new ServerManager(6991);
        return manager;
    }

    @Nullable
    public String startStream(Context context, File file) {
        this.file = file;
        this.context = context;
        if (manager.isAlive()) {
            manager.stop();
        }
        try {
            Log.e("Server Stream URL", file.getAbsolutePath() + " in:\n" + getIp());
            manager.start();
            return getIp();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Response serve(IHTTPSession session) {
        try {
            if (context != null && file != null) {
                if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
                    return newFixedLengthResponse(getStatus(), "video/mp4", new FileInputStream(file), file.length());
                } else {
                    return newFixedLengthResponse(getStatus(), "video/mp4", FileUtil.getInputStreamFromAccess(context, file), file.length());
                }
            } else {
                return newFixedLengthResponse("No File Selected");
            }
        } catch (Exception e) {
            return newFixedLengthResponse("<br>Error getting file<br>" + e.toString());
        }
    }

    private Response.IStatus getStatus() {
        return new Response.IStatus() {
            @Override
            public String getDescription() {
                return file.getAbsolutePath();
            }

            @Override
            public int getRequestStatus() {
                return 200;
            }
        };
    }

    private String getIp() {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        int ipAddress = wifiManager.getConnectionInfo().getIpAddress();
        final String formatedIpAddress = String.format(Locale.getDefault(), "%d.%d.%d.%d", (ipAddress & 0xff), (ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
        return "http://" + formatedIpAddress + ":6991";
    }
}