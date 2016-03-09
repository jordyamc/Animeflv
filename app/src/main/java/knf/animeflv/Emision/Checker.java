package knf.animeflv.Emision;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Checker {
    Context context;
    SharedPreferences preferences;

    public Checker(Context context) {
        this.context = context;
    }

    public void Check(List<AnimeModel> list) {
        preferences = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        for (AnimeModel animeModel : list) {
            if (animeModel.getType() == AnimeType.ANIME && animeModel.isOngoing()) {
                Set<String> ongoing = preferences.getStringSet("ongoingSet", new HashSet<String>());
                if (!contains(ongoing, animeModel.getAid())) {
                    ongoing.add(animeModel.getAid());
                    preferences.edit().putStringSet("ongoingSet", ongoing).apply();
                }
                preferences.edit().putInt(animeModel.getAid() + "onday", animeModel.getDaycode()).apply();
                preferences.edit().putString(animeModel.getAid() + "onhour", animeModel.getHour()).apply();
            } else {
                if (animeModel.getType() == AnimeType.ANIME) {
                    if (!animeModel.isOngoing()) {
                        Set<String> ongoing = preferences.getStringSet("ongoingSet", new HashSet<String>());
                        if (contains(ongoing, animeModel.getAid())) {
                            ongoing.remove(animeModel.getAid());
                            preferences.edit().putStringSet("ongoingSet", ongoing).apply();
                            preferences.edit().putInt(animeModel.getAid() + "onday", 0);
                            preferences.edit().putString(animeModel.getAid() + "onhour", "null");
                        }
                    }
                }
            }
        }
    }

    private boolean contains(Set<String> set, String aid) {
        boolean exist = false;
        for (String in : set) {
            if (in.equals(aid)) exist = true;
        }
        return exist;
    }
}
