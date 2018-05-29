package knf.animeflv.VideoServers;

import android.content.Context;
import android.support.annotation.Nullable;

import java.net.URLDecoder;

import knf.animeflv.JsonFactory.Objects.Option;
import knf.animeflv.JsonFactory.Objects.VideoServer;

import static knf.animeflv.JsonFactory.Objects.VideoServer.Names.MEGA;

/**
 * Created by Jordy on 24/12/2017.
 */

public class MegaServer extends Server {
    public MegaServer(Context context, String baseLink) {
        super(context, baseLink);
    }

    @Override
    public boolean isValid() {
        return baseLink.contains("mega.nz") && !baseLink.contains("embed");
    }

    @Override
    public String getName() {
        return VideoServer.Names.MEGA;
    }

    @Nullable
    @Override
    VideoServer getVideoServer() {
        try {
            return new VideoServer(MEGA, new Option(null, URLDecoder.decode(baseLink, "utf-8")));
        } catch (Exception e) {
            return null;
        }
    }
}
