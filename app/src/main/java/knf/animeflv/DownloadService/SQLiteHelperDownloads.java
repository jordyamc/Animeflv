package knf.animeflv.DownloadService;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class SQLiteHelperDownloads {
    private static final String DOWNLOAD_TABLE_NAME = "DownloadService";
    private static final String KEY_TABLE_ID = "_table_id";
    private static final String KEY_DOWNLOAD_ID = "_download_id";
    private static final String KEY_URL = "_url";
    private static final String KEY_EID = "_eid";
    private static final String KEY_PROGRESS = "_progress";
    private static final String KEY_STATE = "_state";
    private static final String KEY_DOWNLOAD_PREFS = "_downloadID";
    private static final String DATABASE_CREATE =
            "create table if not exists " + DOWNLOAD_TABLE_NAME + "("
                    + KEY_TABLE_ID + " integer primary key autoincrement, "
                    + KEY_DOWNLOAD_ID + " long, "
                    + KEY_URL + " text not null, "
                    + KEY_EID + " text not null, "
                    + KEY_PROGRESS + " integer, "
                    + KEY_STATE + " integer"
                    + ");";
    private static final String DATABASE_NAME = "DOWNLOAD_SERVICE_DATABASE_LIST";
    protected Context context;
    private SQLiteDatabase db;

    public SQLiteHelperDownloads(Context context) {
        this.context = context;
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

    @SuppressLint("ApplySharedPref")
    public SQLiteHelperDownloads addElement(DownloadObject object) {
        ContentValues values = new ContentValues();
        values.put(KEY_DOWNLOAD_ID, object.id);
        values.put(KEY_URL, object.url);
        values.put(KEY_EID, object.eid);
        values.put(KEY_PROGRESS, 0);
        db.insert(DOWNLOAD_TABLE_NAME, null, values);
        context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putLong(object.eid + KEY_DOWNLOAD_PREFS, object.id).commit();
        return this;
    }

    public SQLiteHelperDownloads delete(String eid) {
        db.delete(DOWNLOAD_TABLE_NAME, KEY_DOWNLOAD_ID + "='" + context.getSharedPreferences("data", Context.MODE_PRIVATE).getLong(eid + KEY_DOWNLOAD_PREFS, -1) + "'", null);
        context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putLong(eid + KEY_DOWNLOAD_PREFS, -1).apply();
        return this;
    }

    public DownloadItem getDownloadInfo(String eid, boolean autoclose) {
        DownloadItem item = new DownloadItem();
        Cursor c = db.query(DOWNLOAD_TABLE_NAME, new String[]{
                KEY_TABLE_ID,
                KEY_DOWNLOAD_ID,
                KEY_URL,
                KEY_EID,
                KEY_PROGRESS
        }, KEY_DOWNLOAD_ID + "='" + context.getSharedPreferences("data", Context.MODE_PRIVATE).getLong(eid + KEY_DOWNLOAD_PREFS, -1) + "'", null, null, null, null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            item._table_id = c.getLong(0);
            item._download_id = c.getLong(1);
            item.url = c.getString(2);
            item.eid = c.getString(3);
            item.progress = c.getString(4);
        } else {
            item.isEmpty = true;
        }
        c.close();
        if (autoclose)
            close();
        return item;
    }

    public int getTotalDownloads() {
        Cursor c = db.query(DOWNLOAD_TABLE_NAME, new String[]{
                KEY_EID,
                KEY_DOWNLOAD_ID
        }, null, null, null, null, null);
        int count = 0;
        while (c.moveToNext()) {
            if (c.getLong(1) == context.getSharedPreferences("data", Context.MODE_PRIVATE).getLong(c.getString(0) + KEY_DOWNLOAD_PREFS, -1))
                count++;
        }
        c.close();
        close();
        return count;
    }

    public boolean downloadExist(String eid) {
        Cursor c = db.query(DOWNLOAD_TABLE_NAME, new String[]{
                KEY_DOWNLOAD_ID
        }, KEY_EID + "='" + eid + "'", null, null, null, null);
        if (c.getCount() > 0) {
            while (c.moveToNext()) {
                if (c.getLong(0) == context.getSharedPreferences("data", Context.MODE_PRIVATE).getLong(eid + KEY_DOWNLOAD_PREFS, -1))
                    return true;
            }
        }
        c.close();
        close();
        return false;
    }

    public int getProgress(String eid) {
        Cursor c = db.query(DOWNLOAD_TABLE_NAME, new String[]{
                KEY_PROGRESS
        }, KEY_DOWNLOAD_ID + "='" + context.getSharedPreferences("data", Context.MODE_PRIVATE).getLong(eid + KEY_DOWNLOAD_PREFS, -1) + "'", null, null, null, null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            int progress = c.getInt(0);
            c.close();
            close();
            return progress;
        } else {
            c.close();
            close();
            return -1;
        }
    }

    public SQLiteHelperDownloads updateProgress(String eid, int progress) {
        ContentValues args = new ContentValues();
        args.put(KEY_PROGRESS, progress);
        db.update(DOWNLOAD_TABLE_NAME, args, KEY_DOWNLOAD_ID + "='" + context.getSharedPreferences("data", Context.MODE_PRIVATE).getLong(eid + KEY_DOWNLOAD_PREFS, -1) + "'", null);
        return this;
    }

    public int getState(String eid) {
        Cursor c = db.query(DOWNLOAD_TABLE_NAME, new String[]{
                KEY_STATE
        }, KEY_DOWNLOAD_ID + "='" + context.getSharedPreferences("data", Context.MODE_PRIVATE).getLong(eid + KEY_DOWNLOAD_PREFS, -1) + "'", null, null, null, null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            int state = c.getInt(0);
            c.close();
            close();
            return state;
        } else {
            c.close();
            close();
            return 16;
        }
    }

    public SQLiteHelperDownloads updateState(String eid, int state) {
        ContentValues args = new ContentValues();
        args.put(KEY_STATE, state);
        db.update(DOWNLOAD_TABLE_NAME, args, KEY_DOWNLOAD_ID + "='" + context.getSharedPreferences("data", Context.MODE_PRIVATE).getLong(eid + KEY_DOWNLOAD_PREFS, -1) + "'", null);
        return this;
    }

    public void reset() {
        db.delete(DOWNLOAD_TABLE_NAME, null, null);
        close();
    }

    class DownloadItem {
        public long _table_id;
        public long _download_id = -1;
        public String progress;
        public String url;
        public String eid;
        public boolean isEmpty = false;
    }
}