package knf.animeflv;

/**
 * Created by Jordy on 12/08/2015.
 */
public enum TaskType {
    GET_INICIO(1), GET_HTML1(2);

    int value;

    private TaskType(int value) {
        this.value = value;
    }
}
