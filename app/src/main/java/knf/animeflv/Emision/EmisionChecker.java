package knf.animeflv.Emision;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

import knf.animeflv.Emision.Section.TimeCompareModel;
import knf.animeflv.JsonFactory.BaseGetter;
import knf.animeflv.JsonFactory.JsonTypes.EMISION;
import knf.animeflv.Utils.ExecutorManager;
import knf.animeflv.Utils.MainStates;


/**
 * Created by Jordy on 05/03/2016.
 */
public class EmisionChecker {
    private static Context context;
    private static EmisionChecker checker = new EmisionChecker();

    private static List<TimeCompareModel> lcode1 = new ArrayList<>();
    private static List<TimeCompareModel> lcode2 = new ArrayList<>();
    private static List<TimeCompareModel> lcode3 = new ArrayList<>();
    private static List<TimeCompareModel> lcode4 = new ArrayList<>();
    private static List<TimeCompareModel> lcode5 = new ArrayList<>();
    private static List<TimeCompareModel> lcode6 = new ArrayList<>();
    private static List<TimeCompareModel> lcode7 = new ArrayList<>();

    public static void Ginit(Context con) {
        if (checker == null) {
            checker = new EmisionChecker();
        }
        context = con;
        //Refresh();
    }
    public static Checker init(Context context) {
        return new Checker(context);
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

    public static List<TimeCompareModel> getLcode1() {
        return lcode1;
    }

    public static void setLcode1(List<TimeCompareModel> lcode1) {
        EmisionChecker.lcode1 = lcode1;
    }

    public static List<TimeCompareModel> getLcode2() {
        return lcode2;
    }

    public static void setLcode2(List<TimeCompareModel> lcode2) {
        EmisionChecker.lcode2 = lcode2;
    }

    public static List<TimeCompareModel> getLcode3() {
        return lcode3;
    }

    public static void setLcode3(List<TimeCompareModel> lcode3) {
        EmisionChecker.lcode3 = lcode3;
    }

    public static List<TimeCompareModel> getLcode4() {
        return lcode4;
    }

    public static void setLcode4(List<TimeCompareModel> lcode4) {
        EmisionChecker.lcode4 = lcode4;
    }

    public static List<TimeCompareModel> getLcode5() {
        return lcode5;
    }

    public static void setLcode5(List<TimeCompareModel> lcode5) {
        EmisionChecker.lcode5 = lcode5;
    }

    public static List<TimeCompareModel> getLcode6() {
        return lcode6;
    }

    public static void setLcode6(List<TimeCompareModel> lcode6) {
        EmisionChecker.lcode6 = lcode6;
    }

    public static List<TimeCompareModel> getLcode7() {
        return lcode7;
    }

    public static void setLcode7(List<TimeCompareModel> lcode7) {
        EmisionChecker.lcode7 = lcode7;
    }

    public static void Refresh() {
        new EmisionCkeck(context).check();
    }

    private static class EmisionCkeck {
        private Context context;

        public EmisionCkeck(Context context) {
            this.context = context;
        }

        public void check() {
            BaseGetter.getJson(context, new EMISION(), new BaseGetter.AsyncInterface() {
                @Override
                public void onFinish(String json) {
                    start(json);
                }
            });
        }

        private void start(final String json) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    MainStates.setLoadingEmision(true);
                    try {
                        JSONObject response = new JSONObject(json);
                        Set<String> ongoing = new HashSet<>();
                        JSONArray array = response.getJSONArray("emision");
                        SharedPreferences preferences = context.getSharedPreferences("data", Context.MODE_PRIVATE);
                        List<List<TimeCompareModel>> comparelists = new ArrayList<>();
                        comparelists.add(lcode1);
                        comparelists.add(lcode2);
                        comparelists.add(lcode3);
                        comparelists.add(lcode4);
                        comparelists.add(lcode5);
                        comparelists.add(lcode6);
                        comparelists.add(lcode7);
                        for (List<TimeCompareModel> models : comparelists) {
                            models.clear();
                        }
                        if (array.length() > 0) {
                            List<TimeCompareModel> organizar = new ArrayList<>();
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject object = array.getJSONObject(i);
                                String aid = object.getString("aid");
                                String hora = object.getString("hour");
                                if (!hora.equals("null")) {
                                    preferences.edit().putInt(aid + "onday", Integer.parseInt(object.getString("daycode"))).apply();
                                    preferences.edit().putString(aid + "onhour", hora).apply();
                                    organizar.add(new TimeCompareModel(aid, context));
                                    ongoing.add(aid);
                                }
                            }
                            preferences.edit().putStringSet("ongoingSet", ongoing).apply();
                            Collections.sort(organizar, new DateCompare());
                            for (TimeCompareModel compareModel : organizar) {
                                int day = preferences.getInt(compareModel.getAid() + "onday", 0);
                                if (day != 0) {
                                    boolean isotherday = compareModel.getTime().contains("AM") && UTCtoLocalEm(compareModel.getTime()).contains("PM");
                                    if (!isotherday) {
                                        comparelists.get(day - 1).add(compareModel);
                                    } else {
                                        int daybefore;
                                        if (day - 2 <= -1) {
                                            daybefore = 7;
                                        } else {
                                            daybefore = day - 2;
                                        }
                                        comparelists.get(daybefore).add(compareModel);
                                    }
                                }
                            }
                        }
                        Log.d("EmisionChecker", "Finish Loading");
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d("EmisionChecker", "Error " + e.getCause());
                        MainStates.setLoadingEmision(false);
                    }
                    MainStates.setLoadingEmision(false);
                    return null;
                }
            }.executeOnExecutor(ExecutorManager.getExecutor());
        }
    }

}
