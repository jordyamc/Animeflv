package knf.animeflv.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import knf.animeflv.ColorsRes;
import knf.animeflv.Favorites.FavotiteDB;
import knf.animeflv.LoginActivity.DropboxManager;
import knf.animeflv.Parser;

/**
 * Created by Jordy on 03/05/2017.
 */

public class BackupUtil {
    private static final int DEF = 0;
    private static final int CUSTOM = 1;
    private static final File SAVE_FILE = new File(Environment.getExternalStorageDirectory() + "/Animeflv/cache/data.save");

    public static void backup(Context context) {
        try {
            JSONObject object = new JSONObject();
            JSONArray array = new JSONArray();
            array.put(ConfigurationBackups.getLogins(context));
            array.put(ConfigurationBackups.getNotifications(context));
            array.put(ConfigurationBackups.getConections(context));
            array.put(ConfigurationBackups.getSearch(context));
            array.put(ConfigurationBackups.getPlayers(context));
            array.put(ConfigurationBackups.getFavs(context));
            array.put(ConfigurationBackups.getDownloads(context));
            array.put(ConfigurationBackups.getSugestions(context));
            array.put(ConfigurationBackups.getExtras(context));
            array.put(ConfigurationBackups.getTheme(context));
            object.put("list", array);
            if (SAVE_FILE.exists()) {
                FileUtil.writeToFile(object.toString(), SAVE_FILE);
            } else {
                if (SAVE_FILE.createNewFile()) {
                    FileUtil.writeToFile(object.toString(), SAVE_FILE);
                } else {
                    Log.e("Configuration Backup", "Error al crear archivo de respaldo");
                }
            }
        } catch (Exception e) {
            Log.e("Configuration Backup", "Error al crear respaldo");
            e.printStackTrace();
        }
    }

    public static Parser.Response restore(Context context) {
        try {
            JSONArray array = new JSONObject(FileUtil.getStringFromFile(SAVE_FILE)).getJSONArray("list");
            for (int i = 0; i < array.length(); i++) {
                try {
                    JSONObject object = array.getJSONObject(i);
                    Log.e("Restoring", object.getString("name"));
                    SharedPreferences.Editor preferences = Shared.Default(context).edit();
                    if (!object.getString("name").equals("theme")) {
                        JSONArray sub = object.getJSONArray("list");
                        for (int o = 0; o < sub.length(); o++) {
                            JSONObject pref = sub.getJSONObject(o);
                            String name = pref.getString("name");
                            if (!name.equals("favoritos") && !name.equals("accentColor") && !name.equals("theme")) {
                                Object value = pref.get("value");
                                if (value instanceof String) {
                                    preferences.putString(name, (String) value);
                                } else if (value instanceof Boolean) {
                                    preferences.putBoolean(name, (Boolean) value);
                                }
                            } else if (name.equals("favoritos")) {
                                new FavotiteDB(context).updatebyJSON(pref.getJSONObject("value"), null);
                            } else {
                                preferences.putInt(name, pref.getInt("value"));
                            }
                        }
                    } else {
                        JSONArray sub = object.getJSONArray("theme");
                        for (int o = 0; o < sub.length(); o++) {
                            JSONObject pref = sub.getJSONObject(o);
                            String key = pref.getString("key");
                            if (key.equals(ThemeUtils.Theme.KEY_DARK)) {
                                preferences.putBoolean(key, pref.getBoolean("value"));
                            } else {
                                preferences.putInt(key, pref.getInt("value"));
                            }
                        }
                    }
                    Log.e("Restoring", object.getString("name") + " Complete!!!!!");
                    preferences.apply();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return Parser.Response.OK;
        } catch (Exception e) {
            e.printStackTrace();
            return Parser.Response.ERROR;
        }
    }

    private static class ConfigurationBackups {
        static JSONObject getLogins(Context context) {
            JSONObject object = new JSONObject();
            JSONArray array = new JSONArray();
            try {
                object.put("name", "Logins");
                JSONObject o = new JSONObject();
                o.put("name", "favoritos");
                o.put("value", new FavotiteDB(context).getDBJSON(true));
                array.put(o);
                addPrefs(context, object, array, getLogs(context));
                return object;
            } catch (Exception e) {
                e.printStackTrace();
                return object;
            }
        }

        static JSONObject getNotifications(Context context) {
            JSONObject object = new JSONObject();
            JSONArray array = new JSONArray();
            try {
                object.put("name", "Notificaciones");
                addPrefs(context, object, array, getNots());
                return object;
            } catch (Exception e) {
                e.printStackTrace();
                return object;
            }
        }

        static JSONObject getConections(Context context) {
            JSONObject object = new JSONObject();
            JSONArray array = new JSONArray();
            try {
                object.put("name", "Conexiones");
                addPrefs(context, object, array, getCon());
                return object;
            } catch (Exception e) {
                e.printStackTrace();
                return object;
            }
        }

        static JSONObject getSearch(Context context) {
            JSONObject object = new JSONObject();
            JSONArray array = new JSONArray();
            try {
                object.put("name", "Busqueda");
                addPrefs(context, object, array, getSear());
                return object;
            } catch (Exception e) {
                e.printStackTrace();
                return object;
            }
        }

        static JSONObject getPlayers(Context context) {
            JSONObject object = new JSONObject();
            JSONArray array = new JSONArray();
            try {
                object.put("name", "Reproductores");
                addPrefs(context, object, array, getPlays());
                return object;
            } catch (Exception e) {
                e.printStackTrace();
                return object;
            }
        }

        static JSONObject getFavs(Context context) {
            JSONObject object = new JSONObject();
            JSONArray array = new JSONArray();
            try {
                object.put("name", "Favoritos");
                addPrefs(context, object, array, getFavs());
                return object;
            } catch (Exception e) {
                e.printStackTrace();
                return object;
            }
        }

        static JSONObject getDownloads(Context context) {
            JSONObject object = new JSONObject();
            JSONArray array = new JSONArray();
            try {
                object.put("name", "Descargas");
                addPrefs(context, object, array, getDowns());
                return object;
            } catch (Exception e) {
                e.printStackTrace();
                return object;
            }
        }

        static JSONObject getSugestions(Context context) {
            JSONObject object = new JSONObject();
            JSONArray array = new JSONArray();
            try {
                object.put("name", "Sugerencias");
                addPrefs(context, object, array, getSugs());
                return object;
            } catch (Exception e) {
                e.printStackTrace();
                return object;
            }
        }

        static JSONObject getExtras(Context context) {
            JSONObject object = new JSONObject();
            JSONArray array = new JSONArray();
            try {
                object.put("name", "Extras");
                addPrefs(context, object, array, getExtras());
                return object;
            } catch (Exception e) {
                e.printStackTrace();
                return object;
            }
        }

        private static void addPrefs(Context context, JSONObject object, JSONArray array, Pref[] prefs) throws Exception {
            for (Pref pref : prefs) {
                JSONObject item = new JSONObject();
                item.put("name", pref.getName());
                if (pref.getValue() instanceof Integer) {
                    item.put("value", Shared.I(context, pref.getName(), (int) pref.getValue()));
                } else {
                    item.put("value", (pref.getValue() instanceof String) ? Shared.S(context, pref.getName(), (String) pref.getValue()) : Shared.B(context, pref.getName(), (Boolean) pref.getValue()));
                }
                array.put(item);
            }
            object.put("list", array);
        }

        private static JSONObject getTheme(Context context) throws JSONException {
            JSONObject object = ThemeUtils.Theme.create(context).toJson();
            object.put("name", "theme");
            return object;
        }

        private static Pref[] getLogs(Context context) {
            return new Pref[]{
                    new Pref<>("login_email", "null"),
                    new Pref<>("login_email_coded", "null"),
                    new Pref<>("ogin_pass_coded", "null"),
                    new Pref<>(DropboxManager.KEY_DROPBOX, ""),
                    new Pref<>("accentColor", ColorsRes.Naranja(context)),
            };
        }

        private static Pref[] getNots() {
            return new Pref[]{
                    new Pref<>("notificaciones", true),
                    new Pref<>("notFavs", false),
                    new Pref<>("autoDesc", false),
                    new Pref<>("tiempo", "60000"),
                    new Pref<>("sonido", "0"),
                    new Pref<>("ind_sounds", "0")
            };
        }

        private static Pref[] getCon() {
            return new Pref[]{
                    new Pref<>("t_conexion", "2"),
                    new Pref<>("bypass_time", "30000")
            };
        }

        private static Pref[] getSear() {
            return new Pref[]{
                    new Pref<>("t_busqueda", "0"),
                    new Pref<>("ord_busqueda", "0")
            };
        }

        private static Pref[] getPlays() {
            return new Pref[]{
                    new Pref<>("t_video", "0"),
                    new Pref<>("t_streaming", "0"),
                    new Pref<>("t_player", "0")
            };
        }

        private static Pref[] getFavs() {
            return new Pref[]{
                    new Pref<>("section_favs", false),
                    new Pref<>("sort_fav", "0")
            };
        }

        private static Pref[] getDowns() {
            return new Pref[]{
                    new Pref<>("def_download", "0")
            };
        }

        private static Pref[] getSugs() {
            return new Pref[]{
                    new Pref<>("skip_favs", true),
                    new Pref<>("sug_order", "0")
            };
        }

        private static Pref[] getExtras() {
            return new Pref[]{
                    new Pref<>("resaltar", true),
                    new Pref<>("autoUpdate", false),
                    new Pref<>("statusShown", false),
                    new Pref<>("force_phone", false)
            };
        }
    }

    private static class Shared {
        static SharedPreferences Default(Context context) {
            return PreferenceManager.getDefaultSharedPreferences(context);
        }

        private static SharedPreferences Custom(Context context) {
            return context.getSharedPreferences("data", Context.MODE_PRIVATE);
        }

        static String S(Context context, String key, String value) {
            return Default(context).getString(key, value);
        }

        static boolean B(Context context, String key, boolean value) {
            return Default(context).getBoolean(key, value);
        }

        static int I(Context context, String key, int value) {
            return Default(context).getInt(key, value);
        }
    }

    private static class Pref<T> {
        private String name;
        private T value;

        Pref(String name, @Nullable T value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public T getValue() {
            return value;
        }
    }
}
