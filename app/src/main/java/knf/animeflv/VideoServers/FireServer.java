package knf.animeflv.VideoServers;

import android.content.Context;
import android.support.annotation.Nullable;

import org.jsoup.Jsoup;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import knf.animeflv.Cloudflare.BypassHolder;
import knf.animeflv.JsonFactory.Objects.Option;
import knf.animeflv.JsonFactory.Objects.VideoServer;
import knf.animeflv.Utils.KUtilsKt;

import static knf.animeflv.JsonFactory.Objects.VideoServer.Names.FIRE;

/**
 * Created by Jordy on 24/12/2017.
 */

public class FireServer extends Server {
    public FireServer(Context context, String baseLink) {
        super(context, baseLink);
    }

    @Override
    public boolean isValid() {
        return baseLink.contains("efire.php");
    }

    @Override
    public String getName() {
        return VideoServer.Names.FIRE;
    }

    @Nullable
    @Override
    VideoServer getVideoServer() {
        try {
            String func = KUtilsKt.extractLink(baseLink);
            String media_func = Jsoup.connect(func).userAgent(BypassHolder.getUserAgent()).cookies(BypassHolder.getBasicCookieMap()).get().select("script").last().outerHtml();
            String download = Jsoup.connect(extractMediaLink(media_func)).get().select("a[href~=http://download.*]").first().attr("href");
            return new VideoServer(FIRE, new Option(null, download));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String extractMediaLink(String html) {
        Matcher matcher = Pattern.compile("www\\.mediafire[a-zA-Z0-a.=?/&%]+").matcher(html);
        matcher.find();
        return "https://" + matcher.group().replace("%2F", "/");
    }
}
