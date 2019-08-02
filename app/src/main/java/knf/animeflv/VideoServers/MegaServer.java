package knf.animeflv.VideoServers;

import android.content.Context;

import java.net.URLDecoder;

import androidx.annotation.Nullable;
import knf.animeflv.JsonFactory.Objects.Option;
import knf.animeflv.JsonFactory.Objects.VideoServer;
import knf.animeflv.Utils.KUtilsKt;

/**
 * Created by Jordy on 24/12/2017.
 */

public class MegaServer extends Server {
    private String DOWNLOAD = "1";
    private String STREAM = "2";
    public MegaServer(Context context, String baseLink) {
        super(context, baseLink);
    }

    @Override
    public boolean isValid() {
        return baseLink.contains("mega.nz");
    }

    @Override
    public String getName() {
        return VideoServer.Names.MEGA + " "+ getType();
    }

    public String getType() {
        if (baseLink.contains("mega.nz") && !baseLink.contains("embed"))
            return DOWNLOAD;
        else
            return STREAM;
    }

    @Nullable
    @Override
    VideoServer getVideoServer() {
        try {
            if (getType().equals(STREAM))
                return new VideoServer(getName(),new Option(null, KUtilsKt.extractLink(baseLink)));
            else
                return new VideoServer(getName(),new Option(null, URLDecoder.decode(baseLink,"utf-8")));
        } catch (Exception e) {
            return null;
        }
    }
}
