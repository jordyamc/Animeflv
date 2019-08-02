package knf.animeflv.VideoServers;

import android.content.Context;
import androidx.annotation.Nullable;

import org.jsoup.Jsoup;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import knf.animeflv.Cloudflare.BypassHolder;
import knf.animeflv.JsonFactory.Objects.Option;
import knf.animeflv.JsonFactory.Objects.VideoServer;
import knf.animeflv.Utils.KUtilsKt;

import static knf.animeflv.JsonFactory.Objects.VideoServer.Names.RV;

public class RVServer extends Server {
    public RVServer(Context context, String baseLink) {
        super(context, baseLink);
    }


    private String getRapidLink(String link) {
        Pattern pattern = Pattern.compile("\"(.*rapidvideo.*)\"");
        Matcher matcher = pattern.matcher(link);
        matcher.find();
        return matcher.group(1);
    }

    private String getRapidVideoLink(String link) {
        Pattern pattern = Pattern.compile("\"(http.*\\.mp4)\"");
        Matcher matcher = pattern.matcher(link);
        matcher.find();
        return matcher.group(1);
    }

    @Override
    public boolean isValid() {
        return baseLink.contains("rapidvideo") || baseLink.contains("&server=rv");
    }

    @Override
    public String getName() {
        return RV;
    }

    @Nullable
    @Override
    public VideoServer getVideoServer() {
        try {
            String down_link = KUtilsKt.extractLink(baseLink).replaceAll("&q=720p|&q=480p|&q=360p", "");
            if (down_link.contains("&server=rv"))
                down_link = getRapidLink(Jsoup.connect(down_link).cookies(BypassHolder.getBasicCookieMap()).userAgent(BypassHolder.getUserAgent()).get().outerHtml()).replaceAll("&q=720p|&q=480p|&q=360p", "");
            VideoServer videoServer = new VideoServer(RV);
            try {
                String jsoup720 = getRapidVideoLink(Jsoup.connect(down_link + "&q=720p#").data("block", "1").post().html());
                videoServer.addOption(new Option("720p", jsoup720));
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                String jsoup480 = getRapidVideoLink(Jsoup.connect(down_link + "&q=480p#").data("block", "1").post().html());
                videoServer.addOption(new Option("480p", jsoup480));
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                String jsoup360 = getRapidVideoLink(Jsoup.connect(down_link + "&q=360p#").data("block", "1").post().html());
                videoServer.addOption(new Option("360p", jsoup360));
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (videoServer.options.size() > 0)
                return videoServer;
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
