package knf.animeflv.Directorio;

/**
 * Created by Jordy on 15/02/2016.
 */
public class AnimeClass {
    private String nombre;
    private String aid;
    private String tipo;
    private String imagen;
    private int index;

    public AnimeClass(String nombre, String aid, String tipo, String imagen, int index) {
        this.nombre = nombre;
        this.aid = aid;
        this.tipo = tipo;
        this.imagen = imagen;
        this.index = index;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public String getImagen() {
        return imagen;
    }

    public String getAid() {
        return aid;
    }

    public int getIndex() {
        return index;
    }
}
