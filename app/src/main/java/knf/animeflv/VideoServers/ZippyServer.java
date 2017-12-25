package knf.animeflv.VideoServers;

import android.content.Context;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.net.URLDecoder;

import knf.animeflv.JsonFactory.Objects.Option;
import knf.animeflv.JsonFactory.Objects.VideoServer;

/**
 * Created by Jordy on 24/12/2017.
 */

public class ZippyServer extends Server {

    public ZippyServer(Context context, String baseLink) {
        super(context, baseLink);
    }

    @Override
    public boolean isValid() {
        return baseLink.contains("zippyshare");
    }

    @Override
    public String getName() {
        return VideoServer.Names.ZIPPYSHARE;
    }

    @Override
    VideoServer getVideoServer() {
        try {
            URLDecoder.decode(baseLink, "utf-8");
            Document zi = Jsoup.connect(baseLink).timeout(TIMEOUT).get();
            String t = zi.select("meta[property='og:title']").attr("content");
            if (!t.trim().equals(""))
                return new VideoServer(getName(), new Option(null, baseLink));
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}
