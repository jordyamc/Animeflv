package knf.animeflv;

import android.content.Context;
import android.preference.PreferenceManager;

import knf.animeflv.Utils.ThemeUtils;

/**
 * Created by Jordy on 12/12/2016.
 */

class ThemeHolder {
    static boolean isDark = false;
    static int accentColor = -1;

    static void applyTheme(Context context) {
        ThemeUtils.setAmoled(context, isDark);
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt("accentColor", accentColor).apply();
        isDark = false;
        accentColor = -1;
    }
}
