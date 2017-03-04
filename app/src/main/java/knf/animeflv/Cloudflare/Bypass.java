package knf.animeflv.Cloudflare;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.TextHttpResponseHandler;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.impl.cookie.BasicClientCookie;
import knf.animeflv.JsonFactory.SelfGetter;
import knf.animeflv.JsonFactory.ServerGetter;
import knf.animeflv.Utils.ExecutorManager;
import xdroid.toaster.Toaster;

/**
 * Created by Jordy on 02/03/2017.
 */

//TODO: Find a solution
public class Bypass {

    public static void check(final Context context, final onBypassCheck check) {
        BypassHolder.savedToLocal(context);
        AsyncHttpClient client = ServerGetter.getClient();
        if (BypassHolder.isActive) {
            Log.e("CloudflareBypass", "isActive sending saved Cookies");
            PersistentCookieStore cookieStore = new PersistentCookieStore(context);
            BasicClientCookie duid = new BasicClientCookie(BypassHolder.cookieKeyDuid, BypassHolder.valueDuid);
            duid.setDomain(".animeflv.net");
            duid.setPath("/");
            cookieStore.addCookie(duid);
            BasicClientCookie clearance = new BasicClientCookie(BypassHolder.cookieKeyClearance, BypassHolder.valueClearance);
            clearance.setDomain(".animeflv.net");
            clearance.setPath("/");
            cookieStore.addCookie(clearance);
            client.setCookieStore(cookieStore);
        }
        client.setUserAgent(BypassHolder.getUserAgent());
        client.get("http://animeflv.net", null, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (statusCode != 0) {
                    Log.e("CloudflareBypass", "Failed with code: " + statusCode);
                    ((AppCompatActivity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            CookieManager.getInstance().removeAllCookie();
                            final WebView webView = new WebView(context);
                            webView.getSettings().setJavaScriptEnabled(true);
                            webView.setWebViewClient(new WebViewClient() {
                                @Override
                                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                    Log.e("CloudflareBypass", "Redirect to: " + url);
                                    if (url.equals("http://animeflv.net/")) {
                                        String cookies = CookieManager.getInstance().getCookie("http://animeflv.net");
                                        String ua = webView.getSettings().getUserAgentString();
                                        Log.e("Detected Cookies", cookies);
                                        BypassHolder.setUserAgent(context, ua);
                                        saveCookie(context, cookies, check);
                                        return true;
                                    } else {
                                        return false;
                                    }
                                }
                            });
                            webView.loadUrl("http://animeflv.net");
                        }
                    });
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.e("CloudflareBypass", "Trying again");
                ServerGetter.getClient().get("http://animeflv.net", null, new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Log.e("CloudflareBypass", "ENABLED Keep bypass");
                        check.onFinish();
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        Log.e("CloudflareBypass", "DISABLED clear bypass");
                        BypassHolder.clear(context);
                        check.onFinish();
                    }
                });
            }
        });
    }

    private static void saveCookie(Context context, String cookies, onBypassCheck check) {
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
            check.onFinish();
        }
    }

    public static void runJsoupTest(final onTestResult result) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    Jsoup.connect("http://animeflv.net").cookies(BypassHolder.getBasicCookieMap()).userAgent(BypassHolder.getUserAgent()).timeout(SelfGetter.TIMEOUT).get();
                    result.onResult(false);
                } catch (HttpStatusException ex) {
                    Log.e("CloudflareBypass", "Jsoup Test failed Code: " + ex.getStatusCode());
                    result.onResult(true);
                } catch (Exception e) {
                    Log.e("CloudflareBypass", "Jsoup Test failed", e);
                    result.onResult(false);
                }
                return null;
            }
        }.executeOnExecutor(ExecutorManager.getExecutor());
    }

    public interface onTestResult {
        void onResult(boolean needBypass);
    }

    public interface onBypassCheck {
        void onFinish();
    }
}
