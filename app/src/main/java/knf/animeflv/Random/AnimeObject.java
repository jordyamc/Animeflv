package knf.animeflv.Random;

import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

import knf.animeflv.Utils.FileUtil;

public class AnimeObject {
    public String title = "null";
    public String tid = "null";
    public String aid = "1";
    public boolean isAnime = false;
    public List<AnimeObject> objects;

    public AnimeObject(String aid) {
        isAnime = true;
        String file_loc = Environment.getExternalStorageDirectory() + "/Animeflv/cache/directorio.txt";
        File file = new File(file_loc);
        if (file.exists()) {
            try {
                Log.d("Random", aid);
                JSONObject jsonObj = new JSONObject(FileUtil.getStringFromFile(file_loc));
                JSONArray jsonArray = jsonObj.getJSONArray("lista");
                int position = Integer.parseInt(aid);
                JSONObject nombreJ = jsonArray.getJSONObject(position <= jsonArray.length() ? position : RandomHelper.getRandomNumber());
                this.aid = nombreJ.getString("a");
                title = FileUtil.corregirTit(nombreJ.getString("b"));
                tid = nombreJ.getString("c");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public AnimeObject(String aid, String title, String tid) {
        this.aid = aid;
        this.title = title;
        this.tid = tid;
        this.isAnime = true;
    }

    public AnimeObject(String title, boolean isHeader, @Nullable List<AnimeObject> objects) {
        this.title = title;
        this.isAnime = !isHeader;
        this.objects = objects;

    }
}
