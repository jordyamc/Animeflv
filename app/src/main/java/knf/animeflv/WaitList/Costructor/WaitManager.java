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
    private static List<WaitDBHelper.WaitObject> animesList = new ArrayList<>();
    private static List<List<Integer>> numerosList = new ArrayList<>();

    public static void initial(Context c) {
        context = c;
    }

    public static void Refresh() {
        animesList = new WaitDBHelper(context).getAidsList();
        numerosList.clear();
        for (WaitDBHelper.WaitObject s : animesList) {
            numerosList.add(ChildListCreator.create(context, s.aid));
        }
    }

    public static List<WaitDBHelper.WaitObject> getAnimesList() {
        return animesList;
    }

    public static List<List<Integer>> getNumerosList() {
        return numerosList;
    }
}
