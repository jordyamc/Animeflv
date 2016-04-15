package knf.animeflv.Emision.Section;

import android.content.Context;
import android.content.SharedPreferences;

import knf.animeflv.Parser;
import knf.animeflv.TaskType;

/**
 * Created by Jordy on 05/03/2016.
 */
public class TimeCompareModel {
    private String aid;
    private Context context;
    private Parser parser = new Parser();
    private SharedPreferences preferences;
    private boolean nodata = false;
    private String image;
    private String titulo;
    private String time;

    public TimeCompareModel(String aid, Context context) {
        this.aid = aid;
        this.context = context;
        preferences = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        this.image = getUrl();
        this.titulo = settit();
        this.time = preferences.getString(aid + "onhour", "~00:00AM");
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
            return time;
        } else {
            return "~00:00AM";
        }
    }

    public String getImage() {
        if (!nodata) {
            return image;
        } else {
            return "null";
        }
    }

    private String getUrl() {
        return parser.getBaseUrl(TaskType.NORMAL, context) + "imagen.php?certificate=" + parser.getCertificateSHA1Fingerprint(context) + "&thumb=" + "http://cdn.animeflv.net/img/portada/thumb_80/" + aid + ".jpg";
    }

    private String settit() {
        return parser.getTitCached(aid);
    }

    public String getTitulo() {
        if (!nodata) {
            return titulo;
        } else {
            return "null";
        }
    }
}
