package knf.animeflv.WaitList.Costructor;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import knf.animeflv.WaitList.WaitDBHelper;

/**
 * Created by Jordy on 31/03/2016.
 */
public class WaitManager {
    private static Context context;
    private static List<String> animesList = new ArrayList<>();
    private static List<List<Integer>> numerosList = new ArrayList<>();

    public static void initial(Context c) {
        context = c;
    }

    public static void Refresh() {
        animesList = new WaitDBHelper(context).getAidsList();
        numerosList.clear();
        for (String s : animesList) {
            numerosList.add(ChildListCreator.create(context, s));
        }
    }

    public static List<String> getAnimesList() {
        return animesList;
    }

    public static List<List<Integer>> getNumerosList() {
        return numerosList;
    }
}
