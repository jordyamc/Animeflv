package knf.animeflv.DownloadService;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import fi.iki.elonen.NanoHTTPD;
import knf.animeflv.Utils.NetworkUtils;

/**
 * Created by Jordy on 15/08/2017.
 */

public class WebServer extends NanoHTTPD {

    private Context context;
    private File l_file;

    public WebServer() throws Exception {
        super(8880);
        Log.e("WebServer", "http://localhost:8880");
    }

    public WebServer(Context context, File file) throws IOException {
        super(8880);
        this.context = context;
        this.l_file = file;
        Log.e("WebServer", "http://" + NetworkUtils.getIPAddress(context) + ":8880");
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
            if (l_file == null) {
                throw new FileNotFoundException("Error al pasar archivo");
            }
            String range = null;
            for (String key : session.getHeaders().keySet()) {
                if ("range".equals(key)) {
                    range = session.getHeaders().get(key);
                }
            }
            if (range == null) {
                return getFullResponse(l_file);
            } else {
                return getPartialResponse(l_file, range);
            }
        } catch (Exception e) {
            return newFixedLengthResponse("Error al cargar video: \n\n" + Log.getStackTraceString(e));
        }
    }

    private Response getFullResponse(File file) throws FileNotFoundException {
        return newFixedLengthResponse(Response.Status.OK, "video/mp4", new FileInputStream(file), file.length());
    }

    private Response getPartialResponse(File file, String rangeHeader) throws IOException {
        String rangeValue = rangeHeader.trim().substring("bytes=".length());
        long fileLength = file.length();
        long start, end;
        if (rangeValue.startsWith("-")) {
            end = fileLength - 1;
            start = fileLength - 1
                    - Long.parseLong(rangeValue.substring("-".length()));
        } else {
            String[] range = rangeValue.split("-");
            start = Long.parseLong(range[0]);
            end = range.length > 1 ? Long.parseLong(range[1])
                    : fileLength - 1;
        }
        if (end > fileLength - 1) {
            end = fileLength - 1;
        }
        if (start <= end) {
            long contentLength = end - start + 1;
            FileInputStream fileInputStream = new FileInputStream(file);
            fileInputStream.skip(start);
            Response response = newFixedLengthResponse(Response.Status.PARTIAL_CONTENT, "video/mp4", fileInputStream, contentLength);
            response.addHeader("Content-Range", "bytes " + start + "-" + end + "/" + fileLength);
            response.addHeader("Content-Type", "video/mp4");
            return response;
        } else {
            return newFixedLengthResponse(Response.Status.RANGE_NOT_SATISFIABLE, MIME_HTML, rangeHeader);
        }
    }


}
