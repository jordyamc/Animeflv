package knf.animeflv.Emision;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import knf.animeflv.Emision.Section.TimeCompareModel;

public class DateCompare implements Comparator<TimeCompareModel> {
    private SimpleDateFormat dateFormat = new SimpleDateFormat("~hh:mmaa", Locale.ENGLISH);

    @Override
    public int compare(TimeCompareModel lhs, TimeCompareModel rhs) {
        try {
            return dateFormat.parse(UTCtoLocal(lhs.getTime())).compareTo(dateFormat.parse(UTCtoLocal(rhs.getTime())));
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private String UTCtoLocal(String utc) {
        String convert = "";
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
}
