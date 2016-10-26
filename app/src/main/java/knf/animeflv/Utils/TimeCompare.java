package knf.animeflv.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class TimeCompare {
    public static Times compareFromUTC(String date) {
        try {
            SimpleDateFormat utc = new SimpleDateFormat("~hh:mmaa-F", Locale.ENGLISH);
            utc.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date formatedUtc = utc.parse(date);
            utc.setTimeZone(TimeZone.getDefault());
            String converted = utc.format(formatedUtc);
            int normalDay = Integer.parseInt(date.split("-")[1]);
            int convertedDay = Integer.parseInt(converted.split("-")[1]);
            if (normalDay == convertedDay) {
                return Times.SAME;
            } else if (convertedDay > normalDay) {
                return Times.AFTER;
            } else {
                return Times.BEFORE;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Times.SAME;
        }
    }

    public static Times compareFromNormal(String date) {
        try {
            SimpleDateFormat utc = new SimpleDateFormat("~hh:mmaa-F", Locale.ENGLISH);
            utc.setTimeZone(TimeZone.getDefault());
            Date formatedUtc = utc.parse(date);
            utc.setTimeZone(TimeZone.getTimeZone("UTC"));
            String converted = utc.format(formatedUtc);
            int normalDay = Integer.parseInt(date.split("-")[1]);
            int convertedDay = Integer.parseInt(converted.split("-")[1]);
            if (normalDay == convertedDay) {
                return Times.SAME;
            } else if (convertedDay > normalDay) {
                return Times.AFTER;
            } else {
                return Times.BEFORE;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Times.SAME;
        }
    }

    public static int getFormatedDaycodeFromUTC(String date) {
        try {
            SimpleDateFormat utc = new SimpleDateFormat("~hh:mmaa-d", Locale.ENGLISH);
            utc.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date formatedUtc = utc.parse(date);
            utc.setTimeZone(TimeZone.getDefault());
            String converted = utc.format(formatedUtc);
            int day = Integer.parseInt(converted.split("-")[1]);
            if (date.endsWith("1") && day > 7) {
                return 7;
            } else if (date.endsWith("7") && day > 7) {
                return 1;
            } else {
                return day;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Integer.parseInt(date.split("-")[1]);
        }
    }

    public static int getFormatedDaycodeFromNormal(String date) {
        try {
            SimpleDateFormat utc = new SimpleDateFormat("~hh:mmaa-d", Locale.ENGLISH);
            utc.setTimeZone(TimeZone.getDefault());
            Date formatedUtc = utc.parse(date);
            utc.setTimeZone(TimeZone.getTimeZone("UTC"));
            String converted = utc.format(formatedUtc);
            int day = Integer.parseInt(converted.split("-")[1]);
            if (date.endsWith("1") && day > 7) {
                return 7;
            } else if (date.endsWith("7") && day > 7) {
                return 1;
            } else {
                return day;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Integer.parseInt(date.split("-")[1]);
        }
    }

    public enum Times {
        BEFORE(0),
        SAME(1),
        AFTER(2);
        int value;

        Times(int value) {
            this.value = value;
        }
    }
}
