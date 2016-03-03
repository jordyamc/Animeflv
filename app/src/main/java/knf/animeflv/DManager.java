package knf.animeflv;

import android.util.Log;

import com.thin.downloadmanager.ThinDownloadManager;

/**
 * Created by Jordy on 01/03/2016.
 */
public class DManager {
    private static DManager manager = new DManager();
    private static ThinDownloadManager downloadManager = new ThinDownloadManager();

    private DManager() {
    }

    public void init() {
        manager = new DManager();
        downloadManager = new ThinDownloadManager();
    }

    public static DManager getInstance() {
        if (manager != null) {
            return manager;
        } else {
            manager = new DManager();
            return manager;
        }
    }

    public static ThinDownloadManager getManager() {
        if (downloadManager != null) {
            Log.d("Manager", "Existente");
            return downloadManager;
        } else {
            Log.d("Manager", "Nuevo");
            downloadManager = new ThinDownloadManager();
            return downloadManager;
        }
    }
}
