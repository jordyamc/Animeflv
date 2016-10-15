package knf.animeflv.DownloadService;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.provider.DocumentFile;
import android.support.v7.app.NotificationCompat;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import knf.animeflv.DownloadService.DataBaseHelper.SQLiteHelper;
import knf.animeflv.DownloadService.ServiceHolder.ServiceManager;
import knf.animeflv.R;
import knf.animeflv.Retry;
import knf.animeflv.Utils.FileUtil;

public class DownloaderIntentService extends IntentService {
    public static final int DESCARGANDO = 0;
    public static final int COMPLETADO = 1;
    public static final int ERROR = 2;
    public static final int CANCELADO = 3;
    public static final int PRE_DOWNLOAD = 4;
    public static final int NONE = 5;
    final int TIMEOUT_CONNECTION = 10000;//10sec
    private final int DOWN_NOT_ID = 115989;
    private final int SEARCH_FREQUENCY = 1000;
    public int idDown;
    public NotificationCompat.Builder builder;
    String eid;
    String aid;
    String titulo;
    String numero;
    String url;
    String _response;
    Context context;
    File ext_dir;
    boolean isdown = false;
    boolean isCancelled = false;
    NotificationManager notificationManager;
    private Handler searcherHandler = new Handler();
    private Runnable search = new Runnable() {
        @Override
        public void run() {
            startSearch();
            searcherHandler.postDelayed(search, SEARCH_FREQUENCY);
        }
    };

    public DownloaderIntentService() {
        super("Download Service");
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle bundle = null;
        try {
            bundle = intent.getExtras();
        } catch (Exception e) {
            stopSelf();
        }
        if (bundle == null) {
            stopSelf();
        }
        url = bundle.getString("url");
        eid = bundle.getString("eid");
        titulo = bundle.getString("titulo");
        String[] data = eid.replace("E", "").split("_");
        aid = data[0];
        numero = data[1];
        idDown = Math.abs(eid.hashCode());
        boolean isCancelled = false;
        builder = new NotificationCompat.Builder(this);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        predownload();
        builder.setContentTitle(titulo)
                .setContentText("Capitulo " + numero)
                .setProgress(getTotalProgress(), 0, true)
                .setOngoing(true)
                .setSmallIcon(android.R.drawable.stat_sys_download);
        startForeground(DOWN_NOT_ID, builder.build());
        notificationManager.notify(DOWN_NOT_ID, builder.build());
        try {
            int status = (int) new SQLiteHelper(this).getState(eid, true);
            if (status == PRE_DOWNLOAD || status == DESCARGANDO) {
                new SQLiteHelper(this).updateState(eid, DESCARGANDO, true);
                URL urll = new URL(url);
                URLConnection ucon = urll.openConnection();
                ucon.setReadTimeout(TIMEOUT_CONNECTION);
                ucon.connect();
                if (((HttpURLConnection) ucon).getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND) {
                    onError();
                } else {
                    int total = ucon.getContentLength();
                    InputStream is = ucon.getInputStream();
                    BufferedInputStream inStream = new BufferedInputStream(is, 1024 * 5);
                    byte[] buff = new byte[8 * 1024]; //8kb
                    long progress = 0;
                    String semi_prog = getSharedPreferences("data", Context.MODE_PRIVATE).getString(eid + "long_prog", null);
                    if (semi_prog != null) {
                        DocumentFile cFile = FileUtil.getFileFromAccess(eid);
                        if (cFile != null && cFile.exists()) {
                            long c = Long.parseLong(semi_prog);
                            inStream.skip(c);
                            progress = c;
                        }
                    }
                    final OutputStream outStream = FileUtil.getOutputStream(eid);
                    int current = 0;
                    int len;
                    SQLiteHelper helper = new SQLiteHelper(this);
                    while ((len = inStream.read(buff)) != -1) {
                        if (helper.getState(eid) == CANCELADO) {
                            isCancelled = true;
                            try {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    FileUtil.getFileFromAccess(eid).delete();
                                } else {
                                    FileUtil.getFileNormal(eid).delete();
                                }
                            } catch (Exception e) {
                            }
                            getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString(eid + "long_prog", "0").apply();
                            break;
                        }
                        progress += +len;
                        int p = (int) ((progress * 100) / total);
                        if (p != current) {
                            current = p;
                            builder.setProgress(100, current, false);
                            notificationManager.notify(DOWN_NOT_ID, builder.build());
                            helper.updateProgress(eid, String.valueOf(progress));
                        }
                        //getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString(eid + "long_prog", String.valueOf(progress)).commit();
                        outStream.write(buff, 0, len);
                    }

                    //clean up
                    helper.close();
                    outStream.flush();
                    outStream.close();
                    inStream.close();
                    if (isCancelled) {
                        ServiceManager.delete(this, eid);
                    }
                    getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString(eid, Long.toString(idDown)).apply();
                }
            } else {
                if (status == CANCELADO) {
                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            FileUtil.getFileFromAccess(eid).delete();
                        } else {
                            FileUtil.getFileNormal(eid).delete();
                        }
                    } catch (Exception e) {
                    }
                }
                getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString(eid + "long_prog", "0").apply();
            }

        } catch (Exception e) {
            e.printStackTrace();
            onError();
        }
    }

    private void onError() {
        new SQLiteHelper(this).updateState(eid, ERROR, true);
        new SQLiteHelper(this).updateProgress(eid, "0", true);
        context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putInt(eid + "status", ERROR).apply();
        context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString(eid + "long_prog", "0").apply();
        Bundle bundle = new Bundle();
        bundle.putString("aid", aid);
        bundle.putString("eid", eid);
        bundle.putString("titulo", titulo);
        bundle.putString("numero", numero);
        bundle.putString("file", ext_dir.getAbsolutePath());
        bundle.putString("url", url);
        Intent resultIntent = new Intent(context, Retry.class)
                .putExtras(bundle);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder = new NotificationCompat.Builder(context);
        builder.setContentText("Descarga Fallida")
                .setContentTitle(titulo + " " + numero)
                .setProgress(0, 0, false)
                .setOngoing(false)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_exit_r)
                .setVibrate(new long[]{100, 200, 100, 500})
                .setContentIntent(resultPendingIntent);
        notificationManager.notify(idDown, builder.build());
    }

    private void onFinish() {
        new SQLiteHelper(this).updateState(eid, COMPLETADO, true);
        new SQLiteHelper(this).updateProgress(eid, "0", true);
        getSharedPreferences("data", Context.MODE_PRIVATE).edit().putInt(eid + "status", COMPLETADO).apply();
        getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString(eid + "long_prog", "0").apply();
        Intent resultIntent = new Intent(Intent.ACTION_VIEW, Uri.fromFile(ext_dir))
                .setDataAndType(Uri.fromFile(ext_dir), "video/mp4");
        resultIntent.putExtra("title", titulo + " " + numero);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder = new NotificationCompat.Builder(context);
        builder.setContentText("Descarga Completada")
                .setContentTitle(titulo + " " + numero)
                .setProgress(0, 0, false)
                .setOngoing(false)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_done)
                .setVibrate(new long[]{100, 200, 100, 500})
                .setContentIntent(resultPendingIntent);
        notificationManager.notify(idDown, builder.build());
    }

    private void predownload() {
        String descargados = getSharedPreferences("data", MODE_PRIVATE).getString("eids_descarga", "");
        String epID = getSharedPreferences("data", MODE_PRIVATE).getString("epIDS_descarga", "");
        if (descargados.contains(eid)) {
            getSharedPreferences("data", MODE_PRIVATE).edit().putString("eids_descarga", descargados.replace(eid + ":::", "")).apply();
            getSharedPreferences("data", MODE_PRIVATE).edit().putString("epIDS_descarga", epID.replace(aid + "_" + numero + ":::", "")).apply();
        }
        descargados = getSharedPreferences("data", MODE_PRIVATE).getString("eids_descarga", "");
        getSharedPreferences("data", MODE_PRIVATE).edit().putString("eids_descarga", descargados + eid + ":::").apply();
        String tits = getSharedPreferences("data", MODE_PRIVATE).getString("titulos_descarga", "");
        epID = getSharedPreferences("data", MODE_PRIVATE).getString("epIDS_descarga", "");
        getSharedPreferences("data", MODE_PRIVATE).edit().putString("titulos_descarga", tits + aid + ":::").apply();
        getSharedPreferences("data", MODE_PRIVATE).edit().putString("epIDS_descarga", epID + aid + "_" + numero + ":::").apply();
        getSharedPreferences("data", MODE_PRIVATE).edit().putBoolean("visto" + aid + "_" + numero, true).apply();
        String vistos = getSharedPreferences("data", MODE_PRIVATE).getString("vistos", "");
        if (!vistos.contains(eid.trim())) {
            vistos = vistos + eid.trim() + ":::";
            getSharedPreferences("data", MODE_PRIVATE).edit().putString("vistos", vistos).apply();
        }
    }

    private int getTotalDownloads() {
        SQLiteHelper helper = new SQLiteHelper(this);
        return helper.getTotalDownloads(true);
    }

    private int getTotalProgress() {
        return getTotalDownloads() * 100;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void startSearch() {

    }


}
