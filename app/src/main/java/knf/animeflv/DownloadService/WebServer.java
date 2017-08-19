package knf.animeflv.DownloadService;

import android.content.Context;
import android.support.v4.provider.DocumentFile;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import fi.iki.elonen.NanoHTTPD;
import knf.animeflv.Utils.FileUtil;
import knf.animeflv.Utils.NetworkUtils;

/**
 * Created by Jordy on 15/08/2017.
 */

public class WebServer extends NanoHTTPD {

    private Context context;
    private File l_file;
    private DocumentFile sd_file;

    public WebServer() throws Exception {
        super(8880);
        Log.e("WebServer", "http://localhost:8880");
    }

    public WebServer(Context context, DocumentFile file) throws IOException {
        super(8880);
        this.context = context;
        this.sd_file = file;
        Log.e("WebServer", "http://" + NetworkUtils.getIPAddress(context) + ":8880");
    }

    public WebServer(Context context, File file) throws IOException {
        super(8880);
        this.context = context;
        this.l_file = file;
        Log.e("WebServer", "http://" + NetworkUtils.getIPAddress(context) + ":8880");
    }

    public static void open(Context context, DocumentFile file) {
        try {
            new WebServer(context, file).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void open(Context context, File file) {
        try {
            new WebServer(context, file).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Response serve(IHTTPSession session) {
        try {
            InputStream stream;
            long length = -1;
            if (l_file != null) {
                stream = new FileInputStream(l_file);
                length = l_file.length();
            } else if (sd_file != null) {
                stream = FileUtil.getInputStreamFromAccess(context, sd_file);
                length = sd_file.length();
            } else {
                throw new FileNotFoundException("Error al pasar archivo");
            }
            return newFixedLengthResponse(Response.Status.OK, "video/mp4", stream, length);
        } catch (Exception e) {
            return newFixedLengthResponse("Error al cargar video: \n\n" + Log.getStackTraceString(e));
        }
    }


}
