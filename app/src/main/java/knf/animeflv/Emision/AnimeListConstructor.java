package knf.animeflv.Emision;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class AnimeListConstructor {
    private String[] eids;
    private String[] tipos;
    private List<String> fechas_fin;
    private List<String> daycode;
    private List<String> hours;

    public AnimeListConstructor(String[] eids, String[] tipos, List<String> fechas_fin, List<String> daycode, List<String> hours) {
        this.eids = eids;
        this.tipos = tipos;
        this.fechas_fin = fechas_fin;
        this.daycode = daycode;
        this.hours = hours;
    }

    public List<AnimeModel> list() {
        List<AnimeModel> list = new ArrayList<>();
        for (int i = 0; i < eids.length; i++) {
            list.add(new AnimeModel(eids[i], tipos[i], fechas_fin.get(i), hours.get(i), Integer.parseInt(daycode.get(i))));
        }
        return list;
    }
}
