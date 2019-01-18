package knf.animeflv.VideoServers;

import android.content.Context;
import android.support.annotation.Nullable;

import org.json.JSONObject;
import org.jsoup.Jsoup;

import knf.animeflv.Cloudflare.BypassHolder;
import knf.animeflv.JsonFactory.Objects.Option;
import knf.animeflv.JsonFactory.Objects.VideoServer;
import knf.animeflv.Utils.KUtilsKt;

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
        return baseLink.contains("server=yourupload");
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
            return new VideoServer(YOURUPLOAD, new Option(null, new JSONObject(Jsoup.connect(down_link.replace("embed", "check")).userAgent(BypassHolder.getUserAgent()).cookies(BypassHolder.getBasicCookieMap()).get().body().text()).getString("file")));
        } catch (Exception e) {
            return null;
        }
    }
}
