package knf.animeflv.FavSync;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import knf.animeflv.Favorites.FavotiteDB;
import knf.animeflv.LoginActivity.DropboxManager;

/**
 * Created by Jordy on 19/07/2017.
 */

public class FavSyncHelper {
    public static List<FavSection> local = new ArrayList<>();
    public static List<FavSection> cloud = new ArrayList<>();
    public static boolean isSame = false;

    private static void populate(int type, JSONObject object) {
        List<FavSection> list = new ArrayList<>();
        try {
            JSONArray array = object.getJSONArray("favs");
            for (int i = 0; i < array.length(); i++) {
                String section_name = array.getJSONObject(i).getString("name");
                JSONArray favs_list = array.getJSONObject(i).getJSONArray("list");
                List<FavObject> objects = new ArrayList<>();
                for (int o = 0; o < favs_list.length(); o++) {
                    JSONObject fav = favs_list.getJSONObject(o);
                    objects.add(new FavObject(fav.getString("title"), fav.getString("aid"), fav.getString("section"), fav.getInt("order")));
                }
                list.add(new FavSection(section_name, objects));
            }
            if (type == 0) {
                local = list;
            } else {
                cloud = list;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean same() {
        boolean same = true;
        for (int i = 0; i < cloud.size(); i++) {
            FavSection l = local.get(i);
            FavSection c = cloud.get(i);
            if (!c.name.equals(l.name) || !(c.list.size() == l.list.size())) {
                same = false;
                break;
            }
        }
        return same;
    }

    public static List<knf.animeflv.Favorites.FavObject> getResolved(List<FavSection> list) {
        List<knf.animeflv.Favorites.FavObject> objects = new ArrayList<>();
        for (FavSection section : list) {
            objects.add(new knf.animeflv.Favorites.FavObject(section.name));
            for (FavObject object : section.list) {
                objects.add(new knf.animeflv.Favorites.FavObject(object.title, object.aid, object.section, object.order));
            }
        }
        return objects;
    }

    private static void compareList() {
        isSame = ((local.size() == cloud.size()) && same());
    }

    public static void recreate(Context context, final SyncListener listener) {
        populate(0, new FavotiteDB(context).getDBJSON(true));
        DropboxManager.downloadFavs(context, new DropboxManager.DownloadCallback() {
            @Override
            public void onDownload(JSONObject result, boolean success) {
                if (success) {
                    populate(1, result);
                } else {
                    cloud = new ArrayList<>();
                }
                compareList();
                listener.onSync();
            }
        });
    }

    public interface SyncListener {
        void onSync();
    }
}
