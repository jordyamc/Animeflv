package knf.animeflv.Favorites;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import androidx.annotation.Nullable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import knf.animeflv.Directorio.DB.DirectoryHelper;
import knf.animeflv.FavSyncro;
import knf.animeflv.Favorites.comparators.favComparator;
import knf.animeflv.Favorites.comparators.favNoSectionAidComparator;
import knf.animeflv.Favorites.comparators.favNoSectionNameComparator;
import knf.animeflv.Favorites.comparators.sectionComparator;
import knf.animeflv.Utils.ExecutorManager;
import xdroid.toaster.Toaster;

public class FavotiteDB {
    public static final int TYPE_SECTION = 0;
    public static final int TYPE_FAV = 1;
    public static final String NO_SECTION = "Sin Clasificar";
    private static final String TABLE_NAME = "FavsTable";
    private static final String KEY_TABLE_ID = "_table_id";
    private static final String KEY_TYPE_ID = "_type";
    private static final String KEY_NAME = "_name";
    private static final String KEY_AID = "_aid";
    private static final String KEY_SECTION = "_section";
    private static final String KEY_ORDER = "_order";
    private static final String DATABASE_CREATE =
            "create table if not exists " + TABLE_NAME + "("
                    + KEY_TABLE_ID + " integer primary key autoincrement, "
                    + KEY_TYPE_ID + " integer, "
                    + KEY_NAME + " text not null, "
                    + KEY_AID + " text, "
                    + KEY_SECTION + " text, "
                    + KEY_ORDER + " integer"
                    + ");";
    private static final String DATABASE_NAME = "FAVORITE_ANIME_LIST";
    protected Context context;
    private SQLiteDatabase db;

    public FavotiteDB(Context context) {
        this.context = context;
        set();
    }

    public static String[] getStringArray(List<String> list) {
        String[] array = new String[list.size()];
        list.toArray(array);
        return array;
    }

    private void set() {
        try {
            db = context.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE, null, null);
            db.execSQL(DATABASE_CREATE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            db.close();
        } catch (NullPointerException e) {
            Log.e("On Close DB", "DB is Null!!!!!");
        }
    }

    public FavotiteDB addFav(FavObject object) {
        try {
            if (!isFavorite(object.aid, false)) {
                if (!existSection(object.section, false))
                    addSection(new FavObject(object.section));
                ContentValues values = new ContentValues();
                values.put(KEY_TYPE_ID, TYPE_FAV);
                values.put(KEY_NAME, URLEncoder.encode(object.name, "utf-8"));
                values.put(KEY_AID, object.aid);
                values.put(KEY_SECTION, URLEncoder.encode(object.section, "utf-8"));
                values.put(KEY_ORDER, object.order);
                db.insert(TABLE_NAME, null, values);
                Log.e("FavDB", "Add Fav: " + object.name + " Aid: " + object.aid + " Section: " + object.section);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    public void addFav(final FavObject object, @Nullable final updateDataInterface dataInterface) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    if (!isFavorite(object.aid, false)) {
                        if (!existSection(object.section, false))
                            addSection(new FavObject(object.section));
                        ContentValues values = new ContentValues();
                        values.put(KEY_TYPE_ID, TYPE_FAV);
                        values.put(KEY_NAME, URLEncoder.encode(object.name, "utf-8"));
                        values.put(KEY_AID, object.aid);
                        values.put(KEY_SECTION, URLEncoder.encode(object.section, "utf-8"));
                        if (object.order == -1) {
                            values.put(KEY_ORDER, getSection(object.section).sectionList.size());
                        } else {
                            values.put(KEY_ORDER, object.order);
                        }
                        db.insert(TABLE_NAME, null, values);
                        Log.e("FavDB", "Add Fav: " + object.name + " Aid: " + object.aid + " Section: " + object.section);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                close();
                if (dataInterface != null)
                    dataInterface.onFinish();
                return null;
            }
        }.executeOnExecutor(ExecutorManager.getExecutor());
    }

    public FavotiteDB addSection(FavObject object) {
        try {
            if (!existSection(object.name, false)) {
                ContentValues values = new ContentValues();
                values.put(KEY_TYPE_ID, TYPE_SECTION);
                values.put(KEY_NAME, URLEncoder.encode(object.name, "utf-8"));
                db.insert(TABLE_NAME, null, values);
                Log.e("FavDB", "Add Section: " + object.name);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    public void deleteFav(final String aid, final updateDataInterface dataInterface) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    FavObject now = getFav(aid);
                    List<FavObject> old_sec = getSection(now.section).sectionList;
                    for (int i = now.order; i < old_sec.size(); i++) {
                        FavObject current = old_sec.get(i);
                        if (!current.aid.equals(aid)) {
                            changeFav(current.aid, current.order - 1, current.section);
                        }
                    }
                    db.delete(TABLE_NAME, KEY_AID + "='" + aid + "'", null);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toaster.toast("Error al eliminar favorito");
                }
                dataInterface.onFinish();
                return null;
            }
        }.executeOnExecutor(ExecutorManager.getExecutor());
    }

    public void deleteSection(final String section, final updateDataInterface dataInterface) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    FavObject object = getSection(section);
                    if (object.isSection) {
                        List<FavObject> others = getSection(NO_SECTION).sectionList;
                        for (FavObject fav : object.sectionList) {
                            changeFav(fav.aid, others.size(), NO_SECTION);
                            others.add(fav);
                        }
                        others = null;
                        db.delete(TABLE_NAME, KEY_TYPE_ID + "='" + TYPE_SECTION + "' and " + KEY_NAME + "='" + URLEncoder.encode(section, "utf-8") + "'", null);
                    } else {
                        Log.e("Section Error", "Not Section: " + section);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dataInterface.onFinish();
                return null;
            }
        }.executeOnExecutor(ExecutorManager.getExecutor());
    }

    public boolean isFavorite(String aid, boolean close) {
        try {
            Cursor c = db.query(TABLE_NAME, new String[]{
                    KEY_TYPE_ID
            }, KEY_AID + "='" + aid + "'", null, null, null, null);
            if (c.getCount() > 0) {
                while (c.moveToNext()) {
                    if (c.getInt(0) == TYPE_FAV) {
                        c.close();
                        if (close)
                            close();
                        return true;
                    }
                }
            }
            c.close();
            if (close)
                close();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            if (close)
                close();
            return false;
        }
    }

    public boolean existSection(String name, boolean close) {
        try {
            Cursor c = db.query(TABLE_NAME, new String[]{
                    KEY_TYPE_ID
            }, KEY_NAME + "='" + URLEncoder.encode(name, "utf-8") + "'", null, null, null, null);
            if (c.getCount() > 0) {
                while (c.moveToNext()) {
                    if (c.getInt(0) == TYPE_SECTION) {
                        c.close();
                        if (close)
                            close();
                        return true;
                    }
                }
            }
            c.close();
            if (close)
                close();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    public void changeFavSection(final String aid, final int old_order, final int new_order, final String oldSection, final String newSection, final updateDataInterface dataInterface) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    Log.e("FavoriteDB", "From: " + oldSection + " To: " + newSection);
                    Log.e("FavoriteDB", "From: " + old_order + " To: " + new_order);
                    List<FavObject> old_sec = getSection(oldSection).sectionList;
                    for (int i = old_order; i < old_sec.size(); i++) {
                        FavObject current = old_sec.get(i);
                        if (!current.aid.equals(aid)) {
                            changeFav(current.aid, current.order - 1, current.section);
                        }
                    }
                    List<FavObject> new_sec = getSection(newSection).sectionList;
                    for (int a = new_order; a < new_sec.size(); a++) {
                        FavObject current = new_sec.get(a);
                        changeFav(current.aid, current.order + 1, current.section);
                    }
                    changeFav(aid, new_order, newSection);
                    dataInterface.onFinish();
                } catch (Exception e) {
                    e.printStackTrace();
                    dataInterface.onFinish();
                }
                close();
                return null;
            }
        }.executeOnExecutor(ExecutorManager.getExecutor());
    }

    public void changeFav(String aid, int order, String section) {
        try {
            //Log.e("FavoriteDB", "Move Aid: " + aid + " Order: " + order + " Section: " + section);
            ContentValues values = new ContentValues();
            values.put(KEY_SECTION, URLEncoder.encode(section, "utf-8"));
            if (order >= 0) {
                values.put(KEY_ORDER, order);
            } else {
                values.put(KEY_ORDER, 0);
            }
            db.update(TABLE_NAME, values, KEY_AID + "='" + aid + "'", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void changeFav(String aid, String section) {
        try {
            //Log.e("FavoriteDB", "Move Aid: " + aid + " Order: " + order + " Section: " + section);
            ContentValues values = new ContentValues();
            values.put(KEY_SECTION, URLEncoder.encode(section, "utf-8"));
            db.update(TABLE_NAME, values, KEY_AID + "='" + aid + "'", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void changeFav(final String aid, final String section, final updateDataInterface dataInterface) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    //Log.e("FavoriteDB", "Move Aid: " + aid + " Order: " + order + " Section: " + section);
                    ContentValues values = new ContentValues();
                    values.put(KEY_SECTION, URLEncoder.encode(section, "utf-8"));
                    db.update(TABLE_NAME, values, KEY_AID + "='" + aid + "'", null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dataInterface.onFinish();
                return null;
            }
        }.executeOnExecutor(ExecutorManager.getExecutor());
    }

    public void changeSectionName(final String old, final String actual, final updateDataInterface dataInterface) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    ContentValues name = new ContentValues();
                    name.put(KEY_NAME, URLEncoder.encode(actual, "utf-8"));
                    db.update(TABLE_NAME, name, KEY_TYPE_ID + "='" + TYPE_SECTION + "' and " + KEY_NAME + "='" + URLEncoder.encode(old, "utf-8") + "'", null);
                    List<FavObject> favs = getSection(old).sectionList;
                    for (FavObject fav : favs) {
                        changeFav(fav.aid, actual);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                close();
                dataInterface.onFinish();
                return null;
            }
        }.executeOnExecutor(ExecutorManager.getExecutor());
    }

    public FavObject getFav(String aid) {
        try {
            Cursor c = db.query(TABLE_NAME, new String[]{
                    KEY_TABLE_ID,
                    KEY_NAME,
                    KEY_AID,
                    KEY_SECTION,
                    KEY_ORDER
            }, KEY_AID + "='" + aid + "'", null, null, null, null);
            if (c.getCount() > 0) {
                c.moveToFirst();
                FavObject object = new FavObject(URLDecoder.decode(c.getString(1), "utf-8"), c.getString(2), URLDecoder.decode(c.getString(3), "utf-8"), c.getInt(4));
                c.close();
                return object;
            } else {
                c.close();
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public FavObject getSection(String name) {
        FavObject object = new FavObject(name);
        try {
            List<FavObject> fav = new ArrayList<>();
            Cursor c = db.query(TABLE_NAME, new String[]{
                    KEY_TABLE_ID,
                    KEY_NAME,
                    KEY_AID,
                    KEY_SECTION,
                    KEY_ORDER
            }, KEY_SECTION + "='" + URLEncoder.encode(name, "utf-8") + "'", null, null, null, null);
            if (c.getCount() > 0) {
                while (c.moveToNext()) {
                    fav.add(new FavObject(URLDecoder.decode(c.getString(1), "utf-8"), c.getString(2), URLDecoder.decode(c.getString(3), "utf-8"), c.getInt(4)));
                }
                Collections.sort(fav, new favComparator());
            }
            c.close();
            object.sectionList = fav;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return object;
    }

    public List<FavObject> getAllSections(boolean close) {
        List<FavObject> sections = new ArrayList<>();
        try {
            Cursor cursor_sections = db.query(TABLE_NAME, new String[]{
                    KEY_NAME
            }, KEY_TYPE_ID + "='" + TYPE_SECTION + "'", null, null, null, KEY_NAME + " ASC");
            if (cursor_sections.getCount() > 0) {
                while (cursor_sections.moveToNext()) {
                    if (!URLDecoder.decode(cursor_sections.getString(0), "utf-8").equals(NO_SECTION))
                        sections.add(getSection(URLDecoder.decode(cursor_sections.getString(0), "utf-8")));
                }
                Collections.sort(sections, new sectionComparator());
                sections.add(getSection(NO_SECTION));
            } else {
                sections.add(getSection(NO_SECTION));
            }
            cursor_sections.close();
            if (close)
                close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sections;
    }

    public List<FavObject> getAllSectionsExtended(boolean close, boolean wSections) {
        return getAllSectionsExtended(close, wSections, "");
    }

    public List<FavObject> getAllSectionsExtended(boolean close, boolean wSections, String search) {
        List<FavObject> sections = new ArrayList<>();
        try {
            if (wSections) {
                Cursor cursor_sections = db.query(TABLE_NAME, new String[]{
                        KEY_NAME
                }, KEY_TYPE_ID + "='" + TYPE_SECTION + "'", null, null, null, KEY_NAME + " ASC");
                if (cursor_sections.getCount() > 0) {
                    while (cursor_sections.moveToNext()) {
                        if (!URLDecoder.decode(cursor_sections.getString(0), "utf-8").equals(NO_SECTION)) {
                            FavObject section = new FavObject(URLDecoder.decode(cursor_sections.getString(0), "utf-8"));
                            List<FavObject> tmp = getSection(URLDecoder.decode(cursor_sections.getString(0), "utf-8")).sectionList;
                            if (!search.trim().equals("")) {
                                List<FavObject> tmp_new = new ArrayList<>();
                                for (FavObject fav : tmp) {
                                    if (fav.name.toLowerCase().contains(search.trim().toLowerCase()))
                                        tmp_new.add(fav);
                                }
                                if (tmp_new.size() > 0) {
                                    sections.add(section);
                                    sections.addAll(tmp_new);
                                }
                            } else {
                                sections.add(section);
                                sections.addAll(tmp);
                            }
                        }
                    }
                    FavObject section = new FavObject(NO_SECTION);
                    List<FavObject> no_list = getSection(NO_SECTION).sectionList;
                    if (!search.trim().equals("")) {
                        List<FavObject> tmp_new = new ArrayList<>();
                        for (FavObject fav : no_list) {
                            if (fav.name.toLowerCase().contains(search.trim().toLowerCase()))
                                tmp_new.add(fav);
                        }
                        if (tmp_new.size() > 0) {
                            sections.add(section);
                            SortNoSectionList(tmp_new);
                            sections.addAll(tmp_new);
                        }
                    } else {
                        sections.add(section);
                        SortNoSectionList(no_list);
                        sections.addAll(no_list);
                    }
                } else {
                    FavObject section = new FavObject(NO_SECTION);
                    sections.add(section);
                    List<FavObject> no_list = getSection(NO_SECTION).sectionList;
                    if (!search.trim().equals("")) {
                        List<FavObject> tmp_new = new ArrayList<>();
                        for (FavObject fav : no_list) {
                            if (fav.name.toLowerCase().contains(search.trim().toLowerCase()))
                                tmp_new.add(fav);
                        }
                        SortNoSectionList(tmp_new);
                        sections.addAll(tmp_new);
                    } else {
                        SortNoSectionList(no_list);
                        sections.addAll(no_list);
                    }
                    sections.addAll(no_list);
                }
                cursor_sections.close();
            } else {
                Cursor cursor_all = db.query(TABLE_NAME, new String[]{
                        KEY_TABLE_ID,
                        KEY_NAME,
                        KEY_AID,
                        KEY_SECTION,
                        KEY_ORDER
                }, KEY_TYPE_ID + "='" + TYPE_FAV + "'", null, null, null, null);
                if (cursor_all.getCount() > 0) {
                    while (cursor_all.moveToNext()) {
                        FavObject object = new FavObject(URLDecoder.decode(cursor_all.getString(1), "utf-8"), cursor_all.getString(2), URLDecoder.decode(cursor_all.getString(3), "utf-8"), cursor_all.getInt(4));
                        if (!search.trim().equals("")) {
                            if (object.name.toLowerCase().contains(search.trim().toLowerCase()))
                                sections.add(object);
                        } else {
                            sections.add(object);
                        }
                    }
                    SortNoSectionList(sections);
                }
                cursor_all.close();
            }
            if (close)
                close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sections;
    }

    private void SortNoSectionList(List<FavObject> list) {
        switch (PreferenceManager.getDefaultSharedPreferences(context).getString("sort_fav", "0")) {
            case "1":
                Collections.sort(list, new favNoSectionNameComparator());
                break;
            case "2":
                Collections.sort(list, new favNoSectionAidComparator(false));
                break;
            case "3":
                Collections.sort(list, new favNoSectionAidComparator(true));
                break;
            default:
                Collections.sort(list, new favComparator());
                break;
        }
    }

    public List<String> getSectionListName() {
        try {
            List<String> names = new ArrayList<>();
            Cursor cursor_sections = db.query(TABLE_NAME, new String[]{
                    KEY_NAME
            }, KEY_TYPE_ID + "='" + TYPE_SECTION + "'", null, null, null, KEY_NAME + " ASC");
            if (cursor_sections.getCount() > 0) {
                while (cursor_sections.moveToNext()) {
                    String name = URLDecoder.decode(cursor_sections.getString(0), "utf-8");
                    if (!name.equals(NO_SECTION)) {
                        names.add(name);
                    }
                }
            }
            names.add(NO_SECTION);
            close();
            return names;
        } catch (Exception e) {
            e.printStackTrace();
            close();
            return new ArrayList<>();
        }
    }

    public JSONObject getDBJSON(boolean close) {
        try {
            List<FavObject> animes = getAllSections(false);
            JSONArray array = new JSONArray();
            for (FavObject list : animes) {
                if (list.isSection && list.sectionList.size() > 0) {
                    JSONObject section = new JSONObject();
                    section.put("name", list.name);
                    JSONArray favs = new JSONArray();
                    for (FavObject anim : list.sectionList) {
                        if (!anim.isSection) {
                            JSONObject object = new JSONObject();
                            object.put("title", anim.name);
                            object.put("aid", anim.aid);
                            object.put("section", anim.section);
                            object.put("order", anim.order);
                            favs.put(object);
                        }
                    }
                    section.put("list", favs);
                    array.put(section);
                }
            }
            JSONObject end = new JSONObject();
            end.put("response", "ok");
            end.put("favs", array);
            if (close)
                close();
            return end;
        } catch (Exception e) {
            e.printStackTrace();
            if (close)
                close();
            return new JSONObject();
        }
    }

    public void updatebyJSON(final JSONObject object, @Nullable final updateDataInterface dataInterface) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                JSONObject backup = getDBJSON(false);
                try {
                    reset(false);
                    JSONArray array = object.getJSONArray("favs");
                    for (int i = 0; i < array.length(); i++) {
                        String section_name = array.getJSONObject(i).getString("name");
                        addSection(new FavObject(section_name));
                        JSONArray favs_list = array.getJSONObject(i).getJSONArray("list");
                        for (int o = 0; o < favs_list.length(); o++) {
                            JSONObject fav = favs_list.getJSONObject(o);
                            addFav(new FavObject(fav.getString("title"), fav.getString("aid"), fav.getString("section"), fav.getInt("order")));
                        }
                    }
                    if (dataInterface != null)
                        dataInterface.onFinish();
                    close();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("FavoriteDB", "Error loading data, using backup");
                    updatebyJSON(backup, dataInterface);
                }
                return null;
            }
        }.executeOnExecutor(ExecutorManager.getExecutor());
    }

    public void reset(boolean close) {
        db.delete(TABLE_NAME, null, null);
        if (close) {
            close();
        } else {
            set();
        }
    }

    public void updateOldData(final updateDataInterface dataInterface) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                SharedPreferences sharedPreferences = context.getSharedPreferences("data", Context.MODE_PRIVATE);
                if (!sharedPreferences.getString("favoritos", "").equals("") && !sharedPreferences.getBoolean("data_revised", false)) {
                    Log.e("Update Data", "favs: " + sharedPreferences.getString("favoritos", ""));
                    String fav = sharedPreferences.getString("favoritos", "");
                    reset(false);
                    if (!fav.trim().equals("") && fav.contains(":::")) {
                        String[] favoritos = fav.split(":::");
                        Log.d("favoritos", fav);
                        List<String> aids = new ArrayList<String>();
                        for (String i : favoritos) {
                            if (!i.equals("") && !i.equals("null")) {
                                aids.add(i);
                            }
                        }
                        favoritos = new String[aids.size()];
                        aids.toArray(favoritos);
                        int count = 0;
                        for (String aid : aids) {
                            addFav(new FavObject(DirectoryHelper.get(context).getTitle(aid), aid, NO_SECTION, count));
                            count++;
                        }
                        sharedPreferences.edit().putString("favoritos", "").apply();
                    }
                    close();
                    sharedPreferences.edit().putBoolean("data_revised", true).apply();
                    FavSyncro.updateFavs(context);
                }
                dataInterface.onFinish();
                return null;
            }
        }.executeOnExecutor(ExecutorManager.getExecutor());
    }

    public interface updateDataInterface {
        void onFinish();
    }
}