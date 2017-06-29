package knf.animeflv.Directorio;

import org.json.JSONObject;

import knf.animeflv.Utils.FileUtil;

/**
 * Created by Jordy on 15/02/2016.
 */
public class AnimeClass {
    private String nombre;
    private String aid;
    private String tipo;

    public AnimeClass(String nombre, String aid, String tipo) {
        this.nombre = FileUtil.corregirTit(nombre);
        this.aid = aid;
        this.tipo = tipo;
    }

    public AnimeClass(String json) {
        try {
            JSONObject jsonObj = new JSONObject(json);
            this.nombre = FileUtil.corregirTit(jsonObj.getString("titulo"));
            this.aid = jsonObj.getString("aid");
        } catch (Exception e) {
            e.printStackTrace();
            this.nombre = "Null";
            this.aid = "0";
        }
    }

    public String getNombre() {
        return nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public String getAid() {
        return aid;
    }

    public int getAidInt() {
        try {
            return Integer.parseInt(aid);
        } catch (Exception e) {
            return 0;
        }
    }
}
