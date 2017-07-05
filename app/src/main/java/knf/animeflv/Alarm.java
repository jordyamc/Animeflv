package knf.animeflv;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.util.Log;

import knf.animeflv.BackgroundChecker.startBackground;

public class Alarm extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("Received", intent.getAction());
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "notifications");
        wl.acquire(10000);
        Boolean not= PreferenceManager.getDefaultSharedPreferences(context).getBoolean("notificaciones",true);
        if (not) {
            Log.e("Service", "Servicio Iniciado");
            startBackground.compareNots(context);
            try {
                startBackground.checkUpdate(context);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.e("Service", "Servicio Desactivado");
        }
        wl.release();
    }

    public void StartAlarm(Context context) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
        wl.acquire(10000);
        Boolean not = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("notificaciones", true);
        if (not) {
            Log.e("Service", "Servicio Iniciado");
            startBackground.compareNots(context);
            try {
                startBackground.checkUpdate(context);
            } catch (Exception e) {
                e.printStackTrace();
            }
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
