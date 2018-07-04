package knf.animeflv.VideoServers;

import android.content.Context;
import android.support.annotation.Nullable;

import org.jsoup.Jsoup;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import knf.animeflv.Cloudflare.BypassHolder;
import knf.animeflv.JsonFactory.Objects.Option;
import knf.animeflv.JsonFactory.Objects.VideoServer;

import static knf.animeflv.JsonFactory.Objects.VideoServer.Names.RV;

/**
 * Created by Jordy on 24/12/2017.
 */

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
    public String getName() {
        return VideoServer.Names.RV;
    }

    @Override
    public boolean isValid() {
        return baseLink.contains("rapidvideo") || baseLink.contains("&server=rv");
    }

    @Nullable
    @Override
    VideoServer getVideoServer() {
        try {
            String frame = baseLink.substring(baseLink.indexOf("'") + 1, baseLink.lastIndexOf("'"));
            String down_link = Jsoup.parse(frame).select("iframe").first().attr("src").replaceAll("&q=720p|&q=480p|&q=360p", "");
            if (down_link.contains("&server=rv"))
                down_link = getRapidLink(Jsoup.connect(down_link).userAgent(BypassHolder.getUserAgent()).cookies(BypassHolder.getBasicCookieMap()).get().outerHtml()).replace("&q=720p", "");
            VideoServer videoServer = new VideoServer(RV);
            try {
                String jsoup720 = getRapidVideoLink(Jsoup.connect(down_link + "&q=720p").get().html());
                videoServer.addOption(new Option("720p", jsoup720));
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                String jsoup480 = getRapidVideoLink(Jsoup.connect(down_link + "&q=480p").get().html());
                videoServer.addOption(new Option("480p", jsoup480));
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                String jsoup360 = getRapidVideoLink(Jsoup.connect(down_link + "&q=360p").get().html());
                videoServer.addOption(new Option("360p", jsoup360));
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (videoServer.options.size() > 0)
                return videoServer;
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}
