package knf.animeflv.Utils;

import knf.animeflv.Utils.eNums.UpdateState;

/**
 * Created by Jordy on 31/03/2016.
 */
public class UpdateUtil {
    public static boolean isBeta = false;
    private static UpdateState state = UpdateState.NO_UPDATE;

    public static UpdateState getState() {
        return state;
    }

    public static void setState(UpdateState stat) {
        state = stat;
    }
}
