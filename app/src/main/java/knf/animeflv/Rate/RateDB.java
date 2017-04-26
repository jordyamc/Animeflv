package knf.animeflv.Rate;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by Jordy on 02/01/2017.
 */

public class RateDB {
    private static final String TABLE_NAME = "RATE_SEGISTER";
    private static final String KEY_TABLE_ID = "_table_id";
    private static final String KEY_AID = "_aid";
    private static final String KEY_RATE = "_points";
    private static final String DATABASE_CREATE =
            "create table if not exists " + TABLE_NAME + "("
                    + KEY_TABLE_ID + " integer primary key autoincrement, "
                    + KEY_AID + " text not null, "
                    + KEY_RATE + " text not null"
                    + ");";
    private static final String DATABASE_NAME = "RATE_DB";
    protected Context context;
    private SQLiteDatabase db;

    private RateDB(Context context) {
        this.context = context;
        try {
            db = context.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE, null, null);
            db.execSQL(DATABASE_CREATE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static RateDB get(Context context) {
        return new RateDB(context);
    }

    public void close() {
        try {
            db.close();
        } catch (NullPointerException e) {
            Log.e("On Close DB", "DB is Null!!!!!");
        }
    }

    public boolean isRated(String aid) {
        try {
            Cursor c = db.query(TABLE_NAME, new String[]{
                    KEY_RATE
            }, KEY_AID + "='" + aid + "'", null, null, null, null);
            boolean result = c.getCount() > 0;
            c.close();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    public void addRate(String aid, String rate) {
        ContentValues args = new ContentValues();
        args.put(KEY_AID, aid);
        args.put(KEY_RATE, rate);
        db.insert(TABLE_NAME, null, args);
        close();
    }

}
