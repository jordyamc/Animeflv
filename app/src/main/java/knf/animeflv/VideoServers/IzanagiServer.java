package knf.animeflv.VideoServers;

import android.content.Context;
import androidx.annotation.Nullable;

import org.json.JSONObject;
import org.jsoup.Jsoup;

import knf.animeflv.Cloudflare.BypassHolder;
import knf.animeflv.JsonFactory.Objects.Option;
import knf.animeflv.JsonFactory.Objects.VideoServer;
import knf.animeflv.Utils.KUtilsKt;

import static knf.animeflv.JsonFactory.Objects.VideoServer.Names.IZANAGI;

/**
 * Created by Jordy on 24/12/2017.
 */

public class IzanagiServer extends Server {
    public IzanagiServer(Context context, String baseLink) {
        super(context, baseLink);
    }

    @Override
    public boolean isValid() {
        return baseLink.contains("s=izanagi");
    }

    @Override
    public String getName() {
        return VideoServer.Names.IZANAGI;
    }

    @Nullable
    @Override
    VideoServer getVideoServer() {
        String down_link = KUtilsKt.extractLink(baseLink);
        try {
            String link = new JSONObject(Jsoup.connect(down_link.replace("embed", "check")).userAgent(BypassHolder.getUserAgent()).cookies(BypassHolder.getBasicCookieMap()).get().body().text()).getString("file").replace("\\", "");
            link = link.replaceAll("/", "//").replace(":////", "://");
            return new VideoServer(IZANAGI, new Option(null, link));
        } catch (Exception e) {
            return null;
        }
    }
}
