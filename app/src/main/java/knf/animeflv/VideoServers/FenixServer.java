package knf.animeflv.VideoServers;

import android.content.Context;
import androidx.annotation.Nullable;

import org.json.JSONObject;
import org.jsoup.Jsoup;

import knf.animeflv.Cloudflare.BypassHolder;
import knf.animeflv.JsonFactory.Objects.Option;
import knf.animeflv.JsonFactory.Objects.VideoServer;
import knf.animeflv.Utils.KUtilsKt;

import static knf.animeflv.JsonFactory.Objects.VideoServer.Names.FENIX;

public class FenixServer extends Server {
    FenixServer(Context context, String baseLink) {
        super(context, baseLink);
    }

    @Override
    public boolean isValid() {
        return baseLink.contains("s=fenix");
    }

    @Override
    public String getName() {
        return FENIX;
    }

    @Nullable
    @Override
    public VideoServer getVideoServer() {
        try {
            String down_link = KUtilsKt.extractLink(baseLink);
            String link = new JSONObject(Jsoup.connect(down_link.replace("embed", "check")).cookies(BypassHolder.getBasicCookieMap()).userAgent(BypassHolder.getUserAgent()).get().body().text()).getString("file");
            return new VideoServer(FENIX, new Option(null, link));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
