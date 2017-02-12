package knf.animeflv;

import android.content.Context;
import android.preference.PreferenceManager;

import knf.animeflv.Utils.ThemeUtils;

public class ThemeHolder {
    public static boolean isDark = false;
    public static int accentColor = -1;

    public static boolean old_isDark = false;
    public static int old_accentColor = -1;

    public static void applyTheme(Context context) {
        ThemeUtils.setAmoled(context, isDark);
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt("accentColor", accentColor).apply();
        isDark = false;
        accentColor = -1;
    }

    public static void applyThemeNoReset(Context context) {
        ThemeUtils.setAmoled(context, isDark);
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt("accentColor", accentColor).apply();
    }
}
