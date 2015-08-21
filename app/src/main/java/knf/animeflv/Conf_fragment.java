package knf.animeflv;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

/**
 * Created by Jordy on 16/08/2015.
 */
public class Conf_fragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    Context context;
    MediaPlayer mp;
    MediaPlayer r;
    //Ringtone r;
    @Override
    public void onCreate(final Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferencias);
        context=getActivity().getApplicationContext();
        Boolean activado= PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("notificaciones", true);
        if (!activado){
            getPreferenceScreen().findPreference("tiempo").setEnabled(false);
            getPreferenceScreen().findPreference("sonido").setEnabled(false);
        }else {
            getPreferenceScreen().findPreference("tiempo").setEnabled(true);
            getPreferenceScreen().findPreference("sonido").setEnabled(true);
        }
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        r = MediaPlayer.create(context, notification);
        mp = MediaPlayer.create(context, R.raw.sound);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Set up a listener whenever a key changes
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        // Set up a listener whenever a key changes
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
       switch (key){
           case "notificaciones":
               Boolean activado=sharedPreferences.getBoolean(key,true);
               if (!activado){
                   getPreferenceScreen().findPreference("tiempo").setEnabled(false);
                   getPreferenceScreen().findPreference("sonido").setEnabled(false);
                   new Alarm().CancelAlarm(context);
               }else {
                   getPreferenceScreen().findPreference("tiempo").setEnabled(true);
                   getPreferenceScreen().findPreference("sonido").setEnabled(true);
                   new Alarm().SetAlarm(context);
               }
               break;
           case "tiempo":
               int tiempo=Integer.parseInt(sharedPreferences.getString(key, "60000"));
               new Alarm().CancelAlarm(context);
               new Alarm().SetAlarm(context,tiempo);
               break;
           case "sonido":
               int not=Integer.parseInt(sharedPreferences.getString("sonido","0"));
               if (not==0){
                   if (mp.isPlaying()){
                       mp.stop();
                   }
                   Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                   r = MediaPlayer.create(context, notification);
                   r.start();
               }
               if (not==1){
                   if (r.isPlaying()){
                       r.stop();
                   }
                   mp = MediaPlayer.create(context, R.raw.sound);
                   mp.start();
               }
               break;
       }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mp.isPlaying()){
            mp.stop();
            mp.release();
        }
        if (r.isPlaying()){
            r.stop();
            r.release();
        }
    }
}
