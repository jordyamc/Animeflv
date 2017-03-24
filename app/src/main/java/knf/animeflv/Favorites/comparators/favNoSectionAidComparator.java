package knf.animeflv.Favorites.comparators;

import java.util.Comparator;

import knf.animeflv.Favorites.FavObject;

/**
 * Created by Jordy on 19/03/2017.
 */

public class favNoSectionAidComparator implements Comparator<FavObject> {

    private boolean inverse = false;

    public favNoSectionAidComparator(boolean inverse) {
        this.inverse = inverse;
    }

    @Override
    public int compare(FavObject o1, FavObject o2) {
        if (inverse) {
            return Integer.parseInt(o2.aid) - Integer.parseInt(o1.aid);
        } else {
            return Integer.parseInt(o1.aid) - Integer.parseInt(o2.aid);
        }
    }
}
