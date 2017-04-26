package knf.animeflv.Suggestions.Algoritm;

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

public class SuggestionDB {
    private static final String TABLE_NAME = "SUGGESTION_POINTS";
    private static final String KEY_TABLE_ID = "_table_id";
    private static final String KEY_GENRE = "_genre_name";
    private static final String KEY_POINTS = "_points";
    private static final String DATABASE_CREATE =
            "create table if not exists " + TABLE_NAME + "("
                    + KEY_TABLE_ID + " integer primary key autoincrement, "
                    + KEY_GENRE + " text not null, "
                    + KEY_POINTS + " integer"
                    + ");";
    private static final String DATABASE_NAME = "AUTO_LEARN_POINTS";
    private static final String NEW_COUNT = "NEW_COUNT";
    private static final String OLD_COUNT = "OLD_COUNT";
    protected Context context;
    private SQLiteDatabase db;

    private SuggestionDB(Context context) {
        this.context = context;
        try {
            db = context.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE, null, null);
            db.execSQL(DATABASE_CREATE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static SuggestionDB get(Context context) {
        return new SuggestionDB(context);
    }

    public void close() {
        try {
            db.close();
        } catch (NullPointerException e) {
            Log.e("On Close DB", "DB is Null!!!!!");
        }
    }

    private boolean existGenre(String genre) {
        try {
            Cursor c = db.query(TABLE_NAME, new String[]{
                    KEY_GENRE
            }, KEY_GENRE + "='" + genre + "'", null, null, null, null);
            boolean result = c.getCount() > 0;
            c.close();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    private void addGenre(String genre, int value) {
        ContentValues args = new ContentValues();
        args.put(KEY_GENRE, genre);
        args.put(KEY_POINTS, value);
        db.insert(TABLE_NAME, null, args);
    }

    private void updateGenre(String genre, int value) {
        ContentValues args = new ContentValues();
        args.put(KEY_POINTS, value);
        db.update(TABLE_NAME, args, KEY_GENRE + "='" + genre + "'", null);
        close();
    }

    private int getCount(String genre) {
        try {
            Cursor c = db.query(TABLE_NAME, new String[]{
                    KEY_POINTS
            }, KEY_GENRE + "='" + genre + "'", null, null, null, null);
            if (c.getCount() > 0) {
                c.moveToFirst();
                int count = c.getInt(0);
                c.close();
                return count;
            } else {
                return 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    void register(String genre, int value, boolean isOld) {
        int count = getCount(genre) + value;
        if (count < 0) count = 0;
        if (existGenre(genre)) {
            updateGenre(genre, count);
        } else {
            addGenre(genre, count);
        }
        close();
    }

    List<Suggestion> getSuggestions() {
        List<Suggestion> list = new ArrayList<>();
        try {
            Cursor c = db.query(TABLE_NAME, new String[]{
                    KEY_GENRE,
                    KEY_POINTS
            }, null, null, null, null, null);
            if (c.getCount() > 0) {
                while (c.moveToNext()) {
                    list.add(new Suggestion(c.getString(0), c.getInt(1)));
                }
                c.close();
                close();
                return list;
            } else {
                c.close();
                close();
                return list;
            }
        } catch (Exception e) {
            e.printStackTrace();
            close();
            return list;
        }
    }

    public class Suggestion {
        public String name;
        public int count;

        public Suggestion(String name, int count) {
            this.name = name;
            this.count = count;
        }
    }

}
