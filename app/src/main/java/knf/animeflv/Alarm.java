package knf.animeflv;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import knf.animeflv.BackgroundChecker.startBackground;
import knf.animeflv.Cloudflare.Bypass;
import knf.animeflv.Utils.FastActivity;

public class Alarm extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
        Log.e("Received", intent.getAction());
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "notifications");
        wl.acquire(10000);
        Boolean not= PreferenceManager.getDefaultSharedPreferences(context).getBoolean("notificaciones",true);
        if (not) {
            Bypass.runJsoupTest(context, new Bypass.onTestResult() {
                @Override
                public void onResult(boolean needBypass) {
                    if (needBypass) {
                        Intent intent = new Intent(context, FastActivity.class);
                        intent.putExtra("key", FastActivity.RECREATE_BYPASS);
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, startBackground.CHANNEL_UPDATES)
                                .setContentTitle("Bypass de cloudflare caducado")
                                .setContentText("Click para recrear bypass")
                                .setSmallIcon(R.drawable.ic_not_r)
                                .setAutoCancel(true)
                                .setOnlyAlertOnce(true)
                                .setColor(Color.YELLOW)
                                .setContentIntent(PendingIntent.getActivity(context, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT));
                        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).notify(55898, builder.build());
                    } else {
                        startService(context);
                    }
                }
            });
            startService(context);
        } else {
            Log.e("Service", "Servicio Desactivado");
        }
        wl.release();
    }

    private void startService(Context context) {
        Log.e("Service", "Servicio Iniciado");
        startBackground.compareNots(context);
        try {
            startBackground.checkUpdate(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void StartAlarm(Context context) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
        wl.acquire(10000);
        Boolean not = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("notificaciones", true);
        if (not) {
            startService(context);
        } else {
            Log.d("Service", "Servicio Desactivado");
        }
        wl.release();
    }

    public void SetAlarm(Context context)
    {
        AlarmManager am =( AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent("knf.animeflv.START_ALARM");
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
        int time=Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString("tiempo", "60000"));
        Log.d("Timer",Integer.toString(time));
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), time, pi); //1000*60*10
    }
    public void SetAlarm(Context context,int tiempo)
    {
        AlarmManager am =( AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent("knf.animeflv.START_ALARM");
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
        Log.d("Timer",Integer.toString(tiempo));
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), tiempo, pi); //1000*60*10
    }

    public void CancelAlarm(Context context)
    {
        Log.d("Alarma","Cancelado");
        Intent intent = new Intent("knf.animeflv.START_ALARM");
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }
}
