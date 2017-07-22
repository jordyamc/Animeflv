package knf.animeflv.Utils;

import android.app.Activity;
import android.app.UiModeManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import knf.animeflv.ColorsRes;
import knf.animeflv.R;
import knf.animeflv.Style.ThemeFragmentAdvanced;

import static android.content.Context.UI_MODE_SERVICE;

/**
 * Created by Jordy on 30/03/2016.
 */
public class ThemeUtils {
    private static Context contex;

    public static void init(Context con) {
        contex = con;
    }

    public static int getAcentColor(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt("accentColor", ColorsRes.Naranja(context));
    }

    @DrawableRes
    public static int getAccentColorDrawable(Context context) {
        int accent = getAcentColor(context);
        if (accent == ColorsRes.Rojo(context)) {
            return R.drawable.rojo;
        }
        if (accent == ColorsRes.Naranja(context)) {
            return R.drawable.naranja;
        }
        if (accent == ColorsRes.Gris(context)) {
            return R.drawable.gris;
        }
        if (accent == ColorsRes.Verde(context)) {
            return R.drawable.verde;
        }
        if (accent == ColorsRes.Rosa(context)) {
            return R.drawable.rosa;
        }
        if (accent == ColorsRes.Morado(context)) {
            return R.drawable.morado;
        }
        return R.drawable.cargando;
    }

    public static void setStatusBarPadding(Activity activity, View toolbar) {
        try {
            toolbar.setPadding(0, getStatusBarHeight(activity), 0, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setNavigationBarPadding(Activity activity, View view) {
        view.setPadding(0, 0, 0, getNavigationBarHeight(activity));
    }

    private static int getStatusBarHeight(Activity activity) {
        int result = 0;
        int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = activity.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private static int getNavigationBarHeight(Activity activity) {
        Resources resources = activity.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    public static void setThemeOn(Activity context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        int accent = getAcentColor(context);
        if (preferences.getBoolean("is_amoled", false)) {
            if (accent == ColorsRes.Rojo(context)) {
                context.setTheme(R.style.AppThemeDarkRojo);
            }
            if (accent == ColorsRes.Naranja(context)) {
                context.setTheme(R.style.AppThemeDarkNaranja);
            }
            if (accent == ColorsRes.Gris(context)) {
                context.setTheme(R.style.AppThemeDarkGris);
            }
            if (accent == ColorsRes.Verde(context)) {
                context.setTheme(R.style.AppThemeDarkVerde);
            }
            if (accent == ColorsRes.Rosa(context)) {
                context.setTheme(R.style.AppThemeDarkRosa);
            }
            if (accent == ColorsRes.Morado(context)) {
                context.setTheme(R.style.AppThemeDarkMorado);
            }
        } else {
            if (accent == ColorsRes.Rojo(context)) {
                context.setTheme(R.style.AppThemeRojo);
            }
            if (accent == ColorsRes.Naranja(context)) {
                context.setTheme(R.style.AppThemeNaranja);
            }
            if (accent == ColorsRes.Gris(context)) {
                context.setTheme(R.style.AppThemeGris);
            }
            if (accent == ColorsRes.Verde(context)) {
                context.setTheme(R.style.AppThemeVerde);
            }
            if (accent == ColorsRes.Rosa(context)) {
                context.setTheme(R.style.AppThemeRosa);
            }
            if (accent == ColorsRes.Morado(context)) {
                context.setTheme(R.style.AppThemeMorado);
            }
        }
        setOrientation(context);
    }

    @StyleRes
    public static int getTheme(Activity context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        int accent = getAcentColor(context);
        if (preferences.getBoolean("is_amoled", false)) {
            if (accent == ColorsRes.Rojo(context)) {
                return (R.style.AppThemeDarkRojo);
            }
            if (accent == ColorsRes.Naranja(context)) {
                return (R.style.AppThemeDarkNaranja);
            }
            if (accent == ColorsRes.Gris(context)) {
                return (R.style.AppThemeDarkGris);
            }
            if (accent == ColorsRes.Verde(context)) {
                return (R.style.AppThemeDarkVerde);
            }
            if (accent == ColorsRes.Rosa(context)) {
                return (R.style.AppThemeDarkRosa);
            }
            if (accent == ColorsRes.Morado(context)) {
                return (R.style.AppThemeDarkMorado);
            }
        } else {
            if (accent == ColorsRes.Rojo(context)) {
                return (R.style.AppThemeRojo);
            }
            if (accent == ColorsRes.Naranja(context)) {
                return (R.style.AppThemeNaranja);
            }
            if (accent == ColorsRes.Gris(context)) {
                return (R.style.AppThemeGris);
            }
            if (accent == ColorsRes.Verde(context)) {
                return (R.style.AppThemeVerde);
            }
            if (accent == ColorsRes.Rosa(context)) {
                return (R.style.AppThemeRosa);
            }
            if (accent == ColorsRes.Morado(context)) {
                return (R.style.AppThemeMorado);
            }
        }
        return R.style.AppThemeDark;
    }

    @StyleRes
    public static int getThemeLigth(Activity context) {
        int accent = getAcentColor(context);

        if (accent == ColorsRes.Rojo(context)) {
            return (R.style.AppThemeRojo);
        }
        if (accent == ColorsRes.Naranja(context)) {
            return (R.style.AppThemeNaranja);
        }
        if (accent == ColorsRes.Gris(context)) {
            return (R.style.AppThemeGris);
        }
        if (accent == ColorsRes.Verde(context)) {
            return (R.style.AppThemeVerde);
        }
        if (accent == ColorsRes.Rosa(context)) {
            return (R.style.AppThemeRosa);
        }
        if (accent == ColorsRes.Morado(context)) {
            return (R.style.AppThemeMorado);
        }

        return R.style.AppThemeDark;
    }

    public static void setThemeDark(Activity context) {
        int accent = getAcentColor(context);
        if (accent == ColorsRes.Rojo(context)) {
            context.setTheme(R.style.AppThemeDarkRojo);
        } else if (accent == ColorsRes.Naranja(context)) {
            context.setTheme(R.style.AppThemeDarkNaranja);
        } else if (accent == ColorsRes.Gris(context)) {
            context.setTheme(R.style.AppThemeDarkGris);
        } else if (accent == ColorsRes.Verde(context)) {
            context.setTheme(R.style.AppThemeDarkVerde);
        } else if (accent == ColorsRes.Rosa(context)) {
            context.setTheme(R.style.AppThemeDarkRosa);
        } else if (accent == ColorsRes.Morado(context)) {
            context.setTheme(R.style.AppThemeDarkMorado);
        }
        setOrientation(context);
    }

    public static void setMenuColor(Menu menu, int color) {
        for (int i = 0; i < menu.size(); i++) {
            try {
                Drawable drawable = menu.getItem(i).getIcon();
                drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
                menu.getItem(i).setIcon(drawable);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void setNavigationColor(Toolbar toolbar, int color) {
        try {
            Drawable drawable = toolbar.getNavigationIcon();
            drawable.clearColorFilter();
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
            toolbar.setNavigationIcon(drawable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setSplashTheme(Activity activity, @ColorRes int color) {
        switch (color) {
            case R.color.nmain:
                activity.setTheme(R.style.SplashNormal);
                break;
            case R.color.prim:
                activity.setTheme(R.style.SplashDark);
                break;
            case R.color.navidad:
                activity.setTheme(R.style.SplashNavidad);
                break;
            case R.color.anuevo:
                activity.setTheme(R.style.SplashAnuevo);
                break;
            case R.color.amor:
                activity.setTheme(R.style.SplashAmor);
                break;
            case R.color.negro:
                activity.setTheme(R.style.SplashNegro);
                break;
        }
    }

    public static boolean isTablet(Context activity) {
        return ((activity.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_XLARGE);
    }

    public static boolean isTV(Context context) {
        return (((UiModeManager) context.getSystemService(UI_MODE_SERVICE)).getCurrentModeType() == Configuration.UI_MODE_TYPE_TELEVISION);
    }

    public static void setOrientation(Activity activity) {
        /*if (!isTablet(activity) && !(((UiModeManager) activity.getSystemService(UI_MODE_SERVICE)).getCurrentModeType() == Configuration.UI_MODE_TYPE_TELEVISION)) { //Portrait
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else if (isTV(activity)) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        } else {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }*/
    }

    public static boolean isAmoled(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("is_amoled", false);
    }

    public static void setAmoled(Context context, boolean dark) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean("is_amoled", dark).apply();
    }

    public static class Theme {
        public static final String KEY_DARK = "is_amoled";
        public static final String KEY_ACCENT = "accentColor";
        public static final String KEY_TOOLBAR = "theme_toolbar";
        public static final String KEY_STATUS = "theme_status";
        public static final String KEY_CARD = "theme_card_normal";
        public static final String KEY_CARD_F = "color_favs";
        public static final String KEY_CARD_N = "color_new";
        public static final String KEY_BACKGROUND = "theme_background";
        public static final String KEY_BACKGROUND_T = "theme_background_tablet";
        public static final String KEY_CARD_T = "theme_card_tablet";
        public static final String KEY_TOOLBAR_T = "theme_toolbar_tablet";
        public static final String KEY_TEXT_COLOR_CARD = "theme_card_text";
        public static final String KEY_TEXT_COLOR_TOOLBAR = "theme_toolbar_text";
        public static final String KEY_ICON_FILTER = "theme_icon_filter";
        public static final String KEY_TOOLBAR_NAVIGATION = "theme_toolbar_navigation";

        public int primary;
        public int primaryDark;
        public int accent;
        public int card_normal;
        public int card_fav;
        public int card_new;
        public int background;

        public int tablet_background;
        public int tablet_toolbar;

        public int iconFilter;

        public int textColorToolbar;
        public int textColorCard;
        public int toolbarNavigation;

        public int textColor;

        public int textColorNormal;
        public int textColorI;

        public int secondaryTextColor;

        public int indicatorColor;

        public boolean isDark;

        private Context context;

        private Theme(Context context) {
            this.context = context;
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            isDark = isAmoled(context);
            primary = preferences.getInt(KEY_TOOLBAR, isDark ? ColorsRes.Negro(context) : ColorsRes.Prim(context));
            primaryDark = preferences.getInt(KEY_STATUS, isDark ? ColorsRes.Negro(context) : ColorsRes.Dark(context));
            card_normal = preferences.getInt(KEY_CARD, isDark ? ColorsRes.Prim(context) : ColorsRes.Blanco(context));
            card_fav = ColorsRes.in_favs(context);
            card_new = ColorsRes.in_new(context);
            background = preferences.getInt(KEY_BACKGROUND, isDark ? ColorsRes.Negro(context) : ColorsRes.Blanco(context));
            textColorNormal = isDark ? ColorsRes.Blanco(context) : Color.parseColor("#4d4d4d");
            textColorI = !isDark ? ColorsRes.Blanco(context) : Color.parseColor("#4d4d4d");

            textColorToolbar = preferences.getInt(KEY_TEXT_COLOR_TOOLBAR, isTablet(context) ? (isDark ? ColorsRes.Blanco(context) : Color.parseColor("#4d4d4d")) : ColorsRes.Blanco(context));
            textColorCard = preferences.getInt(KEY_TEXT_COLOR_CARD, isDark ? ColorsRes.SecondaryTextDark(context) : ColorsRes.SecondaryTextLight(context));
            toolbarNavigation = preferences.getInt(KEY_TOOLBAR_NAVIGATION, isTablet(context) ? (isDark ? ColorsRes.Blanco(context) : Color.parseColor("#4d4d4d")) : ColorsRes.Blanco(context));

            textColor = textColorCard;

            tablet_background = preferences.getInt(KEY_BACKGROUND_T, isDark ? ColorsRes.Negro(context) : ColorsRes.Blanco(context));
            tablet_toolbar = preferences.getInt(KEY_TOOLBAR_T, isDark ? ColorsRes.Negro(context) : ColorsRes.Prim(context));

            if (isTablet(context))
                background = tablet_background;

            if (primaryDark == primary) {
                int sum = isColorDark(primary) ? 40 : -40;
                indicatorColor = Color.rgb(Color.red(primary) + sum, Color.green(primary) + sum, Color.blue(primary) + sum);
            } else {
                indicatorColor = primaryDark;
            }

            secondaryTextColor = isDark ? ColorsRes.SecondaryTextDark(context) : ColorsRes.SecondaryTextLight(context);
            iconFilter = preferences.getInt(KEY_ICON_FILTER, isDark ? ColorsRes.Holo_Dark(context) : ColorsRes.Holo_Light(context));
            accent = getAcentColor(context);
        }

        private Theme(Context context, JSONArray array) throws JSONException {
            this.context = context;
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                switch (object.getString("key")) {
                    case KEY_DARK:
                        isDark = object.getBoolean("value");
                        break;
                    case KEY_ACCENT:
                        accent = value(object);
                        break;
                    case KEY_TOOLBAR:
                        primary = value(object);
                        break;
                    case KEY_STATUS:
                        primaryDark = value(object);
                        break;
                    case KEY_CARD:
                        card_normal = value(object);
                        break;
                    case KEY_CARD_F:
                        card_fav = value(object);
                        break;
                    case KEY_CARD_N:
                        card_new = value(object);
                        break;
                    case KEY_BACKGROUND:
                        background = value(object);
                        break;
                    case KEY_BACKGROUND_T:
                        tablet_background = value(object);
                        break;
                    case KEY_TOOLBAR_T:
                        tablet_toolbar = value(object);
                        break;
                    case KEY_TEXT_COLOR_CARD:
                        textColorCard = value(object);
                        break;
                    case KEY_TEXT_COLOR_TOOLBAR:
                        textColorToolbar = value(object);
                        break;
                    case KEY_ICON_FILTER:
                        iconFilter = value(object);
                        break;
                    case KEY_TOOLBAR_NAVIGATION:
                        toolbarNavigation = value(object);
                        break;
                }
            }
            textColor = isDark ? ColorsRes.Blanco(context) : Color.parseColor("#4d4d4d");
            textColorI = !isDark ? ColorsRes.Blanco(context) : Color.parseColor("#4d4d4d");

            if (isTablet(context))
                background = tablet_background;

            if (primaryDark == primary) {
                int sum = isColorDark(primary) ? 40 : -40;
                indicatorColor = Color.rgb(Color.red(primary) + sum, Color.green(primary) + sum, Color.blue(primary) + sum);
            } else {
                indicatorColor = primaryDark;
            }

            secondaryTextColor = isDark ? ColorsRes.SecondaryTextDark(context) : ColorsRes.SecondaryTextLight(context);
            checkForNull(context);
        }

        public static Theme create(Context context) {
            return new Theme(context);
        }

        public static Theme fromJson(Context context, JSONArray array) throws JSONException {
            return new Theme(context, array);
        }

        public static int get(Context context, String key) {
            return PreferenceManager.getDefaultSharedPreferences(context).getInt(key, ThemeFragmentAdvanced.getDefault(context, key));
        }

        private void checkForNull(Context context) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            if (textColorCard == 0)
                textColorCard = preferences.getInt(KEY_TEXT_COLOR_CARD, isDark ? ColorsRes.SecondaryTextDark(context) : ColorsRes.SecondaryTextLight(context));
            if (textColorToolbar == 0)
                textColorToolbar = preferences.getInt(KEY_TEXT_COLOR_TOOLBAR, isTablet(context) ? (isDark ? ColorsRes.Blanco(context) : Color.parseColor("#4d4d4d")) : ColorsRes.Blanco(context));
            if (iconFilter == 0)
                iconFilter = preferences.getInt(KEY_ICON_FILTER, isDark ? ColorsRes.Holo_Dark(context) : ColorsRes.Holo_Light(context));
            if (toolbarNavigation == 0)
                toolbarNavigation = preferences.getInt(KEY_TOOLBAR_NAVIGATION, isTablet(context) ? (isDark ? ColorsRes.Blanco(context) : Color.parseColor("#4d4d4d")) : ColorsRes.Blanco(context));
        }

        private int value(JSONObject object) throws JSONException {
            return object.getInt("value");
        }

        private boolean isColorDark(int color) {
            double darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255;
            return darkness >= 0.5;
        }

        private JSONObject getPref(String key) throws JSONException {
            JSONObject object = new JSONObject();
            object.put("key", key);
            if (key.equals(KEY_DARK)) {
                object.put("value", isAmoled(context));
            } else {
                object.put("value", ThemeFragmentAdvanced.getDefault(context, key));
            }
            return object;
        }

        @Nullable
        public JSONObject toJson() {
            try {
                JSONObject object = new JSONObject();
                JSONArray array = new JSONArray();
                array.put(getPref(KEY_DARK));
                array.put(getPref(KEY_ACCENT));
                array.put(getPref(KEY_TOOLBAR));
                array.put(getPref(KEY_STATUS));
                array.put(getPref(KEY_CARD));
                array.put(getPref(KEY_CARD_F));
                array.put(getPref(KEY_CARD_N));
                array.put(getPref(KEY_BACKGROUND));
                array.put(getPref(KEY_BACKGROUND_T));
                array.put(getPref(KEY_CARD_T));
                array.put(getPref(KEY_TOOLBAR_T));
                array.put(getPref(KEY_TOOLBAR_NAVIGATION));
                array.put(getPref(KEY_TEXT_COLOR_TOOLBAR));
                array.put(getPref(KEY_TEXT_COLOR_CARD));
                array.put(getPref(KEY_ICON_FILTER));
                object.put("theme", array);
                return object;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
