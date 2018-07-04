package knf.animeflv.VideoServers;

import android.content.Context;
import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import knf.animeflv.Cloudflare.BypassHolder;
import knf.animeflv.JsonFactory.Objects.Option;
import knf.animeflv.JsonFactory.Objects.VideoServer;

import static knf.animeflv.JsonFactory.Objects.VideoServer.Names.OKRU;

/**
 * Created by Jordy on 24/12/2017.
 */

public class OkruServer extends Server {
    public OkruServer(Context context, String baseLink) {
        super(context, baseLink);
    }

    @Override
    public boolean isValid() {
        return baseLink.contains("ok.ru");
    }

    @Override
    public String getName() {
        return VideoServer.Names.OKRU;
    }

    @Nullable
    @Override
    VideoServer getVideoServer() {
        try {
            String frame = baseLink.substring(baseLink.indexOf("'") + 1, baseLink.lastIndexOf("'"));
            String down_link = Jsoup.parse(frame).select("iframe").first().attr("src");
            String true_link = extractOkruLink(Jsoup.connect(down_link).userAgent(BypassHolder.getUserAgent()).cookies(BypassHolder.getBasicCookieMap()).get().select("script").last().html());
            String e_json = Jsoup.connect(true_link).get().select("div[data-module='OKVideo']").first().attr("data-options");
            String cut_json = "{" + e_json.substring(e_json.lastIndexOf("\\\"videos"), e_json.indexOf(",\\\"metadataEmbedded")).replace("\\&quot;", "\"").replace("\\u0026", "&").replace("\\", "").replace("%3B", ";") + "}";
            JSONArray array = new JSONObject(cut_json).getJSONArray("videos");
            VideoServer videoServer = new VideoServer(OKRU);
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                switch (object.getString("name")) {
                    case "hd":
                        videoServer.addOption(new Option("HD", object.getString("url")));
                        break;
                    case "sd":
                        videoServer.addOption(new Option("SD", object.getString("url")));
                        break;
                    case "low":
                        videoServer.addOption(new Option("LOW", object.getString("url")));
                        break;
                    case "lowest":
                        videoServer.addOption(new Option("LOWEST", object.getString("url")));
                        break;
                    case "mobile":
                        videoServer.addOption(new Option("MOBILE", object.getString("url")));
                        break;
                }
            }
            return videoServer;
        } catch (Exception e) {
            return null;
        }
    }

    private String extractOkruLink(String html) {
        Matcher matcher = Pattern.compile("\"(https://ok\\.ru.*)\"").matcher(html);
        matcher.find();
        return matcher.group(1);
    }
}
