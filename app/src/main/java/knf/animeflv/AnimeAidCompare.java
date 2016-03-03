package knf.animeflv;

import java.util.Comparator;

import knf.animeflv.Directorio.AnimeClass;

/**
 * Created by Jordy on 15/02/2016.
 */
public class AnimeAidCompare implements Comparator<AnimeClass> {
    @Override
    public int compare(AnimeClass lhs, AnimeClass rhs) {
        return lhs.getAidInt() - rhs.getAidInt();
    }
}