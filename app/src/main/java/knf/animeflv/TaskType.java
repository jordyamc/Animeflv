package knf.animeflv;

/**
 * Created by Jordy on 12/08/2015.
 */
public enum TaskType {
    GET_INICIO(1),
    GET_HTML1(2),
    GET_INFO(3),
    GET_TITULO(4),
    GET_URL(5),
    VERSION(6),
    NOT(7),
    DIRECTORIO(8),
    GET_HTML2(9),
    DIRECTORIO1(10),
    NEW_USER(11),
    GET_FAV(12),
    LIST_USERS(13),
    cCorreo(14),
    GET_FAV_SL(15),
    cPass(16);

    int value;

    private TaskType(int value) {
        this.value = value;
    }
}
