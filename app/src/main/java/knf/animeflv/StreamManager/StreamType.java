package knf.animeflv.StreamManager;

/**
 * Created by Jordy on 04/03/2016.
 */
public enum StreamType {
    INTERNAL(0),
    EXTERNAL(1),
    MX(2);
    int value;

    StreamType(int value) {
        this.value = value;
    }
}
