package knf.animeflv.Utils;

import knf.animeflv.Utils.eNums.UpdateState;

/**
 * Created by Jordy on 31/03/2016.
 */
public class UpdateUtil {
    private static UpdateState state = UpdateState.NO_UPDATE;

    public static UpdateState getState() {
        return state;
    }

    public static void setState(UpdateState stat) {
        state = stat;
    }
}
