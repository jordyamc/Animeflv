package knf.animeflv.Favorites.comparators;

import java.util.Comparator;

import knf.animeflv.Favorites.FavObject;

/**
 * Created by Jordy on 19/03/2017.
 */

public class favNoSectionNameComparator implements Comparator<FavObject> {

    @Override
    public int compare(FavObject o1, FavObject o2) {
        return o1.name.compareToIgnoreCase(o2.name);
    }
}
