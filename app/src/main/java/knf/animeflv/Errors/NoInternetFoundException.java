package knf.animeflv.Errors;

/**
 * Created by Jordy on 22/05/2017.
 */

public class NoInternetFoundException extends IllegalStateException {
    public NoInternetFoundException(String s) {
        super("No se detecta conexion a internet!!!");
    }
}
