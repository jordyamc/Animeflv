package knf.animeflv.DownloadManager;

/**
 * Created by Jordy on 04/03/2016.
 */
public enum DownloadType {
    INTERNAL(0),
    EXTERNAL(1),
    NULL(2);

    int type;

    DownloadType(int type) {
        this.type = type;
    }
}
