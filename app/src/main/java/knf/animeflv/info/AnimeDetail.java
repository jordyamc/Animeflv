package knf.animeflv.info;

import org.json.JSONObject;

import knf.animeflv.Parser;
import knf.animeflv.Utils.FileUtil;

/**
 * Created by Jordy on 08/06/2016.
 */

public class AnimeDetail {
    private String json;
    private String aid = "null";
    private String tid = "null";
    private String titulo = "null";
    private String sinopsis = "null";
    private String fsalida = "null";
    private String estado = "null";
    private String generos = "null";
    private float rate = 0;
    private String rate_count = "0";

    public AnimeDetail(String json) {
        this.json = json;
        if (FileUtil.isJSONValid(json)) {
            try {
                JSONObject object = new JSONObject(json);
                aid = object.getString("aid");
                tid = object.getString("tid");
                titulo = FileUtil.corregirTit(object.getString("titulo"));
                sinopsis = Parser.ValidateSinopsis(object.getString("sinopsis"));
                String fs = object.getString("fecha_inicio").trim();
                fsalida = fs.equals("") ? "Sin Fecha" : fs;
                estado = getState(object.getString("fecha_fin").trim());
                generos = object.getString("generos");
                rate = Float.parseFloat(object.getString("rating_value"));
                rate_count = object.getString("rating_count");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String getTid(String t) {
        switch (t) {
            default:
            case "Type tv":
                return "Anime";
            case "Type ova":
                return "OVA";
            case "Type movie":
                return "Pelicula";

        }
    }

    private String getState(String state) {
        switch (state) {
            case "0000-00-00":
                return "En emision";
            case "prox":
                return "Proximamente";
            default:
                return "Terminado";
        }
    }

    public String getJson() {
        return json;
    }

    public String getAid() {
        return aid;
    }

    public String getTid() {
        return getTid(tid);
    }

    public String getTitulo() {
        return titulo;
    }

    public String getSinopsis() {
        return sinopsis;
    }

    public String getFsalida() {
        return fsalida;
    }

    public String getEstado() {
        return estado;
    }

    public float getRate() {
        return rate;
    }

    public String getRate_count() {
        return rate_count;
    }

    public String getGeneros() {
        if (generos.trim().equals(""))
            return "Sin Generos";
        return generos;
    }
}
