package knf.animeflv.Errors;

/**
 * Created by Jordy on 02/01/2017.
 */

public class NotFoundInDirectoryException extends Exception {
    public NotFoundInDirectoryException(String message) {
        super("Not Found in directory: " + message);
    }
}
