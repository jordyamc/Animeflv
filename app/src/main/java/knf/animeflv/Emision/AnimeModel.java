package knf.animeflv.Emision;

import org.json.JSONObject;

import knf.animeflv.Parser;

/**
 * Created by Jordy on 05/03/2016.
 */
public class AnimeModel {
    private String aid;
    private String tipo;
    private String titulo;
    private String sinopsis;
    private String fecha_fin;
    private String generos;
    private String hour;
    private int daycode;

    public AnimeModel(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            aid = jsonObject.getString("aid");
            tipo = jsonObject.getString("tid");
            titulo = new Parser().corregirTit(jsonObject.getString("titulo"));
            sinopsis = jsonObject.getString("sinopsis");
            fecha_fin = jsonObject.getString("fecha_fin");
            generos = jsonObject.getString("generos");
        } catch (Exception e) {
            e.printStackTrace();
            tipo = titulo = sinopsis = fecha_fin = generos = "null";
        }
    }

    public AnimeModel(String eid, String tipo, String fecha_fin, String hour, int daycode) {
        this.aid = eid.replace("E", "").substring(0, eid.lastIndexOf("_"));
        this.tipo = tipo;
        this.fecha_fin = fecha_fin;
        this.hour = hour;
        this.daycode = daycode;
    }

    public String getHour() {
        return hour;
    }

    public int getDaycode() {
        return daycode;
    }

    public String getAid() {
        return aid;
    }

    public String getTipo() {
        return tipo;
    }

    public String getSinopsis() {
        return sinopsis;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getFecha_fin() {
        return fecha_fin;
    }

    public String getGeneros() {
        return generos;
    }

    public AnimeType getType() {
        AnimeType animeType;
        switch (tipo.toLowerCase()) {
            case "anime":
                animeType = AnimeType.ANIME;
                break;
            case "ova":
                animeType = AnimeType.OVA;
                break;
            case "pelicula":
                animeType = AnimeType.PELICULA;
                break;
            default:
                animeType = AnimeType.NULL;
                break;
        }
        return animeType;
    }

    public boolean isOngoing() {
        return fecha_fin.equals("0000-00-00");
    }
}
