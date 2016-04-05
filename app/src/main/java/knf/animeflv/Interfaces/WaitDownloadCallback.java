package knf.animeflv.Interfaces;

import java.util.List;

/**
 * Created by Jordy on 01/04/2016.
 */
public interface WaitDownloadCallback {
    void onAllCapsDownload(String aid, List<Float> list);

    void onSingleCapDownload(String aid, float num);
}
