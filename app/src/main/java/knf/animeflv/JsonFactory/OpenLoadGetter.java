package knf.animeflv.JsonFactory;

import android.app.Activity;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.net.URLEncoder;

/**
 * Created by Jordy on 08/06/2017.
 */

public class OpenLoadGetter {
    public static void get(final Activity activity, final String url_start, final OpenLoadInterface openLoadInterface) {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                openLoadInterface.onError("TIMEOUT!!");
            }
        }, 10000);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                CookieManager.getInstance().removeAllCookie();
                final WebView webView = new WebView(activity);
                setDesktopMode(webView);
                webView.getSettings().setJavaScriptEnabled(true);
                webView.getSettings().setLoadsImagesAutomatically(false);
                webView.getSettings().setBlockNetworkImage(true);
                webView.addJavascriptInterface(new UrlGetter(handler, openLoadInterface), "getter");
                webView.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        Log.e("Url Openload Finished", url);
                        /*Log.e("Javascript Openload","javascript:getter.getUrl($('#streamurl').text())");
                        webView.loadUrl("javascript:getter.getUrl($('#streamurl').text())");*/
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                webView.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                            webView.evaluateJavascript("(function() { return ('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>'); })();", new ValueCallback<String>() {
                                                @Override
                                                public void onReceiveValue(String value) {
                                                    Log.e("Openload HTML", value);
                                                }
                                            });
                                        } else {
                                            webView.loadUrl("javascript:getter.getHtml('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>')");
                                        }
                                    }
                                });
                            }
                        }, 3000);
                    }

                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        Log.e("Url Openload Override", url);
                        webView.loadUrl(url);
                        return true;
                    }
                });
                Log.e("Url Openload", url_start);
                try {
                    webView.loadUrl("https://9xbuddy.com/process?url=" + URLEncoder.encode(url_start, "UTF-8"));
                } catch (Exception e) {
                    e.printStackTrace();
                    handler.removeCallbacks(null);
                    openLoadInterface.onError("BAD URL ENCODE");
                }
            }
        });
    }

    private static void setDesktopMode(WebView view) {
        final WebSettings webSettings = view.getSettings();

        final String newUserAgent = webSettings.getUserAgentString().replace("Mobile", "eliboM").replace("Android", "diordnA");

        webSettings.setUserAgentString(newUserAgent);
    }

    public interface OpenLoadInterface {
        void onSuccess(String url_final);

        void onError(String error);
    }

    public static class UrlGetter {
        private Handler handler;
        private OpenLoadInterface openLoadInterface;

        public UrlGetter(Handler handler, OpenLoadInterface openLoadInterface) {
            this.handler = handler;
            this.openLoadInterface = openLoadInterface;
        }

        @JavascriptInterface
        public void getUrl(String url) {
            Log.e("Url Openload Gotten", url);
            handler.removeCallbacks(null);
            openLoadInterface.onSuccess("https://openload.co/stream/" + url);
        }

        @JavascriptInterface
        public void getHtml(String html) {
            Log.e("Openload HTML", html);
        }
    }
}
