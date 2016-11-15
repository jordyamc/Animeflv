package knf.animeflv.Utils;

import android.content.Context;

import knf.animeflv.WaitList.Costructor.WaitManager;

public class UtilsInit {
    public static void init(Context context) {
        NetworkUtils.initial(context);
        UtilSound.initial(context);
        WaitManager.initial(context);
        UrlUtils.initial(context);
        SoundsLoader.start(context);
        Logger.initial(context);
    }
}
