package knf.animeflv.VideoServers;

import android.content.Context;
import android.support.annotation.Nullable;

import org.jsoup.Jsoup;

import knf.animeflv.JsonFactory.Objects.Option;
import knf.animeflv.JsonFactory.Objects.VideoServer;

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
            String frame = baseLink.substring(baseLink.indexOf("'") + 1, baseLink.lastIndexOf("'"));
            String func = Jsoup.parse(frame).select("img").first().attr("onclick");
            String download_link = func.substring(func.indexOf("open(\"") + 6, func.indexOf("\","));
            String media_func = Jsoup.connect(download_link).get().select("script").last().outerHtml();
            String download = Jsoup.connect(media_func.substring(media_func.indexOf("get('") + 5, media_func.indexOf("', function"))).get().select("div.download_link a").first().attr("href");
            return new VideoServer(FIRE, new Option(null, download));
        } catch (Exception e) {
            return null;
        }
    }
}
