package knf.animeflv.zippy;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;
import knf.animeflv.DownloadManager.CookieConstructor;
import knf.animeflv.JsonFactory.ServerGetter;
import knf.animeflv.Utils.NoLogInterface;

public class zippyHelper {
    public static void calculate(final String u, final OnZippyResult callback) {
        AsyncHttpClient client = ServerGetter.getClient();
        client.setLogInterface(new NoLogInterface());
        client.get(u, null, new TextHttpResponseHandler() {
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
                        String url = URLDecoder.decode(u, "utf-8");
                        Document document = Jsoup.parse(responseString);
                        Element center = document.select("div.center").first();
                        Element script = center.select("script").get(1);
                        String script_text = script.outerHtml().replace("<script type=\"text/javascript\">", "");

                        Matcher matcher = Pattern.compile("\\D*(\\d*)%(\\d*)\\D*").matcher(script_text);
                        matcher.find();
                        String a = String.valueOf((Integer.parseInt(matcher.group(1)) % Integer.parseInt(matcher.group(2))));
                        Matcher name = Pattern.compile(".*\\/d\\/([a-zA-Z]*)\\/.*\\/(\\d+_\\d+\\.mp4).*").matcher(script_text);
                        name.find();
                        String pre = name.group(1);
                        String d_url = url.substring(0, url.indexOf("/v/")) + "/d/" + pre + "/" + a + "/" + name.group(2);
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

    private static int generateNumber(int a, int b, int c) {
        return ((int) ((a % b) + (a % c)));
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
