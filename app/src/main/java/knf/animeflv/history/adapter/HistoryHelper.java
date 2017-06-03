package knf.animeflv.history.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import knf.animeflv.Utils.FileUtil;

public class HistoryHelper {
    private static final String jsonKey="history_json";
    public static JSONArray getHistoryArray(Activity activity){
        try {
            return new JSONArray(activity.getSharedPreferences("data", Context.MODE_PRIVATE).getString(jsonKey,"null"));
        }catch (Exception e){
            e.printStackTrace();
            return new JSONArray();
        }
    }

    public static String getTitFrom(JSONArray array,int position){
        try {
            return FileUtil.corregirTit(array.getJSONObject(position).getString("titulo"));
        }catch (Exception e){
            e.printStackTrace();
            return "NF";
        }
    }

    public static String getLastFrom(JSONArray array,int position){
        try {
            return "Capítulo " + array.getJSONObject(position).getString("last");
        }catch (Exception e){
            return "NF";
        }
    }

    public static String getLastNumFrom(JSONArray array, int position) {
        try {
            return array.getJSONObject(position).getString("last");
        } catch (Exception e) {
            return "-1";
        }
    }

    public static String getAidFrom(JSONArray array,int position){
        try {
            return array.getJSONObject(position).getString("aid");
        }catch (Exception e){
            e.printStackTrace();
            return "NF";
        }
    }

    public static JSONArray delFromList(JSONArray array, int position, Activity activity){
        try {
            JSONArray tarray=new JSONArray();
            for (int i=0;i<array.length();i++){
                if (i!=position){
                    tarray.put(array.getJSONObject(i));
                }
            }
            modifyListSave(activity,tarray);
            return tarray;
        }catch (Exception e){
            e.printStackTrace();
            return array;
        }
    }

    public static JSONArray addToList(Activity activity,String aid,String tit,String last){
        try {
            JSONArray carray=getHistoryArray(activity);
            JSONObject object=new JSONObject();
            object
                    .put("aid",aid)
                    .put("titulo",tit)
                    .put("last",last);
            JSONArray tarray=new JSONArray();
            tarray.put(object);
            for (int i=0;i<carray.length();i++){
                if (!carray.getJSONObject(i).get("aid").equals(aid)) {
                    tarray.put(carray.getJSONObject(i));
                }
            }
            modifyListSave(activity,tarray);
            return tarray;
        }catch (Exception e){
            e.printStackTrace();
            return new JSONArray();
        }
    }

    private static void modifyListSave(Activity activity,JSONArray array){
        Log.d("Array",array.toString());
        activity.getSharedPreferences("data",Context.MODE_PRIVATE).edit().putString(jsonKey,array.toString()).apply();
    }
}
