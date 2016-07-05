package knf.animeflv;

import android.app.Activity;
import android.preference.PreferenceManager;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import knf.animeflv.Directorio.AnimeClass;

/**
 * Created by Jordy on 15/02/2016.
 */
public class AnimeSorter {
    public static List<AnimeClass> sort(Activity activity,List<AnimeClass> list) {
        int type= Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(activity).getString("ord_busqueda","0"));
        switch (type){
            case 0:
                Collections.sort(list,new ByName());
                break;
            case 1:
                break;
            case 2:
                Collections.reverse(list);
                break;
            default:
                Collections.sort(list,new ByName());
                break;
        }
        return list;
    }

    public static List<AnimeClass> sortByName(List<AnimeClass> list) {
        Collections.sort(list,new ByName());
        return list;
    }

    private static class ByName implements Comparator<AnimeClass> {
        @Override
        public int compare(AnimeClass lhs, AnimeClass rhs) {
            return lhs.getNombre().compareToIgnoreCase(rhs.getNombre());
        }
    }
}