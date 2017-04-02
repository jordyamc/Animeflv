package knf.animeflv.Favorites;

import android.content.Context;
import android.preference.PreferenceManager;

import knf.animeflv.Parser;

/**
 * Created by Jordy on 19/03/2017.
 */

public class FavoriteHelper {

    private static final String KEY_DEF_SECTION = "def_section_name";

    public static boolean isFav(Context context, String aid) {
        return new FavotiteDB(context).isFavorite(aid, true);
    }

    public static void setFav(Context context, String aid, boolean add, FavotiteDB.updateDataInterface dataInterface) {
        if (add) {
            new FavotiteDB(context).addFav(new FavObject(Parser.getTitleCached(aid), aid, getDefaultSectionName(context)), dataInterface);
        } else {
            new FavotiteDB(context).deleteFav(aid, dataInterface);
        }
    }

    static String getDefaultSectionName(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(KEY_DEF_SECTION, FavotiteDB.NO_SECTION);
    }

    static void setDefaultSection(Context context, String name) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(KEY_DEF_SECTION, name).apply();
    }

}
