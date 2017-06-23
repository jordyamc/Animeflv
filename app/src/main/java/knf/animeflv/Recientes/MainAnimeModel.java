package knf.animeflv.Recientes;

import android.content.Context;

import knf.animeflv.Favorites.FavoriteHelper;
import knf.animeflv.Utils.FileUtil;

/**
 * Created by Jordy on 17/03/2016.
 */
public class MainAnimeModel {
    private String eid;
    private String aid;
    private String numero;
    private String tipo;
    private String titulo;
    private Type type;

    public MainAnimeModel(Context context, String eid, String tipo, String titulo) {
        this.eid = eid;
        this.tipo = tipo;
        this.titulo = FileUtil.corregirTit(titulo);
        String[] cortado = eid.replace("E", "").split("_");
        this.aid = cortado[0];
        this.numero = cortado[1];
        if (FavoriteHelper.isFav(context, aid)) {
            type = Type.FAV;
        } else if (numero.equals("0") || numero.equals("1") || !tipo.equals("Anime")) {
            type = Type.NEW;
        } else {
            type = Type.NORMAL;
        }
    }

    public String getEid() {
        return eid;
    }

    public String getAid() {
        return aid;
    }

    public String getNumero() {
        return numero;
    }

    public String getTipo() {
        return tipo;
    }

    public String getTitulo() {
        return titulo;
    }

    public Type getType() {
        return type;
    }

    public enum Type {
        FAV(0),
        NEW(1),
        NORMAL(2);
        int value;

        Type(int value) {
            this.value = value;
        }
    }
}
