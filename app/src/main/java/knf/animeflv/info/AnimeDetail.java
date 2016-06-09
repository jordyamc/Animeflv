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
    private String estado = "null";
    private String generos = "null";

    public AnimeDetail(String json) {
        this.json = json;
        if (FileUtil.isJSONValid(json)) {
            try {
                JSONObject object = new JSONObject(json);
                aid = object.getString("aid");
                tid = object.getString("tid");
                titulo = FileUtil.corregirTit(object.getString("titulo"));
                sinopsis = Parser.ValidateSinopsis(object.getString("sinopsis"));
                String s = object.getString("fecha_fin").trim();
                if (s.equals("0000-00-00")) {
                    s = "En emision";
                } else if (s.equals("prox")) {
                    s = "Proximamente";
                } else {
                    s = "Terminado";
                }
                estado = s;
                generos = object.getString("generos");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String getJson() {
        return json;
    }

    public String getAid() {
        return aid;
    }

    public String getTid() {
        return tid;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getSinopsis() {
        return sinopsis;
    }

    public String getEstado() {
        return estado;
    }

    public String getGeneros() {
        return generos;
    }
}
