package knf.animeflv.Random;

import android.os.Environment;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;

import knf.animeflv.Utils.FileUtil;

public class AnimeObject {
    public String title = "null";
    public String tid = "null";
    public String aid = "1";

    public AnimeObject(String aid) {
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
}
