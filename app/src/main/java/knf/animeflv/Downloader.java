package knf.animeflv;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.provider.DocumentFile;
import android.support.v7.app.NotificationCompat;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import knf.animeflv.Utils.FileUtil;

public class Downloader extends AsyncTask<String, String, String> {
    public static final int DESCARGANDO = 0;
    public static final int COMPLETADO = 1;
    public static final int ERROR = 2;
    public static final int CANCELADO = 3;
    public static final int PRE_DOWNLOAD = 4;
    public static final int NONE = 5;
    private final int TIMEOUT_CONNECTION = 10000;//10sec
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
    NotificationManager notificationManager;
    boolean isCancelled = false;
    private OnFinishListener listener;

    public Downloader(Context c, String url, String eid, String aid, String titulo, String numero, File ext) {
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

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        File Dstorage = new File(FileUtil.init(context).getSDPath() + "/Animeflv/download/" + aid);
        if (!Dstorage.exists()) {
            Dstorage.mkdirs();
        }
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
            int status = context.getSharedPreferences("data", Context.MODE_PRIVATE).getInt(eid + "status", NONE);
            if (status == PRE_DOWNLOAD || status == DESCARGANDO) {
                context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putInt(eid + "status", DESCARGANDO).commit();
            } else {
                if (listener != null) listener.onFinish();
                notificationManager.cancel(idDown);
                if (status == CANCELADO) {
                    try {
                        FileUtil.init(context).getFileFromAccess(eid).delete();
                    } catch (Exception e) {
                    }
                }
                context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString(eid + "long_prog", "0").apply();
                return null;
            }
            isdown = true;
            URL urll = new URL(url);
            URLConnection ucon = urll.openConnection();
            ucon.setReadTimeout(TIMEOUT_CONNECTION);
            ucon.connect();
            if (((HttpURLConnection) ucon).getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND) {
                onError();
                return null;
            }
            int total = ucon.getContentLength();
            InputStream is = ucon.getInputStream();
            BufferedInputStream inStream = new BufferedInputStream(is, 1024 * 5);
            byte[] buff = new byte[8 * 1024]; //8kb
            long progress = 0;
            String semi_prog = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString(eid + "long_prog", null);
            if (semi_prog != null) {
                DocumentFile cFile = FileUtil.init(context).getFileFromAccess(eid);
                if (cFile != null && cFile.exists()) {
                    long c = Long.parseLong(semi_prog);
                    inStream.skip(c);
                    progress = c;
                }
            }
            final OutputStream outStream = FileUtil.init(context).getOutputStreamFromAccess(eid);
            int current = 0;
            int len;
            while ((len = inStream.read(buff)) != -1) {
                if (context.getSharedPreferences("data", Context.MODE_PRIVATE).getInt(eid + "status", DESCARGANDO) == CANCELADO) {
                    isCancelled = true;
                    if (listener != null) listener.onFinish();
                    notificationManager.cancel(idDown);
                    try {
                        FileUtil.init(context).getFileFromAccess(eid).delete();
                    } catch (Exception e) {
                    }
                    context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString(eid + "long_prog", "0").apply();
                    break;
                }
                progress += +len;
                int p = (int) ((progress * 100) / total);
                if (p != current) {
                    current = p;
                    builder.setProgress(100, current, false);
                    notificationManager.notify(idDown, builder.build());
                }
                context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString(eid + "long_prog", String.valueOf(progress)).commit();
                outStream.write(buff, 0, len);
            }

            //clean up
            outStream.flush();
            outStream.close();
            inStream.close();
            if (!isCancelled) onFinish();
            context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString(eid, Long.toString(idDown)).apply();
        } catch (Exception e) {
            e.printStackTrace();
            onError();
        }
        return null;
    }

    private void onError() {
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
        if (listener != null) listener.onFinish();
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
        context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putInt(eid + "status", COMPLETADO).apply();
        context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString(eid + "long_prog", "0").apply();
        Intent resultIntent = new Intent(Intent.ACTION_VIEW, Uri.fromFile(ext_dir))
                .setDataAndType(Uri.fromFile(ext_dir), "video/mp4");
        resultIntent.putExtra("title", titulo + " " + numero);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (listener != null) listener.onFinish();
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

    public void setOnFinishListener(OnFinishListener listener) {
        this.listener = listener;
    }

    public interface OnFinishListener {
        void onFinish();
    }

}
