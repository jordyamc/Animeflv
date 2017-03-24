package knf.animeflv.Favorites;

import android.content.Context;

import knf.animeflv.Parser;

/**
 * Created by Jordy on 19/03/2017.
 */

public class FavoriteHelper {

    public static boolean isFav(Context context, String aid) {
        return new FavotiteDB(context).isFavorite(aid, true);
    }

    public static void setFav(Context context, String aid, boolean add, FavotiteDB.updateDataInterface dataInterface) {
        if (add) {
            new FavotiteDB(context).addFav(new FavObject(Parser.getTitleCached(aid), aid, FavotiteDB.NO_SECTION), dataInterface);
        } else {
            new FavotiteDB(context).deleteFav(aid, dataInterface);
        }
    }

}
