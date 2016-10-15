package knf.animeflv.DownloadService.ServiceHolder;

import android.content.Context;

import knf.animeflv.DownloadService.DataBaseHelper.SQLiteHelper;

/**
 * Created by Jordy on 26/07/2016.
 */

public class ServiceManager {
    public static void add(Context context, DownloadConstructor constructor) {
        SQLiteHelper helper = new SQLiteHelper(context);
        helper.addElement(constructor);
        helper.close();
    }

    public static void delete(Context context, String eid) {
        SQLiteHelper helper = new SQLiteHelper(context);
        helper.delete(eid);
        helper.close();
    }

    public static class DownloadConstructor {
        public String url;
        public String eid;
        public String aid;
        public String numero;
        public String titulo;
        public int id;

        public DownloadConstructor(String url, String eid, String titulo) {
            this.url = url;
            this.eid = eid;
            String[] data = eid.replace("E", "").split("_");
            this.aid = data[0];
            this.numero = data[1];
            this.titulo = titulo;
            this.id = Math.abs(eid.hashCode());
        }
    }
}
