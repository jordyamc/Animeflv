package knf.animeflv.Directorio;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import knf.animeflv.Utils.eNums.Genero;
import knf.animeflv.Utils.eNums.SearchType;

/**
 * Created by Jordy on 29/03/2016.
 */
public class SearchConstructor {
    private static SearchType type;
    private static List<Genero> generos = new ArrayList<>();

    public static void SetSearch(SearchType typ, @Nullable Integer[] genero) {
        type = typ;
        if (genero == null) {
            generos = new ArrayList<>();
        } else {
            generos.clear();
            if (!Arrays.asList(genero).contains(0)) {
                for (int value : genero) {
                    if (value >= 0) {
                        generos.add(Genero.values()[value]);
                    }
                }
            } else {
                generos.add(Genero.TODOS);
            }
        }
    }

    public static SearchType getType() {
        return type;
    }

    public static List<Genero> getGeneros() {
        if (generos.isEmpty()) {
            generos.add(Genero.TODOS);
        }
        return generos;
    }

    public static Integer[] getGenerosInt() {
        if (generos.isEmpty()) {
            return new Integer[]{0};
        } else {
            List<Integer> tGen = new ArrayList<>();
            for (Genero genero : generos) {
                tGen.add(genero.getValue());
            }
            Integer[] array = new Integer[tGen.size()];
            tGen.toArray(array);
            return array;
        }
    }
}
