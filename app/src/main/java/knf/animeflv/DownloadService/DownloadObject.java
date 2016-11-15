package knf.animeflv.DownloadService;

/**
 * Created by Jordy on 11/11/2016.
 */

public class DownloadObject {
    public long id;
    public String url;
    public String eid;

    public DownloadObject(long id, String url, String eid) {
        this.id = id;
        this.url = url;
        this.eid = eid;
    }
}
