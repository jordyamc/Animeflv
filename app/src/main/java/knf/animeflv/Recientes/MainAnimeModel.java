package knf.animeflv.Recientes;

import knf.animeflv.Parser;

/**
 * Created by Jordy on 17/03/2016.
 */
public class MainAnimeModel {
    private String eid;
    private String aid;
    private String numero;
    private String tipo;
    private String titulo;
    private Parser parser = new Parser();

    public MainAnimeModel(String eid, String tipo, String titulo) {
        this.eid = eid;
        this.tipo = tipo;
        this.titulo = parser.corregirTit(titulo);
        String[] cortado = eid.replace("E", "").split("_");
        this.aid = cortado[0];
        this.numero = cortado[1];
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
}
