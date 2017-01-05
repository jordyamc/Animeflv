package knf.animeflv.WaitList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jordy on 02/01/2017.
 */

public class WaitDBHelper {
    private static final String DOWNLOAD_TABLE_NAME = "WaitDB";
    private static final String KEY_TABLE_ID = "_table_id";
    private static final String KEY_AID = "_aid";
    private static final String KEY_LIST = "_list";
    private static final String DATABASE_CREATE =
            "create table if not exists " + DOWNLOAD_TABLE_NAME + "("
                    + KEY_TABLE_ID + " integer primary key autoincrement, "
                    + KEY_AID + " text not null, "
                    + KEY_LIST + " text not null"
                    + ");";
    private static final String DATABASE_NAME = "WAIT_DATABASE_LIST";
    protected Context context;
    private SQLiteDatabase db;

    public WaitDBHelper(Context context) {
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

    public void addToList(String eid) {
        Log.e("WaitList", "Add: " + eid);
        String[] semi = eid.trim().replace("E", "").split("_");
        String aid = semi[0];
        String num = semi[1];
        String list = getList(aid);
        if (list != null && !list.contains(num + "-")) {
            list += num + "-";
            setList(aid, list);
        } else if (list == null) {
            list = num + "-";
            addList(aid, list);
        }
        close();
    }

    public void delFromList(String eid) {
        String[] semi = eid.trim().replace("E", "").split("_");
        String aid = semi[0];
        String num = semi[1];
        String list = getList(aid);
        if (list != null && list.contains(num + "-")) {
            list = list.replace(num + "-", "");
            if (list.trim().equals("")) {
                removeList(aid);
            } else {
                setList(aid, list);
            }
        }
        close();
    }

    public List<String> getAidsList() {
        List<String> list = new ArrayList<>();
        Cursor c = db.query(DOWNLOAD_TABLE_NAME, new String[]{
                KEY_AID
        }, null, null, null, null, null);
        if (c.getCount() > 0) {
            while (c.moveToNext()) {
                list.add(c.getString(0));
            }
        }
        c.close();
        return list;
    }

    private void removeList(String aid) {
        db.delete(DOWNLOAD_TABLE_NAME, KEY_AID + "='" + aid + "'", null);
    }

    public String getList(String aid) {
        Cursor c = db.query(DOWNLOAD_TABLE_NAME, new String[]{
                KEY_LIST
        }, KEY_AID + "='" + aid + "'", null, null, null, null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            String list = c.getString(0);
            Log.e("WaitList", "From " + aid + ": " + list);
            c.close();
            return list;
        } else {
            return null;
        }
    }

    private void setList(String aid, String list) {
        ContentValues args = new ContentValues();
        args.put(KEY_LIST, list);
        db.update(DOWNLOAD_TABLE_NAME, args, KEY_AID + "='" + aid + "'", null);
    }

    private void addList(String aid, String list) {
        ContentValues args = new ContentValues();
        args.put(KEY_AID, aid);
        args.put(KEY_LIST, list);
        db.insert(DOWNLOAD_TABLE_NAME, null, args);
    }
}
