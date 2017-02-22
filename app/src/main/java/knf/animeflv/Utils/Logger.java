package knf.animeflv.Utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import knf.animeflv.R;
import knf.animeflv.Utils.logs.LogViewer;

public class Logger {
    private static Context context;

    public static void initial(Context context) {
        Logger.context = context;
    }

    public static void Error(Class act, Throwable e) {
        String name = act.getName();
        Log.e(name, e.getMessage(), e);
        WriteLog(new log(name, e), false);
    }

    public static void UncaughtError(Throwable e) {
        String name = "Uncaught";
        Log.e(name, e.getMessage(), e);
        WriteLog(new log(name, e), true);
    }

    private static String getTime() {
        return new SimpleDateFormat("_hh:mm:ssaa@dd|MM|yyyy$", Locale.ENGLISH).format(new Date());
    }

    private static void WriteLog(log object, boolean show) {
        File folderlog = new File(Keys.Dirs.LOGS + "/" + object.getName());
        if (!folderlog.exists()) folderlog.mkdirs();
        StringWriter sw = new StringWriter();
        object.getThrowable().printStackTrace(new PrintWriter(sw));
        String body =
                object.getThrowable().getCause() + "\n\n" +
                        sw.toString();
        File writed = new File(folderlog, object.getName() + getTime() + +System.currentTimeMillis() + ".log");
        FileUtil.writeToFile(body, writed);
        if (show) ShowNot(writed);
    }

    private static void ShowNot(File file) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_not_r)
                        .setContentTitle("Informe de error capturado")
                        .setContentText("Presiona para enviar");
        mBuilder.setVibrate(new long[]{100, 200, 100, 500});
        mBuilder.setAutoCancel(true);
        mBuilder.setPriority(Notification.PRIORITY_MAX);
        mBuilder.setLights(Color.argb(0, 255, 128, 0), 5000, 2000);
        Intent resultIntent = new Intent(context, LogViewer.class);
        resultIntent.setData(Uri.parse(file.getAbsolutePath()));
        resultIntent.putExtra("path", file.getAbsolutePath());
        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        int mNotificationId = (int) Math.round(Math.random());
        NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }

    private static class log {
        private String name;
        private Throwable throwable;

        public log(String name, Throwable throwable) {
            this.name = name;
            this.throwable = throwable;
        }

        public String getName() {
            return name;
        }

        public Throwable getThrowable() {
            return throwable;
        }
    }
}
