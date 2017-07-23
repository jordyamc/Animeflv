package knf.animeflv.Utils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import knf.animeflv.Application;

public class TrackingHelper {
    public static final String MAIN = "Recientes";
    public static final String FAVORITOS = "Favoritos";
    public static final String DIRECTORIO = "Directorio";
    public static final String EXPLORADOR = "Explorador";
    public static final String INFO = "Info: ";
    public static final String EMISION = "Emision";
    public static final String RANDOM = "Random";
    public static final String UPDATING = "Actualizando: ";
    public static final String THEME = "Cambiando tema";
    public static final String DONATE = "Considerando donar";

    public static final String ACTION_SHARE = "Share";
    public static final String ACTION_DONATE_PAYPAL = "Paypal Donate";
    public static final String ACTION_DONATE_PATREON = "Patreon Donate";
    public static final String ACTION_DONATE_BITCOIN = "Bitcoin Donate";

    public static void track(Context activity, String screen) {
        Log.e("Traking", screen);
        Tracker tracker = null;
        if (activity instanceof Activity) {
            tracker = Application.get((Activity) activity).getDefaultTracker();
        } else if (activity instanceof Application) {
            tracker = ((Application) activity).getDefaultTracker();
        }
        if (tracker != null) {
            tracker.setScreenName(screen);
            tracker.send(new HitBuilders.ScreenViewBuilder().build());
        }
    }

    public static void action(Activity activity, String screen, String action) {
        Tracker tracker = Application.get(activity).getDefaultTracker();
        tracker.setScreenName(screen);
        tracker.send(
                new HitBuilders.EventBuilder()
                        .setCategory(getCategory(action))
                        .setAction(action)
                        .build()
        );
    }

    private static String getCategory(String action) {
        switch (action) {
            case ACTION_DONATE_PAYPAL:
            case ACTION_DONATE_PATREON:
            case ACTION_DONATE_BITCOIN:
                return "Donate";
            case ACTION_SHARE:
                return "Share";
            default:
                if (action.contains(ACTION_SHARE)) {
                    return "Share";
                } else {
                    return "Action";
                }
        }
    }
}
