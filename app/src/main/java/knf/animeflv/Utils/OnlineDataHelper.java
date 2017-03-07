package knf.animeflv.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class OnlineDataHelper {
    private static final String DATA_TABLE_NAME = "ONLINE_DATA_CONFIGURATION";
    private static final String KEY_TABLE_ID = "_table_id";
    private static final String KEY_TYPE_ID = "_type_id";
    private static final String KEY_NAME = "_name";
    private static final String KEY_DESCRIPTION = "_description";
    private static final String KEY_HAVE_MESSAGE = "_have_message";
    private static final String KEY_MESSAGE = "_message";
    private static final String KEY_ALIAS = "_alias";
    private static final String DATABASE_CREATE =
            "create table if not exists " + DATA_TABLE_NAME + "("
                    + KEY_TABLE_ID + " integer primary key autoincrement, "
                    + KEY_TYPE_ID + " integer, "
                    + KEY_NAME + " text not null, "
                    + KEY_DESCRIPTION + " text not null, "
                    + KEY_HAVE_MESSAGE + " integer, "
                    + KEY_MESSAGE + " text not null, "
                    + KEY_ALIAS + " text not null"
                    + ");";
    private static final String DATABASE_NAME = "ONLINE_CHANGE_DATA";
    public static int TYPE_CONTRIBUTOR = 0;
    public static int TYPE_ALPHA = 1;
    public static int TYPE_BETA = 2;
    protected Context context;
    private SQLiteDatabase db;

    private OnlineDataHelper(Context context) {
        this.context = context;
        try {
            db = context.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE, null, null);
            db.execSQL(DATABASE_CREATE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static OnlineDataHelper get(Context context) {
        return new OnlineDataHelper(context);
    }

    public static void update(final Context context, final JSONObject object) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    OnlineDataHelper.get(context).reset();
                    OnlineDataHelper dataHelper = OnlineDataHelper.get(context);
                    JSONArray array = object.getJSONArray("contributors");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject sub = array.getJSONObject(i);
                        dataHelper.addElement(new PersonItem(TYPE_CONTRIBUTOR, sub));
                    }
                    array = object.getJSONArray("alpha");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject sub = array.getJSONObject(i);
                        dataHelper.addElement(new PersonItem(TYPE_ALPHA, sub));
                    }
                    array = object.getJSONArray("beta");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject sub = array.getJSONObject(i);
                        dataHelper.addElement(new PersonItem(TYPE_BETA, sub));
                    }
                    dataHelper.close();
                } catch (Exception e) {
                    Log.e("Persons DB Error", "Json parsing");
                    e.printStackTrace();
                }
                return null;
            }
        }.executeOnExecutor(ExecutorManager.getExecutor());
    }

    public void close() {
        try {
            db.close();
        } catch (NullPointerException e) {
            Log.e("On Close DB", "DB is Null!!!!!");
        }
    }

    public void addElement(PersonItem object) {
        ContentValues values = new ContentValues();
        values.put(KEY_TYPE_ID, object.type);
        values.put(KEY_NAME, object.name);
        values.put(KEY_DESCRIPTION, object.description);
        values.put(KEY_HAVE_MESSAGE, object.haveMessage);
        values.put(KEY_MESSAGE, object.message);
        values.put(KEY_ALIAS, object.alias);
        db.insert(DATA_TABLE_NAME, null, values);
    }

    public List<PersonItem> getPersons(int type) {
        List<PersonItem> list = new ArrayList<>();
        Cursor c = db.query(DATA_TABLE_NAME, new String[]{
                KEY_NAME,
                KEY_DESCRIPTION,
                KEY_HAVE_MESSAGE,
                KEY_MESSAGE,
                KEY_ALIAS
        }, KEY_TYPE_ID + "='" + type + "'", null, null, null, null);
        if (c.getCount() > 0) {
            while (c.moveToNext()) {
                PersonItem item = new PersonItem(
                        type,
                        c.getString(0),
                        c.getString(1),
                        c.getInt(2),
                        c.getString(3),
                        c.getString(4)
                );
                list.add(item);
            }
        }
        c.close();
        return list;
    }

    public void reset() {
        db.delete(DATA_TABLE_NAME, null, null);
        close();
    }

    public static class PersonItem {
        public int type;
        public String name;
        public String description;
        public int haveMessage;
        public String message;
        public String alias;

        public PersonItem(int type, String name, String description, int haveMessage, String message, String alias) {
            this.type = type;
            this.name = name;
            this.description = description;
            this.haveMessage = haveMessage;
            this.message = message;
            this.alias = alias;
        }

        public PersonItem(int type, JSONObject object) throws JSONException {
            this.type = type;
            this.name = object.getString("name");
            this.description = object.getString("description");
            this.haveMessage = object.getInt("onclick_message");
            this.message = object.getString("message");
            this.alias = object.getString("alias");
        }
    }
}