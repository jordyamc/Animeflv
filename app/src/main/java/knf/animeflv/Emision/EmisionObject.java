package knf.animeflv.Emision;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class EmisionObject {
    public String aid = "0";
    public String titulo = "null";
    public String hour = "~00:00AM";
    public String daycode = "0";
    public boolean isValid = false;
    public boolean exist = false;

    public EmisionObject(JSONObject object) {
        try {
            if (object.getString("response").equals("ok")) {
                this.aid = object.getString("aid");
                this.titulo = object.getString("titulo");
                this.hour = UTCtoLocalEm(object.getString("hour"));
                this.daycode = object.getString("daycode");
                this.exist = object.getBoolean("exist");
                this.isValid = true;
            } else {
                this.isValid = false;
            }
        } catch (JSONException e) {
            try {
                if (object.getString("response").equals("ok")) {
                    this.exist = object.getBoolean("exist");
                    this.isValid = true;
                } else {
                    Log.e("Response", object.toString());
                    this.isValid = false;
                }
            } catch (JSONException ex) {
                e.printStackTrace();
                this.isValid = false;
            }

        }
    }

    public EmisionObject() {
        this.isValid = false;
    }

    public EmisionObject(String aid, String titulo, String hour, String daycode) {
        this.aid = aid;
        this.titulo = titulo;
        this.hour = hour;
        this.daycode = daycode;
        this.isValid = true;
        this.exist = true;
    }

    public EmisionObject(String aid, boolean delete) {
        this.aid = aid;
        this.exist = !delete;
        this.isValid = true;
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

    private static String LocalToUTC(String utc) {
        String convert = "~00:00PM";
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("~hh:mmaa", Locale.ENGLISH);
            simpleDateFormat.setTimeZone(TimeZone.getDefault());
            Date myDate = simpleDateFormat.parse(utc);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            convert = simpleDateFormat.format(myDate);
        } catch (Exception e) {
            e.printStackTrace();
            convert = utc;
        }
        return convert;
    }

    public String getCodedTitle() {
        try {
            return URLEncoder.encode(titulo, "utf-8");
        } catch (Exception e) {
            return titulo.replace(" ", "+");
        }
    }

    public String getUTCHour() {
        return LocalToUTC(hour);
    }
}
