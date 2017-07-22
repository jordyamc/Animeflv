package knf.animeflv.Cloudflare;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.Nullable;
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

public class Bypass {

    public static void check(final Context context, @Nullable final onBypassCheck check) {
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
                            clearCookies(context, ".animeflv.net");
                            final WebView webView = new WebView(context);
                            webView.getSettings().setJavaScriptEnabled(true);
                            webView.setWebViewClient(new WebViewClient() {
                                @Override
                                public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
                                    Log.e("CloudflareBypass", "Redirect to: " + url);
                                    if (url.startsWith("https://animeflv.net") && !url.contains("cdn-cgi")) {
                                        Log.e("CloudflareBypass", "Getting Cookies");
                                        String cookies = CookieManager.getInstance().getCookie("https://animeflv.net");
                                        if (cookies != null)
                                            Log.e("CloudflareBypass", "Getting Cookies: " + cookies);
                                        if (cookies != null && (cookies.contains(BypassHolder.cookieKeyClearance) || cookies.contains(BypassHolder.cookieKeyDuid))) {
                                            String ua = webView.getSettings().getUserAgentString();
                                            Log.e("Detected Cookies", cookies);
                                            BypassHolder.setUserAgent(context, ua);
                                            saveCookie(context, cookies, check);
                                        } else {
                                            Toaster.toast("Error al activar Bypass");
                                            if (check != null)
                                                check.onFinish();
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
                                        if (url.startsWith("https://animeflv.net") && !url.contains("cdn-cgi")) {
                                            Log.e("CloudflareBypass", "Getting Cookies");
                                            String cookies = CookieManager.getInstance().getCookie("https://animeflv.net");
                                            if (cookies != null)
                                                Log.e("CloudflareBypass", "Getting Cookies: " + cookies);
                                            if (cookies != null && (cookies.contains(BypassHolder.cookieKeyClearance) && cookies.contains(BypassHolder.cookieKeyDuid))) {
                                                String ua = webView.getSettings().getUserAgentString();
                                                Log.e("Detected Cookies", cookies);
                                                BypassHolder.setUserAgent(context, ua);
                                                saveCookie(context, cookies, check);
                                            }
                                        }
                                    }
                                }
                            });
                            webView.loadUrl("https://animeflv.net");
                        }
                    });
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.e("CloudflareBypass", "Trying again");
                ServerGetter.getClient().get("https://animeflv.net", null, new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Log.e("CloudflareBypass", "ENABLED Keep bypass");
                        if (check != null)
                            check.onFinish();
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        Log.e("CloudflareBypass", "DISABLED clear bypass");
                        BypassHolder.clear(context);
                        if (check != null)
                            check.onFinish();
                    }
                });
            }
        });
    }

    private static void saveCookie(Context context, String cookies, @Nullable onBypassCheck check) {
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
            if (check != null)
                check.onFinish();
        }
    }

    public static void clearCookies(Context context, String domain) {
        CookieManager cookieManager = CookieManager.getInstance();
        String cookiestring = cookieManager.getCookie(domain);
        String[] cookies = cookiestring.split(";");
        for (String cookie : cookies) {
            String[] cookieparts = cookie.split("=");
            cookieManager.setCookie(domain, cookieparts[0].trim() + "=; Expires=Wed, 31 Dec 2025 23:59:59 GMT");
        }
    }

    public static void runJsoupTest(final onTestResult result) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    Jsoup.connect("https://animeflv.net").cookies(BypassHolder.getBasicCookieMap()).userAgent(BypassHolder.getUserAgent()).timeout(SelfGetter.TIMEOUT).get();
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

    public static void runJsoupTest(final Context context, final onTestResult result) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    Jsoup.connect("https://animeflv.net").cookies(BypassHolder.getBasicCookieMap(context)).userAgent(BypassHolder.getUserAgent(context)).timeout(SelfGetter.TIMEOUT).get();
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
