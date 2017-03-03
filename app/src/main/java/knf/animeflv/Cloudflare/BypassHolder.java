package knf.animeflv.Cloudflare;

import com.loopj.android.http.RequestParams;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Jordy on 02/03/2017.
 */

public class BypassHolder {
    public static final String cookieKeyClearance = "cf_clearance";
    public static final String cookieKeyDuid = "__cfduid";
    public static boolean isActive = false;
    public static String valueClearance = "";
    public static String valueDuid = "";

    public static RequestParams getBasicRequestParams() {
        if (isActive) {
            RequestParams requestParams = new RequestParams();
            requestParams.put(cookieKeyClearance, valueClearance);
            requestParams.put(cookieKeyDuid, valueDuid);
            return requestParams;
        } else {
            return null;
        }
    }

    public static Map<String, String> getBasicCookieMap() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("mobile_detect", "computer");
        if (isActive) {
            map.put(cookieKeyDuid, valueDuid);
            map.put(cookieKeyClearance, valueClearance);
        }
        return map;
    }

    public static void setValueClearance(String cookie) {
        isActive = true;
        valueClearance = cookie;
    }

    public static void setValueDuid(String cookie) {
        isActive = true;
        valueDuid = cookie;
    }

    public static void clear() {
        isActive = false;
        valueClearance = "";
        valueDuid = "";
    }
}
