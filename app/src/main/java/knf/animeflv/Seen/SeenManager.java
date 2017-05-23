package knf.animeflv.Seen;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import java.util.List;

import knf.animeflv.FavSyncro;
import knf.animeflv.Utils.ExecutorManager;
import knf.animeflv.info.fragments.FragmentCaps;


public class SeenManager {
    private static final String PREFS_FAVS = "favs";
    private static final String TABLE_NAME = "SeenManager";
    private static final String KEY_TABLE_ID = "_table_id";
    private static final String KEY_EID = "_eid";
    private static final String DATABASE_CREATE =
            "create table if not exists " + TABLE_NAME + "("
                    + KEY_TABLE_ID + " integer primary key autoincrement, "
                    + KEY_EID + " text not null"
                    + ");";
    private static final String DATABASE_NAME = "SEEN_STATE_MANAGER";
    private static boolean isUpdating = false;
    private static SeenManager manager;
    protected Context context;
    private SQLiteDatabase db;

    private SeenManager(Context context) {
        this.context = context;
        setDB();
    }

    public static SeenManager get(Context context) {
        if (manager == null)
            manager = new SeenManager(context);
        return manager;
    }

    private void setDB() {
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
            db = null;
            manager = null;
        } catch (NullPointerException e) {
            Log.e("On Close DB", "DB is Null!!!!!");
        }
    }

    public void setSeenState(final String eid, final boolean seen) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    if (seen) {
                        if (!isSeenNoClose(eid)) {
                            ContentValues values = new ContentValues();
                            values.put(KEY_EID, eid);
                            db.insert(TABLE_NAME, null, values);
                        }
                    } else {
                        if (isSeenNoClose(eid)) {
                            db.delete(TABLE_NAME, KEY_EID + "='" + eid + "'", null);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.executeOnExecutor(ExecutorManager.getExecutor());
    }

    public void setListSeenState(final List<String> eid, final boolean seen, final FragmentCaps.SeenProgress progress) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                if (seen) {
                    for (String e : eid) {
                        Log.e("Seen State", "setting " + e + "   Seen: true");
                        if (!isSeenNoClose(e)) {
                            ContentValues values = new ContentValues();
                            values.put(KEY_EID, e);
                            db.insert(TABLE_NAME, null, values);
                            Log.e("Seen " + e, "Set as Seen");
                        } else {
                            Log.e("Seen " + e, "Alredy Seen");
                        }
                        progress.onStep();
                    }
                } else {
                    StringBuilder builder = new StringBuilder();
                    for (String e : eid) {
                        builder.append("'");
                        builder.append(e);
                        builder.append("', ");
                    }
                    String f_s = builder.toString();
                    if (f_s.endsWith(", "))
                        f_s = f_s.substring(0, f_s.length() - 2);
                    db.execSQL("delete from " + TABLE_NAME + " where " + KEY_EID + " in (" + f_s + ");");
                }
                close();
                progress.onFinish();
                return null;
            }
        }.executeOnExecutor(ExecutorManager.getExecutor());
    }

    public void setSeenStateUpload(final String eid, final boolean seen) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                if (seen) {
                    if (!isSeenNoClose(eid)) {
                        ContentValues values = new ContentValues();
                        values.put(KEY_EID, eid);
                        db.insert(TABLE_NAME, null, values);
                    }
                } else {
                    if (isSeenNoClose(eid)) {
                        db.delete(TABLE_NAME, KEY_EID + "='" + eid + "'", null);
                    }
                }
                FavSyncro.updateServer(context);
                return null;
            }
        }.executeOnExecutor(ExecutorManager.getExecutor());
    }

    private void setSeenStateNoClose(final String eid, final boolean seen) {
        if (seen) {
            if (!isSeenNoClose(eid)) {
                ContentValues values = new ContentValues();
                values.put(KEY_EID, eid);
                db.insert(TABLE_NAME, null, values);
            }
        } else {
            if (isSeenNoClose(eid)) {
                db.delete(TABLE_NAME, KEY_EID + "='" + eid + "'", null);
            }
        }
    }


    public boolean isSeen(String eid) {
        try {
            Cursor c = db.query(TABLE_NAME, new String[]{
                    KEY_TABLE_ID
            }, KEY_EID + "='" + eid + "'", null, null, null, null);
            if (c.getCount() > 0) {
                c.close();
                return true;
            }
            c.close();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean isSeenNoClose(String eid) {
        try {
            Cursor c = db.query(TABLE_NAME, new String[]{
                    KEY_TABLE_ID
            }, KEY_EID + "='" + eid + "'", null, null, null, null);
            if (c.getCount() > 0) {
                c.close();
                return true;
            }
            c.close();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getSeenList() {
        try {
            Cursor c = db.query(TABLE_NAME, new String[]{
                    KEY_EID
            }, null, null, null, null, null);
            StringBuilder builder = new StringBuilder();
            while (c.moveToNext()) {
                builder.append(c.getString(0));
                builder.append(":::");
            }
            c.close();
            return builder.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public void updateSeen(final String list, final SeenCallback callback) {
        if (!SeenManager.isUpdating)
            SeenManager.isUpdating = true;
        new AsyncTask<Void, Void, Void>() {
            @SuppressLint("ApplySharedPref")
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    db.delete(TABLE_NAME, null, null);
                    setDB();
                    String[] exploded = list.split(":::");
                    Log.e("Seen Sync", "To sync: " + exploded.length);
                    for (String eid : exploded) {
                        if (!eid.trim().equals(""))
                            setSeenStateNoClose(eid.trim(), true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                SeenManager.isUpdating = false;
                callback.onSeenUpdated();
                return null;
            }
        }.executeOnExecutor(ExecutorManager.getExecutor());
    }

    public interface SeenCallback {
        void onSeenUpdated();
    }
}
