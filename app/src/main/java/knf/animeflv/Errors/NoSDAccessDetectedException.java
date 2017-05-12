package knf.animeflv.Errors;

/**
 * Created by Jordy on 07/05/2017.
 */

public class NoSDAccessDetectedException extends Exception {
    public NoSDAccessDetectedException(String message) {
        super("No SD Card Access");
    }
}
