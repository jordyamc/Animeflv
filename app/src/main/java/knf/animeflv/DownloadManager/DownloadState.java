package knf.animeflv.DownloadManager;

/**
 * Created by Jordy on 04/03/2016.
 */
public enum DownloadState {
    DOWNLOADING(0),
    SUCCESS(1),
    ERROR(2),
    CANCELED(3),
    PAUSED(4),
    INLIST(5),
    NULL(6);
    int value;

    DownloadState(int value) {
        this.value = value;
    }
}
