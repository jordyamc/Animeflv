package knf.animeflv.JsonFactory.JsonTypes;

public class ANIME {
    public int aid;

    public ANIME(int aid) {
        this.aid = aid;
    }

    public String getAidString() {
        return String.valueOf(aid);
    }
}
