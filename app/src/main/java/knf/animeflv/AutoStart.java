package knf.animeflv;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;

public class AutoStart extends BroadcastReceiver
{
    Alarm alarm = new Alarm();
    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED"))
        {
            Boolean not= PreferenceManager.getDefaultSharedPreferences(context).getBoolean("notificaciones",true);
            if (not) {
                alarm.SetAlarm(context);
            }
        }
    }
}
