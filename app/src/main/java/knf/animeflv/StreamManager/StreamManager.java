package knf.animeflv.StreamManager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.File;

import knf.animeflv.DownloadManager.ExternalManager;
import knf.animeflv.FileMover;
import knf.animeflv.Parser;
import knf.animeflv.Utils.FileUtil;
import knf.animeflv.history.adapter.HistoryHelper;
import xdroid.toaster.Toaster;

public class StreamManager {
    public static InternalStream internal(Context context) {
        return new InternalStream(context);
    }

    public static ExternalStream external(Context context) {
        return new ExternalStream(context);
    }

    public static MXStream mx(Context context) {
        return new MXStream(context);
    }

    public static void Play(Activity context, String eid) {
        String[] data = eid.replace("E", "").split("_");
        String aid = data[0];
        String semi = eid.replace("E", "");
        String cap = data[1].replace("E", "");
        File sd = new File(FileUtil.init(context).getSDPath() + "/Animeflv/download/" + aid + "/" + semi + ".mp4");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && sd.exists() && getStreamType(context) == 1) {
            if (!ExternalManager.isDownloading(context, eid)) {
                FileMover.PrepareToPlay(context, eid);
            } else {
                Toaster.toast("Espera a que se complete la descarga para poder reproducir");
            }
        } else {
            HistoryHelper.addToList(context, aid, new Parser().getTitCached(aid), cap);
            File file = new File(Environment.getExternalStorageDirectory() + "/Animeflv/download/" + aid + "/" + semi + ".mp4");
            switch (getStreamType(context)) {
                case 0:
                    if (file.exists()) {
                        StreamManager.internal(context).Play(eid, file);
                    } else if (sd.exists()) {
                        StreamManager.internal(context).Play(eid, sd);
                    }
                    break;
                case 1:
                    if (file.exists()) {
                        StreamManager.external(context).Play(eid, file);
                    } else if (sd.exists()) {
                        StreamManager.external(context).Play(eid, sd);
                    }
                    break;

            }
        }
    }

    public static int getStreamType(Context context) {
        return Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString("t_video", "0"));
    }

    public static void Play(Activity context, String eid, File file) {
        switch (getStreamType(context)) {
            case 0:
                StreamManager.internal(context).Play(eid, file);
                break;
            case 1:
                StreamManager.external(context).Play(eid, file);
                break;

        }
    }

    public static void Stream(Activity context, String eid, String url) {
        String[] data = eid.replace("E", "").split("_");
        String aid = data[0];
        String cap = data[1].replace("E", "");
        HistoryHelper.addToList(context, aid, new Parser().getTitCached(aid), cap);
        int type = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString("t_streaming", "0"));
        Log.d("Streaming", PreferenceManager.getDefaultSharedPreferences(context).getString("t_streaming", "0"));
        switch (type) {
            case 0:
                StreamManager.internal(context).Stream(eid, url);
                break;
            case 1:
                StreamingExtbyURL(context, eid, url);
                break;
        }
    }

    private static void StreamingExtbyURL(Activity context, String eid, String url) {
        Intent i = (new Intent(Intent.ACTION_VIEW, Uri.parse(url)).setDataAndType(Uri.parse(url), "video/mp4"));
        if (getDefaultId(context, i).startsWith("com.mxtech.videoplayer")) {
            StreamManager.mx(context).Stream(eid, url);
        } else {
            StreamManager.external(context).Stream(eid, url);

        }
    }

    private static String getDefaultId(Activity context, Intent i) {
        try {
            PackageManager pm = context.getPackageManager();
            final ResolveInfo mInfo = pm.resolveActivity(i, 0);
            return mInfo.activityInfo.applicationInfo.processName;
        } catch (Exception e) {
            return "null";
        }
    }
}
