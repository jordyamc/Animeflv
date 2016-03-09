package knf.animeflv.DownloadManager;

import android.content.Context;

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
}
