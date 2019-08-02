package knf.animeflv.VideoServers;

import android.content.Context;
import androidx.annotation.Nullable;

import org.json.JSONObject;
import org.jsoup.Jsoup;

import knf.animeflv.Cloudflare.BypassHolder;
import knf.animeflv.JsonFactory.Objects.Option;
import knf.animeflv.JsonFactory.Objects.VideoServer;
import knf.animeflv.Utils.KUtilsKt;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static knf.animeflv.JsonFactory.Objects.VideoServer.Names.YOURUPLOAD;

/**
 * Created by Jordy on 24/12/2017.
 */

public class YUServer extends Server {
    public YUServer(Context context, String baseLink) {
        super(context, baseLink);
    }

    @Override
    public boolean isValid() {
        return baseLink.contains("yourupload.com");
    }

    @Override
    public String getName() {
        return VideoServer.Names.YOURUPLOAD;
    }

    @Nullable
    @Override
    VideoServer getVideoServer() {
        String down_link = KUtilsKt.extractLink(baseLink);
        try {
            String yulink = KUtilsKt.getYUvideoLink(down_link);
            OkHttpClient client = new OkHttpClient().newBuilder().followRedirects(false).build();
            Request request = new  Request.Builder()
                    .url(yulink)
                    .addHeader("Referer", down_link)
                    .build();
            Response response = client.newCall(request).execute();
            String refVideoLink = response.header("Location");
            response.close();
            return new VideoServer(YOURUPLOAD, new Option(null, new JSONObject(Jsoup.connect(down_link.replace("embed", "check")).userAgent(BypassHolder.getUserAgent()).cookies(BypassHolder.getBasicCookieMap()).get().body().text()).getString("file")));
        } catch (Exception e) {
            return null;
        }
    }
}
