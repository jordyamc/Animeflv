package knf.animeflv;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import knf.animeflv.Utils.FileUtil;

public class AutoStart extends BroadcastReceiver
{
    Alarm alarm = new Alarm();
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            boolean activate;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                activate = intent.getAction().equals(Intent.ACTION_LOCKED_BOOT_COMPLETED) || intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED);
            } else {
                activate = intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED);
            }
            if (activate) {
                alarm.StartAlarm(context);
                alarm.SetAlarm(context);
                File inicio = new File(Environment.getExternalStorageDirectory() + "/Animeflv/cache", "inicio.txt");
                int nCaps = context.getSharedPreferences("data", Context.MODE_PRIVATE).getInt("nCaps", 0);
                if (nCaps > 0 && inicio.exists()) {
                    Set<String> sts = context.getSharedPreferences("data", Context.MODE_PRIVATE).getStringSet("eidsNot", new HashSet<String>());
                    context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putInt("nCaps", nCaps).apply();
                    context.getSharedPreferences("data", Context.MODE_PRIVATE).edit().putStringSet("eidsNot", sts).apply();
                    String mess;
                    String mainJson = FileUtil.getStringFromFile(inicio);
                    List<String> eids = Arrays.asList(new Parser().parseEID(mainJson));
                    String[] tits = new Parser().parseTitulos(mainJson);
                    String NotTit;
                    if (nCaps == 1) {
                        mess = tits[0] + " " + eids.get(0).replace("E", "").split("_")[1];
                        NotTit = "Nuevo capitulo disponible!";
                    } else {
                        mess = "Hay " + Integer.toString(nCaps) + " nuevos capitulos disponibles!!!";
                        NotTit = "AnimeFLV";
                    }
                    String temp = "";
                    List<String> tlist = new ArrayList<>();
                    tlist.addAll(sts);
                    for (String alone : tlist) {
                        String[] data = alone.replace("E", "").split("_");
                        if (tlist.get(tlist.size() - 1).equals(alone)) {
                            temp += tits[eids.indexOf(alone)] + " " + data[1];
                        } else {
                            temp += tits[eids.indexOf(alone)] + " " + data[1] + "\n";
                        }
                    }
                    if (temp.endsWith("\n")) {
                        temp = temp.substring(0, temp.length() - 2);
                    }
                    NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
                    bigTextStyle.setBigContentTitle("Animes:");
                    bigTextStyle.bigText(temp);
                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(context)
                                    .setSmallIcon(R.drawable.ic_not_r)
                                    .setContentTitle(NotTit)
                                    .setContentText(mess);
                    mBuilder.setStyle(bigTextStyle);
                    mBuilder.setAutoCancel(true);
                    mBuilder.setLights(Color.argb(0, 255, 128, 0), 5000, 2000);
                    mBuilder.setGroup("animeflv_group");
                    Intent resultIntent = new Intent(context, newMain.class);
                    PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    mBuilder.setContentIntent(resultPendingIntent);
                    int mNotificationId = 6991;
                    NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    mNotifyMgr.cancel(mNotificationId);
                    mNotifyMgr.notify(mNotificationId, mBuilder.build());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
