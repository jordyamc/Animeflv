package knf.animeflv.JsonFactory.JsonTypes;

public class ANIME {
    public int aid;

    public ANIME(int aid) {
        this.aid = aid;
    }

    public ANIME(String aid) {
        this.aid = Integer.parseInt(aid);
    }


    public String getAidString() {
        return String.valueOf(aid);
    }
}
