package knf.animeflv.Cloudflare;

import android.content.Context;
import android.preference.PreferenceManager;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Jordy on 02/03/2017.
 */

public class BypassHolder {
    public static final String cookieKeyClearance = "cf_clearance";
    public static final String cookieKeyDuid = "__cfduid";
    public static final String default_userAgent = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 UBrowser/5.7.15533.1010 Safari/537.36";
    public static boolean isActive = false;
    public static String valueClearance = "";
    public static String valueDuid = "";
    public static String userAgent = "";

    public static Map<String, String> getBasicCookieMap() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("mobile_detect", "computer");
        if (isActive) {
            if (!valueDuid.equals(""))
                map.put(cookieKeyDuid, valueDuid);
            if (!valueClearance.equals(""))
                map.put(cookieKeyClearance, valueClearance);
        }
        return map;
    }

    public static Map<String, String> getBasicCookieMap(Context context) {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("mobile_detect", "computer");
        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("isBypassActive", false)) {
            String duid = PreferenceManager.getDefaultSharedPreferences(context).getString("bypassDuid", "");
            if (!duid.equals(""))
                map.put(cookieKeyDuid, duid);
            String clearance = PreferenceManager.getDefaultSharedPreferences(context).getString("bypassClearance", "");
            if (!clearance.equals(""))
                map.put(cookieKeyClearance, clearance);
        }
        return map;
    }

    public static String getUserAgent() {
        if (isActive)
            return userAgent;
        return default_userAgent;
    }

    public static String getUserAgent(Context context) {
        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("isBypassActive", false))
            return PreferenceManager.getDefaultSharedPreferences(context).getString("bypassUserAgent", default_userAgent);
        return default_userAgent;
    }

    public static void setUserAgent(Context context, String ua) {
        setActive(context, true);
        userAgent = ua;
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("bypassUserAgent", ua).apply();
    }

    public static void setValueClearance(Context context, String cookie) {
        setActive(context, true);
        valueClearance = cookie;
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("bypassClearance", cookie).apply();
    }

    public static void setValueDuid(Context context, String cookie) {
        setActive(context, true);
        valueDuid = cookie;
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("bypassDuid", cookie).apply();
    }

    private static void setActive(Context context, boolean active) {
        isActive = active;
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean("isBypassActive", active).apply();
    }

    public static void savedToLocal(Context context) {
        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("isBypassActive", false)) {
            isActive = true;
            valueDuid = PreferenceManager.getDefaultSharedPreferences(context).getString("bypassDuid", "");
            valueClearance = PreferenceManager.getDefaultSharedPreferences(context).getString("bypassClearance", "");
            userAgent = PreferenceManager.getDefaultSharedPreferences(context).getString("bypassUserAgent", default_userAgent);
        }
    }

    public static void clear(Context context) {
        setActive(context, false);
        valueClearance = "";
        valueDuid = "";
        userAgent = "";
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("bypassUserAgent", "").apply();
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("bypassClearance", "").apply();
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("bypassDuid", "").apply();
    }
}
