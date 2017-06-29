package knf.animeflv.AutoEmision;

import android.content.Context;

import knf.animeflv.Directorio.DB.DirectoryHelper;

/**
 * Created by Jordy on 09/01/2017.
 */

public class EmObj {
    private String aid;
    private String title;
    private int daycode = 0;
    private int id = -1;

    public EmObj(Context context, String aid, int daycode) {
        this.aid = aid;
        this.title = DirectoryHelper.get(context).getTitle(aid);
        this.daycode = daycode;
        this.id = Math.abs(aid.hashCode()) + daycode;
    }

    public int getId() {
        return id;
    }

    public String getAid() {
        return aid;
    }

    public String getTitle() {
        return title;
    }

    public int getDaycode() {
        return daycode;
    }
}
