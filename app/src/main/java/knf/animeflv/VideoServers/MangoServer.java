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

public class MangoServer extends Server {

    public MangoServer(Context context, String baseLink) {
        super(context, baseLink);
    }

    @Override
    public boolean isValid() {
        return baseLink.contains("streamango.com") && !baseLink.contains("%");
    }

    @Override
    public String getName() {
        return VideoServer.Names.MANGO;
    }

    @Nullable
    @Override
    VideoServer getVideoServer() {
        try {
            String down_link = KUtilsKt.extractLink(baseLink);
            String html = Jsoup.connect(down_link).get().html();
            Matcher matcher = Pattern.compile("type:\"video/mp4\",src:d\\('([^']+)',(\\d+)\\)").matcher(html);
            matcher.find();
            String hash = matcher.group(1);
            int key = Integer.parseInt(matcher.group(2));
            String file = KDecoder.Companion.decodeMango(hash, key);
            if (file == null || file.trim().equals(""))
                return null;
            else if (file.startsWith("//"))
                file = file.replaceFirst("//", "https://");
                return new VideoServer(getName(),new Option(null,file));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String mangoLink(String html) {
        Matcher matcher = Pattern.compile("\"(https.*streamango\\.com[/a-z]+)\"").matcher(html);
        matcher.find();
        return matcher.group(1);
    }
}
