package knf.animeflv.AdminControl.ControlEnum;

/**
 * Created by Jordy on 11/05/2016.
 */
public enum Control {
    PASS_BY_EMAIL(0),
    FORCE_EMAIL(1),
    FORCE_PASS(2),
    DELETE(3);
    int value;

    Control(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }
}
