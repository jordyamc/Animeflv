package knf.animeflv.Errors;

/**
 * Created by Jordy on 07/05/2017.
 */

public class NoSpaceException extends Exception {
    public NoSpaceException(String message) {
        super("Not enough space!!!");
    }
}
