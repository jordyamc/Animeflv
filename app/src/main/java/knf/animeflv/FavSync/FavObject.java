package knf.animeflv.FavSync;

/**
 * Created by Jordy on 19/07/2017.
 */

class FavObject {
    public String title;
    public String aid;
    public String section;
    public int order;

    public FavObject(String title, String aid, String section, int order) {
        this.title = title;
        this.aid = aid;
        this.section = section;
        this.order = order;
    }
}
