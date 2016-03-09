package knf.animeflv.Emision;

import android.content.Context;

/**
 * Created by Jordy on 05/03/2016.
 */
public class EmisionChecker {
    public static Checker init(Context context) {
        return new Checker(context);
    }
}
