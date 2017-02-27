package knf.animeflv.Utils;

import android.app.Activity;
import android.app.UiModeManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.view.View;

import knf.animeflv.ColorsRes;
import knf.animeflv.R;

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
        toolbar.setPadding(0, getStatusBarHeight(activity), 0, 0);
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                context.getWindow().setNavigationBarColor(context.getResources().getColor(R.color.negro));
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                context.getWindow().setNavigationBarColor(context.getResources().getColor(R.color.prim));
            }
        }
        setOrientation(context);
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

    public static boolean isTablet(Activity activity) {
        return ((activity.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_XLARGE);
    }

    public static void setOrientation(Activity activity) {
        if (!isTablet(activity) && !(((UiModeManager) activity.getSystemService(UI_MODE_SERVICE)).getCurrentModeType() == Configuration.UI_MODE_TYPE_TELEVISION)) { //Portrait
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    public static boolean isAmoled(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("is_amoled", false);
    }

    public static void setAmoled(Context context, boolean dark) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean("is_amoled", dark).apply();
    }
}
