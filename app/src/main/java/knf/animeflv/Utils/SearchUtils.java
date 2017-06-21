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
                        String url = "http://animeflv.net/uploads/animes/covers/80x80/" + aid + ".jpg";
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
                            String url = "http://animeflv.net/uploads/animes/covers/80x80/" + aid + ".jpg";
                            linkArray.add(new AnimeClass(nombre, aid, tipo, url, i + 1));
                        }
                    }
                }
            }
            if (type == SearchType.GENEROS) {
                try {
                    if (SearchConstructor.getGeneros().contains(Genero.TODOS)) {
                        JSONObject jsonObj = new JSONObject(json);
                        JSONArray jsonArray = jsonObj.getJSONArray("lista");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            String tipo = object.getString("c");
                            String nombre = FileUtil.corregirTit(object.getString("b"));
                            String aid = object.getString("a");
                            String url = "http://animeflv.net/uploads/animes/covers/80x80/" + aid + ".jpg";
                            linkArray.add(new AnimeClass(nombre, aid, tipo, url, i + 1));
                        }
                    } else {
                        JSONObject jsonObj = new JSONObject(json);
                        JSONArray jsonArray = jsonObj.getJSONArray("lista");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            String generos = object.getString("f");
                            String nombre = FileUtil.corregirTit(object.getString("b"));
                            if (containsGenero(generos) && nombre.toLowerCase().contains(search)) {
                                String tipo = object.getString("c");
                                String aid = object.getString("a");
                                String url = "http://animeflv.net/uploads/animes/covers/80x80/" + aid + ".jpg";
                                linkArray.add(new AnimeClass(nombre, aid, tipo, url, i + 1));
                            }

                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    JSONObject jsonObj = new JSONObject(json);
                    JSONArray jsonArray = jsonObj.getJSONArray("lista");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        String tipo = object.getString("c");
                        String nombre = FileUtil.corregirTit(object.getString("b"));
                        String aid = object.getString("a");
                        String url = "http://animeflv.net/uploads/animes/covers/80x80/" + aid + ".jpg";
                        linkArray.add(new AnimeClass(nombre, aid, tipo, url, i + 1));
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
                                    String url = "http://animeflv.net/uploads/animes/covers/80x80/" + aid + ".jpg";
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

    private static boolean containsGenero(String generos) {
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
