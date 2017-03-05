package knf.animeflv.JsonFactory.JsonTypes;

public class ANIME {
    public int aid;

    public ANIME(int aid) {
        this.aid = aid;
    }

    public ANIME(String aid) {
        try {
            this.aid = Integer.parseInt(aid);
        } catch (Exception e) {
            this.aid = -1;
        }
    }


    public String getAidString() {
        return String.valueOf(aid);
    }
}
