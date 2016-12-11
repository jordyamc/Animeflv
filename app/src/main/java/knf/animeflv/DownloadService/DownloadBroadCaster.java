package knf.animeflv.DownloadService;

import android.app.DownloadManager;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import xdroid.toaster.Toaster;

/**
 * Created by Jordy on 05/12/2016.
 */

public class DownloadBroadCaster extends BroadcastReceiver {
    public static final String ACTION_RETRY = "download.retry";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION_RETRY)) {
            Toaster.toast("Reintentando...");
            ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).cancel(intent.getIntExtra("not_id", 123));
            Intent n_intent = new Intent(context, DownloaderService.class);
            Bundle bundle = intent.getExtras();
            new SQLiteHelperDownloads(context)
                    .addElement(new DownloadObject(bundle.getLong("downloadID"), bundle.getString("url"), bundle.getString("eid")))
                    .updateState(bundle.getString("eid"), DownloadManager.STATUS_PENDING)
                    .close();
            n_intent.putExtras(bundle);
            context.startService(n_intent);
        }
    }
}
