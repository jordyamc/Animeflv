package knf.animeflv.Utils;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.thin.downloadmanager.DownloadRequest;
import com.thin.downloadmanager.DownloadStatusListenerV1;
import com.thin.downloadmanager.ThinDownloadManager;

import java.io.File;
import java.util.Random;

import knf.animeflv.BackgroundChecker.startBackground;
import knf.animeflv.R;
import knf.animeflv.Utils.eNums.UpdateState;

/**
 * Created by Jordy on 15/05/2017.
 */

public class UpdateService extends IntentService {
    private static final int DOWNLOAD_NOTIFICATION_ID = 58348;
    private static final int FINISH_NOTIFICATION_ID = 58897;
    private NotificationManagerCompat manager;
    private NotificationCompat.Builder downloading;
    private File updateFile;

    public UpdateService() {
        super("Animeflv Update Service");
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        startForeground(DOWNLOAD_NOTIFICATION_ID, getDownloadingBuilder().build());
        return IntentService.START_REDELIVER_INTENT;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        onPreDownload();
        updateFile = Keys.Dirs.getUpdateFile();
        if (updateFile.exists()) updateFile.delete();
        final ThinDownloadManager downloadManager = new ThinDownloadManager();
        final DownloadRequest downloadRequest = new DownloadRequest(Uri.parse(Keys.Url.UPDATE))
                .setDestinationURI(Uri.fromFile(updateFile))
                .setStatusListener(new DownloadStatusListenerV1() {
                    @Override
                    public void onDownloadComplete(DownloadRequest downloadRequest) {
                        UpdateUtil.setState(UpdateState.WAITING_TO_UPDATE);
                        onFinishDownload(true);
                    }

                    @Override
                    public void onDownloadFailed(DownloadRequest downloadRequest, int errorCode, String errorMessage) {
                        onFinishDownload(false);
                    }

                    @Override
                    public void onProgress(DownloadRequest downloadRequest, long totalBytes, long downloadedBytes, final int progress) {
                        onSetProgress(progress);
                    }
                });
        downloadManager.add(downloadRequest);
    }

    private void onPreDownload() {
        UpdateUtil.setState(UpdateState.DOWNLOADING);
        NotificationCompat.Builder builder = getDownloadingBuilder()
                .setProgress(100, 0, true)
                .setContentTitle("Preparando descarga...");
        getManager().notify(DOWNLOAD_NOTIFICATION_ID, builder.build());
    }

    private void onSetProgress(int progress) {
        NotificationCompat.Builder builder = getDownloadingBuilder()
                .setProgress(100, progress, false)
                .setContentTitle("Descargando actualizacion...");
        getManager().notify(DOWNLOAD_NOTIFICATION_ID, builder.build());
    }

    private void onFinishDownload(boolean success) {
        UpdateUtil.setState(UpdateState.FINISHED);
        Log.e("Download Service", "onFinishDownload");
        getManager().cancel(DOWNLOAD_NOTIFICATION_ID);
        downloading = null;
        NotificationCompat.Builder builder = getDownloadingBuilder()
                .setContentTitle("Animeflv App - Actualizacion")
                .setContentText(success ? "CLICK PARA INSTALAR" : "ERROR AL DESCARGAR")
                .setChannelId(startBackground.CHANNEL_UPDATES)
                .setPriority(Notification.PRIORITY_MAX)
                .setAutoCancel(true)
                .setVibrate(new long[]{100, 200, 100, 500})
                .setOngoing(false)
                .setLights(Color.BLUE, 5000, 2000);
        if (success) {
            builder.setSmallIcon(android.R.drawable.stat_sys_download_done);
            Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE, FileUtil.init(this).getUriForFile(updateFile));
            intent.putExtra(Intent.EXTRA_INSTALLER_PACKAGE_NAME, getPackageName());
            intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, false);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, new Random().nextInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(pendingIntent);
        } else {
            builder.setSmallIcon(android.R.drawable.stat_notify_error);
            Intent intent = new Intent(this, UpdateService.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent pendingIntent = PendingIntent.getService(this, new Random().nextInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.addAction(R.drawable.redo, "REINTENTAR", pendingIntent);
        }
        getManager().notify(FINISH_NOTIFICATION_ID, builder.build());
    }

    private NotificationManagerCompat getManager() {
        if (manager == null)
            manager = NotificationManagerCompat.from(this);
        return manager;
    }

    private NotificationCompat.Builder getDownloadingBuilder() {
        if (downloading == null)
            downloading = new NotificationCompat.Builder(this, startBackground.CHANNEL_UPDATES_RUNNING)
                    .setSmallIcon(android.R.drawable.stat_sys_download)
                    .setOngoing(true);
        return downloading;
    }

    @Override
    public void onDestroy() {
        UpdateUtil.setState(UpdateState.FINISHED);
        stopForeground(true);
        super.onDestroy();
    }
}
