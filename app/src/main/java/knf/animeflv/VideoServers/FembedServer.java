package knf.animeflv.VideoServers;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import knf.animeflv.Cloudflare.BypassHolder;
import knf.animeflv.JsonFactory.Objects.Option;
import knf.animeflv.JsonFactory.Objects.VideoServer;
import knf.animeflv.Utils.KUtilsKt;

import static knf.animeflv.JsonFactory.Objects.VideoServer.Names.FEMBED;

public class FembedServer extends Server {
    FembedServer(Context context, String baseLink) {
        super(context, baseLink);
    }

    @Override
    public boolean isValid() {
        return baseLink.contains("fembed.com");
    }

    @Override
    public String getName() {
        return FEMBED;
    }

    @Nullable
    @Override
    public VideoServer getVideoServer() {
        try {
            String down_link = KUtilsKt.extractLink(baseLink);
            JSONObject json = new JSONObject(KUtilsKt.executeOkHttpCookies(context,down_link.replace("/v/", "/api/source/"),"POST").body().string());
            if (!json.getBoolean("success"))
                throw new IllegalStateException("Request was not succeeded");
            JSONArray array = json.getJSONArray("data");
            List<Option> options = new ArrayList<>();
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                options.add(new Option(obj.getString("label"), obj.getString("file")));
            }
            return new VideoServer(getName(), options);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
