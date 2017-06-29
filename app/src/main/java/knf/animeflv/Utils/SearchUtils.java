package knf.animeflv.Utils;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import knf.animeflv.Directorio.AnimeClass;
import knf.animeflv.Directorio.DB.DirectoryHelper;
import knf.animeflv.Directorio.SearchConstructor;
import knf.animeflv.Utils.eNums.Genero;
import knf.animeflv.Utils.eNums.SearchType;

/**
 * Created by Jordy on 29/03/2016.
 */
public class SearchUtils {
    public static List<AnimeClass> Search(Context context, String s) {
        String search;
        if (s != null) {
            search = s.toLowerCase();
        } else {
            search = s;
        }
        List<AnimeClass> linkArray = new ArrayList<AnimeClass>();
        try {
            SearchType type = SearchConstructor.getType();
            if (type == SearchType.NOMBRE) {
                if (search == null) {
                    linkArray = DirectoryHelper.get(context).getAll();
                } else {
                    linkArray = DirectoryHelper.get(context).searchName(search);
                }
            }
            if (type == SearchType.GENEROS) {
                try {
                    if (SearchConstructor.getGeneros().contains(Genero.TODOS)) {
                        linkArray = DirectoryHelper.get(context).getAll();
                    } else {
                        linkArray = DirectoryHelper.get(context).searchGenres(search);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    linkArray = DirectoryHelper.get(context).getAll();
                }
            }
            if (type == SearchType.ID) {
                if (search == null) {
                    linkArray.add(new AnimeClass("_aid_", "_aid_", "_aid_"));
                } else {
                    if (!search.trim().equals("")) {
                        linkArray = DirectoryHelper.get(context).searchID(search);
                    } else {
                        linkArray.add(new AnimeClass("_aid_", "_aid_", "_aid_"));
                    }
                }
            }
            if (linkArray.isEmpty()) {
                linkArray.add(new AnimeClass("none", "none", "none"));
            }
        } catch (Exception e) {
            Log.e("DirAnimes", e.getMessage());
        }
        return linkArray;
    }

    public static String[] getGeneros() {
        ArrayList<String> arrayList = new ArrayList<>(Arrays.asList(new String[]{"Todos"}));
        arrayList.addAll(Arrays.asList(getGenerosOnly()));
        return arrayList.toArray(new String[0]);
    }

    public static String[] getGenerosOnly() {
        return new String[]{
                "Acción",
                "Artes Marciales",
                "Aventuras",
                "Carreras",
                "Comedia",
                "Demencia",
                "Demonios",
                "Deportes",
                "Drama",
                "Ecchi",
                "Escolares",
                "Espacial",
                "Fantasía",
                "Ciencia Ficción",
                "Harem",
                "Historico",
                "Infantil",
                "Josei",
                "Juegos",
                "Magia",
                "Mecha",
                "Militar",
                "Misterio",
                "Musica",
                "Parodia",
                "Policía",
                "Psicológico",
                "Recuentos de la vida",
                "Romance",
                "Samurai",
                "Seinen",
                "Shoujo",
                "Shounen",
                "Sin Generos",
                "Sobrenatural",
                "Superpoderes",
                "Suspenso",
                "Terror",
                "Vampiros",
                "Yaoi",
                "Yuri"};
    }

    public static boolean containsGenero(String generos) {
        List<String> gen = Arrays.asList(generos.split(","));
        List<String> lower = new ArrayList<>();
        for (String g : gen) {
            lower.add(g.toLowerCase().trim());
        }
        if (generos.trim().equals(""))
            lower.add("Sin Generos");
        for (Genero genero : SearchConstructor.getGeneros()) {
            if (!lower.contains(getGeneros()[genero.getValue()].toLowerCase())) {
                return false;
            }
        }
        return true;
    }
}
