package knf.animeflv;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.thin.downloadmanager.DownloadRequest;
import com.thin.downloadmanager.DownloadStatusListenerV1;
import com.thin.downloadmanager.ThinDownloadManager;

import java.io.File;
import java.util.Arrays;

import knf.animeflv.Utils.FileUtil;

/**
 * Created by Jordy on 29/02/2016.
 */
public class Downloader extends AsyncTask<String, String, String> {
    String eid;
    String aid;
    String titulo;
    String numero;
    String _response;
    Context context;
    File ext_dir;
    int idDown;
    boolean isdown = false;
    NotificationManager notificationManager;
    NotificationCompat.Builder builder;

    int DESCARGANDO = 0;
    int COMPLETADO = 1;
    int ERROR = 2;
    int CANCELADO = 3;

    public Downloader(Context c, String eid, String aid, String titulo, String numero, File ext) {
        this.eid = eid;
        this.titulo = titulo;
        this.numero = numero;
        this.aid = aid;
        this.context = c;
        this.ext_dir = ext;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        File Dstorage = new File(FileUtil.getSDPath() + "/Animeflv/download/" + aid);
        if (!Dstorage.exists()) {
            Dstorage.mkdirs();
        }
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        builder = new NotificationCompat.Builder(context);
        builder.setContentTitle(titulo)
                .setContentText("Capitulo " + numero)
                .setOngoing(true)
                .setSmallIcon(android.R.drawable.stat_sys_download);
        String descargados = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("eids_descarga", "");
        String epID = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("epIDS_descarga", "");
        if (descargados.contains(eid)) {
            context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("eids_descarga", descargados.replace(eid + ":::", "")).apply();
            context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("epIDS_descarga", epID.replace(aid + "_" + numero + ":::", "")).apply();
        }
        descargados = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("eids_descarga", "");
        context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("eids_descarga", descargados + eid + ":::").apply();
        String tits = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("titulos_descarga", "");
        epID = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("epIDS_descarga", "");
        context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("titulos_descarga", tits + aid + ":::").apply();
        context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("epIDS_descarga", epID + aid + "_" + numero + ":::").apply();
        context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putBoolean("visto" + aid + "_" + numero, true).apply();
        String vistos = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("vistos", "");
        if (!vistos.contains(eid.trim())) {
            vistos = vistos + eid.trim() + ":::";
            context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("vistos", vistos).apply();
        }
    }

    @Override
    protected String doInBackground(final String... params) {
        builder.setProgress(100, 0, false);
        try {
            isdown = true;
            final ThinDownloadManager downloadManager = DManager.getManager();
            Uri download = Uri.parse(params[0]);
            final DownloadRequest downloadRequest = new DownloadRequest(download)
                    .setDestinationURI(Uri.fromFile(ext_dir))
                    .setStatusListener(new DownloadStatusListenerV1() {
                        @Override
                        public void onDownloadComplete(DownloadRequest downloadRequest) {
                            context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putInt(eid + "status", COMPLETADO).apply();
                            Intent resultIntent = new Intent(Intent.ACTION_VIEW, Uri.fromFile(ext_dir))
                                    .setDataAndType(Uri.fromFile(ext_dir), "video/mp4");
                            PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                            builder.setContentText("Descarga Completada")
                                    .setProgress(0, 0, false)
                                    .setOngoing(false)
                                    .setAutoCancel(true)
                                    .setSmallIcon(R.drawable.ic_not_r)
                                    .setVibrate(new long[]{100, 200, 100, 500})
                                    .setContentIntent(resultPendingIntent);
                            notificationManager.notify(idDown, builder.build());
                        }

                        @Override
                        public void onDownloadFailed(DownloadRequest downloadRequest, int errorCode, String errorMessage) {
                            if (!errorMessage.toLowerCase().contains("cancelled")) {
                                context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putInt(eid + "status", ERROR).apply();
                                context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString(eid + "errmessage", errorMessage).apply();
                                Bundle bundle = new Bundle();
                                bundle.putString("aid", aid);
                                bundle.putString("eid", eid);
                                bundle.putString("titulo", titulo);
                                bundle.putString("numero", numero);
                                bundle.putString("file", ext_dir.getAbsolutePath());
                                bundle.putString("url", params[0]);
                                Intent resultIntent = new Intent(context, Retry.class)
                                        .putExtras(bundle);
                                PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                                builder.setContentText("Descarga Fallida")
                                        .setProgress(0, 0, false)
                                        .setOngoing(false)
                                        .setAutoCancel(true)
                                        .setSmallIcon(R.drawable.ic_not_r)
                                        .setVibrate(new long[]{100, 200, 100, 500})
                                        .setContentIntent(resultPendingIntent);
                                notificationManager.notify(idDown, builder.build());
                            } else {
                                context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putInt(eid + "status", CANCELADO).apply();
                                notificationManager.cancel(idDown);
                            }
                        }

                        @Override
                        public void onProgress(DownloadRequest downloadRequest, long totalBytes, long downloadedBytes, int progress) {
                            builder.setProgress(100, progress, false);
                            context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString(eid + "prog", String.valueOf(progress)).apply();
                            context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putInt(eid + "status", DESCARGANDO).apply();
                            notificationManager.notify(idDown, builder.build());
                        }
                    });
            idDown = downloadManager.add(downloadRequest);
            notificationManager.notify(idDown, builder.build());
            context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString(eid, Long.toString(idDown)).apply();
            _response = "0";
        } catch (Exception e) {
            e.printStackTrace();
            _response = "1";
        }
        return _response;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }

    public String getSD1() {
        String sSDpath = null;
        File fileCur = null;
        for (String sPathCur : Arrays.asList("MicroSD", "external_SD", "sdcard1", "ext_card", "external_sd", "ext_sd", "external", "extSdCard", "externalSdCard", "8E84-7E70")) {
            fileCur = new File("/mnt/", sPathCur);
            if (fileCur.isDirectory() && fileCur.canWrite()) {
                sSDpath = fileCur.getAbsolutePath();
                break;
            }
            if (sSDpath == null) {
                fileCur = new File("/storage/", sPathCur);
                if (fileCur.isDirectory() && fileCur.canWrite()) {
                    sSDpath = fileCur.getAbsolutePath();
                    break;
                }
            }
            if (sSDpath == null) {
                fileCur = new File("/storage/emulated", sPathCur);
                if (fileCur.isDirectory() && fileCur.canWrite()) {
                    sSDpath = fileCur.getAbsolutePath();
                    Log.e("path", sSDpath);
                    break;
                }
            }
        }
        return sSDpath;
    }

}
