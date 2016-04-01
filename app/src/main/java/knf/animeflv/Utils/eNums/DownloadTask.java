package knf.animeflv.Utils.eNums;

/**
 * Created by Jordy on 28/03/2016.
 */
public enum DownloadTask {
    DESCARGA(0),
    STREAMING(1),
    NULL(2);
    int value;

    DownloadTask(int value) {
        this.value = value;
    }
}
