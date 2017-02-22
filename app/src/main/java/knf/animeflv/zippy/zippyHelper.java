package knf.animeflv.zippy;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import cz.msebera.android.httpclient.Header;
import knf.animeflv.DownloadManager.CookieConstructor;
import knf.animeflv.JsonFactory.ServerGetter;
import knf.animeflv.Utils.NoLogInterface;

public class zippyHelper {
    public static void calculate(final String url, final OnZippyResult callback) {
        AsyncHttpClient client = ServerGetter.getClient();
        client.setLogInterface(new NoLogInterface());
        client.get(url, null, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e("Zippy Calculate", "Error - Status: " + statusCode + " Response: " + responseString);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                String cookies = null;
                for (Header header : headers) {
                    if (header.getName().equals("Set-Cookie") && header.getValue().contains("JSESSIONID")) {
                        cookies = header.getValue();
                        break;
                    }
                }
                if (cookies != null) {
                    try {
                        Document document = Jsoup.parse(responseString);
                        Element center = document.select("div.center").first();
                        Element script = center.select("script").get(1);
                        String script_text = script.outerHtml().replace("<script type=\"text/javascript\">", "");
                        String[] values = script_text.split(";");
                        int a = Integer.parseInt(values[0].trim().replace("var a = ", ""));
                        int b = Integer.parseInt(values[1].trim().replace("var b = ", ""));
                        boolean isf = script_text.contains(".omg =");
                        String pre = script_text.substring(script_text.indexOf("/d/") + 3, script_text.indexOf("/\"+(a"));
                        String d_url = url.substring(0, url.indexOf("/v/")) + "/d/" + pre + "/" + generateNumber(a, b, isf) + "/" + script_text.substring(script_text.indexOf("+\"/") + 3, script_text.indexOf(".mp4\";")) + ".mp4";
                        Log.e("Zippy Download", d_url);
                        callback.onSuccess(new zippyObject(d_url, new CookieConstructor(cookies, System.getProperty("http.agent"), url)));
                    } catch (Exception e) {
                        e.printStackTrace();
                        callback.onError();
                    }
                } else {
                    callback.onError();
                }
            }
        });
    }

    private static int generateNumber(int a, int b, boolean isf) {
        double c;
        if (isf) {
            c = Math.floor(a / 3);
        } else {
            c = Math.ceil(a / 3);
        }
        return ((int) (c + (a % b)));
    }

    public interface OnZippyResult {
        void onSuccess(zippyObject object);

        void onError();
    }

    public static class zippyObject {
        public String download_url;
        public CookieConstructor cookieConstructor;

        public zippyObject(String url, CookieConstructor cookieConstructor) {
            this.download_url = url;
            this.cookieConstructor = cookieConstructor;
        }
    }
}
