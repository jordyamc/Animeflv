package knf.animeflv.Errors;

/**
 * Created by Jordy on 07/05/2017.
 */

public class NoInternetException extends Exception {

    public NoInternetException(String message) {
        super("No internet detected!!!");
    }
}
