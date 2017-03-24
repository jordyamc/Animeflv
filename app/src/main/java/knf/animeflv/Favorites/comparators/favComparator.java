package knf.animeflv.Favorites.comparators;

import java.util.Comparator;

import knf.animeflv.Favorites.FavObject;

/**
 * Created by Jordy on 19/03/2017.
 */

public class favComparator implements Comparator<FavObject> {

    @Override
    public int compare(FavObject o1, FavObject o2) {
        return o1.order - o2.order;
    }
}
