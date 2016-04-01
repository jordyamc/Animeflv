package knf.animeflv;

/**
 * Created by Jordy on 17/03/2016.
 */
public class MainRegistrer {
    private static MainRegistrer registrer = new MainRegistrer();
    private static boolean isProcessing = false;

    public static void init() {
        if (registrer == null) {
            registrer = new MainRegistrer();
        }
    }

    public static boolean isProcessing() {
        return isProcessing;
    }

    public static void startProcessing() {
        isProcessing = true;
    }

    public static void stopProcessing() {
        isProcessing = false;
    }

}
