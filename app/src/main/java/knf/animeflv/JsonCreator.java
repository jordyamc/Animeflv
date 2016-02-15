package knf.animeflv;

import android.support.annotation.NonNull;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Jordy on 02/02/2016.
 */
public class JsonCreator {

    public String url_inicio = "http://animeflv.net";

    public String getJSONinicio() {
        String json = null;
        List<JSONObject> janimes = new ArrayList<>();
        try {
            Log.d("JSON INICIO--->", "Start");
            Document html = Jsoup.connect("http://animeflvapp.pythonanywhere.com/v1/content?type=html&url=http://animeflv.net").ignoreHttpErrors(true).ignoreContentType(true).timeout(0).get();
            Elements animes = html.getElementsByClass("not");
            for (Element anime : animes) {
                String htmlFrag = anime.html();
                String tipo;
                Boolean OP = htmlFrag.contains("tpeli") || htmlFrag.contains("tova");
                if (!OP) {
                    tipo = "Anime";
                } else {
                    if (htmlFrag.contains("tpeli")) {
                        tipo = "Pelicula";
                    } else {
                        tipo = "OVA";
                    }
                }
                String link = anime.select("a").attr("href");
                String ABSTitulo = anime.select("a").attr("title");
                String IMGlink = anime.select("img").attr("src");
                String aid = IMGlink.substring(IMGlink.lastIndexOf("/") + 1, IMGlink.lastIndexOf("."));
                String titulo = ABSTitulo.substring(0, ABSTitulo.lastIndexOf(" "));
                String numero = ABSTitulo.substring(ABSTitulo.lastIndexOf(" ") + 1);
                JSONObject JSONanime = new JSONObject();
                JSONanime.put("aid", aid);
                JSONanime.put("titulo", titulo);
                JSONanime.put("numero", numero);
                JSONanime.put("tid", tipo);
                JSONanime.put("eid", aid + "_" + numero + "E");
                janimes.add(JSONanime);
            }
            JSONObject Jfinal = new JSONObject();
            Jfinal.put("cache", 0);
            JSONArray lista = new JSONArray(janimes);
            Jfinal.put("lista", lista);
            json = Jfinal.toString();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("JSON Inicio", "Error: " + e.getMessage());
        }
        return json;
    }
}
