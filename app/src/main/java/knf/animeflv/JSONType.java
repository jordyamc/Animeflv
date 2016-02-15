package knf.animeflv;

/**
 * Created by Jordy on 03/02/2016.
 */
public enum JSONType {
    INICIO(0),
    DIRECTORIO(1),
    INFO(2),
    DESCARGA(3);
    int value;

    JSONType(int value) {
        this.value = value;
    }
}
