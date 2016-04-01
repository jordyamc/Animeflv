package knf.animeflv.Utils;

import android.content.Context;

import knf.animeflv.WaitList.Costructor.WaitManager;

/**
 * Created by Jordy on 26/03/2016.
 */
public class UtilsInit {
    public static void init(Context context) {
        FileUtil.init(context);
        MainStates.init(context);
        NetworkUtils.init(context);
        ThemeUtils.init(context);
        WaitManager.init(context);
        UrlUtils.init(context);
    }
}
