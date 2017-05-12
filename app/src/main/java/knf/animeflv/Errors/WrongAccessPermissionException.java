package knf.animeflv.Errors;

/**
 * Created by Jordy on 07/05/2017.
 */

public class WrongAccessPermissionException extends Exception {
    public WrongAccessPermissionException(String message) {
        super("The tree Uri permission not equals the SD selected!!!\nSelected: " + message);
    }
}
