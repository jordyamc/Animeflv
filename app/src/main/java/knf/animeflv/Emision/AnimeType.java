package knf.animeflv.Emision;

/**
 * Created by Jordy on 05/03/2016.
 */
public enum AnimeType {
    ANIME(0),
    OVA(1),
    PELICULA(2),
    NULL(3);
    int value;

    AnimeType(int value) {
        this.value = value;
    }
}
