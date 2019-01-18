package knf.animeflv.VideoServers;

import android.content.Context;
import android.support.annotation.Nullable;

import org.json.JSONObject;
import org.jsoup.Jsoup;

import knf.animeflv.Cloudflare.BypassHolder;
import knf.animeflv.JsonFactory.Objects.Option;
import knf.animeflv.JsonFactory.Objects.VideoServer;
import knf.animeflv.Utils.KUtilsKt;

public class Mp4UploadServer extends Server {

    public Mp4UploadServer(Context context, String baseLink) {
        super(context, baseLink);
    }

    @Override
    public boolean isValid() {
        return baseLink.contains("s=mp4upload");
    }

    @Override
    public String getName() {
        return VideoServer.Names.MP4UPLOAD;
    }

    @Nullable
    @Override
    VideoServer getVideoServer() {
        try {
            String downLink = KUtilsKt.extractLink(baseLink);
            String link = new JSONObject(Jsoup.connect(downLink.replace("embed", "check")).userAgent(BypassHolder.getUserAgent()).cookies(BypassHolder.getBasicCookieMap()).get().body().text()).getString("file");
            return new VideoServer(getName(),new Option(null,link));
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
