package knf.animeflv.VideoServers;

import android.content.Context;
import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import knf.animeflv.Cloudflare.BypassHolder;
import knf.animeflv.JsonFactory.Objects.Option;
import knf.animeflv.JsonFactory.Objects.VideoServer;
import knf.animeflv.Utils.KUtilsKt;

import static knf.animeflv.JsonFactory.Objects.VideoServer.Names.HYPERION;

/**
 * Created by Jordy on 24/12/2017.
 */

public class HyperionServer extends Server {
    public HyperionServer(Context context, String baseLink) {
        super(context, baseLink);
    }

    @Override
    public boolean isValid() {
        return baseLink.contains("server=hyperion");
    }

    @Override
    public String getName() {
        return VideoServer.Names.HYPERION;
    }

    @Nullable
    @Override
    VideoServer getVideoServer() {
        String down_link = KUtilsKt.extractLink(baseLink);
        try {
            JSONArray array = new JSONObject(Jsoup.connect(down_link.replace("embed_hyperion", "check")).userAgent(BypassHolder.getUserAgent()).cookies(BypassHolder.getBasicCookieMap()).get().body().text()).getJSONArray("streams");
            VideoServer videoServer = new VideoServer(HYPERION);
            for (int i = 0; i < array.length(); i++) {
                switch (array.getJSONObject(i).getInt("label")) {
                    case 360:
                        videoServer.addOption(new Option("360p", array.getJSONObject(i).getString("file")));
                        break;
                    case 480:
                        videoServer.addOption(new Option("480p", array.getJSONObject(i).getString("file")));
                        break;
                    case 720:
                        videoServer.addOption(new Option("720p", array.getJSONObject(i).getString("file")));
                        break;
                }
            }
            videoServer.addOption(new Option("Direct", new JSONObject(Jsoup.connect(down_link.replace("embed_hyperion", "check")).userAgent(BypassHolder.getUserAgent()).cookies(BypassHolder.getBasicCookieMap()).get().body().text()).getString("direct")));
            return videoServer;
        } catch (Exception e) {
            return null;
        }
    }
}
