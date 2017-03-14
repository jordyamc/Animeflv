package knf.animeflv.Cloudflare;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
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
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            addLog("Error TIMEOUT!!!!");
            enableExit();
        }
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
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                        finish();
                    }
                })
                .build();
        dialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
        CookieManager.getInstance().removeAllCookie();
        webView.getSettings().setJavaScriptEnabled(true);
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
                        String ua = webView.getSettings().getUserAgentString();
                        addLog("-User-Agent: " + ua);
                        Log.e("Detected Cookies", cookies);
                        BypassHolder.setUserAgent(DebugBypass.this, ua);
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
        });
        dialog.show();
        addLog("-Reiniciando valores guardados");
        BypassHolder.clear(this);
        Bypass.runJsoupTest(new Bypass.onTestResult() {
            @Override
            public void onResult(boolean needBypass) {
                if (needBypass) {
                    addLog("-Abriendo pagina: http://animeflv.net");
                    handler.postDelayed(runnable, Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(DebugBypass.this).getString("bypass_time", "30000")));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            webView.loadUrl("http://animeflv.net");
                        }
                    });
                } else {
                    addLog("-Bypass no es necesario");
                    enableExit();
                }
            }
        });
    }

    private void addLog(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    log += message;
                    log += "\n\n";
                    dialog.setContent(log);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void enableExit() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog.getActionButton(DialogAction.POSITIVE).setEnabled(true);
            }
        });
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
