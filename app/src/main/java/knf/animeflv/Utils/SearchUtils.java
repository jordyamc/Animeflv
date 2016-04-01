package knf.animeflv.Utils;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import knf.animeflv.Directorio.AnimeClass;
import knf.animeflv.Directorio.SearchConstructor;
import knf.animeflv.Utils.eNums.Genero;
import knf.animeflv.Utils.eNums.SearchType;

/**
 * Created by Jordy on 29/03/2016.
 */
public class SearchUtils {
    public static List<AnimeClass> Search(String json, String s) {
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
                    JSONObject jsonObj = new JSONObject(json);
                    JSONArray jsonArray = jsonObj.getJSONArray("lista");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        String tipo = object.getString("c");
                        String nombre = FileUtil.corregirTit(object.getString("b"));
                        String aid = object.getString("a");
                        String url = "http://cdn.animeflv.net/img/portada/thumb_80/" + aid + ".jpg";
                        linkArray.add(new AnimeClass(nombre, aid, tipo, url, i + 1));

                    }
                } else {
                    JSONObject jsonObj = new JSONObject(json);
                    JSONArray jsonArray = jsonObj.getJSONArray("lista");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        String nombre = FileUtil.corregirTit(object.getString("b"));
                        String aid = object.getString("a");
                        if (nombre.toLowerCase().contains(search.toLowerCase())) {
                            String tipo = object.getString("c");
                            String url = "http://cdn.animeflv.net/img/portada/thumb_80/" + aid + ".jpg";
                            linkArray.add(new AnimeClass(nombre, aid, tipo, url, i + 1));
                        }
                    }
                }
            }
            if (type == SearchType.GENEROS) {
                if (SearchConstructor.getGeneros().contains(Genero.TODOS)) {
                    JSONObject jsonObj = new JSONObject(json);
                    JSONArray jsonArray = jsonObj.getJSONArray("lista");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        String tipo = object.getString("c");
                        String nombre = FileUtil.corregirTit(object.getString("b"));
                        String aid = object.getString("a");
                        String url = "http://cdn.animeflv.net/img/portada/thumb_80/" + aid + ".jpg";
                        linkArray.add(new AnimeClass(nombre, aid, tipo, url, i + 1));
                    }
                } else {
                    JSONObject jsonObj = new JSONObject(json);
                    JSONArray jsonArray = jsonObj.getJSONArray("lista");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        String generos = object.getString("e");
                        String nombre = FileUtil.corregirTit(object.getString("b"));
                        if (containsGenero(generos) && nombre.toLowerCase().contains(search)) {
                            String tipo = object.getString("c");
                            String aid = object.getString("a");
                            String url = "http://cdn.animeflv.net/img/portada/thumb_80/" + aid + ".jpg";
                            linkArray.add(new AnimeClass(nombre, aid, tipo, url, i + 1));
                        }

                    }
                }
            }
            if (type == SearchType.ID) {
                if (search == null) {
                    linkArray.add(new AnimeClass("_aid_", "_aid_", "_aid_", "_aid_", 0));
                } else {
                    if (!search.trim().equals("")) {
                        JSONObject jsonObj = new JSONObject(json);
                        JSONArray jsonArray = jsonObj.getJSONArray("lista");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            if (FileUtil.isNumber(search.trim())) {
                                String nombre = FileUtil.corregirTit(object.getString("b"));
                                String aid = object.getString("a");
                                if (aid.equals(search.trim())) {
                                    String tipo = object.getString("c");
                                    String url = "http://cdn.animeflv.net/img/portada/thumb_80/" + aid + ".jpg";
                                    linkArray.add(new AnimeClass(nombre, aid, tipo, url, i + 1));
                                }
                            } else {
                                linkArray.add(new AnimeClass(search.replace("aid:", "").trim(), "_NoNum_", "_NoNum_", "_NoNum_", 0));
                                break;
                            }
                        }
                    } else {
                        linkArray.add(new AnimeClass("_aid_", "_aid_", "_aid_", "_aid_", 0));
                    }
                }
            }
            if (linkArray.isEmpty()) {
                linkArray.add(new AnimeClass("none", "none", "none", "none", 0));
            }
        } catch (Exception e) {
            Log.e("DirAnimes", e.getMessage());
        }
        return linkArray;
    }

    private static String[] getGeneros() {
        String[] generos = {
                "Todos",
                "Accion",
                "Aventura",
                "Carreras",
                "Comedia",
                "Cyberpunk",
                "Deportes",
                "Drama",
                "Ecchi",
                "Escolares",
                "Fantasia",
                "Ciencia-Ficcion",
                "Gore",
                "Harem",
                "Horror",
                "Josei",
                "Lucha",
                "Magia",
                "Mecha",
                "Militar",
                "Misterio",
                "Musica",
                "Parodias",
                "Psicologico",
                "Recuentos-de-la-vida",
                "Romance",
                "Seinen",
                "Shojo",
                "Shonen",
                "Sin-Generos",
                "Sobrenatural",
                "Vampiros",
                "Yaoi",
                "Yuri"};
        return generos;
    }

    private static boolean containsGenero(String generos) {
        List<String> gen = Arrays.asList(generos.split(","));
        List<String> lower = new ArrayList<>();
        for (String g : gen) {
            lower.add(g.toLowerCase().trim());
        }
        List<Boolean> responses = new ArrayList<>();
        for (Genero genero : SearchConstructor.getGeneros()) {
            if (!lower.contains(getGeneros()[genero.getValue()].toLowerCase())) {
                responses.add(false);
                break;
            }
        }
        return responses.isEmpty();
    }
}
