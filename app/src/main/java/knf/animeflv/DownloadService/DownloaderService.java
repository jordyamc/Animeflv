package knf.animeflv.DownloadService;

import android.app.DownloadManager;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StatFs;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.crashlytics.android.core.CrashlyticsCore;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Random;

import javax.net.ssl.SSLException;

import knf.animeflv.Errors.NoInternetException;
import knf.animeflv.Errors.NoSDAccessDetectedException;
import knf.animeflv.Errors.NoSpaceException;
import knf.animeflv.Errors.WrongAccessPermissionException;
import knf.animeflv.Explorer.ExplorerRoot;
import knf.animeflv.Explorer.Models.ModelFactory;
import knf.animeflv.Parser;
import knf.animeflv.R;
import knf.animeflv.Utils.FileUtil;
import knf.animeflv.Utils.NetworkUtils;

public class DownloaderService extends IntentService {
    public static final int CANCELED = 1554785;
    private static final int DOWNLOAD_NOTIFICATION_ID = 4458758;
    private static final int NULL = 388744;
    private static final String CAUSE_INTERNET = "NO SE DETECTA INTERNET";
    private static final String CAUSE_SD_ACCESS = "NO HAY PERMISO PARA ESCRIBIR EN LA SD";
    private static final String CAUSE_WRONG_SD = "LA SD SELECCIONADA NO CONCUERDA CON EL PERMISO";
    private static final String CAUSE_NO_SPACE = "NO HAY SUFICIENTE ESPACIO EN LA SD";
    private static final String CAUSE_UNKNOWN = "ERROR DESCONOCIDO";
    private static final String CAUSE_DISCONNECTION = "CONEXION INTERRUMPIDA";
    private static final String CAUSE_SSL = "ERROR EN CERTIFICADO SSL";
    public static String RECEIVER_ACTION_ERROR = "knf.animeflv.DownloadService.DownloadService.RECIEVER_ERROR";
    private NotificationManager manager;
    private NotificationCompat.Builder downloading;

    public DownloaderService() {
        super("Animeflv Download Service");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        startForeground(DOWNLOAD_NOTIFICATION_ID, getDownloadingBuilder().build());
        return IntentService.START_REDELIVER_INTENT;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int count;
        Bundle bundle = intent.getExtras();
        String eid = "error";
        try {
            SharedPreferences preferences = getSharedPreferences("data", MODE_PRIVATE);
            eid = bundle.getString("eid");
            long downloadID = bundle.getLong("downloadID");
            if (new SQLiteHelperDownloads(this).getDownloadInfo(eid, true)._download_id != downloadID)
                throw new DownloadCanceledException(String.valueOf(downloadID));
            if (new SQLiteHelperDownloads(this).getState(eid) == DownloadManager.STATUS_RUNNING)
                FileUtil.init(this).DeleteAnime(eid);
            if (!NetworkUtils.isNetworkAvailable())
                throw new NoInternetException(null);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                if (FileUtil.init(this).RootFileHaveAccess()) {
                    if (!FileUtil.init(this).RootFileHaveAccess(null))
                        throw new WrongAccessPermissionException(ModelFactory.getRootSDFile(this).getName());
                } else {
                    throw new NoSDAccessDetectedException(null);
                }
            onStartDownload(eid);
            URL url = new URL(bundle.getString("url"));
            URLConnection conection = url.openConnection();
            if (bundle.getBoolean("constructor")) {
                conection.setRequestProperty("Cookie", bundle.getString("cookie"));
                conection.setRequestProperty("Referer", bundle.getString("referer"));
                conection.setRequestProperty("User-Agent", bundle.getString("useragent"));
            }
            conection.connect();
            long lenghtOfFile = conection.getContentLength();
            if (lenghtOfFile > getAvailableSpace())
                throw new NoSpaceException(null);
            InputStream input = conection.getInputStream();
            OutputStream output = FileUtil.init(this).getOutputStream(eid);
            byte data[] = new byte[1024 * 6];
            long total = 0;
            int prog = 0;
            while ((count = input.read(data)) != -1) {
                if (preferences.getLong(eid + "_downloadID", -1) != downloadID)
                    throw new DownloadCanceledException(String.valueOf(downloadID));
                if (!NetworkUtils.isNetworkAvailable())
                    throw new IllegalStateException();
                total += count;
                int tprog = (int) ((total * 100) / lenghtOfFile);
                if (tprog > prog) {
                    prog = tprog;
                    updateCurrentProgress(eid, prog);
                }
                output.write(data, 0, count);
                output.flush();
            }
            output.close();
            input.close();
            onSuccess(eid);
        } catch (DownloadCanceledException canceled) {
            Log.e("DownloadService", "Canceled - Eid: " + eid + " ID: " + canceled.getMessage());
            FileUtil.init(this).DeleteAnime(eid);
            DownloadListManager.delete(this, eid + "_" + bundle.getLong("downloadID"));
            new SQLiteHelperDownloads(this).delete(eid).close();
        } catch (NoInternetException nie) {
            Log.e("DownloadService", nie.getMessage());
            onDownloadFailed(eid, intent, CAUSE_INTERNET);
        } catch (NoSDAccessDetectedException nsad) {
            Log.e("DownloadService", nsad.getMessage());
            onDownloadFailed(eid, intent, CAUSE_SD_ACCESS);
        } catch (WrongAccessPermissionException wap) {
            Log.e("DownloadService", wap.getMessage());
            onDownloadFailed(eid, intent, CAUSE_WRONG_SD);
        } catch (NoSpaceException nse) {
            Log.e("DownloadService", nse.getMessage());
            onDownloadFailed(eid, intent, CAUSE_NO_SPACE);
        } catch (ProtocolException pe) {
            Log.e("DownloadService", pe.getMessage());
            onDownloadFailed(eid, intent, CAUSE_DISCONNECTION);
        } catch (SSLException ssl) {
            Log.e("DownloadService", ssl.getMessage());
            onDownloadFailed(eid, intent, CAUSE_SSL);
        } catch (IOException ioe) {
            Log.e("DownloadService", ioe.getMessage());
            if (ioe.getMessage().trim().equalsIgnoreCase("unexpected end of stream")) {
                onDownloadFailed(eid, intent, CAUSE_DISCONNECTION);
            } else {
                onDownloadFailed(eid, intent, ioe);
            }
        } catch (Exception e) {
            Log.e("DownloadService", "error on try", e);
            onDownloadFailed(eid, intent, e);
            CrashlyticsCore.getInstance().logException(e);
        }
    }

    private long getAvailableSpace() {
        StatFs stat = new StatFs(ModelFactory.getRootSDFile(this).getPath());
        return (long) stat.getBlockSize() * (long) stat.getBlockCount();
    }

    private void onStartDownload(String eid) {
        getManager().cancel(getDownloadID(eid));
        String title = new Parser().getTitCached(eid.replace("E", "").split("_")[0]);
        NotificationCompat.Builder mBuilder = getDownloadingBuilder()
                .setContentTitle(title)
                .setContentText("Capítulo " + eid.replace("E", "").split("_")[1])
                .setContentIntent(PendingIntent.getActivity(this, 0, getDownloadingIntent(eid.split("_")[0]), PendingIntent.FLAG_UPDATE_CURRENT))
                .setProgress(100, 0, true);
        getManager().notify(DOWNLOAD_NOTIFICATION_ID, mBuilder.build());
        new SQLiteHelperDownloads(this).updateState(eid, DownloadManager.STATUS_RUNNING).close();
    }

    private Intent getDownloadingIntent(String eid) {
        Intent intent = new Intent(this, ExplorerRoot.class);
        intent.putExtra("aid", eid);
        return intent;
    }

    private NotificationCompat.Builder getDownloadingBuilder() {
        if (downloading == null)
            downloading = new NotificationCompat.Builder(this)
                    .setSmallIcon(android.R.drawable.stat_sys_download)
                    .setOngoing(true);
        return downloading;
    }

    private void updateCurrentProgress(String eid, int progress) {
        updateSavedProgress(eid, progress);
        int pending = new SQLiteHelperDownloads(this).getTotalDownloads();
        NotificationCompat.Builder mBuilder = getDownloadingBuilder()
                .setProgress(100, progress, false);
        if (pending - 1 > 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mBuilder.setSubText(pending - 1 + (pending - 1 == 1 ? " pendiente" : " pendientes"));
            } else {
                if (pending > 1) {
                    mBuilder.setNumber(pending);
                } else {
                    downloading = null;
                    mBuilder = getDownloadingBuilder()
                            .setProgress(100, progress, false);
                }
            }
        }
        getManager().notify(DOWNLOAD_NOTIFICATION_ID, mBuilder.build());
    }

    private void updateSavedProgress(String eid, int progress) {
        new SQLiteHelperDownloads(this).updateProgress(eid, progress).close();
    }

    private void onDownloadFailed(String eid, Intent intent, Exception e) {
        onDownloadFailed(eid, intent, CAUSE_UNKNOWN + "\n\n" + Log.getStackTraceString(e));
    }

    private void onDownloadFailed(String eid, Intent intent, String cause) {
        FileUtil.init(this).DeleteAnime(eid);
        DownloadListManager.delete(this, eid + "_" + getSharedPreferences("data", MODE_PRIVATE).getLong(eid + "_downloadID", -1));
        String[] semi = eid.replace("E", "").split("_");
        Intent n_intent = new Intent(DownloadBroadCaster.ACTION_RETRY);
        n_intent.putExtras(intent.getExtras());
        n_intent.putExtra("not_id", getDownloadID(eid));
        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        bigTextStyle.setBigContentTitle("CAUSA:");
        bigTextStyle.bigText(cause);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(android.R.drawable.stat_notify_error)
                .setContentTitle(new Parser().getTitCached(semi[0]) + " - " + semi[1])
                .setContentText("ERROR AL DESCARGAR")
                .setGroup("animeflv_failed_download")
                .setStyle(bigTextStyle)
                .setOngoing(false);
        if (cause.equals(CAUSE_INTERNET) || cause.equals(CAUSE_NO_SPACE))
            builder.addAction(R.drawable.redo, "REINTENTAR", PendingIntent.getBroadcast(this, new Random().nextInt(), n_intent, PendingIntent.FLAG_UPDATE_CURRENT));
        getManager().notify(getDownloadID(eid), builder.build());
        new SQLiteHelperDownloads(this).updateState(eid, DownloadManager.STATUS_FAILED).delete(eid);
        sendBroadcast(new Intent(RECEIVER_ACTION_ERROR));
    }

    private int getDownloadID(String eid) {
        return Math.abs(eid.hashCode());
    }

    private NotificationManager getManager() {
        if (manager == null)
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        return manager;
    }

    private void onSuccess(String eid) {
        DownloadListManager.delete(this, eid + "_" + getSharedPreferences("data", MODE_PRIVATE).getLong(eid + "_downloadID", -1));
        String title = new Parser().getTitCached(eid.replace("E", "").split("_")[0]);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(android.R.drawable.stat_sys_download_done)
                .setContentTitle(title + " - " + eid.replace("E", "").split("_")[1])
                .setContentText("DESCARGA COMPLETADA")
                .setGroup("animeflv_success_group")
                .setAutoCancel(true)
                .setOngoing(false);
        if (Build.VERSION.SDK_INT < 24) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.fromFile(FileUtil.init(this).getFileNormal(eid)));
            intent.putExtra("title", title);
            intent.setDataAndType(Uri.fromFile(FileUtil.init(this).getFileNormal(eid)), "video/mp4");
            PendingIntent pendingIntent = PendingIntent.getActivity(this, getDownloadID(eid), intent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(pendingIntent);
        }
        getManager().notify(getDownloadID(eid), builder.build());
        new SQLiteHelperDownloads(this).updateState(eid, DownloadManager.STATUS_SUCCESSFUL).delete(eid);
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        getManager().cancel(DOWNLOAD_NOTIFICATION_ID);
        new SQLiteHelperDownloads(this).reset();
        super.onDestroy();
    }
}
