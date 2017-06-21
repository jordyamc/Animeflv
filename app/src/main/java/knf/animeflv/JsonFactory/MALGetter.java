package knf.animeflv.JsonFactory;

import android.os.Looper;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.SyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import cz.msebera.android.httpclient.Header;
import knf.animeflv.Utils.NetworkUtils;

/**
 * Created by Jordy on 05/01/2017.
 */

public class MALGetter {
    private static int TIMEOUT = 3000;

    public static AsyncHttpClient getClient() {
        if (Looper.myLooper() == null) {
            return new SyncHttpClient();
        } else {
            return new AsyncHttpClient();
        }
    }

    public static void getAnimeSearch(final String title, final SearchInterface searchInterface) {
        if (NetworkUtils.isNetworkAvailable()) {
            AsyncHttpClient client = getClient();
            client.setBasicAuth("animeflvapp", "cLSmWldhYdSO");
            client.setTimeout(TIMEOUT);
            client.get("https://myanimelist.net/api/anime/search.xml?q=" + getTitleEncoded(title.toLowerCase()), null, new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    searchInterface.onFinishSearch("_error: " + responseString, false);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    searchInterface.onFinishSearch(responseString, true);
                }
            });
        } else {
            searchInterface.onFinishSearch("_error: No internet", false);
        }
    }

    public static String parseImageHtml(String xml, String org_title) {
        try {
            Document document = Jsoup.parse(xml, "", Parser.xmlParser());
            Elements list = document.select("entry");
            for (Element entry : list) {
                if (org_title.equalsIgnoreCase(entry.select("title").first().ownText())) {
                    return entry.select("image").first().ownText();
                }
            }
            return "_error: Anime Not Found";
        } catch (Exception e) {
            e.printStackTrace();
            return "_error: Parsing";
        }
    }

    public static String parseStartDate(String xml, String org_title) {
        try {
            Document document = Jsoup.parse(xml, "", Parser.xmlParser());
            Elements list = document.select("entry");
            for (Element entry : list) {
                if (org_title.equalsIgnoreCase(entry.select("title").first().ownText())) {
                    return entry.select("start_date").first().ownText();
                }
            }
            return "_error: Anime Not Found";
        } catch (Exception e) {
            e.printStackTrace();
            return "_error: Parsing";
        }
    }

    private static String getTitleEncoded(String s) {
        try {
            return URLEncoder.encode(s, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "_error: Url format";
        }
    }

    public interface SearchInterface {
        void onFinishSearch(String result, boolean success);
    }
}
