package knf.animeflv.Favorites;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jordy on 19/03/2017.
 */

public class FavObject {
    public String name;
    public String aid;
    public String section;
    public int id;
    public int order;
    public List<FavObject> sectionList = new ArrayList<>();
    public boolean isSection = false;

    public FavObject(String name, String aid, String section, int order) {
        this.name = name;
        this.aid = aid;
        this.section = section;
        this.order = order;
        this.id = Math.abs(name.hashCode());
        this.isSection = false;
    }

    public FavObject(String name, String aid, String section) {
        this.name = name;
        this.aid = aid;
        this.section = section;
        this.order = -1;
        this.id = Math.abs(name.hashCode());
        this.isSection = false;
    }

    public FavObject(String name) {
        this.name = name;
        this.id = Math.abs(name.hashCode());
        this.isSection = true;
    }
}
