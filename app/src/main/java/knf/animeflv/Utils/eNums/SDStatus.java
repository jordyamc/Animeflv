package knf.animeflv.Utils.eNums;

/**
 * Created by Jordy on 23/08/2017.
 */

public enum SDStatus {
    OK(0),
    ERROR_INTERNAL(1),
    ERROR_NOT_EXTERNAL(2),
    ERROR_NOT_ROOT(3),
    ERROR_UNKNOWN(4);
    int value;

    SDStatus(int value) {
        this.value = value;
    }

    public String getErrorMessage() {
        switch (this) {
            case ERROR_INTERNAL:
            case ERROR_NOT_EXTERNAL:
                return "Debes seleccionar la memoria SD!";
            case ERROR_NOT_ROOT:
                return "Debes seleccionar la raiz de la SD!";
            case ERROR_UNKNOWN:
                return "Error desconocido";
            default:
                return "OK";
        }
    }
}
