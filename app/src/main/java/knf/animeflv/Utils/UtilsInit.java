package knf.animeflv.Utils;

import android.content.Context;

public class UtilsInit {
    public static void init(Context context) {
        NetworkUtils.initial(context);
        UtilSound.initial(context);
        UrlUtils.initial(context);
        SoundsLoader.start(context);
        Logger.initial(context);
    }
}
