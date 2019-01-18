package knf.animeflv.VideoServers;

import android.content.Context;
import android.support.annotation.Nullable;

import org.jsoup.Jsoup;

import knf.animeflv.JsonFactory.Objects.Option;
import knf.animeflv.JsonFactory.Objects.VideoServer;
import knf.animeflv.Utils.KUtilsKt;

/**
 * Created by Jordy on 24/12/2017.
 */

public class MegaServer extends Server {
    public MegaServer(Context context, String baseLink) {
        super(context, baseLink);
    }

    @Override
    public boolean isValid() {
        return baseLink.contains("server=mega");
    }

    @Override
    public String getName() {
        return VideoServer.Names.MEGA;
    }

    @Nullable
    @Override
    VideoServer getVideoServer() {
        try {
            String down_link = KUtilsKt.extractLink(baseLink);
            String link = "https://mega.nz/#" + down_link.substring(down_link.lastIndexOf("!"));
            return new VideoServer(getName(), new Option(null, link));
        } catch (Exception e) {
            return null;
        }
    }
}
