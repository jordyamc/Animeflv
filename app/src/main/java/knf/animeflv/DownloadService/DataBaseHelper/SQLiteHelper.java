package knf.animeflv.DownloadService.DataBaseHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;
import android.util.Log;

import knf.animeflv.DownloadService.ServiceHolder.ServiceManager;

public class SQLiteHelper {
    class DownloadItem {
        public long _table_id;
        public long _download_id;
        public String progress;
        public String url;
        public String eid;
        public String aid;
        public String numero;
        public String titulo;
        public boolean isEmpty = false;
    }

    private static final String DOWNLOAD_TABLE_NAME = "DownloadData";

    private static final String KEY_TABLE_ID = "_table_id";
    private static final String KEY_DOWNLOAD_ID = "_download_id";
    private static final String KEY_URL = "url";
    private static final String KEY_EID = "eid";
    private static final String KEY_AID = "aid";
    private static final String KEY_NUMERO = "numero";
    private static final String KEY_TITULO = "titulo";
    private static final String KEY_PROGRESS = "progress";

    private Context context;

    private static final String DATABASE_CREATE =
            "create table if not exist" + DOWNLOAD_TABLE_NAME + "("
                    + KEY_TABLE_ID + " integer primary key autoincrement, "
                    + KEY_DOWNLOAD_ID + " integer, "
                    + KEY_URL + " text not null, "
                    + KEY_EID + " text not null, "
                    + KEY_AID + " text not null, "
                    + KEY_NUMERO + " text not null, "
                    + KEY_TITULO + " text not null, "
                    + KEY_PROGRESS + " text not null"
                    + ");";
    private static final String DATABASE_NAME = "DOWNLOAD_SERVICE_DATABASE";

    private static final int DATABASE_VERSION = 1;

    private SQLiteDatabase db;

    public SQLiteHelper(Context context) {
        this.context = context;
        try {
            db = context.openOrCreateDatabase(DATABASE_NAME, DATABASE_VERSION, null, null);
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

    public boolean addElement(ServiceManager.DownloadConstructor constructor) {
        ContentValues values = new ContentValues();
        values.put(KEY_DOWNLOAD_ID, constructor.id);
        values.put(KEY_URL, constructor.url);
        values.put(KEY_EID, constructor.eid);
        values.put(KEY_AID, constructor.aid);
        values.put(KEY_NUMERO, constructor.numero);
        values.put(KEY_TITULO, constructor.titulo);
        values.put(KEY_PROGRESS, "0");
        long id = db.insert(DOWNLOAD_TABLE_NAME, null, values);
        if (id != -1) {
            context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putLong(constructor.eid + "_download_table_id", id).apply();
            return true;
        } else {
            return false;
        }
    }

    public void delete(String eid) {
        db.delete(DOWNLOAD_TABLE_NAME, KEY_TABLE_ID + "=" + context.getSharedPreferences("data", Context.MODE_PRIVATE).getLong(eid + "_download_table_id", -1), null);
    }

    public void delete(long id) {
        db.delete(DOWNLOAD_TABLE_NAME, KEY_TABLE_ID + "=" + id, null);
    }

    public DownloadItem getDownloadInfo(String eid) {
        DownloadItem item = new DownloadItem();
        Cursor c = db.query(DOWNLOAD_TABLE_NAME, new String[]{
                KEY_TABLE_ID,
                KEY_DOWNLOAD_ID,
                KEY_URL,
                KEY_EID,
                KEY_AID,
                KEY_NUMERO,
                KEY_TITULO,
                KEY_PROGRESS
        }, KEY_TABLE_ID + "=" + context.getSharedPreferences("data", Context.MODE_PRIVATE).getLong(eid + "_download_table_id", -1), null, null, null, null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            item._table_id = c.getLong(0);
            item._download_id = c.getLong(1);
            item.url = c.getString(2);
            item.eid = c.getString(3);
            item.aid = c.getString(4);
            item.numero = c.getString(5);
            item.titulo = c.getString(6);
            item.progress = c.getString(7);
            c.close();
            return item;
        } else {
            item.isEmpty = true;
            c.close();
            return item;
        }
    }

    public int getTotalDownloads() {
        Cursor c = db.query(DOWNLOAD_TABLE_NAME, new String[]{
                KEY_TABLE_ID
        }, null, null, null, null, null);
        int total = c.getCount();
        c.close();
        return total;
    }

    public boolean downloadExist(String eid) {
        Cursor c = db.query(DOWNLOAD_TABLE_NAME, new String[]{
                KEY_TABLE_ID
        }, KEY_TABLE_ID + "=" + context.getSharedPreferences("data", Context.MODE_PRIVATE).getLong(eid + "_download_table_id", -1), null, null, null, null);
        boolean exist = c.getCount() > 0;
        c.close();
        return exist;
    }

    @Nullable
    public String getProgress(String eid) {
        Cursor c = db.query(DOWNLOAD_TABLE_NAME, new String[]{
                KEY_TABLE_ID,
                KEY_PROGRESS
        }, KEY_TABLE_ID + "=" + context.getSharedPreferences("data", Context.MODE_PRIVATE).getLong(eid + "_download_table_id", -1), null, null, null, null);
        if (c.getCount() > 0) {
            String progress = c.getString(1);
            c.close();
            return progress;
        } else {
            c.close();
            return null;
        }
    }

    public void updateProgress(String eid, String progress) {
        ContentValues args = new ContentValues();
        args.put(KEY_PROGRESS, progress);
        db.update(DOWNLOAD_TABLE_NAME, args, KEY_TABLE_ID + "=" + context.getSharedPreferences("data", Context.MODE_PRIVATE).getLong(eid + "_download_table_id", -1), null);
    }
}
