package knf.animeflv.Directorio.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;
import android.util.Log;

import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;
import java.util.List;

import knf.animeflv.Directorio.AnimeClass;
import knf.animeflv.Utils.FileUtil;
import knf.animeflv.Utils.SearchUtils;

public class DirectoryDB {
    private static final String TABLE_NAME = "DirectoryDB";
    private static final String KEY_TABLE_ID = "_table_id";
    private static final String KEY_AID_A = "_aid";
    private static final String KEY_NAME_B = "_name";
    private static final String KEY_TYPE_C = "_type";
    private static final String KEY_LID_D = "_lid";
    private static final String KEY_SID_E = "_sid";
    private static final String KEY_GENRES_F = "_genres";
    private static final String DATABASE_CREATE =
            "create table if not exists " + TABLE_NAME + "("
                    + KEY_TABLE_ID + " integer primary key autoincrement, "
                    + KEY_AID_A + " text not null, "
                    + KEY_NAME_B + " text not null, "
                    + KEY_TYPE_C + " text not null, "
                    + KEY_LID_D + " text not null, "
                    + KEY_SID_E + " text not null, "
                    + KEY_GENRES_F + " text"
                    + ");";
    private static final String DATABASE_NAME = "Directory_Database";
    protected Context context;
    private SQLiteDatabase db;

    public DirectoryDB(Context context) {
        this.context = context;
        try {
            db = context.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE, null, null);
            db.execSQL(DATABASE_CREATE);
        } catch (Exception e) {
            Crashlytics.logException(e);
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

    public synchronized DirectoryDB addAnime(DirectoryItem object) {
        ContentValues values = new ContentValues();
        values.put(KEY_AID_A, object.aid);
        values.put(KEY_NAME_B, object.name);
        values.put(KEY_TYPE_C, object.type);
        values.put(KEY_LID_D, object.lid);
        values.put(KEY_SID_E, object.sid);
        values.put(KEY_GENRES_F, object.genres);
        db.insert(TABLE_NAME, null, values);
        return this;
    }

    public synchronized List<AnimeClass> getAll(boolean close) {
        List<AnimeClass> items = new ArrayList<>();
        if (db.isOpen()) {
            Cursor c = db.query(TABLE_NAME, new String[]{
                    KEY_AID_A,
                    KEY_NAME_B,
                    KEY_TYPE_C,
            }, null, null, null, null, KEY_NAME_B + " ASC");
            if (c.getCount() > 0) {
                while (c.moveToNext()) {
                    items.add(new AnimeClass(
                            c.getString(1),
                            c.getString(0),
                            c.getString(2)
                    ));
                }
            }
            c.close();
            if (close)
                close();
        }
        return items;
    }

    public List<DirectoryItem> getAllAnimes(boolean close) {
        List<DirectoryItem> items = new ArrayList<>();
        Cursor c = db.query(TABLE_NAME, new String[]{
                KEY_AID_A,
                KEY_NAME_B,
                KEY_TYPE_C,
                KEY_LID_D,
                KEY_SID_E,
                KEY_GENRES_F
        }, null, null, null, null, null);
        if (c.getCount() > 0) {
            while (c.moveToNext()) {
                items.add(new DirectoryItem(
                        c.getString(0),
                        c.getString(1),
                        c.getString(2),
                        c.getString(3),
                        c.getString(4),
                        c.getString(5)));
            }
        }
        c.close();
        if (close)
            close();
        return items;
    }

    public List<DirectoryItem> getAllAnimesCGen(String query, boolean close) {
        List<DirectoryItem> items = new ArrayList<>();
        Cursor c = db.query(TABLE_NAME, new String[]{
                KEY_AID_A,
                KEY_NAME_B,
                KEY_TYPE_C,
                KEY_LID_D,
                KEY_SID_E,
                KEY_GENRES_F
        }, KEY_GENRES_F + " LIKE '%" + query + "%'", null, null, null, null);
        while (c.moveToNext()) {
            items.add(new DirectoryItem(
                    c.getString(0),
                    c.getString(1),
                    c.getString(2),
                    c.getString(3),
                    c.getString(4),
                    c.getString(5)));
        }
        c.close();
        if (close)
            close();
        return items;
    }

    public List<AnimeClass> getAllByType(String type, boolean close) {
        List<AnimeClass> items = new ArrayList<>();
        Cursor c = db.query(TABLE_NAME, new String[]{
                KEY_AID_A,
                KEY_NAME_B,
                KEY_TYPE_C,
        }, KEY_TYPE_C + "='" + type + "'", null, null, null, KEY_NAME_B + " ASC");
        if (c.getCount() > 0) {
            while (c.moveToNext()) {
                items.add(new AnimeClass(
                        c.getString(1),
                        c.getString(0),
                        c.getString(2)
                ));
            }
        }
        c.close();
        if (close)
            close();
        return items;
    }

    @Nullable
    public DirectoryItem getAnimeByAid(String aid) {
        DirectoryItem item = null;
        Cursor c = db.query(TABLE_NAME, new String[]{
                KEY_AID_A,
                KEY_NAME_B,
                KEY_TYPE_C,
                KEY_LID_D,
                KEY_SID_E,
                KEY_GENRES_F
        }, KEY_AID_A + "='" + aid + "'", null, null, null, null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            item = new DirectoryItem(
                    c.getString(0),
                    c.getString(1),
                    c.getString(2),
                    c.getString(3),
                    c.getString(4),
                    c.getString(5));
        }
        c.close();
        close();
        return item;
    }

    public String getTitleByAid(String aid) {
        String item = "null";
        Cursor c = db.query(TABLE_NAME, new String[]{
                KEY_NAME_B,
        }, KEY_AID_A + "='" + aid + "'", null, null, null, null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            item = c.getString(0);
        }
        c.close();
        close();
        return item;
    }

    public String getURLEpByEid(String eid, String sid) {
        String[] data = eid.replace("E", "").split("_");
        return getURLEpByEid(data[0], data[1], sid);
    }

    public String getURLEpByEid(String aid, String numero, String sid) {
        String url = "null";
        Cursor c = db.query(TABLE_NAME, new String[]{
                KEY_AID_A,
                KEY_LID_D
        }, KEY_AID_A + "='" + aid + "'", null, null, null, null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            url = "https://animeflv.net/ver/" + sid + "/" + c.getString(1) + "-" + numero;
        }
        c.close();
        close();
        return url;
    }

    public String getTypeByAid(String aid) {
        String type = "null";
        Cursor c = db.query(TABLE_NAME, new String[]{
                KEY_AID_A,
                KEY_TYPE_C
        }, KEY_AID_A + "='" + aid + "'", null, null, null, null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            type = c.getString(1).trim();
        }
        c.close();
        close();
        return type;
    }

    public String getURLAnimeByAid(String aid) {
        String url = "null";
        Cursor c = db.query(TABLE_NAME, new String[]{
                KEY_AID_A,
                KEY_TYPE_C,
                KEY_SID_E,
                KEY_LID_D
        }, KEY_AID_A + "='" + aid + "'", null, null, null, null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            url = "https://animeflv.net/" + c.getString(1).toLowerCase() + "/" + c.getString(2) + "/" + c.getString(3);
        }
        c.close();
        close();
        return url;
    }

    @Nullable
    public String getAidByLID(String lid) {
        String item = null;
        Cursor c = db.query(TABLE_NAME, new String[]{
                KEY_AID_A,
        }, KEY_LID_D + "='" + lid.trim() + "'", null, null, null, null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            item = c.getString(0);
        }
        c.close();
        close();
        return item;
    }

    public List<AnimeClass> searchName(String query) {
        List<AnimeClass> items = new ArrayList<>();
        Cursor c = db.query(TABLE_NAME, new String[]{
                KEY_AID_A,
                KEY_NAME_B,
                KEY_TYPE_C,
        }, KEY_NAME_B + " LIKE ? COLLATE NOCASE", new String[]{"%" + query + "%"}, null, null, KEY_NAME_B + " ASC");
        if (c.getCount() > 0) {
            while (c.moveToNext()) {
                items.add(new AnimeClass(
                        c.getString(1),
                        c.getString(0),
                        c.getString(2)
                ));
            }
        }
        c.close();
        close();
        return items;
    }

    public List<AnimeClass> searchGenres(String query) {
        List<AnimeClass> items = new ArrayList<>();
        Cursor c = db.query(TABLE_NAME, new String[]{
                KEY_AID_A,
                KEY_NAME_B,
                KEY_TYPE_C,
                KEY_GENRES_F
        }, KEY_NAME_B + " LIKE ? COLLATE NOCASE", new String[]{"%" + query + "%"}, null, null, KEY_NAME_B + " ASC");
        if (c.getCount() > 0) {
            while (c.moveToNext()) {
                if (SearchUtils.containsGenero(c.getString(3)))
                    items.add(new AnimeClass(
                            c.getString(1),
                            c.getString(0),
                            c.getString(2)
                    ));
            }
        }
        c.close();
        close();
        return items;
    }

    public List<AnimeClass> searchID(String query) {
        List<AnimeClass> items = new ArrayList<>();
        Cursor c = db.query(TABLE_NAME, new String[]{
                KEY_AID_A,
                KEY_NAME_B,
                KEY_TYPE_C,
        }, KEY_AID_A + "='" + query + "'", null, null, null, null);
        if (c.getCount() > 0) {
            while (c.moveToNext()) {
                if (FileUtil.isNumber(query)) {
                    items.add(new AnimeClass(
                            c.getString(1),
                            c.getString(0),
                            c.getString(2)
                    ));
                } else {
                    items.add(new AnimeClass(query.replace("aid:", "").trim(), "_NoNum_", "_NoNum_"));
                }
            }
        }
        c.close();
        close();
        return items;
    }

    public synchronized boolean animedExist(String aid, boolean close) {
        if (db.isOpen()) {
            Cursor c = db.query(TABLE_NAME, new String[]{
                    KEY_TABLE_ID
            }, KEY_AID_A + "='" + aid + "'", null, null, null, null);
            if (c.getCount() > 0) {
                c.close();
                return true;
            }
            c.close();
            if (close)
                close();
        }
        return false;
    }

    public int getLastAid() {
        int pos = 2700;
        Cursor c = db.query(TABLE_NAME, new String[]{
                KEY_AID_A,
        }, null, null, null, null, KEY_AID_A + " DESC");
        if (c.getCount() > 0) {
            c.moveToFirst();
            pos = Integer.parseInt(c.getString(0));
        }
        c.close();
        close();
        return pos;
    }

    public int getCount(boolean close) {
        Cursor c = db.query(TABLE_NAME, new String[]{
                KEY_AID_A,
        }, null, null, null, null, null);
        int count = c.getCount();
        c.close();
        if (close)
            close();
        return count;
    }

    public boolean isDBEmpty(boolean close) {
        Cursor c = db.query(TABLE_NAME, new String[]{
                KEY_TABLE_ID
        }, null, null, null, null, null);
        boolean empty = !(c != null && c.getCount() > 0);
        if (!empty)
            c.close();
        if (close)
            close();
        return empty;
    }

    public void reset() {
        db.delete(TABLE_NAME, null, null);
        close();
    }

    public static class DirectoryItem {
        public String aid;
        public String name;
        public String type;
        public String lid;
        public String sid;
        public String genres;

        public DirectoryItem(String aid, String name, String type, String lid, String sid, @Nullable String genres) {
            this.aid = aid;
            this.name = FileUtil.corregirTit(name);
            this.type = type;
            this.lid = lid;
            this.sid = sid;
            this.genres = genres;
        }

        @Override
        public int hashCode() {
            return aid.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof DirectoryItem && ((DirectoryItem) obj).aid.equals(aid);
        }
    }
}