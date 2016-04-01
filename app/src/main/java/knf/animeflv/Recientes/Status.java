package knf.animeflv.Recientes;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

import knf.animeflv.Emision.DateCompare;
import knf.animeflv.Emision.Section.TimeCompareModel;

public class Status {
    private static Context context;
    private static String server_version = "0.0";
    private static String cache = "0";
    private static String late = "00:00";
    private static Status status = new Status();
    private static Bundle code1 = new Bundle();
    private static Bundle code2 = new Bundle();
    private static Bundle code3 = new Bundle();
    private static Bundle code4 = new Bundle();
    private static Bundle code5 = new Bundle();
    private static Bundle code6 = new Bundle();
    private static Bundle code7 = new Bundle();

    public static Status reload(Context cont) {
        context = cont;
        String file_loc = Environment.getExternalStorageDirectory() + "/Animeflv/cache/inicio.txt";
        File file = new File(file_loc);
        if (file.exists()) {
            try {
                JSONObject object = new JSONObject(getStringFromFile(file_loc));
                server_version = object.getString("version");
                cache = object.getString("cache");
                late = object.getString("last");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        new EmisionCkeck(context).execute();
        return status;
    }

    public static String getVersion() {
        return server_version;
    }

    public static String getCacheStatus() {
        return cache;
    }

    public static String getCacheStatusString() {
        String s = "OK";
        if (getCacheStatusInt() == 1) {
            s = "DESACTUALIZADO";
        }
        return s;
    }

    public static int getCacheStatusInt() {
        return Integer.parseInt(cache);
    }

    public static String getLateRefresh() {
        return UTCtoLocal(late);
    }

    private static String UTCtoLocal(String utc) {
        String convert = "00:00";
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mmaa", Locale.ENGLISH);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date myDate = simpleDateFormat.parse(utc);
            simpleDateFormat.setTimeZone(TimeZone.getDefault());
            convert = simpleDateFormat.format(myDate);
        } catch (Exception e) {
            e.printStackTrace();
            convert = utc;
        }
        return convert;
    }

    private static String UTCtoLocalEm(String utc) {
        String convert = "~00:00PM";
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("~hh:mmaa", Locale.ENGLISH);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date myDate = simpleDateFormat.parse(utc);
            simpleDateFormat.setTimeZone(TimeZone.getDefault());
            convert = simpleDateFormat.format(myDate);
        } catch (Exception e) {
            e.printStackTrace();
            convert = utc;
        }
        return convert;
    }

    private static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = "";
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        reader.close();
        return sb.toString();
    }

    private static String getStringFromFile(String filePath) {
        String ret = "";
        try {
            File fl = new File(filePath);
            FileInputStream fin = new FileInputStream(fl);
            ret = convertStreamToString(fin);
            fin.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    public static Bundle getCode1() {
        return code1;
    }

    public static Bundle getCode2() {
        return code2;
    }

    public static Bundle getCode3() {
        return code3;
    }

    public static Bundle getCode4() {
        return code4;
    }

    public static Bundle getCode5() {
        return code5;
    }

    public static Bundle getCode6() {
        return code6;
    }

    public static Bundle getCode7() {
        return code7;
    }

    private static class EmisionCkeck extends AsyncTask<String, String, String> {
        Context context;

        public EmisionCkeck(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... params) {
            SharedPreferences preferences = context.getSharedPreferences("data", Context.MODE_PRIVATE);
            Set<String> ongoing = preferences.getStringSet("ongoingSet", new HashSet<String>());
            ArrayList<String> c1 = new ArrayList<>();
            ArrayList<String> c2 = new ArrayList<>();
            ArrayList<String> c3 = new ArrayList<>();
            ArrayList<String> c4 = new ArrayList<>();
            ArrayList<String> c5 = new ArrayList<>();
            ArrayList<String> c6 = new ArrayList<>();
            ArrayList<String> c7 = new ArrayList<>();
            List<ArrayList<String>> listas = new ArrayList<>();
            listas.add(c1);
            listas.add(c2);
            listas.add(c3);
            listas.add(c4);
            listas.add(c5);
            listas.add(c6);
            listas.add(c7);
            if (!ongoing.isEmpty()) {
                List<TimeCompareModel> organizar = new ArrayList<>();
                for (String aid : ongoing) {
                    TimeCompareModel compareModel = new TimeCompareModel(aid, context);
                    if (!compareModel.getTime().equals("null"))
                        organizar.add(compareModel);
                }
                Collections.sort(organizar, new DateCompare());
                for (TimeCompareModel compareModel : organizar) {
                    int day = preferences.getInt(compareModel.getAid() + "onday", 0);
                    if (day != 0) {
                        boolean isotherday = compareModel.getTime().contains("AM") && UTCtoLocalEm(compareModel.getTime()).contains("PM");
                        if (!isotherday) {
                            listas.get(day - 1).add(compareModel.getAid());
                        } else {
                            int daybefore;
                            if (day - 2 <= -1) {
                                daybefore = 7;
                            } else {
                                daybefore = day - 2;
                            }
                            listas.get(daybefore).add(compareModel.getAid());
                        }
                    }
                }
            }
            code1 = new Bundle();
            code1.putInt("code", 1);
            code1.putStringArrayList("list", c1);
            code2 = new Bundle();
            code2.putInt("code", 2);
            code2.putStringArrayList("list", c2);
            code3 = new Bundle();
            code3.putInt("code", 3);
            code3.putStringArrayList("list", c3);
            code4 = new Bundle();
            code4.putInt("code", 4);
            code4.putStringArrayList("list", c4);
            code5 = new Bundle();
            code5.putInt("code", 5);
            code5.putStringArrayList("list", c5);
            code6 = new Bundle();
            code6.putInt("code", 6);
            code6.putStringArrayList("list", c6);
            code7 = new Bundle();
            code7.putInt("code", 7);
            code7.putStringArrayList("list", c7);
            return "";
        }
    }
}
