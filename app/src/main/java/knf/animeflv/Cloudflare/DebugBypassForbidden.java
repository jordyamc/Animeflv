package knf.animeflv.Cloudflare;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import knf.animeflv.R;
import knf.animeflv.Utils.ThemeUtils;
import knf.kuma.uagen.UAGeneratorKt;
import xdroid.toaster.Toaster;

/**
 * Created by Jordy on 13/03/2017.
 */

public class DebugBypassForbidden extends AppCompatActivity {

    @BindView(R.id.webview)
    WebView webView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ThemeUtils.setThemeOn(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_bypass);
        ButterKnife.bind(this);
        Bypass.clearCookies(this, ".animeflv.net");
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setUserAgentString(BypassHolder.getUserAgent());
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.e("CloudflareBypass", "Redirect to: " + url);
                if (url.startsWith("http://animeflv.net") && !url.contains("cdn-cgi")) {
                    String cookies = CookieManager.getInstance().getCookie("http://animeflv.net");
                    if (cookies != null && (cookies.contains(BypassHolder.cookieKeyClearance) || cookies.contains(BypassHolder.cookieKeyDuid))) {
                        Log.e("Detected Cookies", cookies);
                        saveCookie(DebugBypassForbidden.this, cookies);
                    } else
                        Toaster.toast("Error al activar Bypass");
                    return true;
                } else {
                    return false;
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    if (url.startsWith("http://animeflv.net") && !url.contains("cdn-cgi")) {
                        String cookies = CookieManager.getInstance().getCookie("http://animeflv.net");
                        if (cookies != null && (cookies.contains(BypassHolder.cookieKeyClearance) && cookies.contains(BypassHolder.cookieKeyDuid))) {
                            Log.e("Detected Cookies", cookies);
                            saveCookie(DebugBypassForbidden.this, cookies);
                        }
                    }
                }
            }
        });
        BypassHolder.clear(this);
        Bypass.runJsoupTest(needBypass -> {
            if (needBypass)
                runOnUiThread(() -> webView.loadUrl("http://animeflv.net"));
        });
    }

    private void saveCookie(Context context, String cookies) {
        if (cookies.contains("__cfduid") || cookies.contains("cf_clearance")) {
            String[] parts = cookies.split(";");
            for (String cookie : parts) {
                if (cookie.contains("__cfduid")) {
                    BypassHolder.setValueDuid(context, cookie.trim().substring(cookie.trim().indexOf("=") + 1));
                    Log.e("CloudflareBypass", "set Cookie Duid: " + cookie.trim().substring(cookie.trim().indexOf("=") + 1));
                }
                if (cookie.contains("cf_clearance")) {
                    BypassHolder.setValueClearance(context, cookie.trim().substring(cookie.trim().indexOf("=") + 1));
                    Log.e("CloudflareBypass", "set Cookie Clearance: " + cookie.trim().substring(cookie.trim().indexOf("=") + 1));
                }
            }
            Toaster.toast("Bypass de Cloudflare Activado");
            finish();
        }
    }
}
