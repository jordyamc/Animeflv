package knf.animeflv.Random;

import android.content.Context;
import android.support.annotation.Nullable;

import java.util.List;

import knf.animeflv.Directorio.AnimeClass;
import knf.animeflv.Directorio.DB.DirectoryHelper;

public class AnimeObject {
    public String title = "null";
    public String tid = "null";
    public String aid = "1";
    public boolean isAnime = false;
    public List<AnimeObject> objects;

    public AnimeObject(Context context, String aid) {
        isAnime = true;
        if (DirectoryHelper.get(context).isDirectoryValid()) {
            try {
                List<AnimeClass> list = DirectoryHelper.get(context).getAll();
                int position = Integer.parseInt(aid);
                AnimeClass animeClass = list.get(position <= list.size() ? position : RandomHelper.getRandomNumber(context));
                this.aid = animeClass.getAid();
                title = animeClass.getNombre();
                tid = animeClass.getTipo();
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
