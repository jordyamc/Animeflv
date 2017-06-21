package knf.animeflv.Utils;

import android.app.Activity;
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

    public static final String ACTION_SHARE = "Share: ";

    public static void track(Activity activity, String screen) {
        Log.e("Traking", screen);
        Tracker tracker = Application.get(activity).getDefaultTracker();
        tracker.setScreenName(screen);
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    public static void action(Activity activity, String screen) {
        Tracker tracker = Application.get(activity).getDefaultTracker();
        tracker.setScreenName(screen);
        tracker.send(
                new HitBuilders.EventBuilder()
                        .setCategory("Action")
                        .setAction("Share")
                .build()
        );
    }
}
