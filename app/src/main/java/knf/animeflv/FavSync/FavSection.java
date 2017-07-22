package knf.animeflv.FavSync;

import java.util.List;

/**
 * Created by Jordy on 19/07/2017.
 */

public class FavSection {
    public String name;
    public List<FavObject> list;

    public FavSection(String name, List<FavObject> list) {
        this.name = name;
        this.list = list;
    }
}
