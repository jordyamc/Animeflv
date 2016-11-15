package knf.animeflv.DownloadService;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import knf.animeflv.Parser;

public class DownloadListManager {
    private static final String DOWNLOAD_QUEQUE = "download_queque_list_json";
    private static final String KEY_DOWNLOAD_ID = "eid_downloadID";

    public static void add(Context activity, String eid_code) {
        try {
            SharedPreferences preferences = activity.getSharedPreferences("data", Context.MODE_PRIVATE);
            JSONArray list = new JSONArray(preferences.getString(DOWNLOAD_QUEQUE, new JSONArray().toString()));
            JSONObject object = new JSONObject();
            object.put(KEY_DOWNLOAD_ID, eid_code);
            list.put(object);
            preferences.edit().putString(DOWNLOAD_QUEQUE, list.toString()).apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public static String getAsString(Context activity, String eid) {
        try {
            SharedPreferences preferences = activity.getSharedPreferences("data", Context.MODE_PRIVATE);
            String eid_code = eid + "_" + preferences.getLong(eid + "_downloadID", -1);
            JSONArray list = new JSONArray(preferences.getString(DOWNLOAD_QUEQUE, new JSONArray().toString()));
            StringBuilder builder = new StringBuilder();
            for (int i = 1; i < list.length(); i++) {
                try {
                    String e = list.getJSONObject(i).getString(KEY_DOWNLOAD_ID);
                    if (!e.equals(eid_code)) {
                        builder.append(new Parser().getTitCached(e.split("_")[0]));
                        builder.append(" ");
                        builder.append(e.split("_")[1].replace("E", ""));
                        builder.append("\n");
                    }
                } catch (Exception e) {
                    return "";
                }

            }
            return builder.toString().trim();
        } catch (Exception e) {
            return "";
        }
    }

    public static void delete(Context activity, String eid_code) {
        try {
            SharedPreferences preferences = activity.getSharedPreferences("data", Context.MODE_PRIVATE);
            JSONArray list = new JSONArray(preferences.getString(DOWNLOAD_QUEQUE, new JSONArray().toString()));
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < list.length(); i++) {
                String eid = list.getJSONObject(i).getString(KEY_DOWNLOAD_ID);
                if (!eid.equals(eid_code)) {
                    jsonArray.put(list.getJSONObject(i));
                }
            }
            preferences.edit().putString(DOWNLOAD_QUEQUE, jsonArray.toString()).apply();
        } catch (JSONException e) {

        }
    }
}
