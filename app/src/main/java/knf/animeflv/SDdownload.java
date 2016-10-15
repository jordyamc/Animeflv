package knf.animeflv;

import android.net.Uri;

import com.thin.downloadmanager.DownloadRequest;
import com.thin.downloadmanager.ThinDownloadManager;

/**
 * Created by Jordy on 06/10/2016.
 */

public class SDdownload {
    public void hola() {
        new ThinDownloadManager().add(new DownloadRequest(Uri.parse("")));
    }
}
