package knf.animeflv.Cloudflare;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import knf.animeflv.R;
import knf.animeflv.Utils.ThemeUtils;
import knf.kuma.uagen.UAGeneratorKt;
import xdroid.toaster.Toaster;

/**
 * Created by Jordy on 13/03/2017.
 */

public class DebugBypass extends AppCompatActivity {

    @BindView(R.id.webview)
    WebView webView;
    String log = "-Comenzando...\n\n";
    MaterialDialog dialog;
    Handler handler = new Handler();
    Runnable runnable = () -> {
        addLog("Error TIMEOUT!!!!");
        enableExit();
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ThemeUtils.setThemeOn(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_bypass);
        ButterKnife.bind(this);
        dialog = new MaterialDialog.Builder(this)
                .title("Activando...")
                .content(log)
                .cancelable(false)
                .autoDismiss(false)
                .positiveText("Salir")
                .onPositive((dialog, which) -> {
                    dialog.dismiss();
                    finish();
                })
                .build();
        dialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
        Bypass.clearCookies(this, ".animeflv.net");
        String ua = UAGeneratorKt.randomUA();
        BypassHolder.setUserAgent(this, ua);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setUserAgentString(ua);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                addLog("-Redireccion: " + url);
                Log.e("CloudflareBypass", "Redirect to: " + url);
                if (url.startsWith("http://animeflv.net") && !url.contains("cdn-cgi")) {
                    addLog("-Capturando cookies...");
                    String cookies = CookieManager.getInstance().getCookie("http://animeflv.net");
                    if (cookies != null && (cookies.contains(BypassHolder.cookieKeyClearance) || cookies.contains(BypassHolder.cookieKeyDuid))) {
                        addLog("-Cookies detectadas: " + cookies);
                        Log.e("Detected Cookies", cookies);
                        saveCookie(DebugBypass.this, cookies);
                    } else {
                        Toaster.toast("Error al activar Bypass");
                        addLog("-Error al capturar Cookies!!!");
                        handler.removeCallbacks(runnable);
                        enableExit();
                    }
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
                        addLog("-Page Finished JellyBeam: " + url);
                        String cookies = CookieManager.getInstance().getCookie("http://animeflv.net");
                        if (cookies != null && (cookies.contains(BypassHolder.cookieKeyClearance) && cookies.contains(BypassHolder.cookieKeyDuid))) {
                            addLog("-Cookies detectadas: " + cookies);
                            Log.e("Detected Cookies", cookies);
                            saveCookie(DebugBypass.this, cookies);
                        }
                    }
                }
            }
        });
        dialog.show();
        addLog("-Reiniciando valores guardados");
        BypassHolder.clear(this);
        Bypass.runJsoupTest(needBypass -> {
            if (needBypass) {
                addLog("-Abriendo pagina: http://animeflv.net");
                handler.postDelayed(runnable, Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(DebugBypass.this).getString("bypass_time", "30000")));
                runOnUiThread(() -> webView.loadUrl("http://animeflv.net"));
            } else {
                addLog("-Bypass no es necesario");
                enableExit();
            }
        });
    }

    private void addLog(final String message) {
        runOnUiThread(() -> {
            try {
                log += message;
                log += "\n\n";
                dialog.setContent(log);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void enableExit() {
        runOnUiThread(() -> dialog.getActionButton(DialogAction.POSITIVE).setEnabled(true));
    }

    private void saveCookie(Context context, String cookies) {
        if (cookies.contains("__cfduid") || cookies.contains("cf_clearance")) {
            String[] parts = cookies.split(";");
            for (String cookie : parts) {
                if (cookie.contains("__cfduid")) {
                    BypassHolder.setValueDuid(context, cookie.trim().substring(cookie.trim().indexOf("=") + 1));
                    Log.e("CloudflareBypass", "set Cookie Duid: " + cookie.trim().substring(cookie.trim().indexOf("=") + 1));
                    addLog("-Duid: " + cookie.trim().substring(cookie.trim().indexOf("=") + 1));
                }
                if (cookie.contains("cf_clearance")) {
                    BypassHolder.setValueClearance(context, cookie.trim().substring(cookie.trim().indexOf("=") + 1));
                    Log.e("CloudflareBypass", "set Cookie Clearance: " + cookie.trim().substring(cookie.trim().indexOf("=") + 1));
                    addLog("-Clearance: " + cookie.trim().substring(cookie.trim().indexOf("=") + 1));
                }
            }
            Toaster.toast("Bypass de Cloudflare Activado");
            addLog("-Activacion exitosa!!!!");
            handler.removeCallbacks(runnable);
            enableExit();
        }
    }
}
