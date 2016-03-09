package knf.animeflv.Emision.Section;

import android.content.Context;
import android.content.SharedPreferences;

import knf.animeflv.Parser;
import knf.animeflv.TaskType;

/**
 * Created by Jordy on 05/03/2016.
 */
public class TimeCompareModel {
    String aid;
    Context context;
    Parser parser = new Parser();
    SharedPreferences preferences;
    boolean nodata = false;

    public TimeCompareModel(String aid, Context context) {
        this.aid = aid;
        this.context = context;
        preferences = context.getSharedPreferences("data", Context.MODE_PRIVATE);
    }

    public TimeCompareModel() {
        this.nodata = true;
    }

    public String getAid() {
        if (!nodata) {
            return aid;
        } else {
            return "-1";
        }
    }

    public String getTime() {
        if (!nodata) {
            return preferences.getString(aid + "onhour", "~00:00AM");
        } else {
            return "~00:00AM";
        }
    }

    public String getImage() {
        if (!nodata) {
            return parser.getBaseUrl(TaskType.NORMAL, context) + "imagen.php?certificate=" + parser.getCertificateSHA1Fingerprint(context) + "&thumb=" + "http://cdn.animeflv.net/img/portada/thumb_80/" + aid + ".jpg";
        } else {
            return "null";
        }
    }

    public String getTitulo() {
        if (!nodata) {
            return parser.getTitCached(aid);
        } else {
            return "null";
        }
    }
}
