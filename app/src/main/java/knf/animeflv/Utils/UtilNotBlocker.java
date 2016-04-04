package knf.animeflv.Utils;

/**
 * Created by Jordy on 03/04/2016.
 */
public class UtilNotBlocker {
    private static boolean blocked = false;
    private static boolean paused = false;

    public static boolean isBlocked() {
        return blocked;
    }

    public static void setBlocked(boolean blocked) {
        UtilNotBlocker.blocked = blocked;
    }

    public static boolean isPaused() {
        return paused;
    }

    public static void setPaused(boolean paused) {
        UtilNotBlocker.paused = paused;
    }
}
