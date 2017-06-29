package knf.animeflv.AutoEmision;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import knf.animeflv.Utils.ExecutorManager;
import knf.animeflv.info.EmisionEditDialog;

public class AutoEmisionHelper {
    private static String KEY_PREFS_JSON = "em_list_json";
    private static String KEY_JSON_LIST = "list";
    private static String KEY_JSON_DAYCODE = "daycode";
    private static String KEY_JSON_AIDS = "aids_list";

    public static JSONObject getJson(Context context) {
        try {
            return new JSONObject(getSavedList(context));
        } catch (JSONException e) {
            return createJson(context);
        }
    }

    private static JSONObject createJson(Context context) {
        try {
            JSONObject jsonObject = new JSONObject();
            JSONArray array = new JSONArray();
            for (int i = 1; i < 8; i++) {
                JSONObject object = new JSONObject();
                object.put(KEY_JSON_DAYCODE, i);
                object.put(KEY_JSON_AIDS, new JSONArray());
                array.put(object);
            }
            jsonObject.put(KEY_JSON_LIST, array);
            saveList(context, jsonObject);
            return jsonObject;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("JSON CREATOR EMISION", "Error creating basic json");
            return null;
        }
    }

    private static void saveList(Context context, JSONObject object) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(KEY_PREFS_JSON, object.toString()).apply();
    }

    @SuppressLint("ApplySharedPref")
    private static void saveListCommit(Context context, JSONObject object) {
        Log.e("Emision Save", "Start Saving Commit");
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(KEY_PREFS_JSON, object.toString()).commit();
        Log.e("Emision Save", "Finish Saving Commit");
    }

    private static String getSavedList(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(KEY_PREFS_JSON, "not found");
    }

    @SuppressLint("ApplySharedPref")
    public static void updateSavedList(Context context, JSONObject object) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(KEY_PREFS_JSON, object.toString()).commit();
    }

    public static void asyncAddAnimetoList(final Context context, final String aid, final int daycode, final EmisionEditDialog.SearchListener listener) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    JSONObject object = getJson(context);
                    JSONArray array = object.getJSONArray(KEY_JSON_LIST).getJSONObject(daycode - 1).getJSONArray(KEY_JSON_AIDS);
                    JSONArray n_list = new JSONArray();
                    for (int i = 0; i < array.length(); i++) {
                        if (array.getString(i).equals(aid)) {
                            listener.OnError();
                            return null;
                        }
                        n_list.put(array.getString(i));
                    }
                    n_list.put(aid);
                    object.getJSONArray(KEY_JSON_LIST).getJSONObject(daycode - 1).put(KEY_JSON_AIDS, n_list);
                    saveList(context, object);
                    listener.OnResponse(null);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("ADD Emision", "Error Adding " + aid);
                    listener.OnError();
                }
                return null;
            }
        }.executeOnExecutor(ExecutorManager.getExecutor());
    }

    public static void addAnimetoList(final Context context, final String aid, final int daycode, final EmisionEditDialog.SearchListener listener) {
        try {
            JSONObject object = getJson(context);
            JSONArray array = object.getJSONArray(KEY_JSON_LIST).getJSONObject(daycode - 1).getJSONArray(KEY_JSON_AIDS);
            JSONArray n_list = new JSONArray();
            for (int i = 0; i < array.length(); i++) {
                if (array.getString(i).equals(aid)) {
                    Log.e("ADD Emision", "Already exists " + aid);
                    listener.OnError();
                    return;
                }
                n_list.put(array.getString(i));
            }
            n_list.put(aid);
            object.getJSONArray(KEY_JSON_LIST).getJSONObject(daycode - 1).put(KEY_JSON_AIDS, n_list);
            saveListCommit(context, object);
            listener.OnResponse(null);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("ADD Emision", "Error Adding " + aid);
            listener.OnError();
        }
    }

    public static void editAnimetoList(final Context context, final EmObj from, final EmObj to, final EmisionEditDialog.SearchListener listener) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                removeAnimeFromList(context, from.getAid(), from.getDaycode(), new EmisionEditDialog.SearchListener() {
                    @Override
                    public void OnResponse(EmObj obj) {
                        addAnimetoList(context, to.getAid(), to.getDaycode(), new EmisionEditDialog.SearchListener() {
                            @Override
                            public void OnResponse(EmObj obj) {
                                listener.OnResponse(to);
                            }

                            @Override
                            public void OnError() {
                                listener.OnError();
                            }
                        });
                    }

                    @Override
                    public void OnError() {
                        listener.OnError();
                    }
                });
                return null;
            }
        }.executeOnExecutor(ExecutorManager.getExecutor());
    }

    public static void asyncRemoveAnimeFromList(final Context context, final String aid, final int daycode, EmisionEditDialog.SearchListener listener) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    JSONObject object = getJson(context);
                    JSONArray array = object.getJSONArray(KEY_JSON_LIST).getJSONObject(daycode - 1).getJSONArray(KEY_JSON_AIDS);
                    JSONArray n_list = new JSONArray();
                    for (int i = 0; i < array.length(); i++) {
                        if (!array.getString(i).equals(aid))
                            n_list.put(array.getString(i));
                    }
                    object.getJSONArray(KEY_JSON_LIST).getJSONObject(daycode - 1).put(KEY_JSON_AIDS, n_list);
                    saveList(context, object);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("ADD Emision", "Error Removing " + aid);
                }
                return null;
            }
        }.executeOnExecutor(ExecutorManager.getExecutor());
    }

    public static void removeAnimeFromList(final Context context, final String aid, final int daycode, @Nullable EmisionEditDialog.SearchListener listener) {
        try {
            JSONObject object = getJson(context);
            JSONArray array = object.getJSONArray(KEY_JSON_LIST).getJSONObject(daycode - 1).getJSONArray(KEY_JSON_AIDS);
            JSONArray n_list = new JSONArray();
            for (int i = 0; i < array.length(); i++) {
                if (!array.getString(i).equals(aid))
                    n_list.put(array.getString(i));
            }
            object.getJSONArray(KEY_JSON_LIST).getJSONObject(daycode - 1).put(KEY_JSON_AIDS, n_list);
            saveListCommit(context, object);
            if (listener != null)
                listener.OnResponse(null);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("ADD Emision", "Error Removing " + aid);
            if (listener != null)
                listener.OnError();
        }
    }

    public static void removeAnimeFromList(final Context context, final String aid, @Nullable EmisionEditDialog.SearchListener listener) {
        try {
            JSONArray array = getJson(context).getJSONArray(KEY_JSON_LIST);
            for (int a = 0; a < array.length(); a++) {
                JSONObject object = array.getJSONObject(a);
                JSONArray sub = object.getJSONArray(KEY_JSON_AIDS);
                for (int i = 0; i < sub.length(); i++) {
                    if (sub.getString(i).equals(aid)) {
                        removeAnimeFromList(context, aid, object.getInt(KEY_JSON_DAYCODE), listener);
                        return;
                    }
                }
            }
            if (listener != null)
                listener.OnError();
        } catch (Exception e) {
            e.printStackTrace();
            if (listener != null)
                listener.OnError();
        }
    }

    public static int getListCount(Context context) {
        try {
            int count = 0;
            JSONArray array = getJson(context).getJSONArray(KEY_JSON_LIST);
            for (int a = 0; a < array.length(); a++) {
                JSONObject object = array.getJSONObject(a);
                JSONArray sub = object.getJSONArray(KEY_JSON_AIDS);
                count += sub.length();
            }
            return count;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static void getAnimeInfo(Context context, String aid, EmisionEditDialog.SearchListener listener) {
        try {
            JSONArray array = getJson(context).getJSONArray(KEY_JSON_LIST);
            for (int a = 0; a < array.length(); a++) {
                JSONObject object = array.getJSONObject(a);
                JSONArray sub = object.getJSONArray(KEY_JSON_AIDS);
                for (int i = 0; i < sub.length(); i++) {
                    if (sub.getString(i).equals(aid)) {
                        listener.OnResponse(new EmObj(context, aid, object.getInt(KEY_JSON_DAYCODE)));
                        return;
                    }
                }
            }
            Log.e("Search Emision", "Not Found");
            listener.OnResponse(null);
        } catch (Exception e) {
            e.printStackTrace();
            listener.OnError();
        }
    }

    public static boolean isAnimeAdded(Context context, String aid) {
        try {
            JSONArray array = getJson(context).getJSONArray(KEY_JSON_LIST);
            for (int a = 0; a < array.length(); a++) {
                JSONObject object = array.getJSONObject(a);
                JSONArray sub = object.getJSONArray(KEY_JSON_AIDS);
                for (int i = 0; i < sub.length(); i++) {
                    if (sub.getString(i).equals(aid)) {
                        return true;
                    }
                }
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<EmObj> getDayList(Context context, String arrayString, int daycode) {
        try {
            JSONArray array = new JSONArray(arrayString);
            List<EmObj> objs = new ArrayList<>();
            for (int a = 0; a < array.length(); a++) {
                objs.add(new EmObj(context, array.getString(a), daycode));
            }
            return objs;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static JSONArray getDayJson(JSONObject json, int daycode) {
        try {
            JSONObject object = json.getJSONArray(KEY_JSON_LIST).getJSONObject(daycode - 1);
            if (object.getInt(KEY_JSON_DAYCODE) == daycode) {
                return object.getJSONArray(KEY_JSON_AIDS);
            } else {
                Log.d("JSON EMISION", "Request: " + daycode + "   Obtained: " + object.getInt(KEY_JSON_DAYCODE));
                return new JSONArray();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new JSONArray();
        }
    }

    public static void asyncSaveAllDays(final Context context) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                Log.e("Emision Save", "Start Saving");
                try {
                    JSONObject object = getJson(context);
                    JSONArray array = object.getJSONArray(KEY_JSON_LIST);
                    List<List<EmObj>> lists = AutoEmisionListHolder.getAllLists();
                    for (int i = 0; i < array.length(); i++) {
                        List<EmObj> sub = lists.get(i);
                        if (sub != null) {
                            JSONObject subobject = array.getJSONObject(i);
                            JSONArray subarray = new JSONArray();
                            for (EmObj obj : sub) {
                                subarray.put(obj.getAid());
                            }
                            subobject.put(KEY_JSON_AIDS, subarray);
                        }
                    }
                    saveListCommit(context, object);
                    Log.e("Emision Save", "Saving Success");
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("Emision Save", "Saving Error");
                }
                return null;
            }
        }.executeOnExecutor(ExecutorManager.getExecutor());
    }

}
