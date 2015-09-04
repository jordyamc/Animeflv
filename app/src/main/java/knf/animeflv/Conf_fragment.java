package knf.animeflv;

import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Jordy on 16/08/2015.
 */
public class Conf_fragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    Context context;
    MediaPlayer mp;
    MediaPlayer r;
    MediaPlayer oni;
    MediaPlayer sam;
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
        oni = MediaPlayer.create(context, R.raw.onii);
        sam = MediaPlayer.create(context, R.raw.sam);
        final File file = new File(Environment.getExternalStorageDirectory()+"/.Animeflv/download");
        long size = getcachesize();
        long dirsize=getFileSize(file);
        String tamano=formatSize(size);
        String vidsize=formatSize(dirsize);
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("b_cache",tamano).commit();
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("b_video",vidsize).commit();
        getPreferenceScreen().findPreference("b_cache").setSummary("Tamaño de cache: " + tamano);
        getPreferenceScreen().findPreference("b_cache").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                clearApplicationData();
                String s=formatSize(getcachesize());
                PreferenceManager.getDefaultSharedPreferences(context).edit().putString("b_cache", s).commit();
                getPreferenceScreen().findPreference("b_cache").setSummary("Tamaño de cache: "+s);
                return false;
            }
        });
        getPreferenceScreen().findPreference("b_video").setSummary("Espacio usado: " + vidsize);
        getPreferenceScreen().findPreference("b_video").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                deleteDownload(file);
                DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                String[] eids=context.getSharedPreferences("data",Context.MODE_PRIVATE).getString("teids","").split(":::");
                for (String s:eids){
                    if (!s.trim().equals("")){
                        long l=Long.parseLong(context.getSharedPreferences("data",Context.MODE_PRIVATE).getString(s,"0"));
                        manager.remove(l);
                    }
                }
                String si=formatSize(getFileSize(file));
                PreferenceManager.getDefaultSharedPreferences(context).edit().putString("b_video", si).commit();
                getPreferenceScreen().findPreference("b_video").setSummary("Espacio usado: " + si);
                return false;
            }
        });
    }
    public long getcachesize(){
        long size = 0;
        File[] files = context.getCacheDir().listFiles();
        File[] mediaStorage = new File(Environment.getExternalStorageDirectory() + "/.Animeflv/cache").listFiles();
        for (File f:files) {
            size = size+f.length();
        }
        for (File f1:mediaStorage){
            size = size+f1.length();
        }
        return size;
    }
    public void deleteDownload(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                deleteDir(new File(dir, children[i]));
            }
        }
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
                   if (mp.isPlaying()||oni.isPlaying()||sam.isPlaying()){
                       if (mp.isPlaying())mp.stop();
                       if (oni.isPlaying())oni.stop();
                       if (sam.isPlaying())sam.stop();
                   }
                   Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                   r = MediaPlayer.create(context, notification);
                   r.start();
               }
               if (not==1){
                   if (r.isPlaying()||oni.isPlaying()||sam.isPlaying()){
                       if (r.isPlaying())r.stop();
                       if (oni.isPlaying())oni.stop();
                       if (sam.isPlaying())sam.stop();
                   }
                   mp = MediaPlayer.create(context, R.raw.sound);
                   mp.start();
               }
               if (not==2){
                   if (r.isPlaying()||mp.isPlaying()||sam.isPlaying()){
                       if (r.isPlaying())r.stop();
                       if (mp.isPlaying())mp.stop();
                       if (sam.isPlaying())sam.stop();
                   }
                   oni = MediaPlayer.create(context, R.raw.onii);
                   oni.start();
               }
               if (not==3){
                   if (r.isPlaying()||mp.isPlaying()||sam.isPlaying()){
                       if (r.isPlaying())r.stop();
                       if (mp.isPlaying())mp.stop();
                       if (oni.isPlaying())oni.stop();
                   }
                   sam = MediaPlayer.create(context, R.raw.sam);
                   sam.start();
               }
               break;
           case "b_cache":
               break;
       }
    }
    public static String formatSize(long v) {
        if (v < 1024) return v + " B";
        int z = (63 - Long.numberOfLeadingZeros(v)) / 10;
        return String.format("%.1f %sB", (double)v / (1L << (z*10)), " KMGTPE".charAt(z));
    }
    public void clearApplicationData() {
        File cache = context.getCacheDir();
        File appDir = new File(cache.getParent());
        if (appDir.exists()) {
            String[] children = appDir.list();
            for (String s : children) {
                if (!s.equals("lib")) {
                    deleteDir(new File(appDir, s));
                }
            }
        }
        File mediaStorage = new File(Environment.getExternalStorageDirectory() + "/.Animeflv/cache");
        if (mediaStorage.isDirectory()) {
            String[] children = mediaStorage.list();
            for (int i = 0; i < children.length; i++) {
                new File(mediaStorage, children[i]).delete();
            }
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }
    public static long getFileSize(final File file) {
        if(file==null||!file.exists())
            return 0;
        if(!file.isDirectory())
            return file.length();
        final List<File> dirs=new LinkedList<File>();
        dirs.add(file);
        long result=0;
        while(!dirs.isEmpty()) {
            final File dir=dirs.remove(0);
            if(!dir.exists())
                continue;
            final File[] listFiles=dir.listFiles();
            if(listFiles==null||listFiles.length==0)
                continue;
            for(final File child : listFiles)
            {
                result+=child.length();
                if(child.isDirectory())
                    dirs.add(child);
            }
        }
        return result;
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
        if (oni.isPlaying()){
            oni.stop();
            oni.release();
        }
        if (sam.isPlaying()){
            sam.stop();
            sam.release();
        }
    }
}
