package knf.animeflv.DownloadService;

import android.app.NotificationManager;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.NotificationCompat;

import java.io.File;

import knf.animeflv.Downloader;

/**
 * Created by Jordy on 04/08/2016.
 */

public class DownloadTask extends AsyncTask<Void, Void, Void> {
    public static final int DESCARGANDO = 0;
    public static final int COMPLETADO = 1;
    public static final int ERROR = 2;
    public static final int CANCELADO = 3;
    public static final int PRE_DOWNLOAD = 4;
    public static final int NONE = 5;
    public int idDown;
    public NotificationCompat.Builder builder;
    String eid;
    String aid;
    String titulo;
    String numero;
    String url;
    Context context;
    File ext_dir;
    boolean isdown = false;
    NotificationManager notificationManager;
    boolean isCancelled = false;
    private Downloader.OnFinishListener listener;
    private DownloadProgressListeners listeners;

    public DownloadTask(Context c, String url, String eid, String aid, String titulo, String numero, File ext) {
        this.url = url;
        this.eid = eid;
        this.titulo = titulo;
        this.numero = numero;
        this.aid = aid;
        this.context = c;
        this.ext_dir = ext;
        this.idDown = Math.abs(eid.hashCode());
        builder = new NotificationCompat.Builder(context);
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        builder.setContentTitle(titulo)
                .setContentText("Capitulo " + numero)
                .setOngoing(true)
                .setSmallIcon(android.R.drawable.stat_sys_download);
    }

    public void setListeners(DownloadProgressListeners listeners) {
        this.listeners = listeners;
    }

    @Override
    protected Void doInBackground(Void... voids) {

        return null;
    }

    public interface DownloadProgressListeners {
        void onProgress(int id, String eid, int progress);

        void onError(int id, String eid);
    }
}
