package knf.animeflv.AutoEmision;

import knf.animeflv.Parser;

/**
 * Created by Jordy on 09/01/2017.
 */

public class EmObj {
    private String aid;
    private String title;
    private int daycode = 0;
    private int id = -1;

    public EmObj(String aid, int daycode) {
        this.aid = aid;
        this.title = new Parser().getTitCached(aid);
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
