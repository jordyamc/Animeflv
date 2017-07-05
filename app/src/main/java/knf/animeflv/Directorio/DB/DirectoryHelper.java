package knf.animeflv.Directorio.DB;

import android.content.Context;

import java.util.List;

import knf.animeflv.Directorio.AnimeClass;

public class DirectoryHelper {
    private Context context;

    private DirectoryHelper(Context context) {
        this.context = context;
    }

    public static DirectoryHelper get(Context context) {
        return new DirectoryHelper(context);
    }

    public DirectoryDB.DirectoryItem getAnime(String aid) {
        return new DirectoryDB(context).getAnimeByAid(aid);
    }

    public String getTitle(String aid) {
        return new DirectoryDB(context).getTitleByAid(aid);
    }

    public String getEpUrl(String eid, String sid) {
        return new DirectoryDB(context).getURLEpByEid(eid, sid);
    }

    public String getEpUrl(String aid, String num, String sid) {
        return new DirectoryDB(context).getURLEpByEid(aid, num, sid);
    }

    public String getType(String aid) {
        return new DirectoryDB(context).getTypeByAid(aid);
    }

    public String getAnimeUrl(String aid) {
        return new DirectoryDB(context).getURLAnimeByAid(aid);
    }

    public String getAid(String lid) {
        return new DirectoryDB(context).getAidByLID(lid);
    }

    public List<AnimeClass> searchName(String query) {
        return new DirectoryDB(context).searchName(query);
    }

    public List<AnimeClass> searchID(String query) {
        return new DirectoryDB(context).searchID(query);
    }

    public List<AnimeClass> searchGenres(String query) {
        return new DirectoryDB(context).searchGenres(query);
    }

    public List<AnimeClass> getAllType(String type) {
        return new DirectoryDB(context).getAllByType(type, true);
    }

    public synchronized List<AnimeClass> getAll() {
        return new DirectoryDB(context).getAll(true);
    }

    public boolean containsAnime(String aid) {
        return new DirectoryDB(context).animedExist(aid, true);
    }

    public int lastAid() {
        return new DirectoryDB(context).getLastAid();
    }

    public boolean isDirectoryValid() {
        return !new DirectoryDB(context).isDBEmpty(true);
    }
}
