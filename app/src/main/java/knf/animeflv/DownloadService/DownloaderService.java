package knf.animeflv.DownloadService;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by Jordy on 26/07/2016.
 */

public class DownloaderService extends IntentService {
    public DownloaderService(String name) {
        super(name);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }
}
